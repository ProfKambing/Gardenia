package me.kambing.gardenia.module.movement;

import com.google.common.eventbus.Subscribe;
import me.kambing.gardenia.UpdateEvent;
import me.kambing.gardenia.module.Category;
import me.kambing.gardenia.module.Module;
import me.kambing.gardenia.settings.Setting;
import net.minecraft.block.BlockAir;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.network.play.client.C0BPacketEntityAction;
import net.minecraft.util.*;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

import java.awt.*;
import java.lang.reflect.Field;
import java.util.Objects;

import static me.kambing.gardenia.module.movement.Scaffold.blockBlacklist;
import static me.kambing.gardenia.module.movement.Scaffold.isGoingDiagonally;

public class ScaffoldRewrite extends Module {
    Robot robot;
    BlockPos pos = null;

    public ScaffoldRewrite() {
        super("ScaffoldRewrite", "wip", false, false, Category.Movement);
        new Setting("AutoSwitch", this, false);
        new Setting("StopSprint", this, false);
        new Setting("Speed", this, 0.7,0.1,1, false);
        try {
            robot = new Robot();
        } catch (AWTException e) {
            e.printStackTrace();
        }
    }

    private float yaw = 0;
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
        if (getSetting("StopSprint").getValBoolean()) {
            mc.thePlayer.setSprinting(false);
        }
        mc.thePlayer.motionX *= getSetting("Speed").getValDouble();
        mc.thePlayer.motionZ *= getSetting("Speed").getValDouble();
        BlockPos bp = new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY - 1.0D, mc.thePlayer.posZ);
        pos = bp;
        try {
            if (((mc.thePlayer.getCurrentEquippedItem().getItem() instanceof ItemBlock))) {
                if (mc.theWorld.getBlockState(bp).getBlock() == Blocks.air) {
                    pressed.set(Minecraft.getMinecraft().gameSettings.keyBindSneak, true);
                    //mc.thePlayer.rotationPitch = isGoingDiagonally() ? 78F : 81.5F;
                    pressed.set(mc.gameSettings.keyBindUseItem, true);
                    //getPlaceBlock(getBlockData().blockPos, getBlockData().enumFacing);
                } else {
                    pressed.set(Minecraft.getMinecraft().gameSettings.keyBindSneak, false);

                    //mc.thePlayer.rotationPitch = isGoingDiagonally() ? 78F : 81.5F;
                    pressed.set(mc.gameSettings.keyBindUseItem, false);
                }
            }
        } catch (Exception localException) {
        }
    }
    //MessageUtil.sendMessage(Integer.toString((int)mc.thePlayer.rotationPitch));

    @Override
    public void onEnabled() {
        super.onEnabled();
        if (mc.thePlayer == null) return;
        yaw = mc.thePlayer.rotationYaw;
    }

    private  boolean getPlaceBlock(BlockPos pos, final EnumFacing facing) {
        final Vec3 eyesPos = new Vec3(mc.thePlayer.posX, mc.thePlayer.posY + mc.thePlayer.getEyeHeight(), mc.thePlayer.posZ);
        Vec3i data = getBlockData().getFacing().getDirectionVec();
        if (mc.playerController.onPlayerRightClick(mc.thePlayer, mc.theWorld, mc.thePlayer.getHeldItem(), pos, facing, new Vec3(getBlockData().getPosition()).addVector(0.5, 0.5, 0.5).add(new Vec3(data.getX() * 0.5, data.getY() * 0.5, data.getZ() * 0.5)))) {
            //if(this.swing.getValue()) {
            mc.thePlayer.swingItem();
            //} else {
            //	mc.thePlayer.sendQueue.addToSendQueue(new C0APacketAnimation());
            //}
            return true;
        }
        return false;
    }

    @Subscribe
    public void onLook(UpdateEvent.LookEvent e) {
        if (!isToggled()) return;
        float[] rots = hyprots(Objects.requireNonNull(getBlockData()).getPosition());
        e.setYaw(getYawBackward());
        e.setPitch(isGoingDiagonally() ? 78F : 81.5F);
    }

    @Subscribe
    public void onUpdate(UpdateEvent e) {
        if (!isToggled()) return;
        float[] rots = hyprots(Objects.requireNonNull(getBlockData()).getPosition());
        e.setYaw(getYawBackward());
        e.setPitch(isGoingDiagonally() ? 78F : 81.5F);
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

    private BlockData getBlockData() {
        final EnumFacing[] invert = {EnumFacing.UP, EnumFacing.DOWN, EnumFacing.SOUTH, EnumFacing.NORTH, EnumFacing.EAST, EnumFacing.WEST};
        double yValue = 0;
        BlockPos playerpos = new BlockPos(mc.thePlayer.getPositionVector()).offset(EnumFacing.DOWN).add(0, yValue, 0);

        EnumFacing[] facingVals = EnumFacing.values();
        for (int i = 0; i < facingVals.length; ++i) {
            if (mc.theWorld.getBlockState(playerpos.offset(facingVals[i])).getBlock().getMaterial() != Material.air) {
                return new BlockData(playerpos.offset(facingVals[i]), invert[facingVals[i].ordinal()]);
            }
        }
        final BlockPos[] addons = {
                new BlockPos(-1, 0, 0),
                new BlockPos(1, 0, 0),
                new BlockPos(0, 0, -1),
                new BlockPos(0, 0, 1)};
        for (int length2 = addons.length, j = 0; j < length2; ++j) {
            final BlockPos offsetPos = playerpos.add(addons[j].getX(), 0, addons[j].getZ());
            if (mc.theWorld.getBlockState(offsetPos).getBlock() instanceof BlockAir) {
                for (int k = 0; k < EnumFacing.values().length; ++k) {
                    if (mc.theWorld.getBlockState(offsetPos.offset(EnumFacing.values()[k])).getBlock().getMaterial() != Material.air) {
                        //if(diagonal.getValue())
                        return new BlockData(offsetPos.offset(EnumFacing.values()[k]), invert[EnumFacing.values()[k].ordinal()]);
                    }
                }
            }
        }
        return null;
    }

    private float[] hyprots(BlockPos e) {
        double x = e.getX() - mc.thePlayer.posX;
        double y = (e.getY() - (mc.thePlayer.posY + mc.thePlayer.getEyeHeight() * 1.9));
        if (e.getY() - 1 > mc.thePlayer.posY)
            y = (e.getY() - (mc.thePlayer.posY + mc.thePlayer.getEyeHeight() * 1.1));
        double z = e.getZ() - mc.thePlayer.posZ;
        double dist = Math.sqrt(Math.pow(x, 2) + Math.pow(z, 2));

        float yaw = (float) Math.toDegrees(-Math.atan(x / z));
        float pitch = (float) -Math.toDegrees(Math.atan(y / dist));

        if (x < 0 && z < 0)
            yaw = 90 + (float) Math.toDegrees(Math.atan(z / x));
        else if (x > 0 && z < 0)
            yaw = -90 + (float) Math.toDegrees(Math.atan(z / x));

        if (pitch > 90)
            pitch = 90;
        if (pitch < -90)
            pitch = -90;
        if (yaw > 180)
            yaw = 180;
        if (yaw < -180)
            yaw = -180;

        return new float[]{yaw, pitch};
    }

    private float getYawBackward() {
        float yaw = MathHelper.wrapAngleTo180_float(this.yaw);

        MovementInput input = mc.thePlayer.movementInput;
        float strafe = input.moveStrafe, forward = input.moveForward;

        if (forward != 0) {
            if (strafe < 0) {
                yaw += forward < 0 ? 135 : 45;
            } else if (strafe > 0) {
                yaw -= forward < 0 ? 135 : 45;
            } else if (strafe == 0 && forward < 0) {
                yaw -= 180;
            }

        } else {
            if (strafe < 0) {
                yaw += 90;
            } else if (strafe > 0) {
                yaw -= 90;
            }
        }

        return MathHelper.wrapAngleTo180_float(yaw - 180);
    }

    private class BlockData {
        private BlockPos blockPos;
        private EnumFacing enumFacing;

        private BlockData(final BlockPos blockPos, final EnumFacing enumFacing) {
            this.blockPos = blockPos;
            this.enumFacing = enumFacing;
        }

        private EnumFacing getFacing() {
            return this.enumFacing;
        }

        private BlockPos getPosition() {
            return this.blockPos;
        }
    }
}
