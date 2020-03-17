package club.sk1er.mods.subtitles;

import club.sk1er.vigilance.Vigilant;
import club.sk1er.vigilance.data.Property;
import club.sk1er.vigilance.data.PropertyType;

import java.io.File;

public class SubtitleConfig extends Vigilant {

    @Property(
            type = PropertyType.SWITCH,
            name = "Subtitle X",
            category = "Subtitles",
            subcategory = "Subtitle Pos",
            description = "Subtitle X"
    )
    public static int x = 5;

    @Property(
            type = PropertyType.SWITCH,
            name = "Subtitle Y",
            category = "Subtitles",
            subcategory = "Subtitle Pos",
            description = "Subtitle Y"
    )
    public static int y = 5;

    @Property(
            type = PropertyType.SWITCH,
            name = "Subtitle Alpha",
            category = "Subtitles",
            subcategory = "Subtitle Color",
            description = "Subtitle Alpha"
    )
    public static int alpha = 200;

    @Property(
            type = PropertyType.SWITCH,
            name = "Subtitle Scale",
            category = "Subtitles",
            subcategory = "Subtitle Scale",
            description = "Subtitle Scale"
    )
    public static int scale = 100;

    @Property(
            type = PropertyType.SWITCH,
            name = "Show Subtitles",
            category = "Subtitles",
            subcategory = "General",
            description = "General"
    )
    public static boolean showSubtitles = true;

    @Property(
            type = PropertyType.SWITCH,
            name = "Show Unknown Subtitles",
            category = "Subtitles",
            subcategory = "General",
            description = "General"
    )
    public static boolean showUnknownSubtitles = true;

    public SubtitleConfig() {
        super(new File("./config/subtitles.toml"));
        initialize();
    }
}
