package com.ynabmonthlyreport.report

import com.ynabmonthlyreport.model.Constants.FAILURE_ICON
import com.ynabmonthlyreport.model.Constants.SUCCESS_ICON
import com.ynabmonthlyreport.model.config.YnabMonthlyReportConfig
import com.ynabmonthlyreport.model.month.CategoryData

internal class NoGoalReportGenerator(config: YnabMonthlyReportConfig) : BaseReportGenerator(config) {
  override val title = "------ Misc Report ------"

  override fun filterCategory(category: CategoryData): Boolean {
    return category.goalTarget == 0L && category.name !in config.savingsCategories && category.name !in config.ignoredCategories && !category.hidden
  }

  override fun generateSingleCategory(category: CategoryData): String {
    val hasSpending = category.activity > 0

    val icon: String = if (hasSpending) FAILURE_ICON else SUCCESS_ICON
    return String.format("%s %s: $%d spent", icon, category.name, category.activity)
  }
}
