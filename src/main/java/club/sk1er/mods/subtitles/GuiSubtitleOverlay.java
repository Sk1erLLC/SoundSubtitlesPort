package club.sk1er.mods.subtitles;

import com.google.common.collect.Lists;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sun.javafx.geom.Vec3d;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.commons.io.IOUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class GuiSubtitleOverlay extends Gui {
    private final Minecraft client;
    private final List<Subtitle> subtitles = Lists.newArrayList();
    private HashMap<String, String> soundToSub = new HashMap<>();
    private HashMap<String, String> subToLang = new HashMap<>();

    public GuiSubtitleOverlay(Minecraft clientIn) {
        this.client = clientIn;
        ResourceLocation location = new ResourceLocation("subtitles_mod", "future_sounds.json");
        try {
            JsonObject obj = new JsonParser().parse(read(clientIn.getResourceManager().getResource(location).getInputStream())).getAsJsonObject();
            for (Map.Entry<String, JsonElement> entry : obj.entrySet()) {
                if (entry.getValue().isJsonObject()) {
                    JsonObject asJsonObject = entry.getValue().getAsJsonObject();
                    if (asJsonObject.has("subtitle")) {
                        soundToSub.put(entry.getKey(), asJsonObject.get("subtitle").getAsString());
                    }
                }
            }


        } catch (IOException e) {
            e.printStackTrace();
        }
        location = new ResourceLocation("subtitles_mod", "lang.lang");
        try {
            List<String> strings = IOUtils.readLines(clientIn.getResourceManager().getResource(location).getInputStream());
            for (String string : strings) {
                if (string.contains("=")) {
                    String[] split = string.split("=");
                    if (soundToSub.containsValue(split[0])) {
                        subToLang.put(split[0], split[1]);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
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

    public Vec3d rotatePitch(Vec3d input, float pitch) {
        float f = MathHelper.cos(pitch);
        float f1 = MathHelper.sin(pitch);
        double d0 = input.x;
        double d1 = input.y * (double) f + input.z * (double) f1;
        double d2 = input.z * (double) f - input.y * (double) f1;
        return new Vec3d(d0, d1, d2);
    }

    public Vec3d rotateYaw(Vec3d input, float yaw) {
        float f = MathHelper.cos(yaw);
        float f1 = MathHelper.sin(yaw);
        double d0 = input.x * (double) f + input.z * (double) f1;
        double d1 = input.y;
        double d2 = input.z * (double) f - input.x * (double) f1;
        return new Vec3d(d0, d1, d2);
    }

    public void renderSubtitles(ScaledResolution resolution) {

        if (SubTitleMod.showSubTitles && !this.subtitles.isEmpty()) {
            GlStateManager.pushMatrix();
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
            Vec3d vec3d = new Vec3d(this.client.thePlayer.posX, this.client.thePlayer.posY + (double) this.client.thePlayer.getEyeHeight(), this.client.thePlayer.posZ);
            Vec3d vec3d1 = rotateYaw(rotatePitch((new Vec3d(0.0D, 0.0D, -1.0D)), -this.client.thePlayer.rotationPitch * 0.017453292F), -this.client.thePlayer.rotationYaw * 0.017453292F);
            Vec3d vec3d2 = rotateYaw(rotatePitch((new Vec3d(0.0D, 1.0D, 0.0D)), -this.client.thePlayer.rotationPitch * 0.017453292F), -this.client.thePlayer.rotationYaw * 0.017453292F);
            Vec3d vec3d3 = new Vec3d();
            vec3d3.cross(vec3d1, vec3d2); //sets to cross product
            int i = 0;
            int j = 0;
            Iterator<Subtitle> iterator = this.subtitles.iterator();

            while (iterator.hasNext()) {
                GuiSubtitleOverlay.Subtitle guisubtitleoverlay$subtitle = iterator.next();

                if (guisubtitleoverlay$subtitle.getStartTime() + 3000L <= Minecraft.getSystemTime()) {
                    iterator.remove();
                } else {
                    j = Math.max(j, this.client.fontRendererObj.getStringWidth(guisubtitleoverlay$subtitle.getString()));
                }
            }

            j = j + this.client.fontRendererObj.getStringWidth("<") + this.client.fontRendererObj.getStringWidth(" ") + this.client.fontRendererObj.getStringWidth(">") + this.client.fontRendererObj.getStringWidth(" ");

            for (GuiSubtitleOverlay.Subtitle guisubtitleoverlay$subtitle1 : this.subtitles) {
                int k = 255;
                String s = guisubtitleoverlay$subtitle1.getString();
                Vec3d location = guisubtitleoverlay$subtitle1.getLocation();
                location.sub(vec3d);
                location.normalize();
                double d0 = -vec3d3.dot(location);
                double d1 = -vec3d1.dot(location);
                boolean flag = d1 > 0.5D;
                int l = j / 2;
                int i1 = this.client.fontRendererObj.FONT_HEIGHT;
                int j1 = i1 / 2;
                float f = 1.0F;
                int k1 = this.client.fontRendererObj.getStringWidth(s);
                int l1 = MathHelper.floor_double(MathHelper.denormalizeClamp(255.0D, 75.0D, (double) ((float) (Minecraft.getSystemTime() - guisubtitleoverlay$subtitle1.getStartTime()) / 3000.0F)));
                int i2 = l1 << 16 | l1 << 8 | l1;
                GlStateManager.pushMatrix();
                GlStateManager.translate((float) resolution.getScaledWidth() - (float) l * f - 2.0F, (float) (resolution.getScaledHeight() - 30) - (float) (i * (i1 + 1)) * f, 0.0F);
                GlStateManager.scale(f, f, f);
                drawRect(-l - 1, -j1 - 1, l + 1, j1 + 1, (int) ((double) k * 0.8D) << 24);
                GlStateManager.enableBlend();

                if (!flag) {
                    if (d0 > 0.0D) {
                        this.client.fontRendererObj.drawString(">", l - this.client.fontRendererObj.getStringWidth(">"), -j1, i2 + (k << 24 & -16777216));
                    } else if (d0 < 0.0D) {
                        this.client.fontRendererObj.drawString("<", -l, -j1, i2 + (k << 24 & -16777216));
                    }
                }

                this.client.fontRendererObj.drawString(s, -k1 / 2, -j1, i2 + (k << 24 & -16777216));
                GlStateManager.popMatrix();
                ++i;
            }

            GlStateManager.disableBlend();
            GlStateManager.popMatrix();
        }
    }

    private String getName(ResourceLocation location) {
        String resourcePath = location.getResourcePath();
        if (resourcePath.equals("mob.creeper.say")) {
            resourcePath = "entity.creeper.hurt";
        }
        if (resourcePath.startsWith("mob.")) {
            resourcePath = resourcePath.replace("mob.", "entity.");
        }
        if (resourcePath.endsWith(".say")) {
            resourcePath = resourcePath.replace(".say", ".ambient");
        }
        if (resourcePath.startsWith("step.")) {
            resourcePath = "block." + resourcePath.replace("step.", "") + ".step";
            System.out.println(resourcePath);
        }
        if (resourcePath.startsWith("dig.")) {
            resourcePath = "block." + resourcePath.replace("dig.", "") + ".hit";
        }
        if (resourcePath.equalsIgnoreCase("random.pop")) {
            resourcePath = "entity.item.pickup";
        }
        if (resourcePath.endsWith("swim.splash")) {
            resourcePath = "entity." + resourcePath.split("\\.")[1] + ".splash";
        }
        if (resourcePath.endsWith(".swim")) {
            resourcePath = "entity." + resourcePath.split("\\.")[1] + ".swim";
        }
        if (resourcePath.equals("liquid.lavapop")) {
            resourcePath = "block.lava.pop";
        }
        if (resourcePath.startsWith("liquid.")) {
            resourcePath = "block." + resourcePath.split("\\.")[1] + ".ambient";
        }
        if (resourcePath.equals("game.potion.smash")) {
            resourcePath = "entity.splash_potion.break";
        }
        if (resourcePath.startsWith("game.")) {
            resourcePath = resourcePath.replace("game.", "entity.");
        }
        if (resourcePath.startsWith("note.")) {
            resourcePath = "block." + resourcePath;
        }
        if (resourcePath.startsWith("portal.")) {
            resourcePath = "block." + resourcePath;
        }
        if (resourcePath.equals("random.chestopen")) {
            resourcePath = "block.chest.open";
        }
        if (resourcePath.equals("random.chestclosed")) {
            resourcePath = "block.chest.close";
        }
        if (resourcePath.equals("random.bowhit")) {
            resourcePath = "entity.arrow.hit";
        }
        if (resourcePath.equals("random.bow")) {
            resourcePath = "entity.experience_bottle.throw";
        }

        if (resourcePath.startsWith("random.anvil")) {
            resourcePath = "block.anvil." + resourcePath.replace("random.anvil", "").replace("_", "");
            System.out.println(resourcePath);
        }
        if (resourcePath.equals("fire.fire")) {
            resourcePath = "block.fire.ambient";
        }
        if (resourcePath.endsWith(".idle")) {
            resourcePath = resourcePath.replace(".idle", ".ambient");
        }
        String s = soundToSub.get(resourcePath);
        if (s == null)
            return location.getResourcePath();
        String s1 = subToLang.get(s);
        if (s1 != null) return s1;
        return location.getResourcePath();
    }

    public void soundPlay(ISound soundIn) {

        String s = getName(soundIn.getSoundLocation());
        if (!this.subtitles.isEmpty()) {
            for (GuiSubtitleOverlay.Subtitle guisubtitleoverlay$subtitle : this.subtitles) {
                if (guisubtitleoverlay$subtitle.getString().equals(s)) {
                    guisubtitleoverlay$subtitle.refresh(new Vec3d(soundIn.getXPosF(), soundIn.getYPosF(), soundIn.getZPosF()));
                    return;
                }
            }
        }
        this.subtitles.add(new Subtitle(s, new Vec3d(soundIn.getXPosF(), soundIn.getYPosF(), soundIn.getZPosF())));
    }

    @SideOnly(Side.CLIENT)
    public static class Subtitle {
        private final String subtitle;
        private long startTime;
        private Vec3d location;

        public Subtitle(String subtitleIn, Vec3d locationIn) {
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

        public Vec3d getLocation() {
            return this.location;
        }

        public void refresh(Vec3d locationIn) {
            this.location = locationIn;
            this.startTime = Minecraft.getSystemTime();
        }
    }
}
