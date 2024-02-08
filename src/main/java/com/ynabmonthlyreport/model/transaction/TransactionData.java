package com.ynabmonthlyreport.model.transaction;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "id",
    "date",
    "amount",
    "memo",
    "cleared",
    "approved",
    "flag_color",
    "account_id",
    "payee_id",
    "category_id",
    "transfer_account_id",
    "transfer_transaction_id",
    "matched_transaction_id",
    "import_id",
    "import_payee_name",
    "import_payee_name_original",
    "debt_transaction_type",
    "deleted",
    "account_name",
    "payee_name",
    "category_name",
    "subtransactions"
})
@Generated("jsonschema2pojo")
public class TransactionData {

  @JsonProperty("id")
  public String id;
  @JsonProperty("date")
  public LocalDate date;
  @JsonProperty("amount")
  public long amount;
  @JsonProperty("memo")
  public String memo;

  public enum ClearedStatus {
    CLEARED,
    UNCLEARED,
    RECONCILED,
  }
  public ClearedStatus clearedStatus;
  @JsonProperty("cleared")
  private void formatCleared(String clearedString) {
    ArrayList<String> apiStatuses = new ArrayList<>(List.of("cleared", "uncleared", "reconciled"));
    this.clearedStatus = ClearedStatus.values()[apiStatuses.indexOf(clearedString)];
  }

  @JsonProperty("approved")
  public boolean approved;
  @JsonProperty("flag_color")
  public String flagColor;
  @JsonProperty("account_id")
  public String accountId;
  @JsonProperty("payee_id")
  public String payeeId;
  @JsonProperty("category_id")
  public String categoryId;
  @JsonProperty("transfer_account_id")
  public String transferAccountId;
  @JsonProperty("transfer_transaction_id")
  public String transferTransactionId;
  @JsonProperty("matched_transaction_id")
  public String matchedTransactionId;
  @JsonProperty("import_id")
  public String importId;
  @JsonProperty("import_payee_name")
  public String importPayeeName;
  @JsonProperty("import_payee_name_original")
  public String importPayeeNameOriginal;
  @JsonProperty("debt_transaction_type")
  public String debtTransactionType;
  @JsonProperty("deleted")
  public boolean deleted;
  @JsonProperty("account_name")
  public String accountName;
  @JsonProperty("payee_name")
  public String payeeName;
  @JsonProperty("category_name")
  public String categoryName;
  @JsonProperty("subtransactions")
  public List<SubtransactionData> subtransactions;

}