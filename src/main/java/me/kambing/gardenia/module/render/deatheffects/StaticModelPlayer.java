package me.kambing.gardenia.module.render.deatheffects;

import net.minecraft.client.model.ModelPlayer;
import net.minecraft.entity.player.EntityPlayer;

public class StaticModelPlayer extends ModelPlayer {
    private final EntityPlayer player;
    private float limbSwing;
    private float limbSwingAmount;
    private float yaw;
    private float yawHead;
    private float pitch;

    public StaticModelPlayer(EntityPlayer playerIn, float modelSize, float partialTicks, boolean slim) {
        super(modelSize, slim);
        this.player = playerIn;
        this.limbSwing = player.limbSwing;
        this.limbSwingAmount = player.limbSwingAmount;
        this.yaw = player.rotationYaw;
        this.yawHead = player.rotationYawHead;
        this.pitch = player.rotationPitch;
        this.isSneak = player.isSneaking();
        this.swingProgress = player.swingProgress;
        this.setLivingAnimations(player, limbSwing, limbSwingAmount, partialTicks);
    }

    public void render(float scale) {
        this.render(player, limbSwing, limbSwingAmount, player.ticksExisted, yawHead, pitch, scale);
    }

    public void disableArmorLayers() {
        this.bipedBodyWear.showModel = false;
        this.bipedLeftLegwear.showModel = false;
        this.bipedRightLegwear.showModel = false;
        this.bipedLeftArmwear.showModel = false;
        this.bipedRightArmwear.showModel = false;
        this.bipedHeadwear.showModel = true;
        this.bipedHead.showModel = false;
    }

    public EntityPlayer getPlayer() {
        return player;
    }

    public float getLimbSwing() {
        return limbSwing;
    }

    public void setLimbSwing(float limbSwing) {
        this.limbSwing = limbSwing;
    }

    public float getLimbSwingAmount() {
        return limbSwingAmount;
    }

    public void setLimbSwingAmount(float limbSwingAmount) {
        this.limbSwingAmount = limbSwingAmount;
    }

    public float getYaw() {
        return yaw;
    }

    public void setYaw(float yaw) {
        this.yaw = yaw;
    }

    public float getYawHead() {
        return yawHead;
    }

    public void setYawHead(float yawHead) {
        this.yawHead = yawHead;
    }

    public float getPitch() {
        return pitch;
    }

    public void setPitch(float pitch) {
        this.pitch = pitch;
    }
}
