package com.github.zahisuku.powergrid_illuminations.electricity;

import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;

import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.EnumMap;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

import dev.architectury.utils.Env;
import dev.architectury.utils.EnvExecutor;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import org.patryk3211.powergrid.electricity.base.ThermalBehaviour;
import org.patryk3211.powergrid.electricity.info.IHaveElectricProperties;
import org.patryk3211.powergrid.electricity.info.Power;
import org.patryk3211.powergrid.electricity.info.Resistance;
import org.patryk3211.powergrid.electricity.info.Voltage;

import static com.github.zahisuku.powergrid_illuminations.PowerGridIlluminations.asResource;

public class SmallLedBulb extends Item implements ILedBulb, IHaveElectricProperties {
    // modelSupplierは、電球の状態に応じたモデルを提供するためのSupplierです。
    // stateに応じたPartialModelを返すFunctionを提供します。
    //
    // dyedModelSupplierは、染色された電球の状態に応じたモデル
    // DayedStateに応じたPartialModelを返すFunctionを提供します。
    protected Supplier<Function<State, PartialModel>> modelSupplier = null;
    protected Supplier<Function<DyedState, PartialModel>> dyedModelSupplier = null;

    private static final PartialModel MODEL_OFF = partial("block/small_leds/small_led_bulb");
    private static final PartialModel MODEL_ON = partial("block/small_leds/small_led_bulb_on");
    private static final PartialModel MODEL_BROKEN = partial("block/small_leds/small_led_bulb_broken");
    private static final PartialModel MODEL_LIGHT = partial("block/small_leds/small_led_bulb_light");
    private static final PartialModel DYED_MODEL_OFF = partial("block/small_leds/dyed_small_led_bulb");
    private static final PartialModel DYED_MODEL_ON = partial("block/small_leds/dyed_small_led_bulb_on");
    private static final PartialModel DYED_MODEL_BROKEN = partial("block/small_leds/dyed_small_led_bulb_broken");
    private static final PartialModel DYED_MODEL_LIGHT = partial("block/small_leds/dyed_small_led_bulb_light");
    private static final PartialModel DYED_MODEL_BULB = partial("block/small_leds/dyed_small_led_bulb_bulb");

    // 電球の最大温度、最大抵抗、最小抵抗などの電気的特性を定義するフィールドです。
    // T_maxは、電球が耐えられる最大温度を表します。
    // R_maxは、電球の最大抵抗を表します。
    // R_minは、電球の最小抵抗を表します。
    // thermalPropertiesは、電球の熱的特性を表すILedBulb.Propertiesオブジェクトです。  
    // canBeDyedは、電球が染色可能かどうかを示すブール値です。
    protected float T_max = 1200;
    protected float R_max = 100;
    protected float R_min = 15;
    protected ILedBulb.Properties thermalProperties;
    protected boolean canBeDyed;

    // powerは、電球の定格電力を表します。
    // voltageは、電球の定格電圧を表します。
    protected float power = 0;
    protected float voltage = 0;

    private static final float RATED_POWER_WATTS = 3.0f;
    private static final float RATED_VOLTAGE_VOLTS = 120.0f;
    private static final float TEMPERATURE_AT_RATED_RESISTANCE = 1450.0f;
    private static final float MIN_RESISTANCE_FACTOR = 0.85f;
    private static final float THERMAL_MASS = 0.00015f;
    private static final float OVERHEAT_TEMPERATURE = 2100.0f;
    private static final float DISSIPATION_DIVISOR = 1450.0f;

    // コンストラクタは、Item.Propertiesを引数として受け取り、Itemクラスのコンストラクタを呼び出します。
    // これにより、電球アイテムの基本的な設定が行われます。
    // Item.Propertiesは、アイテムの特性（耐久性、スタックサイズなど）を設定するためのオブジェクトです。
    public SmallLedBulb(net.minecraft.world.item.Item.Properties settings) {
        super(settings);
        this.canBeDyed = true;
        this.modelSupplier = () -> state -> switch (state) {
            case OFF -> MODEL_OFF;
            case LOW_POWER, ON -> MODEL_ON;
            case BROKEN -> MODEL_BROKEN;
            case LIGHT -> MODEL_LIGHT;
        };
        this.dyedModelSupplier = () -> state -> switch (state) {
            case OFF -> DYED_MODEL_OFF;
            case LOW_POWER, ON -> DYED_MODEL_ON;
            case BROKEN -> DYED_MODEL_BROKEN;
            case LIGHT -> DYED_MODEL_LIGHT;
            case BULB -> DYED_MODEL_BULB;
        };
        // this.setProperties(30, 120, 120, 0.005f);
        applyRatedValues(
                RATED_POWER_WATTS,
                RATED_VOLTAGE_VOLTS,
                TEMPERATURE_AT_RATED_RESISTANCE,
                THERMAL_MASS
        );
    }




