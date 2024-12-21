package net.kyrptonaught.inventorysorter.network;

import net.kyrptonaught.inventorysorter.InventorySorterMod;
import net.kyrptonaught.inventorysorter.SortCases;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record SyncInvSortSettingsPayload(boolean isMiddleClick, boolean isDoubleClick, SortCases.SortType sortType) implements CustomPayload {
    public static final Id<SyncInvSortSettingsPayload> ID = new Id<>(Identifier.of(InventorySorterMod.MOD_ID, "sync_settings_packet"));
    public static final PacketCodec<PacketByteBuf, SyncInvSortSettingsPayload> CODEC = PacketCodec.of(SyncInvSortSettingsPayload::write, SyncInvSortSettingsPayload::read);

    public static SyncInvSortSettingsPayload read(PacketByteBuf buf) {
        boolean isMiddleClick = buf.readBoolean();
        boolean isDoubleClick = buf.readBoolean();
        SortCases.SortType sortType = SortCases.SortType.values()[buf.readInt()];
        return new SyncInvSortSettingsPayload(isMiddleClick, isDoubleClick, sortType);
    }


    private void write(PacketByteBuf buf) {
        buf.writeBoolean(isMiddleClick);
        buf.writeBoolean(isDoubleClick);
        buf.writeInt(sortType.ordinal());
    }


    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
