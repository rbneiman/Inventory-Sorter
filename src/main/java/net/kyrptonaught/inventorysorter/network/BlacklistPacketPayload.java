package net.kyrptonaught.inventorysorter.network;

import io.netty.buffer.Unpooled;
import net.kyrptonaught.inventorysorter.InventorySorterMod;
import net.kyrptonaught.inventorysorter.client.config.IgnoreList;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record BlacklistPacketPayload(String[] hides, String[] noSorts) implements CustomPayload {
    public static final Id<BlacklistPacketPayload> ID = new Id<>(Identifier.of(InventorySorterMod.MOD_ID,"sync_blacklist_packet"));
    public static final PacketCodec<PacketByteBuf, BlacklistPacketPayload> CODEC = PacketCodec.of(BlacklistPacketPayload::write, BlacklistPacketPayload::read);

    // or you can also write like this:
    // public static final PacketCodec<PacketByteBuf, BlockHighlightPayload> CODEC = PacketCodec.of((value, buf) -> buf.writeBlockPos(value.blockPos), buf -> new BlockHighlightPayload(buf.readBlockPos()));

    public static BlacklistPacketPayload read(PacketByteBuf buf){
        int numHides = buf.readInt();
        String[] hides = new String[numHides];
        for (int i = 0; i < numHides; i++)
            hides[i] = buf.readString();
//            InventorySorterMod.getBlackList().hideSortBtnsList.add(buf.readString());

        int numNoSort = buf.readInt();
        String[] noSorts = new String[numNoSort];
        for (int i = 0; i < numNoSort; i++)
            noSorts[i] = buf.readString();
//            InventorySorterMod.getBlackList().doNotSortList.add(buf.readString());
        return new BlacklistPacketPayload(hides, noSorts);
    }

    private void write(PacketByteBuf buf) {
//        IgnoreList blacklist = InventorySorterMod.getBlackList();
//        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());

        buf.writeInt(hides.length);
        for (String hide : hides) {
            buf.writeString(hide);
        }

        buf.writeInt(noSorts.length);
        for (String noSort : noSorts) {
            buf.writeString(noSort);
        }


//        String[] hideList = new String[blacklist.hideSortBtnsList.size()];
//        hideList = blacklist.hideSortBtnsList.toArray(hideList);
//        buf.writeInt(hideList.length);
//        for (String value : hideList) buf.writeString(value);
//
//        String[] unSortList = new String[blacklist.doNotSortList.size()];
//        unSortList = blacklist.doNotSortList.toArray(unSortList);
//        buf.writeInt(unSortList.length);
//        for (String s : unSortList) buf.writeString(s);
//        return buf
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
