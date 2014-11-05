package com.forgeessentials.core.network;

import com.forgeessentials.util.OutputHandler;
import com.forgeessentials.util.PlayerInfo;
import com.forgeessentials.util.network.ServerNetworkHandler.IFEPacket;
import com.forgeessentials.util.selections.Point;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.play.client.C17PacketCustomPayload;

public class S1PacketSelectionUpdate implements IFEPacket
{

    private PlayerInfo info;

    public S1PacketSelectionUpdate(){}

    public S1PacketSelectionUpdate(PlayerInfo info)
    {
        this.info = info;
    }

    @Override
    public String getDiscriminator()
    {
        return "sel";
    }

    @Override
    public void onServerReceive(C17PacketCustomPayload packet, NetHandlerPlayServer handler, ByteBuf data){}

    @Override
    public ByteBuf getClientPayload(ByteBuf byteBuf)
    {

        try
        {
            if (info != null && info.getPoint1() != null)
            {
                Point p1 = info.getPoint1();
                byteBuf.writeBoolean(true);
                byteBuf.writeDouble(p1.getX());
                byteBuf.writeDouble(p1.getY());
                byteBuf.writeDouble(p1.getZ());
            }
            else
            {
                byteBuf.writeBoolean(false);
            }

            if (info != null && info.getPoint2() != null)
            {
                Point p2 = info.getPoint2();
                byteBuf.writeBoolean(true);
                byteBuf.writeDouble(p2.getX());
                byteBuf.writeDouble(p2.getY());
                byteBuf.writeDouble(p2.getZ());
            }
            else
            {
                byteBuf.writeBoolean(false);
            }
        }

        catch (Exception e)
        {
            OutputHandler.felog.info("Error creating packet >> " + this.getClass());
        }
        return byteBuf;
    }

}
