package net.kyrptonaught.inventorysorter;

import com.mojang.serialization.DataResult;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.kyrptonaught.inventorysorter.sorttree.ISortTree;
import net.kyrptonaught.inventorysorter.sorttree.SortTree;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;
import org.apache.logging.log4j.Logger;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class DataResourceReloader implements IdentifiableResourceReloadListener {
    private static final Logger logger = InventorySorterMod.logger;

    @Override
    public CompletableFuture<Void> reload(Synchronizer synchronizer, ResourceManager manager, Profiler prepareProfiler, Profiler applyProfiler, Executor prepareExecutor, Executor applyExecutor) {

        return CompletableFuture.supplyAsync(() -> load(manager, prepareProfiler, prepareExecutor), prepareExecutor)
                .thenCompose(synchronizer::whenPrepared)
                .thenAcceptAsync((o) -> o.ifPresent((p)->apply(p, manager, applyProfiler, applyExecutor)), applyExecutor);
    }

    private Optional<ISortTree> load(ResourceManager manager, Profiler profiler, Executor executor){
//        for(Map.Entry<Identifier, Resource> entry: manager.findResources("tree", (a)->a.getPath().endsWith("item_tree.xml")).entrySet()) {
//            Identifier id = entry.getKey();
//            Resource resource = entry.getValue();
//            logger.debug("Found sort tree xml");
//            try (InputStream stream = manager.getResource("inventorysorter:tree/item_tree.xml").orElseThrow().getInputStream()){
//                return Optional.of(SortTree.fromInputStream(stream));
//            } catch (IOException e) {
//                logger.error("Failed to load sort tree.", e);
//                return Optional.empty();
//            }
//
//        }
        Identifier id;
        try {
            id = Identifier.validate("inventorysorter:tree/item_tree.xml").getOrThrow();
        }catch (IllegalStateException e){
            logger.error("Failed to find sort tree.", e);
            return Optional.empty();
        }

        try (InputStream stream = manager.getResource(id).orElseThrow().getInputStream()){
            logger.debug("Found sort tree xml");
            return Optional.of(SortTree.fromInputStream(stream));
        } catch (IOException | ParserConfigurationException | SAXException e) {
            logger.error("Failed to load sort tree from file.", e);
            return Optional.empty();
        }
//        return Optional.empty();
    }

    private void apply(ISortTree in, ResourceManager manager, Profiler profiler, Executor executor){
        logger.debug("apply{} test2", in);
    }

    @Override
    public Identifier getFabricId() {
        return new Identifier(InventorySorterMod.MOD_ID, "data_reloader");
    }
}
