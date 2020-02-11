package club.sk1er.mods.subtitles;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.fml.client.config.GuiSlider;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class GuiConfig extends GuiScreen {

    private boolean dragging;
    private int lastMouseX;
    private int lastMouseY;

    @Override
    public void initGui() {
        ScaledResolution res = new ScaledResolution(Minecraft.getMinecraft());
        buttonList.add(new GuiButton(1, res.getScaledWidth() / 2 - 100, 20, EnumChatFormatting.YELLOW + "Mod Status: " + (SubTitleMod.showSubTitles ? EnumChatFormatting.GREEN + "Enabled" : EnumChatFormatting.RED + "Disabled")));
        buttonList.add(new GuiButton(2, res.getScaledWidth() / 2 - 100, 42, EnumChatFormatting.YELLOW + "Show Unknown Sounds: " + (SubTitleMod.showUnknownSounds ? EnumChatFormatting.GREEN + "Enabled" : EnumChatFormatting.RED + "Disabled")));
        buttonList.add(new GuiSlider(3, res.getScaledWidth() / 2 - 100, 64, 200, 20, "Scale: ", "", .5, 3, SubTitleMod.scale, true, true, (d) -> {
            d.setValue(Math.round(d.getValue() * 100) / 100D);
            SubTitleMod.scale = (float) d.getValue();
        }));
        buttonList.add(new GuiSlider(4, res.getScaledWidth() / 2 - 100, 88, 200, 20, "Opacity: ", "", 0, 255, SubTitleMod.alpha, false, true, (d) -> {
            SubTitleMod.alpha = d.getValueInt();
        }));
        buttonList.add(new GuiButton(5, res.getScaledWidth() / 2 - 100, 110, EnumChatFormatting.YELLOW + "Reset"));

    }

    @Override
    public void onGuiClosed() {
        super.onGuiClosed();
        try {
            File suggestedConfigurationFile = SubTitleMod.instance.suggestedConfigurationFile;
            if(!suggestedConfigurationFile.exists()) suggestedConfigurationFile.createNewFile();
            DataOutputStream dataOutputStream = new DataOutputStream(new FileOutputStream(suggestedConfigurationFile));
            dataOutputStream.writeBoolean(SubTitleMod.showSubTitles);
            dataOutputStream.writeBoolean(SubTitleMod.showUnknownSounds);
            dataOutputStream.writeInt(SubTitleMod.x);
            dataOutputStream.writeInt(SubTitleMod.y);
            dataOutputStream.writeInt(SubTitleMod.alpha);
            dataOutputStream.writeFloat(SubTitleMod.scale);
            dataOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void updateScreen() {
        super.updateScreen();
        SubTitleMod.instance.guiSubtitleOverlay.addTempSignature();
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int button) {
        try {
            super.mouseClicked(mouseX, mouseY, button);
        } catch (IOException ignored) {
        }

        if (button == 0) {
            for (GuiButton guiButton : buttonList) {
                if(guiButton.isMouseOver()) return;
            }
            this.dragging = true;
            this.lastMouseX = mouseX;
            this.lastMouseY = mouseY;
        }
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int action) {
        super.mouseReleased(mouseX, mouseY, action);
        this.dragging = false;
    }

    @Override
    protected void mouseClickMove(int mouseX, int mouseY, int lastButtonClicked, long timeSinceMouseClick) {
        if (this.dragging) {
            int diff = mouseX - this.lastMouseX;
            SubTitleMod.x = SubTitleMod.x + diff;
            SubTitleMod.y = SubTitleMod.y + (mouseY - this.lastMouseY);
            this.lastMouseX = mouseX;
            this.lastMouseY = mouseY;
        }
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        if (button == null) return;
        switch (button.id) {
            case 1: {
                SubTitleMod.showSubTitles = !SubTitleMod.showSubTitles;
                button.displayString = "Mod Status: " + (SubTitleMod.showSubTitles ? EnumChatFormatting.GREEN + "Enabled" : EnumChatFormatting.RED + "Disabled");
                break;
            }
            case 2: {
                SubTitleMod.showUnknownSounds = !SubTitleMod.showUnknownSounds;
                button.displayString = "Show Unknown Sounds: " + (SubTitleMod.showUnknownSounds ? EnumChatFormatting.GREEN + "Enabled" : EnumChatFormatting.RED + "Disabled");
                break;
            }
            case 5: {
                SubTitleMod.showSubTitles = true;
                SubTitleMod.showUnknownSounds = true;
                SubTitleMod.x = 5;
                SubTitleMod.y = 5;
                SubTitleMod.scale = 1;
                SubTitleMod.alpha = 200;
                buttonList.clear();
                initGui();
                break;
            }

        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    private BoxInfo getInfo() {
        int x = SubTitleMod.x;
        int y = SubTitleMod.y;
        int fontHeight = fontRendererObj.FONT_HEIGHT;
        int boxWidth = 0;
        List<GuiSubtitleOverlay.Subtitle> subtitles = SubTitleMod.instance.guiSubtitleOverlay.getSubtitles();
        for (GuiSubtitleOverlay.Subtitle subtitle : subtitles) {
            boxWidth = Math.max(boxWidth, fontRendererObj.getStringWidth(subtitle.getString()));
        }
        boxWidth = boxWidth + fontRendererObj.getStringWidth("<") + fontRendererObj.getStringWidth(" ") + fontRendererObj.getStringWidth(">") + fontRendererObj.getStringWidth(" ");
        int halfFontHeight = fontHeight / 2;

        float scale = SubTitleMod.scale;
        int halfBoxWidth = boxWidth / 2;
        float xFac = (float) halfBoxWidth * scale - 2.0F;
        int lineNumber = subtitles.size() - 1;
        ScaledResolution resolution = new ScaledResolution(Minecraft.getMinecraft());
        float yFac = (float) ((lineNumber) * (fontHeight + 1)) * scale + 10;
        if (x + 2 + xFac > resolution.getScaledWidth()) {
            x = (int) (resolution.getScaledWidth() - 2 - xFac);
        }
        if (y + yFac + halfFontHeight * scale > resolution.getScaledHeight()) {
            y -= (lineNumber) * (fontHeight) * scale;
        }
        if (x - halfBoxWidth * scale < 0)
            x = (int) (halfBoxWidth * scale);
        if (y + yFac - halfFontHeight * scale < 0)
            y = (int) (halfFontHeight * scale);

        return new BoxInfo(((int) (x - (halfBoxWidth - 1) * scale)), ((int) ((y) - (halfFontHeight - 1) * scale)),
                ((int) (boxWidth * scale)), (((int) (fontHeight * scale * subtitles.size()))));
    }

    static class BoxInfo {
        int x;
        int y;
        int width;
        int height;

        public BoxInfo(int x, int y, int width, int height) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
        }

        @Override
        public String toString() {
            return "BoxInfo{" +
                    "x=" + x +
                    ", y=" + y +
                    ", width=" + width +
                    ", height=" + height +
                    '}';
        }
    }
}
