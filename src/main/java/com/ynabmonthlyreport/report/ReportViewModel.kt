package com.ynabmonthlyreport.report

import java.util.Locale
import kotlin.math.abs

private fun formatNumber(value: Long): String = String.format(Locale.US, "%,d", value)

data class CategoryTransactionView(
  val date: String,
  val payee: String,
  val memo: String?,
  val amountFormatted: String,
  val amountDollars: Double,
)

data class MonthlyCategoryView(
  val id: String?,
  val name: String,
  val goalTarget: Long,
  val activity: Long,
  val diff: Long,
  val percentUsed: Long,
  val progressBarWidthPercent: Long,
  val isOver: Boolean,
  val isExact: Boolean,
  val isUnder: Boolean,
  val statusBadgeText: String,
  val statusClass: String, // "over", "under", "exact"
  val topTransactions: List<CategoryTransactionView>,
  val hasTransactions: Boolean = topTransactions.isNotEmpty(),
  val goalTargetFormatted: String = formatNumber(goalTarget),
  val activityFormatted: String = formatNumber(activity),
  val diffFormatted: String = formatNumber(diff),
)

data class SinkingFundView(
  val id: String?,
  val name: String,
  val target: Long,
  val budgeted: Long,
  val activity: Long,
  val balance: Long,
  val statusMessage: String,
  val statusType: String, // "on-track", "overspent", "underfunded"
  val topTransactions: List<CategoryTransactionView>,
  val hasTransactions: Boolean = topTransactions.isNotEmpty(),
  val targetFormatted: String = formatNumber(target),
  val budgetedFormatted: String = formatNumber(budgeted),
  val activityFormatted: String = formatNumber(activity),
  val balanceFormatted: String = formatNumber(balance),
)

data class SavingsCategoryView(
  val id: String?,
  val name: String,
  val goalTarget: Long,
  val budgeted: Long,
  val balance: Long,
  val isOverOrMet: Boolean,
  val saveDiff: Long,
  val statusBadgeText: String,
  val statusClass: String, // "on-track", "under"
  val goalTargetFormatted: String = formatNumber(goalTarget),
  val budgetedFormatted: String = formatNumber(budgeted),
  val balanceFormatted: String = formatNumber(balance),
  val saveDiffFormatted: String = formatNumber(saveDiff),
)

data class MiscCategoryView(
  val id: String?,
  val name: String,
  val activity: Long,
  val hasSpending: Boolean,
  val topTransactions: List<CategoryTransactionView>,
  val hasTransactions: Boolean = topTransactions.isNotEmpty(),
  val activityFormatted: String = formatNumber(activity),
)

data class KpiSummary(
  val totalSpent: Long,
  val totalBudget: Long,
  val netVariance: Long,
  val isNetOver: Boolean,
  val monthlyOnTrackCount: Int,
  val monthlyTotalCount: Int,
  val monthlyHealthPercent: Int,
  val ageOfMoney: Long,
  val totalSpentFormatted: String = formatNumber(totalSpent),
  val totalBudgetFormatted: String = formatNumber(totalBudget),
  val netVarianceFormatted: String = formatNumber(abs(netVariance)),
)

data class ReportViewModel(
  val monthTitle: String,
  val note: String?,
  val ynabWebUrl: String,
  val kpiSummary: KpiSummary,
  val attentionCategories: List<MonthlyCategoryView>,
  val monthlyCategories: List<MonthlyCategoryView>,
  val sinkingFunds: List<SinkingFundView>,
  val savingsCategories: List<SavingsCategoryView>,
  val miscActiveCategories: List<MiscCategoryView>,
  val miscZeroCategories: List<MiscCategoryView>,
)
