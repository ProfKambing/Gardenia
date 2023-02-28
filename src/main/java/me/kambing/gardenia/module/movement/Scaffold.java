package me.kambing.gardenia.module.movement;

import com.google.common.eventbus.Subscribe;
import me.kambing.gardenia.UpdateEvent;
import me.kambing.gardenia.module.Category;
import me.kambing.gardenia.module.Module;
import me.kambing.gardenia.settings.Setting;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.network.play.client.C0BPacketEntityAction;
import net.minecraft.network.play.client.C0BPacketEntityAction.Action;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.input.Keyboard;

import java.util.Arrays;
import java.util.List;

public class Scaffold extends Module {
    private boolean finishedRotatingYaw, finishedRotatingPitch;
    private boolean spoofingSneak;

    private float startingYaw, spoofedYaw, spoofedPitch;

    public Scaffold() {
        super("Scaffold", "work in progress, literally godbridge that works for u", false, false, Category.Movement);
        new Setting("ManualPress", this, false);
    }

    public static final List<Block> blockBlacklist = Arrays.asList(Blocks.air, Blocks.water, Blocks.tnt, Blocks.chest,
            Blocks.flowing_water, Blocks.lava, Blocks.flowing_lava, Blocks.tnt, Blocks.enchanting_table, Blocks.carpet,
            Blocks.glass_pane, Blocks.stained_glass_pane, Blocks.iron_bars, Blocks.snow_layer, Blocks.ice,
            Blocks.packed_ice, Blocks.coal_ore, Blocks.diamond_ore, Blocks.emerald_ore, Blocks.chest, Blocks.torch,
            Blocks.anvil, Blocks.trapped_chest, Blocks.noteblock, Blocks.jukebox, Blocks.tnt, Blocks.gold_ore,
            Blocks.iron_ore, Blocks.lapis_ore, Blocks.sand, Blocks.lit_redstone_ore, Blocks.quartz_ore,
            Blocks.redstone_ore, Blocks.wooden_pressure_plate, Blocks.stone_pressure_plate,
            Blocks.light_weighted_pressure_plate, Blocks.heavy_weighted_pressure_plate, Blocks.stone_button,
            Blocks.wooden_button, Blocks.lever, Blocks.enchanting_table, Blocks.red_flower, Blocks.double_plant,
            Blocks.yellow_flower, Blocks.web);

    @Override
    public void onEnabled() {
        super.onEnabled();
        if (mc.thePlayer == null) return;
        finishedRotatingYaw = finishedRotatingPitch = false;
        startingYaw = mc.thePlayer.rotationYaw;
        spoofedPitch = mc.thePlayer.rotationPitch;

        spoofedYaw = (float) MathHelper.wrapAngleTo180_double(startingYaw);
    }

