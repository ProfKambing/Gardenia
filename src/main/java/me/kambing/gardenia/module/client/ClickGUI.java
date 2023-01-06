package me.kambing.gardenia.module.client;

import me.kambing.gardenia.Gardenia;
import me.kambing.gardenia.module.Category;
import me.kambing.gardenia.module.Module;
import me.kambing.gardenia.settings.Setting;
import me.kambing.gardenia.utils.RenderUtil;
import org.lwjgl.input.Keyboard;

import java.awt.*;
import java.util.ArrayList;

public class ClickGUI extends Module {
    public ClickGUI(){
        super("ClickGUI", "Allows you to manage modules", false, true, Category.Client);
        ArrayList<String> colorSetting = new ArrayList<String>();
        colorSetting.add("Static");
        colorSetting.add("Rainbow");
        colorSetting.add("Alphastep");
        new Setting("Color", this, "Alphastep", colorSetting);
        new Setting("GUIColor", this, Color.ORANGE);
        new Setting("BackgroundAlpha", this, 58, 0, 200, true);
        new Setting("RainbowSpeed", this, 40, 0, 100, true);
        //new Setting("test", this, 40, 0, 100, true);
        //new Setting("test2", this, 40, 0, 100, true);
        //new Setting("test3", this, 40, 0, 100, true);
        //new Setting("test4", this, 40, 0, 100, true);
        //new Setting("test5", this, 40, 0, 100, true);
        //new Setting("test6", this, 40, 0, 100, true);
        this.setKey(Keyboard.KEY_RSHIFT);
    }
    public static ClickGUI getInstance() {
        return new ClickGUI();
    }

    public Color getColor() {
        if (getSetting("Color").getValString().equals("Alphastep")) {
            return RenderUtil.alphaStep(getSetting("GUIColor").getColor(), 50,  15);
        }
        if (getSetting("Color").getValString().equals("Rainbow")) {
            return RenderUtil.rainbow((int)getSetting("RainbowSpeed").getValDouble());
        }
        return getSetting("GUIColor").getColor();
    }
    public int getAlpha() {
        return (int)getSetting("BackgroundAlpha").getValDouble();
    }
   // public int test() {return (int) getSetting("test").getValDouble();}
    //public int test2() {return (int) getSetting("test2").getValDouble();}
    //public int test3() {return (int) getSetting("test3").getValDouble();}
    //public int test4() {return (int) getSetting("test4").getValDouble();}
    //public int test5() {return (int) getSetting("test5").getValDouble();}
    //public int test6() {return (int) getSetting("test6").getValDouble();}


    @Override
    public void onEnabled() {
        super.onEnabled();
        mc.displayGuiScreen(Gardenia.instance.clickGui);
        this.setToggled(false);
    }
}
