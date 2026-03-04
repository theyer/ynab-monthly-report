package com.ynabmonthlyreport.report

import com.ynabmonthlyreport.model.Constants.FAILURE_ICON
import com.ynabmonthlyreport.model.Constants.SUCCESS_ICON
import com.ynabmonthlyreport.model.config.YnabMonthlyReportConfig
import com.ynabmonthlyreport.model.month.CategoryData
import kotlin.math.abs

internal object SavingsReportGenerator : BaseReportGenerator() {
  override val title = "------ Savings Report ------"

  override fun generateSingleCategory(category: CategoryData): String {
    val budgetedToGoal = category.budgeted >= category.goalTarget
    val saveDiff = abs(category.budgeted - category.goalTarget)

    val icon: String = if (budgetedToGoal) SUCCESS_ICON else FAILURE_ICON
    val overOrUnder = if (budgetedToGoal) "over" else "under"
    return String.format(
      "%s %s: $%d %s goal (Goal: $%d, Budgeted: $%d, Balance: $%d)",
      icon,
      category.name,
      saveDiff,
      overOrUnder,
      category.goalTarget,
      category.budgeted,
      category.balance
    )
  }
}
