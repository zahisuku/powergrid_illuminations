package com.github.zahisuku.powergrid_illuminations.registry;

import com.github.zahisuku.powergrid_illuminations.PowerGridIlluminations;
import com.github.zahisuku.powergrid_illuminations.block.LedBlock;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class ModBlocks {

    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(
                    PowerGridIlluminations.MOD_ID,
                    Registries.BLOCK
            );

    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(
                    PowerGridIlluminations.MOD_ID,
                    Registries.ITEM
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

    /*
     * LED Block Item
     */
    public static final RegistrySupplier<Item> LED_BLOCK_ITEM =
            ITEMS.register(
                    "led_block",
                    () -> new BlockItem(
                            LED_BLOCK.get(),
                            new Item.Properties().arch$tab(ModCreativeTabs.POWERGRID_ILLUMINATIONS_TAB)
                    )
            );

    public static void register() {
        BLOCKS.register();
        ITEMS.register();
    }
}
