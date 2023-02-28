package me.kambing.gardenia.injection;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(value = Render.class)
public abstract class MixinRender<T extends Entity> {
    @Shadow
    public abstract FontRenderer getFontRendererFromRenderManager();

    /**
     * @author
     */
    @Overwrite
    protected void renderLivingLabel(T entityIn, String str, double x, double y, double z, int maxDistance) {
        if (entityIn instanceof EntityLivingBase) {
            double d0 = entityIn.getDistanceSqToEntity(Minecraft.getMinecraft().getRenderManager().livingPlayer);

            if (!(entityIn.getEntityId() == 696969696)) {
                if (d0 <= (double) (maxDistance * maxDistance)) {
                    FontRenderer fontrenderer = this.getFontRendererFromRenderManager();
                    float f = 1.6F;
                    float f1 = 0.016666668F * f;
                    GlStateManager.pushMatrix();
                    GlStateManager.translate((float) x + 0.0F, (float) y + entityIn.height + 0.5F, (float) z);
                    GL11.glNormal3f(0.0F, 1.0F, 0.0F);
                    GlStateManager.rotate(-Minecraft.getMinecraft().getRenderManager().playerViewY, 0.0F, 1.0F, 0.0F);
                    GlStateManager.rotate(Minecraft.getMinecraft().getRenderManager().playerViewX, 1.0F, 0.0F, 0.0F);
                    GlStateManager.scale(-f1, -f1, f1);
                    GlStateManager.disableLighting();
                    GlStateManager.depthMask(false);
                    GlStateManager.disableDepth();
                    GlStateManager.enableBlend();
                    GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
                    Tessellator tessellator = Tessellator.getInstance();
                    WorldRenderer worldrenderer = tessellator.getWorldRenderer();
                    byte b0 = 0;

                    if (str.equals("deadmau5")) {
                        b0 = -10;
                    }

                    int i = fontrenderer.getStringWidth(str) / 2;
                    GlStateManager.disableTexture2D();
                    worldrenderer.begin(7, DefaultVertexFormats.POSITION_COLOR);
                    worldrenderer.pos(-i - 1, -1 + b0, 0.0D).color(0.0F, 0.0F, 0.0F, 0.25F).endVertex();
                    worldrenderer.pos(-i - 1, 8 + b0, 0.0D).color(0.0F, 0.0F, 0.0F, 0.25F).endVertex();
                    worldrenderer.pos(i + 1, 8 + b0, 0.0D).color(0.0F, 0.0F, 0.0F, 0.25F).endVertex();
                    worldrenderer.pos(i + 1, -1 + b0, 0.0D).color(0.0F, 0.0F, 0.0F, 0.25F).endVertex();
                    tessellator.draw();
                    GlStateManager.enableTexture2D();
                    fontrenderer.drawString(str, -fontrenderer.getStringWidth(str) / 2, b0, 553648127);
                    GlStateManager.enableDepth();
                    GlStateManager.depthMask(true);
                    fontrenderer.drawString(str, -fontrenderer.getStringWidth(str) / 2, b0, -1);
                    GlStateManager.enableLighting();
                    GlStateManager.disableBlend();
                    GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
                    GlStateManager.popMatrix();
                }
            }
        }
    }
}
