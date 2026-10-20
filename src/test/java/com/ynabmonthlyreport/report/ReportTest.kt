package com.ynabmonthlyreport.report

import com.ynabmonthlyreport.model.config.YnabMonthlyReportConfig
import com.ynabmonthlyreport.model.month.BudgetMonthData
import com.ynabmonthlyreport.model.month.CategoryData
import com.ynabmonthlyreport.model.transaction.SubtransactionData
import com.ynabmonthlyreport.model.transaction.TransactionData
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate

class ReportTest {

  private val testConfig = YnabMonthlyReportConfig(
    apiKey = "test-api-key",
    budgetId = "test-budget-id",
    budgetMonth = LocalDate.of(2026, 9, 1),
    enableEmailReport = false,
    emailSendFromName = "High Kings",
    emailSendFromAddress = "test@example.com",
    emailSendFromPassword = "password",
    emailSendToAddresses = listOf("test@example.com"),
    monthlySpendingCategoryGroups = setOf("Frequent Spending"),
    nonMonthlySpendingCategoryGroups = setOf("Sinking Funds"),
    savingsCategoryGroups = setOf("Savings"),
    ignoredCategories = setOf("Ignored Cat"),
  )

  @Test
  fun testTransactionAggregatorFiltersAndCapsAtMaxFive() {
    val catId = "cat-dining"
    val transactions = (1..8).map { i ->
      TransactionData(
        id = "tx-$i",
        date = LocalDate.of(2026, 9, i),
        amount = -10000L * i, // -$10, -$20, ..., -$80
        memo = "Dinner $i",
        approved = true,
        cleared = TransactionData.ClearedStatus.CLEARED,
        flagColor = null,
        flagName = null,
        accountId = "acc-1",
        payeeId = "payee-1",
        categoryId = catId,
        transferAccountId = null,
        transferTransactionId = null,
        matchedTransactionId = null,
        importId = null,
        importPayeeName = null,
        importPayeeNameOriginal = null,
        debtTransactionType = null,
        deleted = false,
        accountName = "Checking",
        payeeName = "Restaurant $i",
        categoryName = "Dining Out",
        subtransactions = emptyList(),
      )
    }

    val topTx = TransactionAggregator.getTopTransactions(catId, "Dining Out", transactions)

    assertEquals(5, topTx.size)
    // Largest first ($80)
    assertEquals("Restaurant 8", topTx[0].payee)
    assertEquals("$80", topTx[0].amountFormatted)
    assertEquals(80.0, topTx[0].amountDollars, 0.001)

    // Second largest ($70)
    assertEquals("Restaurant 7", topTx[1].payee)
  }

  @Test
  fun testTransactionAggregatorHandlesSubtransactions() {
    val splitTx = TransactionData(
      id = "tx-split",
      date = LocalDate.of(2026, 9, 15),
      amount = -150000L, // -$150 total
      memo = "Target run",
      approved = true,
      cleared = TransactionData.ClearedStatus.CLEARED,
      flagColor = null,
      flagName = null,
      accountId = "acc-1",
      payeeId = "payee-1",
      categoryId = null,
      transferAccountId = null,
      transferTransactionId = null,
      matchedTransactionId = null,
      importId = null,
      importPayeeName = null,
      importPayeeNameOriginal = null,
      debtTransactionType = null,
      deleted = false,
      accountName = "Credit Card",
      payeeName = "Target",
      categoryName = "Split",
      subtransactions = listOf(
        SubtransactionData(
          id = "sub-1",
          transactionId = "tx-split",
          amount = -45000L, // $45 Groceries
          memo = "Food",
          payeeId = null,
          payeeName = null,
          categoryId = "cat-groceries",
          categoryName = "Groceries",
          transferAccountId = null,
          transferTransactionId = null,
          deleted = false,
        ),
        SubtransactionData(
          id = "sub-2",
          transactionId = "tx-split",
          amount = -105000L, // $105 Clothing
          memo = "Shirts",
          payeeId = null,
          payeeName = null,
          categoryId = "cat-clothing",
          categoryName = "Clothing",
          transferAccountId = null,
          transferTransactionId = null,
          deleted = false,
        )
      ),
    )

    val groceryTx = TransactionAggregator.getTopTransactions("cat-groceries", "Groceries", listOf(splitTx))
    assertEquals(1, groceryTx.size)
    assertEquals("Target", groceryTx[0].payee)
    assertEquals("$45", groceryTx[0].amountFormatted)
    assertEquals("Food", groceryTx[0].memo)

    val clothingTx = TransactionAggregator.getTopTransactions("cat-clothing", "Clothing", listOf(splitTx))
    assertEquals(1, clothingTx.size)
    assertEquals("Target", clothingTx[0].payee)
    assertEquals("$105", clothingTx[0].amountFormatted)
    assertEquals("Shirts", clothingTx[0].memo)
  }