    // resistanceFunctionメソッドは、電球の抵抗を温度に応じて計算するためのメソッドです。
    // このメソッドは、温度を引数として受け取り、抵抗値を返します。
    // 抵抗値は、R_minからR_maxまでの範囲で、温度に応じて線形に変化します。
    @Override
    public float resistanceFunction(float temperature) {
        return resistanceFunction(R_min, R_max, T_max, temperature);
    }

    // resistanceFunctionメソッドは、電球の抵抗を温度に応じて計算するための静的メソッドです。
    // このメソッドは、最小抵抗R_min、最大抵抗R_max、最大温度T_max、温度temperatureを引数として受け取り、抵抗値を返します。
    // 抵抗値は、R_minからR_maxまでの範囲で、温度に応じて線形に変化します。
    public static float resistanceFunction(float R_min, float R_max, float T_max, float temperature) {
        return R_min + ((R_max - R_min) / T_max) * temperature;
    }

    @Override
    public ILedBulb.Properties thermalProperties() {
        return thermalProperties;
    }

    @Override
    public <F extends SmartBlockEntity & ILedFixtureEntity> LedBulbState createState(F fixture) {
        return new SimpleState(this, fixture, modelSupplier, dyedModelSupplier);
    }

    @Override
    public boolean canBeDyed() {
        return canBeDyed;
    }

    // appendPropertiesメソッドは、電球のアイテムスタックに関する情報をツールチップに追加するためのメソッドです。
    // このメソッドは、ItemStack、Player、ツールチップのリストを引数として受け取り、
    // 電球の定格電圧、定格電力、または最大抵抗に関する情報をツールチップに追加します。
    @Override
    public void appendProperties(ItemStack stack, Player player, List<Component> tooltip) {
        // 電球の定格電圧と定格電力が正の値である場合、それらの情報をツールチップに追加します。
        // そうでない場合、最大抵抗に関する情報をツールチップに追加します。
        if(voltage > 0 && power > 0) {
            Voltage.rated(voltage, player, tooltip);
            Power.rated(power, player, tooltip);
        } else {
            Resistance.series(R_max, player, tooltip);
        }
    }

    // Stateは、電球の状態を表す列挙型です。
    public enum State {
        OFF, LOW_POWER, ON, BROKEN, LIGHT
    }

    // DyedStateは、染色された電球の状態を表す列挙型です。
    public enum DyedState {
        OFF, LOW_POWER, ON, BROKEN, LIGHT, BULB;

        // fromStateメソッドは、State列挙型の値をDyedState列挙型の対応する値に変換します。
        public static DyedState fromState(State state) {
            return switch(state) {
                case OFF -> OFF;
                case LOW_POWER -> LOW_POWER;
                case ON -> ON;
                case BROKEN -> BROKEN;
                case LIGHT -> LIGHT;
            };
        }
    }

    // SimpleStateクラスは、LedBulbStateクラスを継承し、電球の状態を管理するための内部クラスです。
    // このクラスは、電球のモデルや染色モデルを提供するためのFunctionを保持し、
    // 電球の状態に応じたモデルを返すメソッドを実装しています。
    // EnvExecutor.runInEnvを使用して、クライアント環境でのみモデルプロバイダーを設定する処理を実行します。
    // これにより、電球の状態に応じたモデルが正しく表示されるようになります。
    // また、電球の状態に応じたアルファ値を返すgetAlphaメソッドも実装されています。
    public static class SimpleState extends LedBulbState {
        // modelProviderは、電球の状態に応じたモデルを提供するためのFunctionです。
        @Environment(EnvType.CLIENT)
        public Function<State, PartialModel> modelProvider;
        // dyedModelProviderは、染色された電球の状態に応じたモデルを提供するためのFunctionです。
        @Environment(EnvType.CLIENT)
        public Function<DyedState, PartialModel> dyedModelProvider;

