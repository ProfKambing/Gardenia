package me.kambing.gardenia.utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Objects;
import java.util.UUID;

public class CapeUtil {
    final static ArrayList<String> finalUUIDList = getUUIDS();

    public static ArrayList<String> getUUIDS() {
        try {
            URL url = new URL("https://raw.githubusercontent.com/ProfKambing/zzzzzzzz/main/zz");
            BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
            final ArrayList<String> uuid_list = new ArrayList<>();

            String s;

            while ((s = reader.readLine()) != null) {
                uuid_list.add(s);
            }

            return uuid_list;
        } catch (Exception ignored) {
            return null;
        }
    }

    public static boolean isUUIDValid(String name) {
        for (String u : Objects.requireNonNull(finalUUIDList)) {
            if (u.equals(name)) {
                return true;
            }
        }
        return false;
    }
}
