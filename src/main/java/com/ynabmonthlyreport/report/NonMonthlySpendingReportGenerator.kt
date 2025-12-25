package com.ynabmonthlyreport.report

import com.ynabmonthlyreport.model.Constants.FAILURE_ICON
import com.ynabmonthlyreport.model.Constants.SUCCESS_ICON
import com.ynabmonthlyreport.model.config.YnabMonthlyReportConfig
import com.ynabmonthlyreport.model.month.CategoryData
import kotlin.math.abs

internal class NonMonthlySpendingReportGenerator(config: YnabMonthlyReportConfig) : BaseReportGenerator(config) {
  override val title = "------ Non-Monthly Spending Report ------"

  override fun filterCategory(category: CategoryData): Boolean {
    return category.name !in config.savingsCategories && category.goalType != CategoryData.GoalType.NEED && category.goalTarget != 0L
  }

  override fun generateSingleCategory(category: CategoryData): String {
    val budgetedUnderGoal = category.budgeted <= category.goalTarget
    val spendDiff = abs(category.budgeted - category.goalTarget)

    val icon: String = if (budgetedUnderGoal) SUCCESS_ICON else FAILURE_ICON
    val overOrUnder = if (budgetedUnderGoal) "under" else "over"
    return String.format(
      "%s %s: $%d %s goal (Goal: $%d, Budgeted: $%d, Spend: $%d)",
      icon,
      category.name,
      spendDiff,
      overOrUnder,
      category.goalTarget,
      category.budgeted,
      category.activity
    )
  }
}
