package me.kambing.gardenia.module.client.targethud;

import me.kambing.gardenia.Gardenia;
import me.kambing.gardenia.module.Category;
import me.kambing.gardenia.module.Module;
import me.kambing.gardenia.settings.Setting;
import me.kambing.gardenia.utils.RenderUtil;
import me.kambing.gardenia.utils.TimeUtil;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.ArrayList;

import static me.kambing.gardenia.module.render.TwoDESP.blendColors;

public class TargetHUD extends Module {
    public TargetHUD() {
        super("TargetHUD", "target hud", false, false, Category.Client);
        new Setting("HealthBarColor", this, new Color(0xFF078301));
        new Setting("ParticleColor", this, new Color(0x3F7331));
    }

    private int alpha = 0;
    TimeUtil timer = new TimeUtil();
    private final ArrayList<Particles> particles = new ArrayList<>();
    private boolean sentParticles;
    private final ArrayList<AbstractClientPlayer> player = new ArrayList<>();

    @SubscribeEvent
    public void onRender(TickEvent.RenderTickEvent event) {
        final ScaledResolution sr = new ScaledResolution(mc);
        if (mc.thePlayer == null) return;
        if (mc.theWorld == null) return;
        Entity target = null;
        if (Gardenia.instance.moduleManager.legitAura.target != null) {
            target = Gardenia.instance.moduleManager.legitAura.target;
            player.add((AbstractClientPlayer) Gardenia.instance.moduleManager.legitAura.target);
            if (alpha < 255) {
                alpha++;
            }
        } else if (mc.objectMouseOver != null) {
            if (mc.objectMouseOver.entityHit != null) {
                if (mc.objectMouseOver.entityHit instanceof EntityPlayer) {
                    target = mc.objectMouseOver.entityHit;
                    player.add((AbstractClientPlayer) mc.objectMouseOver.entityHit);
                    if (alpha < 255) {
                        alpha++;
                    }
                }
            }
        }
        if (target == null && alpha < 1) {
            for (int i = 0; i < player.size(); i++) {
                player.remove(i);
            }
        }
        if (target == null && alpha > 0 && player.get(0) != null) {
            alpha--;
            final EntityPlayer ent = player.get(0);

            final float clampedHealthValue = MathHelper.clamp_float(ent.getHealth(), 0, ent.getMaxHealth());
            final float normalizedenHealthValue = clampedHealthValue / ent.getMaxHealth();

            Gui.drawRect(((sr.getScaledWidth() - 150)) / (int) 2F, (int) (sr.getScaledHeight() / 2F + 205) - 60, ((sr.getScaledWidth() + 150)) / (int) 2F, (sr.getScaledHeight() / (int) 2F + 165) - 60, fadeColor(new Color(0x000000), true).getRGB());

            Gui.drawRect(((sr.getScaledWidth() - 70)) / (int) 2F, (sr.getScaledHeight() / (int) 2F + 202) - 60, ((sr.getScaledWidth() + 146)) / (int) 2F, (sr.getScaledHeight() / (int) 2F + 199) - 60, fadeColor(new Color(0xFF353535)).getRGB());
            Gui.drawRect(((sr.getScaledWidth() - 70)) / (int) 2F, (sr.getScaledHeight() / (int) 2F + 197) - 60, ((sr.getScaledWidth() + 146)) / (int) 2F, (sr.getScaledHeight() / (int) 2F + 194) - 60, fadeColor(new Color(0xFF353535)).getRGB());

            RenderUtil.renderGradientRectLeftRight(((sr.getScaledWidth() - 70)) / 2, (int) (sr.getScaledHeight() / 2F + 194) - 60, (int) ((sr.getScaledWidth() - 70 + (216 * normalizedenHealthValue))) / 2, (int) (sr.getScaledHeight() / 2F + 197) - 60, fadeColor(getSetting("HealthBarColor").getColor()).darker().getRGB(), fadeColor(getSetting("HealthBarColor").getColor()).getRGB());
            RenderUtil.renderGradientRectLeftRight(((sr.getScaledWidth() - 70)) / 2, (int) (sr.getScaledHeight() / 2F + 199) - 60, (int) ((sr.getScaledWidth() - 70 + (216 * (player.get(0).getTotalArmorValue() / 20f)))) / 2, (int) (sr.getScaledHeight() / 2F + 202) - 60, fadeColor(new Color(0xFF0050FF)).getRGB(), fadeColor(new Color(0xFF00FFFF)).getRGB());
            final int scaleOffset = (int) ((player.get(0)).hurtTime * 0.40f);
            renderPlayerModelTexture(((sr.getScaledWidth() - 146) + scaleOffset) / 2f, (sr.getScaledHeight() / 2F + 167 + scaleOffset) - 60, 3, 3, 3, 3, 36 - scaleOffset, 36 - scaleOffset, 24, 24, player.get(0));
            renderPlayerModelTexture(((sr.getScaledWidth() - 146)) / 2f, (sr.getScaledHeight() / 2F + 167) - 60, 15, 3, 3, 3, 36, 36, 24, 24, player.get(0));
        }
        if (target != null) {
            if (player.get(0) != target) {
                player.set(0, (AbstractClientPlayer) target);
            }
            final EntityPlayer ent = (EntityPlayer) target;
            final double dista = mc.thePlayer.getDistanceToEntity(target);

            final float clampedHealthValue = MathHelper.clamp_float(ent.getHealth(), 0, ent.getMaxHealth());
            final double enHeartsValue = (clampedHealthValue / ent.getMaxHealth()) * 20.0;
            final float normalizedenHealthValue = clampedHealthValue / ent.getMaxHealth();
            final String enHearts = String.valueOf(enHeartsValue).split("\\.")[0] + "." + String.valueOf(enHeartsValue).split("\\.")[1].charAt(0);
            final String enDistance = String.valueOf(dista).split("\\.")[0] + "." + String.valueOf(dista).split("\\.")[1].charAt(0);


            Gui.drawRect(((sr.getScaledWidth() - 150)) / (int) 2F, (int) (sr.getScaledHeight() / 2F + 205) - 60, ((sr.getScaledWidth() + 150)) / (int) 2F, (sr.getScaledHeight() / (int) 2F + 165) - 60, fadeColor(new Color(0x000000), true).getRGB());

            mc.fontRendererObj.drawString(ent.getName(), ((sr.getScaledWidth() - 70)) / (int) 2F, (sr.getScaledHeight() / (int) 2F + 169) - 60, -1);
            mc.fontRendererObj.drawString("Dist: " + enDistance, ((sr.getScaledWidth() - 70)) / (int) 2F, (sr.getScaledHeight() / (int) 2F + 179) - 60, -1);
            final float[] fractions = new float[]{0.0F, 0.5F, 1.0F};
            final Color[] colors = new Color[]{Color.RED, Color.YELLOW, Color.GREEN};
            final float progress = ent.getHealth() / ent.getMaxHealth();
            final Color healthColor = ent.getHealth() >= 0.0F ? blendColors(fractions, colors, progress).brighter() : Color.RED;
            mc.fontRendererObj.drawString(enHearts, ((sr.getScaledWidth() + 149)) / (int) 2F - mc.fontRendererObj.getStringWidth(enHearts), (sr.getScaledHeight() / (int) 2F + 184) - 60, healthColor.getRGB());

            Gui.drawRect(((sr.getScaledWidth() - 70)) / (int) 2F, (sr.getScaledHeight() / (int) 2F + 202) - 60, ((sr.getScaledWidth() + 146)) / (int) 2F, (sr.getScaledHeight() / (int) 2F + 199) - 60, fadeColor(new Color(0xFF353535)).getRGB());
            Gui.drawRect(((sr.getScaledWidth() - 70)) / (int) 2F, (sr.getScaledHeight() / (int) 2F + 197) - 60, ((sr.getScaledWidth() + 146)) / (int) 2F, (sr.getScaledHeight() / (int) 2F + 194) - 60, fadeColor(new Color(0xFF353535)).getRGB());

            RenderUtil.renderGradientRectLeftRight(((sr.getScaledWidth() - 70)) / 2, (int) (sr.getScaledHeight() / 2F + 194) - 60, (int) ((sr.getScaledWidth() - 70 + (216 * normalizedenHealthValue))) / 2, (int) (sr.getScaledHeight() / 2F + 197) - 60, fadeColor(getSetting("HealthBarColor").getColor()).darker().getRGB(), fadeColor(getSetting("HealthBarColor").getColor()).getRGB());
            RenderUtil.renderGradientRectLeftRight(((sr.getScaledWidth() - 70)) / 2, (int) (sr.getScaledHeight() / 2F + 199) - 60, (int) ((sr.getScaledWidth() - 70 + (216 * (ent.getTotalArmorValue() / 20f)))) / 2, (int) (sr.getScaledHeight() / 2F + 202) - 60, fadeColor(new Color(0xFF0050FF)).getRGB(), fadeColor(new Color(0xFF00FFFF)).getRGB());

            //offset other colors aside from red, so the face turns red
            final double offset = -(ent.hurtTime * 23);
            //sets color to red
            RenderUtil.Rise.color(fadeColor(new Color(255, (int) (255 + offset), (int) (255 + offset))));
            //Renders face
            final int scaleOffset = (int) (((EntityPlayer) target).hurtTime * 0.40f);
            if (target instanceof AbstractClientPlayer) {
                //renders face
                renderPlayerModelTexture(((sr.getScaledWidth() - 146) + scaleOffset) / 2f, (sr.getScaledHeight() / 2F + 167 + scaleOffset) - 60, 3, 3, 3, 3, 36 - scaleOffset, 36 - scaleOffset, 24, 24, (AbstractClientPlayer) ent);
                //renders top layer of face
                renderPlayerModelTexture(((sr.getScaledWidth() - 146)) / 2f, (sr.getScaledHeight() / 2F + 167) - 60, 15, 3, 3, 3, 36, 36, 24, 24, (AbstractClientPlayer) ent);
            } else {
                // renders face
                renderSteveModelTexture(((sr.getScaledWidth() - 146)) / 2f, (sr.getScaledHeight() / 2F + 167) - 60, 3, 3, 3, 3, 36, 36, 24, 24);
                // renders top layer of face
                renderSteveModelTexture(((sr.getScaledWidth() - 146)) / 2f, (sr.getScaledHeight() / 2F + 167) - 60, 15, 3, 3, 3, 36, 36, 24, 24);
            }
            for (final Particles p : particles) {
                if (p.opacity > 4) p.render2D();
            }

            //resets color to white
            RenderUtil.Rise.color(fadeColor(Color.WHITE));
            if (timer.hasReached(1000 / 60)) {

                for (final Particles p : particles) {
                    p.updatePosition();

                    if (p.opacity < 1) particles.remove(p);
                }

                timer.reset();
            }
            if ((((EntityPlayer) target).hurtTime == 9 && !sentParticles)) {
                for (int i = 0; i < 15; i++) {
                    final Particles p = new Particles();
                    p.init((double) (sr.getScaledWidth() - 106) / 2f, (sr.getScaledHeight() / 2F + 120), ((Math.random() - 0.5) * 2) * 1.4, ((Math.random() - 0.5) * 2) * 1.4, Math.random() * 4, fadeColor(getSetting("ParticleColor").getColor()));
                    particles.add(p);
                }
                sentParticles = true;
            }
            if (((EntityPlayer) target).hurtTime == 8) sentParticles = false;
        }
    }


