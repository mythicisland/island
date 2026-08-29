package net.mythicisland.common.i18n

import net.kyori.adventure.key.Key
import net.kyori.adventure.text.minimessage.translation.MiniMessageTranslator
import net.kyori.adventure.util.TriState
import java.util.Locale

/**
 * Looks up the MiniMessage string behind a translation key.
 *
 * Adventure builds the component from it and resolves the named arguments that
 * were added with [net.kyori.adventure.text.minimessage.translation.Argument].
 *
 * @property defaultLocale the locale used when a key is missing in the one asked for.
 */
class MessageTranslator(private val defaultLocale: Locale) : MiniMessageTranslator() {

    private val key = Key.key("mythicisland", "messages")

    @Volatile
    private var translations: Map<String, Map<Locale, String>> = emptyMap()

    override fun name(): Key = this.key

    override fun hasAnyTranslations(): TriState = when {
        this.translations.isEmpty() -> TriState.FALSE
        else -> TriState.TRUE
    }

    override fun canTranslate(key: String, locale: Locale): Boolean =
        getMiniMessageString(key, locale) != null

    /**
     * Replaces every translation.
     *
     * @param translations the translations, mapped by key and then by locale.
     */
    fun load(translations: Map<String, Map<Locale, String>>) {
        this.translations = translations
    }

    /**
     * The translation of a key, falling back to the language without a country
     * and then to the default locale of the server.
     *
     * Returning null leaves the key to the client, which is what keeps the
     * vanilla keys working.
     *
     * @param key the translation key.
     * @param locale the locale of the receiver.
     * @return the MiniMessage string, or null if the key is unknown.
     */
    override fun getMiniMessageString(key: String, locale: Locale): String? {
        val byLocale = this.translations[key] ?: return null

        val exact = byLocale[locale]
        if (exact != null) {
            return exact
        }

        val language = byLocale[Locale.of(locale.language)]
        if (language != null) {
            return language
        }

        return byLocale[this.defaultLocale]
    }

}
