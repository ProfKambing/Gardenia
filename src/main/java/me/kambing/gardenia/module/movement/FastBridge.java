package me.kambing.gardenia.module.movement;

import me.kambing.gardenia.Gardenia;
import me.kambing.gardenia.module.Category;
import me.kambing.gardenia.module.Module;
import me.kambing.gardenia.settings.Setting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.BlockPos;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import org.lwjgl.input.Keyboard;

import java.lang.reflect.Field;

public class FastBridge extends Module {
    public FastBridge(){
        super("FastBridge", "helps you fastbridge", false, false, Category.Movement);
        new Setting("DisableOnShift", this, false);
    }


    public static final Field pressed = ReflectionHelper.findField(KeyBinding.class, "field_74513_e", "pressed");

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent e) {
        if (Gardenia.instance.destructed) {return;}
        if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) && getSetting("DisableOnShift").getValBoolean()) this.toggle();
        try {
            if (((mc.thePlayer.getCurrentEquippedItem().getItem() instanceof ItemBlock))) {
                BlockPos bp = new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY - 1.0D, mc.thePlayer.posZ);
                if (mc.theWorld.getBlockState(bp).getBlock() == Blocks.air) {
                    pressed.set(Minecraft.getMinecraft().gameSettings.keyBindSneak, true);
                } else {
                    pressed.set(Minecraft.getMinecraft().gameSettings.keyBindSneak, false);
                }
            }
        } catch (Exception localException) {
        }
    }

    @Override
    public void onDisabled() {
        super.onDisabled();
        KeyBinding.setKeyBindState(mc.gameSettings.keyBindSneak.getKeyCode(), false);
    }
}
