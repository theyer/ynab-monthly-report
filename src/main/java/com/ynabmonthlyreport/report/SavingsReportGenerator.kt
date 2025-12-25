package com.ynabmonthlyreport.report;

import static com.ynabmonthlyreport.model.Constants.FAILURE_ICON;
import static com.ynabmonthlyreport.model.Constants.SUCCESS_ICON;

import com.ynabmonthlyreport.model.config.YnabMonthlyReportConfig;
import com.ynabmonthlyreport.model.month.CategoryData;

class SavingsReportGenerator extends BaseReportGenerator {

  SavingsReportGenerator(YnabMonthlyReportConfig config) {
    super(config);
  }

  @Override
  String title() {
    return "------ Savings Report ------";
  }

  @Override
  boolean filterCategory(CategoryData category) {
    return config.savingsCategories.contains(category.name);
  }

  @Override
  String generateSingleCategory(CategoryData category) {
    boolean budgetedToGoal = category.budgeted >= category.goalTarget;
    long saveDiff = Math.abs(category.budgeted - category.goalTarget);

    String icon = budgetedToGoal ? SUCCESS_ICON : FAILURE_ICON;
    String overOrUnder = budgetedToGoal ? "over" : "under";
    return String.format("%s %s: $%d %s goal (Goal: $%d, Budgeted: $%d)", icon, category.name, saveDiff, overOrUnder,
        category.goalTarget, category.budgeted);
  }
}
