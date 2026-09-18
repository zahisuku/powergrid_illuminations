package com.github.zahisuku.powergrid_illuminations.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.foundation.blockEntity.renderer.SafeBlockEntityRenderer;
import net.createmod.catnip.render.CachedBuffers;
import net.createmod.catnip.render.SuperByteBuffer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;

public class LEDFixtureRenderer extends SafeBlockEntityRenderer<LEDFixtureBlockEntity> {
    public LEDFixtureRenderer(BlockEntityRendererProvider.Context context) {}

    @Override
    protected void renderSafe(LEDFixtureBlockEntity be, float partialTicks,
                              PoseStack poseStack, MultiBufferSource buffers,
                              int light, int overlay) {
        var bulb = be.getBulbState();
        if (bulb == null)
            return;

        var state = be.getBlockState();
        var model = bulb.getModel();
        if (model == null)
            return;

        var buffer = CachedBuffers.partial(model, state);
        rotateToFacing(buffer, state.getValue(LEDFixtureBlock.FACING))
                .translate(((LEDFixtureBlock) state.getBlock()).modelOffset)
                .light(light)
                .renderInto(poseStack, buffers.getBuffer(RenderType.cutout()));
    }

    private static SuperByteBuffer rotateToFacing(SuperByteBuffer buffer, Direction facing) {
        return switch (facing) {
            case UP -> buffer;
            case DOWN -> buffer.rotateCentered((float) Math.PI, Direction.EAST);
            default -> {
                buffer.rotateCentered((float) Math.PI * 0.5f, Direction.EAST);
                yield buffer.rotateCentered(
                    (float) (facing.toYRot() / 180f * Math.PI), Direction.SOUTH);
            }
        };
    }
}