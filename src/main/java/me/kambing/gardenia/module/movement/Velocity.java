package me.kambing.gardenia.module.movement;

import me.kambing.gardenia.module.Category;
import me.kambing.gardenia.module.Module;
import me.kambing.gardenia.settings.Setting;
import net.minecraft.network.Packet;
import org.apache.commons.lang3.RandomUtils;

public class Velocity extends Module {
    public Velocity(){
        super("Velocity", "modifies velocity", false, false, Category.Movement);
        new Setting("Horizontal", this, 90, 10, 100, true);
        new Setting("Chance%", this, 80, 40, 100, true);
    }

    @Override
    public void onPacketReceived(Packet<?> packet) {
        if (mc.thePlayer == null) {
            return;
        }
        float horizontal = (float)getSetting("Horizontal").getValDouble();

        if (getSetting("Chance%").getValDouble() > RandomUtils.nextInt(1, 100)) {
            if (packet instanceof net.minecraft.network.play.server.S12PacketEntityVelocity || packet instanceof net.minecraft.network.play.server.S27PacketExplosion) {
                if (mc.thePlayer.hurtTime > 0) {
                    mc.thePlayer.motionX *= horizontal / 100;
                    mc.thePlayer.motionZ *= horizontal / 100;
                }
            }
        }
    }
}
