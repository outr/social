package com.outr.social

import org.openqa.selenium.By
import scala.collection.JavaConverters._

class WebElement(e: org.openqa.selenium.WebElement) extends AbstractElement {
  override def by(by: By): List[WebElement] = e.findElements(by).asScala.toList.map(new WebElement(_))

  def click(): Unit = e.click()
  def submit(): Unit = e.submit()

  def sendInput(text: String): Unit = {
    e.click()
    e.sendKeys(text)
  }
}