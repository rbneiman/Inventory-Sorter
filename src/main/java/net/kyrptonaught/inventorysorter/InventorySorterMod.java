package net.kyrptonaught.inventorysorter;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.kyrptonaught.inventorysorter.client.config.IgnoreList;
import net.kyrptonaught.inventorysorter.interfaces.InvSorterPlayer;
import net.kyrptonaught.inventorysorter.network.InventorySortPacket;
import net.kyrptonaught.inventorysorter.network.SyncBlacklistPacket;
import net.kyrptonaught.inventorysorter.network.SyncInvSortSettingsPacket;
import net.kyrptonaught.kyrptconfig.config.ConfigManager;
import net.minecraft.resource.ResourceType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class InventorySorterMod implements ModInitializer {
    public static final Logger logger = LogManager.getLogger();
    public static ConfigManager.MultiConfigManager configManager = new ConfigManager.MultiConfigManager(InventorySorterMod.MOD_ID);
    public static final String MOD_ID = "inventorysorter";

    @Override
    public void onInitialize() {
        configManager.registerFile("blacklist.json5", new IgnoreList());
        configManager.load();
        ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(new DataResourceReloader());
        CommandRegistrationCallback.EVENT.register(SortCommand::register);
        InventorySortPacket.registerReceivePacket();
        SyncInvSortSettingsPacket.registerReceiveSyncData();
        SyncBlacklistPacket.registerSyncOnPlayerJoin();

        ServerPlayerEvents.COPY_FROM.register((oldPlayer, newPlayer, alive) -> {
            if (oldPlayer instanceof InvSorterPlayer) {
                ((InvSorterPlayer) newPlayer).setSortType(((InvSorterPlayer) oldPlayer).getSortType());
                ((InvSorterPlayer) newPlayer).setMiddleClick(((InvSorterPlayer) oldPlayer).getMiddleClick());
            }
        });
    }

    public static IgnoreList getBlackList() {
        return (IgnoreList) configManager.getConfig("blacklist.json5");
    }



}
