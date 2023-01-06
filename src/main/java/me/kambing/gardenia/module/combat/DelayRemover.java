package me.kambing.gardenia.module.combat;

import com.mojang.realmsclient.gui.ChatFormatting;
import me.kambing.gardenia.module.Category;
import me.kambing.gardenia.settings.Setting;
import me.kambing.gardenia.module.Module;
import me.kambing.gardenia.utils.MessageUtil;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.lang.reflect.Field;

// copied from raven b3
// @author Blowsy
public class DelayRemover extends Module {
    private boolean leftDisable, rightDisable;
    private Field leftCap = null;
    private Field rightCap = null;

    public DelayRemover() {
        super("DelayRemover", "Does not cap cps", false, false, Category.Combat);

        new Setting("RemoveLeftDelay", this, true);
        new Setting("RemoveRightDelay", this, true);
    }

    @Override
    public void onEnabled() {
        updateValues();
        if (leftDisable) {
            try {
                this.leftCap = mc.getClass().getDeclaredField("field_71429_W");
            } catch (Exception var4) {
                try {
                    this.leftCap = mc.getClass().getDeclaredField("leftClickCounter");
                } catch (Exception var3) {
                }
            }

            if (this.leftCap != null) {
                MessageUtil.sendMessage(ChatFormatting.GRAY + "[" + ChatFormatting.GOLD + "DelayRemover" + ChatFormatting.GRAY + "] " + ChatFormatting.RESET + "Made left accessible");
                this.leftCap.setAccessible(true);
            } else {
                this.toggle();
            }
        }

        if (rightDisable) {
            try {
                this.rightCap = mc.getClass().getDeclaredField("rightClickCounter");
            } catch (Exception var4) {
            }


            if (this.rightCap != null) {
                MessageUtil.sendMessage(ChatFormatting.GRAY + "[" + ChatFormatting.GOLD + "DelayRemover" + ChatFormatting.GRAY + "] " + ChatFormatting.RESET + "Made right accessible");
                this.rightCap.setAccessible(true);
            } else {
                this.toggle();
            }
        }
    }

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent e) {
        updateValues();
        if(leftDisable) {
            if (mc.thePlayer != null && mc.theWorld != null) {
                if (!mc.inGameHasFocus || mc.thePlayer.capabilities.isCreativeMode) {
                    return;
                }

                try {
                    this.leftCap.set(mc, 0);
                } catch (Exception what3) {
                    what3.printStackTrace();
                }
            }
        }

        if(rightDisable) {
            if (mc.thePlayer != null && mc.theWorld != null) {
                if (!mc.inGameHasFocus || mc.thePlayer.capabilities.isCreativeMode) {
                    return;
                }

                try {
                    this.rightCap.set(mc, 0);
                } catch (Exception what4) {
                    what4.printStackTrace();
                }
            }
        }
    }

    public void updateValues() {
        leftDisable = getSetting( "RemoveLeftDelay").getValBoolean();
        rightDisable = getSetting( "RemoveRightDelay").getValBoolean();

        if (!leftDisable && !rightDisable) {
            this.toggle();
        }
    }
}
