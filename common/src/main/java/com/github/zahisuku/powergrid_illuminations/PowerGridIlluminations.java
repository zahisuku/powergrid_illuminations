package com.github.zahisuku.powergrid_illuminations;

import dev.architectury.event.events.common.PlayerEvent;
import net.minecraft.network.chat.Component;

import com.github.zahisuku.powergrid_illuminations.registry.ModBlockEntities;
import com.github.zahisuku.powergrid_illuminations.registry.ModBlocks;
import com.github.zahisuku.powergrid_illuminations.registry.ModCreativeTabs;

public final class PowerGridIlluminations {
    public static final String MOD_ID = "powergrid_illuminations";

    public static void init() {
        ModBlocks.register();
        ModBlockEntities.register();
        ModCreativeTabs.initTabs();
        PlayerEvent.PLAYER_JOIN.register(player -> {
            player.sendSystemMessage(Component.literal("Welcome to Power Grid Illuminations!"));
        });
    }
}
