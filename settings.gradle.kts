rootProject.name = "nexus-pim"

include(
    "libs:shared-kernel",
    "libs:event-contracts",

    "services:catalog",
    "services:attribute",
    "services:taxonomy",
    "services:content",
    "services:media",
    "services:workflow",
    "services:search",
)
