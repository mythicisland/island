package net.mythicisland.common.repository

/** A [Repository] that can load its elements from a data source. */
interface LoadableRepository<I, E> : Repository<I, E> {

    /** Loads all elements from the data source and returns them. */
    fun load(): List<E>

}