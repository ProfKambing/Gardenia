package me.kambing.gardenia.module.player;

import com.google.common.eventbus.Subscribe;
import me.kambing.gardenia.Gardenia;
import me.kambing.gardenia.UpdateEvent;
import me.kambing.gardenia.module.Category;
import me.kambing.gardenia.module.Module;
import me.kambing.gardenia.settings.Setting;
import me.kambing.gardenia.utils.MessageUtil;
import me.kambing.gardenia.utils.RenderUtil;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C0APacketAnimation;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.awt.*;

public class BedBreaker extends Module {

    public BlockPos pos;
    public BlockPos defense;
    private static boolean breakingDefense;

    public BedBreaker() {
        super("BedBreaker", "breaks za bed", false, false, Category.Player);
        new Setting("Hypixel", this, true);
        new Setting("Radius", this, 4, 1, 4, true);
        new Setting("Delay", this, 300, 10, 1000, true);
        new Setting("Rotate", this, true);
        new Setting("Color", this, Color.RED);
    }


    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent e) {
        if (!this.isToggled()) return;
        if (mc.thePlayer == null) return;
        if (mc.theWorld == null) return;
        try {
            if (this.isToggled()) {
                int ra = (int) getSetting("Radius").getValDouble();
                for (int y = ra; y >= -ra; --y) {
                    for (int x = -ra; x <= ra; ++x) {
                        for (int z = -ra; z <= ra; ++z) {
                            if (mc.theWorld != null) {
                                BlockPos p = new BlockPos(Module.mc.thePlayer.posX + (double) x,
                                        Module.mc.thePlayer.posY + (double) y, Module.mc.thePlayer.posZ + (double) z);
                                boolean bed = Module.mc.theWorld.getBlockState(p).getBlock() == Blocks.bed;
                                if (pos == p) {
                                    if (!bed) {
                                        pos = null;
                                    }
                                } else if (bed) {
                                    smashBlock(new BlockPos(p.getX(), p.getY(), p.getZ()));
                                    pos = p;
                                    setToggled(false);
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    @SubscribeEvent
    public void onRenderGameOverlay(RenderGameOverlayEvent event) {
        if (pos != null && !Gardenia.instance.moduleManager.twoDESP.isToggled()) {
            RenderUtil.re(pos, getSetting("Color").getColor().getRGB(), false);
        }
    }

    @Subscribe
    public void onLook(UpdateEvent.LookEvent e) {
        if (pos != null) {
            double[] angles;
            if (!breakingDefense) {
                angles = calculateLookAt(pos.getX(), pos.getY() - 0.5, pos.getZ() + 0.5, mc.thePlayer);
            } else {
                angles = calculateLookAt(pos.getX(), pos.getY(), pos.getZ() + 1, mc.thePlayer);
            }
            if (getSetting("Rotate").getValBoolean()) {
                e.setYaw((int) angles[0]);
                e.setPitch((int) angles[1]);
            }
        }
    }

    @Subscribe
    public void onUpdate(UpdateEvent e) {
        if (pos != null) {
            double[] angles;
            if (!breakingDefense) {
                angles = calculateLookAt(pos.getX(), pos.getY() - 0.5, pos.getZ() + 0.5, mc.thePlayer);
            } else {
                angles = calculateLookAt(pos.getX(), pos.getY(), pos.getZ() + 1, mc.thePlayer);
            }
            if (getSetting("Rotate").getValBoolean()) {
                e.setYaw((int) angles[0]);
                e.setPitch((int) angles[1]);
            }
        }
    }

    public void smashBlock(BlockPos pos) {
        if (!getSetting("Hypixel").getValBoolean()) {
            doBreaking(new BlockPos(pos.getX(), pos.getY(), pos.getZ()));
        } else {
            try {
                BlockPos defense = new BlockPos(pos.getX(), pos.getY() + 1, pos.getZ());
                this.defense = defense;
                MessageUtil.sendMessage(mc.theWorld.getBlockState(defense).getBlock().toString());
            } catch (Exception e) {
                e.printStackTrace();
            }
            breakingDefense = true;
            doBreaking(defense);
            breakingDefense = false;
            //TimeUtil.Timer timer = new TimeUtil.Timer();
            //if (timer.hasReached((long) getSetting("Delay").getValDouble())) {
            doBreaking(new BlockPos(pos.getX(), pos.getY(), pos.getZ()));
            //timer.reset();
            //}
        }
    }

    static void doBreaking(BlockPos pos) {
        final int oldSlot = mc.thePlayer.inventory.currentItem;
        hotkeyToSlot(getBestItem(pos));
        mc.thePlayer.sendQueue.addToSendQueue(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.START_DESTROY_BLOCK, pos, EnumFacing.UP));
        mc.thePlayer.sendQueue.addToSendQueue(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.STOP_DESTROY_BLOCK, pos, EnumFacing.UP));
        mc.thePlayer.sendQueue.addToSendQueue(new C0APacketAnimation());
        hotkeyToSlot(oldSlot);
    }

    public static double[] calculateLookAt(double px, double py, double pz, EntityPlayer me) {
        double dirx = me.posX - px;
        double diry = me.posY - py;
        double dirz = me.posZ - pz;

        double len = Math.sqrt(dirx * dirx + diry * diry + dirz * dirz);

        dirx /= len;
        diry /= len;
        dirz /= len;

        double pitch = Math.asin(diry);
        double yaw = Math.atan2(dirz, dirx);

        pitch = pitch * 180.0d / Math.PI;
        yaw = yaw * 180.0d / Math.PI;

        yaw += 90f;

        return new double[]{yaw, pitch};
    }

    public static void hotkeyToSlot(int slot) {
        if (mc.theWorld == null) return;
        mc.thePlayer.inventory.currentItem = slot;
    }

    private static int getBestItem(final BlockPos blockPos) {
        float bestSpeed = 1F;
        int bestSlot = -1;

        final IBlockState blockState = mc.theWorld.getBlockState(blockPos);

        for (int i = 0; i < 9; i++) {
            final ItemStack item = mc.thePlayer.inventory.getStackInSlot(i);
            if (item == null) continue;

            final float speed = item.getStrVsBlock(blockState.getBlock());

            if (speed > bestSpeed) {
                bestSpeed = speed;
                bestSlot = i;
            }
        }

        return bestSlot;
    }

    @Override
    public void onDisabled() {
        super.onDisabled();
        pos = null;
    }
}