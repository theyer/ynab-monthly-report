package com.ynabmonthlyreport.model.month

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonSetter
import com.fasterxml.jackson.annotation.Nulls
import tools.jackson.core.JsonParser
import tools.jackson.databind.BeanProperty
import tools.jackson.databind.DeserializationContext
import tools.jackson.databind.ValueDeserializer
import tools.jackson.databind.annotation.JsonDeserialize
import tools.jackson.databind.deser.std.StdDeserializer
import java.time.Instant
import java.time.LocalDate

data class CategoryData(
  val id: String?,
  val categoryGroupId: String?,
  val categoryGroupName: String?,
  val name: String?,
  val hidden: Boolean,
  val originalCategoryGroupId: String?,
  val note: String?,
  @JsonDeserialize(using = LongCurrencyDeserializer::class) val budgeted: Long,
  @JsonDeserialize(using = LongCurrencyDeserializer::class) @get:LongModifier(multiplier = -1L) val activity: Long,
  val balance: Long,
  @JsonSetter(nulls = Nulls.SKIP) val goalType: GoalType = GoalType.NO_GOAL,
  val goalNeedsWholeAmount: Boolean?,
  val goalDay: Long?,
  @JsonSetter(nulls = Nulls.SKIP) val goalCadence: GoalCadence = GoalCadence.NONE,
  val goalCadenceFrequency: Long?,
  val goalCreationMonth: LocalDate?,
  @JsonDeserialize(using = LongCurrencyDeserializer::class) val goalTarget: Long,
  val goalTargetMonth: LocalDate?,
  val goalPercentageComplete: Long?,
  val goalMonthsToBudget: Long?,
  val goalUnderFunded: Long?,
  val goalOverallFunded: Long?,
  val goalOverallLeft: Long?,
  val goalSnoozedAt: Instant?,
  val deleted: Boolean?,
) {
  enum class GoalType {
    NO_GOAL,
    TB,  // Target Category Balance
    TBD,  // Target Category Balance by Date
    MF,  // Monthly Funding
    NEED,  // Plan Your Spending
    DEBT;

    companion object {
      @JsonCreator
      @JvmStatic
      fun fromString(value: String?): GoalType = entries.find { it.name == value } ?: NO_GOAL
    }
  }

  // The goal_cadence field isn't always consistent w.r.t. monthly goals.
  // When goal_type is MF, it seems that the cadence for a monthly goal may be either MONTHLY or NONE.
  // When goal_type is NEED, monthly goals seem to always be MONTHLY.
  enum class GoalCadence {
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
    EVERY_2_YEARS;

    companion object {
      @JsonCreator
      @JvmStatic
      fun fromLong(value: Long?): GoalCadence = value?.let { entries[it.toInt()] } ?: NONE
    }
  }

  /**
   * Formats a currency [Long] where the last 3 digits are after the decimal.
   *
   * For example, 27990 represents $27.99 in the YNAB API. This deserializer rounds to the nearest whole number and
   * truncates the digits after the decimal, returning 28.
   *
   * @param multiplier an optional multiplier to apply after the rounding and truncation.
   */
  private class LongCurrencyDeserializer(private val multiplier: Long = 1L) : StdDeserializer<Long>(Long::class.java) {
    override fun createContextual(ctx: DeserializationContext, property: BeanProperty): ValueDeserializer<*> {
      val annotation = property.getAnnotation(LongModifier::class.java) ?: return this
      return LongCurrencyDeserializer(annotation.multiplier)
    }

    override fun deserialize(p: JsonParser, ctx: DeserializationContext): Long {
      val originalValue: Long = p.valueAsLong
      return Math.round((originalValue.toDouble()) / 1000.0) * multiplier
    }
  }

  /** Optionally annotates a field using [LongCurrencyDeserializer] to apply additional modifications. */
  @Retention(AnnotationRetention.RUNTIME)
  private annotation class LongModifier(val multiplier: Long)
}