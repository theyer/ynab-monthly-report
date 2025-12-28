package com.ynabmonthlyreport.report

import com.ynabmonthlyreport.model.config.YnabMonthlyReportConfig
import com.ynabmonthlyreport.model.month.BudgetMonthData
import com.ynabmonthlyreport.model.month.CategoryData
import java.time.format.TextStyle
import java.util.Locale

class ReportAssembler(private val config: YnabMonthlyReportConfig) {
  fun getAssembledReport(budgetMonth: BudgetMonthData): String {
    val month = budgetMonth.month.month.getDisplayName(TextStyle.FULL, Locale.ENGLISH)
    val year = budgetMonth.month.year
    val reportBuilder = StringBuilder()
    reportBuilder.append("$month $year\n")
    if (budgetMonth.note?.isNotEmpty() ?: false) {
      reportBuilder.append("Notes:\n")
      reportBuilder.append("${budgetMonth.note}\n")
    }
    reportBuilder.append("\n")

    val generatorMap: Map<BaseReportGenerator?, List<CategoryData>> =
      budgetMonth.categories.groupBy { getGenerator(it) }
    for (generator in ORDERED_GENERATORS) {
      generatorMap[generator]?.let { reportBuilder.append("${generator.generate(it)}\n\n") }
    }
    return reportBuilder.toString()
  }

  private fun getGenerator(category: CategoryData): BaseReportGenerator? {
    return when {
      category.name in config.ignoredCategories || category.hidden -> null
      category.categoryGroupName in config.savingsCategoryGroups -> SAVINGS_GENERATOR
      category.goalTarget == 0L -> NO_GOAL_GENERATOR
      category.categoryGroupName in config.monthlySpendingCategoryGroups -> MONTHLY_GENERATOR
      category.categoryGroupName in config.nonMonthlySpendingCategoryGroups -> NON_MONTHLY_GENERATOR
      else -> NO_GOAL_GENERATOR
    }
  }

  companion object {
    private val MONTHLY_GENERATOR = MonthlySpendingReportGenerator
    private val NON_MONTHLY_GENERATOR = NonMonthlySpendingReportGenerator
    private val SAVINGS_GENERATOR = SavingsReportGenerator
    private val NO_GOAL_GENERATOR = NoGoalReportGenerator
    // All generators in the order in which their outputs will appear in the report.
    private val ORDERED_GENERATORS =
      listOf(MONTHLY_GENERATOR, NON_MONTHLY_GENERATOR, SAVINGS_GENERATOR, NO_GOAL_GENERATOR)
  }
}
