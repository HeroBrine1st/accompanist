/*
 * ru.herobrine1st.accompanist is a set of utility libraries for Jetpack Compose
 * Copyright (C) 2025 HeroBrine1st Erquilenne <accompanist@herobrine1st.ru>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package ru.herobrine1st.autocomplete

public sealed interface AutocompleteSearchResult<out T> {
    /**
     * A holder for a query and results for it.
     *
     * It is created like this:
     *
     * ```
     * snapshotFlow { state.currentText }
     *   .map { query ->
     *     val cleanedQuery = query.lowercase().trim()
     *     val results = api.suggestSomethingFor(cleanedQuery)
     *     AutocompleteSearchResult(results, query) // Note query is not cleaned here
     *   }
     * ```
     *
     * Do not forget to catch exceptions and emit [Error] object in that case.
     *
     * [query] parameter must be exactly the source of actual query used in search as it is used to determine if current result is
     * for current text field value or not, which determines visibility of loading indicator. As such, there's no need to set [query] to null
     * when suggestions are updating, and doing that is not recommended.
     *
     * Setting [query] to null is allowed for a single case when suggestions are believed to be available *soon* without user interaction.
     * Note that suggestions are not shown without user interaction as field should be focused.
     *
     * Due to ease of misuse, null [query] is considered [delicate API][DelicateAutocompleteAPI]. Consider using empty string instead
     * as it is a valid value for [query] when no suggestions are available for empty query.
     *
     * It is possible to provide suggestions while they are still "loading", for example, combining API result flow with
     * the same snapshotFlow used to get query allows to do a fast local search through known suggestions. In such case,
     * [query] must not be changed and should still refer to initial user query. This is also applicable for null [query].
     *
     * @param suggestions list of suggested selections for user to choose from
     * @param query exactly the [value of text field][AutocompleteState.currentText] when suggestion search is started, or null to mark as available *soon*.
     */
    public data class Ready<T> @DelicateAutocompleteAPI constructor(
        val suggestions: List<T>,
        val query: String?,
    ) : AutocompleteSearchResult<T> {
        @OptIn(DelicateAutocompleteAPI::class)
        public constructor(suggestions: List<T>, query: String): this(suggestions, query as String?)
    }

    /**
     * A simple error object state. Can be emitted in e.g. retryWhen.
     */
    public data object Error : AutocompleteSearchResult<Nothing>

    public companion object {
        /**
         * Default value: empty query usually means no suggestions. If doesn't fit (e.g. there could be suggestions for empty
         * query), use `Ready(emptyList(), null)`
         */
        public val Empty: Ready<Nothing> = Ready(emptyList(), "")
    }
}