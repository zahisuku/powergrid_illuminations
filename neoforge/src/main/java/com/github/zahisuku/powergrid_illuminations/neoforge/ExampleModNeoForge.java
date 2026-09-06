package com.github.zahisuku.powergrid_illuminations.neoforge;

import dev.architectury.platform.hooks.EventBusesHooks;
import net.neoforged.fml.common.Mod;

import com.github.zahisuku.powergrid_illuminations.PowerGridIlluminations;

@Mod(PowerGridIlluminations.MOD_ID)
public final class ExampleModNeoForge {
    public ExampleModNeoForge() {
        // // Submit our event bus to let Architectury API register our content on the right time.
        // EventBusesHooks.whenAvailable(PowerGridIlluminations.MOD_ID, bus -> {
        //     bus.register(PowerGridIlluminations.class);
        // });
        // Run our common setup.
        PowerGridIlluminations.init();
    }
}
