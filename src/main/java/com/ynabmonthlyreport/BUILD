load("@rules_kotlin//kotlin:jvm.bzl", "kt_jvm_binary", "kt_jvm_library")

kt_jvm_library(
    name = "runner_lib",
    srcs = ["Runner.kt"],
    data = ["//src/main/resources"],
    deps = [
        "//src/main/java/com/ynabmonthlyreport/email",
        "//src/main/java/com/ynabmonthlyreport/model",
        "//src/main/java/com/ynabmonthlyreport/model/config",
        "//src/main/java/com/ynabmonthlyreport/model/month",
        "//src/main/java/com/ynabmonthlyreport/report",
        "//src/main/java/com/ynabmonthlyreport/scheduler",
        "//src/main/java/com/ynabmonthlyreport/ynab",
    ],
)

kt_jvm_binary(
    name = "runner",
    main_class = "com.ynabmonthlyreport.Runner",
    runtime_deps = [":runner_lib"],
)
