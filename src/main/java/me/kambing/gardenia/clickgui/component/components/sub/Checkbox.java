package me.kambing.gardenia.clickgui.component.components.sub;

import me.kambing.gardenia.Gardenia;
import me.kambing.gardenia.settings.Setting;
import me.kambing.gardenia.utils.RenderUtil;
import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;

//Your Imports
import me.kambing.gardenia.clickgui.component.Component;
import me.kambing.gardenia.clickgui.component.components.Button;

import java.awt.*;

public class Checkbox extends Component {

	private boolean hovered;
	private Setting op;
	private Button parent;
	private int offset;
	private int x;
	private int y;
	
	public Checkbox(Setting option, Button button, int offset) {
		this.op = option;
		this.parent = button;
		this.x = button.parent.getX() + button.parent.getWidth();
		this.y = button.parent.getY() + button.offset;
		this.offset = offset;
	}

	@Override
	public void renderComponent(int mouseX, int mouseY) {
		Gui.drawRect(parent.parent.getX() + 2, parent.parent.getY() + offset, parent.parent.getX() + (parent.parent.getWidth() * 1), parent.parent.getY() + offset + 18,new Color(0,0,0, Gardenia.instance.moduleManager.clickGUI.getAlpha()).getRGB());
		Gui.drawRect(parent.parent.getX(), parent.parent.getY() + offset, parent.parent.getX() + 2, parent.parent.getY() + offset + 18, new Color(0,0,0, Gardenia.instance.moduleManager.clickGUI.getAlpha()).getRGB());
		GL11.glPushMatrix();
		GL11.glScalef(1f,1f, 1f);
		Minecraft.getMinecraft().fontRendererObj.drawStringWithShadow(this.op.getName(), (parent.parent.getX() + 7) , (parent.parent.getY() + offset + 2) + 5, op.getValBoolean() ? Gardenia.instance.moduleManager.clickGUI.getColor().getRGB() : -1);
		GL11.glPopMatrix();
		//RenderUtil.drawOutlineRect(parent.parent.getX() + parent.parent.getWidth() - 3, parent.parent.getY() + offset + 5, parent.parent.getX() + parent.parent.getWidth() - 14, parent.parent.getY() + offset + 15, Color.BLACK.getRGB());
		//if(this.op.getValBoolean())
			//Gui.drawRect(parent.parent.getX() + parent.parent.getWidth() - 3, parent.parent.getY() + offset + 5, parent.parent.getX() + parent.parent.getWidth() - 14, parent.parent.getY() + offset + 15, Gardenia.instance.moduleManager.clickGUI.getColor().getRGB());
	}
	
	@Override
	public void setOff(int newOff) {
		offset = newOff;
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
			this.op.setValBoolean(!op.getValBoolean());;
		}
	}
	
	public boolean isMouseOnButton(int x, int y) {
		if(x > this.x && x < this.x + 120 && y > this.y && y < this.y + 18) {
			return true;
		}
		return false;
	}
}
