package me.kambing.gardenia.module.render;

import me.kambing.gardenia.module.Category;
import me.kambing.gardenia.module.Module;
import me.kambing.gardenia.settings.Setting;
import me.kambing.gardenia.utils.RenderUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.awt.*;

import static me.kambing.gardenia.utils.EntityUtil.isTeam;

public class PlayerESP extends Module {

    public PlayerESP() {
        super("PlayerESP", "Renders players in a dif way", false, false, Category.Render);
        new Setting("Color", this, Color.RED);
        new Setting("TeamColor", this, Color.GREEN);
        new Setting("Rainbow", this, false);
        new Setting("2D", this, false);
        new Setting("Arrow", this, false);
        new Setting("Box", this, false);
        new Setting("Health", this, true);
        new Setting("Ring", this, false);
        new Setting("Shaded", this, false);
        new Setting("Expand", this, 0.0D, -0.3D, 2.0D, true);
        new Setting("X-Shift", this, 0.0D, -35.0D, 10.0D, true);
        new Setting("ShowInvis", this, true);
        new Setting("RedOnDamage", this, true);
    }

    @SubscribeEvent
    public void onRenderWorldLast(RenderWorldLastEvent event) {
        for (EntityPlayer entityPlayer : mc.theWorld.playerEntities) {
            if (entityPlayer.equals(mc.thePlayer)) continue;
            if (entityPlayer.getDisplayName().getUnformattedText().contains("[NPC] ")) continue;
            render(entityPlayer, isTeam(mc.thePlayer, entityPlayer) ? getSetting("TeamColor").getColor().getRGB() : getColor().getRGB(), (int) event.partialTicks);
        }
    }
    private void render(Entity en, int rgb, float partialTicks) {
        if (getSetting("Box").getValBoolean()) {
            RenderUtil.drawBoxAroundEntity(en, 1, getSetting("Expand").getValDouble(), getSetting("Expand").getValDouble(), rgb, getSetting("RedOnDamage").getValBoolean(), partialTicks);
        }

        if (getSetting("Shaded").getValBoolean()) {
            RenderUtil.drawBoxAroundEntity(en, 2, getSetting("Expand").getValDouble(), getSetting("Expand").getValDouble(), rgb, getSetting("RedOnDamage").getValBoolean(), partialTicks);
        }

        if (getSetting("2D").getValBoolean()) {
            RenderUtil.drawBoxAroundEntity(en, 3, getSetting("Expand").getValDouble(), getSetting("Expand").getValDouble(), rgb, getSetting("RedOnDamage").getValBoolean(), partialTicks);
        }

        if (getSetting("Health").getValBoolean()) {
            RenderUtil.drawBoxAroundEntity(en, 4, getSetting("Expand").getValDouble(), getSetting("Expand").getValDouble(), rgb, getSetting("RedOnDamage").getValBoolean(), partialTicks);
        }

        if (getSetting("Arrow").getValBoolean()) {
            RenderUtil.drawBoxAroundEntity(en, 5, getSetting("Expand").getValDouble(), getSetting("Expand").getValDouble(), rgb, getSetting("RedOnDamage").getValBoolean(), partialTicks);
        }

        if (getSetting("Ring").getValBoolean()) {
            RenderUtil.drawBoxAroundEntity(en, 6, getSetting("Expand").getValDouble(), getSetting("Expand").getValDouble(), rgb, getSetting("RedOnDamage").getValBoolean(), partialTicks);
        }

    }
    public Color getColor() {
        return getSetting("Rainbow").getValBoolean() ? RenderUtil.rainbow(50) : getSetting("Color").getColor();
    }
}
