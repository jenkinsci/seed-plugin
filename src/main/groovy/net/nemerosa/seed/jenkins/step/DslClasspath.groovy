package net.nemerosa.seed.jenkins.step

import hudson.remoting.Which

class DslClasspath {

    static URL classpathFor(Class<?> cls) {
        System.out.println(cls);
        URL url = Which.classFileUrl(cls);
        System.out.println(url);
        if ("file".equals(url.getProtocol())) {
            String suffix = cls.getName().replace(".", "/") + ".class";
            String path = url.toString() - suffix
            return new URL(path)
        } else {
            throw new IllegalArgumentException(
                    String.format(
                            "Class %s is loaded from %s but this is not a JAR or a file",
                            cls.getName(),
                            url
                    )
            );
        }
    }

}
