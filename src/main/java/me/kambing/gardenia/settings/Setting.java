package me.kambing.gardenia.settings;

import me.kambing.gardenia.Gardenia;
import me.kambing.gardenia.module.Module;

import java.awt.*;
import java.util.ArrayList;

/**
 * Made by HeroCode
 * it's free to use
 * but you have to credit him
 *
 * @author HeroCode
 */
public class Setting {

    private final String name;
    private final Module parent;
    private final String mode;

    private String sval;
    private ArrayList<String> options;

    private boolean bval;

    private double dval;
    private double min;
    private double max;
    private boolean onlyint = false;
    private Color color;
    private boolean open = false;


    public Setting(String name, Module parent, String sval, ArrayList<String> options) {
        this.name = name;
        this.parent = parent;
        this.sval = sval;
        this.options = options;
        this.mode = "Combo";
        Gardenia.instance.settingsManager.addSetting(this);
    }

    public Setting(String name, Module parent, boolean bval) {
        this.name = name;
        this.parent = parent;
        this.bval = bval;
        this.mode = "Check";
        Gardenia.instance.settingsManager.addSetting(this);
    }

    public Setting(String name, Module parent, double dval, double min, double max, boolean onlyint) {
        this.name = name;
        this.parent = parent;
        this.dval = dval;
        this.min = min;
        this.max = max;
        this.onlyint = onlyint;
        this.mode = "Slider";
        Gardenia.instance.settingsManager.addSetting(this);
    }

    public Setting(String name, Module parent, Color color) {
        this.color = color;
        this.name = name;
        this.parent = parent;
        this.mode = "Color";
        Gardenia.instance.settingsManager.addSetting(this);
    }

    public String getName() {
        return name;
    }

    public Module getParentMod() {
        return parent;
    }

    public String getValString() {
        return this.sval;
    }

    public void setValString(String in) {
        this.sval = in;
        if (Gardenia.instance.saveLoad != null) {
            Gardenia.instance.saveLoad.save();
        }
    }

    public ArrayList<String> getOptions() {
        return this.options;
    }

    public boolean getValBoolean() {
        return this.bval;
    }

    public void setValBoolean(boolean in) {
        this.bval = in;
        if (Gardenia.instance.saveLoad != null) {
            Gardenia.instance.saveLoad.save();
        }
    }

    public void setOpen(boolean open) {
        this.open = open;
    }

    public boolean isOpen() {
        return this.open;
    }

    public double getValDouble() {
        if (this.onlyint) {
            this.dval = (int) dval;
        }
        return this.dval;
    }

    public void setValDouble(double in) {
        this.dval = in;
        if (Gardenia.instance.saveLoad != null) {
            Gardenia.instance.saveLoad.save();
        }
    }

    public Color getColor() {
        return this.color;
    }

    public void setColor(Color color) {
        this.color = color;
        if (Gardenia.instance.saveLoad != null) {
            Gardenia.instance.saveLoad.save();
        }
    }

    public double getMin() {
        return this.min;
    }

    public double getMax() {
        return this.max;
    }

    public boolean isCombo() {
        return this.mode.equalsIgnoreCase("Combo");
    }

    public boolean isCheck() {
        return this.mode.equalsIgnoreCase("Check");
    }

    public boolean isSlider() {
        return this.mode.equalsIgnoreCase("Slider");
    }

    public boolean isColor() {
        return this.mode.equalsIgnoreCase("Color");
    }

    public boolean onlyInt() {
        return this.onlyint;
    }
}
