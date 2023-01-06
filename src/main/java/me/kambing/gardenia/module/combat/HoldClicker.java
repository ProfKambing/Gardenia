package me.kambing.gardenia.module.combat;


import me.kambing.gardenia.module.Category;
import me.kambing.gardenia.module.Module;
import me.kambing.gardenia.settings.Setting;
import me.kambing.gardenia.utils.TimeUtil;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MovingObjectPosition;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.apache.commons.lang3.RandomUtils;
import org.lwjgl.input.Mouse;

import java.awt.*;

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
        new Setting("ExtraRandomisation", this, true);
        try {
            robot = new Robot();
        } catch (AWTException e) {
            e.printStackTrace();
        }
    }
    private TimeUtil.Timer timer = new TimeUtil.Timer();

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent e) {
        if (mc.thePlayer == null || mc.currentScreen != null) return;
        if (!(mc.gameSettings.keyBindUseItem.isKeyDown())) {
                if (Mouse.isButtonDown(0) && timer.hasReached((long)1000D / RandomUtils.nextInt((int)getSetting("MinCPS").getValDouble(), (int)getSetting("MaxCPS").getValDouble()))&& (getSetting("ExtraRandomisation").getValBoolean() && mc.thePlayer.ticksExisted % 5 != 0 && mc.thePlayer.ticksExisted % 17 != 0)) {
                    if (Minecraft.getMinecraft().objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK && getSetting("BreakBlocks").getValBoolean()) return;
                    if (getSetting("Legit").getValBoolean()) {
                        this.robot.mouseRelease(16);
                        this.robot.mousePress(16);
                    } else {
                        clickMouse();
                    }
                    timer.reset();
                }
            }

        }

    public void clickMouse() {

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
