package me.kambing.gardenia.module.player;

import me.kambing.gardenia.module.Category;
import me.kambing.gardenia.module.Module;
import me.kambing.gardenia.settings.Setting;
import net.minecraft.block.Block;
import net.minecraft.block.BlockSnow;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.input.Keyboard;

public class BlockClutch extends Module {
    public BlockClutch(){
        super("BlockClutch", "Block Clutches you", false, false, Category.Player);
        new Setting("Range", this, 4, 0, 10, false);
        new Setting("AutoSwitch", this, true);
        new Setting("RotateNew", this, false);
    }

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent e) {
        if (getSetting("RotateNew").getValBoolean()) {
            if (Keyboard.isKeyDown(Keyboard.KEY_S)) {
                float[] angles = doScaffoldRotations(new Vec3(new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY - 1.0D, mc.thePlayer.posZ)));
                mc.thePlayer.rotationYawHead = angles[1];
                mc.thePlayer.rotationPitch = angles[1];
                mc.thePlayer.renderYawOffset = angles[1];
                placeBlock((int) getSetting("Range").getValDouble(), true);
            }
        } else {
            placeBlock((int) getSetting("Range").getValDouble(), true);
        }
    }
    public void placeBlock(final int range, final boolean place) {
        if (isAirBlock(getBlock(new BlockPos(mc.thePlayer).down()))) {
            return;
        }
        if (placeBlockSimple(new BlockPos(mc.thePlayer).down(), place, getSetting("AutoSwitch").getValBoolean(), getSetting("RotateNew").getValBoolean())) {
            return;
        }
        int dist = 0;
        while (dist <= range) {
            for (int blockDist = 0; dist != blockDist; ++blockDist) {
                for (int x = blockDist; x >= 0; --x) {
                    final int z = blockDist - x;
                    final int y = dist - blockDist;
                    if (placeBlockSimple(new BlockPos(mc.thePlayer).down(y).north(x).west(z), place, getSetting("AutoSwitch").getValBoolean(), getSetting("RotateNew").getValBoolean())) {
                        return;
                    }
                    if (placeBlockSimple(new BlockPos(mc.thePlayer).down(y).north(x).west(-z), place, getSetting("AutoSwitch").getValBoolean(), getSetting("RotateNew").getValBoolean())) {
                        return;
                    }
                    if (placeBlockSimple(new BlockPos(mc.thePlayer).down(y).north(-x).west(z), place, getSetting("AutoSwitch").getValBoolean(), getSetting("RotateNew").getValBoolean())) {
                        return;
                    }
                    if (placeBlockSimple(new BlockPos(mc.thePlayer).down(y).north(-x).west(-z), place, getSetting("AutoSwitch").getValBoolean(), getSetting("RotateNew").getValBoolean())) {
                        return;
                    }
                }
            }
            ++dist;
        }
    }

    public boolean isAirBlock(final Block block) {
        return !block.getMaterial().isReplaceable() || (block instanceof BlockSnow && !(block.getBlockBoundsMaxY() <= 0.125));
    }

    public static boolean placeBlockSimple(final BlockPos pos, final boolean place, boolean autoSwitch, boolean scaffold) {
        if (!doesSlotHaveBlocks(mc.thePlayer.inventory.currentItem) && autoSwitch) {
            mc.thePlayer.inventory.currentItem = getFirstHotBarSlotWithBlocks();
        }
        final Minecraft mc = Minecraft.getMinecraft();
        final Entity entity = mc.getRenderViewEntity();
        final double d0 = entity.posX;
        final double d2 = entity.posY;
        final double d3 = entity.posZ;
        final Vec3 eyesPos = new Vec3(d0, d2 + mc.thePlayer.getEyeHeight(), d3);
        for (final EnumFacing side : EnumFacing.values()) {
            if (!side.equals(EnumFacing.UP)) {
                if (!side.equals(EnumFacing.DOWN) || Keyboard.isKeyDown(mc.gameSettings.keyBindJump.getKeyCode())) {
                    final BlockPos neighbor = pos.offset(side);
                    final EnumFacing side2 = side.getOpposite();
                    if (getBlock(neighbor).canCollideCheck(mc.theWorld.getBlockState(neighbor), false)) {
                        final Vec3 hitVec = new Vec3(neighbor).addVector(0.5, 0.5, 0.5).add(new Vec3(side2.getDirectionVec()));
                        if (eyesPos.squareDistanceTo(hitVec) <= 36.0) {
                            float[] angles;
                            if (!scaffold) {
                                angles  = getRotations(neighbor, side2);
                            } else {
                                angles = doScaffoldRotations(new Vec3(pos));
                            }
                            //mc.getRenderViewEntity().rotationYaw = angles[1];
                            //mc.getRenderViewEntity().rotationPitch = angles[1];

                            if (place) {
                                mc.thePlayer.rotationYawHead = angles[1];
                                mc.thePlayer.rotationPitch = angles[1];
                                mc.thePlayer.renderYawOffset = angles[1];
                                mc.playerController.onPlayerRightClick(mc.thePlayer, mc.theWorld, mc.thePlayer.getCurrentEquippedItem(), neighbor, side2, hitVec);
                                mc.thePlayer.swingItem();
                            }
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    public static Block getBlock(final BlockPos pos) {
        return Minecraft.getMinecraft().theWorld.getBlockState(pos).getBlock();
    }

    public static float[] getRotations(final BlockPos block, final EnumFacing face) {
        final Entity entity = mc.getRenderViewEntity();
        final double posX = entity.posX;
        final double posY = entity.posY;
        final double posZ = entity.posZ;
        final double x = block.getX() + 0.5 - posX + face.getFrontOffsetX() / 2.0;
        final double z = block.getZ() + 0.5 - posZ + face.getFrontOffsetZ() / 2.0;
        final double y = block.getY() + 0.5;
        final double d1 = posY + mc.thePlayer.getEyeHeight() - y;
        final double d2 = MathHelper.sqrt_double(x * x + z * z);
        float yaw = (float)(Math.atan2(z, x) * 180.0 / 3.141592653589793) - 90.0f;
        final float pitch = (float)(Math.atan2(d1, d2) * 180.0 / 3.141592653589793);
        if (yaw < 0.0f) {
            yaw += 360.0f;
        }
        return new float[] { yaw, pitch };
    }

    public static int getFirstHotBarSlotWithBlocks() {
        for (int i = 0; i < 9; ++i) {
            if (mc.thePlayer.inventory.getStackInSlot(i) != null && mc.thePlayer.inventory.getStackInSlot(i).getItem() instanceof ItemBlock) {
                if (mc.thePlayer.inventory.getStackInSlot(i).getItem().equals(ItemBlock.getItemFromBlock(Blocks.tnt))) continue;
                return i;
            }
        }
        return 0;
    }
    public static boolean doesSlotHaveBlocks(final int slotToCheck) {
        return mc.thePlayer.inventory.getStackInSlot(slotToCheck) != null && mc.thePlayer.inventory.getStackInSlot(slotToCheck).getItem() instanceof ItemBlock && mc.thePlayer.inventory.getStackInSlot(slotToCheck).stackSize > 0;
    }

    public static float[] doScaffoldRotations(Vec3 vec) {
        double diffX = vec.xCoord - mc.thePlayer.posX;
        double diffY = vec.yCoord - (mc.thePlayer.getEntityBoundingBox().minY);
        double diffZ = vec.zCoord - mc.thePlayer.posZ;
        double dist = MathHelper.sqrt_double(diffX * diffX + diffZ * diffZ);
        float yaw = (float) (Math.toDegrees(Math.atan2(diffZ, diffX)));
        float pitch = (float) -Math.toDegrees(Math.atan2(diffY, dist));
        return new float[] {
                mc.thePlayer.rotationYaw
                        + MathHelper.wrapAngleTo180_float(yaw - mc.thePlayer.rotationYaw),
                mc.thePlayer.rotationPitch
                        + MathHelper.wrapAngleTo180_float(pitch - mc.thePlayer.rotationPitch) };
    }

}
