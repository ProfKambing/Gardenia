package me.kambing.gardenia.clickgui.component.components.sub;

import com.mojang.realmsclient.gui.ChatFormatting;
import me.kambing.gardenia.Gardenia;
import me.kambing.gardenia.clickgui.component.Component;
import me.kambing.gardenia.clickgui.component.components.Button;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;

import java.awt.*;

//Your Imports


public class Keybind extends Component {

	private boolean hovered;
	private boolean binding;
	private final Button parent;
	private int offset;
	private int x;
	private int y;
	
	public Keybind(Button button, int offset) {
		this.parent = button;
		this.x = button.parent.getX() + button.parent.getWidth();
		this.y = button.parent.getY() + button.offset;
		this.offset = offset;
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
		Minecraft.getMinecraft().fontRendererObj.drawStringWithShadow(binding ? "Press a key..." : ("Key " + ChatFormatting.GRAY + (this.parent.mod.getKey() == 0 ? "None" : Keyboard.getKeyName(this.parent.mod.getKey()))), (parent.parent.getX() + 7) , (parent.parent.getY() + offset + 2) + 5, -1);
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
			this.binding = !this.binding;
		}
	}
	
	@Override
	public void keyTyped(char typedChar, int key) {
		if(this.binding) {
			this.parent.mod.setKey(key);
			this.binding = false;
		}
	}
	
	public boolean isMouseOnButton(int x, int y) {
		return x > this.x && x < this.x + 120 && y > this.y && y < this.y + 18;
	}
}
