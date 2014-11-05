package com.forgeessentials.client.network;

import com.forgeessentials.client.ForgeEssentialsClient;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.network.FMLEventChannel;
import cpw.mods.fml.common.network.FMLNetworkEvent.ClientCustomPacketEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.internal.FMLProxyPacket;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.network.play.server.S3FPacketCustomPayload;

import java.util.HashMap;
import java.util.Map;

public class ClientNetworkHandler
{
    public static final String BASE_CHANNEL_NAME = "FE|";

    private static Map<String, FMLEventChannel> channels = new HashMap<>();
    private static Map<String, IFEClientPacket> packets = new HashMap<>();

    public static void registerPacket(IFEClientPacket packet)
    {
        FMLEventChannel channel = NetworkRegistry.INSTANCE.newEventDrivenChannel(BASE_CHANNEL_NAME + packet.getDiscriminator());
        channel.register(new ClientNetworkHandler());
        channels.put(packet.getDiscriminator(), channel);
        packets.put(packet.getDiscriminator(), packet);
        ForgeEssentialsClient.feclientlog.info("Registered packet with discriminator " + packet.getDiscriminator());
    }

    @SubscribeEvent
    public void receiveServerPacket(ClientCustomPacketEvent e)
    {
        S3FPacketCustomPayload packet = (S3FPacketCustomPayload) e.packet.toS3FPacket();

        if (!e.packet.channel().startsWith("FE|"))
        {
            return;
        }
        String[] split = e.packet.channel().split("|");
        System.out.println(split[1]);
        for (String s : channels.keySet())
        {
            System.out.println(s);
            if (split[1].equalsIgnoreCase(s))
            {
                System.out.println(split[1]);

                packets.get(s).onClientReceive(packet, (NetHandlerPlayClient)e.handler, e.packet.payload());
                ForgeEssentialsClient.feclientlog.info("Processed packet on channel " + s);
            }
        }
    }

    public static void sendToServer(IFEClientPacket packet)
    {
        channels.get(packet.getDiscriminator()).sendToServer(new FMLProxyPacket(packet.getServerPayload(Unpooled.buffer()), BASE_CHANNEL_NAME + packet.getDiscriminator()));
    }

    public static interface IFEClientPacket
    {
        public String getDiscriminator();

        public void onClientReceive(S3FPacketCustomPayload packet, NetHandlerPlayClient handler, ByteBuf data);

        public ByteBuf getServerPayload(ByteBuf write);

    }
}
