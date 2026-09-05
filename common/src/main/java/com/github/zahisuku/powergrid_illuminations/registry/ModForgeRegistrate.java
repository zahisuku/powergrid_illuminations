package com.github.zahisuku.powergrid_illuminations.registry;

import org.patryk3211.powergrid.forge.ForgePowerGridRegistrate;
import org.patryk3211.powergrid.AbstractPowerGridRegistrate;

public class ModForgeRegistrate extends ForgePowerGridRegistrate {
    
    protected ModForgeRegistrate(String modid) {
        super(modid);
    }
    
    public static AbstractPowerGridRegistrate create(String modid) {
        return new ModForgeRegistrate(modid);
    }
}