package com.ynabmonthlyreport.report

import com.ynabmonthlyreport.model.config.YnabMonthlyReportConfig
import com.ynabmonthlyreport.model.month.BudgetMonthData
import com.ynabmonthlyreport.model.month.CategoryData
import java.util.stream.Collectors

/** Abstract base class used by all report generators.  */
internal sealed class BaseReportGenerator() {
  /** The title of the generator's report.  */
  abstract val title: String

  /** Generates a report string for a single [CategoryData].  */
  abstract fun generateSingleCategory(category: CategoryData): String

  /**
   * Generates the complete report for the generator.
   *
   * First sorts alphabetically, then places lines with a `FAILURE_ICON`
   * before those with a `SUCCESS_ICON`.
   */
  fun generate(categories: List<CategoryData>): String {
    return "$title\n" + categories.asSequence()
      .map { this.generateSingleCategory(it) }
      .sorted()
      .sortedWith { str1: String, str2: String -> str2[0].compareTo(str1[0]) }
      .joinToString("\n")
  }
}
