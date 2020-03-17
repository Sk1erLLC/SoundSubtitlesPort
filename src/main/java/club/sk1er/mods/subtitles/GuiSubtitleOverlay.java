package club.sk1er.mods.subtitles;

import com.google.common.collect.Lists;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class GuiSubtitleOverlay extends Gui {
    private final Minecraft client;
    private final List<Subtitle> subtitles = Lists.newArrayList();
    private final HashMap<String, String> soundMap = new HashMap<>();

    public GuiSubtitleOverlay(Minecraft clientIn) {
        this.client = clientIn;
        ResourceLocation mapped = new ResourceLocation("subtitles_mod", "data.json");
        try {
            JsonObject obj = new JsonParser().parse(read(clientIn.getResourceManager().getResource(mapped).getInputStream())).getAsJsonObject();
            for (Map.Entry<String, JsonElement> entry : obj.entrySet()) {
                soundMap.put(entry.getKey(), entry.getValue().getAsString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    public List<Subtitle> getSubtitles() {
        return subtitles;
    }


    private String read(InputStream stream) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        StringBuilder stringBuilder = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            stringBuilder.append(line);
        }
        return stringBuilder.toString();
    }

    public void renderSubtitles(ScaledResolution resolution) {

        if (SubtitleConfig.showSubtitles && !this.subtitles.isEmpty()) {
            GlStateManager.pushMatrix();
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
            Vec3 Vec3 = new Vec3(this.client.thePlayer.posX, this.client.thePlayer.posY + (double) this.client.thePlayer.getEyeHeight(), this.client.thePlayer.posZ);
            Vec3 Vec31 = (new Vec3(0.0D, 0.0D, -1.0D)).rotatePitch(-this.client.thePlayer.rotationPitch * 0.017453292F).rotateYaw(-this.client.thePlayer.rotationYaw * 0.017453292F);
            Vec3 Vec32 = (new Vec3(0.0D, 1.0D, 0.0D)).rotatePitch(-this.client.thePlayer.rotationPitch * 0.017453292F).rotateYaw(-this.client.thePlayer.rotationYaw * 0.017453292F);
            Vec3 Vec33 = Vec31.crossProduct(Vec32);
            int lineNumber = 0;
            int boxWidth = 0;
            Iterator<Subtitle> iterator = this.subtitles.iterator();

            while (iterator.hasNext()) {
                GuiSubtitleOverlay.Subtitle guisubtitleoverlay$subtitle = iterator.next();
                if (guisubtitleoverlay$subtitle.getStartTime() + 3000L <= Minecraft.getSystemTime()) {
                    iterator.remove();
                } else {
                    boxWidth = Math.max(boxWidth, this.client.fontRendererObj.getStringWidth(guisubtitleoverlay$subtitle.getString()));
                }
            }

            boxWidth = boxWidth + this.client.fontRendererObj.getStringWidth("<") + this.client.fontRendererObj.getStringWidth(" ") + this.client.fontRendererObj.getStringWidth(">") + this.client.fontRendererObj.getStringWidth(" ");
            int overFlowFac = 1;
            int underFlowFac = 0;
            for (Iterator<Subtitle> var26 = this.subtitles.iterator(); var26.hasNext(); ++lineNumber) {
                GuiSubtitleOverlay.Subtitle guisubtitleoverlay$subtitle1 = var26.next();
                int k = 255;
                String s = guisubtitleoverlay$subtitle1.getString();
                Vec3 Vec34 = guisubtitleoverlay$subtitle1.getLocation().subtract(Vec3).normalize();
                double d0 = -Vec33.dotProduct(Vec34);
                double d1 = -Vec31.dotProduct(Vec34);
                boolean flag = d1 > 0.5D;
                int halfBoxWidth = boxWidth / 2;
                int fontHeight = this.client.fontRendererObj.FONT_HEIGHT;
                int halfFontHeight = fontHeight / 2;
                float scale = SubtitleConfig.scale / 100F;
                int elementWidth = this.client.fontRendererObj.getStringWidth(s);
                int l1 = MathHelper.floor_double(MathHelper.denormalizeClamp(255.0D, 75.0D, (float) (Minecraft.getSystemTime() - guisubtitleoverlay$subtitle1.getStartTime()) / 3000.0F));
                int i2 = l1 << 16 | l1 << 8 | l1;
                GlStateManager.pushMatrix();

                int x = SubtitleConfig.x;
                int y = SubtitleConfig.y;
                float xFac = (float) halfBoxWidth * scale - 2.0F;
                float yFac = (float) (lineNumber * (fontHeight + 1)) * scale + 10;
                if (x + 2 + xFac > resolution.getScaledWidth()) {
                    x = (int) (resolution.getScaledWidth() - 2 - xFac);
                }
                if (y + yFac + halfFontHeight * scale > resolution.getScaledHeight()) {
                    y -= yFac - 10;
                    y -= overFlowFac * scale * (fontHeight + 1);
                    overFlowFac++;
                }
                y += underFlowFac * scale * (fontHeight + 1);
                if (x - halfBoxWidth * scale < 0)
                    x = (int) (halfBoxWidth * scale);
                if (y + yFac - halfFontHeight * scale < 0) {
                    y = 10;
                    underFlowFac++;
                }
                GlStateManager.translate(x, y + yFac, 0.0F);
                GlStateManager.scale(scale, scale, scale);
                drawRect(-halfBoxWidth - 1, -halfFontHeight - 1, halfBoxWidth + 1, halfFontHeight + 1, (SubtitleConfig.alpha) << 24);
                GlStateManager.enableBlend();
                if (!flag) {
                    if (d0 > 0.0D) {
                        this.client.fontRendererObj.drawString(">", halfBoxWidth - this.client.fontRendererObj.getStringWidth(">"), -halfFontHeight, i2 + (k << 24 & -16777216));
                    } else if (d0 < 0.0D) {
                        this.client.fontRendererObj.drawString("<", -halfBoxWidth, -halfFontHeight, i2 + (k << 24 & -16777216));
                    }
                }

                this.client.fontRendererObj.drawString(s, -elementWidth / 2, -halfFontHeight, i2 + (k << 24 & -16777216));
                GlStateManager.popMatrix();
            }

            GlStateManager.disableBlend();
            GlStateManager.popMatrix();
        }
    }

    private String getName(ResourceLocation location) {
        return soundMap.get(location.getResourcePath());
    }


    public void soundPlay(ISound soundIn) {
        String s = getName(soundIn.getSoundLocation());
        if (s == null) {
            if (SubtitleConfig.showUnknownSubtitles) s = soundIn.getSoundLocation().getResourcePath();
            else return;
        }
        if (s.isEmpty()) return;
        if (!this.subtitles.isEmpty()) {
            for (GuiSubtitleOverlay.Subtitle guisubtitleoverlay$subtitle : this.subtitles) {
                if (guisubtitleoverlay$subtitle.getString().equals(s)) {
                    guisubtitleoverlay$subtitle.refresh(new Vec3(soundIn.getXPosF(), soundIn.getYPosF(), soundIn.getZPosF()));
                    return;
                }
            }
        }
        this.subtitles.add(new Subtitle(s, new Vec3(soundIn.getXPosF(), soundIn.getYPosF(), soundIn.getZPosF())));
    }

    public void addTempSignature() {
        int i = 0;
        for (Subtitle subtitle : this.subtitles) {
            if (subtitle.subtitle.contains("Example Sound")) {
                subtitle.startTime = Minecraft.getSystemTime();
                i++;
            }
        }
        if (i < 2)
            this.subtitles.add(new Subtitle("Example Sound " + (i + 1), new Vec3(0, 0, 0)));
    }

    @SideOnly(Side.CLIENT)
    public static class Subtitle {
        private final String subtitle;
        private long startTime;
        private Vec3 location;

        public Subtitle(String subtitleIn, Vec3 locationIn) {
            this.subtitle = subtitleIn;
            this.location = locationIn;
            this.startTime = Minecraft.getSystemTime();
        }

        public String getString() {
            return this.subtitle;
        }

        public long getStartTime() {
            return this.startTime;
        }

        public Vec3 getLocation() {
            return this.location;
        }

        public void refresh(Vec3 locationIn) {
            this.location = locationIn;
            this.startTime = Minecraft.getSystemTime();
        }
    }
}
