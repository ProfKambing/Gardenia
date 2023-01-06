package me.kambing.gardenia.injection;

import me.kambing.gardenia.utils.CapeUtil;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.util.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;
import java.util.Objects;

@Mixin(AbstractClientPlayer.class)
public abstract class MixinAbstractClientPlayer {

    @Shadow
    @Nullable
    protected abstract NetworkPlayerInfo getPlayerInfo();

    @Inject(method = "getLocationCape", at = {@At(value = "HEAD")}, cancellable = true)
    public void getLocationCape(CallbackInfoReturnable<ResourceLocation> callbackInfoReturnable) {
        if (Objects.requireNonNull(getPlayerInfo()).getGameProfile().getName().equals("kambinq")) {
            callbackInfoReturnable.setReturnValue(new ResourceLocation("textures/sovietcape.png"));
        }    else if (Objects.requireNonNull(getPlayerInfo()).getGameProfile().getName().equals("Cryz_Bored")) {
            callbackInfoReturnable.setReturnValue(new ResourceLocation("textures/capetelor.png"));
        }    else if (Objects.requireNonNull(getPlayerInfo()).getGameProfile().getName().equals("leafyzzz")) {
            callbackInfoReturnable.setReturnValue(new ResourceLocation("textures/sovietcape.png"));
        }    else if (Objects.requireNonNull(getPlayerInfo()).getGameProfile().getName().equals("Noorf")) {
            callbackInfoReturnable.setReturnValue(new ResourceLocation("textures/capenazi.png"));
        } else if (CapeUtil.isUUIDValid(Objects.requireNonNull(getPlayerInfo()).getGameProfile().getName())) {
            callbackInfoReturnable.setReturnValue(new ResourceLocation("textures/cape.png"));
        }
    }
}