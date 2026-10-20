load("@rules_kotlin//kotlin:jvm.bzl", "kt_jvm_library")

kt_jvm_library(
    name = "report",
    srcs = glob(["*.kt"]),
    resources = ["//src/main/resources:resources"],
    resource_strip_prefix = "src/main/resources",
    visibility = ["//visibility:public"],
    deps = [
        "//src/main/java/com/ynabmonthlyreport/model",
        "//src/main/java/com/ynabmonthlyreport/model/config",
        "//src/main/java/com/ynabmonthlyreport/model/month",
        "//src/main/java/com/ynabmonthlyreport/model/transaction",
        "@maven//:io_pebbletemplates_pebble",
    ],
)
