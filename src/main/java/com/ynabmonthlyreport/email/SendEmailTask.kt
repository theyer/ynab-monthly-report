package com.ynabmonthlyreport.email

import com.ynabmonthlyreport.model.config.YnabMonthlyReportConfig
import java.io.UnsupportedEncodingException
import java.time.format.TextStyle
import java.util.Locale
import java.util.Properties
import javax.mail.Authenticator
import javax.mail.Message
import javax.mail.MessagingException
import javax.mail.PasswordAuthentication
import javax.mail.Session
import javax.mail.Transport
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage

class SendEmailTask(private val config: YnabMonthlyReportConfig) {
  fun sendEmail(body: String) {
    val auth = object : Authenticator() {
      protected override fun getPasswordAuthentication(): PasswordAuthentication =
        PasswordAuthentication(config.emailSendFromAddress, config.emailSendFromPassword)
    }
    val session: Session = Session.getInstance(PROPERTIES, auth)
    val msg = MimeMessage(session)

    try {
      msg.addHeader("Content-type", "text/HTML; charset=UTF-8")
      msg.addHeader("format", "flowed")
      msg.addHeader("Content-Transfer-Encoding", "8bit")

      msg.setFrom(InternetAddress(config.emailSendFromAddress, config.emailSendFromName))
      msg.setRecipients(
        Message.RecipientType.TO,
        InternetAddress.parse(config.emailSendToAddresses.joinToString(","), false)
      )

      msg.setSubject(getSubject(), "UTF-8")
      msg.setText(body, "UTF-8")
    } catch (e: Exception) {
      when (e) {
        is MessagingException,
        is UnsupportedEncodingException -> throw RuntimeException("Failed to build email", e)
      }
    }

    println("Sending email...")
    try {
      Transport.send(msg)
    } catch (e: MessagingException) {
      throw RuntimeException("Failed to send email", e)
    }
    println("Email sent successfully")
  }

  private fun getSubject(): String {
    val month: String = config.budgetMonth.month.getDisplayName(TextStyle.FULL, Locale.ENGLISH)
    val year: Int = config.budgetMonth.year
    return "Budget Report: $month $year"
  }

  companion object {
    private val PROPERTIES: Properties = Properties().apply {
      this["mail.smtp.host"] = "smtp.gmail.com"
      this["mail.smtp.port"] = "587"
      this["mail.smtp.auth"] = "true"
      this["mail.smtp.starttls.enable"] = "true"
    }
  }
}
