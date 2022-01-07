rootProject.name = "Stock Price"
include(
  ":app",
  ":navigation",

  ":core",
  ":core:ui",
  ":core:data",
  ":core:domain",

  ":features:about:data",
  ":features:about:domain",
  ":features:about:ui",
  ":features:stocks:data",
  ":features:stocks:domain",
  ":features:stocks:ui",
  ":features:splash:ui",
  ":features:authentication:data",
  ":features:authentication:domain",
  ":features:authentication:ui",
  ":features:settings:data",
  ":features:settings:domain",
  ":features:settings:ui",
  ":features:search:ui",
  ":features:search:domain",
  ":features:search:data",
)