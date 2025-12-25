package com.ynabmonthlyreport.scheduler

import com.ynabmonthlyreport.model.config.YnabMonthlyReportConfig
import com.ynabmonthlyreport.model.month.BudgetMonthData
import com.ynabmonthlyreport.model.transaction.TransactionData
import com.ynabmonthlyreport.ynab.YnabFetcher
import java.io.IOException

class Scheduler(private val config: YnabMonthlyReportConfig, private val ynabFetcher: YnabFetcher) {
  enum class ScheduleReportOutcome {
    READY_TO_SCHEDULE,
    TO_BE_BUDGETED_NOT_EMPTY,
    SPENDING_BALANCES_NOT_EMPTY,
    NEGATIVE_BALANCES,
    TRANSACTIONS_UNCLEARED,
    TRANSACTIONS_NOT_APPROVED,
    DATA_FETCH_ERROR,
  }

  fun readyToScheduleReport(): ScheduleReportOutcome {
    val budgetMonth: BudgetMonthData = try {
      ynabFetcher.fetchBudgetMonthData()
    } catch (e: Exception) {
      return handleFetchError(e)
    }
    val transactions: List<TransactionData> = try {
      ynabFetcher.fetchTransactionData()
    } catch (e: Exception) {
      return handleFetchError(e)
    }

    return when {
      budgetMonth.toBeBudgeted != 0L -> ScheduleReportOutcome.TO_BE_BUDGETED_NOT_EMPTY
      budgetMonth.categories.asSequence().filter { it.categoryGroupName in config.balanceZeroedMonthlyCategoryGroups }
        .any { it.balance != 0L } -> ScheduleReportOutcome.SPENDING_BALANCES_NOT_EMPTY
      budgetMonth.categories.asSequence().filter { it.name !in config.ignoredCategories }
        .any { it.balance < 0 } -> ScheduleReportOutcome.NEGATIVE_BALANCES
      transactions.any { it.cleared == TransactionData.ClearedStatus.UNCLEARED } -> ScheduleReportOutcome.TRANSACTIONS_UNCLEARED
      transactions.any { !it.approved } -> ScheduleReportOutcome.TRANSACTIONS_NOT_APPROVED
      else -> ScheduleReportOutcome.READY_TO_SCHEDULE
    }
  }

  private fun handleFetchError(e: Exception): ScheduleReportOutcome {
    when (e) {
      is IOException, is InterruptedException -> {
        System.err.println(e.toString())
        return ScheduleReportOutcome.DATA_FETCH_ERROR
      }
      else -> throw e
    }
  }
}
