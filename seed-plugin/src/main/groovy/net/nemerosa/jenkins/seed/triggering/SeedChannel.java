package net.nemerosa.jenkins.seed.triggering;

/**
 * Describes the source of an event
 */
public class SeedChannel {

    private final String id;
    private final String name;

    public SeedChannel(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public static SeedChannel of(String id, String name) {
        return new SeedChannel(id, name);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SeedChannel that = (SeedChannel) o;

        return id.equals(that.id) && name.equals(that.name);

    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + name.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "SeedChannel{" + "id='" + id + '\'' + ", name='" + name + '\'' + '}';
    }
}
