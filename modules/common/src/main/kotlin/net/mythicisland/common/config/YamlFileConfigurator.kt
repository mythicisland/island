package net.mythicisland.common.config

import net.mythicisland.common.configurate.DurationSerializer
import net.mythicisland.common.configurate.GenericEnumSerializer
import org.spongepowered.configurate.CommentedConfigurationNode
import org.spongepowered.configurate.kotlin.objectMapperFactory
import org.spongepowered.configurate.serialize.TypeSerializerCollection
import org.spongepowered.configurate.yaml.NodeStyle
import org.spongepowered.configurate.yaml.YamlConfigurationLoader
import java.io.File
import java.time.Duration

/**
 * Reads and writes yaml files holding a [clazz] value. One loader is cached
 * per file.
 *
 * @param E the config type
 */
class YamlFileConfigurator<E>(
    private val clazz: Class<E>,
    private val serializers: TypeSerializerCollection? = null
) {

    private val loaders = hashMapOf<File, YamlConfigurationLoader>()

    private val loader = YamlConfigurationLoader.builder()
        .nodeStyle(NodeStyle.BLOCK)
        .defaultOptions { options ->
            options.serializers { builder ->
                builder.registerAnnotatedObjects(objectMapperFactory())
                builder.register(Enum::class.java, GenericEnumSerializer)
                builder.register(Duration::class.java, DurationSerializer)
                builder.registerAll(serializers)
            }
        }

    /** Serializes [entity] into [file], overwriting its content. */
    fun save(file: File, entity: E) {
        val (node, loader) = buildNode(file)
        node.set(this.javaClass, entity)
        loader.save(node)
    }

    /** Reads [file], returns `null` when it holds no value. */
    fun load(file: File): E? {
        val (node, _) = buildNode(file)
        return node.get(this.clazz)
    }

    /** Loads the root node of [file] together with the loader that read it. */
    fun buildNode(file: File): Pair<CommentedConfigurationNode, YamlConfigurationLoader> {
        val loader = getOrCreateConfigurationLoader(file)
        return Pair(loader.load(), loader)
    }

    /** Returns the loader for [file], creating and caching it on first use. */
    fun getOrCreateConfigurationLoader(file: File): YamlConfigurationLoader {
        return this.loaders.getOrPut(file) {
            this.loader
                .path(file.toPath())
                .build()
        }
    }

}