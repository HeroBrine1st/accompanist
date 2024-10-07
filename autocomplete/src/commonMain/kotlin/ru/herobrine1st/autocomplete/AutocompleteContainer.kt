package ru.herobrine1st.autocomplete

/**
 * This container is useful for "Create new" functionality of an autocomplete field
 */
public sealed interface AutocompleteContainer<T> {
    public data class Object<T>(val value: T) : AutocompleteContainer<T>

    public data class CreateNew<T>(val query: String) : AutocompleteContainer<T>
}