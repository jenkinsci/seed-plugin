package net.nemerosa.seed.jenkins.acceptance

import java.util.concurrent.TimeoutException

class Until {

    static def until(int seconds) {
        return new Scheduler(seconds)
    }

    static class Scheduler {

        final int timeout

        Scheduler(int seconds) {
            this.timeout = seconds
        }

        def every(int interval, Closure code) {
            int start = System.currentTimeMillis() / 1000
            int duration = 0
            while (duration < timeout) {
                def result = code(duration)
                if (result) {
                    return result
                }
                sleep interval * 1000
                duration = (System.currentTimeMillis() / 1000) - start
            }
            throw new TimeoutException()
        }
    }

}
