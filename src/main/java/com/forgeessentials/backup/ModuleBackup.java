package com.forgeessentials.backup;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.core.ForgeEssentials;
import com.forgeessentials.core.moduleLauncher.FEModule;
import com.forgeessentials.util.FunctionHelper;
import com.forgeessentials.util.OutputHandler;
import com.forgeessentials.util.events.FEModuleEvent.FEModuleInitEvent;
import com.forgeessentials.util.events.FEModuleEvent.FEModuleServerInitEvent;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.ServerConfigurationManager;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.permissions.PermissionsManager;
import net.minecraftforge.permissions.PermissionsManager.RegisteredPermValue;

import java.io.File;
import java.io.PrintWriter;
import java.util.Timer;

@FEModule(name = "Backups", parentMod = ForgeEssentials.class)
public class ModuleBackup {
    
    @FEModule.ModuleDir
    public static File moduleDir;

    public static File baseFolder;

    private Timer timer = new Timer();

    @SuppressWarnings("unused")
    private WorldSaver worldSaver;

    public static void msg(String msg)
    {
        MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
        if (!BackupConfig.enableMsg)
        {
            return;
        }
        try
        {
            if (FMLCommonHandler.instance().getEffectiveSide().isClient())
            {
                OutputHandler.felog.info(msg);
            }
            else
            {
                OutputHandler.sendMessage(server, "[ForgeEssentials] " + msg);
            }
            ServerConfigurationManager manager = server.getConfigurationManager();
            for (String username : manager.getAllUsernames())
            {
                EntityPlayerMP player = manager.func_152612_a(username);
                if (PermissionsManager.checkPermission(player, "ForgeEssentials.backup.msg"))
                {
                    OutputHandler.chatNotification(player, "[ForgeEssentials] " + msg);
                }
            }
        }
        catch (Exception e)
        {
        }
    }

    @SubscribeEvent
    public void load(FEModuleInitEvent e)
    {
        // Register configuration
        ForgeEssentials.getConfigManager().registerLoader("Backups", new BackupConfig());
        
        MinecraftForge.EVENT_BUS.register(this);
        worldSaver = new WorldSaver();
    }

    @SubscribeEvent
    public void serverStarting(FEModuleServerInitEvent e)
    {
        FunctionHelper.registerServerCommand(new CommandBackup());
        if (BackupConfig.autoInterval != 0)
        {
            timer.schedule(new AutoBackup(), BackupConfig.autoInterval*60*1000, BackupConfig.autoInterval*60*1000);
        }
        if (BackupConfig.worldSaveInterval != 0)
        {
            timer.schedule(new AutoWorldSave(), BackupConfig.worldSaveInterval*60*1000, BackupConfig.worldSaveInterval*60*1000);
        }
        makeReadme();

        APIRegistry.perms.registerPermission("fe.backup.msg", RegisteredPermValue.TRUE);
    }

    @SubscribeEvent
    public void worldUnload(WorldEvent.Unload e)
    {
        if (FMLCommonHandler.instance().getEffectiveSide().isServer())
        {
            if (BackupConfig.backupOnWorldUnload)
            {
                new Backup((WorldServer) e.world, false).run();
            }
        }
    }

    @SubscribeEvent
    public void worldLoad(WorldEvent.Load e)
    {
        if (FMLCommonHandler.instance().getEffectiveSide().isServer())
        {
            ((WorldServer) e.world).levelSaving = !BackupConfig.worldSaving;
        }
    }

    private static void makeReadme()
    {
        try
        {
            if (!baseFolder.exists())
            {
                baseFolder.mkdirs();
            }
            File file = new File(baseFolder, "README.txt");
            if (file.exists())
            {
                return;
            }
            try (PrintWriter pw = new PrintWriter(file))
            {
                pw.println("############");
                pw.println("## WARNING ##");
                pw.println("############");
                pw.println("");
                pw.println("DON'T CHANGE ANYTHING IN THIS FOLDER.");
                pw.println("IF YOU DO, AUTOREMOVE WILL SCREW UP.");
                pw.println("");
                pw.println("If you have problems with this, report an issue and don't put:");
                pw.println("\"Yes, I read the readme\" in the issue or your message on github,");
                pw.println("YOU WILL BE IGNORED.");
                pw.println("- The FE Team");
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    
}
