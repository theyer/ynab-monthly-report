package com.ynabmonthlyreport.report;

import com.ynabmonthlyreport.model.config.YnabMonthlyReportConfig;
import com.ynabmonthlyreport.model.month.BudgetMonthData;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class ReportAssembler {
  private List<BaseReportGenerator> generators;

  public ReportAssembler(YnabMonthlyReportConfig config) {
    this.generators = List.of(new MonthlySpendingReportGenerator(config), new NonMonthlySpendingReportGenerator(config),
        new SavingsReportGenerator(config), new NoGoalReportGenerator(config));
  }

  public String getAssembledReport(BudgetMonthData budgetMonth) {
    String month = budgetMonth.month.getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH);
    int year = budgetMonth.month.getYear();

    StringBuilder reportBuilder = new StringBuilder();
    reportBuilder.append(month + " " + year + "\n");
    if (budgetMonth.note != null && !budgetMonth.note.isEmpty()) {
      reportBuilder.append("Notes:\n");
      reportBuilder.append(budgetMonth.note + "\n");
    }
    reportBuilder.append("\n");

    reportBuilder.append(generators.stream()
        .map(generator -> generator.generate(budgetMonth))
        .collect(Collectors.joining("\n\n")));

    return reportBuilder.toString();
  }
}
