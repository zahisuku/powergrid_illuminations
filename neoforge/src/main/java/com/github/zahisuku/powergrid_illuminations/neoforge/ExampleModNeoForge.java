package com.github.zahisuku.powergrid_illuminations.neoforge;

import net.neoforged.fml.common.Mod;

import com.github.zahisuku.powergrid_illuminations.PowerGridIlluminations;

@Mod(PowerGridIlluminations.MOD_ID)
public final class ExampleModNeoForge {
    public ExampleModNeoForge() {
        // Run our common setup.
        PowerGridIlluminations.init();
    }
}
