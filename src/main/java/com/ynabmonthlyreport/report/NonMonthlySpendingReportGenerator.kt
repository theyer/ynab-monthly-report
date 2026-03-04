package com.ynabmonthlyreport.report

import com.ynabmonthlyreport.model.Constants.FAILURE_ICON
import com.ynabmonthlyreport.model.Constants.SUCCESS_ICON
import com.ynabmonthlyreport.model.config.YnabMonthlyReportConfig
import com.ynabmonthlyreport.model.month.CategoryData
import kotlin.math.abs

internal object NonMonthlySpendingReportGenerator : BaseReportGenerator() {
  override val title = "------ Non-Monthly Spending Report ------"

  override fun generateSingleCategory(category: CategoryData): String {
    val details = getSpendingDetails(category)
    return String.format(
        "%s %s: %s (Target: $%d, Budgeted: $%d, Spent: $%d, Balance: $%d)",
        details.icon,
        category.name,
        details.spendingMessage,
        category.goalTarget,
        category.budgeted,
        category.activity,
        category.balance
      )
  }

  private fun getSpendingDetails(category: CategoryData): SpendingDetails {
    val isUnderfunded = (category.goalUnderFunded ?: 0L) > 0L
    if (isUnderfunded) {
      return Underfunded(category.goalUnderFunded ?: 0L)
    }

    var overspentAmount = 0L
    if (category.hasMonthlyTarget) {
      val netSpend = category.budgeted - category.balance
      if (netSpend > category.goalTarget) {
        overspentAmount = netSpend - category.goalTarget
      }
    } else {
      val overallFunded = category.goalOverallFunded ?: 0L
      if (overallFunded > category.goalTarget) {
        overspentAmount = overallFunded - category.goalTarget
      }
    }

    return if (overspentAmount > 0) Overspent(overspentAmount) else OnTrack
  }

  private sealed interface SpendingDetails {
    val icon: String
    val spendingMessage: String
  }

  private class Overspent(overspentAmount: Long) : SpendingDetails {
    override val icon = FAILURE_ICON
    override val spendingMessage = "$$overspentAmount overspent"
  }

  private class Underfunded(underfundedAmount: Long) : SpendingDetails {
    override val icon = FAILURE_ICON
    override val spendingMessage = "$$underfundedAmount underfunded"
  }

  private object OnTrack : SpendingDetails {
    override val icon = SUCCESS_ICON
    override val spendingMessage = "On track"
  }
}
