package me.kambing.gardenia.module.player;

import me.kambing.gardenia.settings.Setting;
import me.kambing.gardenia.module.Category;
import me.kambing.gardenia.module.Module;
import net.minecraft.client.Minecraft;
import net.minecraft.init.Items;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

import java.lang.reflect.Field;

public class FastPlace extends Module {
    public static final Field delayTimer = ReflectionHelper.findField(Minecraft.class, "field_71467_ac", "rightClickDelayTimer");

    public FastPlace() {
        super("FastPlace", "Places blocks faster", false, false, Category.Player);
        new Setting("Delay", this,1,0,10,true);
    }

    @SubscribeEvent
    public void PlayerTickEvent(TickEvent.PlayerTickEvent e) {
        if(this.isToggled()) {
            try {
                if (mc.thePlayer.getHeldItem().getItem() == null) return;
                if (mc.thePlayer.getHeldItem().getItem().equals(Items.paper)) return;
                if (mc.thePlayer.getHeldItem().getItem().equals(Items.fishing_rod)) return;
                delayTimer.setInt(Minecraft.getMinecraft(), (int)getSetting("Delay").getValDouble());
            } catch (IllegalAccessException illegalAccessException) {
                illegalAccessException.printStackTrace();
            }
            catch (NullPointerException ignored) {
            }
        }
    }
}