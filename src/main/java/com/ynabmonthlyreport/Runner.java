package com.ynabmonthlyreport;

import static com.ynabmonthlyreport.model.Constants.CONFIG_FILENAME;

import com.ynabmonthlyreport.model.JsonConversionUtils;
import com.ynabmonthlyreport.model.config.YnabMonthlyReportConfig;
import com.ynabmonthlyreport.model.month.BudgetMonthData;
import com.ynabmonthlyreport.report.ReportAssembler;
import com.ynabmonthlyreport.scheduler.Scheduler;
import com.ynabmonthlyreport.scheduler.Scheduler.ScheduleReportOutcome;
import com.ynabmonthlyreport.ynab.YnabFetcher;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;

public class Runner {
  public static void main(String args[]) {
    YnabMonthlyReportConfig config = loadConfig();
    if (config.budgetMonth == null) {
      config.budgetMonth = LocalDate.now().minusMonths(1);
      System.out.println("Month not specified in config; using " + config.budgetMonth);
    }

    YnabFetcher ynabFetcher = new YnabFetcher(config.apiKey, config.budgetId, config.budgetMonth);
    Scheduler scheduler = new Scheduler(config, ynabFetcher);
    ScheduleReportOutcome schedulerOutcome = scheduler.readyToScheduleReport();
    if (!ScheduleReportOutcome.READY_TO_SCHEDULE.equals(schedulerOutcome)) {
      System.err.println("Not ready to schedule: " + schedulerOutcome);
      System.exit(0);
    }

    BudgetMonthData budgetMonth = null;
    try {
      budgetMonth = ynabFetcher.fetchBudgetMonthData();
    } catch (IOException | InterruptedException e) {
      System.err.println("Failed to fetch YNAB budget month data: " + e);
      System.exit(0);
    }
    System.out.println("Successfully fetched YNAB data!");

    ReportAssembler reportAssembler = new ReportAssembler(config);
    String report = reportAssembler.getAssembledReport(budgetMonth);
    System.out.println(report);
  }

  private static YnabMonthlyReportConfig loadConfig() {
    String configJson = null;
    try {
      configJson = new String(Files.readAllBytes(Paths.get(CONFIG_FILENAME)));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    return JsonConversionUtils.convertConfigJson(configJson);
  }
}