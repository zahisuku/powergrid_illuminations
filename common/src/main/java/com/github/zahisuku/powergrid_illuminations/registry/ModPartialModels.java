package com.github.zahisuku.powergrid_illuminations.registry;

import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import org.patryk3211.powergrid.PowerGrid;

public class ModPartialModels {
    
    // ✅ カスタムライトコンポーネント用モデル
    // assets/addon/models/component/custom_light_bulb.json などが必要
    public static final PartialModel CUSTOM_LIGHT_BULB = 
        model("component/custom_light_bulb");
    
    public static final PartialModel CUSTOM_LIGHT_GLOW = 
        model("component/custom_light_glow");
    
    public static final PartialModel CUSTOM_LIGHT_BULB_DYED = 
        model("component/custom_light_bulb_dyed");
    
    public static final PartialModel CUSTOM_LIGHT_GLOW_DYED = 
        model("component/custom_light_glow_dyed");

    private static PartialModel model(String path) {
        return PartialModel.of(PowerGrid.asResource(path));
    }

    public static void register() { /* Initialize static fields. */ }
}