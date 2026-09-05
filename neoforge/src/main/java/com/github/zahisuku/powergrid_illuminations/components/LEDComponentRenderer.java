/*
 * Copyright 2025 [Your Name/Organization]
 * Licensed under the Apache License, Version 2.0
 */
package com.github.zahisuku.powergrid_illuminations.components;

import com.github.zahisuku.powergrid_illuminations.registry.ModPartialModels;
import com.google.common.collect.ImmutableCollection;
import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.foundation.render.RenderTypes;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import net.createmod.catnip.render.CachedBuffers;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.Mth;
import net.minecraft.world.item.DyeColor;
import org.jetbrains.annotations.NotNull;
import org.patryk3211.powergrid.circuits.circuitboard.CircuitBoardBlockEntity;
import org.patryk3211.powergrid.circuits.components.IGoggleLabel;
import org.patryk3211.powergrid.circuits.components.IRenderedComponent;
import org.patryk3211.powergrid.circuits.components.properties.ComponentProperty;
import org.patryk3211.powergrid.circuits.schematic.PlacedComponent;

/**
 * Neoforge専用：LEDComponent のレンダリング実装
 */
public class LEDComponentRenderer {
    
    public static void render(CircuitBoardBlockEntity be, PlacedComponent placed, 
                       float partialTicks, PoseStack ms, MultiBufferSource bufferSource, 
                       int light, int overlay) {
        
        // ✅ 修正1: PartialModel を使用（モデルが存在しない場合はスキップ）
        PartialModel glowModel = ModPartialModels.CUSTOM_LIGHT_GLOW_DYED;
        PartialModel bulbModel = ModPartialModels.CUSTOM_LIGHT_BULB_DYED;

        DyeColor color = placed.get(LEDComponent.COLOR);
        if (color == DyeColor.WHITE) {
            glowModel = ModPartialModels.CUSTOM_LIGHT_GLOW;
            bulbModel = ModPartialModels.CUSTOM_LIGHT_BULB;
        }
        
        // ✅ 修正2: 色を正しく取得
        int colorValue = color.getTextureDiffuseColor();
        int red = (colorValue >> 16) & 0xFF;
        int green = (colorValue >> 8) & 0xFF;
        int blue = colorValue & 0xFF;

        // 電球本体のレンダリング
        var bulb = CachedBuffers.partial(bulbModel, be.getBlockState());
        bulb.color(red, green, blue, 255);
        bulb.light(light).renderInto(ms, bufferSource.getBuffer(RenderType.cutoutMipped()));
        
        // グロー（発光）のレンダリング
        // ✅ 修正3: 変数名の競合を避ける（aは不要、r/g/b変数をリネーム）
        int glowAlpha = 0;
        int glowRed = 0;
        int glowGreen = 0;
        int glowBlue = 0;
        
        if(placed.customData instanceof FloatPair temps) {
            // ✅ 修正4: partialTicks の型を正しく使用
            float brightness = temps.lerped(partialTicks);
            glowAlpha = (int) (brightness * 200);
            glowRed = (int) (red * brightness * 200 / 256);
            glowGreen = (int) (green * brightness * 200 / 256);
            glowBlue = (int) (blue * brightness * 200 / 256);
        }
        
        if(glowAlpha != 0) {
            var buffer = CachedBuffers.partial(glowModel, be.getBlockState());
            var center = 1.5f / 16f;
            var orientation = placed.get(LEDComponent.ORIENTATION);
            
            // ✅ 修正5-7: メソッドチェーンを正しく使用
            buffer
                .disableDiffuse()
                .color(glowRed, glowGreen, glowBlue, 255)
                .light(LightTexture.FULL_BRIGHT)
                .translate(center, center, center)
                .rotateYDegrees(orientation.ordinal() * 90)
                .translateBack(center, center, center)
                .renderInto(ms, bufferSource.getBuffer(RenderTypes.additive()));
        }
    }
}
