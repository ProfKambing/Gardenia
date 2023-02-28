package me.kambing.gardenia.module.movement;

import com.google.common.eventbus.Subscribe;
import me.kambing.gardenia.UpdateEvent;
import me.kambing.gardenia.module.Category;
import me.kambing.gardenia.module.Module;
import me.kambing.gardenia.settings.Setting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Vec3;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import org.lwjgl.input.Keyboard;

import java.awt.*;
import java.lang.reflect.Field;

import static me.kambing.gardenia.module.movement.Scaffold.blockBlacklist;
import static me.kambing.gardenia.module.movement.Scaffold.isGoingDiagonally;

public class FastBridge extends Module {
    Robot robot;

    public FastBridge() {
        super("FastBridge", "helps you fastbridge", false, false, Category.Movement);
        new Setting("DisableOnShift", this, false);
        new Setting("DirectionCheck", this, true);
        new Setting("AutoPlace", this, false);
        new Setting("AutoSwitch", this, false);
        new Setting("TestPlace", this, false);
        try {
            robot = new Robot();
        } catch (AWTException e) {
            e.printStackTrace();
        }
    }

    public static final Field pressed = ReflectionHelper.findField(KeyBinding.class, "field_74513_e", "pressed");


    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent e) {
        if (getSetting("AutoSwitch").getValBoolean()) {
            boolean foundSlot = false;
            int block = 0;

            for (int i = 0; i < 9; i++) {
                if (mc.thePlayer.inventory.getStackInSlot(i) == null)
                    continue;
                if (mc.thePlayer.inventory.getStackInSlot(i).getItem() instanceof ItemBlock && !blockBlacklist.contains(((ItemBlock) mc.thePlayer.inventory.getStackInSlot(i).getItem()).getBlock())) {
                    block = i;
                    foundSlot = true;
                    break;
                }
            }
            if (foundSlot) {
                mc.thePlayer.inventory.currentItem = block;
            }
        }
        if (!getSetting("DirectionCheck").getValBoolean() || Keyboard.isKeyDown(Keyboard.KEY_S)) {
            BlockPos bp = new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY - 1.0D, mc.thePlayer.posZ);
            if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) && getSetting("DisableOnShift").getValBoolean()) this.toggle();
            try {
                if (((mc.thePlayer.getCurrentEquippedItem().getItem() instanceof ItemBlock))) {
                    if (mc.theWorld.getBlockState(bp).getBlock() == Blocks.air) {
                        pressed.set(Minecraft.getMinecraft().gameSettings.keyBindSneak, true);
                        if (Keyboard.isKeyDown(Keyboard.KEY_S) && getSetting("AutoPlace").getValBoolean()) {
                            //mc.thePlayer.rotationPitch = isGoingDiagonally() ? 78F : 81.5F;
                            if (getSetting("TestPlace").getValBoolean()) {
                                if (mc.playerController.onPlayerRightClick(mc.thePlayer, mc.theWorld, mc.thePlayer.getHeldItem(), bp, EnumFacing.UP, new Vec3(bp).addVector(0.5, 0.5, 0.5).add(new Vec3(bp.getX() * 0.5, bp.getY() * 0.5, bp.getZ() * 0.5)))) {
                                    mc.thePlayer.swingItem();
                                }
                            } else {
                                pressed.set(mc.gameSettings.keyBindUseItem, true);
                            }
                        }
                    } else {
                        pressed.set(Minecraft.getMinecraft().gameSettings.keyBindSneak, false);
                        if (Keyboard.isKeyDown(Keyboard.KEY_S) && getSetting("AutoPlace").getValBoolean()) {
                            //mc.thePlayer.rotationPitch = isGoingDiagonally() ? 78F : 81.5F;
                            pressed.set(mc.gameSettings.keyBindUseItem, false);
                        }
                    }
                }
            } catch (Exception localException) {
            }
        } else if (getSetting("DirectionCheck").getValBoolean() && !Keyboard.isKeyDown(Keyboard.KEY_S) && Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) {
            try {
                pressed.set(Minecraft.getMinecraft().gameSettings.keyBindSneak, true);
            } catch (IllegalAccessException e1) {
                e1.printStackTrace();
            }
        }
        //MessageUtil.sendMessage(Integer.toString((int)mc.thePlayer.rotationPitch));
    }

    @Override
    public void onEnabled() {
        super.onEnabled();
        if (mc.thePlayer == null) return;
    }

    @Subscribe
    public void onLook(UpdateEvent.LookEvent e) {
        if (!isToggled()) return;
        if (Keyboard.isKeyDown(Keyboard.KEY_S) && getSetting("AutoPlace").getValBoolean()) {
            mc.thePlayer.setSprinting(false);
            e.setPitch(isGoingDiagonally() ? 78F : 81.5F);
        }
    }

    @Subscribe
    public void onUpdate(UpdateEvent e) {
        if (!isToggled()) return;
        if (Keyboard.isKeyDown(Keyboard.KEY_S) && getSetting("AutoPlace").getValBoolean()) {
            mc.thePlayer.setSprinting(false);
            e.setPitch(isGoingDiagonally() ? 78F : 81.5F);
        }
    }


    @Override
    public void onDisabled() {
        super.onDisabled();
        try {
            pressed.set(mc.gameSettings.keyBindUseItem, false);
            pressed.set(Minecraft.getMinecraft().gameSettings.keyBindSneak, true);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        KeyBinding.setKeyBindState(mc.gameSettings.keyBindSneak.getKeyCode(), false);
    }
}
