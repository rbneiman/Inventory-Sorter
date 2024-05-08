package net.kyrptonaught.inventorysorter.network;

import io.netty.buffer.Unpooled;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.kyrptonaught.inventorysorter.InventoryHelper;
import net.kyrptonaught.inventorysorter.SortCases;
import net.kyrptonaught.inventorysorter.client.InventorySorterModClient;
import net.kyrptonaught.inventorysorter.interfaces.InvSorterPlayer;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

public class SyncInvSortSettingsPacket {

    @Environment(EnvType.CLIENT)
    public static void registerSyncOnPlayerJoin() {
        SyncInvSortSettingsPayload payload = new SyncInvSortSettingsPayload(
                InventorySorterModClient.getConfig().middleClick,
                InventorySorterModClient.getConfig().doubleClickSort,
                InventorySorterModClient.getConfig().sortType
        );
        ClientPlayNetworking.send(payload);
    }

    public static void registerReceiveSyncData() {
        PayloadTypeRegistry.playC2S().register(SyncInvSortSettingsPayload.ID, SyncInvSortSettingsPayload.CODEC);

        ServerPlayNetworking.registerGlobalReceiver(SyncInvSortSettingsPayload.ID, ((payload, context) -> {
            InvSorterPlayer sorterPlayer = (InvSorterPlayer) context.player();
            context.player().server.execute(() -> {
                sorterPlayer.setMiddleClick(payload.isMiddleClick());
                sorterPlayer.setDoubleClickSort(payload.isDoubleClick());
                sorterPlayer.setSortType(payload.sortType());
            });
        }));

    }
}
