package dev.oblongboot.sxp.mixin;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.text.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import dev.oblongboot.sxp.utils.APIUtils;
import java.util.ArrayList;
import java.util.List;

@Mixin(TextRenderer.class)
public class MixinContributerColor {
    @ModifyVariable(method = "prepare(Lnet/minecraft/text/OrderedText;FFIZZI)Lnet/minecraft/client/font/TextRenderer$GlyphDrawable;", at = @At("HEAD"), argsOnly = true)
    private OrderedText modifyContributerColor(OrderedText text) {
        List<APIUtils.Contributor> contributors = APIUtils.INSTANCE.getContributors();
        for (APIUtils.Contributor c : contributors) {
            int colorOne = parseHexColor(c.getColorOne());
            int colorTwo = parseHexColor(c.getColorTwo());
            text = replaceContributorWordWithColor(text, c.getName(), colorOne, colorTwo);
        }
        return text;
    }

    private static int parseHexColor(String hex) {
        if (hex.startsWith("#")) {
            hex = hex.substring(1);
        }
        try {
            long val = Long.parseLong(hex, 16);
            if (hex.length() == 8) {
                return (int) ((val >> 8) & 0xFFFFFF);
            }
            return (int) (val & 0xFFFFFF);
        } catch (NumberFormatException e) {
            return 0xFFFFFF;
        }
    }

    private static OrderedText replaceContributorWordWithColor(OrderedText orderedText, String target, int colorOne,
            int colorTwo) {
        List<String> chars = new ArrayList<>();
        List<Style> styles = new ArrayList<>();

        orderedText.accept((index, style, codePoint) -> {
            chars.add(new String(Character.toChars(codePoint)));
            styles.add(style);
            return true;
        });

        StringBuilder rawBuilder = new StringBuilder(chars.size());
        for (String c : chars)
            rawBuilder.append(c);
        String raw = rawBuilder.toString();

        if (!raw.contains(target))
            return orderedText;

        MutableText rebuilt = Text.empty();
        int searchIndex = 0;
        int rawLen = raw.length();

        while (searchIndex < rawLen) {
            int found = raw.indexOf(target, searchIndex);
            if (found == -1) {
                for (int i = searchIndex; i < rawLen; i++) {
                    rebuilt.append(Text.literal(chars.get(i)).setStyle(styles.get(i)));
                }
                break;
            }

            for (int i = searchIndex; i < found; i++) {
                rebuilt.append(Text.literal(chars.get(i)).setStyle(styles.get(i)));
            }

            int targetLen = target.length();
            for (int i = 0; i < targetLen; i++) {
                int charIndex = found + i;
                float ratio = targetLen > 1 ? (float) i / (targetLen - 1) : 0.0f;
                int color = interpolate(colorOne, colorTwo, ratio);

                Style originalStyle = styles.get(charIndex);
                rebuilt.append(Text.literal(chars.get(charIndex)).setStyle(originalStyle.withColor(color)));
            }

            searchIndex = found + targetLen;
        }

        return rebuilt.asOrderedText();
    }

    private static int interpolate(int start, int end, float ratio) {
        int r1 = (start >> 16) & 0xFF;
        int g1 = (start >> 8) & 0xFF;
        int b1 = start & 0xFF;
        int r2 = (end >> 16) & 0xFF;
        int g2 = (end >> 8) & 0xFF;
        int b2 = end & 0xFF;

        int r = (int) (r1 + (r2 - r1) * ratio);
        int g = (int) (g1 + (g2 - g1) * ratio);
        int b = (int) (b1 + (b2 - b1) * ratio);

        return (r << 16) | (g << 8) | b;
    }
}