    @Override
    public void onDisabled() {
        super.onDisabled();
        if (mc.thePlayer == null) return;
        mc.thePlayer.rotationYaw = startingYaw;
        mc.thePlayer.rotationPitch = 10;

        if (spoofingSneak) {
            mc.getNetHandler().getNetworkManager().sendPacket(new C0BPacketEntityAction(mc.thePlayer, Action.STOP_SNEAKING));
            spoofingSneak = false;
        }

        try {
            FastBridge.pressed.set(mc.gameSettings.keyBindUseItem, false);
            FastBridge.pressed.set(mc.gameSettings.keyBindForward, Keyboard.isKeyDown(mc.gameSettings.keyBindForward.getKeyCode()));
            FastBridge.pressed.set(mc.gameSettings.keyBindBack, Keyboard.isKeyDown(mc.gameSettings.keyBindBack.getKeyCode()));
            FastBridge.pressed.set(mc.gameSettings.keyBindSneak, Keyboard.isKeyDown(mc.gameSettings.keyBindSneak.getKeyCode()));
            FastBridge.pressed.set(mc.gameSettings.keyBindSneak, false);
        } catch (Exception ignored) {
        }
    }

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent e) {
        if (mc.thePlayer == null) return;
        //if (mc.thePlayer.limbSwing > 0) {
         //   rotate();
        //}

        if (this.isToggled()) {
            try {
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

                BlockPos pos = new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY - 1, mc.thePlayer.posZ);
                if (mc.theWorld.getBlockState(pos).getBlock() instanceof BlockAir) {
                    FastBridge.pressed.set(mc.gameSettings.keyBindSneak, false);
                    FastBridge.pressed.set(mc.gameSettings.keyBindUseItem, true);
                    if (!spoofingSneak) {
                        mc.getNetHandler().getNetworkManager().sendPacket(new C0BPacketEntityAction(mc.thePlayer, Action.START_SNEAKING));
                        spoofingSneak = true;
                    }
                    mc.thePlayer.motionX *= 0.7;
                    mc.thePlayer.motionZ *= 0.7;
                } else {
                    if (spoofingSneak) {
                        mc.getNetHandler().getNetworkManager().sendPacket(new C0BPacketEntityAction(mc.thePlayer, Action.STOP_SNEAKING));
                        spoofingSneak = false;
                    }
                    FastBridge.pressed.set(mc.gameSettings.keyBindSneak, false);
                    FastBridge.pressed.set(mc.gameSettings.keyBindUseItem, false);
                }

                //mc.thePlayer.rotationYaw = startingYaw - 180;
                //mc.thePlayer.rotationPitch = isGoingDiagonally() ? 78F : 81.5F;
                FastBridge.pressed.set(mc.gameSettings.keyBindForward, false);
                FastBridge.pressed.set(mc.gameSettings.keyBindBack, getSetting("ManualPress").getValBoolean() || Keyboard.isKeyDown(Keyboard.KEY_W));
            } catch (Exception ignored) {
            }
        }
    }
    private void rotate(UpdateEvent.LookEvent event) {
        float yaw = this.isToggled() ? (float) MathHelper.wrapAngleTo180_double(startingYaw - 135) : startingYaw;

        double turnSpeed = 30;

        if (this.isToggled()) {
            if (Math.abs(yaw - spoofedYaw) < turnSpeed || (Math.abs(yaw - spoofedYaw) > 360 - turnSpeed)) {
                spoofedYaw = yaw;
            } else {
                if (Math.abs(yaw - spoofedYaw) < 180) {
                    if (spoofedYaw > yaw) {
                        spoofedYaw -= turnSpeed;
                    } else {
                        spoofedYaw += turnSpeed;
                    }
                } else {
                    if (spoofedYaw > yaw) {
                        spoofedYaw += turnSpeed;
                    } else {
                        spoofedYaw -= turnSpeed;
                    }
                }
            }
        } else if (!finishedRotatingYaw) {
            if (Math.abs(yaw - spoofedYaw) < turnSpeed || (Math.abs(yaw - spoofedYaw) > 360 - turnSpeed)) {
                spoofedYaw = yaw;
                finishedRotatingYaw = true;
            } else {
                if (Math.abs(yaw - spoofedYaw) < 180) {
                    if (spoofedYaw > yaw) {
                        spoofedYaw -= turnSpeed;
                    } else {
                        spoofedYaw += turnSpeed;
                    }
                } else {
                    if (spoofedYaw > yaw) {
                        spoofedYaw += turnSpeed;
                    } else {
                        spoofedYaw -= turnSpeed;
                    }
                }
            }
        }

        if (this.isToggled()) {
            spoofedPitch += 20;
            if (spoofedPitch > (isGoingDiagonally() ? 78F : 81.5F)) {
                spoofedPitch = isGoingDiagonally() ? 78F : 81.5F;
            }
        } else if (!finishedRotatingPitch) {
            spoofedPitch -= 20;
            if (spoofedPitch > mc.thePlayer.rotationYaw) {
                spoofedPitch = mc.thePlayer.rotationYaw;
                finishedRotatingPitch = true;
            }
        }

        if (this.isToggled() || !finishedRotatingYaw) {
            mc.thePlayer.rotationYawHead = spoofedYaw;
            mc.thePlayer.renderYawOffset = spoofedYaw;
            event.setYaw(spoofedYaw);
        }
    }
    @Subscribe
    public void onLook(UpdateEvent.LookEvent e) {
        if (!isToggled()) return;
        if (Keyboard.isKeyDown(Keyboard.KEY_W)) {
            rotate(e);
            e.setPitch(isGoingDiagonally() ? 78F : 81.5F);
        }
    }

    public static boolean isGoingDiagonally() {
        return Math.abs(mc.thePlayer.motionX) > 0.08 && Math.abs(mc.thePlayer.motionZ) > 0.08;
    }

}
