package me.kambing.gardenia.module.render;

import me.kambing.gardenia.Gardenia;
import me.kambing.gardenia.module.Category;
import me.kambing.gardenia.module.Module;
import me.kambing.gardenia.module.combat.LegitAura;
import me.kambing.gardenia.module.player.BedBreaker;
import me.kambing.gardenia.module.render.deatheffects.DeathEffects;
import me.kambing.gardenia.settings.Setting;
import me.kambing.gardenia.utils.EntityUtil;
import me.kambing.gardenia.utils.RenderUtil;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;

import javax.vecmath.Vector3d;
import javax.vecmath.Vector4d;
import java.awt.*;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.List;


public class TwoDESP extends Module {
    public TwoDESP() {
        super("2DESP", "ESP but 2d", false, false, Category.Render);
        new Setting("OnlyRenderDistance", this, false);
        new Setting("Color", this, new Color(-1));
        new Setting("TeamColor", this, new Color(0x00FF00));
        new Setting("Invisible", this, true);
    }

    private final IntBuffer viewport = GLAllocation.createDirectIntBuffer(16);
    private final FloatBuffer modelview = GLAllocation.createDirectFloatBuffer(16);
    private final FloatBuffer projection = GLAllocation.createDirectFloatBuffer(16);
    private final FloatBuffer vector = GLAllocation.createDirectFloatBuffer(4);

    @SubscribeEvent
    public void onRenderGameOverlay(RenderWorldLastEvent event) {
        if (this.isToggled()) {
            BedBreaker bedBreaker = Gardenia.instance.moduleManager.bedBreaker;
            if (bedBreaker.pos != null) {
                BlockPos pos = bedBreaker.pos;
                RenderUtil.re(pos, bedBreaker.getSetting("Color").getColor().getRGB(), false);
            }
            DeathEffects deathEffects = Gardenia.instance.moduleManager.deathEffects;
            if (deathEffects.isToggled()) {
                deathEffects.onRenderWorldLast(event);
            }
            LegitAura legitAura = Gardenia.instance.moduleManager.legitAura;
            if (legitAura.isToggled() && legitAura.target != null && legitAura.getSetting("Render").getValBoolean()) {
                legitAura.drawCircle(event.partialTicks, legitAura.target, 0.67, true);
            }
            GL11.glPushMatrix();

            final ScaledResolution scaledresolution = new ScaledResolution(mc);
            final EntityRenderer entityRenderer = mc.entityRenderer;
            final int scaleFactor = scaledresolution.getScaleFactor();
            final RenderManager renderMng = mc.getRenderManager();

            for (final EntityPlayer p : mc.theWorld.playerEntities) {
                if (p != null) {
                    if (getSetting("OnlyRenderDistance").getValBoolean() && p.getDistanceToEntity(mc.thePlayer) > (mc.gameSettings.renderDistanceChunks * 16))
                        continue;
                    if (!getSetting("Invisible").getValBoolean() && p.isInvisible()) continue;
                    final String name = p.getName();
                    if (!p.isDead && p != mc.thePlayer && !p.getDisplayName()
                            .getUnformattedText().contains("[NPC] ") &&
                            !name.isEmpty() && !name.equals(" ") && RenderUtil.isInViewFrustrum(p)) {
                        final float partialTicks = event.partialTicks;
                        final double x = p.lastTickPosX + (p.posX - p.lastTickPosX) * partialTicks;
                        final double y = (p.lastTickPosY + (p.posY - p.lastTickPosY) * partialTicks);
                        final double z = p.lastTickPosZ + (p.posZ - p.lastTickPosZ) * partialTicks;

                        final double width = p.width / 1.5D;
                        final double height = p.height + (p.isSneaking() ? -0.3D : 0.2D);
                        final AxisAlignedBB aabb = new AxisAlignedBB(x - width, y, z - width, x + width, y + height, z + width);
                        final List<Vector3d> vectors = Arrays.asList(new Vector3d(aabb.minX, aabb.minY, aabb.minZ), new Vector3d(aabb.minX, aabb.maxY, aabb.minZ), new Vector3d(aabb.maxX, aabb.minY, aabb.minZ), new Vector3d(aabb.maxX, aabb.maxY, aabb.minZ), new Vector3d(aabb.minX, aabb.minY, aabb.maxZ), new Vector3d(aabb.minX, aabb.maxY, aabb.maxZ), new Vector3d(aabb.maxX, aabb.minY, aabb.maxZ), new Vector3d(aabb.maxX, aabb.maxY, aabb.maxZ));

                        entityRenderer.setupCameraTransform(partialTicks, 0);

                        Vector4d position = null;
                        for (Vector3d v : vectors) {

                            v = project2D(scaleFactor, v.x - renderMng.viewerPosX, v.y - renderMng.viewerPosY, v.z - renderMng.viewerPosZ);
                            if (v != null && v.z >= 0.0D && v.z < 1.0D) {
                                if (position == null)
                                    position = new Vector4d(v.x, v.y, v.z, 0.0D);
                                position.x = Math.min(v.x, position.x);
                                position.y = Math.min(v.y, position.y);
                                position.z = Math.max(v.x, position.z);
                                position.w = Math.max(v.y, position.w);
                            }

                        }

                        if (position != null) {

                            entityRenderer.setupOverlayRendering();
                            final double posX = position.x;
                            final double posY = position.y;
                            final double endPosX = position.z;
                            final double endPosY = position.w;

                            final float w = 0.5f;

                            //Drawing box
                            final Color c = EntityUtil.isTeam(mc.thePlayer, p) ? getSetting("TeamColor").getColor() : getSetting("Color").getColor();

                            RenderUtil.Rise.lineNoGl(posX - w, posY, posX + w - w, endPosY, c);
                            RenderUtil.Rise.lineNoGl(posX, endPosY - w, endPosX, endPosY, c);
                            RenderUtil.Rise.lineNoGl(posX - w, posY, endPosX, posY + w, c);
                            RenderUtil.Rise.lineNoGl(endPosX - w, posY, endPosX, endPosY, c);

                            final double percentage = (endPosY - posY) * p.getHealth() / p.getMaxHealth();

                            final double distance = 2;

                            final float[] fractions = new float[]{0.0F, 0.5F, 1.0F};
                            final Color[] colors = new Color[]{Color.RED, Color.YELLOW, Color.GREEN};
                            final float progress = p.getHealth() / p.getMaxHealth();
                            final Color healthColor = p.getHealth() >= 0.0F ? blendColors(fractions, colors, progress).brighter() : Color.RED;

                            RenderUtil.Rise.lineNoGl(posX - w - distance, endPosY - percentage, posX + w - w - distance, endPosY, healthColor);
                        }
                    }
                }
            }

            GL11.glPopMatrix();
        }
    }

