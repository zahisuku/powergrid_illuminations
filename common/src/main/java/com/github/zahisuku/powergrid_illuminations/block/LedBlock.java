package com.github.zahisuku.powergrid_illuminations.block;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import org.patryk3211.powergrid.electricity.base.ElectricBlock;
import org.patryk3211.powergrid.electricity.base.IDecoratedTerminal;
import org.patryk3211.powergrid.electricity.base.TerminalBoundingBox;
import org.patryk3211.powergrid.electricity.base.terminals.BlockStateTerminalCollection;

public class LedBlock extends ElectricBlock {
    // 点灯・消灯を管理するブロック状態（Lit）
    public static final BooleanProperty LIT = BlockStateProperties.LIT;

    /**
     * LEDの電気端子。
     *
     * Terminal 0 = Z-側
     * Terminal 1 = Z+側
     *
     * 座標はPowerGridの既存ResistorBlockの
     * 2端子配置を基準にしている。
     */
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

    /**
     * LED本体の形状。
     *
     * STEP4ではまだ専用モデルを作成していないため、
     * ブロック本体を通常の1ブロック形状として扱う。
     *
     * BlockStateTerminalCollection側で端子形状も
     * 自動的に追加される。
     */
   private static final VoxelShape SHAPE = Shapes.block();

    public LedBlock(Properties properties) {
        super(properties);

        /*
         * 初期状態は消灯。
         */
        registerDefaultState(
                defaultBlockState()
                        .setValue(LIT, false)
        );

        /*
         * PowerGridの電気端子を登録。
         *
         * LITの値によって端子を変更する必要はないため、
         * すべてのBlockStateで同じ2端子を使用する。
         */
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
}