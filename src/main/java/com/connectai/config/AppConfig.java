package com.connectai.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

/**
 * Manages application configuration properties loaded from application.properties or environment variables.
 */
public class AppConfig {
    private static final Properties properties = new Properties();
    private static boolean loaded = false;

    public static synchronized void load() {
        if (loaded) return;

        // 1. Try loading from local application.properties file in working directory
        File localFile = new File("application.properties");
        if (localFile.exists()) {
            try (InputStream is = new FileInputStream(localFile)) {
                properties.load(is);
            } catch (Exception e) {
                System.err.println("Failed to load application.properties: " + e.getMessage());
            }
        }

        // 2. Fallback/merge classpath application.properties
        try (InputStream is = AppConfig.class.getClassLoader().getResourceAsStream("application.properties")) {
            if (is != null) {
                Properties classpathProps = new Properties();
                classpathProps.load(is);
                for (String name : classpathProps.stringPropertyNames()) {
                    if (!properties.containsKey(name)) {
                        properties.put(name, classpathProps.getProperty(name));
                    }
                }
            }
        } catch (Exception ignored) {}

        // 3. Override with environment variables if present
        String envUrl = System.getenv("SUPABASE_URL");
        if (envUrl != null && !envUrl.isBlank()) {
            properties.setProperty("supabase.url", envUrl);
        }

        String envKey = System.getenv("SUPABASE_ANON_KEY");
        if (envKey != null && !envKey.isBlank()) {
            properties.setProperty("supabase.anon.key", envKey);
        }

        loaded = true;
    }

    private static Boolean mockModeOverride = null;

    public static boolean isMockMode() {
        load();
        if (mockModeOverride != null) return mockModeOverride;
        String prop = properties.getProperty("app.mock_mode", "");
        if ("true".equalsIgnoreCase(prop.trim())) return true;
        if ("false".equalsIgnoreCase(prop.trim())) return false;

        // Auto-enable mock mode if Supabase URL is placeholder or default
        String url = getSupabaseUrl();
        return url.isBlank() || url.contains("xyzcompany.supabase.co") || getSupabaseAnonKey().isBlank();
    }

    public static void setMockMode(boolean mockMode) {
        mockModeOverride = mockMode;
        properties.setProperty("app.mock_mode", String.valueOf(mockMode));
    }

    public static String getSupabaseUrl() {
        load();
        return properties.getProperty("supabase.url", "https://xyzcompany.supabase.co").trim();
    }

    public static String getSupabaseAnonKey() {
        load();
        return properties.getProperty("supabase.anon.key", "").trim();
    }

    public static String getAppName() {
        load();
        return properties.getProperty("app.name", "ConnectAI");
    }

    public static String getAppVersion() {
        load();
        return properties.getProperty("app.version", "1.0.0");
    }
}
