package net.mythicisland.common.i18n

import net.kyori.adventure.translation.Translator
import org.apache.logging.log4j.LogManager
import org.spongepowered.configurate.ConfigurateException
import org.spongepowered.configurate.ConfigurationNode
import org.spongepowered.configurate.yaml.YamlConfigurationLoader
import java.nio.file.Path
import java.util.Locale
import kotlin.io.path.isDirectory
import kotlin.io.path.listDirectoryEntries
import kotlin.io.path.name
import kotlin.io.path.nameWithoutExtension

/**
 * Reads translations from a directory.
 *
 * @property directory the directory holding the locales.
 */
class TranslationLoader(private val directory: Path) {

    private val logger = LogManager.getLogger(TranslationLoader::class.java)

    /**
     * Reads every locale of the directory.
     *
     * @return the translations, mapped by key and then by locale.
     */
    fun load(): Map<String, Map<Locale, String>> {
        if (!this.directory.isDirectory()) {
            this.logger.warn("No translations were loaded, {} does not exist", this.directory)
            return emptyMap()
        }

        val translations = mutableMapOf<String, MutableMap<Locale, String>>()
        for (path in this.directory.listDirectoryEntries()) {
            if (!path.isDirectory()) {
                continue
            }

            val locale = Translator.parseLocale(path.name)
            if (locale == null) {
                this.logger.warn("Skipping {}, its name is not a locale", path.name)
                continue
            }

            for ((key, value) in loadLocale(path)) {
                translations.getOrPut(key) { mutableMapOf() }[locale] = value
            }
        }
        return translations
    }

    /**
     * Reads every namespace of a single locale.
     *
     * @param directory the directory of the locale.
     * @return the translations of the locale, mapped by key.
     */
    private fun loadLocale(directory: Path): Map<String, String> {
        val translations = mutableMapOf<String, String>()
        for (file in directory.listDirectoryEntries("*.yml")) {
            val node = loadFile(file) ?: continue
            flatten(file.nameWithoutExtension, node, translations)
        }
        return translations
    }

    /**
     * Reads a single file.
     *
     * @param file the file to read.
     * @return the root node, or null when the file could not be parsed.
     */
    private fun loadFile(file: Path): ConfigurationNode? {
        return try {
            YamlConfigurationLoader.builder()
                .path(file)
                .build()
                .load()
        } catch (exception: ConfigurateException) {
            this.logger.error("Could not read {}, make sure it is correctly formatted", file, exception)
            null
        }
    }

    /**
     * Turns a nested node into dotted keys. Lists become a single text joined by
     * line breaks, so tooltips can be written line by line.
     *
     * @param prefix the key of the node.
     * @param node the node to flatten.
     * @param translations the map the entries are written into.
     */
    private fun flatten(prefix: String, node: ConfigurationNode, translations: MutableMap<String, String>) {
        when {
            node.isMap -> for ((key, child) in node.childrenMap()) {
                flatten("$prefix.$key", child, translations)
            }

            node.isList -> translations[prefix] = node.childrenList()
                .joinToString("<newline>") { child -> child.getString("") }

            else -> {
                val value = node.string
                if (value != null) {
                    translations[prefix] = value
                }
            }
        }
    }

}
