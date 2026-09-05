/*
 * Copyright 2025 [Your Name/Organization]
 * Licensed under the Apache License, Version 2.0
 */
package com.github.zahisuku.powergrid_illuminations.components;

import com.google.common.collect.ImmutableCollection;
import net.minecraft.util.Mth;
import net.minecraft.world.item.DyeColor;
import org.jetbrains.annotations.NotNull;
import org.patryk3211.powergrid.circuits.circuitboard.ComponentCircuitBuilder;
import org.patryk3211.powergrid.circuits.components.*;
import org.patryk3211.powergrid.circuits.components.properties.ComponentProperty;
import org.patryk3211.powergrid.circuits.components.properties.EnumProperty;
import org.patryk3211.powergrid.circuits.schematic.ComponentFootprint;
import org.patryk3211.powergrid.circuits.schematic.PlacedComponent;
import org.patryk3211.powergrid.circuits.thermal.ThermalBuilder;
import org.patryk3211.powergrid.electricity.sim.ElectricWire;

public class LEDComponent extends OrientableComponent {
    
    // プロパティ定義
    public static final EnumProperty<DyeColor> COLOR = 
        new EnumProperty<>("addon", "custom_light_color", DyeColor.class);

    public LEDComponent(ComponentFootprint footprint) {
        super(footprint);
    }

    @Override
    protected void addProperties(ImmutableCollection.Builder<ComponentProperty<?>> properties) {
        super.addProperties(properties);
        // LABEL: コンポーネント名の表示
        // COLOR: 色選択
        // voltage/power: Goggleの表示情報
        properties.add(LABEL, COLOR, voltage(24), power(5f));
    }

    @Override
    public void bake(@NotNull PlacedComponent placed, 
                     @NotNull ComponentCircuitBuilder builder, 
                     ThermalBuilder.@NotNull IEmitter thermals) {
        
        // 電気ワイア作成（初期抵抗20Ω）
        var wire = new ElectricWire(20f, builder.terminalNode(0), builder.terminalNode(1));
        builder.add(wire);
        placed.add(wire);  // PlacedComponentに保存（レンダリング等で使用）

        // 抵抗温度係数の計算（LightBulbと同じ）
        final float R_max = 24 * 24 / 5.0f;  // 最大電力時の抵抗
        
        // グロー強度を保存するデータ
        var data = new FloatPair();
        placed.customData = data;
        
        // 熱解析設定
        thermals.builder()
            .addHeatSource(wire)           // ワイアから熱が発生
            .setThermalMass(0.001f)        // 熱容量（小さい = 反応が早い）
            .setMaxPower(5.0f, 1800f)      // 最大電力5W、1800Kで臨界
            .setOverheatTemperature(2000f) // オーバーヒート温度
            .withTemperatureCallback(T -> {
                // 温度に応じて抵抗を変化（フィラメント効果）
                wire.setResistance(20f + (R_max - 20f) / 1800f * T);
                
                // 発光強度を計算（600K-1400Kで急上昇）
                var brightness = Mth.clamp((T - 600f) / (1400f - 600f), 0, 1);
                data.lerped(brightness * brightness);  // 非線形変化
            });
    }

    @Override
    public boolean tick(@NotNull PlacedComponent placed) {
        // クライアント側での毎フレーム処理
        if(!placed.isClient())
            return false;
        
        // グロー強度を前フレームの値に更新（滑らかなアニメーション用）
        renderDataTick(placed);
        return true;
    }
}
