package me.kambing.gardenia.clickgui.component.components.sub;

import com.mojang.realmsclient.gui.ChatFormatting;
import me.kambing.gardenia.Gardenia;
import me.kambing.gardenia.clickgui.component.Component;
import me.kambing.gardenia.clickgui.component.components.Button;
import me.kambing.gardenia.settings.Setting;
import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;

//Your Imports
import me.kambing.gardenia.module.Module;

import java.awt.*;

public class ModeButton extends Component {

	private boolean hovered;
	private final Button parent;
	private final Setting set;
	private int offset;
	private int x;
	private int y;
	private final Module mod;

	private int modeIndex;
	
	public ModeButton(Setting set, Button button, Module mod, int offset) {
		this.set = set;
		this.parent = button;
		this.mod = mod;
		this.x = button.parent.getX() + button.parent.getWidth();
		this.y = button.parent.getY() + button.offset;
		this.offset = offset;
		this.modeIndex = 0;
	}
	
	@Override
	public void setOff(int newOff) {
		offset = newOff;
	}
	
	@Override
	public void renderComponent(int mouseX, int mouseY) {
		Gui.drawRect(parent.parent.getX() + 2, parent.parent.getY() + offset, parent.parent.getX() + (parent.parent.getWidth() * 1), parent.parent.getY() + offset + 18, new Color(0,0,0, Gardenia.instance.moduleManager.clickGUI.getAlpha()).getRGB());
		Gui.drawRect(parent.parent.getX(), parent.parent.getY() + offset, parent.parent.getX() + 2, parent.parent.getY() + offset + 18, new Color(0,0,0, Gardenia.instance.moduleManager.clickGUI.getAlpha()).getRGB());
		GL11.glPushMatrix();
		GL11.glScalef(1f,1f, 1f);
		Minecraft.getMinecraft().fontRendererObj.drawStringWithShadow("Mode " + ChatFormatting.GRAY + set.getValString(), (parent.parent.getX() + 7) , (parent.parent.getY() + offset + 2)  + 5, -1);
		GL11.glPopMatrix();
	}
	
	@Override
	public void updateComponent(int mouseX, int mouseY) {
		this.hovered = isMouseOnButton(mouseX, mouseY);
		this.y = parent.parent.getY() + offset;
		this.x = parent.parent.getX();
	}
	
	@Override
	public void mouseClicked(int mouseX, int mouseY, int button) {
		if(isMouseOnButton(mouseX, mouseY) && button == 0 && this.parent.open) {
			int maxIndex = set.getOptions().size() - 1;

			if(modeIndex + 1 > maxIndex)
				modeIndex = 0;
			else
				modeIndex++;

			set.setValString(set.getOptions().get(modeIndex));
		}
	}
	
	public boolean isMouseOnButton(int x, int y) {
        return x > this.x && x < this.x + 120 && y > this.y && y < this.y + 18;
    }
}
