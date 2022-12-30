package ru.sliva.easychat.text;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.intellij.lang.annotations.RegExp;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public final class TextUtil {

    public static final LegacyComponentSerializer paragraphSerializer = LegacyComponentSerializer.legacySection();
    public static final LegacyComponentSerializer ampersandSerializer = LegacyComponentSerializer.legacyAmpersand();

    public static @NotNull Component color(@NotNull Component component) {
        return ampersandSerializer.deserialize(paragraphSerializer.serialize(component));
    }

    public static @NotNull Component removeSpaces(@NotNull Component component) {
        return replace(component, "^[ \\t]+|[ \\t]+(?=\\s)", "");
    }

    @Contract(pure = true)
    public static @NotNull Component replaceLiteral(@NotNull Component component, String literal, Component replacement) {
        return component.replaceText(builder -> builder.matchLiteral(literal).replacement(replacement));
    }

    public static @NotNull Component replaceLiteralOnce(@NotNull Component component, String literal, Component replacement) {
        return component.replaceText(builder -> builder.matchLiteral(literal).once().replacement(replacement));
    }

    @Contract(pure = true)
    public static @NotNull Component replace(@NotNull Component component, @RegExp String pattern, String replacement) {
        return component.replaceText(builder -> builder.match(pattern).replacement(replacement));
    }

    public static boolean startsWith(@NotNull Component component, @NotNull String prefix) {
        return paragraphSerializer.serialize(component).startsWith(prefix);
    }

}
