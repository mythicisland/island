package net.mythicisland.ui.text

import net.mythicisland.ui.text.font.VisualFonts
import kotlin.math.abs

/**
 * Pixel precise horizontal offsets.
 *
 * Positive amounts move the cursor to the right, negative amounts to the left.
 * Any amount can be represented because the font provides advances for every
 * power of two from one to 1024 in both directions.
 */
object VisualSpace {

    private val advances = intArrayOf(1024, 512, 256, 128, 64, 32, 16, 8, 4, 2, 1)
    private val positiveCodePoints = intArrayOf(0xF82F, 0xF82E, 0xF82D, 0xF82C, 0xF82B, 0xF82A, 0xF829, 0xF828, 0xF824, 0xF822, 0xF821)
    private val negativeCodePoints = intArrayOf(0xF80F, 0xF80E, 0xF80D, 0xF80C, 0xF80B, 0xF80A, 0xF809, 0xF808, 0xF804, 0xF802, 0xF801)

    fun pixels(amount: Int): VisualText {
        if (amount == 0) {
            return VisualText.empty
        }

        val codePoints = if (amount > 0) positiveCodePoints else negativeCodePoints
        var remaining = abs(amount)
        val raw = StringBuilder()

        advances.forEachIndexed { index, advance ->
            while (remaining >= advance) {
                raw.appendCodePoint(codePoints[index])
                remaining -= advance
            }
        }

        return VisualFonts.space.text(raw.toString(), shadow = true)
    }
}