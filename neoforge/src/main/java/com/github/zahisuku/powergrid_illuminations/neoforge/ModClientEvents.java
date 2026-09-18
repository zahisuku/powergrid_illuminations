package com.github.zahisuku.powergrid_illuminations.neoforge;

import com.github.zahisuku.powergrid_illuminations.PowerGridIlluminations;
import com.github.zahisuku.powergrid_illuminations.block.LEDFixtureRenderer;
import com.github.zahisuku.powergrid_illuminations.registry.ModBlockEntities;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

@EventBusSubscriber(
        modid = PowerGridIlluminations.MOD_ID,
        bus = EventBusSubscriber.Bus.MOD,
        value = Dist.CLIENT
)
public final class ModClientEvents {
    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(
                ModBlockEntities.LED_FIXTURE.get(),
                LEDFixtureRenderer::new
        );
    }
}