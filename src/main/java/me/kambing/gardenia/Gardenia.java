package me.kambing.gardenia;

import com.mojang.realmsclient.gui.ChatFormatting;
import me.kambing.gardenia.clickgui.ClickGui;
import me.kambing.gardenia.command.CommandManager;
import me.kambing.gardenia.config.SaveLoad;
import me.kambing.gardenia.module.Module;
import me.kambing.gardenia.module.ModuleManager;
import me.kambing.gardenia.settings.SettingsManager;
import me.kambing.gardenia.utils.MessageUtil;
import me.kambing.gardenia.utils.StatsAnalyzerUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import org.lwjgl.input.Keyboard;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class Gardenia
{
    public static final String MODID = "Gardenia";
    public static final String VERSION = "v2.0";
    public static String prefix = ".";

    public static Gardenia instance;
    private final DiscordRP discordRP = new DiscordRP();
    public ModuleManager moduleManager;
    public SettingsManager settingsManager;
    public ClickGui clickGui;
    public SaveLoad saveLoad;
    public CommandManager commandManager;
    public ArrayList<EntityPlayer> bots = new ArrayList<EntityPlayer>();
    private static final ScheduledExecutorService executor = Executors.newScheduledThreadPool(2);

    public boolean destructed = false;
    List<String> arrayList = new ArrayList<>();

    public void init() {
        // following 5 lines must be in this order or java has a stroke and dies

        MinecraftForge.EVENT_BUS.register(this);
        instance = this;

        settingsManager = new SettingsManager();
        moduleManager = new ModuleManager();
        saveLoad = new SaveLoad();
        clickGui = new ClickGui();
        commandManager = new CommandManager();
        discordRP.start();

    }

    @SubscribeEvent
    public void key(InputEvent.KeyInputEvent e) {
        Minecraft mc = Minecraft.getMinecraft();
        //make sure that we are in a game
        if (mc.theWorld == null || mc.thePlayer == null)
            return;

        //Basically get the key id, go through the list of modules
        //If module key is equal to the one pressed than we toggle the modules
        try{
            if (Keyboard.isCreated()) {
                if (Keyboard.getEventKeyState()) {
                    int keyCode = Keyboard.getEventKey();
                    if (keyCode <= 0)
                        return;
                    for (Module m : moduleManager.getModulesList()) {
                        if (m.getKey() == keyCode && keyCode > 0) {
                            m.toggle();
                        }
                    }
                }
            }
            //In case java shits itself ft. my code
        } catch (Exception q) {
            q.printStackTrace();
        }
    }

    public void onDestruct() {
        if (Minecraft.getMinecraft().currentScreen != null && Minecraft.getMinecraft().thePlayer != null) {
            Minecraft.getMinecraft().thePlayer.closeScreen();
        }
        destructed = true;
        MinecraftForge.EVENT_BUS.unregister(this);
        for (int k = 0; k < this.moduleManager.modules.size(); k++) {
            Module m = this.moduleManager.modules.get(k);
            MinecraftForge.EVENT_BUS.unregister(m);
            this.moduleManager.getModulesList().remove(m);
        }
        this.moduleManager = null;
        this.clickGui = null;
    }

    public ArrayList<EntityPlayer> getBots() {
        return this.bots;
    }

    public void updateBots(List<EntityPlayer> e) {
        this.bots = new ArrayList<EntityPlayer>();
        for (EntityPlayer entityPlayer : e) {
            this.bots.add(entityPlayer);
        }
    }
    @SuppressWarnings("unused")
    @SubscribeEvent
    public void onChatMessageReceived(ClientChatReceivedEvent event) {
        if (Minecraft.getMinecraft().thePlayer != null) {
            String msg = event.message.getUnformattedText();

            if (msg.startsWith("Your new API key is")) {
                StatsAnalyzerUtil.hypixelApiKey = msg.replace("Your new API key is ", "");
                MessageUtil.sendMessage("Set api key to " + ChatFormatting.BOLD + StatsAnalyzerUtil.hypixelApiKey);
                saveLoad.saveHypixelApiKey();
            }
        }
    }
    public static ScheduledExecutorService getExecutor() {
        return executor;
    }
}
