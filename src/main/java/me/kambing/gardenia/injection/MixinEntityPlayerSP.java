package me.kambing.gardenia.injection;

import com.mojang.authlib.GameProfile;
import me.kambing.gardenia.Gardenia;
import me.kambing.gardenia.UpdateEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(priority = 995, value = EntityPlayerSP.class)
public abstract class MixinEntityPlayerSP extends AbstractClientPlayer {

    public MixinEntityPlayerSP(World worldIn, GameProfile playerProfile) {
        super(worldIn, playerProfile);
    }

    @Override
    @Shadow
    public abstract void setSprinting(boolean p_setSprinting_1_);

    @Shadow
    protected Minecraft mc;

    @Override
    @Shadow
    protected abstract boolean pushOutOfBlocks(double p_pushOutOfBlocks_1_, double p_pushOutOfBlocks_3_,
                                               double p_pushOutOfBlocks_5_);

    @Override
    @Shadow
    public abstract void sendPlayerAbilities();

    @Shadow
    protected abstract boolean isCurrentViewEntity();
    @Shadow
    @Final
    public NetHandlerPlayClient sendQueue;

    @Override
    @Shadow
    public abstract boolean isSneaking();
    @Shadow
    private double lastReportedPosX;
    @Shadow
    private double lastReportedPosY;
    @Shadow
    private double lastReportedPosZ;
    @Shadow
    private float lastReportedYaw;
    @Shadow
    private float lastReportedPitch;
    @Shadow
    private int positionUpdateTicks;

    /**
     * @author kambing
     * @reason lel
     */
    @Overwrite
    public void onUpdateWalkingPlayer() {
        if (this.isCurrentViewEntity()) {

            UpdateEvent e = new UpdateEvent(this.posX, this.getEntityBoundingBox().minY, this.posZ,
                    this.rotationYaw, this.rotationPitch, this.onGround);
            Gardenia.eventBus.post(e);

            double d0 = e.getX() - this.lastReportedPosX;
            double d1 = e.getY() - this.lastReportedPosY;
            double d2 = e.getZ() - this.lastReportedPosZ;
            double d3 = e.getYaw() - this.lastReportedYaw;
            double d4 = e.getPitch() - this.lastReportedPitch;
            boolean flag2 = (((d0 * d0) + (d1 * d1) + (d2 * d2)) > 9.0E-4D) || (this.positionUpdateTicks >= 20);
            boolean flag3 = (d3 != 0.0D) || (d4 != 0.0D);
            if (this.ridingEntity == null) {
                if (flag2 && flag3)
                    this.sendQueue.addToSendQueue(new C03PacketPlayer.C06PacketPlayerPosLook(e.getX(), e.getY(),
                            e.getZ(), e.getYaw(), e.getPitch(), e.isOnGround()));
                else if (flag2)
                    this.sendQueue.addToSendQueue(
                            new C03PacketPlayer.C04PacketPlayerPosition(e.getX(), e.getY(), e.getZ(), e.isOnGround()));
                else if (flag3)
                    this.sendQueue.addToSendQueue(
                            new C03PacketPlayer.C05PacketPlayerLook(e.getYaw(), e.getPitch(), e.isOnGround()));
                else
                    this.sendQueue.addToSendQueue(new C03PacketPlayer(e.isOnGround()));
            } else {
                this.sendQueue.addToSendQueue(new C03PacketPlayer.C06PacketPlayerPosLook(this.motionX, -999.0D,
                        this.motionZ, e.getYaw(), e.getPitch(), e.isOnGround()));
                flag2 = false;
            }

            ++this.positionUpdateTicks;
            if (flag2) {
                this.lastReportedPosX = e.getX();
                this.lastReportedPosY = e.getY();
                this.lastReportedPosZ = e.getZ();
                this.positionUpdateTicks = 0;
            }

            if (flag3) {
                this.lastReportedYaw = e.getYaw();
                this.lastReportedPitch = e.getPitch();
            }
            e = UpdateEvent.convertPost(e);
            Gardenia.eventBus.post(e);
        }
    }
}
