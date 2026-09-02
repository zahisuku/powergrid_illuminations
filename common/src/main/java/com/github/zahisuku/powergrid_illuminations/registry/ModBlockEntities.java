package com.github.zahisuku.powergrid_illuminations.registry;

import com.github.zahisuku.powergrid_illuminations.PowerGridIlluminations;
import com.github.zahisuku.powergrid_illuminations.block.LedBlockEntity;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class ModBlockEntities {

    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(
                    PowerGridIlluminations.MOD_ID,
                    Registries.BLOCK_ENTITY_TYPE
            );

    public static final RegistrySupplier<BlockEntityType<LedBlockEntity>> LED_BLOCK_ENTITY =
            BLOCK_ENTITIES.register(
                    "led_block",
                    () -> BlockEntityType.Builder.of(
                            LedBlockEntity::new,
                            ModBlocks.LED_BLOCK.get()
                    ).build(null)
            );

    public static void register() {
        BLOCK_ENTITIES.register();
    }
}
