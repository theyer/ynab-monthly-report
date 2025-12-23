load("@rules_java//java:defs.bzl", "java_library")

package(default_visibility = ["//visibility:public"])

java_library(
    name = "model",
    srcs = glob(["*.java"]),
    deps = [
        "//src/main/java/com/ynabmonthlyreport/model/config",
        "//src/main/java/com/ynabmonthlyreport/model/month",
        "//src/main/java/com/ynabmonthlyreport/model/transaction",
        "@maven//:com_fasterxml_jackson_core_jackson_core",
        "@maven//:com_fasterxml_jackson_core_jackson_databind",
        "@maven//:com_fasterxml_jackson_datatype_jackson_datatype_jsr310",
    ],
)
