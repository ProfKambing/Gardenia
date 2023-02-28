package me.kambing.gardenia.injection;

import me.kambing.gardenia.Gardenia;
import me.kambing.gardenia.UpdateEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;

@Mixin(priority = 1005, value = Entity.class)
public abstract class MixinEntity {

    @Shadow
    public float rotationYaw;

    @Shadow
    public float rotationPitch;

    @Shadow
    public float prevRotationPitch;

    @Shadow
    public float prevRotationYaw;

    /*
     * @author mc code
     * @reason lookevent
     */
    /*
   @Overwrite
   public Vec3 getLook(float partialTicks)
   {
       LookEvent e = new LookEvent(rotationPitch, prevRotationPitch, rotationYaw, prevRotationYaw);
       if((Object) this == Minecraft.getMinecraft().thePlayer)
           Raven.eventBus.post(e);
       if (partialTicks == 1.0F)
           return this.getVectorForRotation(e.getPitch(), e.getYaw());
       float f = e.getPrevPitch() + ((e.getPitch() - e.getPrevPitch()) * partialTicks);
       float f1 = e.getPrevYaw() + ((e.getYaw() - e.getPrevYaw()) * partialTicks);
       return this.getVectorForRotation(f, f1);
   } */

    /**
     * @author kambing
     * @reason skiddy skid skid skiddo bomb
     */
    @Overwrite
    public final Vec3 getVectorForRotation(float pitch, float yaw) {
        if((Object) this == Minecraft.getMinecraft().thePlayer) {
            UpdateEvent.LookEvent e = new UpdateEvent.LookEvent(pitch, yaw);
            Gardenia.eventBus.post(e);
            pitch = e.getPitch();
            yaw = e.getYaw();
        }
        float f = MathHelper.cos(-yaw * 0.017453292F - (float)Math.PI);
        float f1 = MathHelper.sin(-yaw * 0.017453292F - (float)Math.PI);
        float f2 = -MathHelper.cos(-pitch * 0.017453292F);
        float f3 = MathHelper.sin(-pitch * 0.017453292F);
        return new Vec3((double)(f1 * f2), (double)f3, (double)(f * f2));
    }

}
