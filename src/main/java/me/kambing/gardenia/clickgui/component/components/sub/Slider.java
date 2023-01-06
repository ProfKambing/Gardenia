package me.kambing.gardenia.clickgui.component.components.sub;

import java.awt.*;
import java.math.BigDecimal;
import java.math.RoundingMode;

import com.mojang.realmsclient.gui.ChatFormatting;
import me.kambing.gardenia.Gardenia;
import me.kambing.gardenia.clickgui.component.Component;
import me.kambing.gardenia.clickgui.component.components.Button;
import me.kambing.gardenia.settings.Setting;
import me.kambing.gardenia.utils.RenderUtil;
import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;

//Your Imports


public class Slider extends Component {

	private boolean hovered;

	private Setting set;
	private Button parent;
	private int offset;
	private int x;
	private int y;
	private boolean dragging = false;

	private double renderWidth;
	
	public Slider(Setting value, Button button, int offset) {
		this.set = value;
		this.parent = button;
		this.x = button.parent.getX() + button.parent.getWidth();
		this.y = button.parent.getY() + button.offset;
		this.offset = offset;
	}
	
	@Override
	public void renderComponent(int mouseX, int mouseY) {
		Gui.drawRect(parent.parent.getX() + 2, parent.parent.getY() + offset, parent.parent.getX() + parent.parent.getWidth(), parent.parent.getY() + offset + 18, new Color(0,0,0, Gardenia.instance.moduleManager.clickGUI.getAlpha()).getRGB());
		Gui.drawRect(parent.parent.getX(), parent.parent.getY() + offset, parent.parent.getX() + 2, parent.parent.getY() + offset + 18, new Color(0,0,0, Gardenia.instance.moduleManager.clickGUI.getAlpha()).getRGB());
		 final int drag = (int)(this.set.getValDouble() / this.set.getMax() * this.parent.parent.getWidth());
		RenderUtil.drawRectCol(parent.parent.getX() + 2, parent.parent.getY() + offset + 15.5f, (int) renderWidth - 2, 5 / 3f - 0.5f, Gardenia.instance.moduleManager.clickGUI.getColor());
		GL11.glPushMatrix();
		GL11.glScalef(1f,1f, 1f);
		Minecraft.getMinecraft().fontRendererObj.drawStringWithShadow(this.set.getName() + " " + ChatFormatting.GRAY + this.set.getValDouble() ,(parent.parent.getX() + 7) , (parent.parent.getY() + offset + 2) + 5.6f, -1);
		
		GL11.glPopMatrix();
	}
	
	@Override
	public void setOff(int newOff) {
		offset = newOff;
	}
	
	@Override
	public void updateComponent(int mouseX, int mouseY) {
		this.hovered = isMouseOnButtonD(mouseX, mouseY) || isMouseOnButtonI(mouseX, mouseY);
		this.y = parent.parent.getY() + offset;
		this.x = parent.parent.getX();
		
		double diff = Math.min(120, Math.max(0, mouseX - this.x));

		double min = set.getMin();
		double max = set.getMax();
		
		renderWidth = (120) * (set.getValDouble() - min) / (max - min);
		
		if (dragging) {
			if (diff == 0) {
				set.setValDouble(set.getMin());
			}
			else {
				double newValue = roundToPlace(((diff / 120) * (max - min) + min), 2);
				set.setValDouble(newValue);
			}
		}
	}
	
	private static double roundToPlace(double value, int places) {
        if (places < 0) {
            throw new IllegalArgumentException();
        }
        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }
	
	@Override
	public void mouseClicked(int mouseX, int mouseY, int button) {
		if(isMouseOnButtonD(mouseX, mouseY) && button == 0 && this.parent.open) {
			dragging = true;
		}
		if(isMouseOnButtonI(mouseX, mouseY) && button == 0 && this.parent.open) {
			dragging = true;
		}
	}
	
	@Override
	public void mouseReleased(int mouseX, int mouseY, int mouseButton) {
		dragging = false;
	}
	
	public boolean isMouseOnButtonD(int x, int y) {
		if(x > this.x && x < this.x + (parent.parent.getWidth() / 2 + 1) && y > this.y && y < this.y + 18) {
			return true;
		}
		return false;
	}
	
	public boolean isMouseOnButtonI(int x, int y) {
		if(x > this.x + parent.parent.getWidth() / 2 && x < this.x + parent.parent.getWidth() && y > this.y && y < this.y + 18) {
			return true;
		}
		return false;
	}
}
