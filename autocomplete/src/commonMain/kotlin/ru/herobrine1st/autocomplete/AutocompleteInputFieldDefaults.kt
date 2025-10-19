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

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.stringResource
import ru.herobrine1st.autocomplete.resources.Res
import ru.herobrine1st.autocomplete.resources.autocomplete_error_icon_content_description

public object AutocompleteInputFieldDefaults {
    @Composable
    public fun DefaultTrailingIcon(
        state: AutocompleteState<*>,
        currentSuggestions: () -> AutocompleteSearchResult<*>,
    ) {
        if (state.selectedItem == null) when (val result = currentSuggestions()) {
            is AutocompleteSearchResult.Ready -> if (result.query != state.currentTextValue.text) {
                // 24.dp is size of icon
                // strokeWidth is decreased in proportion (24/40 * 4)
                CircularProgressIndicator(Modifier.size(24.dp), strokeWidth = 2.4.dp)
            }

            is AutocompleteSearchResult.Error -> Icon(
                Icons.Default.Error,
                contentDescription = stringResource(Res.string.autocomplete_error_icon_content_description)
            )
        }
    }

    /**
     * For consistent arrow trailing icons
     */
    public fun isExpanded(
        state: AutocompleteState<*>,
        currentSuggestions: () -> AutocompleteSearchResult<*>,
        enabled: Boolean,
    ): Boolean = state.allowDropDownExpand // master switch
            // if there's something to show, show it
            && (currentSuggestions() as? AutocompleteSearchResult.Ready)?.result?.isNotEmpty() == true
            && state.selectedItem == null // of course if nothing is selected yet
            && state.isFocused // but only if focused
            && enabled // and enabled
}