        // SimpleStateコンストラクタは、電球(アイテム)とフィクスチャを引数として受け取り、モデルプロバイダーを設定します。
        // EnvExecutor.runInEnvを使用して、クライアント環境でのみモデルプロバイダーを設定する処理を実行します。
        // これにより、電球の状態に応じたモデルが正しく表示されるようになります。
        public <T extends Item & ILedBulb, F extends SmartBlockEntity & ILedFixtureEntity> SimpleState(T bulb, F fixture,
                                                                                                        Supplier<Function<State, PartialModel>> modelProviderSupplier,
                                                                                                        @Nullable Supplier<Function<DyedState, PartialModel>> dyedModelProviderSupplier) {
            // LedBulbStateクラスのコンストラクタを呼び出し、電球とフィクスチャを設定します。
            super(bulb, fixture);
            // クライアント環境でのみモデルプロバイダーを設定する処理を実行します。
            EnvExecutor.runInEnv(Env.CLIENT, () -> () -> {
                modelProvider = modelProviderSupplier.get();
                if(dyedModelProviderSupplier != null)
                    dyedModelProvider = dyedModelProviderSupplier.get();
            });
        }

        // getModelメソッドは、電球の状態に応じたモデルを返すメソッドです。
        // 電球が壊れている場合はBROKEN状態のモデルを返し
        // 電球が点灯している場合はON状態のモデルを返し、
        // 低電力の場合はLOW_POWER状態のモデルを返します。
        @Override
        @Environment(EnvType.CLIENT)
        public PartialModel getModel() {
            var state = State.OFF;
            if(burned) {
                state = State.BROKEN;
            } else {
                int powerLevel = fixtureLogic.getPowerLevel();
                if(powerLevel == 1) {
                    state = State.LOW_POWER;
                } else if(powerLevel == 2) {
                    state = State.ON;
                }
            }
            if(bulb.canBeDyed() && color != null)
                return dyedModelProvider.apply(DyedState.fromState(state));
            return modelProvider.apply(state);
        }

        // getDyedBulbメソッドは、染色された電球のモデルを返すメソッドです。
        // 電球が染色可能である場合、DyedState.BULB状態のモデルを返します。
        // それ以外の場合はnullを返します。
        @Override
        @Environment(EnvType.CLIENT)
        public PartialModel getDyedBulb() {
            if(bulb.canBeDyed())
                return dyedModelProvider.apply(DyedState.BULB);
            return null;
        }

        // getLightModelメソッドは、電球の光のモデルを返すメソッドです。
        // 電球が染色可能である場合、DyedState.LIGHT状態のモデルを返します。
        // それ以外の場合はnullを返します。
        @Override
        @Environment(EnvType.CLIENT)
        public @NotNull PartialModel getLightModel() {
            // if(bulb.canBeDyed() && color != null)
            //     return dyedModelProvider.apply(DyedState.LIGHT);
            return modelProvider.apply(State.LIGHT);
        }

        // getAlphaメソッドは、電球の状態に応じたアルファ値を返すメソッドです。
        // 電球の電力レベルに応じて、アルファ値を調整します。
        // 電力レベルが2の場合は完全に不透明（1.0）を返し、
        // 電力レベルが1の場合は0.5625以上の値を返します。
        // それ以外の場合は、親クラスのgetAlphaメソッドの値を返します。
        @Override
        public float getAlpha() {
            int powerLevel = fixtureLogic.getPowerLevel();
            if(powerLevel == 2) {
                return 1;
            } else if(powerLevel == 1) {
                // 0.5625から1の間の値で、(temperature - 600f) / (1400f - 600f)
                return Math.max(0.5625f, super.getAlpha());
            } else {
                // 0から1の間の値で、(temperature - 600f) / (1400f - 600f)
                return super.getAlpha();
            }
        }
    }

    private static PartialModel partial(String path) {
        return PartialModel.of(asResource(path));
    }

    private void applyRatedValues(
            float ratedPower,
            float ratedVoltage,
            float maxTemperature,
            float thermalMass
    ) {
        this.power = ratedPower;
        this.voltage = ratedVoltage;
        this.T_max = maxTemperature;
        this.R_max = (ratedVoltage * ratedVoltage) / ratedPower;
        this.R_min = this.R_max * MIN_RESISTANCE_FACTOR;
        this.thermalProperties = new ILedBulb.Properties(
                ratedPower / DISSIPATION_DIVISOR,
                thermalMass,
                OVERHEAT_TEMPERATURE
        );
    }
}