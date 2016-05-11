package net.nemerosa.jenkins.seed.test

import java.text.SimpleDateFormat
import java.util.concurrent.atomic.AtomicLong

class TestUtils {

    private static final AtomicLong counter = new AtomicLong()

    static String uid(String prefix) {
        prefix + new SimpleDateFormat('mmssSSS').format(new Date()) + counter.incrementAndGet()
    }

}
