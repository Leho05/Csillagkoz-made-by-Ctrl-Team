package org.csillagkoz.visual;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.text.Normalizer;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

/** Classpath-only, lazy, shared image cache and extensible name mapping. */
public final class SpaceAssets {
    private static final String ROOT = "/org/csillagkoz/visual/";
    private final Properties mapping = new Properties();
    private final Map<String, Image> cache = new HashMap<>();
    private final Set<String> absent = new HashSet<>();

    public SpaceAssets() {
        try (InputStream in = SpaceAssets.class.getResourceAsStream(ROOT + "assets.properties")) {
            if (in != null) mapping.load(new InputStreamReader(in, StandardCharsets.UTF_8));
        } catch (java.io.IOException e) {
            System.err.println("A grafikai hozzárendelések nem olvashatók; az alapértelmezések maradnak.");
        }
    }

    public Image image(String key) {
        String path = mapping.getProperty(key);
        if (path == null || absent.contains(path)) return null;
        if (cache.containsKey(path)) return cache.get(path);
        java.net.URL url = SpaceAssets.class.getResource(ROOT + path);
        if (url == null) {
            absent.add(path);
            return null;
        }
        double size = key.equals("background") ? 3840 : 640;
        Image image = new Image(url.toExternalForm(), size, size, true, true, false);
        if (image.isError()) {
            absent.add(path);
            return null;
        }
        cache.put(path, image);
        return image;
    }

    public Image destination(String name) {
        Image found = image("destination." + normalize(name));
        return found == null ? image("unknown") : found;
    }

    public Image passenger(String value) {
        return image(passengerKey(value));
    }

    public static String passengerKey(String value) {
        String normalized = normalize(value);
        if (normalized.contains("gyermek") || normalized.contains("diak")) return "alien.young";
        if (normalized.contains("nyugdijas")) return "alien.senior";
        return "alien.adult";
    }

    public static String normalize(String value) {
        if (value == null) return "";
        return Normalizer.normalize(value, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "").toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9]", "");
    }

    public static ImageView view(Image image, double width, double height) {
        ImageView view = new ImageView(image);
        view.setPreserveRatio(true);
        view.setSmooth(true);
        view.setFitWidth(width);
        view.setFitHeight(height);
        view.setMouseTransparent(true);
        return view;
    }
}
