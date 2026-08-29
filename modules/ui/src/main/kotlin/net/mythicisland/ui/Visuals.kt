package net.mythicisland.ui

import net.kyori.adventure.text.ComponentLike
import net.kyori.adventure.text.TranslatableComponent
import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.translation.GlobalTranslator
import net.mythicisland.common.i18n.Message
import net.mythicisland.ui.layout.VisualAlignment
import net.mythicisland.ui.layout.VisualComponent
import net.mythicisland.ui.layout.VisualLayer
import net.mythicisland.ui.render.UiRenderContext
import net.mythicisland.ui.text.VisualElement
import net.mythicisland.ui.text.VisualSpace
import net.mythicisland.ui.text.VisualText
import net.mythicisland.ui.text.font.VisualFonts
import net.mythicisland.ui.text.font.metrics.MinecraftFontMetrics
import java.util.Locale

object Visuals {

    const val CAP_WIDTH = 3
    const val MIN_BACKGROUND_WIDTH = CAP_WIDTH * 2

    private const val CAP_LEFT_CODE_POINT = 0xE100
    private const val CAP_RIGHT_CODE_POINT = 0xE101
    private const val MIDDLE_BASE_CODE_POINT = 0xE110

    private val middleParts = intArrayOf(128, 64, 32, 16, 8, 4, 2, 1)

    /**
     * An invisible horizontal offset, positive to the right, negative to the left.
     */
    fun space(pixels: Int): VisualText =
        VisualSpace.pixels(pixels)

    /**
     * A MiniMessage text measured with the vanilla font metrics.
     */
    fun text(miniMessage: String): VisualComponent =
        MinecraftFontMetrics.formattedWidth(miniMessage).let { width ->
            VisualComponent(width, advance = width + 1, element = VisualText.raw(miniMessage))
        }

    /**
     * A translated message measured with the vanilla font metrics.
     *
     * A translation key has no width of its own, so the message is resolved for
     * [locale] before it is measured. Build the arguments with
     * [net.kyori.adventure.text.minimessage.translation.Argument], their names
     * are usable as `<name>` inside the translation.
     */
    fun message(message: Message, locale: Locale, vararg arguments: ComponentLike): VisualComponent {
        val translated = GlobalTranslator.render(message.component(*arguments), locale)

        // An unknown key comes back untouched. Showing it keeps the gap visible
        // instead of laying out an empty line.
        return when (translated) {
            is TranslatableComponent -> text(message.key)
            else -> text(MiniMessage.miniMessage().serialize(translated))
        }
    }

    /**
     * A translated message, resolved for the player that is being rendered for.
     */
    fun message(message: Message, context: UiRenderContext, vararg arguments: ComponentLike): VisualComponent =
        message(message, context.locale, *arguments)

    /**
     * A translucent background of exactly [width] pixels, built from the left
     * cap, power of two middle parts and the right cap.
     */
    fun background(width: Int): VisualComponent {
        require(width >= MIN_BACKGROUND_WIDTH) {
            "Background width must be at least $MIN_BACKGROUND_WIDTH pixels."
        }

        val elements = mutableListOf<VisualElement>()
        elements += seamlessGlyph(CAP_LEFT_CODE_POINT)

        var remaining = width - MIN_BACKGROUND_WIDTH
        for (part in middleParts) {
            while (remaining >= part) {
                elements += seamlessGlyph(MIDDLE_BASE_CODE_POINT + Integer.numberOfTrailingZeros(part))
                remaining -= part
            }
        }

        elements += seamlessGlyph(CAP_RIGHT_CODE_POINT)
        return VisualComponent(width, element = VisualText.of(elements))
    }

    /**
     * A translucent panel around [content].
     *
     * Without an explicit [width] the panel sizes itself to the content plus
     * [padding] on both sides. [width] is the total width including both caps.
     */
    fun panel(
        content: VisualComponent,
        width: Int? = null,
        padding: Int = 5,
        align: VisualAlignment = VisualAlignment.CENTER,
    ): VisualComponent {
        require(padding >= 0) { "padding must be zero or positive." }

        val panelWidth = width ?: (content.width + (padding + CAP_WIDTH) * 2)
        val innerStart = CAP_WIDTH + padding
        val innerWidth = (panelWidth - innerStart * 2).coerceAtLeast(0)

        val contentX = innerStart + when {
            content.width >= innerWidth -> 0
            align == VisualAlignment.START -> 0
            align == VisualAlignment.CENTER -> (innerWidth - content.width) / 2
            else -> innerWidth - content.width
        }

        return VisualLayer(width = panelWidth)
            .child(x = 0, background(panelWidth))
            .child(x = contentX, content)
            .toComponent()
    }

    /**
     * A translucent panel around a MiniMessage text, measured with the vanilla
     * font metrics.
     */
    fun panel(
        text: String,
        width: Int? = null,
        padding: Int = 5,
        align: VisualAlignment = VisualAlignment.CENTER,
    ): VisualComponent =
        panel(text(text), width, padding, align)

    /**
     * A background glyph followed by a minus one pixel offset, so consecutive
     * glyphs connect without the one pixel spacing the client adds.
     */
    private fun seamlessGlyph(codePoint: Int): VisualElement =
        VisualText.of(VisualFonts.background.glyph(codePoint), VisualSpace.pixels(-1))
}