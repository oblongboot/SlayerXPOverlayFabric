package dev.oblongboot.sxp.mixin;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.text.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import net.minecraft.text.Text;
import dev.oblongboot.sxp.utils.APIUtils;
import java.util.ArrayList;
import java.util.List;

@Mixin(TextRenderer.class)
public class MixinContributerColor {
    @ModifyVariable(
        method = "Lnet/minecraft/client/font/TextRenderer;prepare(Lnet/minecraft/text/OrderedText;FFIZI)Lnet/minecraft/client/font/TextRenderer$GlyphDrawable;",
        at = @At("HEAD"),
        argsOnly = true
    )
    private OrderedText modifyContributerColor(OrderedText text) {
        List<APIUtils.Contributor> contributors = APIUtils.INSTANCE.getContributors();
        for (APIUtils.Contributor c : contributors) {
            int color = Integer.parseInt(c.getColor().substring(1), 16);
            text = replaceContributorWordWithColor(text, c.getName(), color);
        }
        return text;
    }
    
    private static OrderedText replaceContributorWordWithColor(OrderedText orderedText, String target, int color) {
        List<String> chars = new ArrayList<>();
        List<Style> styles = new ArrayList<>();

        orderedText.accept((index, style, codePoint) -> {
            chars.add(new String(Character.toChars(codePoint)));
            styles.add(style);
            return true;
        });
        
        StringBuilder rawBuilder = new StringBuilder(chars.size());
        for (String c : chars) rawBuilder.append(c);
        String raw = rawBuilder.toString();
        
        if (!raw.contains(target)) return orderedText;
        
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

            for (int i = found; i < found + target.length(); i++) {
                Style originalStyle = styles.get(i);
                rebuilt.append(Text.literal(chars.get(i)).setStyle(originalStyle.withColor(color)));
            }
            
            searchIndex = found + target.length();
        }
        
        return rebuilt.asOrderedText();
    }
}