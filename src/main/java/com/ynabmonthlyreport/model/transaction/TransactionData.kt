package com.ynabmonthlyreport.model.transaction

import com.fasterxml.jackson.annotation.JsonCreator
import java.time.LocalDate

data class TransactionData(
  val id: String,
  val date: LocalDate,
  val amount: Long,
  val memo: String?,
  val approved: Boolean,
  val cleared: ClearedStatus?,
  val flagColor: String?,
  val flagName: String?,
  val accountId: String?,
  val payeeId: String?,
  val categoryId: String?,
  val transferAccountId: String?,
  val transferTransactionId: String?,
  val matchedTransactionId: String?,
  val importId: String?,
  val importPayeeName: String?,
  val importPayeeNameOriginal: String?,
  val debtTransactionType: String?,
  val deleted: Boolean?,
  val accountName: String?,
  val payeeName: String?,
  val categoryName: String?,
  val subtransactions: List<SubtransactionData>,
) {
  enum class ClearedStatus(val value: String) {
    CLEARED("cleared"),
    UNCLEARED("uncleared"),
    RECONCILED("reconciled");

    companion object {
      @JsonCreator
      @JvmStatic
      fun fromString(value: String): ClearedStatus? = entries.find { it.value.equals(value, ignoreCase = true) }
    }
  }
}