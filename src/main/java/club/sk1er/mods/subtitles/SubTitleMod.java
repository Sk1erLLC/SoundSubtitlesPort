package club.sk1er.mods.subtitles;

import club.sk1er.mods.subtitles.modcore.ModCoreInstaller;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.sound.PlaySoundEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.apache.commons.io.FileUtils;

import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;

import static net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType.CHAT;

@Mod(modid = SubTitleMod.MODID, version = SubTitleMod.VERSION)
public class SubTitleMod {
    public static final String MODID = "subtitles_mod";
    public static final String VERSION = "1.0";
    public static boolean showSubTitles = true;
    public static boolean showUnknownSounds = true;
    public static int x = 5;
    public static int y = 5;
    public static int alpha = 200;
    public static float scale = 1.0f;
    public static SubTitleMod instance;
    private static boolean openNext = false;
    public GuiSubtitleOverlay guiSubtitleOverlay;
    public File suggestedConfigurationFile;

    public static void openConfig() {
        openNext = true;
    }

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        this.suggestedConfigurationFile = event.getSuggestedConfigurationFile();
        if (suggestedConfigurationFile.exists()) {
            try {
                DataInputStream dataInputStream = new DataInputStream(FileUtils.openInputStream(suggestedConfigurationFile));
                showSubTitles = dataInputStream.readBoolean();
                showUnknownSounds = dataInputStream.readBoolean();
                x = dataInputStream.readInt();
                y = dataInputStream.readInt();
                alpha = dataInputStream.readInt();
                scale = dataInputStream.readFloat();
                dataInputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        instance = this;
        ClientCommandHandler.instance.registerCommand(new CommandSubtitle());
        guiSubtitleOverlay = new GuiSubtitleOverlay(Minecraft.getMinecraft());
        MinecraftForge.EVENT_BUS.register(this);
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        ModCoreInstaller.initializeModCore(Minecraft.getMinecraft().mcDataDir);
    }

    @SubscribeEvent
    public void gameOverlay(RenderGameOverlayEvent event) {
        if (event.type == CHAT) {
            guiSubtitleOverlay.renderSubtitles(new ScaledResolution(Minecraft.getMinecraft()));
        }
    }

    @SubscribeEvent
    public void tick(TickEvent.ClientTickEvent event) {
        if (openNext) {
            openNext = false;
            Minecraft.getMinecraft().displayGuiScreen(new GuiConfig());
        }
    }

    @SubscribeEvent
    public void sound(PlaySoundEvent event) {
        guiSubtitleOverlay.soundPlay(event.sound);
    }
}
