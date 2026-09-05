package com.github.zahisuku.powergrid_illuminations.block;

import net.minecraft.world.phys.shapes.VoxelShape;
import org.patryk3211.powergrid.electricity.base.TerminalBoundingBox;

/**
 * Fabric用LedBlockSetupの実装
 * Fabricではsetupが不要なため、空の実装
 */
public class LedBlockSetup {
    public static void setupTerminalCollection(
            LedBlock ledBlock,
            TerminalBoundingBox[] terminals,
            VoxelShape shape
    ) {
        // Fabric では setTerminalCollection メソッドが存在しないため
        // ここでは何もしない
    }
}
