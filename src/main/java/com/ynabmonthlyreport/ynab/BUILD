load("@rules_kotlin//kotlin:jvm.bzl", "kt_jvm_library")

kt_jvm_library(
    name = "ynab",
    srcs = glob(["*.kt"]),
    visibility = [
        "//src/main/java/com/ynabmonthlyreport:__pkg__",
        "//src/main/java/com/ynabmonthlyreport/scheduler:__pkg__",
    ],
    deps = [
        "//src/main/java/com/ynabmonthlyreport/model",
        "//src/main/java/com/ynabmonthlyreport/model/month",
        "//src/main/java/com/ynabmonthlyreport/model/transaction",
    ],
)
