package FarmEngine;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

public final class GameSettings {
    private static final Path SETTINGS_PATH = Path.of("saves/settings.properties");

    private static final IntegerProperty autosaveIntervalSeconds = new SimpleIntegerProperty(120);
    private static final DoubleProperty volume = new SimpleDoubleProperty(0.8);
    private static final StringProperty language = new SimpleStringProperty("FR");

    private GameSettings() {}

    public static void load() {
        try {
            Files.createDirectories(SETTINGS_PATH.getParent());
            if (!Files.exists(SETTINGS_PATH)) {
                save();
                return;
            }

            Properties p = new Properties();
            try (InputStream in = Files.newInputStream(SETTINGS_PATH)) {
                p.load(in);
            }
            autosaveIntervalSeconds.set(Integer.parseInt(p.getProperty("autosaveIntervalSeconds", "120")));
            volume.set(Double.parseDouble(p.getProperty("volume", "0.8")));
            language.set(p.getProperty("language", "FR"));
        } catch (Exception ignored) {
        }
    }

    public static void save() {
        try {
            Files.createDirectories(SETTINGS_PATH.getParent());
            Properties p = new Properties();
            p.setProperty("autosaveIntervalSeconds", String.valueOf(getAutosaveIntervalSeconds()));
            p.setProperty("volume", String.valueOf(getVolume()));
            p.setProperty("language", getLanguage());
            try (OutputStream out = Files.newOutputStream(SETTINGS_PATH)) {
                p.store(out, "PixFarm settings");
            }
        } catch (IOException ignored) {
        }
    }

    public static int getAutosaveIntervalSeconds() {
        return Math.max(30, autosaveIntervalSeconds.get());
    }

    public static void setAutosaveIntervalSeconds(int seconds) {
        autosaveIntervalSeconds.set(Math.max(30, seconds));
    }

    public static IntegerProperty autosaveIntervalSecondsProperty() {
        return autosaveIntervalSeconds;
    }

    public static double getVolume() {
        return Math.max(0, Math.min(1, volume.get()));
    }

    public static void setVolume(double value) {
        volume.set(Math.max(0, Math.min(1, value)));
    }

    public static DoubleProperty volumeProperty() {
        return volume;
    }

    public static String getLanguage() {
        return language.get();
    }

    public static void setLanguage(String value) {
        language.set((value == null || value.isBlank()) ? "FR" : value);
    }

    public static StringProperty languageProperty() {
        return language;
    }
}
