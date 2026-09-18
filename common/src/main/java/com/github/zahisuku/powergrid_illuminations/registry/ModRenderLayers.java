package com.github.zahisuku.powergrid_illuminations.registry;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;

import java.util.OptionalDouble;

public class ModRenderLayers {
    private static final RenderType DEBUG_LINES = RenderType.create(
            "powergrid_illuminations_debug_lines",
            DefaultVertexFormat.POSITION_COLOR,
            VertexFormat.Mode.DEBUG_LINES,
            256,
            RenderType.CompositeState.builder()
                    .setShaderState(RenderStateShard.POSITION_COLOR_SHADER)
                    .setCullState(RenderStateShard.NO_CULL)
                    .setTransparencyState(RenderStateShard.NO_TRANSPARENCY)
                    .setLineState(new RenderStateShard.LineStateShard(OptionalDouble.of(1.0f)))
                    .createCompositeState(false)
    );

    private static final RenderType COLOR = RenderType.create(
            "powergrid_illuminations_color",
            DefaultVertexFormat.POSITION_COLOR,
            VertexFormat.Mode.QUADS,
            256,
            RenderType.CompositeState.builder()
                    .setShaderState(RenderStateShard.POSITION_COLOR_SHADER)
                    .setCullState(RenderStateShard.NO_CULL)
                    .setTransparencyState(RenderStateShard.NO_TRANSPARENCY)
                    .createCompositeState(false)
    );

    private static final RenderType ADDITIVE_CRT = RenderType.create(
            "powergrid_illuminations_additive_crt",
            DefaultVertexFormat.POSITION_COLOR,
            VertexFormat.Mode.TRIANGLES,
            256,
            false, false,
            RenderType.CompositeState.builder()
                    .setShaderState(RenderStateShard.POSITION_COLOR_SHADER)
                    .setCullState(RenderStateShard.NO_CULL)
                    .setTransparencyState(RenderStateShard.ADDITIVE_TRANSPARENCY)
                    .setWriteMaskState(RenderStateShard.COLOR_WRITE)
                    .setOutputState(RenderStateShard.PARTICLES_TARGET)
                    .createCompositeState(false)
    );

    private static final RenderType ADDITIVE_COLOR = RenderType.create(
            "powergrid_illuminations_additive_color",
            DefaultVertexFormat.POSITION_COLOR,
            VertexFormat.Mode.QUADS,
            256,
            false, false,
            RenderType.CompositeState.builder()
                    .setShaderState(RenderStateShard.POSITION_COLOR_SHADER)
                    .setCullState(RenderStateShard.NO_CULL)
                    .setTransparencyState(RenderStateShard.ADDITIVE_TRANSPARENCY)
                    .setOutputState(RenderStateShard.PARTICLES_TARGET)
                    .createCompositeState(false)
    );

    private static final RenderType ADDITIVE = RenderType.create("powergrid_illuminations_additive", DefaultVertexFormat.BLOCK,
            VertexFormat.Mode.QUADS, RenderType.SMALL_BUFFER_SIZE, true, true, RenderType.CompositeState.builder()
                    .setShaderState(RenderStateShard.RENDERTYPE_TRANSLUCENT_SHADER)
                    .setTextureState(RenderStateShard.BLOCK_SHEET)
                    .setTransparencyState(RenderStateShard.ADDITIVE_TRANSPARENCY)
                    .setCullState(RenderStateShard.NO_CULL)
                    .setLightmapState(RenderStateShard.LIGHTMAP)
                    .setOverlayState(RenderStateShard.OVERLAY)
                    .setOutputState(RenderStateShard.TRANSLUCENT_TARGET)
                    .createCompositeState(true));

    public static RenderType getDebugLines() {
        return DEBUG_LINES;
    }

    public static RenderType getColor() {
        return COLOR;
    }

    public static RenderType getAdditiveCrt() {
        return ADDITIVE_CRT;
    }

    public static RenderType getAdditiveColor() {
        return ADDITIVE_COLOR;
    }

    public static RenderType getAdditive() {
        return ADDITIVE;
    }

    @SuppressWarnings("EmptyMethod")
    public static void register() { /* Initialize static fields. */ }
}
