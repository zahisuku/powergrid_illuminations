package com.github.zahisuku.powergrid_illuminations.block;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import org.patryk3211.powergrid.electricity.base.ElectricBlock;
import org.patryk3211.powergrid.electricity.base.IDecoratedTerminal;
import org.patryk3211.powergrid.electricity.base.TerminalBoundingBox;
import org.patryk3211.powergrid.electricity.base.terminals.BlockStateTerminalCollection;

public class LedBlock extends ElectricBlock {
    public static final BooleanProperty LIT = BlockStateProperties.LIT;

    private static final TerminalBoundingBox[] TERMINALS = {
            new TerminalBoundingBox(
                    IDecoratedTerminal.CONNECTOR,
                    7, 4, 0.5,
                    9, 6, 3
            ),
            new TerminalBoundingBox(
                    IDecoratedTerminal.CONNECTOR,
                    7, 4, 13,
                    9, 6, 15.5
            )
    };

    private static final VoxelShape SHAPE = Shapes.block();

    public LedBlock(Properties properties) {
        super(properties);

        registerDefaultState(
                defaultBlockState()
                        .setValue(LIT, false)
        );

        setTerminalCollection(
                BlockStateTerminalCollection.builder(this)
                        .forAllStates(state -> TERMINALS)
                        .withShapeMapper(state -> SHAPE)
                        .build()
        );
    }

    @Override
    protected void createBlockStateDefinition(
            StateDefinition.Builder<Block, BlockState> builder
    ) {
        super.createBlockStateDefinition(builder);
        builder.add(LIT);
    }

    @Override
    public InteractionResult use(
            BlockState state,
            Level level,
            BlockPos pos,
            Player player,
            InteractionHand hand,
            BlockHitResult hit
    ) {
        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        }

        if (level.getBlockEntity(pos) instanceof LedBlockEntity led) {
            double power = led.getPower();
            player.displayClientMessage(
                    Component.literal("Power Status: " + String.format("%.3f", power) + " W"),
                    true
            );
            return InteractionResult.SUCCESS;
        }

        return InteractionResult.PASS;
    }
}