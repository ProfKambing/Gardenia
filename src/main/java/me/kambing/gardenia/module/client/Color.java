package me.kambing.gardenia.module.client;

import me.kambing.gardenia.Gardenia;
import me.kambing.gardenia.module.Category;
import me.kambing.gardenia.module.Module;
import me.kambing.gardenia.settings.Setting;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class Color extends Module {
    public Color() {
        super("Color", "color syncing purposes", false, false, Category.Client);
        new Setting("Color", this, java.awt.Color.decode("#ff00cc"));
        new Setting("SyncAll", this, false);
    }

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent e) {
        if (getSetting("SyncAll").getValBoolean()) {
            for (Module module : Gardenia.instance.moduleManager.modules) {
                for (Setting setting : Gardenia.instance.settingsManager.getSettingsByMod(module)) {
                    if (!setting.isColor()) continue;
                    setting.setColor(getSetting("Color").getColor());
                }
            }
            this.setToggled(false);
            getSetting("SyncAll").setValBoolean(false);
        }
    }
}
