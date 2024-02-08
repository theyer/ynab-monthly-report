package com.ynabmonthlyreport.model.month;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.time.LocalDate;
import java.util.List;
import javax.annotation.Generated;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "month",
    "note",
    "income",
    "budgeted",
    "activity",
    "to_be_budgeted",
    "age_of_money",
    "deleted",
    "categories"
})
@Generated("jsonschema2pojo")
public class BudgetMonthData {

  @JsonProperty("month")
  public LocalDate month;
  @JsonProperty("note")
  public String note;
  @JsonProperty("income")
  public long income;
  @JsonProperty("budgeted")
  public long budgeted;
  @JsonProperty("activity")
  public long activity;
  @JsonProperty("to_be_budgeted")
  public long toBeBudgeted;
  @JsonProperty("age_of_money")
  public long ageOfMoney;
  @JsonProperty("deleted")
  public boolean deleted;
  @JsonProperty("categories")
  public List<CategoryData> categories;
}