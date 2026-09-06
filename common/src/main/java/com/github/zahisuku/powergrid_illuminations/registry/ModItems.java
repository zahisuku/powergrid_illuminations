package com.github.zahisuku.powergrid_illuminations.registry;

import com.github.zahisuku.powergrid_illuminations.PowerGridIlluminations;
import com.github.zahisuku.powergrid_illuminations.electricity.*;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;

import static com.github.zahisuku.powergrid_illuminations.registry.ModBlocks.LED_BLOCK;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(
                    PowerGridIlluminations.MOD_ID,
                    Registries.ITEM
            );

    /*
     * LED Block Item
     */
    public static final RegistrySupplier<Item> LED_BLOCK_ITEM = ITEMS
        .register(
                "led_block",
                () -> new BlockItem(
                        LED_BLOCK.get(),
                        new Item
                            .Properties()
                            .arch$tab(ModCreativeTabs.POWERGRID_ILLUMINATIONS_TAB)
                )
            );

    public static final RegistrySupplier<Item> LV_LED_BULB = ITEMS
        .register(
                "lv_led_bulb",
                () -> new LvLedLightBulb(
                        new Item
                            .Properties()
                            .arch$tab(ModCreativeTabs.POWERGRID_ILLUMINATIONS_TAB)
                )
            );
    public static final RegistrySupplier<Item> LED_BULB = ITEMS
        .register(
                "led_bulb",
                () -> new LedLightBulb(
                        new Item
                            .Properties()
                            .arch$tab(ModCreativeTabs.POWERGRID_ILLUMINATIONS_TAB)
                )
            );
    public static final RegistrySupplier<Item> LED_FILAMENT = ITEMS
        .register(
                "led_filament",
                () -> new Item(
                        new Item
                            .Properties()
                            .arch$tab(ModCreativeTabs.POWERGRID_ILLUMINATIONS_TAB)

                )
            );

    private ModItems() {
    }

    
    public static void register() {
        ITEMS.register();
    }
}
