load("@rules_kotlin//kotlin:jvm.bzl", "kt_jvm_library")

package(default_visibility = ["//visibility:public"])

kt_jvm_library(
    name = "month",
    srcs = glob(["*.kt"]),
    deps = [
        "@maven//:com_fasterxml_jackson_core_jackson_annotations",
        "@maven//:tools_jackson_core_jackson_core",
        "@maven//:tools_jackson_core_jackson_databind",
    ],
)
