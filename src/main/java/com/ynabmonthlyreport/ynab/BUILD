load("@rules_java//java:defs.bzl", "java_library")

java_library(
    name = "ynab",
    srcs = glob(["*.java"]),
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
