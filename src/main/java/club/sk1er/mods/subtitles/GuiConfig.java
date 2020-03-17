package club.sk1er.mods.subtitles;

import java.io.IOException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.fml.client.config.GuiSlider;

public class GuiConfig extends GuiScreen {

    private boolean dragging;
    private int lastMouseX;
    private int lastMouseY;

    @Override
    public void initGui() {
        ScaledResolution res = new ScaledResolution(Minecraft.getMinecraft());
        buttonList.add(new GuiButton(1, res.getScaledWidth() / 2 - 100, 20, EnumChatFormatting.YELLOW + "Mod Status: " + (SubtitleConfig.showSubtitles ? EnumChatFormatting.GREEN + "Enabled" : EnumChatFormatting.RED + "Disabled")));
        buttonList.add(new GuiButton(2, res.getScaledWidth() / 2 - 100, 42, EnumChatFormatting.YELLOW + "Show Unknown Sounds: " + (SubtitleConfig.showUnknownSubtitles ? EnumChatFormatting.GREEN + "Enabled" : EnumChatFormatting.RED + "Disabled")));
        buttonList.add(new GuiSlider(3, res.getScaledWidth() / 2 - 100, 64, 200, 20, "Scale: ", "", 50, 300, SubtitleConfig.scale, true, true, (d) -> {
            d.setValue(Math.round(d.getValue() * 100) / 100D);
            SubtitleConfig.scale = d.getValueInt();
        }));
        buttonList.add(new GuiSlider(4, res.getScaledWidth() / 2 - 100, 88, 200, 20, "Opacity: ", "", 0, 255, SubtitleConfig.alpha, false, true, (d) ->
                SubtitleConfig.alpha = d.getValueInt()));
        buttonList.add(new GuiButton(5, res.getScaledWidth() / 2 - 100, 110, EnumChatFormatting.YELLOW + "Reset"));

    }

    @Override
    public void onGuiClosed() {
        SubTitleMod.instance.getSubtitleConfig().markDirty();
        SubTitleMod.instance.getSubtitleConfig().writeData();
        super.onGuiClosed();
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
                if (guiButton.isMouseOver()) return;
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
            SubtitleConfig.x = SubtitleConfig.x + diff;
            SubtitleConfig.y = SubtitleConfig.y + (mouseY - this.lastMouseY);
            this.lastMouseX = mouseX;
            this.lastMouseY = mouseY;
        }
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        if (button == null) return;
        switch (button.id) {
            case 1: {
                SubtitleConfig.showSubtitles = !SubtitleConfig.showSubtitles;
                button.displayString = "Mod Status: " + (SubtitleConfig.showSubtitles ? EnumChatFormatting.GREEN + "Enabled" : EnumChatFormatting.RED + "Disabled");
                break;
            }
            case 2: {
                SubtitleConfig.showUnknownSubtitles = !SubtitleConfig.showUnknownSubtitles;
                button.displayString = "Show Unknown Sounds: " + (SubtitleConfig.showUnknownSubtitles ? EnumChatFormatting.GREEN + "Enabled" : EnumChatFormatting.RED + "Disabled");
                break;
            }
            case 5: {
                SubtitleConfig.showSubtitles = true;
                SubtitleConfig.showUnknownSubtitles = true;
                SubtitleConfig.x = 5;
                SubtitleConfig.y = 5;
                SubtitleConfig.scale = 100;
                SubtitleConfig.alpha = 200;
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
}
