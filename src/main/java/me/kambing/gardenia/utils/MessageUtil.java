package me.kambing.gardenia.utils;

import com.mojang.realmsclient.gui.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ChatComponentText;

public class MessageUtil {
    public static void sendMessage(String message) {
        Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(ChatFormatting.GRAY + "[" + ChatFormatting.GOLD + "Gardenia" + ChatFormatting.GRAY + "] " + ChatFormatting.RESET + message));
    }
}
