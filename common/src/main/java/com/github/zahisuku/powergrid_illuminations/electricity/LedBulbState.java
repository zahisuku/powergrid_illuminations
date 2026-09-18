package com.github.zahisuku.powergrid_illuminations.electricity;


import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.patryk3211.powergrid.PowerGrid;
import org.patryk3211.powergrid.electricity.base.ElectricBehaviour;
import org.patryk3211.powergrid.electricity.base.ThermalBehaviour;

// LightBulbStateは電球の状態を表す抽象クラスであり、電球の温度、過熱状態、色などの情報を管理します。
// ElectricBehaviour.SyncAppenderインターフェースを実装しており、
// 電球の状態を同期するためのメソッドを提供します。
public abstract class LedBulbState implements ElectricBehaviour.SyncAppender {
    // 電球のアイテム、電球のインターフェース、照明器具のロジック、照明器具のブロックエンティティを保持します。
    protected final Item item;
    protected final ILedBulb bulb;
    protected final ILedFixtureEntity fixtureLogic;
    protected final SmartBlockEntity fixtureBE;

    // 電球の熱特性を表すフィールドを定義します。
    // thermalMass: 電球の熱容量を表す浮動小数点数
    // dissipationFactor: 電球の熱放散係数を表す浮動小数点数
    // overheatTemperature: 電球の過熱温度を表す浮動小数点数
    // temperature: 電球の現在の温度を表す浮動小数点数
    // burned: 電球が焼損しているかどうかを表すブール値
    // overheatTicks: 電球が過熱状態にある時間を表す整数
    // playEffect: 電球が焼損した際にエフェクトを再生するかどうかを表すブール値
    // cooldown: 電球が冷却中かどうかを表すブール値
    protected final float thermalMass;
    protected final float dissipationFactor;
    protected final float overheatTemperature;
    protected float temperature;
    protected boolean burned;
    private int overheatTicks;
    private boolean playEffect;
    private boolean cooldown;

    // cachedAmbientTemperature: 電球の周囲温度をキャッシュするためのフィールド
    private Float cachedAmbientTemperature = null;

    // color: 電球の色を表すDyeColorオブジェクト。nullの場合は色が設定されていないことを示す。
    @Nullable
    protected DyeColor color;

    // コンストラクタ。電球のアイテムと照明器具のブロックエンティティを受け取り、初期化を行います。
    // TはItemとILightBulbの両方を実装する型、FはSmartBlockEntityとIFixtureEntityの両方を実装する型です。
    // 電球の熱特性を取得し、初期温度や焼損状態を設定します。
    // @param bulb 電球のアイテム
    // @param fixture 照明器具のブロックエンティティ
    // @param <T> 電球の型
    // @param <F> 照明器具のブロックエンティティの型
    public <T extends Item&ILedBulb, F extends SmartBlockEntity&ILedFixtureEntity> LedBulbState(T bulb, F fixture) {
        // 電球のアイテム、電球のインターフェース、照明器具のロジック、照明器具のブロックエンティティを初期化します。
        this.item = bulb;
        this.bulb = bulb;
        this.fixtureBE = fixture;
        this.fixtureLogic = fixture;

        // 電球の熱特性を取得し、熱容量、熱放散係数、過熱温度を設定します。
        var properties = bulb.thermalProperties();
        thermalMass = properties.thermalMass();
        dissipationFactor = properties.dissipationFactor();
        overheatTemperature = properties.overheatTemperature();

        this.burned = false;
    }

    // 電球に電力を適用するメソッド。
    // 電球が焼損していない場合、与えられた熱量を基にエネルギーを計算し、温度を更新します。
    // 熱量が負の場合、温度が周囲温度より低くならないように調整します。
    protected void applyPower(double power) {
        if(burned)
            return;
        double energy = power / 20.0;
        temperature += (float) (energy / thermalMass);
        if(energy < 0 && temperature < cachedAmbientTemperature)
            temperature = cachedAmbientTemperature;
    }

    // 電球の電力レベルを更新するメソッド。
    // 新しい電力レベルが現在の電力レベルと異なる場合、照明器具のロジックに新しい電力レベルを設定します。
    protected void updatePowerLevel(int newLevel) {
        if(newLevel != fixtureLogic.getPowerLevel()) {
            fixtureLogic.setPowerLevel(newLevel);
        }
    }

    public int getPowerLevel() {
        return fixtureLogic.getPowerLevel();
    }

    private void burnEffect() {
        var world = fixtureBE.getLevel();
        if(world.isClientSide) {
            var pos = fixtureBE.getBlockPos().getCenter();
            world.addParticle(ParticleTypes.FLASH, pos.x, pos.y, pos.z, 0, 0, 0);
        }
    }

