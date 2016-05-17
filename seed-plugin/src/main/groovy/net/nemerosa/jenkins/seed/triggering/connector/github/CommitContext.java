package net.nemerosa.jenkins.seed.triggering.connector.github;

public class CommitContext {

    private boolean seed;

    public boolean isSeed() {
        return seed;
    }

    public void feed(boolean seed) {
        this.seed = this.seed || seed;
    }

}
