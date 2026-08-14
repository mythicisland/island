package net.mythicisland.ui.text.font.metrics

import it.unimi.dsi.fastutil.chars.Char2IntOpenHashMap
import net.mythicisland.ui.text.font.VisualFontMetrics

/**
 * Metrics of the vanilla `minecraft:default` font. Every glyph is five pixels
 */
object MinecraftFontMetrics : VisualFontMetrics {

    private val widths = Char2IntOpenHashMap().apply {
        defaultReturnValue(5)

        put(' ', 3)
        put('!', 1)
        put('"', 3)
        put('\'', 1)
        put('(', 3)
        put(')', 3)
        put('*', 3)
        put(',', 1)
        put('.', 1)
        put(':', 1)
        put(';', 1)
        put('<', 4)
        put('>', 4)
        put('@', 6)
        put('I', 3)
        put('[', 3)
        put(']', 3)
        put('`', 2)
        put('f', 4)
        put('i', 1)
        put('k', 4)
        put('l', 2)
        put('t', 3)
        put('{', 3)
        put('|', 1)
        put('}', 3)
        put('~', 6)
        put('ɪ', 3)
    }

    override fun charWidth(char: Char): Int =
        widths.get(char)
}