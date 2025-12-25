package com.ynabmonthlyreport.report;

import static com.ynabmonthlyreport.model.Constants.FAILURE_ICON;
import static com.ynabmonthlyreport.model.Constants.SUCCESS_ICON;

import com.ynabmonthlyreport.model.config.YnabMonthlyReportConfig;
import com.ynabmonthlyreport.model.month.CategoryData;

class MonthlySpendingReportGenerator extends BaseReportGenerator {

  MonthlySpendingReportGenerator(YnabMonthlyReportConfig config) {
    super(config);
  }

  @Override
  String title() {
    return "------ Monthly Spending Report ------";
  }

  @Override boolean filterCategory(CategoryData category) {
    return !config.savingsCategories.contains(category.name)
        && CategoryData.GoalType.NEED.equals(category.goalType)
        && category.goalTarget != 0;
  }

  @Override
  String generateSingleCategory(CategoryData category) {
    boolean spendUnderGoal = category.activity <= category.goalTarget;
    long spendDiff = Math.abs(category.activity - category.goalTarget);

    String icon = spendUnderGoal ? SUCCESS_ICON : FAILURE_ICON;
    String overOrUnder = spendUnderGoal ? "under" : "over";
    return String.format("%s %s: $%d %s goal (Goal: $%d, Spend: $%d)", icon, category.name, spendDiff, overOrUnder,
        category.goalTarget, category.activity);
  }
}
