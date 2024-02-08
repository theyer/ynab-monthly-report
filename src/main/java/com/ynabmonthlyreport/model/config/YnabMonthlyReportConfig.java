package com.ynabmonthlyreport.model.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.time.LocalDate;
import java.util.List;
import javax.annotation.Generated;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
"api_key",
"budget_id",
"budget_month",
"savings_categories",
"ignored_categories"
})
@Generated("jsonschema2pojo")
public class YnabMonthlyReportConfig {

@JsonProperty("api_key")
public String apiKey;
@JsonProperty("budget_id")
public String budgetId;
@JsonProperty("budget_month")
public LocalDate budgetMonth;
@JsonProperty("savings_categories")
public List<String> savingsCategories;
@JsonProperty("ignored_categories")
public List<String> ignoredCategories;

}