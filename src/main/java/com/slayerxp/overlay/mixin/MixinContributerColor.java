package com.slayerxp.overlay.mixin;

import com.slayerxp.overlay.util.ContributerColors;
import net.minecraft.client.font.TextHandler;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Style;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.List;

@Mixin(TextHandler.class)
public class MixinContributerColor {

    @ModifyVariable(
        method = "wrapLines(Lnet/minecraft/text/StringVisitable;ILnet/minecraft/text/Style;)Ljava/util/List;",
        at = @At("HEAD"),
        index = 1,
        argsOnly = true
    )
    private StringVisitable modifyWrapLinesStringVisitable(StringVisitable input) {
        StringVisitable transformed = ContributerColors.INSTANCE.transformStringVisitable(input);
        return transformed != null ? transformed : input;
    }

    @ModifyVariable(
        method = "wrapLines(Lnet/minecraft/text/StringVisitable;ILnet/minecraft/text/Style;Ljava/util/function/BiConsumer;)V",
        at = @At("HEAD"),
        index = 1,
        argsOnly = true
    )
    private StringVisitable modifyWrapLinesStringVisitableWithConsumer(StringVisitable input) {
        StringVisitable transformed = ContributerColors.INSTANCE.transformStringVisitable(input);
        return transformed != null ? transformed : input;
    }

}
