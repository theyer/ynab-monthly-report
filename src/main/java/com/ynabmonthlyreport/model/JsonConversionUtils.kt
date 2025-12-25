package com.ynabmonthlyreport.model

import com.ynabmonthlyreport.model.config.YnabMonthlyReportConfig
import com.ynabmonthlyreport.model.month.BudgetMonthData
import com.ynabmonthlyreport.model.transaction.TransactionData
import tools.jackson.core.JacksonException
import tools.jackson.databind.JsonNode
import tools.jackson.databind.PropertyNamingStrategies
import tools.jackson.databind.json.JsonMapper
import tools.jackson.module.kotlin.KotlinFeature
import tools.jackson.module.kotlin.jsonMapper
import tools.jackson.module.kotlin.kotlinModule
import tools.jackson.module.kotlin.readValue
import tools.jackson.module.kotlin.treeToValue

object JsonConversionUtils {
  fun convertBudgetMonthJson(json: String?): BudgetMonthData {
    try {
      val rootNode: JsonNode = MAPPER.readTree(json)
      val budgetMonthNode: JsonNode = rootNode.get("data").get("month")
      return MAPPER.treeToValue<BudgetMonthData>(budgetMonthNode)
    } catch (e: JacksonException) {
      throw IllegalArgumentException(e)
    }
  }

  fun convertTransactionsJson(json: String?): List<TransactionData> {
    try {
      val rootNode: JsonNode = MAPPER.readTree(json)
      val transactionsNode: JsonNode = rootNode.get("data").get("transactions")
      return MAPPER.treeToValue<List<TransactionData>>(transactionsNode)
    } catch (e: JacksonException) {
      throw IllegalArgumentException(e)
    }
  }

  fun convertConfigJson(json: String): YnabMonthlyReportConfig {
    return try {
      MAPPER.readValue<YnabMonthlyReportConfig>(json)
    } catch (e: JacksonException) {
      throw IllegalArgumentException(e)
    }
  }

  private val MAPPER: JsonMapper = jsonMapper {
    addModule(kotlinModule {
      enable(KotlinFeature.NullToEmptyCollection)
      enable(KotlinFeature.NullToEmptyMap)
    })
    propertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE)
  }
}
