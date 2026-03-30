package dev.oblongboot.sxp.mixin;

import dev.oblongboot.sxp.ui.SettingsScreen;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Gui.class)
public class HotbarMixin {
    @Inject(method = "renderItemHotbar", at = @At("HEAD"), cancellable = true)
    private void injectRenderHotbar(GuiGraphics guiGraphics, DeltaTracker deltaTracker, CallbackInfo ci) {
        if (Minecraft.getInstance().screen instanceof SettingsScreen) {
            ci.cancel();
        }
    }

    @Inject(method = "renderCrosshair", at = @At("HEAD"), cancellable = true)
    private void injectRenderCrosshair(GuiGraphics g, DeltaTracker d, CallbackInfo ci) {
        if (Minecraft.getInstance().screen instanceof SettingsScreen) {
            ci.cancel();
        }
    }
}