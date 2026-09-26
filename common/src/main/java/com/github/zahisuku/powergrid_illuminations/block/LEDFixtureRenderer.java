package com.github.zahisuku.powergrid_illuminations.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.foundation.blockEntity.renderer.SafeBlockEntityRenderer;
import net.createmod.catnip.render.CachedBuffers;
import net.createmod.catnip.render.SuperByteBuffer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;

import com.github.zahisuku.powergrid_illuminations.registry.ModRenderLayers;
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
        var facing = state.getValue(LEDFixtureBlock.FACING);
        boolean isaxis = state.getValue(LEDFixtureBlock.ALONG_FIRST_AXIS);

        var vb = buffers.getBuffer(RenderType.cutout());
        var model = bulb.getModel();
        if (model == null)
            return;

        var buffer = CachedBuffers.partial(model, state);
        rotateToAxisFacing(buffer, state.getValue(LEDFixtureBlock.FACING),isaxis)
                .translate(((LEDFixtureBlock) state.getBlock()).modelOffset)
                .light(light)
                .renderInto(poseStack, vb);


        var color = bulb.getColor();
        int r = 255, g = 255, b = 255;
        if(color != null) {
            vb = buffers.getBuffer(RenderType.translucent());
            var bulbBuffer = CachedBuffers.partial(bulb.getDyedBulb(), state);
            var texDif = (int)color.getTextureDiffuseColor();
            // Basic port of getTextureDiffuseColors
            r = (texDif & 0xFF0000) >> 16;
            g = (texDif & 0xFF00) >> 8;
            b = (texDif & 0xFF) >> 0;
            rotateToFacing(bulbBuffer, facing)
                    .color(r, g, b, 255)
                    .translate(((LEDFixtureBlock) state.getBlock()).modelOffset)
                    .light(light)
                    .renderInto(poseStack, vb);
        }
        
        if(bulb.isBurned())
            return;
        
        float a = bulb.getAlpha();
        if(a > 0) {
            var vba = buffers.getBuffer(ModRenderLayers.getAdditive());
            var lightModel = bulb.getLightModel();
            var lightBuffer = CachedBuffers.partial(lightModel, state);
            rotateToFacing(lightBuffer, facing)
                    .translate(((LEDFixtureBlock) state.getBlock()).modelOffset)
                    .light(light)
                    .color((int) (a * r), (int) (a * g), (int) (a * b), 255)
                    .renderInto(poseStack,vba);
        }
    }

    private static SuperByteBuffer rotateToAxisFacing(SuperByteBuffer buffer, Direction facing, boolean isaxis) {
    // -------------------------------------------------------------
    // Step 1: まず軸（Axis）に沿わせる回転を適用 (Axis Along First)
    // -------------------------------------------------------------
    if(isaxis) {
        switch(facing){
            case UP -> buffer.rotateCentered((float) (Math.PI * 1.5f), Direction.UP); // Y軸まわりに90°傾けてX軸に合わせる
            case DOWN -> buffer.rotateCentered((float) (Math.PI * 0.5f), Direction.UP);
            case SOUTH -> buffer.rotateCentered((float) Math.PI * 0.5f, Direction.SOUTH);
            case NORTH -> buffer.rotateCentered((float) (Math.PI * 1.5f), Direction.SOUTH);
            case EAST -> buffer.rotateCentered((float) Math.PI * 0.5f, Direction.EAST);
            case WEST ->  buffer.rotateCentered((float) (Math.PI * 1.5f), Direction.EAST);
            default -> {}
        }
    }else{
        switch(facing){
            case UP -> buffer.rotateCentered((float) Math.PI,Direction.UP);
            default -> {}
        }
    }

    // -------------------------------------------------------------
    // Step 2: その後に設置面（Facing）へ向けた回転を適用
    // -------------------------------------------------------------
    return rotateToFacing(buffer, facing);
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