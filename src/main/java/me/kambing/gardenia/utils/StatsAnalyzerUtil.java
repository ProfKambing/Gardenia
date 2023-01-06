package me.kambing.gardenia.utils;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import java.net.HttpURLConnection;
import java.net.URL;
public class StatsAnalyzerUtil {
    public static String hypixelApiKey = "";

    public static String getTextFromURL(String _url) {
        String r = "";
        HttpURLConnection con = null;

        try {
            URL url = new URL(_url);
            con = (HttpURLConnection)url.openConnection();
            r = getTextFromConnection(con);
        } catch (IOException ignored) {
        } finally {
            if (con != null) {
                con.disconnect();
            }

        }

        return r;
    }

    private static String getTextFromConnection(HttpURLConnection connection) {
        if (connection != null) {
            try {
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

                String result;
                try {
                    StringBuilder stringBuilder = new StringBuilder();

                    String input;
                    while((input = bufferedReader.readLine()) != null) {
                        stringBuilder.append(input);
                    }

                    String res = stringBuilder.toString();
                    connection.disconnect();

                    result = res;
                } finally {
                    bufferedReader.close();
                }

                return result;
            } catch (Exception ignored) {}
        }

        return "";
    }
    public static int getValue(JsonObject type, String member) {
        try {
            return type.get(member).getAsInt();
        } catch (NullPointerException er) {
            return 0;
        }
    }
    public static JsonObject getStringAsJson(String text) {
        return new JsonParser().parse(text).getAsJsonObject();
    }
    public static boolean isHypixelKeyValid(String ak) {
        String c = getTextFromURL("https://api.hypixel.net/key?key=" + ak);
        return !c.isEmpty() && !c.contains("Invalid");
    }
}

