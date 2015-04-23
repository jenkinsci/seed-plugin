package net.nemerosa.seed.jenkins.step;

public final class CombinedClassLoader extends ClassLoader {

    private final static class ClassLoaderWrapper extends ClassLoader {
        ClassLoaderWrapper(ClassLoader cl) {
            super(cl);
        }

        @Override
        protected Class<?> loadClass(String name, boolean resolve)
                throws ClassNotFoundException {
            return super.loadClass(name, resolve);
        }
    }

    private final ClassLoaderWrapper defaultCL;

    public CombinedClassLoader(ClassLoader parent, ClassLoader defaultCL) {
        super(parent);
        this.defaultCL = new ClassLoaderWrapper(defaultCL);
    }

    @Override
    protected Class<?> loadClass(String name, boolean resolve)
            throws ClassNotFoundException {
        try {
            return super.loadClass(name, resolve);
        } catch (Exception e) {
            for (Throwable t = e; t != null; t = t.getCause()) {
                if (t instanceof SecurityException) {
                    throw t == e ? (SecurityException) t : new SecurityException(t.getMessage(), e);
                }
            }
        }
        return defaultCL.loadClass(name, resolve);
    }

}