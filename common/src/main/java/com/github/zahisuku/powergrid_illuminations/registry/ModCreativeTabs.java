package com.github.zahisuku.powergrid_illuminations.registry;
//CREATIVE_MODE_TABS is a DeferredRegister<CreativeModeTab>

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;

import dev.architectury.registry.CreativeTabRegistry;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;

import static com.github.zahisuku.powergrid_illuminations.PowerGridIlluminations.MOD_ID;

public class ModCreativeTabs {
    private static final DeferredRegister<CreativeModeTab> TABS =
            DeferredRegister.create(MOD_ID, Registries.CREATIVE_MODE_TAB);
 
    public static RegistrySupplier<CreativeModeTab> POWERGRID_ILLUMINATIONS_TAB;
 
    //Init Creative Tabs
    public static void initTabs(){
        POWERGRID_ILLUMINATIONS_TAB = TABS.register("powergrid_illuminations_tab",
                () -> CreativeTabRegistry.create(Component.translatable("category.powergrid_illuminations_tab"),
                    () -> new ItemStack(ModBlocks.LED_BLOCK_ITEM.get())
                )
        );
        TABS.register();
    }
}
