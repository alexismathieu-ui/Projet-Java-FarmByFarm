package FarmEngine;

import javafx.scene.image.Image;

import java.util.HashMap;
import java.util.Map;

public class ImageManager {
    private static Map<String, Image> cache = new HashMap<>();

    public static Image getImage(String path){
        if (!cache.containsKey(path)){
            Image img = new Image(ImageManager.class.getResourceAsStream("/" + path));
            cache.put(path, img);
        }
        return cache.get(path);
    }
}
