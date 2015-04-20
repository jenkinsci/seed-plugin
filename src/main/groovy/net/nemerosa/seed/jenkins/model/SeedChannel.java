package net.nemerosa.seed.jenkins.model;

/**
 * Describes the source of an event
 */
public class SeedChannel {

    private final String name;

    public SeedChannel(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static SeedChannel of(String name) {
        return new SeedChannel(name);
    }

    @Override
    public String toString() {
        return "SeedChannel{" + "name='" + name + '\'' + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SeedChannel that = (SeedChannel) o;

        return name.equals(that.name);

    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }
}
