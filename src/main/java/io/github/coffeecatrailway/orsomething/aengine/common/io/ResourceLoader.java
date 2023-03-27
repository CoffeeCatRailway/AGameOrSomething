package io.github.coffeecatrailway.orsomething.aengine.common.io;

import com.mojang.logging.LogUtils;
import io.github.coffeecatrailway.orsomething.aengine.ObjectLocation;
import org.slf4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

/**
 * @author CoffeeCatRailway
 * Created: 15/07/2022
 */
public final class ResourceLoader
{
    private static final Logger LOGGER = LogUtils.getLogger();

    private ResourceLoader()
    {}

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

    public static String readToString(ObjectLocation location)
    {
        StringBuilder string = new StringBuilder();
        try
        {
            BufferedReader br = new BufferedReader(new InputStreamReader(ResourceLoader.getResource(location).openStream()));
            String line;
            while ((line = br.readLine()) != null)
            {
                string.append(line);
                string.append("\n");
            }
            br.close();
        } catch (IOException e)
        {
            LOGGER.error("Something went wrong reading file {}!", location, e);
            e.printStackTrace();
        }
        return string.toString();
    }
}
