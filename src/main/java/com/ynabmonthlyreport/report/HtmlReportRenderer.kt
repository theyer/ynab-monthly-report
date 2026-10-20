package com.ynabmonthlyreport.report

import com.ynabmonthlyreport.model.config.YnabMonthlyReportConfig
import com.ynabmonthlyreport.model.month.BudgetMonthData
import com.ynabmonthlyreport.model.transaction.TransactionData
import io.pebbletemplates.pebble.PebbleEngine
import io.pebbletemplates.pebble.loader.ClasspathLoader
import java.io.StringWriter

data class RenderedReport(
  val plainText: String,
  val htmlText: String,
  val ampHtmlText: String,
  val viewModel: ReportViewModel,
)

class HtmlReportRenderer(private val config: YnabMonthlyReportConfig) {
  private val engine: PebbleEngine = PebbleEngine.Builder()
    .loader(ClasspathLoader())
    .autoEscaping(true)
    .build()

  private val reportAssembler = ReportAssembler(config)

  fun render(
    budgetMonth: BudgetMonthData,
    transactions: List<TransactionData>,
  ): RenderedReport {
    val viewModel = ReportViewModelBuilder.build(budgetMonth, transactions, config)
    val context = mapOf("model" to viewModel)

    val htmlWriter = StringWriter()
    val htmlTemplate = engine.getTemplate("templates/email_report.html")
    htmlTemplate.evaluate(htmlWriter, context)
    val htmlText = htmlWriter.toString()

    val ampWriter = StringWriter()
    val ampTemplate = engine.getTemplate("templates/email_report_amp.html")
    ampTemplate.evaluate(ampWriter, context)
    val ampHtmlText = ampWriter.toString()

    val plainText = reportAssembler.getAssembledReport(budgetMonth)

    return RenderedReport(
      plainText = plainText,
      htmlText = htmlText,
      ampHtmlText = ampHtmlText,
      viewModel = viewModel,
    )
  }
}
