package net.mythicisland.ui.text

import it.unimi.dsi.fastutil.chars.Char2CharOpenHashMap

/**
 * Converts letters to their small caps variants, which the
 * vanilla font renders as small uppercase letters.
 */
object SmallCaps {

    private const val SMALL_CAPS = "ᴀʙᴄᴅᴇꜰɢʜɪᴊᴋʟᴍɴᴏᴘǫʀꜱᴛᴜᴠᴡxʏᴢ"

    private val mapping = Char2CharOpenHashMap().apply {
        SMALL_CAPS.forEachIndexed { index, smallCap ->
            put('a' + index, smallCap)
            put('A' + index, smallCap)
        }
    }

    fun convert(char: Char): Char =
        mapping.getOrDefault(char, char)


    fun convert(content: String): String =
        buildString(content.length) {
            var inTag = false

            for (char in content) {
                when (char) {
                    '<' -> inTag = true
                    '>' if inTag -> inTag = false
                }

                append(if (inTag) char else convert(char))
            }
        }
}