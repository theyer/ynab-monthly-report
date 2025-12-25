package com.ynabmonthlyreport.report;

import static com.ynabmonthlyreport.model.Constants.FAILURE_ICON;
import static com.ynabmonthlyreport.model.Constants.SUCCESS_ICON;

import com.ynabmonthlyreport.model.config.YnabMonthlyReportConfig;
import com.ynabmonthlyreport.model.month.CategoryData;

class NoGoalReportGenerator extends BaseReportGenerator {

  NoGoalReportGenerator(YnabMonthlyReportConfig config) {
    super(config);
  }

  @Override
  String title() {
    return "------ Misc Report ------";
  }

  @Override
  boolean filterCategory(CategoryData category) {
    return category.goalTarget == 0
        && !config.savingsCategories.contains(category.name)
        && !config.ignoredCategories.contains(category.name)
        && !category.hidden;
  }

  @Override
  String generateSingleCategory(CategoryData category) {
    boolean hasSpending = category.activity > 0;

    String icon = hasSpending ? FAILURE_ICON : SUCCESS_ICON;
    return String.format("%s %s: $%d spent", icon, category.name, category.activity);
  }
}
