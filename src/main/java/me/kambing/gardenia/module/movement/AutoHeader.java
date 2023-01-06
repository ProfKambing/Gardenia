package me.kambing.gardenia.module.movement;

import me.kambing.gardenia.module.Category;
import me.kambing.gardenia.module.Module;
import me.kambing.gardenia.settings.Setting;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.input.Keyboard;

import java.util.concurrent.ThreadLocalRandom;

public class AutoHeader extends Module {
    private double startWait;

    public AutoHeader() {
        super("AutoHeadHitter", "Hits your head so hard your mom aborts you",false, false, Category.Movement);
        new Setting("UnderBlockOnly", this, true);
        new Setting("CancelIfSneaking", this, true);
        new Setting("OnlyWhenHoldingJump", this,true);
        new Setting("JumpsPerSecond", this , 1,1, 20, true);

        boolean jumping = false;
    }

    @Override
    public void onEnabled(){
        startWait = System.currentTimeMillis();
        super.onEnabled();
    }

    @SubscribeEvent
    public void onTick(TickEvent.RenderTickEvent e) {
        if (mc.theWorld == null || mc.currentScreen != null)
            return;
        if (getSetting("CancelIfSneaking").getValBoolean() && mc.thePlayer.isSneaking())
            return;

        if(getSetting("OnlyWhenHoldingJump").getValBoolean()){
            if(!Keyboard.isKeyDown(mc.gameSettings.keyBindJump.getKeyCode())){
                return;
            }
        }


        if (playerUnderBlock() && mc.thePlayer.onGround){
            if(startWait + (1000 / ThreadLocalRandom.current().nextDouble(getSetting("JumpsPerSecond").getValDouble() - 0.543543, getSetting("JumpsPerSecond").getValDouble() + 1.32748923)) < System.currentTimeMillis()){
                mc.thePlayer.jump();
                startWait = System.currentTimeMillis();
            }
        }

    }
    public static boolean playerUnderBlock() {
        double x = mc.thePlayer.posX;
        double y = mc.thePlayer.posY + 2.0D;
        double z = mc.thePlayer.posZ;
        BlockPos p = new BlockPos(MathHelper.floor_double(x), MathHelper.floor_double(y), MathHelper.floor_double(z));
        return mc.theWorld.isBlockFullCube(p) || mc.theWorld.isBlockNormalCube(p, false);
    }
}
