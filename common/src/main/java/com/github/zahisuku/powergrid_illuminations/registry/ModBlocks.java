package com.github.zahisuku.powergrid_illuminations.registry;

// import org.patryk3211.powergrid.PowerGrid;
// import org.patryk3211.powergrid.electricity.light.bulb.LightBulb;

import com.github.zahisuku.powergrid_illuminations.PowerGridIlluminations;
import com.github.zahisuku.powergrid_illuminations.block.LedBlock;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;

// import com.tterrag.registrate.util.entry.ItemEntry;

// import static org.patryk3211.powergrid.PowerGrid.REGISTRATE;
// import static org.patryk3211.powergrid.utility.DataProviderUtility.itemWithParent;

public class ModBlocks {

    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(
                    PowerGridIlluminations.MOD_ID,
                    Registries.BLOCK
            );

    /*
     * LED Block
     *
     * Hardness: 1.5
     * Explosion Resistance: 6.0
     *
     * Pickaxe指定はminecraft:mineable/pickaxeタグで行う。
     *
     * LIT=false -> Light Level 0
     * LIT=true  -> Light Level 15
     */
    public static final RegistrySupplier<Block> LED_BLOCK =
            BLOCKS.register(
                    "led_block",
                    () -> new LedBlock(
                            BlockBehaviour.Properties.of()
                                    .strength(1.5f, 6.0f)
                                    .lightLevel(
                                            state ->
                                                    state.getValue(LedBlock.LIT)
                                                            ? 15
                                                            : 0
                                    )
                    )
            );

    

//     public static final ItemEntry<LED_Bulb> LED_BULB = REGISTRATE.item("LED_bulb", LED_Bulb::new)
//             .transform(LightBulb.setModelNameProvider(() -> state -> PowerGrid.asResource(switch(state) {
//                 case OFF -> "block/lamps/light_bulb";
//                 case LOW_POWER, ON -> "block/lamps/light_bulb_on";
//                 case BROKEN -> "block/lamps/light_bulb_broken";
//                 case LIGHT -> "block/lamps/light_bulb_light";
//             })))
//             .transform(LightBulb.setDyedModelNameProvider(() -> state -> PowerGrid.asResource(switch(state) {
//                 case OFF -> "block/lamps/dyed_light_bulb";
//                 case LOW_POWER, ON -> "block/lamps/dyed_light_bulb_on";
//                 case BROKEN -> "block/lamps/dyed_light_bulb_broken";
//                 case LIGHT -> "block/lamps/dyed_light_bulb_light";
//                 case BULB -> "block/lamps/dyed_light_bulb_bulb";
//             })))
//             .transform(LightBulb.setProperties(0.05f, 2, 15, 0.036f))
//             .model(itemWithParent("block/lamps/light_bulb"))
//             .lang("LED Bulb")
//             .register();
            

    public static void register() {
        BLOCKS.register();
    }
}
