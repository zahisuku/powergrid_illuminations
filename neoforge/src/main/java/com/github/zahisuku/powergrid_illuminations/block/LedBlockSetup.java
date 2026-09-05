package com.github.zahisuku.powergrid_illuminations.block;

import net.minecraft.world.phys.shapes.VoxelShape;
import org.patryk3211.powergrid.electricity.base.TerminalBoundingBox;
import org.patryk3211.powergrid.electricity.base.terminals.BlockStateTerminalCollection;

/**
 * Neoforge用LedBlockSetupの実装
 * PowerGridライブラリのターミナルコレクション設定を行う
 */
public class LedBlockSetup {
    public static void setupTerminalCollection(
            LedBlock ledBlock,
            TerminalBoundingBox[] terminals,
            VoxelShape shape
    ) {
        ledBlock.setTerminalCollection(
                BlockStateTerminalCollection.builder(ledBlock)
                        .forAllStates(state -> terminals)
                        .withShapeMapper(state -> shape)
                        .build()
        );
    }
}
