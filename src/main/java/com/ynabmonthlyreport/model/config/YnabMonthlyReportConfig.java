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
"enable_email_report",
"email_send_from_name",
"email_send_from_address",
"email_send_from_password",
"email_send_to_addresses",
"balance_zeroed_monthly_category_groups",
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
@JsonProperty("enable_email_report")
public boolean enableEmailReport;
@JsonProperty("email_send_from_name")
public String emailSendFromName;
@JsonProperty("email_send_from_address")
public String emailSendFromAddress;
@JsonProperty("email_send_from_password")
public String emailSendFromPassword;
@JsonProperty("email_send_to_addresses")
public List<String> emailSendToAddresses;
@JsonProperty("balance_zeroed_monthly_category_groups")
public List<String> balanceZeroedMonthlyCategoryGroups;
@JsonProperty("savings_categories")
public List<String> savingsCategories;
@JsonProperty("ignored_categories")
public List<String> ignoredCategories;

}