    public static Color blendColors(final float[] fractions, final Color[] colors, final float progress) {
        if (fractions == null) {
            throw new IllegalArgumentException("Fractions can't be null");
        }
        if (colors == null) {
            throw new IllegalArgumentException("Colours can't be null");
        }
        if (fractions.length == colors.length) {
            final int[] getFractionBlack = getFraction(fractions, progress);
            final float[] range = new float[]{fractions[getFractionBlack[0]], fractions[getFractionBlack[1]]};
            final Color[] colorRange = new Color[]{colors[getFractionBlack[0]], colors[getFractionBlack[1]]};
            final float max = range[1] - range[0];
            final float value = progress - range[0];
            final float weight = value / max;
            return blend(colorRange[0], colorRange[1], 1.0f - weight);
        }
        throw new IllegalArgumentException("Fractions and colours must have equal number of elements");
    }

    public static int[] getFraction(final float[] fractions, final float progress) {
        int startPoint;
        final int[] range = new int[2];
        for (startPoint = 0; startPoint < fractions.length && fractions[startPoint] <= progress; ++startPoint) {
        }
        if (startPoint >= fractions.length) {
            startPoint = fractions.length - 1;
        }
        range[0] = startPoint - 1;
        range[1] = startPoint;
        return range;
    }

    public static Color blend(final Color color1, final Color color2, final double ratio) {
        final float r = (float) ratio;
        final float ir = 1.0f - r;
        final float[] rgb1 = new float[3];
        final float[] rgb2 = new float[3];
        color1.getColorComponents(rgb1);
        color2.getColorComponents(rgb2);
        float red = rgb1[0] * r + rgb2[0] * ir;
        float green = rgb1[1] * r + rgb2[1] * ir;
        float blue = rgb1[2] * r + rgb2[2] * ir;
        if (red < 0.0f) {
            red = 0.0f;
        } else if (red > 255.0f) {
            red = 255.0f;
        }
        if (green < 0.0f) {
            green = 0.0f;
        } else if (green > 255.0f) {
            green = 255.0f;
        }
        if (blue < 0.0f) {
            blue = 0.0f;
        } else if (blue > 255.0f) {
            blue = 255.0f;
        }
        Color color3 = null;
        try {
            color3 = new Color(red, green, blue);
        } catch (final IllegalArgumentException exp) {
            final NumberFormat nf = NumberFormat.getNumberInstance();
            // System.out.println(nf.format(red) + "; " + nf.format(green) + "; " + nf.format(blue));
            exp.printStackTrace();
        }
        return color3;
    }

    private Vector3d project2D(final int scaleFactor, final double x, final double y, final double z) {

        GL11.glGetFloat(2982, modelview);
        GL11.glGetFloat(2983, projection);
        GL11.glGetInteger(2978, viewport);
        if (GLU.gluProject((float) x, (float) y, (float) z, modelview, projection, viewport, vector))
            return new Vector3d((vector.get(0) / scaleFactor), ((Display.getHeight() - vector.get(1)) / scaleFactor), vector.get(2));
        return null;
    }
}
