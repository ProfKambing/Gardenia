package me.kambing.gardenia.module.render.deatheffects;

import com.mojang.authlib.GameProfile;
import me.kambing.gardenia.Gardenia;
import me.kambing.gardenia.module.Category;
import me.kambing.gardenia.module.Module;
import me.kambing.gardenia.settings.Setting;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MathHelper;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.ArrayList;
import java.util.UUID;

import static org.lwjgl.opengl.GL11.*;

public class DeathEffects extends Module {
    protected final ArrayList<PopData> popEntities = new ArrayList<>();

    public DeathEffects() {
        super("DeathEffects", "cool effects when u kill somebody", false, false, Category.Render);
        new Setting("Color", this, new Color(0x7E24BB8D, true));
        new Setting("OutlineColor", this, new Color(0x24BB8D));
        new Setting("FadeTime", this, 1500, 0, 5000, true);
        new Setting("YAnimation", this, 3, -7, 7, true);
    }

    Entity target;

    @SubscribeEvent
    public void onRenderWorld(RenderWorldLastEvent event) {
        if (!Gardenia.instance.moduleManager.twoDESP.isToggled()) {
            onRenderWorldLast(event);
        }
    }

    @SubscribeEvent
    public void onAttackEntity(AttackEntityEvent event) {
        if (event.target instanceof EntityLivingBase && ((EntityLivingBase) event.target).getHealth() > 0) {
            if (event.target instanceof EntityPlayer) {
                target = event.target;
            }
        }
    }

    public void onRenderWorldLast(RenderWorldLastEvent event) {
        if (target != null && !mc.theWorld.loadedEntityList.contains(target) && mc.thePlayer.getDistanceSqToEntity(target) < 100 && target.isDead) {
            if (mc.thePlayer.ticksExisted > 3) {
                EntityPlayer entity = (EntityPlayer) target;
                popEntities.add(new PopData(copyPlayer(entity, true), System.currentTimeMillis(),
                        entity.posX,
                        entity.posY,
                        entity.posZ, event.partialTicks, entity instanceof AbstractClientPlayer && ((AbstractClientPlayer) entity).getSkinType().equals("slim")));
            }
        }
        for (final PopData data : popEntities) {
            EntityPlayer player = data.getPlayer();
            StaticModelPlayer model = data.getModel();
            double x = data.getX() - mc.getRenderManager().viewerPosX;
            double y = data.getY() - mc.getRenderManager().viewerPosY;
            y += getSetting("YAnimation").getValDouble() * (System.currentTimeMillis() - data.getTime()) / getSetting("FadeTime").getValDouble();
            double z = data.getZ() - mc.getRenderManager().viewerPosZ;

            GlStateManager.pushMatrix();
            startRender();

            GlStateManager.translate(x, y, z);
            GlStateManager.rotate(180 - model.getYaw(), 0, 1, 0);

            final Color boxColor = getSetting("Color").getColor();
            final Color outlineColor = getSetting("OutlineColor").getColor();
            final float maxBoxAlpha = boxColor.getAlpha();
            final float maxOutlineAlpha = outlineColor.getAlpha();
            final float alphaBoxAmount = maxBoxAlpha / 1500;
            final float alphaOutlineAmount = maxOutlineAlpha / 1500;
            final int fadeBoxAlpha = MathHelper.clamp_int((int) (alphaBoxAmount * (data.getTime() + getSetting("FadeTime").getValDouble() - System.currentTimeMillis())), 0, (int) maxBoxAlpha);
            final int fadeOutlineAlpha = MathHelper.clamp_int((int) (alphaOutlineAmount * (data.getTime() + getSetting("FadeTime").getValDouble() - System.currentTimeMillis())), 0, (int) maxOutlineAlpha);

            Color box = new Color(boxColor.getRed(), boxColor.getGreen(), boxColor.getBlue(), fadeBoxAlpha);
            Color out = new Color(outlineColor.getRed(), outlineColor.getGreen(), outlineColor.getBlue(), fadeOutlineAlpha);

            GlStateManager.enableRescaleNormal();
            GlStateManager.scale(-1.0F, -1.0F, 1.0F);
            double widthX = player.getEntityBoundingBox().maxX - player.getEntityBoundingBox().minX + 1;
            double widthZ = player.getEntityBoundingBox().maxZ - player.getEntityBoundingBox().minZ + 1;

            GlStateManager.scale(widthX, player.height, widthZ);

            GlStateManager.translate(0.0F, -1.501F, 0.0F);

            color(box);
            GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL);
            model.render(0.0625f);

            color(out);
            GL11.glLineWidth(1f);
            GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE);
            model.render(0.0625f);


