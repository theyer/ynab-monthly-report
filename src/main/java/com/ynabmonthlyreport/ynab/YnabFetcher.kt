package com.ynabmonthlyreport.ynab

import com.ynabmonthlyreport.model.JsonConversionUtils.convertBudgetMonthJson
import com.ynabmonthlyreport.model.JsonConversionUtils.convertTransactionsJson
import com.ynabmonthlyreport.model.config.YnabMonthlyReportConfig
import com.ynabmonthlyreport.model.month.BudgetMonthData
import com.ynabmonthlyreport.model.transaction.TransactionData
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.net.http.HttpResponse.BodyHandlers
import java.time.LocalDate

class YnabFetcher(config: YnabMonthlyReportConfig) {
  private val apiKey: String = config.apiKey
  private val budgetId: String = config.budgetId
  private val month: LocalDate = formatMonthForApi(config.budgetMonth)
  private val client: HttpClient = HttpClient.newHttpClient()

  // Cache data to ensure only a single API call is made (per data type).
  private val cachedBudgetMonth: BudgetMonthData by lazy {
    val request = createRequest(String.format(BUDGET_MONTH_URL_TEMPLATE, budgetId, month))
    val response = client.send(request, BodyHandlers.ofString())
    checkResponse(request, response)
    convertBudgetMonthJson(response.body())
  }
  private val cachedTransactions: List<TransactionData> by lazy {
    val request = createRequest(String.format(TRANSACTIONS_URL_TEMPLATE, budgetId, month))
    val response = client.send(request, BodyHandlers.ofString())
    checkResponse(request, response)
    convertTransactionsJson(response.body()).filter { it.date.month == month.month }
  }

  fun fetchBudgetMonthData(): BudgetMonthData {
    return cachedBudgetMonth
  }

  fun fetchTransactionData(): List<TransactionData> {
    return cachedTransactions
  }

  private fun createRequest(url: String): HttpRequest {
    return HttpRequest.newBuilder(URI.create(url)).header("Authorization", "Bearer $apiKey").build()
  }

  companion object {
    private const val BUDGET_MONTH_URL_TEMPLATE = "https://api.youneedabudget.com/v1/budgets/%s/months/%s"
    private const val TRANSACTIONS_URL_TEMPLATE =
      "https://api.youneedabudget.com/v1/budgets/%s/transactions?since_date=%s"

    /** Transforms [LocalDate] to format yyyy-mm-01 for YNAB API.  */
    private fun formatMonthForApi(month: LocalDate): LocalDate {
      return month.withDayOfMonth(1)
    }

    private fun checkResponse(request: HttpRequest, response: HttpResponse<String?>) {
      if (response.statusCode() != 200) {
        throw RuntimeException("YNAB API request failed with status code ${response.statusCode()}. Request: $request")
      }
    }
  }
}
