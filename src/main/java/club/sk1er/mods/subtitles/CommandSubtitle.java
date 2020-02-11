package club.sk1er.mods.subtitles;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;

public class CommandSubtitle extends CommandBase {
    @Override
    public boolean canCommandSenderUseCommand(ICommandSender sender) {
        return true;
    }

    @Override
    public String getCommandName() {
        return "subtitle_config";

    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/subtitle_config";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) throws CommandException {
        SubTitleMod.openConfig();
    }
}