  @Test
  fun testReportViewModelBuilderAndHtmlRenderer() {
    val categories = listOf(
      // Monthly category - Over goal
      createCategory(
        id = "cat-1",
        group = "Frequent Spending",
        name = "Eating Out",
        goalTarget = 500L,
        activity = 863L,
        budgeted = 863L,
        balance = 0L,
      ),
      // Monthly category - Under goal
      createCategory(
        id = "cat-2",
        group = "Frequent Spending",
        name = "Groceries",
        goalTarget = 500L,
        activity = 242L,
        budgeted = 500L,
        balance = 0L,
      ),
      // Sinking fund - On track
      createCategory(
        id = "cat-3",
        group = "Sinking Funds",
        name = "Auto Insurance",
        goalTarget = 1000L,
        activity = 0L,
        budgeted = 167L,
        balance = 674L,
      ),
      // Sinking fund - Overspent
      createCategory(
        id = "cat-4",
        group = "Sinking Funds",
        name = "Medical",
        goalTarget = 150L,
        activity = 387L,
        budgeted = 390L,
        balance = 3L,
        goalType = "MF", // monthly target flag
      ),
      // Savings
      createCategory(
        id = "cat-5",
        group = "Savings",
        name = "Emergency Fund",
        goalTarget = 0L,
        activity = 0L,
        budgeted = 500L,
        balance = 55000L,
      ),
      // Misc with spending
      createCategory(
        id = "cat-6",
        group = "Other",
        name = "Home Repairs",
        goalTarget = 0L,
        activity = 3085L,
        budgeted = 3085L,
        balance = 0L,
      )
    )

    val budgetMonth = BudgetMonthData(
      month = LocalDate.of(2026, 9, 1),
      note = "September was a busy month!",
      income = 15000L,
      budgeted = 14000L,
      activity = -5000L,
      toBeBudgeted = 0L,
      ageOfMoney = 65L,
      deleted = false,
      categories = categories,
    )

    val renderer = HtmlReportRenderer(testConfig)
    val rendered = renderer.render(budgetMonth, emptyList())

    // 1. Verify ViewModel calculations
    val vm = rendered.viewModel
    assertEquals("September 2026", vm.monthTitle)
    assertEquals(2, vm.monthlyCategories.size)
    assertEquals(1, vm.attentionCategories.size)
    assertEquals("Eating Out", vm.attentionCategories[0].name)
    assertEquals(363L, vm.attentionCategories[0].diff)
    assertTrue(vm.attentionCategories[0].isOver)

    val groceries = vm.monthlyCategories.find { it.name == "Groceries" }!!
    assertFalse(groceries.isOver)
    assertEquals(258L, groceries.diff)

    assertEquals(1105L, vm.kpiSummary.totalSpent) // 863 + 242
    assertEquals(1000L, vm.kpiSummary.totalBudget) // 500 + 500
    assertEquals(105L, vm.kpiSummary.netVariance) // 1105 - 1000
    assertTrue(vm.kpiSummary.isNetOver)
    assertEquals(65L, vm.kpiSummary.ageOfMoney)

    // 2. Verify AMP HTML template
    val ampHtml = rendered.ampHtmlText
    assertTrue("AMP email must contain ⚡4email", ampHtml.contains("⚡4email"))
    assertTrue("AMP email must contain amp-accordion", ampHtml.contains("<amp-accordion"))
    assertTrue("AMP email should include Eating Out", ampHtml.contains("Eating Out"))
    assertTrue("AMP email should include Medical", ampHtml.contains("Medical"))
    assertTrue("AMP email should include Emergency Fund", ampHtml.contains("Emergency Fund"))
    assertTrue("AMP email should include Home Repairs", ampHtml.contains("Home Repairs"))
    assertTrue("AMP email should include note", ampHtml.contains("September was a busy month!"))

    // 3. Verify Standard HTML template
    val standardHtml = rendered.htmlText
    assertTrue("Standard HTML should contain details/summary", standardHtml.contains("<details>"))
    assertTrue("Standard HTML should include Eating Out", standardHtml.contains("Eating Out"))
    assertTrue("Standard HTML should include KPI values", standardHtml.contains("$1,105") || standardHtml.contains("$1105"))

    // Verify no accidental double dollar signs
    assertFalse("HTML should not contain double dollar signs", standardHtml.contains("$$"))
    assertFalse("AMP HTML should not contain double dollar signs", ampHtml.contains("$$"))

    // 4. Verify Plain Text template
    val plain = rendered.plainText
    assertTrue("Plain text should contain Eating Out", plain.contains("Eating Out"))
  }

  private fun createCategory(
    id: String,
    group: String,
    name: String,
    goalTarget: Long,
    activity: Long,
    budgeted: Long,
    balance: Long,
    goalType: String? = null,
  ): CategoryData {
    return CategoryData(
      id = id,
      categoryGroupId = "grp-$group",
      categoryGroupName = group,
      name = name,
      hidden = false,
      originalCategoryGroupId = null,
      note = null,
      goalType = goalType,
      budgeted = budgeted,
      activity = activity,
      balance = balance,
      goalNeedsWholeAmount = false,
      goalDay = null,
      goalCadence = CategoryData.GoalCadence.MONTHLY,
      goalCadenceFrequency = 1L,
      goalCreationMonth = LocalDate.of(2025, 1, 1),
      goalTarget = goalTarget,
      goalTargetMonth = null,
      goalPercentageComplete = null,
      goalMonthsToBudget = null,
      goalUnderFunded = null,
      goalOverallFunded = null,
      goalOverallLeft = null,
      goalSnoozedAt = null,
      deleted = false,
    )
  }
}
