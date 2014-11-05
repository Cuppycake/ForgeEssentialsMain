package com.forgeessentials.client.network;

import com.forgeessentials.client.ForgeEssentialsClient;
import com.forgeessentials.client.network.ClientNetworkHandler.IFEClientPacket;
import com.forgeessentials.client.util.ClientPoint;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.network.play.server.S3FPacketCustomPayload;

@SideOnly(Side.CLIENT)
public class C1PacketSelectionUpdate implements IFEClientPacket
{

    @Override
    public String getDiscriminator()
    {
        return "sel";
    }

    @Override
    public void onClientReceive(S3FPacketCustomPayload packet, NetHandlerPlayClient handler, ByteBuf byteBuf)
    {
        System.out.println(byteBuf.toString());
        if (byteBuf.readBoolean())
        {
            double x = byteBuf.readDouble();
            double y = byteBuf.readDouble();
            double z = byteBuf.readDouble();
            System.out.println(x + y + z);

            ForgeEssentialsClient.info.setPoint1(new ClientPoint(x, y, z));
        }
        else
        {
            ForgeEssentialsClient.info.setPoint1(null);
        }

        // podouble 2 available
        if (byteBuf.readBoolean())
        {
            double x = byteBuf.readDouble();
            double y = byteBuf.readDouble();
            double z = byteBuf.readDouble();

            ForgeEssentialsClient.info.setPoint2(new ClientPoint(x, y, z));
        }
        else
        {
            ForgeEssentialsClient.info.setPoint2(null);
        }
    }

    @Override
    public ByteBuf getServerPayload(ByteBuf write)
    {
        return null;
    }
}
