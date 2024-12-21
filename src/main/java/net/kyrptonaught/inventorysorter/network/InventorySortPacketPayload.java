package net.kyrptonaught.inventorysorter.network;

import net.kyrptonaught.inventorysorter.InventorySorterMod;
import net.kyrptonaught.inventorysorter.SortCases;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record InventorySortPacketPayload(boolean isPlayerInv, SortCases.SortType sortType)  implements CustomPayload {
    public static final CustomPayload.Id<InventorySortPacketPayload> ID = new Id<>(Identifier.of(InventorySorterMod.MOD_ID, "sort_inv_packet"));
    public static final PacketCodec<PacketByteBuf, InventorySortPacketPayload> CODEC = PacketCodec.of(InventorySortPacketPayload::write, InventorySortPacketPayload::read);


    public static InventorySortPacketPayload read(PacketByteBuf buf) {
        boolean isPlayerInv = buf.readBoolean();
        SortCases.SortType sortType = SortCases.SortType.values()[buf.readInt()];
        return new InventorySortPacketPayload(isPlayerInv, sortType);
    }


    private void write(PacketByteBuf buf) {
        buf.writeBoolean(isPlayerInv);
        buf.writeInt(sortType.ordinal());
    }

    @Override
    public CustomPayload.Id<? extends CustomPayload> getId() {
        return ID;
    }
}
