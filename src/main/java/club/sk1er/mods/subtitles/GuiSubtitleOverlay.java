package club.sk1er.mods.subtitles;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.SoundList;
import net.minecraft.client.audio.SoundListSerializer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.commons.io.IOUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class GuiSubtitleOverlay extends Gui implements IResourceManagerReloadListener {
    private static final Gson GSON = (new GsonBuilder()).registerTypeAdapter(SoundList.class, new SoundListSerializer()).create();
    private static final ParameterizedType TYPE = new ParameterizedType() {
        public Type[] getActualTypeArguments() {
            return new Type[]{String.class, SoundList.class};
        }

        public Type getRawType() {
            return Map.class;
        }

        public Type getOwnerType() {
            return null;
        }
    };
    private final Minecraft client;
    private final List<Subtitle> subtitles = Lists.newArrayList();
    private final HashMap<String, String> soundToSub = new HashMap<>();
    private final HashMap<String, String> subToLang = new HashMap<>();
    private final HashMap<String, String> locationToSound = new HashMap<>();

    public GuiSubtitleOverlay(Minecraft clientIn) {
        this.client = clientIn;
        ResourceLocation location = new ResourceLocation("subtitles_mod", "future_sounds.json");
        try {
            JsonObject obj = new JsonParser().parse(read(clientIn.getResourceManager().getResource(location).getInputStream())).getAsJsonObject();
            for (Map.Entry<String, JsonElement> entry : obj.entrySet()) {
                if (entry.getValue().isJsonObject()) {
                    JsonObject asJsonObject = entry.getValue().getAsJsonObject();
                    if (asJsonObject.has("subtitle")) {
                        String subTitle = asJsonObject.get("subtitle").getAsString();
                        if (asJsonObject.has("sounds")) {
                            for (JsonElement sounds : asJsonObject.get("sounds").getAsJsonArray()) {
                                String asString;
                                if (!sounds.isJsonPrimitive()) {
                                    JsonObject asJsonObject1 = sounds.getAsJsonObject();
                                    if (asJsonObject1.has("name")) {
                                        asString = asJsonObject1.get("name").getAsString();
                                    } else continue;
                                } else
                                    asString = sounds.getAsString();
                                asString = asString.replaceAll("\\d+", "");
                                System.out.println("sound to sub: " + asString + " -> " + subTitle);
                                soundToSub.put(asString, subTitle);
                                break;
                            }
                        }
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

    protected Map<String, SoundList> getSoundMap(InputStream stream) {
        Map map;

        try {
            map = (Map) GSON.fromJson((Reader) (new InputStreamReader(stream)), TYPE);
        } finally {
            IOUtils.closeQuietly(stream);
        }

        return map;
    }

    public void onResourceManagerReload(IResourceManager resourceManager) {
        for (String s : resourceManager.getResourceDomains()) {
            try {
                for (IResource iresource : resourceManager.getAllResources(new ResourceLocation(s, "sounds.json"))) {
                    try {
                        Map<String, SoundList> map = this.getSoundMap(iresource.getInputStream());

                        for (Map.Entry<String, SoundList> set : map.entrySet()) {
                            for (SoundList.SoundEntry soundEntry : set.getValue().getSoundList()) {
                                String soundEntryName = soundEntry.getSoundEntryName().replaceAll("\\d+", "");

                                System.out.println("Location to sound: " + set.getKey() + " -> " + soundEntryName);
                                this.locationToSound.put(set.getKey(), soundEntryName);
                                break;
                            }
                        }
                    } catch (RuntimeException ignored) {
                    }
                }
            } catch (IOException ignored) {
                ;
            }
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


    public void renderSubtitles(ScaledResolution resolution) {

        if (SubTitleMod.showSubTitles && !this.subtitles.isEmpty()) {
            GlStateManager.pushMatrix();
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
            Vec3 Vec3 = new Vec3(this.client.thePlayer.posX, this.client.thePlayer.posY + (double)this.client.thePlayer.getEyeHeight(), this.client.thePlayer.posZ);
            Vec3 Vec31 = (new Vec3(0.0D, 0.0D, -1.0D)).rotatePitch(-this.client.thePlayer.rotationPitch * 0.017453292F).rotateYaw(-this.client.thePlayer.rotationYaw * 0.017453292F);
            Vec3 Vec32 = (new Vec3(0.0D, 1.0D, 0.0D)).rotatePitch(-this.client.thePlayer.rotationPitch * 0.017453292F).rotateYaw(-this.client.thePlayer.rotationYaw * 0.017453292F);
            Vec3 Vec33 = Vec31.crossProduct(Vec32);
            int i = 0;
            int j = 0;
            Iterator iterator = this.subtitles.iterator();

            while(iterator.hasNext()) {
                GuiSubtitleOverlay.Subtitle guisubtitleoverlay$subtitle = (GuiSubtitleOverlay.Subtitle)iterator.next();
                if (guisubtitleoverlay$subtitle.getStartTime() + 3000L <= Minecraft.getSystemTime()) {
                    iterator.remove();
                } else {
                    j = Math.max(j, this.client.fontRendererObj.getStringWidth(guisubtitleoverlay$subtitle.getString()));
                }
            }

            j = j + this.client.fontRendererObj.getStringWidth("<") + this.client.fontRendererObj.getStringWidth(" ") + this.client.fontRendererObj.getStringWidth(">") + this.client.fontRendererObj.getStringWidth(" ");

            for(Iterator var26 = this.subtitles.iterator(); var26.hasNext(); ++i) {
                GuiSubtitleOverlay.Subtitle guisubtitleoverlay$subtitle1 = (GuiSubtitleOverlay.Subtitle)var26.next();
                int k = 255;
                String s = guisubtitleoverlay$subtitle1.getString();
                Vec3 Vec34 = guisubtitleoverlay$subtitle1.getLocation().subtract(Vec3).normalize();
                double d0 = -Vec33.dotProduct(Vec34);
                double d1 = -Vec31.dotProduct(Vec34);
                boolean flag = d1 > 0.5D;
                int l = j / 2;
                int i1 = this.client.fontRendererObj.FONT_HEIGHT;
                int j1 = i1 / 2;
                float f = 1.0F;
                int k1 = this.client.fontRendererObj.getStringWidth(s);
                int l1 = MathHelper.floor_double(MathHelper.denormalizeClamp(255.0D, 75.0D, (double)((float)(Minecraft.getSystemTime() - guisubtitleoverlay$subtitle1.getStartTime()) / 3000.0F)));
                int i2 = l1 << 16 | l1 << 8 | l1;
                GlStateManager.pushMatrix();
                GlStateManager.translate((float)resolution.getScaledWidth() - (float)l * f - 2.0F, (float)(resolution.getScaledHeight() - 30) - (float)(i * (i1 + 1)) * f, 0.0F);
                GlStateManager.scale(f, f, f);
                drawRect(-l - 1, -j1 - 1, l + 1, j1 + 1, (int)((double)k * 0.8D) << 24);
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
            }

            GlStateManager.disableBlend();
            GlStateManager.popMatrix();
        }
    }

    private String getName(ResourceLocation location) {
        String s3 = locationToSound.get(location.getResourcePath());
        if (s3 == null) return location.getResourcePath();
        String s = soundToSub.get(s3);
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
                    guisubtitleoverlay$subtitle.refresh(new Vec3(soundIn.getXPosF(), soundIn.getYPosF(), soundIn.getZPosF()));
                    return;
                }
            }
        }
        this.subtitles.add(new Subtitle(s, new Vec3(soundIn.getXPosF(), soundIn.getYPosF(), soundIn.getZPosF())));
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
