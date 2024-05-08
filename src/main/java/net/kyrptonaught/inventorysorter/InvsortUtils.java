package net.kyrptonaught.inventorysorter;

import com.mojang.brigadier.context.CommandContext;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.command.ServerCommandSource;
import org.apache.logging.log4j.Logger;

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Map;

public class InvsortUtils {
    private static final Logger logger = InventorySorterMod.logger;

    public static void dumpItems(CommandContext<ServerCommandSource> ctx){
        Path outPath = Path.of("./dump.txt").toAbsolutePath();

        Registry<Item> itemReg = Registries.ITEM;
        Registry<?> groupReg = Registries.ITEM_GROUP;

        ArrayList<Item> items = new ArrayList<>();
        ArrayList<String> identifiers = new ArrayList<>();
        for(Map.Entry<RegistryKey<Item>, Item> entry : itemReg.getEntrySet()){
            Item item = entry.getValue();
            String identifier = entry.getKey().getValue().toString();
            items.add(item);
            identifiers.add(identifier);
        }

        String idList = identifiers.stream().reduce("", (a,b)-> a+"\n"+b);
        try (OutputStream os = Files.newOutputStream(outPath); OutputStreamWriter out = new OutputStreamWriter(os, StandardCharsets.UTF_8)) {
            out.write(idList);
        } catch (Exception e) {
            logger.debug("Failed to dump");
            logger.error(e);
        }

        logger.debug(outPath);
    }

}
