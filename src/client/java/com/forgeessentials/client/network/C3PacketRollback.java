package com.forgeessentials.client.network;

import com.forgeessentials.client.ForgeEssentialsClient;
import com.forgeessentials.client.network.ClientNetworkHandler.IFEClientPacket;
import com.forgeessentials.client.util.ClientPoint;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.network.play.server.S3FPacketCustomPayload;

public class C3PacketRollback implements IFEClientPacket
{

    @Override
    public String getDiscriminator()
    {
        return "pl_rb";
    }

    @Override
    public void onClientReceive(S3FPacketCustomPayload packet, NetHandlerPlayClient handler, ByteBuf buf)
    {
        byte id = buf.readByte();
        if (id == 0)
        {
            ForgeEssentialsClient.info.rbList.clear();
            System.out.println("Clear list");
        }
        else if (id == 1)
        {
            ForgeEssentialsClient.info.rbList.clear();
            System.out.println("Clear list");
            int amount = buf.readInt();
            for (int i = 0; i < amount; i++)
            {
                try
                {
                    ClientPoint p = new ClientPoint(buf.readInt(), buf.readInt(), buf.readInt());
                    System.out.println(p.x + "; " + p.y + "; " + p.z);
                    ForgeEssentialsClient.info.rbList.put(p, buf.readInt());
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public ByteBuf getServerPayload(ByteBuf write)
    {
        return null;
    }
}
