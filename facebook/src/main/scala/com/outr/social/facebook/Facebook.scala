package com.outr.social.facebook

import java.io.File

import com.outr.social.HeadlessBrowser
import io.youi.Unique
import io.youi.client.HttpClient
import io.youi.http.{Headers, HttpRequest, HttpResponse, Method}
import io.youi.http.content.Content
import io.youi.net._
import io.youi.http.cookie.ResponseCookie
import javax.swing.JOptionPane
import org.powerscala.io.IO
import profig.JsonUtil

import scala.concurrent.{Await, Future}
import scribe.Execution.global

import scala.annotation.tailrec
import scala.concurrent.duration.Duration

case class Facebook(cookies: List[ResponseCookie]) {
  private val userAgent: String = "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/71.0.3578.98 Safari/537.36"
  private lazy val client = HttpClient()

  def send(recipientId: String, message: String): Future[HttpResponse] = {
    val messageId: String  = messageIdGenerator
    val timestamp = System.currentTimeMillis()
    val req = java.lang.Integer.toUnsignedString(0, 32)
    val headers = Headers
      .empty
      .withHeader("cookie", cookies.map(c => s"${c.name}=${c.value}").mkString("; "))
      .withHeader(Headers.Request.`User-Agent`(userAgent))
      .withHeader("x-msgr-region", "FRC")
    val content = Content
      .form
      .withString("client", "mercury")
      .withString("action_type", "ma-type:user-generated-message")
      .withString("body", message)
      .withString("ephemeral_ttl_mode", "0")
      .withString("has_attachment", "false")
      .withString("message_id", messageId)
      .withString("offline_threading_id", messageId)
      .withString("other_user_fbid", recipientId)
      .withString("signature_id", "327b8974")
      .withString("source", "source:chat:web")
      .withString("specific_to_list[0]", s"fbid:$recipientId")
      .withString("specific_to_list[1]", "fbid:1213772039")
      .withString("tags[0]", "web:trigger:fb_header_dock:jewel_thread")
      .withString("timestamp", timestamp.toString)
      .withString("ui_push_phase", "C3")
      .withString("__user", "1213772039")
      .withString("__a", "1")
      .withString("__dyn", "7AgNe-4amaAxd2u6aJGeFxqeCwKyaGey8gF4Wo8ovxGdwIhE98nwgUaofUvnyorxuEbbyEjKewXGu3yaUS2SVFEgU9A69Ukz8nxm3i3a4E9ohxG18wzU88eGxW5o7Cum1owhFUhKEsgoxu6Uao4a3mbx-2KdU5XximfKEgy4m1yGdhUix69wyXxK8BojUy6F8gzaG9BKm4U-4Kq7oqxm7ohAyaxuK6oogJ4h9ogVFXAy8aElxeaCyXwPxe8KUkG5E98e8Wrz8mgK7o88vw")
      .withString("__req", req)
      .withString("__be", "1")
      .withString("__pc", "PHASED:ufi_home_page_pkg")
      .withString("dpr", "1.5")
      .withString("__rev", "4658501")
      .withString("fb_dtsg", "AQF554XuQKb3:AQGlXOTrVOxN")
      .withString("jazoest", "21995")
      .withString("__spin_r", "4658501")
      .withString("__spin_b", "trunk")
      .withString("__spin_t", "1546110165")

    client.send(HttpRequest(
      method = Method.Post,
      url = url"https://www.facebook.com/messaging/send",
      headers = headers,
      content = Some(content)
    ))
  }

  private def messageIdGenerator: String = Unique(length = 19, characters = Unique.Numbers)
}

object Facebook {
  private val checkPointURL = url"https://www.facebook.com/checkpoint/?next"

  def logIn(email: String,
            password: String,
            approval: => Future[String] = Future(JOptionPane.showInputDialog("Login Approval:"))): Future[Facebook] = Future {
    val browser = new HeadlessBrowser
    try {
      browser.load(url"https://www.facebook.com/login/device-based/regular/login")
      val form = browser.byId("login_form")
      val emailInput = form.byId("email")
      val passwordInput = form.byId("pass")

      emailInput.sendInput(email)
      passwordInput.sendInput(password)
      form.submit()

      @tailrec
      def checkPoint(): Unit = {
        scribe.info("Check point!")
        browser.byId("checkpointSubmitButton").click()
        browser.pageWait()
        if (browser.url == checkPointURL) {
          checkPoint()
        }
      }

      if (browser.url == checkPointURL) {
        val approvalCode = Await.result(approval, Duration.Inf)
        val input = browser.byId("approvals_code")
        input.sendInput(approvalCode.trim)
        checkPoint()
      }

      new Facebook(browser.cookies)
    } finally {
      browser.dispose()
    }
  }

  def load(file: File): Facebook = {
    val jsonString = IO.stream(file, new StringBuilder).toString
    JsonUtil.fromJsonString[Facebook](jsonString)
  }

  def main(args: Array[String]): Unit = {
    val facebook = load(new File("facebook.json"))
    val future = facebook.send("100000006305389", "Message sent by social-facebook!")
    val response = Await.result(future, Duration.Inf)
    scribe.info(s"Response: $response")
    scribe.info(response.content.get.asString)
    sys.exit(0)
  }
}