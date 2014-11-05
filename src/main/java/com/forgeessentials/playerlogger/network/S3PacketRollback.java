package com.forgeessentials.playerlogger.network;

import com.forgeessentials.playerlogger.BlockChange;
import com.forgeessentials.util.network.ServerNetworkHandler.IFEPacket;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.play.client.C17PacketCustomPayload;

import java.util.ArrayList;
import java.util.List;

public class S3PacketRollback implements IFEPacket
{

    private int dim;
    private List<BlockChange> changes;

    public S3PacketRollback(int dim, ArrayList<BlockChange> changes)
    {
        this.dim = dim;
        this.changes = changes;
    }

    public S3PacketRollback(){}

    @Override
    public String getDiscriminator()
    {
        return "pl_rb";
    }

    @Override
    public void onServerReceive(C17PacketCustomPayload packet, NetHandlerPlayServer handler, ByteBuf data){}

    @Override
    public ByteBuf getClientPayload(ByteBuf buf)
    {
        if (changes == null)
        {
            buf.writeByte(0);
        }
        else
        {
            buf.writeByte(1);
            buf.writeInt(changes.size());
            System.out.println("Sending " + changes.size());
            for (BlockChange bc : changes)
            {
                if (bc.getDimension() == dim)
                {
                    System.out.println(bc.toString());
                    buf.writeInt(bc.getX());
                    buf.writeInt(bc.getY());
                    buf.writeInt(bc.getZ());
                    // True if the change was a placement.
                    buf.writeInt(bc.getType());
                }
            }
        }
        return buf;
    }
}
