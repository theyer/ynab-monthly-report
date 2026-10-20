package com.ynabmonthlyreport

import com.ynabmonthlyreport.email.SendEmailTask
import com.ynabmonthlyreport.model.Constants.CONFIG_FILENAME
import com.ynabmonthlyreport.model.JsonConversionUtils
import com.ynabmonthlyreport.model.config.YnabMonthlyReportConfig
import com.ynabmonthlyreport.model.month.BudgetMonthData
import com.ynabmonthlyreport.model.transaction.TransactionData
import com.ynabmonthlyreport.report.HtmlReportRenderer
import com.ynabmonthlyreport.report.RenderedReport
import com.ynabmonthlyreport.scheduler.Scheduler
import com.ynabmonthlyreport.ynab.YnabFetcher
import java.io.File
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Paths
import kotlin.system.exitProcess

object Runner {
  @JvmStatic
  fun main(args: Array<String>) {
    val isDryRun = args.contains("--dry-run")
    val testEmail = args.find { it.startsWith("--test-email=") }?.substringAfter("--test-email=")
    val exportDir = args.find { it.startsWith("--export-dir=") }?.substringAfter("--export-dir=")

    val config: YnabMonthlyReportConfig = loadConfig()
    println("Generating report for ${config.budgetMonth}")
    val ynabFetcher = YnabFetcher(config)
    val scheduler = Scheduler(config, ynabFetcher)

    val schedulerOutcome: Scheduler.ScheduleReportOutcome = scheduler.readyToScheduleReport()
    if (schedulerOutcome != Scheduler.ScheduleReportOutcome.READY_TO_SCHEDULE) {
      System.err.println("Not ready to schedule: $schedulerOutcome")
      exitProcess(0)
    }

    val budgetMonth: BudgetMonthData = try {
      ynabFetcher.fetchBudgetMonthData()
    } catch (e: Exception) {
      when (e) {
        is IOException, is InterruptedException -> {
          System.err.println("Failed to fetch YNAB budget month data: $e")
          exitProcess(0)
        }
        else -> throw e
      }
    }

    val transactions: List<TransactionData> = try {
      ynabFetcher.fetchTransactionData()
    } catch (e: Exception) {
      when (e) {
        is IOException, is InterruptedException -> {
          System.err.println("Failed to fetch YNAB transaction data: $e")
          emptyList()
        }
        else -> throw e
      }
    }
    println("Successfully fetched YNAB data (${transactions.size} transactions)!")

    val htmlReportRenderer = HtmlReportRenderer(config)
    val renderedReport: RenderedReport = htmlReportRenderer.render(budgetMonth, transactions)
    println(renderedReport.plainText)

    if (exportDir != null) {
      val dir = File(exportDir)
      dir.mkdirs()
      File(dir, "rendered_email_report.html").writeText(renderedReport.htmlText)
      File(dir, "rendered_email_report_amp.html").writeText(renderedReport.ampHtmlText)
      println("Saved HTML & AMP previews to $exportDir")
    }

    if (config.enableEmailReport && !isDryRun) {
      val emailTask = SendEmailTask(config)
      val recipients = if (testEmail != null) listOf(testEmail) else config.emailSendToAddresses
      emailTask.sendEmail(renderedReport, recipientAddresses = recipients)
    } else if (isDryRun) {
      println("Dry run enabled: email delivery skipped.")
    }
  }

  private fun loadConfig(): YnabMonthlyReportConfig {
    return try {
      val configJson = String(Files.readAllBytes(Paths.get(CONFIG_FILENAME)))
      JsonConversionUtils.convertConfigJson(configJson)
    } catch (e: IOException) {
      throw RuntimeException(e)
    }
  }
}
