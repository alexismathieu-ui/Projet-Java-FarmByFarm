package FarmEngine;

import java.util.HashMap;
import java.util.Map;

public final class I18n {
    private static final Map<String, String> FR = new HashMap<>();
    private static final Map<String, String> EN = new HashMap<>();

    static {
        FR.put("menu.continue.none", "Derniere partie : aucune");
        FR.put("menu.continue.last", "Derniere partie : Slot %d - %s");
        FR.put("main.money", "Argent : %d $");
        FR.put("main.level", "Niveau %d");
        FR.put("main.barn.open", "Grange");
        FR.put("main.barn.locked", "Grange (Niv. 5)");
        FR.put("main.weather", "Meteo : %s");
        FR.put("store.money", "Argent : %d $");
        FR.put("store.locked", "Verrouille Niv. %d");
        FR.put("store.buy", "Acheter (%d $)");
        FR.put("store.sell.dynamic", "Prix de vente actuel : %d $");
        FR.put("settings.title", "Parametres");
        FR.put("inventory.title", "Inventaire");
        FR.put("store.title", "Marche");
        FR.put("quest.title", "Tableau des quetes");
        FR.put("admin.title", "Panneau admin");

        EN.put("menu.continue.none", "Last game: none");
        EN.put("menu.continue.last", "Last game: Slot %d - %s");
        EN.put("main.money", "Money: %d $");
        EN.put("main.level", "Level %d");
        EN.put("main.barn.open", "Barn");
        EN.put("main.barn.locked", "Barn (Lvl. 5)");
        EN.put("main.weather", "Weather: %s");
        EN.put("store.money", "Money: %d $");
        EN.put("store.locked", "Locked Lv. %d");
        EN.put("store.buy", "Buy (%d $)");
        EN.put("store.sell.dynamic", "Current sell price: %d $");
        EN.put("settings.title", "Settings");
        EN.put("inventory.title", "Inventory");
        EN.put("store.title", "Store");
        EN.put("quest.title", "Quest Board");
        EN.put("admin.title", "Admin Panel");
    }

    private I18n() {}

    public static String tr(String key, Object... args) {
        Map<String, String> map = "EN".equalsIgnoreCase(GameSettings.getLanguage()) ? EN : FR;
        String pattern = map.getOrDefault(key, key);
        return String.format(pattern, args);
    }
}
