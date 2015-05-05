package net.nemerosa.seed.triggering.connector.github;

public class Commits {

    private boolean seed;
    private boolean other;

    public boolean isSeed() {
        return seed;
    }

    public boolean isOther() {
        return other;
    }

    public void feed(boolean seed) {
        this.seed = this.seed || seed;
        this.other = this.other || !seed;
    }

    public boolean isOnlySeed() {
        return seed && !other;
    }
}
