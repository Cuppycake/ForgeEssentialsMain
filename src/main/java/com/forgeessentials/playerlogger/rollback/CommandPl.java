package com.forgeessentials.playerlogger.rollback;

import com.forgeessentials.core.commands.ForgeEssentialsCommandBase;
import com.forgeessentials.playerlogger.network.S2PacketPlayerLogger;
import com.forgeessentials.util.OutputHandler;
import com.forgeessentials.util.network.ServerNetworkHandler;
import cpw.mods.fml.common.FMLCommonHandler;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.permissions.PermissionsManager.RegisteredPermValue;

import java.util.Arrays;
import java.util.List;

/**
 * Main playerlogger command. Still WIP
 *
 * @author Dries007
 */

public class CommandPl extends ForgeEssentialsCommandBase {
    @Override
    public String getCommandName()
    {
        return "playerlogger";
    }

    @Override
    public List<String> getCommandAliases()
    {
        return Arrays.asList(new String[]
                { "pl" });
    }

    @Override
    public void processCommandPlayer(EntityPlayer sender, String[] args)
    {
        if (sender.worldObj.isRemote)
        {
            return;
        }
        if (args.length == 0)
        {
            OutputHandler.chatError(sender, "You must use /playerlogger enable");
            return;
        }
        else if (args[0].equalsIgnoreCase("enable"))
        {
            int limit = 5;
            if (args.length == 2)
            {
                limit = parseIntWithMin(sender, args[1], 0);
            }
            sender.getEntityData().setBoolean("lb", true);
            sender.getEntityData().setInteger("lb_limit", limit);
            OutputHandler.chatNotification(sender, "Click a block and you will get the last " + limit + " changes.");
        }
        else if (args[0].equalsIgnoreCase("disable"))
        {
            sender.getEntityData().setBoolean("lb", false);
        }

        ServerNetworkHandler.sendTo(new S2PacketPlayerLogger(sender), (EntityPlayerMP) sender);
    }

    @Override
    public boolean canConsoleUseCommand()
    {
        return false;
    }

    @Override
    public String getPermissionNode()
    {
        return "fe.pl.command";
    }

    @Override
    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args)
    {
        if (args.length == 1)
        {
            return getListOfStringsMatchingLastWord(args, "enable", "disable");
        }
        else if (args.length == 2)
        {
            return getListOfStringsMatchingLastWord(args, FMLCommonHandler.instance().getMinecraftServerInstance().getAllUsernames());
        }
        else
        {
            return null;
        }
    }

    @Override
    public String getCommandUsage(ICommandSender sender)
    {
        return "/playerlogger [enable|disable]";
    }

    @Override
    public RegisteredPermValue getDefaultPermission()
    {

        return RegisteredPermValue.OP;
    }
}
