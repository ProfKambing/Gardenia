package me.kambing.gardenia.module.client;

import me.kambing.gardenia.Gardenia;
import me.kambing.gardenia.module.Category;
import me.kambing.gardenia.module.Module;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.awt.Color;

/**
 * @author kambing
 */
public class Watermark extends Module {

    public Watermark() {
        super("Watermark", "Watermark module", false, false, Category.Client);
    }

    @SubscribeEvent
    public void onRender(TickEvent.RenderTickEvent event) {
        //mc.fontRendererObj.drawStringWithShadow(Gardenia.MODID, 2.0f, 2f, Gardenia.instance.moduleManager.hud.getColor(1).getRGB());
        //mc.fontRendererObj.drawStringWithShadow(Gardenia.VERSION, 2.0f + mc.fontRendererObj.getStringWidth(Gardenia.MODID) + 2f, 2, Color.GRAY.getRGB());
        String string = Gardenia.MODID;
        float f = 0.0f;
        for (char c : string.toCharArray()) {
            mc.fontRendererObj.drawString(String.valueOf(c), 2.0f + f, 2, Gardenia.instance.moduleManager.hud.getColor((int) ((int)f)).getRGB(), true);
            f += (float) mc.fontRendererObj.getStringWidth(String.valueOf(c));
        }
        mc.fontRendererObj.drawString(Gardenia.VERSION, 2.0f + mc.fontRendererObj.getStringWidth(string) + 2, 2, Color.GRAY.getRGB(), true);
    }
}
