package me.kambing.gardenia.module.combat;

import com.google.common.eventbus.Subscribe;
import me.kambing.gardenia.Gardenia;
import me.kambing.gardenia.UpdateEvent;
import me.kambing.gardenia.module.Category;
import me.kambing.gardenia.module.Module;
import me.kambing.gardenia.settings.Setting;
import me.kambing.gardenia.utils.TimeUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntitySquid;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemSword;
import net.minecraft.util.MathHelper;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.apache.commons.lang3.RandomUtils;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.awt.event.MouseEvent;

import static me.kambing.gardenia.module.combat.HoldClicker.clickMouse;
import static me.kambing.gardenia.module.combat.STap.isTeam;


public class LegitAura extends Module {

    public LegitAura() {
        super("LegitAura", "legit aura", false, false, Category.Combat);


        new Setting("AttackSpeed", this, 20, 15, 25, true);
        new Setting("RandomAttacks", this, 3.5f, 1.5f, 6f, false);
        new Setting("Range", this, 4.5f, 1f, 6f, false);
        new Setting("Speed", this, 50f, 0.25f, 80f, false);
        new Setting("ButterSmooth", this, true);
        new Setting("TeamCheck", this, true);
        new Setting("OnlyInFOV", this, true);
        new Setting("BlockHit", this, false);
        new Setting("SilentRotate", this, true);
        new Setting("LimitAngle", this, true);
        new Setting("AutoSwitch", this, false);
        new Setting("Render", this, true);
    }

