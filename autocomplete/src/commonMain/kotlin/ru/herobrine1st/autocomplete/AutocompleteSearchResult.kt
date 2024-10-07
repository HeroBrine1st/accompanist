package ru.herobrine1st.autocomplete

public sealed interface AutocompleteSearchResult<out T> {
    public data class Ready<T>(val result: List<T>, val query: String) :
        AutocompleteSearchResult<T>

    public data object Error : AutocompleteSearchResult<Nothing>
}