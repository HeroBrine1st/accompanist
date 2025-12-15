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

package ru.herobrine1st.accompanist

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import ru.herobrine1st.autocomplete.AutocompleteInputField
import ru.herobrine1st.autocomplete.AutocompleteSearchResult
import ru.herobrine1st.autocomplete.rememberAutocompleteState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun App() {
    Scaffold { paddingValues ->
        Column(Modifier.padding(paddingValues).padding(4.dp)) {
            // null as String? because overload resolution ambiguity, will be fixed later™
            val state = rememberAutocompleteState<String>(null as String?) { AnnotatedString(it) }

            AutocompleteInputField(
                state,
                currentSuggestions = {
                    AutocompleteSearchResult.Ready(listOf("Abc", "Def").filter {
                        it.lowercase().startsWith(state.currentText.lowercase())
                    }, state.currentText)
                }
            )
        }
    }
}