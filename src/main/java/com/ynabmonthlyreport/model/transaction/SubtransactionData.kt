package com.ynabmonthlyreport.model.transaction;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import javax.annotation.Generated;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "id",
    "transaction_id",
    "amount",
    "memo",
    "payee_id",
    "payee_name",
    "category_id",
    "category_name",
    "transfer_account_id",
    "transfer_transaction_id",
    "deleted"
})
@Generated("jsonschema2pojo")
public class SubtransactionData {

  @JsonProperty("id")
  public String id;
  @JsonProperty("transaction_id")
  public String transactionId;
  @JsonProperty("amount")
  public long amount;
  @JsonProperty("memo")
  public String memo;
  @JsonProperty("payee_id")
  public String payeeId;
  @JsonProperty("payee_name")
  public String payeeName;
  @JsonProperty("category_id")
  public String categoryId;
  @JsonProperty("category_name")
  public String categoryName;
  @JsonProperty("transfer_account_id")
  public String transferAccountId;
  @JsonProperty("transfer_transaction_id")
  public String transferTransactionId;
  @JsonProperty("deleted")
  public boolean deleted;

}