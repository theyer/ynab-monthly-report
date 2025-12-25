load("@rules_kotlin//kotlin:jvm.bzl", "kt_jvm_library")

kt_jvm_library(
    name = "scheduler",
    srcs = glob(["*.kt"]),
    visibility = ["//src/main/java/com/ynabmonthlyreport:__pkg__"],
    deps = [
        "//src/main/java/com/ynabmonthlyreport/model",
        "//src/main/java/com/ynabmonthlyreport/model/config",
        "//src/main/java/com/ynabmonthlyreport/model/month",
        "//src/main/java/com/ynabmonthlyreport/model/transaction",
        "//src/main/java/com/ynabmonthlyreport/ynab",
    ],
)
