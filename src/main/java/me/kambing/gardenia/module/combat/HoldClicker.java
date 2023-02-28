package me.kambing.gardenia.module.combat;


import me.kambing.gardenia.module.Category;
import me.kambing.gardenia.module.Module;
import me.kambing.gardenia.settings.Setting;
import me.kambing.gardenia.utils.TimeUtil;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemSword;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MovingObjectPosition;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.apache.commons.lang3.RandomUtils;
import org.lwjgl.input.Mouse;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.Random;

/**
 * @author kambing
 */
public class HoldClicker extends Module {


    Robot robot;

    public HoldClicker() {
        super("HoldClicker", "hold click", false, false, Category.Combat);
        new Setting("MaxCPS", this, 8, 1, 20, true);
        new Setting("MinCPS", this, getSetting("MaxCPS").getValDouble(), 1, 20, true);
        new Setting("Legit", this, true);
        new Setting("BreakBlocks", this, true);
        new Setting("BlockHit", this, false);
        new Setting("Bypass", this, true);
        try {
            robot = new Robot();
        } catch (AWTException e) {
            e.printStackTrace();
        }
    }

    private final TimeUtil.Timer timer = new TimeUtil.Timer();
    private double tick = 0.0;
    private double currentSpeed;
    private final Random random = new Random();

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent e) {
        if (!isToggled())
            return;
        if (mc.theWorld == null)
            return;
        if (mc.thePlayer == null)
            return;
        if (!mc.thePlayer.isEntityAlive())
            return;
        if (mc.currentScreen != null)
            return;
        if (!(mc.gameSettings.keyBindUseItem.isKeyDown())) {
            if (!getSetting("Bypass").getValBoolean()) {
                if (Mouse.isButtonDown(0) && timer.hasReached((long) 1000D / RandomUtils.nextInt((int) getSetting("MinCPS").getValDouble(), (int) getSetting("MaxCPS").getValDouble()))) {
                    if (Minecraft.getMinecraft().objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK && getSetting("BreakBlocks").getValBoolean())
                        return;
                    if (getSetting("Legit").getValBoolean()) {
                        this.robot.mouseRelease(16);
                        this.robot.mousePress(16);
                    } else {
                        clickMouse();
                    }
                    if (Minecraft.getMinecraft().objectMouseOver.entityHit != null) {
                        if (mc.thePlayer.inventory.getCurrentItem() != null && getSetting("BlockHit").getValBoolean()) {
                            if (mc.thePlayer.inventory.getCurrentItem().getItem() instanceof ItemSword) {
                                robot.mousePress(MouseEvent.BUTTON3_DOWN_MASK);
                                robot.mouseRelease(MouseEvent.BUTTON3_DOWN_MASK);
                            }
                        }
                    }
                    timer.reset();
                }
            } else {
                if (Mouse.isButtonDown(0)) {
                    this.tick += this.currentSpeed / 40.0;
                    if (this.tick > 1.0) {
                        this.tick -= 1.0;
                        clickMouse();
                        if (Minecraft.getMinecraft().objectMouseOver.entityHit != null) {
                            if (mc.thePlayer.inventory.getCurrentItem() != null && getSetting("BlockHit").getValBoolean()) {
                                if (mc.thePlayer.inventory.getCurrentItem().getItem() instanceof ItemSword) {
                                    robot.mousePress(MouseEvent.BUTTON3_DOWN_MASK);
                                    robot.mouseRelease(MouseEvent.BUTTON3_DOWN_MASK);
                                }
                            }
                        }
                    }
                }
                if (AutoClicker.mc.thePlayer.ticksExisted % (10 + this.random.nextInt(30)) != 0) {
                    if (this.currentSpeed != 0.0) return;
                }
                double dif = getSetting("MaxCPS").getValDouble() - getSetting("MinCPS").getValDouble();
                dif *= this.random.nextDouble();
                this.currentSpeed = dif += getSetting("MinCPS").getValDouble();
            }
        }

    }

    public static void clickMouse() {

        switch (Minecraft.getMinecraft().objectMouseOver.typeOfHit) {
            case ENTITY:
                mc.thePlayer.swingItem();
                mc.playerController.attackEntity(mc.thePlayer, mc.objectMouseOver.entityHit);
                break;
            case BLOCK:
                BlockPos blockpos = mc.objectMouseOver.getBlockPos();
                if (mc.theWorld.getBlockState(blockpos).getBlock().getMaterial() != Material.air) {
                    mc.thePlayer.swingItem();
                    mc.playerController.clickBlock(blockpos, mc.objectMouseOver.sideHit);
                    break;
                }
            case MISS:
                mc.thePlayer.swingItem();
        }
    }
}
