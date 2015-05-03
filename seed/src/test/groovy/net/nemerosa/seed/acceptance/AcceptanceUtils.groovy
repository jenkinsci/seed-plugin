package net.nemerosa.seed.acceptance

import org.junit.Assert

class AcceptanceUtils {

    static def failOn(Closure code) {
        return [
                withMessage: { Closure<String> message ->
                    try {
                        code()
                        Assert.fail 'Should have failed'
                    } catch (Exception ex) {
                        assert ex.message == message()
                    }
                }
        ]
    }

}
