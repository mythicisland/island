package net.mythicisland.core.command

data class CommandCategory(
    val order: Int,
    val name: String
) {
    companion object {
        // Will not be shown in the help command
        val HIDDEN: CommandCategory? = null
        // Default category
        val DEFAULT: CommandCategory = CommandCategory(Int.MAX_VALUE, "General")
    }
}