            endRender();
            GlStateManager.popMatrix();
            break;

        }
        popEntities.removeIf(e -> e.getTime() + getSetting("FadeTime").getValDouble() < System.currentTimeMillis());
    }

    public static void startRender() {
        glPushAttrib(GL_ALL_ATTRIB_BITS);
        glPushMatrix();
        glDisable(GL_ALPHA_TEST);
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glDisable(GL_TEXTURE_2D);
        glDisable(GL_DEPTH_TEST);
        glDepthMask(false);
        glEnable(GL_CULL_FACE);
        glEnable(GL_LINE_SMOOTH);
        glHint(GL_LINE_SMOOTH_HINT, GL_FASTEST);
        glDisable(GL_LIGHTING);
    }

    public static void endRender() {
        glEnable(GL_LIGHTING);
        glDisable(GL_LINE_SMOOTH);
        glEnable(GL_TEXTURE_2D);
        glEnable(GL_DEPTH_TEST);
        glDisable(GL_BLEND);
        glEnable(GL_ALPHA_TEST);
        glDepthMask(true);
        glCullFace(GL_BACK);
        glPopMatrix();
        glPopAttrib();
    }

    public static void color(Color color) {
        glColor4f(color.getRed() / 255.0f,
                color.getGreen() / 255.0f,
                color.getBlue() / 255.0f,
                color.getAlpha() / 255.0f);
    }

    public static class PopData {
        private final EntityPlayer player;
        private final StaticModelPlayer model;
        private final long time;
        private final double x;
        private final double y;
        private final double z;

        public PopData(EntityPlayer player, long time, double x, double y, double z, float partialTicks, boolean slim) {
            this.player = player;
            this.time = time;
            this.x = x;
            this.y = y - (player.isSneaking() ? 0.125 : 0);
            this.z = z;
            this.model = new StaticModelPlayer(player, 0, partialTicks, slim);
            this.model.disableArmorLayers();
        }

        public EntityPlayer getPlayer() {
            return player;
        }

        public long getTime() {
            return time;
        }

        public double getX() {
            return x;
        }

        public double getY() {
            return y;
        }

        public double getZ() {
            return z;
        }

        public StaticModelPlayer getModel() {
            return model;
        }
    }

    public static EntityPlayer copyPlayer(EntityPlayer playerIn, boolean animations) {
        int count = playerIn.getItemInUseCount();
        EntityPlayer copy = new EntityPlayer(mc.theWorld, new GameProfile(UUID.randomUUID(), playerIn.getName())) {
            @Override
            public boolean isSpectator() {
                return false;
            }

            @Override
            public int getItemInUseCount() {
                return count;
            }
        };
        if (animations) {
            copy.setSneaking(playerIn.isSneaking());
            copy.swingProgress = playerIn.swingProgress;
            copy.limbSwing = playerIn.limbSwing;
            copy.limbSwingAmount = playerIn.prevLimbSwingAmount;
            copy.inventory.copyInventory(playerIn.inventory);
        }
        copy.ticksExisted = playerIn.ticksExisted;
        copy.setEntityId(playerIn.getEntityId());
        copy.copyLocationAndAnglesFrom(playerIn);
        return copy;
    }
}
