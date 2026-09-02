package com.github.zahisuku.powergrid_illuminations.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.patryk3211.powergrid.electricity.base.ElectricBlockEntity;

public class LedBlockEntity extends ElectricBlockEntity {
    private static final float LED_RESISTANCE = 15.0f;

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
}
