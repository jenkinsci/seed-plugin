package net.nemerosa.seed.demo

class GeneralHelper {

    static void generalConfiguration(def jobDsl, String description) {
        jobDsl.description "${description}. This job has been generated."
    }

}
