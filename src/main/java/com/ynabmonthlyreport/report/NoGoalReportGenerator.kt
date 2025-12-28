package com.ynabmonthlyreport.report

import com.ynabmonthlyreport.model.Constants.FAILURE_ICON
import com.ynabmonthlyreport.model.Constants.SUCCESS_ICON
import com.ynabmonthlyreport.model.config.YnabMonthlyReportConfig
import com.ynabmonthlyreport.model.month.CategoryData

internal object NoGoalReportGenerator : BaseReportGenerator() {
  override val title = "------ Misc Report ------"

  override fun generateSingleCategory(category: CategoryData): String {
    val hasSpending = category.activity > 0

    val icon: String = if (hasSpending) FAILURE_ICON else SUCCESS_ICON
    return String.format("%s %s: $%d spent", icon, category.name, category.activity)
  }
}
