package net.mythicisland.common.config

import org.spongepowered.configurate.serialize.TypeSerializerCollection
import java.io.File

/**
 * Loads and caches a yaml config file.
 *
 * @param E the config type
 */
class ConfigurationFactory<E>(
    private val file: File,
    clazz: Class<E>,
    serializers: TypeSerializerCollection? = null
) {

    private val yamlFileConfigurator = YamlFileConfigurator(clazz, serializers)

    private var config: E? = null

    /**
     * Loads the config, or writes [defaultConfig] to disk and returns it when
     * the file does not exist yet.
     */
    fun loadOrCreate(defaultConfig: E): E {
        if (this.file.exists()) {
            return loadConfiguration()
                ?: throw NullPointerException("failed to load config")
        }

        this.yamlFileConfigurator.save(this.file, defaultConfig)
        this.config = defaultConfig
        return defaultConfig
    }

    /** Returns the cached config, throws when nothing has been loaded yet. */
    fun get(): E {
        return this.config ?: throw NullPointerException("failed to find config")
    }

    /** Writes [entry] to disk and replaces the cached config. */
    fun save(entry: E) {
        this.yamlFileConfigurator.save(this.file, entry)
        this.config = entry
    }

    /** Reads the file and updates the cache. */
    private fun loadConfiguration(): E? {
        val configuration = this.yamlFileConfigurator.load(this.file)
        this.config = configuration
        return configuration
    }

    /** Re-reads the config from disk, discarding the cached one. */
    fun reload() {
        loadConfiguration()
    }
}