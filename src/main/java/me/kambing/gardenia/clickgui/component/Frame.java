package me.kambing.gardenia.clickgui.component;

import java.awt.*;
import java.util.ArrayList;

import me.kambing.gardenia.clickgui.component.components.Button;
import me.kambing.gardenia.Gardenia;
import me.kambing.gardenia.utils.RenderUtil;
import org.lwjgl.opengl.GL11;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;

//Your Imports
import me.kambing.gardenia.clickgui.ClickGui;
import me.kambing.gardenia.module.Category;
import me.kambing.gardenia.module.Module;

public class Frame {

	public ArrayList<Component> components;
	public Category category;
	private boolean open;
	private int width;
	private int y;
	private int x;
	private int barHeight;
	private boolean isDragging;
	public int dragX;
	public int dragY;
	
	public Frame(Category cat) {
		this.components = new ArrayList<Component>();
		this.category = cat;
		this.width = 120;
		this.x = 5;
		this.y = 5;
		this.barHeight = 18;
		this.dragX = 0;
		this.open = true;
		this.isDragging = false;
		int tY = this.barHeight;
		
		/**
		 * 		public ArrayList<Module> getModulesInCategory(Category categoryIn){
		 * 			ArrayList<Module> mods = new ArrayList<Module>();
		 * 			for(Module m : this.modules){
		 * 				if(m.getCategory() == categoryIn)
		 * 					mods.add(m);
		 * 			}
		 * 			return mods;
		 * 		}
		 */

		for(Module mod : Gardenia.instance.moduleManager.getModulesInCategory(category)) {
			Button modButton = new Button(mod, this, tY);
			this.components.add(modButton);
			tY += 18;
		}
	}
	
	public ArrayList<Component> getComponents() {
		return components;
	}
	
	public void setX(int newX) {
		this.x = newX;
	}
	
	public void setY(int newY) {
		this.y = newY;
	}
	
	public void setDrag(boolean drag) {
		this.isDragging = drag;
	}
	
	public boolean isOpen() {
		return open;
	}
	
	public void setOpen(boolean open) {
		this.open = open;
	}
	
	public void renderFrame(int mouseX, int mouseY,FontRenderer fontRenderer) {
		Gui.drawRect((this.x - 1), (this.y + 4), (this.x + this.width + 1), (this.y + this.barHeight), new Color(Gardenia.instance.moduleManager.clickGUI.getColor().getRed(),Gardenia.instance.moduleManager.clickGUI.getColor().getGreen(),Gardenia.instance.moduleManager.clickGUI.getColor().getBlue(),180).getRGB());
		RenderUtil.drawOutlineRect(this.x - 1, this.y + 4, this.x + this.width + 1, this.y + this.barHeight, new Color(Gardenia.instance.moduleManager.clickGUI.getColor().getRed(),Gardenia.instance.moduleManager.clickGUI.getColor().getGreen(),Gardenia.instance.moduleManager.clickGUI.getColor().getBlue(),255).getRGB());
		GL11.glPushMatrix();
		GL11.glScalef(1f,1f, 1f);
		fontRenderer.drawStringWithShadow(this.category.name(), (this.x + 2) + 5, (this.y + 6.5f), 0xFFFFFFFF);
		GL11.glPopMatrix();
		if(this.open) {
			if(!this.components.isEmpty()) {
				//Gui.drawRect(this.x, this.y + this.barHeight, this.x + 1, this.y + this.barHeight + (12 * components.size()), new Color(0, 200, 20, 150).getRGB());
				//Gui.drawRect(this.x, this.y + this.barHeight + (12 * components.size()), this.x + this.width, this.y + this.barHeight + (12 * components.size()) + 1, new Color(0, 200, 20, 150).getRGB());
				//Gui.drawRect(this.x + this.width, this.y + this.barHeight, this.x + this.width - 1, this.y + this.barHeight + (12 * components.size()), new Color(0, 200, 20, 150).getRGB());
				for(Component component : components) {
					component.renderComponent(mouseX, mouseY);
				}
			}
		}
	}
	
	public void refresh() {
		int off = this.barHeight;
		for(Component comp : components) {
			comp.setOff(off);
			off += comp.getHeight();
		}
	}
	
	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}
	
	public int getWidth() {
		return width;
	}
	
	public void updatePosition(int mouseX, int mouseY) {
		if(this.isDragging) {
			this.setX(mouseX - dragX);
			this.setY(mouseY - dragY);
		}
	}
	
	public boolean isWithinHeader(int x, int y) {
		if(x >= this.x && x <= this.x + this.width && y >= this.y && y <= this.y + this.barHeight) {
			return true;
		}
		return false;
	}
	
}
