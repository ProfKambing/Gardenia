package me.kambing.gardenia.utils;

import me.kambing.gardenia.Gardenia;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.Vec3;
import org.lwjgl.opengl.GL11;

import java.awt.Color;
import java.text.NumberFormat;

import static me.kambing.gardenia.module.render.Trajectories.rect;
import static net.minecraft.client.renderer.GlStateManager.*;
import static org.lwjgl.opengl.GL11.GL_LINES;

public class RenderUtil {
    private static final Frustum frustrum = new Frustum();
    public static Minecraft mc = Minecraft.getMinecraft();

    public static Color rainbow(final int delay) {
        double rainbowState = Math.ceil((System.currentTimeMillis() + delay) / 20.0);
        rainbowState %= 360.0;
        return Color.getHSBColor((float) (rainbowState / 360.0), 0.5f, 1.0f);
    }

    public static Color alphaStep(Color color, int index, int count) {
        float[] hsb = new float[3];
        Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), hsb);
        float brightness = Math.abs(((float) (System.currentTimeMillis() % 2000L) / 1000.0F + (float) index / (float) count * 2.0F) % 2.0F - 1.0F);
        brightness = 0.5F + 0.5F * brightness;
        hsb[2] = brightness % 2.0F;
        return new Color(Color.HSBtoRGB(hsb[0], hsb[1], hsb[2]));
    }

    public static int astolfo(int offset) {
        int i = (int) ((System.currentTimeMillis() / 11 + offset) % 360);
        i = (i > 180 ? 360 - i : i) + 180;
        return Color.HSBtoRGB(i / 360f, 0.55f, 1f);
    }

    public static Color interpolateColorsBackAndForth(int speed, int index, Color start, Color end, boolean trueColor) {
        int angle = (int) (((System.currentTimeMillis()) / speed + index) % 360);
        angle = (angle >= 180 ? 360 - angle : angle) * 2;
        return trueColor ? interpolateColorHue(start, end, angle / 360f) : interpolateColorC(start, end, angle / 360f);
    }

    public static Color interpolateColorC(Color color1, Color color2, float amount) {
        amount = Math.min(1, Math.max(0, amount));
        return new Color(interpolateInt(color1.getRed(), color2.getRed(), amount),
                interpolateInt(color1.getGreen(), color2.getGreen(), amount),
                interpolateInt(color1.getBlue(), color2.getBlue(), amount),
                interpolateInt(color1.getAlpha(), color2.getAlpha(), amount));
    }

    public static Color interpolateColorHue(Color color1, Color color2, float amount) {
        amount = Math.min(1, Math.max(0, amount));

        float[] color1HSB = Color.RGBtoHSB(color1.getRed(), color1.getGreen(), color1.getBlue(), null);
        float[] color2HSB = Color.RGBtoHSB(color2.getRed(), color2.getGreen(), color2.getBlue(), null);

        Color resultColor = Color.getHSBColor(interpolateFloat(color1HSB[0], color2HSB[0], amount),
                interpolateFloat(color1HSB[1], color2HSB[1], amount), interpolateFloat(color1HSB[2], color2HSB[2], amount));

        return new Color(resultColor.getRed(), resultColor.getGreen(), resultColor.getBlue(),
                interpolateInt(color1.getAlpha(), color2.getAlpha(), amount));
    }

    public static Double interpolate(double oldValue, double newValue, double interpolationValue) {
        return (oldValue + (newValue - oldValue) * interpolationValue);
    }

    public static float interpolateFloat(float oldValue, float newValue, double interpolationValue) {
        return interpolate(oldValue, newValue, (float) interpolationValue).floatValue();
    }

    public static int interpolateInt(int oldValue, int newValue, double interpolationValue) {
        return interpolate(oldValue, newValue, (float) interpolationValue).intValue();
    }

    public static Color syncColor() {
        return Gardenia.instance.moduleManager.color.getSetting("Color").getColor();
    }

    public static void re(BlockPos bp, int color, boolean shade) {
        if (bp != null) {
            double x = (double) bp.getX() - mc.getRenderManager().viewerPosX;
            double y = (double) bp.getY() - mc.getRenderManager().viewerPosY;
            double z = (double) bp.getZ() - mc.getRenderManager().viewerPosZ;
            GL11.glBlendFunc(770, 771);
            GL11.glEnable(3042);
            GL11.glLineWidth(2.0F);
            GL11.glDisable(3553);
            GL11.glDisable(2929);
            GL11.glDepthMask(false);
            float a = (float) ((color >> 24) & 255) / 255.0F;
            float r = (float) ((color >> 16) & 255) / 255.0F;
            float g = (float) ((color >> 8) & 255) / 255.0F;
            float b = (float) (color & 255) / 255.0F;
            GL11.glColor4d(r, g, b, a);
            RenderGlobal.drawSelectionBoundingBox(new AxisAlignedBB(x, y, z, x + 1.0D, y + 1.0D, z + 1.0D));
            if (shade)
                dbb(new AxisAlignedBB(x, y, z, x + 1.0D, y + 1.0D, z + 1.0D), r, g, b);
            GL11.glEnable(3553);
            GL11.glEnable(2929);
            GL11.glDepthMask(true);
            GL11.glDisable(3042);
        }
    }


    public static void renderGradientRectLeftRight(final int left, final int top, final int right, final int bottom, final int startColor, final int endColor) {
        final float f = (float) (startColor >> 24 & 255) / 255.0F;
        final float f1 = (float) (startColor >> 16 & 255) / 255.0F;
        final float f2 = (float) (startColor >> 8 & 255) / 255.0F;
        final float f3 = (float) (startColor & 255) / 255.0F;
        final float f4 = (float) (endColor >> 24 & 255) / 255.0F;
        final float f5 = (float) (endColor >> 16 & 255) / 255.0F;
        final float f6 = (float) (endColor >> 8 & 255) / 255.0F;
        final float f7 = (float) (endColor & 255) / 255.0F;
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.disableAlpha();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.shadeModel(GL11.GL_SMOOTH);
        final Tessellator tessellator = Tessellator.getInstance();
        final WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        worldrenderer.begin(7, DefaultVertexFormats.POSITION_COLOR);
        worldrenderer.pos(right, bottom, 0f).color(f5, f6, f7, f4).endVertex();
        worldrenderer.pos(right, top, 0f).color(f5, f6, f7, f4).endVertex();
        worldrenderer.pos(left, top, 0f).color(f1, f2, f3, f).endVertex();
        worldrenderer.pos(left, bottom, 0f).color(f1, f2, f3, f).endVertex();
        tessellator.draw();
        GlStateManager.shadeModel(GL11.GL_FLAT);
        GlStateManager.disableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.enableTexture2D();
    }
    public static void renderEntity(final Entity entity, final float partialTicks) {
        if (mc.thePlayer != null && mc.theWorld != null && entity != null) {
            if (entity.ticksExisted == 0) {
                entity.lastTickPosX = entity.posX;
                entity.lastTickPosY = entity.posY;
                entity.lastTickPosZ = entity.posZ;
            }
            final double x = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * partialTicks;
            final double y = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * partialTicks;
            final double z = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * partialTicks;
            final float yaw = entity.prevRotationYaw + (entity.rotationYaw - entity.prevRotationYaw) * partialTicks;
            final float l = 65536.0f;
            final int i = entity.getBrightnessForRender(partialTicks);
            OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, i % l, i / l);
            try {
                mc.getRenderManager().doRenderEntity(entity, x - mc.getRenderManager().viewerPosX, y - mc.getRenderManager().viewerPosY, z - mc.getRenderManager().viewerPosZ, yaw, partialTicks, false);
            } catch (Exception ignored) {
            }
        }
    }

    public static void drawBoxAroundEntity(Entity e, int type, double expand, double shift, int color, boolean damage, float partialTicks) {
        if (e instanceof EntityLivingBase) {
            double x = e.lastTickPosX + (e.posX - e.lastTickPosX) * (double) partialTicks - mc.getRenderManager().viewerPosX;
            double y = e.lastTickPosY + (e.posY - e.lastTickPosY) * (double) partialTicks - mc.getRenderManager().viewerPosY;
            double z = e.lastTickPosZ + (e.posZ - e.lastTickPosZ) * (double) partialTicks - mc.getRenderManager().viewerPosZ;
            float d = (float) expand / 40.0F;
            if (e instanceof EntityPlayer && damage && ((EntityPlayer) e).hurtTime != 0) {
                color = Color.RED.getRGB();
            }

            GlStateManager.pushMatrix();
            if (type == 3) {
                GL11.glTranslated(x, y - 0.2D, z);
                GL11.glRotated(-mc.getRenderManager().playerViewY, 0.0D, 1.0D, 0.0D);
                GlStateManager.disableDepth();
                GL11.glScalef(0.03F + d, 0.03F + d, 0.03F + d);
                int outline = Color.black.getRGB();
                net.minecraft.client.gui.Gui.drawRect(-20, -1, -26, 75, outline);
                net.minecraft.client.gui.Gui.drawRect(20, -1, 26, 75, outline);
                net.minecraft.client.gui.Gui.drawRect(-20, -1, 21, 5, outline);
                net.minecraft.client.gui.Gui.drawRect(-20, 70, 21, 75, outline);
                if (color != 0) {
                    net.minecraft.client.gui.Gui.drawRect(-21, 0, -25, 74, color);
                    net.minecraft.client.gui.Gui.drawRect(21, 0, 25, 74, color);
                    net.minecraft.client.gui.Gui.drawRect(-21, 0, 24, 4, color);
                    net.minecraft.client.gui.Gui.drawRect(-21, 71, 25, 74, color);
                } else {
                    int st = rainbowDraw(2L, 0L);
                    int en = rainbowDraw(2L, 1000L);
                    dGR(-21, 0, -25, 74, st, en);
                    dGR(21, 0, 25, 74, st, en);
                    net.minecraft.client.gui.Gui.drawRect(-21, 0, 21, 4, en);
                    net.minecraft.client.gui.Gui.drawRect(-21, 71, 21, 74, st);
                }

                GlStateManager.enableDepth();
            } else {
                int i;
                if (type == 4) {
                    EntityLivingBase en = (EntityLivingBase) e;
                    double r = en.getHealth() / en.getMaxHealth();
                    int b = (int) (74.0D * r);
                    int hc = r < 0.3D ? Color.red.getRGB() : (r < 0.5D ? Color.orange.getRGB() : (r < 0.7D ? Color.yellow.getRGB() : Color.green.getRGB()));
                    GL11.glTranslated(x, y - 0.2D, z);
                    GL11.glRotated(-mc.getRenderManager().playerViewY, 0.0D, 1.0D, 0.0D);
                    GlStateManager.disableDepth();
                    GL11.glScalef(0.03F + d, 0.03F + d, 0.03F + d);
                    i = (int) (21.0D + shift * 2.0D);
                    net.minecraft.client.gui.Gui.drawRect(i, -1, i + 5, 75, Color.black.getRGB());
                    net.minecraft.client.gui.Gui.drawRect(i + 1, b, i + 4, 74, Color.darkGray.getRGB());
                    net.minecraft.client.gui.Gui.drawRect(i + 1, 0, i + 4, b, hc);
                    GlStateManager.enableDepth();
                } else if (type == 6) {
                    d3p(x, y, z, 0.699999988079071D, 45, 1.5F, color, color == 0);
                } else {
                    if (color == 0) {
                        color = rainbowDraw(2L, 0L);
                    }

                    float a = (float) (color >> 24 & 255) / 255.0F;
                    float r = (float) (color >> 16 & 255) / 255.0F;
                    float g = (float) (color >> 8 & 255) / 255.0F;
                    float b = (float) (color & 255) / 255.0F;
                    if (type == 5) {
                        GL11.glTranslated(x, y - 0.2D, z);
                        GL11.glRotated(-mc.getRenderManager().playerViewY, 0.0D, 1.0D, 0.0D);
                        GlStateManager.disableDepth();
                        GL11.glScalef(0.03F + d, 0.03F, 0.03F + d);
                        int base = 1;
                        d2p(0.0D, 95.0D, 10, 3, Color.black.getRGB());

                        for (i = 0; i < 6; ++i) {
                            d2p(0.0D, 95 + (10 - i), 3, 4, Color.black.getRGB());
                        }

                        for (i = 0; i < 7; ++i) {
                            d2p(0.0D, 95 + (10 - i), 2, 4, color);
                        }

                        d2p(0.0D, 95.0D, 8, 3, color);
                        GlStateManager.enableDepth();
                    } else {
                        AxisAlignedBB bbox = e.getEntityBoundingBox().expand(0.1D + expand, 0.1D + expand, 0.1D + expand);
                        AxisAlignedBB axis = new AxisAlignedBB(bbox.minX - e.posX + x, bbox.minY - e.posY + y, bbox.minZ - e.posZ + z, bbox.maxX - e.posX + x, bbox.maxY - e.posY + y, bbox.maxZ - e.posZ + z);
                        GL11.glBlendFunc(770, 771);
                        GL11.glEnable(3042);
                        GL11.glDisable(3553);
                        GL11.glDisable(2929);
                        GL11.glDepthMask(false);
                        GL11.glLineWidth(2.0F);
                        GL11.glColor4f(r, g, b, a);
                        if (type == 1) {
                            RenderGlobal.drawSelectionBoundingBox(axis);
                        } else if (type == 2) {
                            dbb(axis, r, g, b);
                        }

                        GL11.glEnable(3553);
                        GL11.glEnable(2929);
                        GL11.glDepthMask(true);
                        GL11.glDisable(3042);
                    }
                }
            }

            GlStateManager.popMatrix();
        }
    }

    public static void dGR(int left, int top, int right, int bottom, int startColor, int endColor) {
        int j;
        if (left < right) {
            j = left;
            left = right;
            right = j;
        }

        if (top < bottom) {
            j = top;
            top = bottom;
            bottom = j;
        }

        float f = (float) (startColor >> 24 & 255) / 255.0F;
        float f1 = (float) (startColor >> 16 & 255) / 255.0F;
        float f2 = (float) (startColor >> 8 & 255) / 255.0F;
        float f3 = (float) (startColor & 255) / 255.0F;
        float f4 = (float) (endColor >> 24 & 255) / 255.0F;
        float f5 = (float) (endColor >> 16 & 255) / 255.0F;
        float f6 = (float) (endColor >> 8 & 255) / 255.0F;
        float f7 = (float) (endColor & 255) / 255.0F;
        disableTexture2D();
        enableBlend();
        disableAlpha();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.shadeModel(7425);
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        worldrenderer.begin(7, DefaultVertexFormats.POSITION_COLOR);
        worldrenderer.pos(right, top, 0.0D).color(f1, f2, f3, f).endVertex();
        worldrenderer.pos(left, top, 0.0D).color(f1, f2, f3, f).endVertex();
        worldrenderer.pos(left, bottom, 0.0D).color(f5, f6, f7, f4).endVertex();
        worldrenderer.pos(right, bottom, 0.0D).color(f5, f6, f7, f4).endVertex();
        tessellator.draw();
        GlStateManager.shadeModel(7424);
        GlStateManager.disableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.enableTexture2D();
    }

    public static void d2p(double x, double y, int radius, int sides, int color) {
        float a = (float) (color >> 24 & 255) / 255.0F;
        float r = (float) (color >> 16 & 255) / 255.0F;
        float g = (float) (color >> 8 & 255) / 255.0F;
        float b = (float) (color & 255) / 255.0F;
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        enableBlend();
        disableTexture2D();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.color(r, g, b, a);
        worldrenderer.begin(6, DefaultVertexFormats.POSITION);

        for (int i = 0; i < sides; ++i) {
            double angle = 6.283185307179586D * (double) i / (double) sides + Math.toRadians(180.0D);
            worldrenderer.pos(x + Math.sin(angle) * (double) radius, y + Math.cos(angle) * (double) radius, 0.0D).endVertex();
        }

        tessellator.draw();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
    }

    public static void d3p(double x, double y, double z, double radius, int sides, float lineWidth, int color, boolean chroma) {
        float a = (float) (color >> 24 & 255) / 255.0F;
        float r = (float) (color >> 16 & 255) / 255.0F;
        float g = (float) (color >> 8 & 255) / 255.0F;
        float b = (float) (color & 255) / 255.0F;
        mc.entityRenderer.disableLightmap();
        GL11.glDisable(3553);
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        GL11.glDisable(2929);
        GL11.glEnable(2848);
        GL11.glDepthMask(false);
        GL11.glLineWidth(lineWidth);
        if (!chroma) {
            GL11.glColor4f(r, g, b, a);
        }

        GL11.glBegin(1);
        long d = 0L;
        long ed = 15000L / (long) sides;
        long hed = ed / 2L;

        for (int i = 0; i < sides * 2; ++i) {
            if (chroma) {
                if (i % 2 != 0) {
                    if (i == 47) {
                        d = hed;
                    }

                    d += ed;
                }

                int c = rainbowDraw(2L, d);
                float r2 = (float) (c >> 16 & 255) / 255.0F;
                float g2 = (float) (c >> 8 & 255) / 255.0F;
                float b2 = (float) (c & 255) / 255.0F;
                GL11.glColor3f(r2, g2, b2);
            }

            double angle = 6.283185307179586D * (double) i / (double) sides + Math.toRadians(180.0D);
            GL11.glVertex3d(x + Math.cos(angle) * radius, y, z + Math.sin(angle) * radius);
        }

        GL11.glEnd();
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glDepthMask(true);
        GL11.glDisable(2848);
        GL11.glEnable(2929);
        GL11.glDisable(3042);
        GL11.glEnable(3553);
        mc.entityRenderer.enableLightmap();
    }

    public static void dbb(AxisAlignedBB abb, float r, float g, float b) {
        float a = 0.25F;
        Tessellator ts = Tessellator.getInstance();
        WorldRenderer vb = ts.getWorldRenderer();
        vb.begin(7, DefaultVertexFormats.POSITION_COLOR);
        vb.pos(abb.minX, abb.minY, abb.minZ).color(r, g, b, a).endVertex();
        vb.pos(abb.minX, abb.maxY, abb.minZ).color(r, g, b, a).endVertex();
        vb.pos(abb.maxX, abb.minY, abb.minZ).color(r, g, b, a).endVertex();
        vb.pos(abb.maxX, abb.maxY, abb.minZ).color(r, g, b, a).endVertex();
        vb.pos(abb.maxX, abb.minY, abb.maxZ).color(r, g, b, a).endVertex();
        vb.pos(abb.maxX, abb.maxY, abb.maxZ).color(r, g, b, a).endVertex();
        vb.pos(abb.minX, abb.minY, abb.maxZ).color(r, g, b, a).endVertex();
        vb.pos(abb.minX, abb.maxY, abb.maxZ).color(r, g, b, a).endVertex();
        ts.draw();
        vb.begin(7, DefaultVertexFormats.POSITION_COLOR);
        vb.pos(abb.maxX, abb.maxY, abb.minZ).color(r, g, b, a).endVertex();
        vb.pos(abb.maxX, abb.minY, abb.minZ).color(r, g, b, a).endVertex();
        vb.pos(abb.minX, abb.maxY, abb.minZ).color(r, g, b, a).endVertex();
        vb.pos(abb.minX, abb.minY, abb.minZ).color(r, g, b, a).endVertex();
        vb.pos(abb.minX, abb.maxY, abb.maxZ).color(r, g, b, a).endVertex();
        vb.pos(abb.minX, abb.minY, abb.maxZ).color(r, g, b, a).endVertex();
        vb.pos(abb.maxX, abb.maxY, abb.maxZ).color(r, g, b, a).endVertex();
        vb.pos(abb.maxX, abb.minY, abb.maxZ).color(r, g, b, a).endVertex();
        ts.draw();
        vb.begin(7, DefaultVertexFormats.POSITION_COLOR);
        vb.pos(abb.minX, abb.maxY, abb.minZ).color(r, g, b, a).endVertex();
        vb.pos(abb.maxX, abb.maxY, abb.minZ).color(r, g, b, a).endVertex();
        vb.pos(abb.maxX, abb.maxY, abb.maxZ).color(r, g, b, a).endVertex();
        vb.pos(abb.minX, abb.maxY, abb.maxZ).color(r, g, b, a).endVertex();
        vb.pos(abb.minX, abb.maxY, abb.minZ).color(r, g, b, a).endVertex();
        vb.pos(abb.minX, abb.maxY, abb.maxZ).color(r, g, b, a).endVertex();
        vb.pos(abb.maxX, abb.maxY, abb.maxZ).color(r, g, b, a).endVertex();
        vb.pos(abb.maxX, abb.maxY, abb.minZ).color(r, g, b, a).endVertex();
        ts.draw();
        vb.begin(7, DefaultVertexFormats.POSITION_COLOR);
        vb.pos(abb.minX, abb.minY, abb.minZ).color(r, g, b, a).endVertex();
        vb.pos(abb.maxX, abb.minY, abb.minZ).color(r, g, b, a).endVertex();
        vb.pos(abb.maxX, abb.minY, abb.maxZ).color(r, g, b, a).endVertex();
        vb.pos(abb.minX, abb.minY, abb.maxZ).color(r, g, b, a).endVertex();
        vb.pos(abb.minX, abb.minY, abb.minZ).color(r, g, b, a).endVertex();
        vb.pos(abb.minX, abb.minY, abb.maxZ).color(r, g, b, a).endVertex();
        vb.pos(abb.maxX, abb.minY, abb.maxZ).color(r, g, b, a).endVertex();
        vb.pos(abb.maxX, abb.minY, abb.minZ).color(r, g, b, a).endVertex();
        ts.draw();
        vb.begin(7, DefaultVertexFormats.POSITION_COLOR);
        vb.pos(abb.minX, abb.minY, abb.minZ).color(r, g, b, a).endVertex();
        vb.pos(abb.minX, abb.maxY, abb.minZ).color(r, g, b, a).endVertex();
        vb.pos(abb.minX, abb.minY, abb.maxZ).color(r, g, b, a).endVertex();
        vb.pos(abb.minX, abb.maxY, abb.maxZ).color(r, g, b, a).endVertex();
        vb.pos(abb.maxX, abb.minY, abb.maxZ).color(r, g, b, a).endVertex();
        vb.pos(abb.maxX, abb.maxY, abb.maxZ).color(r, g, b, a).endVertex();
        vb.pos(abb.maxX, abb.minY, abb.minZ).color(r, g, b, a).endVertex();
        vb.pos(abb.maxX, abb.maxY, abb.minZ).color(r, g, b, a).endVertex();
        ts.draw();
        vb.begin(7, DefaultVertexFormats.POSITION_COLOR);
        vb.pos(abb.minX, abb.maxY, abb.maxZ).color(r, g, b, a).endVertex();
        vb.pos(abb.minX, abb.minY, abb.maxZ).color(r, g, b, a).endVertex();
        vb.pos(abb.minX, abb.maxY, abb.minZ).color(r, g, b, a).endVertex();
        vb.pos(abb.minX, abb.minY, abb.minZ).color(r, g, b, a).endVertex();
        vb.pos(abb.maxX, abb.maxY, abb.minZ).color(r, g, b, a).endVertex();
        vb.pos(abb.maxX, abb.minY, abb.minZ).color(r, g, b, a).endVertex();
        vb.pos(abb.maxX, abb.maxY, abb.maxZ).color(r, g, b, a).endVertex();
        vb.pos(abb.maxX, abb.minY, abb.maxZ).color(r, g, b, a).endVertex();
        ts.draw();
    }

    public static int rainbowDraw(long speed, long... delay) {
        long time = System.currentTimeMillis() + (delay.length > 0 ? delay[0] : 0L);
        return Color.getHSBColor((float) (time % (15000L / speed)) / (15000.0F / (float) speed), 1.0F, 1.0F).getRGB();
    }

    public static void drawTracer(Entity entity, Color color) {
        final RenderManager renderManager = Minecraft.getMinecraft().getRenderManager();
        final double x = entity.posX - renderManager.renderPosX;
        final double y = entity.posY - renderManager.renderPosY;
        final double z = entity.posZ - renderManager.renderPosZ;
        final Vec3 eyeVector = new Vec3(0.0, 0.0, 1.0).rotatePitch(-(float) Math.toRadians(Minecraft.getMinecraft().thePlayer.rotationPitch)).rotateYaw(-(float) Math.toRadians(Minecraft.getMinecraft().thePlayer.rotationYaw));
        GL11.glBlendFunc(770, 771);
        GL11.glEnable(3042);
        GL11.glLineWidth(2.0f);
        GL11.glDisable(3553);
        GL11.glDisable(2929);
        GL11.glDepthMask(false);
        glColor(color);
        GL11.glBegin(1);
        GL11.glVertex3d(eyeVector.xCoord, Minecraft.getMinecraft().thePlayer.getEyeHeight() + eyeVector.yCoord, eyeVector.zCoord);
        GL11.glVertex3d(x, y, z);
        GL11.glVertex3d(x, y, z);
        GL11.glVertex3d(x, y + entity.height, z);
        GL11.glEnd();
        GL11.glEnable(3553);
        GL11.glEnable(2929);
        GL11.glDepthMask(true);
        GL11.glDisable(3042);
        GlStateManager.resetColor();
    }

    public static void glColor(Color color) {
        GL11.glColor4f(color.getRed() / 255.0f, color.getGreen() / 255.0f, color.getBlue() / 255.0f, color.getAlpha() / 255.0f);
    }

    public static boolean isInViewFrustrum(final Entity entity) {
        return (isInViewFrustrum(entity.getEntityBoundingBox()) || entity.ignoreFrustumCheck);
    }

    private static boolean isInViewFrustrum(final AxisAlignedBB bb) {
        final Entity current = mc.getRenderViewEntity();
        frustrum.setPosition(current.posX, current.posY, current.posZ);
        return frustrum.isBoundingBoxInFrustum(bb);
    }

    public static void drawOutlineRect(final float drawX, final float drawY, final float drawWidth, final float drawHeight, final int color) {
        rect(drawX, drawY, drawWidth, drawY + 0.5f, color);
        rect(drawX, drawY + 0.5f, drawX + 0.5f, drawHeight, color);
        rect(drawWidth - 0.5f, drawY + 0.5f, drawWidth, drawHeight - 0.5f, color);
        rect(drawX + 0.5f, drawHeight - 0.5f, drawWidth, drawHeight, color);
    }

    public static void drawRectCol(float x, float y, float width, float height, Color color) {
        GL11.glPushMatrix();
        GL11.glDisable(3553);
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        GL11.glShadeModel(7425);
        GL11.glBegin(7);
        GL11.glColor4f((float) color.getRed() / 255.0f, (float) color.getGreen() / 255.0f, (float) color.getBlue() / 255.0f, (float) color.getAlpha() / 255.0f);
        GL11.glVertex2f(x, y);
        GL11.glVertex2f(x, y + height);
        GL11.glVertex2f(x + width, y + height);
        GL11.glVertex2f(x + width, y);
        GL11.glColor4f(0.0f, 0.0f, 0.0f, 1.0f);
        GL11.glEnd();
        GL11.glEnable(3553);
        GL11.glDisable(3042);
        GL11.glPopMatrix();
    }

    public static void drawFilledCircle(final double x, final double y, final double r, final int c, final int quality) {
        final float f = ((c >> 24) & 0xff) / 255F;
        final float f1 = ((c >> 16) & 0xff) / 255F;
        final float f2 = ((c >> 8) & 0xff) / 255F;
        final float f3 = (c & 0xff) / 255F;
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glColor4f(f1, f2, f3, f);
        GL11.glBegin(GL11.GL_TRIANGLE_FAN);

        for (int i = 0; i <= 360 / quality; i++) {
            final double x2 = Math.sin(((i * quality * Math.PI) / 180)) * r;
            final double y2 = Math.cos(((i * quality * Math.PI) / 180)) * r;
            GL11.glVertex2d(x + x2, y + y2);
        }

        GL11.glEnd();
        GL11.glDisable(GL11.GL_LINE_SMOOTH);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_BLEND);
    }

    public static void draw2DPolygon(double x, double y, double radius, int sides, int color) {
        if (sides < 3) return;
        float a = (color >> 24 & 0xFF) / 255.0F;
        float r = (color >> 16 & 0xFF) / 255.0F;
        float g = (color >> 8 & 0xFF) / 255.0F;
        float b = (color & 0xFF) / 255.0F;
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GL11.glColor4f(r, g, b, a);
        worldrenderer.begin(GL11.GL_TRIANGLE_FAN, DefaultVertexFormats.POSITION);
        for (int i = 0; i < sides; i++) {
            double angle = (Math.PI * 2 * i / sides) + Math.toRadians(180);
            worldrenderer.pos(x + Math.sin(angle) * radius, y + Math.cos(angle) * radius, 0).endVertex();
        }
        tessellator.draw();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
    }

    public static class Rise {
        public static void enable(final int glTarget) {
            GL11.glEnable(glTarget);
        }

        public static void disable(final int glTarget) {
            GL11.glDisable(glTarget);
        }

        public static void start() {
            enable(GL11.GL_BLEND);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            disable(GL11.GL_TEXTURE_2D);
            disable(GL11.GL_CULL_FACE);
            GlStateManager.disableAlpha();
            GlStateManager.disableDepth();
        }

        public static void stop() {
            GlStateManager.enableAlpha();
            GlStateManager.enableDepth();
            enable(GL11.GL_CULL_FACE);
            enable(GL11.GL_TEXTURE_2D);
            disable(GL11.GL_BLEND);
            color(Color.white);
        }

        public static void begin(final int glMode) {
            GL11.glBegin(glMode);
        }

        public static void end() {
            GL11.glEnd();
        }

        public static void vertex(final double x, final double y) {
            GL11.glVertex2d(x, y);
        }

        public static void color(final double red, final double green, final double blue, final double alpha) {
            GL11.glColor4d(red, green, blue, alpha);
        }

        public void color(final double red, final double green, final double blue) {
            color(red, green, blue, 1);
        }

        public static void color(Color color) {
            if (color == null)
                color = Color.white;
            color(color.getRed() / 255F, color.getGreen() / 255F, color.getBlue() / 255F, color.getAlpha() / 255F);
        }

        public void color(Color color, final int alpha) {
            if (color == null)
                color = Color.white;
            color(color.getRed() / 255F, color.getGreen() / 255F, color.getBlue() / 255F, 0.5);
        }

        public static void lineNoGl(final double firstX, final double firstY, final double secondX, final double secondY, final Color color) {

            start();
            if (color != null)
                color(color);
            GL11.glLineWidth(1);
            GL11.glEnable(GL11.GL_LINE_SMOOTH);
            begin(GL_LINES);
            {
                vertex(firstX, firstY);
                vertex(secondX, secondY);
            }
            end();
            GL11.glDisable(GL11.GL_LINE_SMOOTH);
            stop();
        }

        public static void polygon(final double x, final double y, double sideLength, final double amountOfSides, final boolean filled, final Color color) {
            sideLength /= 2;
            start();
            if (color != null)
                color(color);
            if (!filled) GL11.glLineWidth(2);
            GL11.glEnable(GL11.GL_LINE_SMOOTH);
            begin(filled ? GL11.GL_TRIANGLE_FAN : GL11.GL_LINE_STRIP);
            {
                for (double i = 0; i <= amountOfSides / 4; i++) {
                    final double angle = i * 4 * (Math.PI * 2) / 360;
                    vertex(x + (sideLength * Math.cos(angle)) + sideLength, y + (sideLength * Math.sin(angle)) + sideLength);
                }
            }
            end();
            GL11.glDisable(GL11.GL_LINE_SMOOTH);
            stop();
        }

        public static void circle(final double x, final double y, final double radius, final Color color) {
            polygon(x, y, radius, 360, true, color);
        }

    }
}

