package com.ynabmonthlyreport.report

import com.ynabmonthlyreport.model.config.YnabMonthlyReportConfig
import com.ynabmonthlyreport.model.month.BudgetMonthData
import com.ynabmonthlyreport.model.month.CategoryData
import com.ynabmonthlyreport.model.transaction.TransactionData
import java.time.format.TextStyle
import java.util.Locale
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

object ReportViewModelBuilder {

  private fun fmt(num: Long): String = String.format(Locale.US, "%,d", num)

  fun build(
    budgetMonth: BudgetMonthData,
    transactions: List<TransactionData>,
    config: YnabMonthlyReportConfig,
  ): ReportViewModel {
    val monthDisplayName = budgetMonth.month.month.getDisplayName(TextStyle.FULL, Locale.ENGLISH)
    val year = budgetMonth.month.year
    val monthTitle = "$monthDisplayName $year"
    val yearMonthString = String.format("%04d-%02d", year, budgetMonth.month.monthValue)
    val ynabWebUrl = "https://app.ynab.com/${config.budgetId}/budget/$yearMonthString"

    val validCategories = budgetMonth.categories.filter {
      it.name !in config.ignoredCategories && !it.hidden
    }

    // 1. Monthly Spending Categories
    val monthlyList = validCategories
      .filter { it.categoryGroupName in config.monthlySpendingCategoryGroups && it.goalTarget > 0L }
      .map { cat ->
        val isOver = cat.activity > cat.goalTarget
        val isExact = cat.activity == cat.goalTarget
        val isUnder = cat.activity < cat.goalTarget
        val diff = abs(cat.activity - cat.goalTarget)
        val percentUsed = if (cat.goalTarget > 0) {
          Math.round((cat.activity.toDouble() / cat.goalTarget.toDouble()) * 100)
        } else {
          0L
        }
        val progressBarWidth = min(max(percentUsed, 0L), 100L)
        val badgeText = when {
          isOver -> "+\$${fmt(diff)} over"
          isExact -> "Exact"
          else -> "-\$${fmt(diff)} saved"
        }
        val statusClass = when {
          isOver -> "over"
          isExact -> "exact"
          else -> "under"
        }
        val topTx = TransactionAggregator.getTopTransactions(cat.id, cat.name, transactions)

        MonthlyCategoryView(
          id = cat.id,
          name = cat.name ?: "Unknown",
          goalTarget = cat.goalTarget,
          activity = cat.activity,
          diff = diff,
          percentUsed = percentUsed,
          progressBarWidthPercent = progressBarWidth,
          isOver = isOver,
          isExact = isExact,
          isUnder = isUnder,
          statusBadgeText = badgeText,
          statusClass = statusClass,
          topTransactions = topTx,
        )
      }
      .sortedWith(
        compareByDescending<MonthlyCategoryView> { it.isOver }
          .thenByDescending { if (it.isOver) it.diff else -it.diff }
          .thenBy { it.name }
      )

    val attentionCategories = monthlyList.filter { it.isOver }

    // 2. Non-Monthly Spending Categories (Sinking Funds)
    val sinkingFunds = validCategories
      .filter { it.categoryGroupName in config.nonMonthlySpendingCategoryGroups && it.goalTarget > 0L }
      .map { cat ->
        val isUnderfunded = (cat.goalUnderFunded ?: 0L) > 0L
        var overspentAmount = 0L
        if (cat.hasMonthlyTarget) {
          val netSpend = cat.budgeted - cat.balance
          if (netSpend > cat.goalTarget) {
            overspentAmount = netSpend - cat.goalTarget
          }
        } else {
          val overallFunded = cat.goalOverallFunded ?: 0L
          if (overallFunded > cat.goalTarget) {
            overspentAmount = overallFunded - cat.goalTarget
          }
        }

        val statusMessage: String
        val statusType: String
        if (overspentAmount > 0) {
          statusMessage = "\$${fmt(overspentAmount)} overspent"
          statusType = "overspent"
        } else if (isUnderfunded) {
          statusMessage = "\$${fmt(cat.goalUnderFunded ?: 0L)} underfunded"
          statusType = "underfunded"
        } else {
          statusMessage = "On track"
          statusType = "on-track"
        }

        val topTx = if (cat.activity > 0) {
          TransactionAggregator.getTopTransactions(cat.id, cat.name, transactions)
        } else {
          emptyList()
        }

        SinkingFundView(
          id = cat.id,
          name = cat.name ?: "Unknown",
          target = cat.goalTarget,
          budgeted = cat.budgeted,
          activity = cat.activity,
          balance = cat.balance,
          statusMessage = statusMessage,
          statusType = statusType,
          topTransactions = topTx,
        )
      }
      .sortedWith(
        compareBy<SinkingFundView> {
          when (it.statusType) {
            "overspent" -> 0
            "underfunded" -> 1
            else -> 2
          }
        }.thenBy { it.name }
      )

    // 3. Savings Categories
    val savingsList = validCategories
      .filter { it.categoryGroupName in config.savingsCategoryGroups }
      .map { cat ->
        val budgetedToGoal = cat.budgeted >= cat.goalTarget
        val saveDiff = abs(cat.budgeted - cat.goalTarget)
        val badgeText = if (budgetedToGoal) "On track" else "\$${fmt(saveDiff)} under goal"
        val statusClass = if (budgetedToGoal) "on-track" else "under"

        SavingsCategoryView(
          id = cat.id,
          name = cat.name ?: "Unknown",
          goalTarget = cat.goalTarget,
          budgeted = cat.budgeted,
          balance = cat.balance,
          isOverOrMet = budgetedToGoal,
          saveDiff = saveDiff,
          statusBadgeText = badgeText,
          statusClass = statusClass,
        )
      }
      .sortedWith(
        compareByDescending<SavingsCategoryView> { !it.isOverOrMet }
          .thenBy { it.name }
      )

    // 4. Misc / No Goal Categories
    val otherCategories = validCategories.filter { cat ->
      cat.categoryGroupName !in config.monthlySpendingCategoryGroups &&
        cat.categoryGroupName !in config.nonMonthlySpendingCategoryGroups &&
        cat.categoryGroupName !in config.savingsCategoryGroups ||
        cat.goalTarget == 0L
    }

    val miscActive = otherCategories
      .filter { it.activity > 0 }
      .map { cat ->
        MiscCategoryView(
          id = cat.id,
          name = cat.name ?: "Unknown",
          activity = cat.activity,
          hasSpending = true,
          topTransactions = TransactionAggregator.getTopTransactions(cat.id, cat.name, transactions),
        )
      }
      .sortedByDescending { it.activity }

    val miscZero = otherCategories
      .filter { it.activity <= 0 }
      .map { cat ->
        MiscCategoryView(
          id = cat.id,
          name = cat.name ?: "Unknown",
          activity = cat.activity,
          hasSpending = false,
          topTransactions = emptyList(),
        )
      }
      .sortedBy { it.name }

    // KPI Summary
    val totalSpent = monthlyList.sumOf { it.activity }
    val totalBudget = monthlyList.sumOf { it.goalTarget }
    val netVariance = totalSpent - totalBudget
    val onTrackCount = monthlyList.count { !it.isOver }
    val totalCount = monthlyList.size
    val healthPercent = if (totalCount > 0) Math.round((onTrackCount.toDouble() / totalCount.toDouble()) * 100).toInt() else 100

    val kpiSummary = KpiSummary(
      totalSpent = totalSpent,
      totalBudget = totalBudget,
      netVariance = netVariance,
      isNetOver = netVariance > 0,
      monthlyOnTrackCount = onTrackCount,
      monthlyTotalCount = totalCount,
      monthlyHealthPercent = healthPercent,
      ageOfMoney = budgetMonth.ageOfMoney,
    )

    return ReportViewModel(
      monthTitle = monthTitle,
      note = budgetMonth.note,
      ynabWebUrl = ynabWebUrl,
      kpiSummary = kpiSummary,
      attentionCategories = attentionCategories,
      monthlyCategories = monthlyList,
      sinkingFunds = sinkingFunds,
      savingsCategories = savingsList,
      miscActiveCategories = miscActive,
      miscZeroCategories = miscZero,
    )
  }
}
