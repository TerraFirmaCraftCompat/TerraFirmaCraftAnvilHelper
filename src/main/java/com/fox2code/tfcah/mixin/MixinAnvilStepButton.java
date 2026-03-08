package com.fox2code.tfcah.mixin;

import com.fox2code.tfcah.client.GuiGraphicsHelper;
import com.fox2code.tfcah.client.TerraFirmaCraftAnvilHelperClient;
import net.dries007.tfc.client.screen.AnvilScreen;
import net.dries007.tfc.client.screen.button.AnvilStepButton;
import net.dries007.tfc.common.capabilities.forge.ForgeStep;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AnvilStepButton.class)
public class MixinAnvilStepButton extends Button {
    @Shadow(remap = false)
    @Final
    private ForgeStep step;

    protected MixinAnvilStepButton(Builder builder) {
        super(builder);
    }

    @Inject(method = "renderWidget", at = @At("HEAD"), cancellable = true, remap = false)
    public void onRenderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick, CallbackInfo ci) {
        if (TerraFirmaCraftAnvilHelperClient.shouldGlow(this.step)) {
            GuiGraphicsHelper.blitYellowBlink(graphics, AnvilScreen.BACKGROUND, this.getX(), this.getY(), 16, 16,
                    (float)this.step.iconX(), (float)this.step.iconY(), 32, 32, 256, 256);
            ci.cancel();
        }
    }
}
