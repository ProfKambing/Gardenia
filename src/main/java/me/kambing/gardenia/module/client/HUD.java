package me.kambing.gardenia.module.client;

import me.kambing.gardenia.Gardenia;
import me.kambing.gardenia.module.Category;
import me.kambing.gardenia.module.Module;
import me.kambing.gardenia.settings.Setting;
import me.kambing.gardenia.utils.ModuleUtil;
import me.kambing.gardenia.utils.RenderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.awt.Color;
import java.util.ArrayList;

public class HUD extends Module {
    private boolean active;
    private int modColor;
    private int wmColor;
    private String sortMode;
    public ArrayList<Module> modList;

    public HUD() {
        super("HUD", "Draws the module list on your screen", false, false, Category.Client);

        ArrayList<String> sort = new ArrayList<String>();
        sort.add("Length long > short");
        sort.add("Length short > long");
        sort.add("Alphabet");
        sort.add("idfc");

        new Setting("Watermark", this, true);
        new Setting("Background", this, false);
        new Setting("TextShadow", this, true);
        new Setting("Horizontal", this, true);
        new Setting("TestVH", this, true);
        new Setting("ArraylistSort", this, "Length long > short", sort);

        new Setting("ModulePadding", this, 2, 0, 10, true);
        new Setting("WatermarkPadding", this, 3, 0, 10, true);
        new Setting("MiniBoxWidth", this, 1, 0, 10, true);
        new Setting("YOffset", this, 4, 0, 10, true);
        new Setting("XOffset", this, 4, 0, 10, true);
        new Setting("Speed", this, 4, 1, 100, true);
        ArrayList<String> colorSetting = new ArrayList<String>();
        colorSetting.add("Static");
        colorSetting.add("Rainbow");
        colorSetting.add("Alphastep");
        colorSetting.add("SecondColor");
        colorSetting.add("Astolfo");
        new Setting("Color", this, "Alphastep", colorSetting);
        new Setting("HUDColor", this, Color.MAGENTA);
        new Setting("SecondColor", this, Color.RED);

    }

    @SubscribeEvent
    public void onRender(TickEvent.RenderTickEvent event) {
        if (!isToggled()) return;
        if (mc.currentScreen != null) return;
        if (mc.thePlayer == null || mc.theWorld == null) return;
        int modCount = 0;
        int counter = 0;
        if (Gardenia.instance.destructed) {
            return;
        }

        wmColor = getColor(1).getRGB();
        sortMode = getSetting("ArraylistSort").getValString();
        boolean watermark = getSetting("Watermark").getValBoolean();
        boolean background = getSetting("Background").getValBoolean();
        boolean textShadow = getSetting("TextShadow").getValBoolean();
        // Renders active modules
        ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());


        int margin = (int) getSetting("ModulePadding").getValDouble();
        int waterMarkMargin = (int) getSetting("WatermarkPadding").getValDouble();
        int topOffSet = (int) getSetting("YOffset").getValDouble();
        int rightOffSet = (int) getSetting("XOffset").getValDouble();
        int miniboxWidth = (int) getSetting("MiniBoxWidth").getValDouble();

        if (watermark) {
            String waterMarkText = Gardenia.MODID + " " + Gardenia.VERSION;
            FontRenderer fr = Minecraft.getMinecraft().fontRendererObj;
            if (background) {
                Gui.drawRect(sr.getScaledWidth() - fr.getStringWidth(waterMarkText) - waterMarkMargin * 2 - rightOffSet, topOffSet, sr.getScaledWidth() - rightOffSet, topOffSet + waterMarkMargin * 2 + fr.FONT_HEIGHT, 0x90000000);
                Gui.drawRect(sr.getScaledWidth() - fr.getStringWidth(waterMarkText) - waterMarkMargin * 2 - rightOffSet - miniboxWidth, topOffSet, sr.getScaledWidth() - fr.getStringWidth(waterMarkText) - waterMarkMargin * 2 - rightOffSet, topOffSet + waterMarkMargin * 2 + fr.FONT_HEIGHT, wmColor);
            }
            if (textShadow) {
                fr.drawStringWithShadow(waterMarkText, sr.getScaledWidth() - fr.getStringWidth(waterMarkText) - rightOffSet - waterMarkMargin, topOffSet + waterMarkMargin, wmColor);
            } else {
                fr.drawString(waterMarkText, sr.getScaledWidth() - fr.getStringWidth(waterMarkText) - rightOffSet - waterMarkMargin, topOffSet + waterMarkMargin, wmColor);
            }

            topOffSet += fr.FONT_HEIGHT + waterMarkMargin * 2;
        }


