load("@rules_java//java:defs.bzl", "java_library")

java_library(
    name = "email",
    srcs = glob(["*.java"]),
    deps = [
        "//src/main/java/com/ynabmonthlyreport/model/config",
        "@maven//:com_sun_mail_javax_mail",
    ],
    visibility = ["//src/main/java/com/ynabmonthlyreport:__pkg__"],
)