    // tickは電球の状態を毎ゲーム更新するメソッドです。
    public void tick() {
        // 焼損済みの電球は終了。
        if(burned)
            return;
        var world = fixtureBE.getLevel();
        if(cachedAmbientTemperature == null) {
            // 環境温度を保存
            cachedAmbientTemperature = ThermalBehaviour.getAmbientTemperature(world, fixtureBE.getBlockPos());
        }
        // サーバー側で処理
        if(!world.isClientSide) {
            
            // 照明器具のロジックから、フィラメント情報を取得
            var filament = fixtureLogic.getFilament();
            // 環境へ逃げる熱量を計算（熱放散係数×(現在の温度-周囲の温度)）
            float dissipatedPower = dissipationFactor * (temperature - cachedAmbientTemperature);
           
            // 回路が完成しているとき、フィラメントの発熱量-放熱量を電球に適用
            // ネットワークが未接続の時、cooldown(放熱)を有効にし、-放熱量を電球に適用
            if(filament.isConverged()) {
                applyPower(filament.power() - dissipatedPower);
                cooldown = false;
            } else if(filament.getNetwork() == null) {
                if(cooldown) {
                    applyPower(-dissipatedPower);
                } else {
                    cooldown = true;
                }
            }
            // 温度が異常値のとき、環境温度に設定。
            if(!Float.isFinite(temperature))
                temperature = cachedAmbientTemperature;
            // 現在温度を用いて、フィラメントの抵抗値を更新する。
            filament.setResistance(bulb.resistanceFunction(temperature));

            //　オーバーヒートかつ、その時間が4Tick以上ならば、焼損し、フィラメント停止、電力値を0にする。
            // また、オーバーヒート判定が5回目のときに実行
            if (isOverheated() && overheatTicks++ >= 4) {
                burned = true;
                playEffect = true;
                filament.setState(false);
                updatePowerLevel(0);
                fixtureBE.notifyUpdate();
                return;
            } else if (!isOverheated()) {
                overheatTicks = 0;
            }
            // 電力レベルを1200以上で1,1400以上で2,それ以外は0
            int powerLevel = 0;
            if(temperature > 1400f) {
                powerLevel = 2;
            } else if(temperature > 1200f) {
                powerLevel = 1;
            }
            // 電力レベルを更新する。
            updatePowerLevel(powerLevel);
        }
    }

    protected void specialEffects(BlockPos pos, @Nullable Direction facing) {

    }

    public void runSpecialEffects(Level level, BlockPos pos, @Nullable Direction facing) {
        if(burned || level.isClientSide || getPowerLevel() == 0)
            return;
        specialEffects(pos, facing);
    }

    // OverHeatか否か
    public boolean isOverheated() {
        return temperature >= overheatTemperature;
    }

    public boolean isBurned() {
        return burned;
    }

    // 温度による抵抗の函数
    public float resistance() {
        return bulb.resistanceFunction(temperature);
    }

    public ItemStack toStack() {
        return new ItemStack(item);
    }

    public boolean isOf(Item item) {
        return this.item == item;
    }

    @Environment(EnvType.CLIENT)
    public abstract PartialModel getModel();
    @Environment(EnvType.CLIENT)
    public abstract PartialModel getDyedBulb();

    @NotNull
    @Environment(EnvType.CLIENT)
    public abstract PartialModel getLightModel();

    public float getAlpha() {
        var x = Mth.clamp((temperature - 600f) / (1400f - 600f), 0, 1);
        return (Float)x * (Float)x;
    }

    public void write(CompoundTag nbt) {
        nbt.putString("led_Bulb", BuiltInRegistries.ITEM.getKey(item).toString());
        nbt.putFloat("Temperature", temperature);
        if(burned)
            nbt.putBoolean("Burned", true);
        if(playEffect) {
            nbt.putBoolean("Effect", true);
            playEffect = false;
        }
        if(color != null)
            nbt.putInt("Color", color.ordinal());
    }

    public void read(CompoundTag nbt) {
        var bulbItem = BuiltInRegistries.ITEM.get(ResourceLocation.parse(nbt.getString("led_Bulb")));
        if(bulbItem != item) {
            PowerGrid.LOGGER.error("Bulb item validation failed");
            return;
        }
        temperature = nbt.getFloat("Temperature");
        burned = nbt.getBoolean("Burned");
        fixtureLogic.getFilament().setState(!burned);
        if(nbt.getBoolean("Effect")) {
            burnEffect();
        }

        if(bulb.canBeDyed() && nbt.contains("Color")) {
            color = DyeColor.values()[nbt.getInt("Color")];
        } else {
            color = null;
        }
    }

    public static Item getBulbItem(CompoundTag nbt) {
        if(!nbt.contains("led_Bulb"))
            return null;
        var bulbItem = BuiltInRegistries.ITEM.get(ResourceLocation.parse(nbt.getString("led_Bulb")));
        if(!(bulbItem instanceof ILedBulb)) {
            PowerGrid.LOGGER.error("Tried to use a non light bulb item for light bulb state");
            return null;
        }
        return bulbItem;
    }

    public Item getItem() {
        return item;
    }

    public boolean setColor(DyeColor color) {
        if(bulb.canBeDyed()) {
            this.color = color;
            return true;
        }
        return false;
    }

    public DyeColor getColor() {
        return color;
    }

    @Override
    public void writeToSync(FriendlyByteBuf buffer) {
        buffer.writeFloat(temperature);
    }

    @Override
    public void readFromSync(FriendlyByteBuf buffer) {
        temperature = buffer.readFloat();
    }
}
