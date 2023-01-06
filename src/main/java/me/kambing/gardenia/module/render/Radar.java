package me.kambing.gardenia.module.render;

import me.kambing.gardenia.module.Category;
import me.kambing.gardenia.module.Module;
import me.kambing.gardenia.settings.Setting;
import me.kambing.gardenia.utils.RenderUtil;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.opengl.GL11;

import java.awt.*;

import static me.kambing.gardenia.utils.EntityUtil.isTeam;

public class Radar extends Module {

    public Radar() {
        super("Radar", "draw radar to fuck up ur niggers", false, false, Category.Render);
        new Setting("ScaleX", this, 100, 100, 300,true);
        new Setting("ScaleY", this, 100, 100, 300,true);
        new Setting("SelfColor", this, Color.GREEN);
        new Setting("EntitiesColor", this, Color.RED);
        new Setting("RectColor", this, new Color(0,0,0,40));
        new Setting("TeamColor", this, Color.GREEN);
    }

    @SubscribeEvent
    public void onRender(TickEvent.RenderTickEvent e) {
        if (e.phase != TickEvent.Phase.END) {
            return;
        }
        if (mc.theWorld == null || mc.thePlayer == null) {
            return;
        }
        if (mc.gameSettings.showDebugInfo || mc.currentScreen != null) {
            return;
        }

        int miX = 5, miY = 70;
        int maX = miX + (int)getSetting("ScaleX").getValDouble(), maY = miY + (int)getSetting("ScaleY").getValDouble();
        Gui.drawRect(miX, miY, maX, maY, getSetting("RectColor").getColor().getRGB());
        // drawing horizontal lines
        Gui.drawRect(miX - 1, miY - 1, maX + 1, miY, -1); // top
        Gui.drawRect(miX - 1, maY, maX + 1, maY + 1, -1); // bottom
        // drawing vertical lines
        Gui.drawRect(miX - 1, miY, miX, maY, -1); // left
        Gui.drawRect(maX, miY, maX + 1, maY, -1); // right
        RenderUtil.draw2DPolygon(maX / 2 + 3, miY + 52, 5f, 3, getSetting("SelfColor").getColor().getRGB()); // self
        GL11.glPushMatrix();
        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        int scale = new ScaledResolution(mc).getScaleFactor();
        GL11.glScissor(miX * scale, mc.displayHeight - scale * 170, maX * scale - (scale * 5), scale * 100);

        for (Entity en : mc.theWorld.loadedEntityList) {
            if (!(en instanceof EntityPlayer)) {
                continue;
            }
            if (en == mc.thePlayer) {
                continue;
            }
            if (en.getDisplayName().getUnformattedText().contains("[NPC] ")) continue;
            double dist_sq = mc.thePlayer.getDistanceSqToEntity(en);
            if (dist_sq > 360) {
                continue;
            }
            double x = en.posX - mc.thePlayer.posX, z = en.posZ - mc.thePlayer.posZ;
            double calc = Math.atan2(x, z) * 57.2957795131f;
            double angle = ((mc.thePlayer.rotationYaw + calc) % 360) * 0.01745329251f;
            double hypotenuse = dist_sq / 5;
            double x_shift = hypotenuse * Math.sin(angle), y_shift = hypotenuse * Math.cos(angle);
            RenderUtil.draw2DPolygon(maX / 2 + 3 - x_shift, miY + 52 - y_shift, 3f, 4, isTeam(mc.thePlayer,en) ? getSetting("TeamColor").getColor().getRGB() : getSetting("EntitiesColor").getColor().getRGB());
        }

        GL11.glDisable(GL11.GL_SCISSOR_TEST);
        GL11.glPopMatrix();
    }

}