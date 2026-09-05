package com.github.zahisuku.powergrid_illuminations.registry;

import com.tterrag.registrate.util.entry.RegistryEntry;
import org.patryk3211.powergrid.circuits.components.Component;
import org.patryk3211.powergrid.circuits.components.Components;
import com.github.zahisuku.powergrid_illuminations.components.LEDComponent;

import static com.github.zahisuku.powergrid_illuminations.PowerGridIlluminations.REGISTRATE;

public class ModComponents {
    
    // カスタムライトコンポーネントの登録
    public static final RegistryEntry<Component, LEDComponent> LED = 
        REGISTRATE.component("LED", LEDComponent::new)
            .footprint(3, 3, b -> b
                .addPad(0, 1, 0)
                .addPad(2, 1, 1)
                .withItem()
                .withOutline())
            .item(ModItems.LED_BLOCK_ITEM.get())  // アイテム登録
            .register();

    public static void register() {
        /* Initialize static fields */
        Components.register();
    }
}