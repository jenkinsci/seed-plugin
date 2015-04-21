package net.nemerosa.seed.jenkins.pipeline

import net.nemerosa.seed.jenkins.support.SeedProjectEnvironment

class SeedPipelineGenerator extends AbstractPipelineGenerator {
    @Override
    void steps(def job, SeedProjectEnvironment env) {
        job.steps {
            dsl {
                removeAction 'DELETE'        // Jobs no longer in the pipeline definition are removed
                external 'seed/seed.groovy'  // seed.groovy provided by the branch itself
                ignoreExisting false         // Always update
            }
        }
    }
}
