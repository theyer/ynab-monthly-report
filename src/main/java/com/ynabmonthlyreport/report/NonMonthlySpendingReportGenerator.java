package com.ynabmonthlyreport.report;

import static com.ynabmonthlyreport.model.Constants.FAILURE_ICON;
import static com.ynabmonthlyreport.model.Constants.SUCCESS_ICON;

import com.ynabmonthlyreport.model.config.YnabMonthlyReportConfig;
import com.ynabmonthlyreport.model.month.CategoryData;

class NonMonthlySpendingReportGenerator extends BaseReportGenerator {

  NonMonthlySpendingReportGenerator(YnabMonthlyReportConfig config) {
    super(config);
  }

  @Override
  String title() {
    return "------ Non-Monthly Spending Report ------";
  }

  @Override
  boolean filterCategory(CategoryData category) {
    return !config.savingsCategories.contains(category.name)
        && !CategoryData.GoalType.NEED.equals(category.goalType)
        && category.goalTarget != 0;
  }

  @Override
  String generateSingleCategory(CategoryData category) {
    boolean budgetedUnderGoal = category.budgeted <= category.goalTarget;
    long spendDiff = Math.abs(category.budgeted - category.goalTarget);

    String icon = budgetedUnderGoal ? SUCCESS_ICON : FAILURE_ICON;
    String overOrUnder = budgetedUnderGoal ? "under" : "over";
    return String.format("%s %s: $%d %s goal (Goal: $%d, Budgeted: $%d, Spend: $%d)", icon, category.name, spendDiff,
        overOrUnder, category.goalTarget, category.budgeted, category.activity);
  }
}
