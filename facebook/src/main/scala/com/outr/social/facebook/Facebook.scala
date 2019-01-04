package com.outr.social.facebook

import com.outr.social.HeadlessBrowser
import io.youi.net._
import io.youi.http.cookie.ResponseCookie
import javax.swing.JOptionPane

import scala.concurrent.{Await, Future}
import scribe.Execution.global

import scala.annotation.tailrec
import scala.concurrent.duration.Duration

case class Facebook(cookies: List[ResponseCookie]) {

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
}