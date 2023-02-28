package me.kambing.gardenia;

import net.arikia.dev.drpc.DiscordEventHandlers;
import net.arikia.dev.drpc.DiscordRPC;
import net.arikia.dev.drpc.DiscordRichPresence;
import net.arikia.dev.drpc.DiscordUser;
import net.arikia.dev.drpc.callbacks.ReadyCallback;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiMainMenu;

public class DiscordRP {

    private boolean running = true;
    private long created = 0;
    DiscordRichPresence.Builder b;

    public void start() {
        this.created = System.currentTimeMillis();

        DiscordEventHandlers handlers = new DiscordEventHandlers.Builder().setReadyEventHandler(new ReadyCallback() {
            @Override
            public void apply(DiscordUser user) {
                System.out.println("Welcome: " + user.username + "#" + user.discriminator + ".");
                    update("Playing as " + Minecraft.getMinecraft().getSession().getUsername(), "");
                }
        }).build();

        DiscordRPC.discordInitialize("1031167103635562536", handlers, true);
        new Thread("Discord RPC Callback") {
            @Override
            public void run() {
                while(running) {
                    DiscordRPC.discordRunCallbacks();
                }
            }
        }.start();
    }

    public void shutdown() {
        running = false;
        DiscordRPC.discordShutdown();
    }

    public void update(String firstLine, String secondLine) {
        b = new DiscordRichPresence.Builder(secondLine);
        b.setBigImage("gardenianazi", "discord.gg/Gsq43Yrehp");
        b.setSmallImage("gardenia", Gardenia.VERSION);
        b.setDetails(firstLine);
        b.setStartTimestamps(created);

        DiscordRPC.discordUpdatePresence(b.build());
    }

}
