package com.github.zahisuku.powergrid_illuminations;

import dev.architectury.event.events.common.PlayerEvent;
import dev.architectury.platform.Platform;
import net.minecraft.network.chat.Component;

import org.patryk3211.powergrid.AbstractPowerGridRegistrate;
import org.patryk3211.powergrid.PowerGrid;

import com.github.zahisuku.powergrid_illuminations.registry.ModBlockEntities;
import com.github.zahisuku.powergrid_illuminations.registry.ModBlocks;
import com.github.zahisuku.powergrid_illuminations.registry.ModComponents;
import com.github.zahisuku.powergrid_illuminations.registry.ModCreativeTabs;
import com.github.zahisuku.powergrid_illuminations.registry.ModForgeRegistrate;
import com.github.zahisuku.powergrid_illuminations.registry.ModItems;
public final class PowerGridIlluminations {
    public static final String MOD_ID = "powergrid_illuminations";

    // 静的フィールド REGISTRATEはAbstractPowerGridRegistrate型の変数で、PowerGridクラス内で共有される
	public static AbstractPowerGridRegistrate REGISTRATE;

    public static void init() {

        REGISTRATE = createRegistrate();
        finalizeRegistrate();

        ModItems.register();
        ModBlocks.register();
        ModBlockEntities.register();
        ModCreativeTabs.initTabs();
        ModComponents.register();
        PlayerEvent.PLAYER_JOIN.register(player -> {
            player.sendSystemMessage(Component.literal("Welcome to Power Grid Illuminations!"));
        });
    }

    public static AbstractPowerGridRegistrate createRegistrate() {
        if (Platform.isNeoForge()) {
            return ModForgeRegistrate.create(MOD_ID);
        }
        return PowerGrid.REGISTRATE;
    }
    
    public static void finalizeRegistrate() {
        // プラットフォーム依存の最終処理
        if(Platform.isNeoForge()) {
            // Forge: EventBus登録
        } else {
            // Fabric: register() 呼び出し
        }
    }
}
