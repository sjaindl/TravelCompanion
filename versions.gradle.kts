mapOf(
    "kotlinVersion" to "1.7.21",
    "coroutineVersion" to "1.6.4",
    "coreVersion" to "1.9.0",
    "appcompatVersion" to "1.5.1",
    "roomVersion" to "2.4.3",
    "materialVersion" to "1.6.1",
    "navigationVersion" to "2.5.2",
    "lifecycleVersion" to "2.5.1",
    "lifecycleExtensionsVersion" to "2.2.0",
    "coreTestingVersion" to "2.1.0",
    "constraintLayoutVersion" to "2.1.4",
    "recyclerViewVersion" to "1.2.1",
    "picassoVersion" to "2.8",

    "googleLocationVersion" to "17.0.0",
    "firebaseBomVersion" to "29.0.3",

    "leakCanaryVersion" to "2.9.1",

    "retrofitVersion" to "2.9.0",
    "gsonVersion" to "2.9.1",
    "timberVersion" to "5.0.1",
    "okhttpVersion" to "4.10.0",
    "coilVersion" to "2.2.2",

    "jetbrainsAnnotations" to "16.0.1",

    "jUnitVersion" to "4.13.2",
    "testRunnerVersion" to "1.4.0",
    "espressoVersion" to "3.4.0",
    "mockitoVersion" to "4.8.0",
    "mockitoKotlinVersion" to "2.2.0",
    "supportAnnotationsVersion" to "28.0.0",
    "supportTestRunnerVersion" to "1.0.2",

    "slf4jVersion" to "1.7.30",
    "logbackVersion" to "2.0.0",

    "ktorVersion" to "2.1.3",
    "ktorSerializationVersion" to "1.3.1",
    "kotlinxSerializationVersion" to "1.4.1",

    "sqlDelightVersion" to "1.5.3",
    "kotlinxDatetimeVersion" to "0.4.0",
    "okioVersion" to "3.3.0",
    "resourcesGeneratorVersion" to "0.20.1",
    "kotlinxResources" to "0.2.5",

    "mapsVersion" to "18.1.0",
    "googleMapsUtilsVersion" to "2.4.0",

    "accompanistVersion" to "0.28.0",
    "composeNavigationVersion" to "2.5.3"
).forEach { (name, version) ->
    project.extra.set(name, version)
}
