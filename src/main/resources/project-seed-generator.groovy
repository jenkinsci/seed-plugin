/**
 * Script to generate a project seed.
 *
 * Parameters are:
 *
 * - PROJECT - Name (identifier) of the project
 */

// TODO Use a SeedNamingStrategy, used by the SeedBrachingStrategies and ConfigurableBranchStrategy

folder(PROJECT) {
    // TODO Authorisations for the project, part of the project configuration
}

freeStyleJob("${PROJECT}/${PROJECT}-seed") {
    description "Project seed for ${PROJECT}"
}
