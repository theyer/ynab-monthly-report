package com.ynabmonthlyreport.scheduler;

import com.ynabmonthlyreport.model.config.YnabMonthlyReportConfig;
import com.ynabmonthlyreport.model.month.BudgetMonthData;
import com.ynabmonthlyreport.model.month.CategoryData;
import com.ynabmonthlyreport.model.transaction.TransactionData;
import com.ynabmonthlyreport.ynab.YnabFetcher;
import java.io.IOException;
import java.util.List;

public class Scheduler {

  private final YnabMonthlyReportConfig config;
  private final YnabFetcher ynabFetcher;

  public Scheduler(YnabMonthlyReportConfig config, YnabFetcher ynabFetcher) {
    this.config = config;
    this.ynabFetcher = ynabFetcher;
  }

  public enum ScheduleReportOutcome {
    READY_TO_SCHEDULE,
    TO_BE_BUDGETED_NOT_EMPTY,
    SPENDING_BALANCES_NOT_EMPTY,
    NEGATIVE_BALANCES,
    TRANSACTIONS_UNCLEARED,
    TRANSACTIONS_NOT_APPROVED,
    DATA_FETCH_ERROR,
  }

  public ScheduleReportOutcome readyToScheduleReport() {
    BudgetMonthData budgetMonth = null;
    List<TransactionData> transactions = null;
    try {
      budgetMonth = ynabFetcher.fetchBudgetMonthData();
      transactions = ynabFetcher.fetchTransactionData();
    } catch (IOException | InterruptedException e) {
      System.err.println(e.toString());
      return ScheduleReportOutcome.DATA_FETCH_ERROR;
    }

    if (budgetMonth.toBeBudgeted != 0) {
      return ScheduleReportOutcome.TO_BE_BUDGETED_NOT_EMPTY;
    }

    if (budgetMonth.categories.stream()
        .filter(category -> config.balanceZeroedMonthlyCategoryGroups.contains(category.categoryGroupName))
        .anyMatch(category -> category.balance != 0)) {
      return ScheduleReportOutcome.SPENDING_BALANCES_NOT_EMPTY;
    }

    if (budgetMonth.categories.stream()
        .filter(category -> !config.ignoredCategories.contains(category.name))
        .anyMatch(category -> category.balance < 0)) {
      return ScheduleReportOutcome.NEGATIVE_BALANCES;
    }

    if (transactions.stream()
        .anyMatch(transaction -> TransactionData.ClearedStatus.UNCLEARED.equals(transaction.clearedStatus))) {
      return ScheduleReportOutcome.TRANSACTIONS_UNCLEARED;
    }

    if (transactions.stream().anyMatch(transaction -> !transaction.approved)) {
      return ScheduleReportOutcome.TRANSACTIONS_NOT_APPROVED;
    }

    return ScheduleReportOutcome.READY_TO_SCHEDULE;
  }
}
