mapOf(
    "kotlinVersion" to "1.7.10",
    "coroutineVersion" to "1.6.4",
    "coreVersion" to "1.9.0",
    "appcompatVersion" to "1.4.1",
    "roomVersion" to "2.4.1",
    "materialVersion" to "1.3.0",
    "navigationVersion" to "2.3.4",
    "lifecycleVersion" to "2.4.0",
    "lifecycleExtensionsVersion" to "2.2.0",
    "coreTestingVersion" to "2.1.0",
    "constraintLayoutVersion" to "2.1.3",
    "recyclerViewVersion" to "1.2.1",

    "googleLocationVersion" to "17.0.0",
    "firebaseBomVersion" to "29.0.3",

    "leakCanaryVersion" to "2.9.1",

    "retrofitVersion" to "2.9.0",
    "gsonVersion" to "2.8.6",
    "timberVersion" to "4.7.1",
    "okhttpVersion" to "4.9.0",

    "jetbrainsAnnotations" to "16.0.1",

    "jUnitVersion" to "4.13.2",
    "testRunnerVersion" to "1.4.0",
    "espressoVersion" to "3.4.0",
    "mockitoVersion" to "3.10.0",
    "mockitoKotlinVersion" to "2.2.0",
    "supportAnnotationsVersion" to "28.0.0",
    "supportTestRunnerVersion" to "1.0.2",

    "slf4jVersion" to "1.7.25",
    "logbackVersion" to "1.1.1-12",

    "ktorVersion" to "1.6.7", //"2.0.0-beta-1",
    "ktorSerializationVersion" to "1.3.1",

    "sqlDelightVersion" to "1.5.3",
    "kotlinxDatetimeVersion" to "0.3.2"
).forEach { (name, version) ->
    project.extra.set(name, version)
}
