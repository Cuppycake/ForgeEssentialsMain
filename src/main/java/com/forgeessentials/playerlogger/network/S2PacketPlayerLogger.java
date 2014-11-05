package com.forgeessentials.playerlogger.network;

import com.forgeessentials.util.network.ServerNetworkHandler.IFEPacket;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.play.client.C17PacketCustomPayload;

public class S2PacketPlayerLogger implements IFEPacket
{

    private EntityPlayer player;

    public S2PacketPlayerLogger()
    {
    }

    public S2PacketPlayerLogger(EntityPlayer player)
    {
        this.player = player;
    }

    @Override
    public String getDiscriminator()
    {
        return "pl";
    }

    @Override
    public void onServerReceive(C17PacketCustomPayload packet, NetHandlerPlayServer handler, ByteBuf data) {}

    @Override
    public ByteBuf getClientPayload(ByteBuf buf)
    {
        buf.writeBoolean(player.getEntityData().getBoolean("lb"));
        return buf;
    }
}
