package net.kyrptonaught.inventorysorter.sorttree.node.constraints;

import net.kyrptonaught.inventorysorter.InventorySorterMod;
import net.kyrptonaught.inventorysorter.sorttree.node.SortTreeNode;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.PotionContentsComponent;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Optional;

public class DataConstraints {
    private static final Logger logger = InventorySorterMod.logger;
    private static final HashMap<String, DataConstraintMaker> registeredConstraints = new HashMap<>();

    static {
        DataConstraints.registerConstraint(PotionConstraint.NAME, PotionConstraint::new);
        SimpleConstraint.register("isShield", (stack, rValue)->{
            return false;
        });
        SimpleConstraint.register("ToolClass", (stack, rValue)->{
            return false;
        });
        SimpleConstraint.register("ArmorType", (stack, rValue)->{
            return false;
        });
    }

    public static boolean registerConstraint(String name, DataConstraintMaker maker){
        if(registeredConstraints.containsKey(name)){
            logger.warn("Ignoring attempt to register data constraint with duplicate name {}", name);
            return false;
        }

        registeredConstraints.put(name, maker);
        return true;
    }

    public static Optional<DataConstraint> makeConstraint(SortTreeNode node, String name, String value){
        if(!registeredConstraints.containsKey(name)){
            logger.warn("Attempt to get non-existent constraint with name {}", name);
            return Optional.empty();
        }
        return Optional.of(registeredConstraints.get(name).construct(node, value));
    }

    public interface DataConstraintMaker{
        DataConstraint construct(SortTreeNode node, String rValue);
    }
}
