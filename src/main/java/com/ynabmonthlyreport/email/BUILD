load("@rules_kotlin//kotlin:jvm.bzl", "kt_jvm_library")

kt_jvm_library(
    name = "email",
    srcs = glob(["*.kt"]),
    visibility = ["//src/main/java/com/ynabmonthlyreport:__pkg__"],
    deps = [
        "//src/main/java/com/ynabmonthlyreport/model/config",
        "@maven//:com_sun_mail_javax_mail",
    ],
)
