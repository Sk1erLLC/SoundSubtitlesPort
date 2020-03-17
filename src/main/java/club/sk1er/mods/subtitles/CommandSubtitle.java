package club.sk1er.mods.subtitles;

import club.sk1er.mods.core.ModCore;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;

import java.util.Arrays;
import java.util.List;

public class CommandSubtitle extends CommandBase {

    @Override
    public String getCommandName() {
        return "subtitle_config";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/" + getCommandName();
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        ModCore.getInstance().getGuiHandler().open(new GuiConfig());
    }

    @Override
    public List<String> getCommandAliases() {
        return Arrays.asList("subs", "subtitles");
    }

    @Override
    public int getRequiredPermissionLevel() {
        return -1;
    }
}
