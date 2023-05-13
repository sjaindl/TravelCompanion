mapOf(
    "roomVersion" to "2.4.3",
).forEach { (name, version) ->
    project.extra.set(name, version)
}
