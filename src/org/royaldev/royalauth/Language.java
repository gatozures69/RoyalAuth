package org.royaldev.royalauth;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.PropertyResourceBundle;

public class Language {

    static RoyalAuth plugin;
    static PropertyResourceBundle rb;

    public Language(RoyalAuth instance) throws IOException {
        plugin = instance;
        rb = new PropertyResourceBundle(new FileInputStream(plugin.getDataFolder() + File.separator + "messages.properties"));
    }

    public static String _(String label) {
        return rb.getString(label);
    }

    public static boolean reload() {
        try {
            rb = new PropertyResourceBundle(new FileInputStream(plugin.getDataFolder() + File.separator + "messages.properties"));
            return true;
        } catch (IOException e) {
            return false;
        }
    }

}
