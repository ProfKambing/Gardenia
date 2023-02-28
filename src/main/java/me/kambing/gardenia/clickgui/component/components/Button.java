package me.kambing.gardenia.clickgui.component.components;

import me.kambing.gardenia.Gardenia;
import me.kambing.gardenia.clickgui.component.Component;
import me.kambing.gardenia.clickgui.component.Frame;
import me.kambing.gardenia.clickgui.component.components.sub.Checkbox;
import me.kambing.gardenia.clickgui.component.components.sub.*;
import me.kambing.gardenia.module.Module;
import me.kambing.gardenia.settings.Setting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.ArrayList;

import static me.kambing.gardenia.utils.RenderUtil.mc;

public class Button extends Component {

    public Module mod;
    public Frame parent;
    public int offset;
    private boolean isHovered;
    private final ArrayList<Component> subcomponents;
    public boolean open;
    private final int height;

    public Button(Module mod, Frame parent, int offset) {
        this.mod = mod;
        this.parent = parent;
        this.offset = offset;
        this.subcomponents = new ArrayList<Component>();
        this.open = false;
        height = 18;
        int opY = offset + 18;
        if (Gardenia.instance.settingsManager.getSettingsByMod(mod) != null) {
            for (Setting s : Gardenia.instance.settingsManager.getSettingsByMod(mod)) {
                if (s.isCombo()) {
                    this.subcomponents.add(new ModeButton(s, this, mod, opY));
                    opY += 18;
                }
                if (s.isSlider()) {
                    this.subcomponents.add(new Slider(s, this, opY));
                    opY += 18;
                }
                if (s.isCheck()) {
                    this.subcomponents.add(new Checkbox(s, this, opY));
                    opY += 18;
                }
                if (s.isColor()) {
                    this.subcomponents.add(new ColorButton(s, this, opY));
                    opY += 18;
                }
            }
        }
        this.subcomponents.add(new Keybind(this, opY));
    }

    @Override
    public void setOff(int newOff) {
        offset = newOff;
        int opY = offset + 18;
        for (Component comp : this.subcomponents) {
            comp.setOff(opY);
            opY += 18;
        }
    }

    @Override
    public void renderComponent(int mouseX, int mouseY) {
        Gui.drawRect(parent.getX(), this.parent.getY() + offset, parent.getX() + parent.getWidth(), this.parent.getY() + 18 + this.offset, new Color(0,0,0, Gardenia.instance.moduleManager.clickGUI.getAlpha()).getRGB());
        GL11.glPushMatrix();
        GL11.glScalef(1f,1f, 1f);
        Minecraft.getMinecraft().fontRendererObj.drawStringWithShadow(this.mod.getName(), (parent.getX() + 3.5f), (parent.getY() + offset + 2) + 4, this.mod.isToggled() ? Gardenia.instance.moduleManager.clickGUI.getColor().getRGB() : -1);
        if(this.subcomponents.size() > 1)
        Minecraft.getMinecraft().fontRendererObj.drawStringWithShadow(this.open ? "-" : "+", (parent.getX() + parent.getWidth() - 8), (parent.getY() + offset + 2) + 4, -1);
        GL11.glPopMatrix();
        if (this.open) {
            if (!this.subcomponents.isEmpty()) {
                for (Component comp : this.subcomponents) {
                    comp.renderComponent(mouseX, mouseY);
                }
                Gui.drawRect(parent.getX() + 2, parent.getY() + this.offset + 18, parent.getX() + 3, parent.getY() + this.offset + ((this.subcomponents.size() + 1) * 18), Gardenia.instance.moduleManager.clickGUI.getColor().getRGB());
            }
        }
    }

    @Override
    public int getHeight() {
        if (this.open) {
            return (18 * (this.subcomponents.size() + 1));
        }
        return 18;
    }

    @Override
    public void updateComponent(int mouseX, int mouseY) {
        this.isHovered = isMouseOnButton(mouseX, mouseY);
        if (isHovered) {
            mc.fontRendererObj.drawStringWithShadow(mod.getDescription(), 2, (new ScaledResolution(mc).getScaledHeight() - mc.fontRendererObj.FONT_HEIGHT - 2), -1);
        }
        if (!this.subcomponents.isEmpty()) {
            for (Component comp : this.subcomponents) {
                comp.updateComponent(mouseX, mouseY);
            }
        }
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int button) {
        if (isMouseOnButton(mouseX, mouseY) && button == 0) {
            this.mod.toggle();
        }
        if (isMouseOnButton(mouseX, mouseY) && button == 1) {
            this.open = !this.open;
            this.parent.refresh();
        }
        if (isMouseOnButton(mouseX, mouseY) && button == 2) {
            this.mod.visible = !this.mod.visible;
        }
        for (Component comp : this.subcomponents) {
            comp.mouseClicked(mouseX, mouseY, button);
        }
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int mouseButton) {
        for (Component comp : this.subcomponents) {
            comp.mouseReleased(mouseX, mouseY, mouseButton);
        }
    }

    @Override
    public void keyTyped(char typedChar, int key) {
        for (Component comp : this.subcomponents) {
            comp.keyTyped(typedChar, key);
        }
    }

    public boolean isMouseOnButton(int x, int y) {
        return x > parent.getX() && x < parent.getX() + parent.getWidth() && y > this.parent.getY() + this.offset && y < this.parent.getY() + 18 + this.offset;
    }
}
