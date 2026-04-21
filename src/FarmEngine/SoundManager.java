package FarmEngine;

import javafx.scene.media.AudioClip;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public final class SoundManager {
    private static final Map<String, AudioClip> CLIPS = new HashMap<>();
    private static MediaPlayer musicPlayer;

    private SoundManager() {}

    public static void playMusic(String resourcePath) {
        try {
            URL url = SoundManager.class.getResource(resourcePath);
            if (url == null) return;
            if (musicPlayer != null) {
                musicPlayer.stop();
            }
            Media media = new Media(url.toExternalForm());
            musicPlayer = new MediaPlayer(media);
            musicPlayer.setCycleCount(MediaPlayer.INDEFINITE);
            musicPlayer.setVolume(GameSettings.getVolume());
            musicPlayer.play();
        } catch (Exception ignored) {
        }
    }

    public static void stopMusic() {
        if (musicPlayer != null) {
            musicPlayer.stop();
        }
    }

    public static void updateVolume() {
        if (musicPlayer != null) {
            musicPlayer.setVolume(GameSettings.getVolume());
        }
    }

    public static void playSfx(String resourcePath) {
        try {
            AudioClip clip = CLIPS.computeIfAbsent(resourcePath, key -> {
                URL url = SoundManager.class.getResource(key);
                return url != null ? new AudioClip(url.toExternalForm()) : null;
            });
            if (clip == null) return;
            clip.play(GameSettings.getVolume());
        } catch (Exception ignored) {
        }
    }
}
