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
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(PowerGridIlluminations.MOD_ID, Registries.BLOCK);
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(PowerGridIlluminations.MOD_ID, Registries.ITEM);

    // LEDブロックの登録
    public static final RegistrySupplier<Block> LED_BLOCK = BLOCKS.register("led_block", 
        () -> new LedBlock(BlockBehaviour.Properties.of()
            .strength(1.5f)
            .lightLevel(state -> state.getValue(LedBlock.LIT) ? 15 : 0) // 点灯時は明るさ15
        )
    );

    // LEDブロック用アイテムの登録
    public static final RegistrySupplier<Item> LED_BLOCK_ITEM = ITEMS.register("led_block",
        () -> new BlockItem(LED_BLOCK.get(), new Item.Properties())
    );

    public static void register() {
        BLOCKS.register();
        ITEMS.register();
    }
}
