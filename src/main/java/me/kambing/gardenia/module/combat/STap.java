package me.kambing.gardenia.module.combat;


import me.kambing.gardenia.module.Category;
import me.kambing.gardenia.module.Module;
import me.kambing.gardenia.settings.Setting;
import me.kambing.gardenia.utils.TimeUtil;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.input.Mouse;

import static me.kambing.gardenia.utils.EntityUtil.isTeam;
import static me.kambing.gardenia.utils.TimeUtil.getCurrentMS;

public class STap extends Module {

    private long lastHold = 2000000000L;

    public STap() {
        super("STap", "stop sprinting so u could combo", false, false, Category.Combat);
        new Setting("Delay", this, 500, 50, 2000, false);
        new Setting("Held", this, 100, 5, 250, false);
    }

    protected boolean isTargetValid(Entity entity) {
        if (entity == null) {
            return false;
        }
        if (entity.isDead) {
            return false;
        }
        if (isTeam(mc.thePlayer, entity)) {
            return false;
        }
        float range = 4f;
        return !(mc.thePlayer.getDistanceToEntity(entity) > range);
    }

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

        Entity ens = null;

        if (mc.objectMouseOver != null && mc.objectMouseOver.entityHit != null && Mouse.isButtonDown(0)) {
            if (mc.objectMouseOver.entityHit != mc.thePlayer && isTargetValid(mc.objectMouseOver.entityHit)) {
                if (mc.thePlayer.isSwingInProgress) {
                    ens = mc.objectMouseOver.entityHit;
                }
            }
        }

        if (ens != null && mc.thePlayer.isSprinting() && TimeUtil.hasTimePassedMS((long) getSetting("Delay").getValDouble())) {
            KeyBinding.setKeyBindState(mc.gameSettings.keyBindBack.getKeyCode(), true);
            this.lastHold = getCurrentMS();
            TimeUtil.reset();
        }
        if (this.lastHold != -1L && TimeUtil.hasTimePassedMS(this.lastHold, (long) getSetting("Held").getValDouble())) {
            KeyBinding.setKeyBindState(mc.gameSettings.keyBindBack.getKeyCode(), false);
            this.lastHold = -1L;
        }
    }

    @Override
    public void onDisabled() {
        super.onDisabled();
        KeyBinding.setKeyBindState(mc.gameSettings.keyBindBack.getKeyCode(), false);
    }
}