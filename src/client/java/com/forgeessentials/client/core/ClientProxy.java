package com.forgeessentials.client.core;

import com.forgeessentials.client.ForgeEssentialsClient;
import com.forgeessentials.client.cui.CUIPlayerLogger;
import com.forgeessentials.client.cui.CUIRenderrer;
import com.forgeessentials.client.cui.CUIRollback;
import com.forgeessentials.client.network.C0PacketHandshake;
import com.forgeessentials.client.network.C1PacketSelectionUpdate;
import com.forgeessentials.client.network.C2PacketPlayerLogger;
import com.forgeessentials.client.network.C3PacketRollback;
import com.forgeessentials.client.network.C4PacketEconomy;
import com.forgeessentials.client.network.C5PacketNoclip;
import com.forgeessentials.client.network.ClientNetworkHandler;
import com.forgeessentials.client.util.DummyProxy;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;

public class ClientProxy extends DummyProxy
{
    private ClientConfig config;

    @Override
    public void doPreInit(FMLPreInitializationEvent e)
    {

        if (FMLCommonHandler.instance().getSide().isClient())
        {
            config = new ClientConfig(new Configuration(e.getSuggestedConfigurationFile()));
            config.init();
        }
        ClientNetworkHandler.registerPacket(new C0PacketHandshake());
        ClientNetworkHandler.registerPacket(new C1PacketSelectionUpdate());
        ClientNetworkHandler.registerPacket(new C2PacketPlayerLogger());
        ClientNetworkHandler.registerPacket(new C3PacketRollback());
        ClientNetworkHandler.registerPacket(new C4PacketEconomy());
        ClientNetworkHandler.registerPacket(new C5PacketNoclip());

    }
    
    @Override
    public void load(FMLInitializationEvent e)
    {
        super.load(e);

        FMLCommonHandler.instance().bus().register(new ClientEventHandler());
        if (ForgeEssentialsClient.allowCUI)
        {
            MinecraftForge.EVENT_BUS.register(new CUIRenderrer());
            MinecraftForge.EVENT_BUS.register(new CUIPlayerLogger());
            MinecraftForge.EVENT_BUS.register(new CUIRollback());
        }
    }
}
