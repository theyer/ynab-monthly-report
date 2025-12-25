package com.ynabmonthlyreport.model.month

import java.time.LocalDate

data class BudgetMonthData(
  val month: LocalDate,
  val note: String?,
  val income: Long,
  val budgeted: Long,
  val activity: Long,
  val toBeBudgeted: Long,
  val ageOfMoney: Long,
  val deleted: Boolean?,
  val categories: List<CategoryData>,
)
