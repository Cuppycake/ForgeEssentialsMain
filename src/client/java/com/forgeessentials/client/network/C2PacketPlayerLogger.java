package com.forgeessentials.client.network;

import com.forgeessentials.client.ForgeEssentialsClient;
import com.forgeessentials.client.network.ClientNetworkHandler.IFEClientPacket;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.network.play.server.S3FPacketCustomPayload;

@SideOnly(Side.CLIENT)
public class C2PacketPlayerLogger implements IFEClientPacket
{

    @Override
    public String getDiscriminator()
    {
        return "pl";
    }

    @Override
    public void onClientReceive(S3FPacketCustomPayload packet, NetHandlerPlayClient handler, ByteBuf data)
    {
        ForgeEssentialsClient.info.playerLogger = data.readBoolean();
    }

    @Override
    public ByteBuf getServerPayload(ByteBuf write)
    {
        return null;
    }
}
