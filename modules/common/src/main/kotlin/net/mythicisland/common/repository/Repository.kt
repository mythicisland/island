package net.mythicisland.common.repository

/**
 * Generic repository for storing and retrieving elements.
 *
 * @param I the identifier type
 * @param E the element type
 */
interface Repository<I, E> {

    /** Returns the element with the given [identifier], or `null` if not found. */
    fun find(identifier: I): E?

    /** Returns all stored elements. */
    fun all(): List<E>

    /** Saves [element]. */
    fun save(element: E)

    /** Deletes the element with [identifier], returns `true` if it was present. */
    fun delete(identifier: I): Boolean
}