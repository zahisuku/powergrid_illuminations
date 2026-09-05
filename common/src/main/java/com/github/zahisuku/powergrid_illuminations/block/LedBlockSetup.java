package com.github.zahisuku.powergrid_illuminations.block;

import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.patryk3211.powergrid.electricity.base.TerminalBoundingBox;

/**
 * プラットフォーム固有のLedBlockセットアップを行うクラス
 * Forge と Fabric/Neoforge で異なる実装が必要
 */
public class LedBlockSetup {
    /**
     * LedBlockのプラットフォーム固有の初期化を行う
     * @param ledBlock セットアップ対象のLedBlockインスタンス
     * @param terminals ターミナルの配置情報
     * @param shape ブロックの形状
     */
    @ExpectPlatform
    public static void setupTerminalCollection(
            LedBlock ledBlock,
            TerminalBoundingBox[] terminals,
            VoxelShape shape
    ) {
        throw new AssertionError();
    }
}
