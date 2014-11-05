package com.forgeessentials.client.network;

import com.forgeessentials.client.network.ClientNetworkHandler.IFEClientPacket;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.network.play.server.S3FPacketCustomPayload;

public class C0PacketHandshake implements IFEClientPacket
{
    public C0PacketHandshake(){}

    @Override
    public String getDiscriminator()
    {
        return "HS";
    }

    @Override
    public void onClientReceive(S3FPacketCustomPayload packet, NetHandlerPlayClient handler, ByteBuf data)
    {
        // does nothing yet
    }

    @Override
    public ByteBuf getServerPayload(ByteBuf write)
    {
        System.out.println("Sending handshake packet");
        write.writeByte(1);
        return write;
    }
}
