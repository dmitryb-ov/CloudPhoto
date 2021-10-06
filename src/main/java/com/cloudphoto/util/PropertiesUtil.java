package com.cloudphoto.util;

import java.io.*;
import java.util.Properties;

public class PropertiesUtil {
    private static final String PATH = "application.properties";

    private PropertiesUtil() {
        throw new IllegalStateException("Utility class");
    }

    public static String getBucket() {
        return getProperties("bucket");
    }

    public static String getRegion() {
        return getProperties("region");
    }

    public static String getEndpoint(){
        return getProperties("endpoint");
    }

    private static String getProperties(String key) {
        try (OutputStream os = new FileOutputStream(PATH)) {
            var prop = new Properties();
            return prop.getProperty(key);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
