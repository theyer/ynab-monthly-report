load("@rules_java//java:defs.bzl", "java_library")

java_library(
    name = "report",
    srcs = glob(["*.java"]),
    visibility = ["//src/main/java/com/ynabmonthlyreport:__pkg__"],
    deps = [
        "//src/main/java/com/ynabmonthlyreport/model",
        "//src/main/java/com/ynabmonthlyreport/model/config",
        "//src/main/java/com/ynabmonthlyreport/model/month",
    ],
)
