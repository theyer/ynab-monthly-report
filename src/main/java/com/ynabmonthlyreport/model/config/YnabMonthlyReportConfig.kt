package com.ynabmonthlyreport.model.config

import java.time.LocalDate

data class YnabMonthlyReportConfig(
  val apiKey: String,
  val budgetId: String,
  val budgetMonth: LocalDate = LocalDate.now().minusMonths(1),
  val enableEmailReport: Boolean,
  val emailSendFromName: String,
  val emailSendFromAddress: String,
  val emailSendFromPassword: String,
  val emailSendToAddresses: List<String>,
  val monthlySpendingCategoryGroups: Set<String>,
  val nonMonthlySpendingCategoryGroups: Set<String>,
  val savingsCategoryGroups: Set<String>,
  val ignoredCategories: Set<String>,
)
