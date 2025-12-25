package com.ynabmonthlyreport.report

import com.ynabmonthlyreport.model.config.YnabMonthlyReportConfig
import com.ynabmonthlyreport.model.month.BudgetMonthData
import java.time.format.TextStyle
import java.util.Locale

class ReportAssembler(config: YnabMonthlyReportConfig) {
  private val generators: List<BaseReportGenerator> = listOf(
    MonthlySpendingReportGenerator(config),
    NonMonthlySpendingReportGenerator(config),
    SavingsReportGenerator(config),
    NoGoalReportGenerator(config)
  )

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

    reportBuilder.append(generators.asSequence().map { it.generate(budgetMonth) }.joinToString("\n\n"))
    return reportBuilder.toString()
  }
}
