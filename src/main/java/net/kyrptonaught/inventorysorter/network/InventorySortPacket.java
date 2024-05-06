package net.kyrptonaught.inventorysorter.network;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.kyrptonaught.inventorysorter.InventoryHelper;
import net.kyrptonaught.inventorysorter.client.InventorySorterModClient;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.ServerDynamicRegistryType;
import net.minecraft.resource.featuretoggle.FeatureSet;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

import java.util.List;

public class InventorySortPacket {
    static boolean populated = false;

    private static void populateEntries(MinecraftServer server){
        List<ItemGroup> groups = ItemGroups.getGroups();

        ItemGroup.DisplayContext dumnmy = new ItemGroup.DisplayContext(server.getOverworld().getEnabledFeatures(), true, server.getRegistryManager());

        for(ItemGroup group : groups){
            group.updateEntries(dumnmy);
        }

        populated = true;
    }

    public static void registerReceivePacket() {
        PayloadTypeRegistry.playC2S().register(InventorySortPacketPayload.ID, InventorySortPacketPayload.CODEC);


        ServerPlayNetworking.registerGlobalReceiver(InventorySortPacketPayload.ID, ((payload, context) -> {
            if(!populated){
                populateEntries(context.player().server);
            }
            context.player().server.execute(() -> InventoryHelper.sortInv(context.player(), payload.isPlayerInv(), payload.sortType()));
        }));
    }

    @Environment(EnvType.CLIENT)
    public static void sendSortPacket(boolean playerInv) {

        ClientPlayNetworking.send(new InventorySortPacketPayload(playerInv, InventorySorterModClient.getConfig().sortType));
        if (!playerInv && InventorySorterModClient.getConfig().sortPlayer)
            sendSortPacket(true);
    }
}
