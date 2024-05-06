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
//    private static final Identifier SYNC_SETTINGS = new Identifier("inventorysorter", "sync_settings_packet");


    @Environment(EnvType.CLIENT)
    public static void registerSyncOnPlayerJoin() {
        SyncInvSortSettingsPayload payload = new SyncInvSortSettingsPayload(
                InventorySorterModClient.getConfig().middleClick,
                InventorySorterModClient.getConfig().doubleClickSort,
                InventorySorterModClient.getConfig().sortType
        );
        ClientPlayNetworking.send(payload);

//        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
//        buf.writeBoolean(InventorySorterModClient.getConfig().middleClick);
//        buf.writeBoolean(InventorySorterModClient.getConfig().doubleClickSort);
//        buf.writeInt(InventorySorterModClient.getConfig().sortType.ordinal());
//        ClientPlayNetworking.send(SYNC_SETTINGS, buf);
    }

    public static void registerReceiveSyncData() {
        PayloadTypeRegistry.playC2S().register(SyncInvSortSettingsPayload.ID, SyncInvSortSettingsPayload.CODEC);

        ServerPlayNetworking.registerGlobalReceiver(SyncInvSortSettingsPayload.ID, ((payload, context) -> {
            InvSorterPlayer sorterPlayer = (InvSorterPlayer) context.player();
            context.player().server.execute(() -> {
                System.out.println("middle: " + payload.isMiddleClick() + " double: " + payload.isMiddleClick() + " sort: " + payload.sortType());
                sorterPlayer.setMiddleClick(payload.isMiddleClick());
                sorterPlayer.setDoubleClickSort(payload.isDoubleClick());
                sorterPlayer.setSortType(payload.sortType());
            });
        }));

//        ServerPlayNetworking.registerGlobalReceiver(SYNC_SETTINGS, ((server, player, handler, buf, responseSender) -> {
//            boolean middleClick = buf.readBoolean();
//            boolean doubleClick = buf.readBoolean();
//            int sortType = buf.readInt();
//            server.execute(() -> {
//                ((InvSorterPlayer) player).setMiddleClick(middleClick);
//                ((InvSorterPlayer) player).setDoubleClickSort(doubleClick);
//                ((InvSorterPlayer) player).setSortType(SortCases.SortType.values()[sortType]);
//            });
//        }));
    }
}
