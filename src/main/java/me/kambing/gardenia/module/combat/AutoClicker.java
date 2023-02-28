package me.kambing.gardenia.module.combat;


import me.kambing.gardenia.module.Category;
import me.kambing.gardenia.module.Module;
import me.kambing.gardenia.settings.Setting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.item.ItemSword;
import net.minecraft.util.MovingObjectPosition;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.input.Mouse;

import java.security.SecureRandom;

/**
 * @author bypass for losebypass
 * @author rise dev
 * @author kambing for blockhit
 */
public class AutoClicker extends Module {
    public AutoClicker() {
        super("AutoClicker", "a good cps autoclicker with real actual set cps", false, false, Category.Combat);
        new Setting("MaxCPS", this, 8, 1, 20, true);
        new Setting("MinCPS", this, getSetting("MaxCPS").getValDouble(), 1, 20, true);
        new Setting("BlockHit", this, false);
        new Setting("Bypass", this, true);
    }

    SecureRandom random = new SecureRandom();
    private double tick = 0.0;
    private double currentSpeed;


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
        if (mc.objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) return;

        if (!getSetting("Bypass").getValBoolean()) {
            if (mc.currentScreen == null && !mc.thePlayer.isBlocking()) {
                Mouse.poll();

                if (Mouse.isButtonDown(0) && Math.random() * 50 <= minCps() + (random.nextDouble() * ((int) getSetting("MaxCPS").getValDouble() - minCps())) && (mc.thePlayer.ticksExisted % 5 != 0 && mc.thePlayer.ticksExisted % 17 != 0)) {
                    sendClick(0, true);
                    sendClick(0, false);
                    if (Minecraft.getMinecraft().objectMouseOver.entityHit != null) {
                        if (mc.thePlayer.inventory.getCurrentItem() != null && getSetting("BlockHit").getValBoolean()) {
                            if (mc.thePlayer.inventory.getCurrentItem().getItem() instanceof ItemSword) {
                                sendClick(1, true);
                                sendClick(1, false);
                            }
                        }
                    }
                }
            }
        } else {
            if (Mouse.isButtonDown(0)) {
                this.tick += this.currentSpeed / 40.0;
                if (this.tick > 1.0) {
                    this.tick -= 1.0;
                    sendClick(0, true);
                    sendClick(0, false);
                    if (Minecraft.getMinecraft().objectMouseOver.entityHit != null) {
                        if (mc.thePlayer.inventory.getCurrentItem() != null && getSetting("BlockHit").getValBoolean()) {
                            if (mc.thePlayer.inventory.getCurrentItem().getItem() instanceof ItemSword) {
                                sendClick(1, true);
                                sendClick(1, false);
                            }
                        }
                    }
                }
            }
            if (AutoClicker.mc.thePlayer.ticksExisted % (10 + this.random.nextInt(30)) != 0) {
                if (this.currentSpeed != 0.0) return;
            }
            double dif = getSetting("MaxCPS").getValDouble() - minCps();
            dif *= this.random.nextDouble();
            this.currentSpeed = dif += minCps();
        }
    }

    private void sendClick(final int button, final boolean state) {
        final Minecraft mc = Minecraft.getMinecraft();
        final int keyBind = button == 0 ? mc.gameSettings.keyBindAttack.getKeyCode() : mc.gameSettings.keyBindUseItem.getKeyCode();

        KeyBinding.setKeyBindState(button == 0 ? mc.gameSettings.keyBindAttack.getKeyCode() : mc.gameSettings.keyBindUseItem.getKeyCode(), state);

        if (state) {
            KeyBinding.onTick(keyBind);
        }
    }

    int minCps() {
        double cps = getSetting("MinCPS").getValDouble();
        if (cps > (int) getSetting("MaxCPS").getValDouble())
            return (int) (getSetting("MaxCPS").getValDouble());
        return (int) cps;
    }
}
