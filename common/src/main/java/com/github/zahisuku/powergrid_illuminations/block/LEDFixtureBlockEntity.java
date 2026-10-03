package com.github.zahisuku.powergrid_illuminations.block;

import com.github.zahisuku.powergrid_illuminations.electricity.sim.special.SwitchedPNJunctionWire;
import com.simibubi.create.content.schematics.requirement.ItemRequirement;

import net.minecraft.core.BlockPos;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import static net.minecraft.world.level.block.Block.UPDATE_ALL_IMMEDIATE;

public class LEDFixtureBlockEntity extends AbstractLedFixtureBlockEntity{
    private SwitchedPNJunctionWire filament;

    public LEDFixtureBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state, true);
    }

    @Override
    public void buildCircuit(CircuitBuilder builder) {
        builder.setTerminalCount(2);
        var anode = builder.terminalNode(0);
        var cathode = builder.terminalNode(1);
        // 一般的な5mm白色LEDの仕様
        // 逆方向飽和電流, 直列抵抗, 温度（摂氏）, 理想係数, 絶縁破壊電圧, 降伏・飽和電流
        filament = new SwitchedPNJunctionWire(1e-28, 10,25, 2.0,
            5, 1e-5,
            anode,cathode,false);
        builder.add(filament);
    }

    @Override
    public void electricalTick() {
        super.electricalTick();
        if(bulbState != null)
            bulbState.runSpecialEffects(level, worldPosition, getBlockState().getValue(LEDFixtureBlock.FACING));
    }

    public SwitchedPNJunctionWire getFilament() {
        return filament;
    }

    @Override
    public void setPowerLevel(int bulbPower) {
        level.setBlock(worldPosition, getBlockState().setValue(LEDFixtureBlock.POWER, bulbPower), UPDATE_ALL_IMMEDIATE);
    }

    @Override
    public int getPowerLevel() {
        return getBlockState().getValue(LEDFixtureBlock.POWER);
    }

    @Override
    public ItemRequirement getRequiredItems(BlockState state) {
        if(bulbState != null)
            return new ItemRequirement(ItemRequirement.ItemUseType.CONSUME, bulbState.getItem());
        return ItemRequirement.NONE;
    }

    public ItemInteractionResult setColor(DyeColor color) {
        if(bulbState == null)
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        if(bulbState.setColor(color)) {
            notifyUpdate();
            return ItemInteractionResult.SUCCESS;
        }
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }
}
