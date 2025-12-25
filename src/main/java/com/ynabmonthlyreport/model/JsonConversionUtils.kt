package com.ynabmonthlyreport.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.ynabmonthlyreport.model.config.YnabMonthlyReportConfig;
import com.ynabmonthlyreport.model.month.BudgetMonthData;
import com.ynabmonthlyreport.model.transaction.TransactionData;
import java.util.Arrays;
import java.util.List;

public class JsonConversionUtils {

  public static BudgetMonthData convertBudgetMonthJson(String json) {
    ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    try {
      JsonNode rootNode = objectMapper.readTree(json);
      JsonNode budgetMonthNode = rootNode.get("data").get("month");
      return objectMapper.treeToValue(budgetMonthNode, BudgetMonthData.class);
    } catch (JsonProcessingException e) {
      throw new IllegalArgumentException(e);
    }
  }

  public static List<TransactionData> convertTransactionsJson(String json) {
    ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    try {
      JsonNode rootNode = objectMapper.readTree(json);
      JsonNode transactionsNode = rootNode.get("data").get("transactions");
      return Arrays.asList(objectMapper.treeToValue(transactionsNode, TransactionData[].class));
    } catch (JsonProcessingException e) {
      throw new IllegalArgumentException(e);
    }
  }

  public static YnabMonthlyReportConfig convertConfigJson(String json) {
    ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    try {
      return objectMapper.readValue(json, YnabMonthlyReportConfig.class);
    } catch (JsonProcessingException e) {
      throw new IllegalArgumentException(e);
    }
  }
}
