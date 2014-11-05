package com.forgeessentials.core.network;

import com.forgeessentials.util.PlayerInfo;
import com.forgeessentials.util.network.ServerNetworkHandler.IFEPacket;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.play.client.C17PacketCustomPayload;

public class S0PacketHandshake implements IFEPacket
{
    public S0PacketHandshake(){}

    @Override
    public String getDiscriminator()
    {
        return "HS";
    }

    @Override
    public void onServerReceive(C17PacketCustomPayload packet, NetHandlerPlayServer handler, ByteBuf data)
    {
        System.out.println("Received handshake packet");
        PlayerInfo.getPlayerInfo(handler.playerEntity).setHasFEClient(true);
    }

    @Override
    public ByteBuf getClientPayload(ByteBuf buf)
    {
        return null; // no reply to the client yet
    }
}
