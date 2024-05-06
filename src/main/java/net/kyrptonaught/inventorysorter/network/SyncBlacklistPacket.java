package net.kyrptonaught.inventorysorter.network;

import io.netty.buffer.Unpooled;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.kyrptonaught.inventorysorter.InventorySorterMod;
import net.kyrptonaught.inventorysorter.client.config.IgnoreList;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.network.packet.Packet;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

import java.util.Collections;

public class SyncBlacklistPacket {
//    public static final Identifier SYNC_BLACKLIST = new Identifier("inventorysorter", "sync_blacklist_packet");
    static boolean registered = false;

    public static void registerSyncOnPlayerJoin() {
        if(!registered){
            PayloadTypeRegistry.playS2C().register(BlacklistPacketPayload.ID, BlacklistPacketPayload.CODEC);
            registered = true;
        }



        ServerPlayConnectionEvents.JOIN.register((serverPlayNetworkHandler, packetSender, minecraftServer) ->
                packetSender.sendPacket(getPacket())
        );
    }

    public static void sync(ServerPlayerEntity player) {
        ServerPlayNetworking.send(player, getPacket());
    }

    private static BlacklistPacketPayload getPacket(){
        IgnoreList blacklist = InventorySorterMod.getBlackList();

        return new BlacklistPacketPayload(
                blacklist.hideSortBtnsList.toArray(new String[0]),
                blacklist.doNotSortList.toArray(new String[0])
        );
    }

    private static PacketByteBuf getBuf() {
        IgnoreList blacklist = InventorySorterMod.getBlackList();
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());

        String[] hideList = new String[blacklist.hideSortBtnsList.size()];
        hideList = blacklist.hideSortBtnsList.toArray(hideList);
        buf.writeInt(hideList.length);
        for (String value : hideList) buf.writeString(value);

        String[] unSortList = new String[blacklist.doNotSortList.size()];
        unSortList = blacklist.doNotSortList.toArray(unSortList);
        buf.writeInt(unSortList.length);
        for (String s : unSortList) buf.writeString(s);
        return buf;
    }

    @Environment(EnvType.CLIENT)
    public static void registerReceiveBlackList() {
        if(!registered){
            PayloadTypeRegistry.playS2C().register(BlacklistPacketPayload.ID, BlacklistPacketPayload.CODEC);
            registered = true;
        }



        ClientPlayNetworking.registerGlobalReceiver(BlacklistPacketPayload.ID, (payload, context) -> {

            Collections.addAll(InventorySorterMod.getBlackList().hideSortBtnsList, payload.hides());
            Collections.addAll(InventorySorterMod.getBlackList().doNotSortList, payload.noSorts());

//            int numHides = packet.readInt();
//            for (int i = 0; i < numHides; i++)
//                InventorySorterMod.getBlackList().hideSortBtnsList.add(packet.readString());
//
//            int numNoSort = packet.readInt();
//            for (int i = 0; i < numNoSort; i++)
//                InventorySorterMod.getBlackList().doNotSortList.add(packet.readString());
        });
    }
}
