package com.ynabmonthlyreport.report

import com.ynabmonthlyreport.model.config.YnabMonthlyReportConfig
import com.ynabmonthlyreport.model.month.BudgetMonthData
import com.ynabmonthlyreport.model.month.CategoryData
import java.util.stream.Collectors

/** Abstract base class used by all report generators.  */
internal sealed class BaseReportGenerator(val config: YnabMonthlyReportConfig) {
  /** The title of the generator's report.  */
  abstract val title: String

  /** Returns true if the [CategoryData] is relevant to this generator, false otherwise. */
  abstract fun filterCategory(category: CategoryData): Boolean

  /** Generates a report string for a single [CategoryData].  */
  abstract fun generateSingleCategory(category: CategoryData): String

  /**
   * Generates the complete report for the generator.
   *
   * First sorts alphabetically, then places lines with a `FAILURE_ICON`
   * before those with a `SUCCESS_ICON`.
   */
  fun generate(budgetMonth: BudgetMonthData): String {
    return "$title\n" + budgetMonth.categories.asSequence()
      .filter { this.filterCategory(it) }
      .map { this.generateSingleCategory(it) }
      .sorted()
      .sortedWith { str1: String, str2: String -> str2[0].compareTo(str1[0]) }
      .joinToString("\n")
  }
}
