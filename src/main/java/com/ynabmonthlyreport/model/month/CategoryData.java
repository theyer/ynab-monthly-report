package com.ynabmonthlyreport.model.month;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.time.LocalDate;
import javax.annotation.Generated;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "id",
    "category_group_id",
    "category_group_name",
    "name",
    "hidden",
    "original_category_group_id",
    "note",
    "budgeted",
    "activity",
    "balance",
    "goal_type",
    "goal_day",
    "goal_cadence",
    "goal_cadence_frequency",
    "goal_creation_month",
    "goal_target",
    "goal_target_month",
    "goal_percentage_complete",
    "goal_months_to_budget",
    "goal_under_funded",
    "goal_overall_funded",
    "goal_overall_left",
    "deleted"
})
@Generated("jsonschema2pojo")
public class CategoryData {

  @JsonProperty("id")
  public String id;
  @JsonProperty("category_group_id")
  public String categoryGroupId;
  @JsonProperty("category_group_name")
  public String categoryGroupName;
  @JsonProperty("name")
  public String name;
  @JsonProperty("hidden")
  public boolean hidden;
  @JsonProperty("original_category_group_id")
  public String originalCategoryGroupId;
  @JsonProperty("note")
  public String note;
  
  public long budgeted;
  @JsonProperty("budgeted")
  private void formatBudgeted(long budgeted) {
    this.budgeted = formatLong(budgeted);
  }

  public long activity;
  @JsonProperty("activity")
  private void formatActivity(long activity) {
    this.activity = formatLong(activity) * -1;
  }

  @JsonProperty("balance")
  public long balance;

  public enum GoalType {
    NO_GOAL,
    TB, // Target Category Balance
    TBD, // Target Category Balance by Date
    MF, // Monthly Funding
    NEED, // Plan Your Spending
  }
  public GoalType goalType;
  @JsonProperty("goal_type")
  private void formatGoalType(String goalTypeStr) {
    this.goalType = goalTypeStr == null ? GoalType.NO_GOAL : GoalType.valueOf(goalTypeStr);
  }

  @JsonProperty("goal_day")
  public long goalDay;

  public enum GoalCadence {
    NONE,
    MONTHLY,
    WEEKLY,
    EVERY_2_MONTHS,
    EVERY_3_MONTHS,
    EVERY_4_MONTHS,
    EVERY_5_MONTHS,
    EVERY_6_MONTHS,
    EVERY_7_MONTHS,
    EVERY_8_MONTHS,
    EVERY_9_MONTHS,
    EVERY_10_MONTHS,
    EVERY_11_MONTHS,
    YEARLY,
    EVERY_2_YEARS,
  }
  // The goal_cadence field isn't always consistent w.r.t. monthly goals.
  // When goal_type is MF, it seems that the cadence for a monthly goal may be either MONTHLY or NONE.
  // When goal_type is NEED, monthly goals seem to always be MONTHLY.
  public GoalCadence goalCadence;
  @JsonProperty("goal_cadence")
  private void formatGoalCadence(long goalCadenceLong) {
    this.goalCadence = GoalCadence.values()[(int) goalCadenceLong];
  }

  @JsonProperty("goal_cadence_frequency")
  public long goalCadenceFrequency;
  @JsonProperty("goal_creation_month")
  public LocalDate goalCreationMonth;

  public long goalTarget;
  @JsonProperty("goal_target")
  private void formatGoalTarget(long goalTarget) {
    this.goalTarget = formatLong(goalTarget);
  }

  @JsonProperty("goal_target_month")
  public LocalDate goalTargetMonth;
  @JsonProperty("goal_percentage_complete")
  public long goalPercentageComplete;
  @JsonProperty("goal_months_to_budget")
  public long goalMonthsToBudget;
  @JsonProperty("goal_under_funded")
  public long goalUnderFunded;
  @JsonProperty("goal_overall_funded")
  public long goalOverallFunded;
  @JsonProperty("goal_overall_left")
  public long goalOverallLeft;
  @JsonProperty("deleted")
  public boolean deleted;

  private static long formatLong(long input) {
    return Math.round(((double)input) / 1000.0);
  }
}