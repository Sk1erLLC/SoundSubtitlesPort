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

import static net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType.CHAT;

@Mod(modid = SubTitleMod.MODID, version = SubTitleMod.VERSION)
public class SubTitleMod {
    public static final String MODID = "subtitles_mod";
    public static final String VERSION = "1.1";
    public static SubTitleMod instance;
    public GuiSubtitleOverlay guiSubtitleOverlay;
    private SubtitleConfig subtitleConfig;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        subtitleConfig = new SubtitleConfig();
        subtitleConfig.preload();
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
    public void sound(PlaySoundEvent event) {
        guiSubtitleOverlay.soundPlay(event.sound);
    }

    public SubtitleConfig getSubtitleConfig() {
        return subtitleConfig;
    }
}
