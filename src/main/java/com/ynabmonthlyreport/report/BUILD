load("@rules_kotlin//kotlin:jvm.bzl", "kt_jvm_library")

kt_jvm_library(
    name = "report",
    srcs = glob(["*.kt"]),
    visibility = ["//src/main/java/com/ynabmonthlyreport:__pkg__"],
    deps = [
        "//src/main/java/com/ynabmonthlyreport/model",
        "//src/main/java/com/ynabmonthlyreport/model/config",
        "//src/main/java/com/ynabmonthlyreport/model/month",
    ],
)