    public static void renderSteveModelTexture(final double x, final double y, final float u, final float v, final int uWidth, final int vHeight, final int width, final int height, final float tileWidth, final float tileHeight) {
        final ResourceLocation skin = new ResourceLocation("textures/entity/steve.png");
        mc.getTextureManager().bindTexture(skin);
        GL11.glEnable(GL11.GL_BLEND);
        Gui.drawScaledCustomSizeModalRect((int) x, (int) y, u, v, uWidth, vHeight, width, height, tileWidth, tileHeight);
        GL11.glDisable(GL11.GL_BLEND);
    }

    public static void renderPlayerModelTexture(final double x, final double y, final float u, final float v, final int uWidth, final int vHeight, final int width, final int height, final float tileWidth, final float tileHeight, final AbstractClientPlayer target) {
        final ResourceLocation skin = target.getLocationSkin();
        mc.getTextureManager().bindTexture(skin);
        GL11.glEnable(GL11.GL_BLEND);
        Gui.drawScaledCustomSizeModalRect((int) x, (int) y, u, v, uWidth, vHeight, width, height, tileWidth, tileHeight);
        GL11.glDisable(GL11.GL_BLEND);
    }

    Color fadeColor(Color color) {
        return new Color(color.getRed(), color.getGreen(), color.getBlue(), alpha);
    }

    Color fadeColor(Color color, boolean yuh) {
        return new Color(color.getRed(), color.getGreen(), color.getBlue(), Math.min(alpha, 101));
    }
}
