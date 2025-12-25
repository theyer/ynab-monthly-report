package com.ynabmonthlyreport.ynab;

import com.ynabmonthlyreport.model.JsonConversionUtils;
import com.ynabmonthlyreport.model.month.BudgetMonthData;
import com.ynabmonthlyreport.model.transaction.TransactionData;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class YnabFetcher {
  private static final String BUDGET_MONTH_URL_TEMPLATE =
    "https://api.youneedabudget.com/v1/budgets/%s/months/%s";
  private static final String TRANSACTIONS_URL_TEMPLATE =
    "https://api.youneedabudget.com/v1/budgets/%s/transactions?since_date=%s";

  // Cache data to ensure only a single API call is made (per data type).
  private BudgetMonthData cachedBudgetMonth = null;
  private List<TransactionData> cachedTransactions = null;

  private final String apiKey;
  private final String budgetId;
  private final LocalDate month;
  private final HttpClient client;

  public YnabFetcher(String apiKey, String budgetId, LocalDate month) {
    this.apiKey = apiKey;
    this.budgetId = budgetId;
    this.month = formatMonthForApi(month);
    this.client = HttpClient.newHttpClient();
  }

  public BudgetMonthData fetchBudgetMonthData() throws IOException, InterruptedException {
    if (cachedBudgetMonth != null) {
      return cachedBudgetMonth;
    }

    HttpRequest request = createRequest(String.format(BUDGET_MONTH_URL_TEMPLATE, budgetId, month));
    HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
    checkResponse(request, response);

    cachedBudgetMonth = JsonConversionUtils.convertBudgetMonthJson(response.body());
    return cachedBudgetMonth;
  }

  public List<TransactionData> fetchTransactionData() throws IOException, InterruptedException {
    if (cachedTransactions != null) {
      return cachedTransactions;
    }

    HttpRequest request = createRequest(String.format(TRANSACTIONS_URL_TEMPLATE, budgetId, month));
    HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
    checkResponse(request, response);

    cachedTransactions = JsonConversionUtils.convertTransactionsJson(response.body()).stream()
        .filter(transaction -> transaction.date.getMonth().equals(month.getMonth()))
        .collect(Collectors.toList());
    return cachedTransactions;
  }

  /** Transforms {@link LocalDate} to format yyyy-mm-01 for YNAB API. */
  private static LocalDate formatMonthForApi(LocalDate month) {
    return month.withDayOfMonth(1);
  }

  private HttpRequest createRequest(String url) {
    return HttpRequest.newBuilder(
        URI.create(url))
        .header("Authorization", String.format("Bearer %s", apiKey))
        .build();
  }

  private static void checkResponse(HttpRequest request, HttpResponse<String> response) {
    if (response.statusCode() != 200) {
      throw new RuntimeException(
          "YNAB API request failed with status code " + response.statusCode() + ". Request: " + request.toString());
    }
  }
}
