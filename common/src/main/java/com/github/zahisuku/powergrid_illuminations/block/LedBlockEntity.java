package com.github.zahisuku.powergrid_illuminations.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.patryk3211.powergrid.electricity.base.ElectricBlockEntity;
import org.patryk3211.powergrid.electricity.sim.ElectricWire;

public class LedBlockEntity extends ElectricBlockEntity {
    private static final float LED_RESISTANCE = 15.0f;
    private static final double RATED_VOLTAGE = 2.0;
    private static final double MAX_VOLTAGE = 3.0;
    private static final double RATED_POWER = 0.05;
    private static final double MAX_POWER = 0.1;

    private ElectricWire wire;

    public LedBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public void buildCircuit(CircuitBuilder builder) {
        builder.setTerminalCount(2);
        wire = builder.connect(
            LED_RESISTANCE,
            builder.terminalNode(0),
            builder.terminalNode(1)
        );
    }

    public double getPower() {
        return wire == null ? 0.0 : wire.power();
    }

    @Override
    public void electricalTick() {
        super.electricalTick();

        if (wire == null || !wire.isConverged())
            return;

        double voltage = Math.abs(wire.potentialDifference());
        double power = wire.power();
        boolean lit = voltage >= RATED_VOLTAGE
            && voltage <= MAX_VOLTAGE
            && power >= RATED_POWER
            && power <= MAX_POWER;

        BlockState state = getBlockState();
        if (state.getValue(LedBlock.LIT) != lit) {
            level.setBlockAndUpdate(
                worldPosition,
                state.setValue(LedBlock.LIT, lit)
            );
        }

        applyPower(wire);
    }
}