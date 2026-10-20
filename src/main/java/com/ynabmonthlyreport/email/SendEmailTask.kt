package com.ynabmonthlyreport.email

import com.ynabmonthlyreport.model.config.YnabMonthlyReportConfig
import com.ynabmonthlyreport.report.RenderedReport
import java.io.UnsupportedEncodingException
import java.time.format.TextStyle
import java.util.Locale
import java.util.Properties
import javax.activation.CommandMap
import javax.activation.DataHandler
import javax.activation.MailcapCommandMap
import javax.mail.Authenticator
import javax.mail.Message
import javax.mail.MessagingException
import javax.mail.PasswordAuthentication
import javax.mail.Session
import javax.mail.Transport
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeBodyPart
import javax.mail.internet.MimeMessage
import javax.mail.internet.MimeMultipart
import javax.mail.util.ByteArrayDataSource

class SendEmailTask(private val config: YnabMonthlyReportConfig) {

  fun sendEmail(
    report: RenderedReport,
    recipientAddresses: List<String> = config.emailSendToAddresses,
  ) {
    val msg = createBaseMessage(recipientAddresses)

    try {
      val multipart = MimeMultipart("alternative")

      // 1. Plain text fallback
      val plainPart = MimeBodyPart().apply {
        setText(report.plainText, "UTF-8")
      }
      multipart.addBodyPart(plainPart)

      // 2. Rich HTML fallback
      val htmlPart = MimeBodyPart().apply {
        setContent(report.htmlText, "text/html; charset=UTF-8")
      }
      multipart.addBodyPart(htmlPart)

      // 3. AMP for Email dynamic content
      // Note: text/x-amp-html requires ByteArrayDataSource to bypass JAF DataContentHandler lookup
      val ampPart = MimeBodyPart().apply {
        val ds = ByteArrayDataSource(report.ampHtmlText.toByteArray(Charsets.UTF_8), "text/x-amp-html; charset=UTF-8")
        dataHandler = DataHandler(ds)
        setHeader("Content-Type", "text/x-amp-html; charset=UTF-8")
        setHeader("Content-Transfer-Encoding", "8bit")
      }
      multipart.addBodyPart(ampPart)

      msg.setContent(multipart)
    } catch (e: Exception) {
      when (e) {
        is MessagingException,
        is UnsupportedEncodingException -> throw RuntimeException("Failed to build multipart email", e)
        else -> throw e
      }
    }

    dispatchMessage(msg, recipientAddresses)
  }

  /** Backward-compatible method for plain-text emails. */
  fun sendEmail(body: String, recipientAddresses: List<String> = config.emailSendToAddresses) {
    val msg = createBaseMessage(recipientAddresses)

    try {
      msg.setText(body, "UTF-8")
    } catch (e: MessagingException) {
      throw RuntimeException("Failed to set body on email", e)
    }

    dispatchMessage(msg, recipientAddresses)
  }

  private fun createBaseMessage(recipientAddresses: List<String>): MimeMessage {
    val auth = object : Authenticator() {
      override fun getPasswordAuthentication(): PasswordAuthentication =
        PasswordAuthentication(config.emailSendFromAddress, config.emailSendFromPassword)
    }
    val session: Session = Session.getInstance(PROPERTIES, auth)
    val msg = MimeMessage(session)

    try {
      msg.setFrom(InternetAddress(config.emailSendFromAddress, config.emailSendFromName))
      msg.setRecipients(
        Message.RecipientType.TO,
        InternetAddress.parse(recipientAddresses.joinToString(","), false)
      )
      msg.setSubject(getSubject(), "UTF-8")
    } catch (e: Exception) {
      when (e) {
        is MessagingException,
        is UnsupportedEncodingException -> throw RuntimeException("Failed to configure email headers", e)
        else -> throw e
      }
    }
    return msg
  }

  private fun dispatchMessage(msg: MimeMessage, recipientAddresses: List<String>) {
    println("Sending email to ${recipientAddresses.joinToString(", ")}...")
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
    init {
      try {
        val mc = CommandMap.getDefaultCommandMap() as? MailcapCommandMap ?: MailcapCommandMap()
        mc.addMailcap("text/x-amp-html;; x-java-content-handler=com.sun.mail.handlers.text_html")
        CommandMap.setDefaultCommandMap(mc)
      } catch (_: Exception) {
        // Fallback handled via ByteArrayDataSource
      }
    }

    private val PROPERTIES: Properties = Properties().apply {
      this["mail.smtp.host"] = "smtp.gmail.com"
      this["mail.smtp.port"] = "587"
      this["mail.smtp.auth"] = "true"
      this["mail.smtp.starttls.enable"] = "true"
    }
  }
}
