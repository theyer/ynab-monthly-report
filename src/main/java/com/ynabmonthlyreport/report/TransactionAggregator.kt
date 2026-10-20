package com.ynabmonthlyreport.report

import com.ynabmonthlyreport.model.transaction.TransactionData
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlin.math.abs

object TransactionAggregator {
  private val DATE_FORMATTER: DateTimeFormatter = DateTimeFormatter.ofPattern("MMM d", Locale.ENGLISH)
  private const val MAX_TRANSACTIONS = 5

  /**
   * Internal representation of an expense extracted from a parent transaction or subtransaction.
   */
  private data class ExtractedExpense(
    val categoryId: String?,
    val categoryName: String?,
    val date: java.time.LocalDate,
    val payee: String,
    val memo: String?,
    val amountMilliunits: Long,
  )

  /**
   * Finds the top expenses (up to [MAX_TRANSACTIONS]) for a category.
   * Only expenses (outflows where amount < 0) are considered.
   */
  fun getTopTransactions(
    categoryId: String?,
    categoryName: String?,
    transactions: List<TransactionData>,
  ): List<CategoryTransactionView> {
    val expenses = mutableListOf<ExtractedExpense>()

    for (tx in transactions) {
      if (tx.subtransactions.isNotEmpty()) {
        for (sub in tx.subtransactions) {
          if (matchesCategory(sub.categoryId, sub.categoryName, categoryId, categoryName)) {
            if (sub.amount < 0) {
              val payee = sub.payeeName ?: tx.payeeName ?: "Unknown"
              expenses.add(
                ExtractedExpense(
                  categoryId = sub.categoryId,
                  categoryName = sub.categoryName,
                  date = tx.date,
                  payee = payee,
                  memo = sub.memo ?: tx.memo,
                  amountMilliunits = sub.amount,
                )
              )
            }
          }
        }
      } else {
        if (matchesCategory(tx.categoryId, tx.categoryName, categoryId, categoryName)) {
          if (tx.amount < 0) {
            val payee = tx.payeeName ?: "Unknown"
            expenses.add(
              ExtractedExpense(
                categoryId = tx.categoryId,
                categoryName = tx.categoryName,
                date = tx.date,
                payee = payee,
                memo = tx.memo,
                amountMilliunits = tx.amount,
              )
            )
          }
        }
      }
    }

    return expenses
      .sortedByDescending { abs(it.amountMilliunits) }
      .take(MAX_TRANSACTIONS)
      .map { expense ->
        val dollars = abs(expense.amountMilliunits) / 1000.0
        val formattedAmount = if (dollars % 1.0 == 0.0) {
          String.format(Locale.US, "$%,.0f", dollars)
        } else {
          String.format(Locale.US, "$%,.2f", dollars)
        }
        CategoryTransactionView(
          date = expense.date.format(DATE_FORMATTER),
          payee = expense.payee,
          memo = expense.memo,
          amountFormatted = formattedAmount,
          amountDollars = dollars,
        )
      }
  }

  private fun matchesCategory(
    itemCatId: String?,
    itemCatName: String?,
    targetCatId: String?,
    targetCatName: String?,
  ): Boolean {
    if (targetCatId != null && itemCatId != null && targetCatId == itemCatId) {
      return true
    }
    if (targetCatName != null && itemCatName != null && targetCatName.equals(itemCatName, ignoreCase = true)) {
      return true
    }
    return false
  }
}
