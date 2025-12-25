load("@rules_kotlin//kotlin:jvm.bzl", "kt_jvm_library")

package(default_visibility = ["//visibility:public"])

kt_jvm_library(
    name = "transaction",
    srcs = glob(["*.kt"]),
    deps = [
        "@maven//:com_fasterxml_jackson_core_jackson_annotations",
    ],
)
