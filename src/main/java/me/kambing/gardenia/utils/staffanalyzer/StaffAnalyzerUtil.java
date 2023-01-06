package me.kambing.gardenia.utils.staffanalyzer;

import com.google.gson.Gson;
import com.mojang.realmsclient.gui.ChatFormatting;
import me.kambing.gardenia.Gardenia;
import me.kambing.gardenia.utils.MessageUtil;
import me.kambing.gardenia.utils.StatsAnalyzerUtil;

import java.net.URL;

public class StaffAnalyzerUtil extends Thread {
    int lastBannedCount = 0;

    @Override
    public void run() {
        while (true) {
            try {
                while (true) {
                    if (StatsAnalyzerUtil.hypixelApiKey == null) {
                        Thread.sleep(1000L);
                    } else {
                        Thread.sleep((long) Gardenia.instance.moduleManager.staffAnalyzer.getSetting("Delay").getValDouble() * (long)1000.0);
                        final String result = HttpUtil.performGetRequest(new URL("https://api.hypixel.net/watchdogStats?key=" + StatsAnalyzerUtil.hypixelApiKey));
                        final Gson gson = new Gson();
                        final BanQuantityListJSON banQuantityListJSON = (BanQuantityListJSON) gson.fromJson(result, (Class<?>) BanQuantityListJSON.class);
                        final int staffTotal = banQuantityListJSON.getStaffTotal();
                        if (this.lastBannedCount == 0) {
                            this.lastBannedCount = staffTotal;
                        } else {
                            final int banned = staffTotal - this.lastBannedCount;
                            this.lastBannedCount = staffTotal;
                            if (banned > 1) {
                                MessageUtil.sendMessage(ChatFormatting.GRAY + "[" + ChatFormatting.GOLD + "StaffAnalyzer" + ChatFormatting.GRAY + "] " + ChatFormatting.RESET + "Staff didn't ban any player in " + Gardenia.instance.moduleManager.staffAnalyzer.getSetting("Delay").getValDouble() + "s.");
                            } else {
                                if (Gardenia.instance.moduleManager.staffAnalyzer.getSetting("Notify0Ban").getValBoolean()) {
                                    MessageUtil.sendMessage(ChatFormatting.GRAY + "[" + ChatFormatting.GOLD + "StaffAnalyzer" + ChatFormatting.GRAY + "] " + ChatFormatting.RESET + "Staff banned " + ChatFormatting.RED + banned + ChatFormatting.RESET + " player(s) in " + Gardenia.instance.moduleManager.staffAnalyzer.getSetting("Delay").getValDouble() + "s.");
                                }
                            }
                        }
                    }
                }
            } catch (Exception ignored) {
            }
        }
    }
}
