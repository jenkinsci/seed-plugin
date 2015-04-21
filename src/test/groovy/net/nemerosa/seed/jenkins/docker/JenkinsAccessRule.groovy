package net.nemerosa.seed.jenkins.docker

import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement

class JenkinsAccessRule implements TestRule {

    @Override
    Statement apply(Statement base, Description description) {
        String jenkinsUrl = System.getProperty('jenkinsUrl')
        println """Running "${description.methodName}" against ${jenkinsUrl}"""
        // FIXME Method org.junit.rules.TestRule.apply
        return base
    }

}
