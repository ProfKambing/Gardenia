package me.kambing.gardenia.module.client;

import me.kambing.gardenia.Gardenia;
import me.kambing.gardenia.module.Category;
import me.kambing.gardenia.module.Module;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.awt.*;

/**
 * @author kambing
 */
public class Watermark extends Module {

    public Watermark() {
        super("Watermark", "Watermark module", false, false, Category.Client);
    }
    @SubscribeEvent
    public void onRender(TickEvent.RenderTickEvent event) {
        mc.fontRendererObj.drawStringWithShadow("Gardenia", 2.0f, 2f, Gardenia.instance.moduleManager.hud.getColor(1).getRGB());
        mc.fontRendererObj.drawStringWithShadow(Gardenia.VERSION, 2.0f + mc.fontRendererObj.getStringWidth("Gardenia") + 2f, 2, Color.GRAY.getRGB());
    }
}