        modList = Gardenia.instance.moduleManager.getModulesList();

        if (sortMode.equalsIgnoreCase("Length long > short")) {
            modList = ModuleUtil.longShort();
        } else if (sortMode.equalsIgnoreCase("Length short > long")) {
            modList = ModuleUtil.shortLong();
        } else if (sortMode.equalsIgnoreCase("Alphabet")) {
            modList = ModuleUtil.abcList();
        }

        for (Module mod : modList) {
            modColor = getColor(modCount + counter).getRGB();
            if (mod.visible && mod.isToggled()) {
                FontRenderer fr = Minecraft.getMinecraft().fontRendererObj;
                if (background) {
                    Gui.drawRect(sr.getScaledWidth() - fr.getStringWidth(mod.getName()) - margin * 2 - rightOffSet, topOffSet, sr.getScaledWidth() - rightOffSet, topOffSet + margin * 2 + fr.FONT_HEIGHT, 0x90000000);
                    Gui.drawRect(sr.getScaledWidth() - fr.getStringWidth(mod.getName()) - margin * 2 - rightOffSet - miniboxWidth, topOffSet, sr.getScaledWidth() - fr.getStringWidth(mod.getName()) - margin * 2 - rightOffSet, topOffSet + margin * 2 + fr.FONT_HEIGHT, modColor);
                }
                float f = 0.0f;
                if (textShadow) {
                    for (char c : mod.getName().toCharArray()) {
                        mc.fontRendererObj.drawStringWithShadow(String.valueOf(c), sr.getScaledWidth() - fr.getStringWidth(mod.getName()) - rightOffSet - margin + f, topOffSet + margin, getSetting("Horizontal").getValBoolean() ? Gardenia.instance.moduleManager.hud.getColor((int) ((int)f)).getRGB() : modColor);
                        f += (float) mc.fontRendererObj.getStringWidth(String.valueOf(c));
                    }
                    //fr.drawStringWithShadow(mod.getName(), sr.getScaledWidth() - fr.getStringWidth(mod.getName()) - rightOffSet - margin, topOffSet + margin, modColor);
                } else {
                    for (char c : mod.getName().toCharArray()) {
                        mc.fontRendererObj.drawString(String.valueOf(c), sr.getScaledWidth() - fr.getStringWidth(mod.getName()) - rightOffSet - margin + f, topOffSet + margin, getSetting("Horizontal").getValBoolean() ? Gardenia.instance.moduleManager.hud.getColor((int) ((int)f)).getRGB() : modColor, true);
                        f += (float) mc.fontRendererObj.getStringWidth(String.valueOf(c));
                    }
                    //fr.drawString(mod.getName(), sr.getScaledWidth() - fr.getStringWidth(mod.getName()) - rightOffSet - margin, topOffSet + margin, modColor);
                }

                topOffSet += fr.FONT_HEIGHT + margin * 2;
            }
            modCount++;
            counter++;
        }
    }

    public Color getColor(int modCount) {
        String colorMode = getSetting("Color").getValString();
        switch (colorMode) {
            case "Static":
                return getSetting("HUDColor").getColor();
            case "Rainbow":
                return RenderUtil.rainbow(modCount * 100);
            case "Alphastep":
                return RenderUtil.alphaStep(getSetting("HUDColor").getColor(), 50, (int) (((modCount * 2) + 15) / 0.5f));
            case "SecondColor":
                return RenderUtil.interpolateColorsBackAndForth((int) getSetting("Speed").getValDouble(), modCount, getSetting("HUDColor").getColor(), getSetting("SecondColor").getColor(), true);
            case "Astolfo":
                return new Color(RenderUtil.astolfo(modCount));
        }
        return Color.red;
    }

    @Override
    public void onEnabled() {
        super.onEnabled();
        this.active = true;
    }

    @Override
    public void onDisabled() {
        super.onDisabled();
        this.active = false;
    }

    public static HUD getInstance() {
        return new HUD();
    }
}
