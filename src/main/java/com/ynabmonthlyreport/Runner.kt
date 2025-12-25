package com.ynabmonthlyreport

import com.ynabmonthlyreport.email.SendEmailTask
import com.ynabmonthlyreport.model.Constants.CONFIG_FILENAME
import com.ynabmonthlyreport.model.JsonConversionUtils
import com.ynabmonthlyreport.model.config.YnabMonthlyReportConfig
import com.ynabmonthlyreport.model.month.BudgetMonthData
import com.ynabmonthlyreport.report.ReportAssembler
import com.ynabmonthlyreport.scheduler.Scheduler
import com.ynabmonthlyreport.ynab.YnabFetcher
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Paths
import kotlin.system.exitProcess

object Runner {
  @JvmStatic
  fun main(args: Array<String>) {
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
    println("Successfully fetched YNAB data!")

    val reportAssembler = ReportAssembler(config)
    val report: String = reportAssembler.getAssembledReport(budgetMonth)
    println(report)

    if (config.enableEmailReport) {
      val emailTask = SendEmailTask(config)
      emailTask.sendEmail(report)
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