    public Entity target;
    TimeUtil.Timer timer = new TimeUtil.Timer();

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent e) {
        if (!isToggled()) return;
        if (target == null || !canAttack(target)) {
            target = getClosest(getSetting("Range").getValDouble());
        }
        if (target != null) {
            if (getSetting("AutoSwitch").getValBoolean()) {
                boolean foundSlot = false;
                int sword = 0;

                for (int i = 0; i < 9; i++) {
                    if (mc.thePlayer.inventory.getStackInSlot(i) == null)
                        continue;
                    if (mc.thePlayer.inventory.getStackInSlot(i).getItem() instanceof ItemSword) {
                        sword = i;
                        foundSlot = true;
                        break;
                    }
                }
                if (foundSlot) {
                    mc.thePlayer.inventory.currentItem = sword;
                }
            }
            if (!getSetting("SilentRotate").getValBoolean()) {
                this.faceEntity(target, Math.min(2.0 * this.getSetting("Speed").getValDouble(), this.getSetting("Speed").getValDouble() * 2.0 * Math.max(0.2, this.getDistanceFromMouse(target) / mc.gameSettings.fovSetting)));
            }
            if (timer.hasReached((long) (1000D / (RandomUtils.nextDouble(getSetting("AttackSpeed").getValDouble() - getSetting("RandomAttacks").getValDouble(), getSetting("AttackSpeed").getValDouble())))) && (mc.thePlayer.ticksExisted % 5 != 0 && mc.thePlayer.ticksExisted % 17 != 0)) {
                clickMouse();

                if (Minecraft.getMinecraft().objectMouseOver.entityHit != null) {
                    if (mc.thePlayer.inventory.getCurrentItem() != null && getSetting("BlockHit").getValBoolean()) {
                        if (mc.thePlayer.inventory.getCurrentItem().getItem() instanceof ItemSword) {
                            try {
                                Robot robot = new Robot();
                                robot.mousePress(MouseEvent.BUTTON3_DOWN_MASK);
                                robot.mouseRelease(MouseEvent.BUTTON3_DOWN_MASK);
                            } catch (Exception ignored) {
                            }
                        }
                    }
                }
                timer.reset();
            }
        }
    }

    //@Override
    //public void onPacketReceived(Packet<?> packet) {
    //if (packet instanceof C03PacketPlayer) {
    //  C03PacketPlayer packetPlayer = (C03PacketPlayer)packet;
    // if (getSetting("SilentRotate").getValBoolean()) {
    //  if (target != null) {
    //   float[] rotations = getSetting("ButterSmooth").getValBoolean() ? this.getRotations() : this.getRotationsNeeded(target);
    // if (rotations != null) {
    //   try {
    //   yaw.setAccessible(true);
    //  pitch.setAccessible(true);
    //  rotating.setAccessible(true);
    //packetPlayer.yaw = (float) this.limitAngleChange(mc.thePlayer.prevRotationYaw, rotations[0] + RandomUtils.nextFloat(0.1f, 1.3f), getSetting("Speed").getValDouble());
    //packetPlayer.pitch = (float) this.limitAngleChange(mc.thePlayer.prevRotationPitch, rotations[1] + RandomUtils.nextFloat(0.1f, 0.9f), getSetting("Speed").getValDouble());
    //                    //packetPlayer.rotating = true;
    //yaw.setFloat(packetPlayer.yaw, (float) this.limitAngleChange(mc.thePlayer.prevRotationYaw, rotations[0] + RandomUtils.nextFloat(0.1f, 1.3f), getSetting("Speed").getValDouble()));
    //pitch.setFloat(packetPlayer.pitch, (float) this.limitAngleChange(mc.thePlayer.prevRotationPitch, rotations[1] + RandomUtils.nextFloat(0.1f, 0.9f), getSetting("Speed").getValDouble()));
    //                   //rotating.setBoolean(packetPlayer.rotating, true);
    //     } catch (Exception ignored) {
    //        }
    //            }
    //        }
    //}
    //  }
    // }
    @SubscribeEvent
    public void onRenderGameOverlay(RenderWorldLastEvent event) {
        if (!Gardenia.instance.moduleManager.getModule("2DESP").isToggled() && target != null && getSetting("Render").getValBoolean()) {
           drawCircle(event.partialTicks, target, 0.67, true);
        }
    }

    @Subscribe
    public void onUpdate(UpdateEvent e) {
        if (!isToggled()) {
            target = null;
        }
        if (!getSetting("SilentRotate").getValBoolean()) return;

        float yaw = 0;
        float pitch = 0;
        float[] rotations = this.getRotationsNeeded(target);
        if (getSetting("ButterSmooth").getValBoolean()) {
            rotations = this.getRotations();
        }
        if (rotations != null) {
            yaw = getSetting("LimitAngle").getValBoolean() ? (float) this.limitAngleChange(mc.thePlayer.prevRotationYaw, rotations[0] + RandomUtils.nextFloat(0.1f, 1.3f), getSetting("Speed").getValDouble()) : rotations[0] + RandomUtils.nextFloat(0.1f, 1.3f);
            pitch = getSetting("LimitAngle").getValBoolean() ? (float) this.limitAngleChange(mc.thePlayer.prevRotationPitch, rotations[1] + RandomUtils.nextFloat(0.1f, 0.9f), getSetting("Speed").getValDouble()) : rotations[1] + RandomUtils.nextFloat(0.1f, 0.9f);
        }
        if (yaw != 0 && pitch != 0) {
            e.setYaw(yaw + RandomUtils.nextFloat(0.1f, 0.5f));
            e.setPitch(pitch + RandomUtils.nextFloat(0.1f, 1.0f));
        }
    }

    @Subscribe
    public void onLook(UpdateEvent.LookEvent e) {
        if (!getSetting("SilentRotate").getValBoolean()) return;

        float yaw = 0;
        float pitch = 0;
        float[] rotations = this.getRotationsNeeded(target);
        if (getSetting("ButterSmooth").getValBoolean()) {
            rotations = this.getRotations();
        }
        if (rotations != null) {
            yaw = getSetting("LimitAngle").getValBoolean() ? (float) this.limitAngleChange(mc.thePlayer.prevRotationYaw, rotations[0] + RandomUtils.nextFloat(0.1f, 1.3f), getSetting("Speed").getValDouble()) : rotations[0] + RandomUtils.nextFloat(0.1f, 1.3f);
            pitch = getSetting("LimitAngle").getValBoolean() ? (float) this.limitAngleChange(mc.thePlayer.prevRotationPitch, rotations[1] + RandomUtils.nextFloat(0.1f, 0.9f), getSetting("Speed").getValDouble()) : rotations[1] + RandomUtils.nextFloat(0.1f, 0.9f);
        }
        if (yaw != 0 && pitch != 0) {
            e.setYaw(yaw + RandomUtils.nextFloat(0.1f, 0.5f));
            e.setPitch(pitch + RandomUtils.nextFloat(0.1f, 1.0f));
        }
    }

    public boolean canAttack(Entity target) {
        if (target instanceof EntityLivingBase) {
            if (target.isDead) return false;
            if (target.getDisplayName().getUnformattedText().contains("[NPC] ")) return false;
            if (!(target instanceof EntityPlayer)) {
                return false;
            } else if (target.isInvisible() || target.isInvisibleToPlayer(mc.thePlayer)) {
                return false;
            } else if (getSetting("OnlyInFOV").getValBoolean() && !mc.thePlayer.canEntityBeSeen(target)) {
                return false;
            } else if (!(target instanceof EntityPlayer) && !(target instanceof EntityAnimal) && !(target instanceof EntitySquid) && !(target instanceof EntityVillager) && !(target instanceof EntityMob)) {
                return false;
            } else if (getSetting("TeamCheck").getValBoolean() && isTeam(mc.thePlayer, target)) {
                return false;
            } else {
                return target != mc.thePlayer && mc.thePlayer.isEntityAlive() && mc.thePlayer.getDistanceToEntity(target) <= this.getSetting("Range").getValDouble() && mc.thePlayer.ticksExisted > 100;
            }
        } else {
            return false;
        }
    }

    public EntityLivingBase getClosest(double range) {
        double dist = range;
        EntityLivingBase target1 = null;
        for (Object object : mc.theWorld.loadedEntityList) {
            Entity entity = (Entity) object;
            if (entity instanceof EntityLivingBase) {
                EntityLivingBase player = (EntityLivingBase) entity;
                if (canAttack(player)) {
                    double currentDist = mc.thePlayer.getDistanceToEntity(player);
                    if (currentDist <= dist) {
                        dist = currentDist;
                        target1 = player;
                    }
                }
            }
        }
        return target1;
    }

    private int getDistanceFromMouse(Entity entity) {
        float[] neededRotations = this.getRotationsNeeded(entity);
        if (neededRotations != null) {
            float distanceFromMouse = MathHelper.sqrt_float(mc.thePlayer.rotationYaw - neededRotations[0] * mc.thePlayer.rotationYaw - neededRotations[0] + mc.thePlayer.rotationPitch - neededRotations[1] * mc.thePlayer.rotationPitch - neededRotations[1]);
            return (int) distanceFromMouse;
        }
        return -1;
    }

    private float[] getRotationsNeeded(Entity entity) {
        if (entity == null) {
            return null;
        }
        double diffX = entity.posX - mc.thePlayer.posX;
        double diffY;
        if (entity instanceof EntityLivingBase) {
            EntityLivingBase entityLivingBase = (EntityLivingBase) entity;
            diffY = entityLivingBase.posY + entityLivingBase.getEyeHeight() * 0.9 - (mc.thePlayer.posY + mc.thePlayer.getEyeHeight());
        } else {
            diffY = (entity.getEntityBoundingBox().minY + entity.getEntityBoundingBox().maxY) / 2.0 - (mc.thePlayer.posY + mc.thePlayer.getEyeHeight());
        }
        double diffZ = entity.posZ - mc.thePlayer.posZ;
        double dist = MathHelper.sqrt_double(diffX * diffX + diffZ * diffZ);
        float yaw = (float) (Math.atan2(diffZ, diffX) * 180.0 / 3.141592653589793) - 90.0f;
        float pitch = (float) (-(Math.atan2(diffY, dist) * 180.0 / 3.141592653589793));
        return new float[]{mc.thePlayer.rotationYaw + MathHelper.wrapAngleTo180_float(yaw - mc.thePlayer.rotationYaw), mc.thePlayer.rotationPitch + MathHelper.wrapAngleTo180_float(pitch - mc.thePlayer.rotationPitch)};
    }

    private void faceEntity(Entity entity, double speed) {
        //speed += Math.random() * 4 - 3;
        //speed = Math.round(speed * 5000) / 5000.0D;

        float[] rotations = this.getRotationsNeeded(entity);
        if (getSetting("ButterSmooth").getValBoolean()) {
            rotations = this.getRotations();
        }
        if (rotations != null) {
            //mc.thePlayer.rotationYaw = (float) this.limitAngleChange(mc.thePlayer.prevRotationYaw, rotations[0] + RandomUtils.nextFloat(0.1f, 1.3f), speed);
            //mc.thePlayer.rotationPitch = (float) this.limitAngleChange(mc.thePlayer.prevRotationPitch, rotations[1] + RandomUtils.nextFloat(0.1f, 0.9f), speed);
        }
    }

    private double limitAngleChange(double current, double intended, double speed) {
        double change = intended - current;
        if (change > speed) {
            change = speed;
        } else if (change < -speed) {
            change = -speed;
        }
        return current + change;
    }

    public float[] getRotations() {
        if (target == null) return null;
        /*
         * Using some basic math this method gets the rotations needed to look inside an entities BoundingBox.
         *
         * Credits to Alan
         */
        final double var4 = (target.posX - (target.lastTickPosX - target.posX)) + 0.01 - mc.thePlayer.posX;
        final double var6 = (target.posZ - (target.lastTickPosZ - target.posZ)) - mc.thePlayer.posZ;
        final double var8 = (target.posY - (target.lastTickPosY - target.posY)) + 0.4 + target.getEyeHeight() / 1.3 - (mc.thePlayer.posY + mc.thePlayer.getEyeHeight());

        final double var14 = MathHelper.sqrt_double(var4 * var4 + var6 * var6);

        float yaw = (float) (Math.atan2(var6, var4) * 180.0D / Math.PI) - 90.0F;
        float pitch = (float) -(Math.atan2(var8, var14) * 180.0D / Math.PI);

        yaw = mc.thePlayer.rotationYaw + MathHelper.wrapAngleTo180_float(yaw - mc.thePlayer.rotationYaw);
        pitch = mc.thePlayer.rotationPitch + MathHelper.wrapAngleTo180_float(pitch - mc.thePlayer.rotationPitch);


        /*
         * Gets the current and last rotations and smooths them for aura to be harder to flag.
         */
        final float[] rotations = new float[]{yaw, pitch};

        yaw = rotations[0];
        pitch = rotations[1];


        // Clamps the pitch so that aura doesn't flag everything with an illegal rotation.
        pitch = MathHelper.clamp_float(pitch, -90.0F, 90.0F);

        return new float[]{yaw, pitch};
    }


    public void drawCircle(float partialTicks, final Entity entity, final double rad, final boolean shade) {
        if (mc.thePlayer == null) return;
        Color color1 = Gardenia.instance.moduleManager.hud.getColor(1);
        GL11.glPushMatrix();
        GL11.glDisable(3553);
        GL11.glEnable(2848);
        GL11.glEnable(2832);
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        GL11.glHint(3154, 4354);
        GL11.glHint(3155, 4354);
        GL11.glHint(3153, 4354);
        GL11.glDepthMask(false);
        GlStateManager.alphaFunc(GL11.GL_GREATER, 0.0F);
        if (shade) GL11.glShadeModel(GL11.GL_SMOOTH);
        GlStateManager.disableCull();
        GL11.glBegin(GL11.GL_TRIANGLE_STRIP);

        final double x = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * partialTicks - (mc.getRenderManager()).renderPosX;
        final double y = (entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * partialTicks - (mc.getRenderManager()).renderPosY) + Math.sin(System.currentTimeMillis() / 2E+2) + 1;
        final double z = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * partialTicks - (mc.getRenderManager()).renderPosZ;

        Color c;

        for (float i = 0; i < Math.PI * 2; i += Math.PI * 2 / 64.F) {
            final double vecX = x + rad * Math.cos(i);
            final double vecZ = z + rad * Math.sin(i);

            c = color1;

            if (shade) {
                GL11.glColor4f(c.getRed() / 255.F,
                        c.getGreen() / 255.F,
                        c.getBlue() / 255.F,
                        0
                );
                GL11.glVertex3d(vecX, y - Math.cos(System.currentTimeMillis() / 2E+2) / 2.0F, vecZ);
                GL11.glColor4f(c.getRed() / 255.F,
                        c.getGreen() / 255.F,
                        c.getBlue() / 255.F,
                        0.85F
                );
            }
            GL11.glVertex3d(vecX, y, vecZ);
        }

        GL11.glEnd();
        if (shade) GL11.glShadeModel(GL11.GL_FLAT);
        GL11.glDepthMask(true);
        GL11.glEnable(2929);
        GlStateManager.alphaFunc(GL11.GL_GREATER, 0.1F);
        GlStateManager.enableCull();
        GL11.glDisable(2848);
        GL11.glDisable(2848);
        GL11.glEnable(2832);
        GL11.glEnable(3553);
        GL11.glPopMatrix();
        GL11.glColor3f(255, 255, 255);
    }
}
