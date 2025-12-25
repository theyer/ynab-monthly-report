package com.ynabmonthlyreport.email;

import com.ynabmonthlyreport.model.config.YnabMonthlyReportConfig;

import java.io.UnsupportedEncodingException;
import java.time.format.TextStyle;
import java.util.Locale;
import java.util.Properties;
import java.util.stream.Collectors;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class SendEmailTask {
  
  private final YnabMonthlyReportConfig config;
  private final Properties properties;

  public SendEmailTask(YnabMonthlyReportConfig config) {
    this.config = config;
    this.properties = new Properties();
    properties.put("mail.smtp.host", "smtp.gmail.com");
		properties.put("mail.smtp.port", "587");
		properties.put("mail.smtp.auth", "true");
		properties.put("mail.smtp.starttls.enable", "true");
  }

  public void sendEmail(String body) {
    Authenticator auth = new Authenticator() {
			@Override
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(config.emailSendFromAddress, config.emailSendFromPassword);
			}
		};
		Session session = Session.getInstance(properties, auth);
    MimeMessage msg = new MimeMessage(session);

    try {
      msg.addHeader("Content-type", "text/HTML; charset=UTF-8");
      msg.addHeader("format", "flowed");
      msg.addHeader("Content-Transfer-Encoding", "8bit");

      msg.setFrom(new InternetAddress(config.emailSendFromAddress, config.emailSendFromName));
      msg.setRecipients(Message.RecipientType.TO,
          InternetAddress.parse(config.emailSendToAddresses.stream().collect(Collectors.joining(",")), false));

      msg.setSubject(getSubject(), "UTF-8");
      msg.setText(body, "UTF-8");
    } catch (MessagingException | UnsupportedEncodingException e) {
      throw new RuntimeException("Failed to build email", e);
    }

    System.out.println("Sending email...");
    try {
      Transport.send(msg);
    } catch (MessagingException e) {
      throw new RuntimeException("Failed to send email", e);
    }
    System.out.println("Email sent successfully");
  }

  private String getSubject() {
    String month = config.budgetMonth.getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH);
    int year = config.budgetMonth.getYear();
    return String.format("Budget Report: %s %s", month, year);
  }
}
