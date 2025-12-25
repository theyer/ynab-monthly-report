load("@rules_kotlin//kotlin:jvm.bzl", "kt_jvm_library")

package(default_visibility = ["//visibility:public"])

kt_jvm_library(
    name = "model",
    srcs = glob(["*.kt"]),
    deps = [
        "//src/main/java/com/ynabmonthlyreport/model/config",
        "//src/main/java/com/ynabmonthlyreport/model/month",
        "//src/main/java/com/ynabmonthlyreport/model/transaction",
        "@maven//:tools_jackson_core_jackson_core",
        "@maven//:tools_jackson_core_jackson_databind",
        "@maven//:tools_jackson_module_jackson_module_kotlin",
    ],
)
