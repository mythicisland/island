package net.mythicisland.common.i18n

import net.kyori.adventure.translation.GlobalTranslator
import org.apache.logging.log4j.LogManager
import java.nio.file.Path
import java.util.Locale

/**
 * Loads the translations of the server.
 *
 * @param directory the directory holding the locales.
 * @param defaultLocale the locale used when a key is missing in the one asked for.
 */
class Translations(directory: Path, defaultLocale: Locale) {

    private val logger = LogManager.getLogger(Translations::class.java)
    private val loader = TranslationLoader(directory)
    private val translator = MessageTranslator(defaultLocale)
    private val cache = CachedTranslator(this.translator)

    /**
     * Reads the translations and registers them.
     */
    fun install() {
        reload()
        GlobalTranslator.translator().addSource(this.cache)
    }

    /**
     * Reads the translations again and replaces the ones that are in use.
     */
    fun reload() {
        val translations = this.loader.load()

        this.translator.load(translations)
        this.cache.invalidate()

        this.logger.info("Loaded {} messages", translations.size)
    }

}
