package io.github.coffeecatrailway.agameorsomething.common.io;

import io.github.coffeecatrailway.agameorsomething.registry.ObjectLocation;

import java.net.URL;

/**
 * @author CoffeeCatRailway
 * Created: 15/07/2022
 */
public final class ResourceLoader
{
    public static URL getResource(ObjectLocation location)
    {
        return getResource(location.getNamespace() + "/" + location.getPath());
    }

    public static URL getResource(String path)
    {
        URL url;
        // Try with the Thread Context Loader
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        if (classLoader != null)
        {
            url = classLoader.getResource(path);
            if (url != null)
                return url;
        }

        // Try with the class loader of loaded class
        classLoader = ResourceLoader.class.getClassLoader();
        if (classLoader != null)
        {
            url = classLoader.getResource(path);
            if (url != null)
                return url;
        }

        // Last ditch attempt. Get resource from the classpath
        return ClassLoader.getSystemResource(path);
    }
}
