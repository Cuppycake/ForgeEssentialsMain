package com.forgeessentials.commands.network;

import com.forgeessentials.util.network.ServerNetworkHandler;
import com.forgeessentials.util.network.ServerNetworkHandler.IFEPacket;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.play.client.C17PacketCustomPayload;

public class S5PacketNoclip implements IFEPacket
{

    public S5PacketNoclip(){}

    private boolean mode; //true for turn on noclip, false for turn off

    private S5PacketNoclip(boolean mode)
    {
        this.mode = mode;
    }

    public static void setPlayerNoclipStatus(EntityPlayer player, boolean status)
    {
        ServerNetworkHandler.sendTo(new S5PacketNoclip(status), (EntityPlayerMP) player);
    }

    @Override
    public String getDiscriminator()
    {
        return "noclip";
    }

    @Override
    public void onServerReceive(C17PacketCustomPayload packet, NetHandlerPlayServer handler, ByteBuf data){}

    @Override
    public ByteBuf getClientPayload(ByteBuf buf)
    {
        buf.writeBoolean(mode);
        return null;
    }
}
