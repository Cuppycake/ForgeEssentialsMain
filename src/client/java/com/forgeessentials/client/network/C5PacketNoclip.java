package com.forgeessentials.client.network;

import com.forgeessentials.client.network.ClientNetworkHandler.IFEClientPacket;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.network.play.server.S3FPacketCustomPayload;

public class C5PacketNoclip implements IFEClientPacket
{

    @Override
    public String getDiscriminator()
    {
        return "noclip";
    }

    @Override
    public void onClientReceive(S3FPacketCustomPayload packet, NetHandlerPlayClient handler, ByteBuf data)
    {
        Minecraft.getMinecraft().thePlayer.noClip = data.readBoolean();
    }

    @Override
    public ByteBuf getServerPayload(ByteBuf write)
    {
        return null;
    }
}
