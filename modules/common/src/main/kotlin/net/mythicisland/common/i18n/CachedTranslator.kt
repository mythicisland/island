package net.mythicisland.common.i18n

import com.github.benmanes.caffeine.cache.Cache
import com.github.benmanes.caffeine.cache.Caffeine
import net.kyori.adventure.key.Key
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.TranslatableComponent
import net.kyori.adventure.text.format.Style
import net.kyori.adventure.translation.Translator
import net.kyori.adventure.util.TriState
import java.text.MessageFormat
import java.util.Locale

/**
 * Caches the components of messages without arguments.
 *
 * @property translator the translator doing the actual work.
 * @param maximumSize the number of components kept in memory.
 */
class CachedTranslator(
    private val translator: MessageTranslator,
    maximumSize: Long = 4096,
) : Translator {

    private val components: Cache<Pair<String, Locale>, Component> = Caffeine.newBuilder()
        .maximumSize(maximumSize)
        .recordStats()
        .build()

    override fun name(): Key = this.translator.name()

    override fun hasAnyTranslations(): TriState = this.translator.hasAnyTranslations()

    override fun canTranslate(key: String, locale: Locale): Boolean =
        this.translator.canTranslate(key, locale)

    /** Messages are always built from MiniMessage, never from a message format. */
    override fun translate(key: String, locale: Locale): MessageFormat? = null

    override fun translate(component: TranslatableComponent, locale: Locale): Component? {
        if (!isCacheable(component)) {
            return this.translator.translate(component, locale)
        }

        val cacheKey = component.key() to locale
        val cached = this.components.getIfPresent(cacheKey)
        if (cached != null) {
            return cached
        }

        // Unknown keys stay null, so the client keeps translating them itself.
        val translated = this.translator.translate(component, locale)
        if (translated != null) {
            this.components.put(cacheKey, translated)
        }
        return translated
    }

    /**
     * Drops every cached component, used after the translations were reloaded.
     */
    fun invalidate() {
        this.components.invalidateAll()
    }

    /**
     * Whether the component always renders the same way.
     *
     * Arguments, children and a style of its own make the result depend on the
     * call instead of only on the key and the locale.
     *
     * @param component the component to check.
     * @return true if the rendered component may be cached.
     */
    private fun isCacheable(component: TranslatableComponent): Boolean =
        component.arguments().isEmpty() &&
                component.children().isEmpty() &&
                component.style() == Style.empty() &&
                component.fallback() == null

}
