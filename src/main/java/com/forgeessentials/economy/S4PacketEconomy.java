package com.forgeessentials.economy;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.util.network.ServerNetworkHandler;
import com.forgeessentials.util.network.ServerNetworkHandler.IFEPacket;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.play.client.C17PacketCustomPayload;

import java.util.UUID;

public class S4PacketEconomy implements IFEPacket
{

    private UUID player;

    public S4PacketEconomy(){}

    public S4PacketEconomy(UUID player)
    {
        this.player = player;
    }

    @Override
    public String getDiscriminator()
    {
        return "econ";
    }

    @Override
    public void onServerReceive(C17PacketCustomPayload packet, NetHandlerPlayServer handler, ByteBuf data)
    {
        ServerNetworkHandler.sendTo(new S4PacketEconomy(handler.playerEntity.getPersistentID()), handler.playerEntity);

    }

    @Override
    public ByteBuf getClientPayload(ByteBuf buf)
    {
        buf.writeInt(APIRegistry.wallet.getWallet(player));
        return buf;
    }
}

