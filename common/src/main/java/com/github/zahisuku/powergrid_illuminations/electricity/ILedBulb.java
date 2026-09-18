package com.github.zahisuku.powergrid_illuminations.electricity;

import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;

// ILightBulbインターフェースは、電球の特性や動作を定義するためのインターフェースです。
public interface ILedBulb {
    // 電球の光レベルを定義する定数です。
    // LIGHT_LEVEL_LOW_POWERは低電力時の光レベルを表し、
    // LIGHT_LEVEL_FULL_POWERは最大電力時の光レベルを表します。
    int LIGHT_LEVEL_LOW_POWER = 10;
    int LIGHT_LEVEL_FULL_POWER = 15;

    // Propertiesレコードは、電球の熱特性を表すためのデータ構造です。
    // dissipationFactorは熱の放散率を表し、
    // thermalMassは熱容量を表し、
    // overheatTemperatureは過熱温度を表します。
    record Properties(float dissipationFactor, float thermalMass, float overheatTemperature) { }

    // resistanceFunctionメソッドは、温度に応じた電球の抵抗値を計算するための関数です。
    // thermalPropertiesメソッドは、電球の熱特性を返すメソッドです。
    // createStateメソッドは、電球の状態を作成するためのメソッドです。
    float resistanceFunction(float temperature);
    Properties thermalProperties();
    <F extends SmartBlockEntity & ILedFixtureEntity> LedBulbState createState(F fixture);

    // canBeDyedメソッドは、電球が染色可能かどうかを判定するためのメソッドです。
    boolean canBeDyed();
}
