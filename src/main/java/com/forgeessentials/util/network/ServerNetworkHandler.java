package com.forgeessentials.util.network;

import com.forgeessentials.util.OutputHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.network.FMLEventChannel;
import cpw.mods.fml.common.network.FMLNetworkEvent.ServerCustomPacketEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.internal.FMLProxyPacket;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.play.client.C17PacketCustomPayload;

import java.util.HashMap;
import java.util.Map;

// let's do it the minecraft way - ie hacky nonsense
// one channel per packet

public class ServerNetworkHandler
{

    public static final String BASE_CHANNEL_NAME = "FE|";

    private static Map<String, FMLEventChannel> channels = new HashMap<>();
    private static Map<String, IFEPacket> packets = new HashMap<>();

    public static void registerPacket(IFEPacket packet)
    {
        FMLEventChannel channel = NetworkRegistry.INSTANCE.newEventDrivenChannel(BASE_CHANNEL_NAME + packet.getDiscriminator());
        channel.register(new ServerNetworkHandler());
        channels.put(packet.getDiscriminator(), channel);
        packets.put(packet.getDiscriminator(), packet);
        OutputHandler.felog.fine("Registered packet with discriminator " + packet.getDiscriminator());
    }

    @SubscribeEvent
    public void receiveClientPacket(ServerCustomPacketEvent e)
    {
        C17PacketCustomPayload packet = (C17PacketCustomPayload) e.packet.toC17Packet();

        for (String s : channels.keySet())
        {
            if (e.packet.channel().equals(s))
            {
                packets.get(s.substring(3)).onServerReceive(packet, (NetHandlerPlayServer)e.handler, e.packet.payload());
                OutputHandler.felog.fine("Processed packet with discriminator " + s.substring(3));
            }
        }
    }

    public static void sendToAll(IFEPacket packet)
    {
        channels.get(packet.getDiscriminator()).sendToAll(new FMLProxyPacket(packet.getClientPayload(Unpooled.buffer()), BASE_CHANNEL_NAME + packet.getDiscriminator()));
    }

    public static void sendTo(IFEPacket packet, EntityPlayerMP player)
    {
        channels.get(packet.getDiscriminator()).sendTo(new FMLProxyPacket(packet.getClientPayload(Unpooled.buffer()), BASE_CHANNEL_NAME + packet.getDiscriminator()), player);
    }

    public static void sendToAllAround(IFEPacket packet, NetworkRegistry.TargetPoint point)
    {
        channels.get(packet.getDiscriminator()).sendToAllAround(new FMLProxyPacket(packet.getClientPayload(Unpooled.buffer()), BASE_CHANNEL_NAME + packet.getDiscriminator()), point);
    }

    public static void sendToDimension(IFEPacket packet, int dimensionId)
    {
        channels.get(packet.getDiscriminator()).sendToDimension(new FMLProxyPacket(packet.getClientPayload(Unpooled.buffer()), BASE_CHANNEL_NAME + packet.getDiscriminator()), dimensionId);
    }

    public interface IFEPacket
    {
        public String getDiscriminator();

        public void onServerReceive(C17PacketCustomPayload packet, NetHandlerPlayServer handler, ByteBuf data);

        public ByteBuf getClientPayload(ByteBuf write);
    }

}
