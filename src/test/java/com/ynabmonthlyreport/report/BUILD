load("@rules_kotlin//kotlin:jvm.bzl", "kt_jvm_test")

kt_jvm_test(
    name = "ReportTest",
    srcs = ["ReportTest.kt"],
    test_class = "com.ynabmonthlyreport.report.ReportTest",
    deps = [
        "//src/main/java/com/ynabmonthlyreport/model",
        "//src/main/java/com/ynabmonthlyreport/model/config",
        "//src/main/java/com/ynabmonthlyreport/model/month",
        "//src/main/java/com/ynabmonthlyreport/model/transaction",
        "//src/main/java/com/ynabmonthlyreport/report",
        "@maven//:junit_junit",
    ],
)
