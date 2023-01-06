package me.kambing.gardenia.clickgui.component.components.sub;

import com.mojang.realmsclient.gui.ChatFormatting;
import me.kambing.gardenia.Gardenia;
import me.kambing.gardenia.settings.Setting;
import me.kambing.gardenia.utils.MessageUtil;
import me.kambing.gardenia.utils.RenderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.Objects;

public class ColorPicker extends GuiScreen {

    private final Setting setting;
    private float[] color;
    private boolean pickingColor;
    private boolean pickingHue;
    private boolean pickingAlpha;
    private int pickerX, pickerY, pickerWidth, pickerHeight;
    private int hueSliderX, hueSliderY, hueSliderWidth, hueSliderHeight;
    private int alphaSliderX, alphaSliderY, alphaSliderWidth, alphaSliderHeight;
    private float rainbowSpeed = 20.0f;
    private boolean rainbowState = false;
    private boolean pickingCopy;
    private boolean pickingPaste;

    public ColorPicker(Setting setting) {
        float[] settingColor = Color.RGBtoHSB(setting.getColor().getRed(), setting.getColor().getGreen(), setting.getColor().getBlue(), null);
        this.color = new float[] {settingColor[0], settingColor[1], settingColor[2], setting.getColor().getAlpha() / 255.0f};
        pickingColor = false;
        this.setting = setting;
    }

    @Override
    public void initGui() {
        pickerWidth = 120;
        pickerHeight = 100;
        pickerX = width / 2 - pickerWidth / 2;
        pickerY = height / 2 - pickerHeight / 2;
        hueSliderX = pickerX;
        hueSliderY = pickerY + pickerHeight + 6;
        hueSliderWidth = pickerWidth;
        hueSliderHeight = 10;
        alphaSliderX = pickerX + pickerWidth + 6;
        alphaSliderY = pickerY;
        alphaSliderWidth = 10;
        alphaSliderHeight = pickerHeight;
    }
    // need to remember this
    // " R: " + setting.getColor().getRed() + " G: " + setting.getColor().getGreen() +  " B: " + setting.getColor().getBlue()
    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        pickingCopy = (check(pickerX,pickerY + 120, pickerX + mc.fontRendererObj.getStringWidth("Copy"), pickerY + 120 + mc.fontRendererObj.FONT_HEIGHT, mouseX,mouseY));
        pickingPaste = check(pickerX + 30,pickerY + 120, pickerX + mc.fontRendererObj.getStringWidth("Paste") + 30, pickerY + 120 + mc.fontRendererObj.FONT_HEIGHT, mouseX,mouseY);

