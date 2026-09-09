package com.github.zahisuku.powergrid_illuminations.registry;

import com.github.zahisuku.powergrid_illuminations.PowerGridIlluminations;
import com.github.zahisuku.powergrid_illuminations.block.LedBlockEntity;
import com.github.zahisuku.powergrid_illuminations.block.LEDFixtureBlockEntity;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

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
                            ModBlockEntities::createLedBlockEntity,
                            ModBlocks.LED_BLOCK.get()
                    ).build(null)
            );

        public static final RegistrySupplier<BlockEntityType<LEDFixtureBlockEntity>> LED_FIXTURE =
            BLOCK_ENTITIES.register(
                "led_fixture",
                () -> BlockEntityType.Builder.of(
                        ModBlockEntities::createLEDFixtureBlockEntity,
                        ModBlocks.LED_FIXTURE.get()
                ).build(null)
            );

    private static LedBlockEntity createLedBlockEntity(BlockPos pos, BlockState state) {
        return new LedBlockEntity(LED_BLOCK_ENTITY.get(), pos, state);
    }


    private static LEDFixtureBlockEntity createLEDFixtureBlockEntity(BlockPos pos, BlockState state) {
        return new LEDFixtureBlockEntity(LED_FIXTURE.get(), pos, state);
    }

    public static void register() {
        BLOCK_ENTITIES.register();
    }
}
