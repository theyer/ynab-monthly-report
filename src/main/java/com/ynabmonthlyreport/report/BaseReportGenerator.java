package com.ynabmonthlyreport.report;

import com.ynabmonthlyreport.model.config.YnabMonthlyReportConfig;
import com.ynabmonthlyreport.model.month.BudgetMonthData;
import com.ynabmonthlyreport.model.month.CategoryData;
import java.util.stream.Collectors;

/** Abstract base class used by all report generators. */
abstract class BaseReportGenerator {

  final YnabMonthlyReportConfig config;
  
  BaseReportGenerator(YnabMonthlyReportConfig config) {
    this.config = config;
  }

  /** The title of the generator's report. */
  abstract String title();

  /**
   * Returns true if the {@link CategoryData} is relevant to this generator, false otherwise.
   */
  abstract boolean filterCategory(CategoryData category);

  /** Generates a report string for a single {@link CategoryData}. */
  abstract String generateSingleCategory(CategoryData category);

  /**
   * Generates the complete report for the generator.
   * 
   * <p>
   * First sorts alphabetically, then places lines with a {@code FAILURE_ICON}
   * before those with a {@code SUCCESS_ICON}.
   */
  String generate(BudgetMonthData budgetMonth) {
    return title() + "\n" + budgetMonth.categories.stream()
        .filter(this::filterCategory)
        .map(this::generateSingleCategory)
        .sorted()
        .sorted((str1, str2) -> Character.compare(str2.charAt(0), str1.charAt(0)))
        .collect(Collectors.joining("\n"));
  }

}
