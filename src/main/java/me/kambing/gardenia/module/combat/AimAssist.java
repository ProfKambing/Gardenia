package me.kambing.gardenia.module.combat;

import com.google.common.eventbus.Subscribe;
import me.kambing.gardenia.UpdateEvent;
import me.kambing.gardenia.module.Category;
import me.kambing.gardenia.module.Module;
import me.kambing.gardenia.settings.Setting;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntitySquid;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MathHelper;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.apache.commons.lang3.RandomUtils;
import org.lwjgl.input.Mouse;

import static me.kambing.gardenia.module.combat.STap.isTeam;


public class AimAssist extends Module {

    public AimAssist() {
        super("AimAssist", "Assist in aiming", false, false, Category.Combat);


        new Setting("NewRotation", this, false);
        new Setting("Range", this, 3.5f, 1f, 6f, false);
        new Setting("Speed", this, 10f, 0.25f, 80f, false);
        new Setting("AimPitch", this, false);
        new Setting("SilentAim", this, false);
        new Setting("TeamCheck", this, true);
        new Setting("OnlyInFOV", this, true);
        new Setting("Jitter", this, false);
    }

    public Entity target;

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent e) {
        if (!isToggled()) return;
        if (mc.objectMouseOver.entityHit != null) return;
        if (target == null || !canAttack(target)) {
            target = getClosest(getSetting("Range").getValDouble());
        }


        if (mc.gameSettings.keyBindAttack.isKeyDown()) {
            this.faceEntity(target, Math.min(2.0 * this.getSetting("Speed").getValDouble(), this.getSetting("Speed").getValDouble() * 2.0 * Math.max(0.2, this.getDistanceFromMouse(target) / mc.gameSettings.fovSetting)));

            if (getSetting("Jitter").getValBoolean()) {
                mc.thePlayer.rotationYaw += Math.random() * 0.5 - 0.25;
                mc.thePlayer.rotationYaw = Math.round(mc.thePlayer.rotationYaw * 5000) / 5000.0F;

                mc.thePlayer.rotationPitch += Math.random() * 0.2 - 0.1;
                mc.thePlayer.rotationPitch = Math.max(mc.thePlayer.rotationPitch, -90);
                mc.thePlayer.rotationPitch = Math.min(mc.thePlayer.rotationPitch, 90);
            }
        }
    }


    public boolean canAttack(Entity target) {
        if (target instanceof EntityLivingBase) {
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
        if (getSetting("NewRotation").getValBoolean()) {
            rotations = this.getRotations();
        }
        if (rotations != null) {
            if (!getSetting("SilentAim").getValBoolean()) {
                mc.thePlayer.rotationYaw = (float) this.limitAngleChange(mc.thePlayer.prevRotationYaw, rotations[0] + RandomUtils.nextFloat(0.1f, 1.3f), speed);
            } else {
                //mc.thePlayer.rotationYawHead = (float) this.limitAngleChange(mc.thePlayer.prevRotationYaw, rotations[0] + RandomUtils.nextFloat(0.1f, 1.3f), speed);
                //mc.thePlayer.renderYawOffset = (float) this.limitAngleChange(mc.thePlayer.prevRotationYaw, rotations[0] + RandomUtils.nextFloat(0.1f, 1.3f), speed);
                //mc.objectMouseOver.entityHit = entity;
                //mc.playerController.attackEntity(mc.thePlayer, entity);
            }
            if (getSetting("AimPitch").getValBoolean()) {
                mc.thePlayer.rotationPitch = (float) this.limitAngleChange(mc.thePlayer.prevRotationPitch, rotations[1] + RandomUtils.nextFloat(0.1f, 0.9f), speed);
            }
        }
    }

    @Subscribe
    public void onUpdate(UpdateEvent e) {
        if (!isToggled()) return;
        if (!getSetting("SilentAim").getValBoolean()) return;

        float yaw = 0;
        float pitch = 0;
        float[] rotations = this.getRotationsNeeded(target);
        if (getSetting("NewRotation").getValBoolean()) {
            rotations = this.getRotations();
        }
        if (rotations != null) {
            yaw = (float) this.limitAngleChange(mc.thePlayer.prevRotationYaw, rotations[0] + RandomUtils.nextFloat(0.1f, 1.3f), getSetting("Speed").getValDouble());
            pitch = (float) this.limitAngleChange(mc.thePlayer.prevRotationPitch, rotations[1] + RandomUtils.nextFloat(0.1f, 0.9f), getSetting("Speed").getValDouble());
        }
        if (target != null && Mouse.isButtonDown(0)) {
            if (yaw != 0 && pitch != 0) {
                e.setYaw(yaw);
                e.setPitch(pitch);
            }
        }
    }

    @Subscribe
    public void onLook(UpdateEvent.LookEvent e) {
        if (!isToggled()) return;
        if (!getSetting("SilentAim").getValBoolean()) return;

        float yaw = 0;
        float pitch = 0;
        float[] rotations = this.getRotationsNeeded(target);
        if (getSetting("NewRotation").getValBoolean()) {
            rotations = this.getRotations();
        }
        if (rotations != null) {
            yaw = (float) this.limitAngleChange(mc.thePlayer.prevRotationYaw, rotations[0] + RandomUtils.nextFloat(0.1f, 1.3f), getSetting("Speed").getValDouble());
            pitch = (float) this.limitAngleChange(mc.thePlayer.prevRotationPitch, rotations[1] + RandomUtils.nextFloat(0.1f, 0.9f), getSetting("Speed").getValDouble());
        }
        if (target != null) {
            if (yaw != 0 && pitch != 0 && Mouse.isButtonDown(0)) {
                e.setYaw(yaw);
                e.setPitch(pitch);
            }
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

    private float[] getRotations() {
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


}
