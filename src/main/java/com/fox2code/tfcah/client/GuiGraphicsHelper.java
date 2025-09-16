package com.fox2code.tfcah.client;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;

public final class GuiGraphicsHelper {
    private GuiGraphicsHelper() {}

    public static void blitYellowBlink(GuiGraphics guiGraphics, ResourceLocation atlasLocation, int x, int y, int width, int height, float uOffset, float vOffset, int uWidth, int vHeight, int textureWidth, int textureHeight) {
        blitYellowBlink(guiGraphics, atlasLocation, x, x + width, y, y + height, 0, uWidth, vHeight, uOffset, vOffset, textureWidth, textureHeight);
    }

    public static void blitYellowBlink(GuiGraphics guiGraphics, ResourceLocation atlasLocation, int x1, int x2, int y1, int y2, int blitOffset, int uWidth, int vHeight, float uOffset, float vOffset, int textureWidth, int textureHeight) {
        float modifier = ((System.currentTimeMillis() / 500L) & 1L) == 0L ? 0F : 0.2F;
        guiGraphics.innerBlit(atlasLocation, x1, x2, y1, y2, blitOffset,
                (uOffset + 0.0F) / (float)textureWidth, (uOffset + (float)uWidth) / (float)textureWidth,
                (vOffset + 0.0F) / (float)textureHeight, (vOffset + (float)vHeight) / (float)textureHeight,
                1F - modifier, 1F - modifier, modifier, 1F);
    }
}