        drawDefaultBackground();
        if (rainbowState) {
            double rainbowState = Math.ceil((System.currentTimeMillis() + 200) / 20.0);
            rainbowState %= 360.0;
            color[0] = (float) (rainbowState / 360.0);
        }
        Gui.drawRect(pickerX - 3, pickerY - 16, pickerX + pickerWidth + alphaSliderWidth + 9, pickerY + pickerHeight + hueSliderHeight + 24, new Color(0,0,0,Gardenia.instance.moduleManager.clickGUI.getAlpha()).getRGB());
        Gui.drawRect(pickerX - 3, pickerY - 16, pickerX + pickerWidth + alphaSliderWidth + 9, pickerY - 6, new Color(Gardenia.instance.moduleManager.clickGUI.getColor().getRed(),Gardenia.instance.moduleManager.clickGUI.getColor().getGreen(),Gardenia.instance.moduleManager.clickGUI.getColor().getBlue(),100).getRGB());
        RenderUtil.drawOutlineRect(pickerX - 3, pickerY - 16, pickerX + pickerWidth + alphaSliderWidth + 9, pickerY - 6, new Color(Gardenia.instance.moduleManager.clickGUI.getColor().getRed(),Gardenia.instance.moduleManager.clickGUI.getColor().getGreen(),Gardenia.instance.moduleManager.clickGUI.getColor().getBlue(),255).getRGB());
        Minecraft.getMinecraft().fontRendererObj.drawStringWithShadow(setting.getParentMod().getName() + " - " + setting.getName(), pickerX, pickerY - 11 - (mc.fontRendererObj.FONT_HEIGHT / 2f), -1);
        Minecraft.getMinecraft().fontRendererObj.drawStringWithShadow("Press anywhere to exit", pickerX, pickerY - 40 - (mc.fontRendererObj.FONT_HEIGHT / 2f), -1);
        if (pickingHue) {
            if (hueSliderWidth > hueSliderHeight) {
                float restrictedX = (float) Math.min(Math.max(hueSliderX, mouseX), hueSliderX + hueSliderWidth);
                color[0] = (restrictedX - (float) hueSliderX) / hueSliderWidth;
            } else {
                float restrictedY = (float) Math.min(Math.max(hueSliderY, mouseY), hueSliderY + hueSliderHeight);
                color[0] = (restrictedY - (float) hueSliderY) / hueSliderHeight;
            }
        }
        if (pickingAlpha) {
            if (alphaSliderWidth > alphaSliderHeight) {
                float restrictedX = (float) Math.min(Math.max(alphaSliderX, mouseX), alphaSliderX + alphaSliderWidth);
                color[3] = 1 - (restrictedX - (float) alphaSliderX) / alphaSliderWidth;
            } else {
                float restrictedY = (float) Math.min(Math.max(alphaSliderY, mouseY), alphaSliderY + alphaSliderHeight);
                color[3] = 1 - (restrictedY - (float) alphaSliderY) / alphaSliderHeight;
            }
        }
        if (pickingColor) {
            float restrictedX = (float) Math.min(Math.max(pickerX, mouseX), pickerX + pickerWidth);
            float restrictedY = (float) Math.min(Math.max(pickerY, mouseY), pickerY + pickerHeight);
            color[1] = (restrictedX - (float) pickerX) / pickerWidth;
            color[2] = 1 - (restrictedY - (float) pickerY) / pickerHeight;
        }
        int selectedX = pickerX + pickerWidth + 6;
        int selectedY = pickerY + pickerHeight + 6;
        int selectedWidth = 10;
        int selectedHeight = 10;
        Gui.drawRect(pickerX - 2, pickerY - 2, pickerX + pickerWidth + 2, pickerY + pickerHeight + 2, 0xFC000000);
        Gui.drawRect(hueSliderX - 2, hueSliderY - 2, hueSliderX + hueSliderWidth + 2, hueSliderY + hueSliderHeight + 2, 0xFC000000);
        Gui.drawRect(alphaSliderX - 2, alphaSliderY - 2, alphaSliderX + alphaSliderWidth + 2, alphaSliderY + alphaSliderHeight + 2, 0xFC000000);
        int selectedColor = Color.HSBtoRGB(color[0], 1.0f, 1.0f);
        float selectedRed = (selectedColor >> 16 & 0xFF) / 255.0f;
        float selectedGreen = (selectedColor >> 8 & 0xFF) / 255.0f;
        float selectedBlue = (selectedColor & 0xFF) / 255.0f;
        drawPickerBase(pickerX, pickerY, pickerWidth, pickerHeight, selectedRed, selectedGreen, selectedBlue, color[3]);
        drawHueSlider(hueSliderX, hueSliderY, hueSliderWidth, hueSliderHeight, color[0]);
        drawAlphaSlider(alphaSliderX, alphaSliderY, alphaSliderWidth, alphaSliderHeight, selectedRed, selectedGreen, selectedBlue, color[3]);
        final int selectedColorFinal = alpha(new Color(Color.HSBtoRGB(color[0], color[1], color[2])), color[3]);
        Gui.drawRect(selectedX - 2, selectedY - 2, selectedX + selectedWidth + 2, selectedY + selectedHeight + 2, 0xFC000000);
        Gui.drawRect(selectedX, selectedY, selectedX + selectedWidth, selectedY + selectedHeight, selectedColorFinal);
        {
            final int cursorX = (int) (pickerX + color[1] * pickerWidth);
            final int cursorY = (int) ((pickerY + pickerHeight) - color[2] * pickerHeight);
            Gui.drawRect(cursorX - 2, cursorY - 2, cursorX + 2, cursorY + 2, -1);
        }
        setting.setColor(urmom(new Color(Color.HSBtoRGB(color[0], color[1], color[2])), color[3]));
//        for (int i = 1; i < pickerHeight/10; i++) {
//            Gui.drawRect(selectedX - 2, pickerY + i * 14, selectedX + 12, pickerY + i * 14, 0xFC000000);
//        }
        if (Keyboard.isKeyDown(Keyboard.KEY_ESCAPE)) {
            setting.setOpen(false);
        }
        Minecraft.getMinecraft().fontRendererObj.drawStringWithShadow(pickingCopy ? ChatFormatting.UNDERLINE  + "Copy" : "Copy", pickerX, pickerY + 120, -1);
        Minecraft.getMinecraft().fontRendererObj.drawStringWithShadow(pickingPaste ? ChatFormatting.UNDERLINE + "Paste" : "Paste", pickerX + 30, pickerY + 120, -1);
    }

    final int alpha(Color color, float alpha) {
        final float red = (float) color.getRed() / 255;
        final float green = (float) color.getGreen() / 255;
        final float blue = (float) color.getBlue() / 255;
        return new Color(red, green, blue, alpha).getRGB();
    }

    final Color urmom(Color color, float alpha) {
        final float red = (float) color.getRed() / 255;
        final float green = (float) color.getGreen() / 255;
        final float blue = (float) color.getBlue() / 255;
        return new Color(red, green, blue, alpha);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        pickingColor = check(pickerX, pickerY, pickerX + pickerWidth, pickerY + pickerHeight, mouseX, mouseY);
        pickingHue = check(hueSliderX, hueSliderY, hueSliderX + hueSliderWidth, hueSliderY + hueSliderHeight, mouseX, mouseY);
        pickingAlpha = check(alphaSliderX, alphaSliderY, alphaSliderX + alphaSliderWidth, alphaSliderY + alphaSliderHeight, mouseX, mouseY);
        if (check(pickerX,pickerY + 120, pickerX + mc.fontRendererObj.getStringWidth("Copy"), pickerY + 120 + mc.fontRendererObj.FONT_HEIGHT, mouseX,mouseY)) {
            //copy
            String hex = String.format("#%06x", setting.getColor().getRGB() & 0xFFFFFF);
            StringSelection selection = new StringSelection(hex);
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            clipboard.setContents(selection, selection);
            MessageUtil.sendMessage("Color has been successfully copied to clipboard!");
        }
        if (check(pickerX + 30,pickerY + 120, pickerX + mc.fontRendererObj.getStringWidth("Paste") + 30, pickerY + 120 + mc.fontRendererObj.FONT_HEIGHT, mouseX,mouseY)) {
            //paste
            if (readClipboard() != null) {
                if (Objects.requireNonNull(readClipboard()).startsWith("#")) {
                    Color color1 = Color.decode(Objects.requireNonNull(readClipboard()));
                    float[] decodedColor = Color.RGBtoHSB(color1.getRed(), color1.getGreen(), color1.getBlue(), null);
                    color = new float[]{decodedColor[0], decodedColor[1], decodedColor[2], color1.getAlpha() / 255.0f};
                    MessageUtil.sendMessage("Set color to " + readClipboard());
                } else {
                    MessageUtil.sendMessage("The color your pasting is not a hex-type color.");
                }
            }
        }
        if (!isInsideBox(mouseX, mouseY)) {
            setting.setOpen(false);
            mc.displayGuiScreen(Gardenia.instance.clickGui);
        }
    }

    public boolean isInsideBox(int mouseX, int mouseY){
        return (mouseX > pickerX - 3 && mouseX < pickerX + pickerWidth + alphaSliderWidth + 9) && (mouseY > pickerY - 16 && mouseY < pickerY + pickerHeight + hueSliderHeight + 24);
    }
    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        pickingColor = pickingHue = pickingAlpha = false;
    }

    private void drawHueSlider(int x, int y, int width, int height, float hue) {
        int step = 0;
        if (height > width) {
            Gui.drawRect(x, y, x + width, y + 4, 0xFFFF0000);
            y += 4;
            for (int colorIndex = 0; colorIndex < 6; colorIndex++) {
                int previousStep = Color.HSBtoRGB((float) step / 6, 1.0f, 1.0f);
                int nextStep = Color.HSBtoRGB((float) (step + 1) / 6, 1.0f, 1.0f);
                drawGradientRect(x, y + step * (height / 6), x + width, y + (step + 1) * (height / 6), previousStep, nextStep);
                step++;
            }
            final int sliderMinY = (int) (y + (height * hue)) - 4;
            Gui.drawRect(x, sliderMinY - 1, x + width, sliderMinY + 1, -1);
        } else {
            for (int colorIndex = 0; colorIndex < 6; colorIndex++) {
                int previousStep = Color.HSBtoRGB((float) step / 6, 1.0f, 1.0f);
                int nextStep = Color.HSBtoRGB((float) (step + 1) / 6, 1.0f, 1.0f);
                gradient(x + step * (width / 6), y, x + (step + 1) * (width / 6), y + height, previousStep, nextStep, true);
                step++;
            }
            final int sliderMinX = (int) (x + (width * hue));
            Gui.drawRect(sliderMinX - 1, y, sliderMinX + 1, y + height, -1);
        }
    }

    private void drawAlphaSlider(int x, int y, int width, int height, float red, float green, float blue, float alpha) {
        boolean left = true;
        int checkerBoardSquareSize = width / 2;
        for (int squareIndex = -checkerBoardSquareSize; squareIndex < height; squareIndex += checkerBoardSquareSize) {
            if (!left) {
                Gui.drawRect(x, y + squareIndex, x + width, y + squareIndex + checkerBoardSquareSize, 0xFFFFFFFF);
                Gui.drawRect(x + checkerBoardSquareSize, y + squareIndex, x + width, y + squareIndex + checkerBoardSquareSize, 0xFF909090);
                if (squareIndex < height - checkerBoardSquareSize) {
                    int minY = y + squareIndex + checkerBoardSquareSize;
                    int maxY = Math.min(y + height, y + squareIndex + checkerBoardSquareSize * 2);
                    Gui.drawRect(x, minY, x + width, maxY, 0xFF909090);
                    Gui.drawRect(x + checkerBoardSquareSize, minY, x + width, maxY, 0xFFFFFFFF);
                }
            }
            left = !left;
        }
        gradient(x, y, x + width, y + height, new Color(red, green, blue, alpha).getRGB(), 0, false);
        final int sliderMinY = (int) (y + height - (height * alpha));
        Gui.drawRect(x, sliderMinY - 1, x + width, sliderMinY + 1, -1);
    }

    private void drawPickerBase(int pickerX, int pickerY, int pickerWidth, int pickerHeight, float red, float green, float blue, float alpha) {
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glShadeModel(GL11.GL_SMOOTH);
        GL11.glBegin(GL11.GL_POLYGON);
        {
            GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
            GL11.glVertex2f(pickerX, pickerY);
            GL11.glVertex2f(pickerX, pickerY + pickerHeight);
            GL11.glColor4f(red, green, blue, alpha);
            GL11.glVertex2f(pickerX + pickerWidth, pickerY + pickerHeight);
            GL11.glVertex2f(pickerX + pickerWidth, pickerY);
        }
        GL11.glEnd();
        GL11.glDisable(GL11.GL_ALPHA_TEST);
        GL11.glBegin(GL11.GL_POLYGON);
        {
            GL11.glColor4f(0.0f, 0.0f, 0.0f, 0.0f);
            GL11.glVertex2f(pickerX, pickerY);
            GL11.glColor4f(0.0f, 0.0f, 0.0f, 1.0f);
            GL11.glVertex2f(pickerX, pickerY + pickerHeight);
            GL11.glVertex2f(pickerX + pickerWidth, pickerY + pickerHeight);
            GL11.glColor4f(0.0f, 0.0f, 0.0f, 0.0f);
            GL11.glVertex2f(pickerX + pickerWidth, pickerY);
        }
        GL11.glEnd();
        GL11.glEnable(GL11.GL_ALPHA_TEST);
        GL11.glShadeModel(GL11.GL_FLAT);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_BLEND);
    }

    protected void gradient(int minX, int minY, int maxX, int maxY, int startColor, int endColor, boolean left) {
        if (left) {

            final float startA = (startColor >> 24 & 0xFF) / 255.0f;
            final float startR = (startColor >> 16 & 0xFF) / 255.0f;
            final float startG = (startColor >> 8 & 0xFF) / 255.0f;
            final float startB = (startColor & 0xFF) / 255.0f;

            final float endA = (endColor >> 24 & 0xFF) / 255.0f;
            final float endR = (endColor >> 16 & 0xFF) / 255.0f;
            final float endG = (endColor >> 8 & 0xFF) / 255.0f;
            final float endB = (endColor & 0xFF) / 255.0f;

            GL11.glEnable(GL11.GL_BLEND);
            GL11.glDisable(GL11.GL_TEXTURE_2D);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            GL11.glShadeModel(GL11.GL_SMOOTH);
            GL11.glBegin(GL11.GL_POLYGON);
            {
                GL11.glColor4f(startR, startG, startB, startA);
                GL11.glVertex2f(minX, minY);
                GL11.glVertex2f(minX, maxY);
                GL11.glColor4f(endR, endG, endB, endA);
                GL11.glVertex2f(maxX, maxY);
                GL11.glVertex2f(maxX, minY);
            }
            GL11.glEnd();
            GL11.glShadeModel(GL11.GL_FLAT);
            GL11.glEnable(GL11.GL_TEXTURE_2D);
            GL11.glDisable(GL11.GL_BLEND);
        } else drawGradientRect(minX, minY, maxX, maxY, startColor, endColor);
    }

    protected boolean check(int minX, int minY, int maxX, int maxY, int curX, int curY) {
        return curX >= minX && curY >= minY && curX < maxX && curY < maxY;
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        super.keyTyped(typedChar, keyCode);
        if (keyCode == Keyboard.KEY_R) {
            rainbowState = !rainbowState;
        }
        if (keyCode == Keyboard.KEY_LEFT) {
            rainbowSpeed -= 0.1;
        } else if (keyCode == Keyboard.KEY_RIGHT) rainbowSpeed += 0.1;
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }
    public static String readClipboard() {
        try {
            return (String) Toolkit.getDefaultToolkit().getSystemClipboard().getData(DataFlavor.stringFlavor);
        } catch (IOException | UnsupportedFlavorException exception) {
            return null;
        }

    }
}