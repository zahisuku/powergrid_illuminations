package com.github.zahisuku.powergrid_illuminations;

import dev.architectury.event.events.common.PlayerEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import com.github.zahisuku.powergrid_illuminations.registry.ModBlockEntities;
import com.github.zahisuku.powergrid_illuminations.registry.ModBlocks;
import com.github.zahisuku.powergrid_illuminations.registry.ModCreativeTabs;
import com.github.zahisuku.powergrid_illuminations.registry.ModItems;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
public final class PowerGridIlluminations {
    public static final String MOD_ID = "powergrid_illuminations";

	public static final Logger LOGGER = LoggerFactory.getLogger(PowerGridIlluminations.class);

    public static void init() {
        ModBlocks.register();
        ModItems.register();
        ModBlockEntities.register();
        ModCreativeTabs.initTabs();
        PlayerEvent.PLAYER_JOIN.register(player -> {
            player.sendSystemMessage(Component.literal("Welcome to Power Grid Illuminations!"));
        });
    }

    public static ResourceLocation asResource(String path) {
		return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
	}
}
