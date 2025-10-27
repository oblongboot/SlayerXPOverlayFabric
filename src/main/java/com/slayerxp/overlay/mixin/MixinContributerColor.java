package com.slayerxp.overlay.mixin;

//@Mixin(TextHandler.class)
//public class MixinContributerColor {
//
//    @ModifyVariable(
//        method = "wrapLines(Lnet/minecraft/text/StringVisitable;ILnet/minecraft/text/Style;)Ljava/util/List;",
//        at = @At("HEAD"),
//        index = 1,
//        argsOnly = true
//    )
//    private StringVisitable modifyWrapLinesStringVisitable(StringVisitable input) {
//        StringVisitable transformed = ContributerColors.INSTANCE.transformStringVisitable(input);
//        return transformed != null ? transformed : input;
//    }
//
//    @ModifyVariable(
//        method = "wrapLines(Lnet/minecraft/text/StringVisitable;ILnet/minecraft/text/Style;Ljava/util/function/BiConsumer;)V",
//        at = @At("HEAD"),
//        index = 1,
//        argsOnly = true
//    )
//    private StringVisitable modifyWrapLinesStringVisitableWithConsumer(StringVisitable input) {
//        StringVisitable transformed = ContributerColors.INSTANCE.transformStringVisitable(input);
//        return transformed != null ? transformed : input;
//    }
//
//}
// on hold rn while i fix shit