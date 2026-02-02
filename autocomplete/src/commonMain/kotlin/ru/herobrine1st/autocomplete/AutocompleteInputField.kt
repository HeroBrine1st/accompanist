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

import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.width
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.stringResource
import ru.herobrine1st.autocomplete.resources.Res
import ru.herobrine1st.autocomplete.resources.autocomplete_error_intermediate_state

/**
 * [AutocompleteInputField] is a field capable of providing responsive suggestions as user types the text.
 *
 * There is an alternate variant supporting different types for selected and suggested items.
 *
 * @param T type of selected item and shown suggestion, the same as in [state].
 *
 * @param state linked [AutocompleteState] that is used to get current text for [suggestions].
 * @param suggestions a getter for current state of suggestion search, should react to changes in [currentTextValue][AutocompleteState.currentTextValue] or [currentText][AutocompleteState.currentText].
 * @param modifier Modifier
 * @param enabled true if enabled, set false to disable dropdown [ExposedDropdownMenuBox] interactions. Note that [textField] does not get this parameter, though default value uses it directly.,
 * @param anchorType passed through to menuAnchor modifier, which in turn is passed to [textField]. Set null to disable.
 * @param suggestedItem usually a [DropdownMenuItem] showing [suggested item][T] from [search results][suggestions].
 * @param textField a widget that must use provided modifier directly on text field. Must also bind [AutocompleteState.currentTextValue] and [AutocompleteState.onValueChange] as a required minimum.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
public fun <T> AutocompleteInputField(
    state: AutocompleteState<T>,
    suggestions: () -> AutocompleteSearchResult<T>,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    anchorType: ExposedDropdownMenuAnchorType? = ExposedDropdownMenuAnchorType.PrimaryEditable,
    suggestedItem: @Composable (item: T) -> Unit = { item ->
        DropdownMenuItem(
            text = { Text(state.transformToInputText(item)) },
            onClick = { state.selectItem(item) }
        )
    },
    textField: @Composable ExposedDropdownMenuBoxScope.(Modifier) -> Unit = {
        OutlinedTextField(
            value = state.currentTextValue,
            onValueChange = state::onValueChange,
            modifier = it,
            trailingIcon = { AutocompleteInputFieldDefaults.DefaultTrailingIcon(state, enabled, true, suggestions) },
            isError = state.isLeftInIntermediateState,
            supportingText = { if (state.isLeftInIntermediateState) Text(stringResource(Res.string.autocomplete_error_intermediate_state)) },
            enabled = enabled,
        )
    },
): Unit = AutocompleteInputField(
    state = state,
    suggestions = suggestions,
    transformToSelectedItem = @Suppress("UNCHECKED_CAST") (Identity as (T) -> T),
    modifier = modifier,
    enabled = enabled,
    anchorType = anchorType,
    suggestedItem = suggestedItem,
    textField = textField
)

/**
 * [AutocompleteInputField] is a field capable of providing responsive suggestions as user types the text.
 *
 * There is an alternate variant without [transformToSelectedItem] parameter for cases when [T] is the same as [R].
 *
 * @param T type of selected item, the same as in [state].
 * @param R type of proposed item, usually the same as [T], but it is possible to provide rich suggestions and then [transform item to state type][transformToSelectedItem].
 *
 * @param state linked [AutocompleteState] that is used to get current text for [suggestions].
 * @param suggestions a getter for current state of suggestion search, should react to changes in [currentTextValue][AutocompleteState.currentTextValue] or [currentText][AutocompleteState.currentText].
 * @param transformToSelectedItem a transformer of suggestion item shown by [suggestedItem] to a selected item used by [state].
 * @param modifier Modifier
 * @param enabled true if enabled, set false to disable dropdown [ExposedDropdownMenuBox] interactions. Note that [textField] does not get this parameter, though default value uses it directly.,
 * @param anchorType passed through to menuAnchor modifier, which in turn is passed to [textField]. Set null to disable.
 * @param suggestedItem usually a [DropdownMenuItem] showing [suggested item][R] from [search results][suggestions].
 * @param textField a widget that must use provided modifier directly on text field. Must also bind [AutocompleteState.currentTextValue] and [AutocompleteState.onValueChange] as a required minimum.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
public fun <T, R> AutocompleteInputField(
    state: AutocompleteState<T>,
    suggestions: () -> AutocompleteSearchResult<R>,
    transformToSelectedItem: (R) -> T,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    anchorType: ExposedDropdownMenuAnchorType? = ExposedDropdownMenuAnchorType.PrimaryEditable,
    suggestedItem: @Composable (item: R) -> Unit = { item ->
        DropdownMenuItem(
            text = { Text(state.transformToInputText(transformToSelectedItem(item))) },
            onClick = { state.selectItem(transformToSelectedItem(item)) }
        )
    },
    textField: @Composable ExposedDropdownMenuBoxScope.(Modifier) -> Unit = {
        OutlinedTextField(
            value = state.currentTextValue,
            onValueChange = state::onValueChange,
            modifier = it,
            trailingIcon = { AutocompleteInputFieldDefaults.DefaultTrailingIcon(state, enabled, true, suggestions) },
            isError = state.isLeftInIntermediateState,
            supportingText = { if (state.isLeftInIntermediateState) Text(stringResource(Res.string.autocomplete_error_intermediate_state)) },
            enabled = enabled,
        )
    },
) {
    val autocompleteExpanded by remember(enabled) {
        derivedStateOf {
            AutocompleteInputFieldDefaults.isExpanded(state, enabled, suggestions)
        }
    }

    // hysteresis: the field is still considered left in intermediate state even after it is focused again
    // or users will be confused as error is removed on click
    LaunchedEffect(state.isFocused, state.currentTextValue, state.selectedItem) {
        if (!state.isFocused && state.currentTextValue.text.isNotBlank() && state.selectedItem == null) {
            state.isLeftInIntermediateState = true
        } else if (state.currentTextValue.text.isBlank() || state.selectedItem != null) {
            state.isLeftInIntermediateState = false
        }
    }

    ExposedDropdownMenuBox(
        expanded = autocompleteExpanded,
        onExpandedChange = { if (enabled) state.allowDropDownExpand = !autocompleteExpanded },
        modifier = modifier.width(IntrinsicSize.Max)
    ) {
        textField(
            Modifier
                .run { anchorType?.let { menuAnchor(it) } ?: this }
                .onFocusChanged {
                    state.isFocused = it.isFocused
                }
        )
        ExposedDropdownMenu(
            expanded = autocompleteExpanded,
            onDismissRequest = { state.allowDropDownExpand = false },
            modifier = Modifier
                .heightIn(max = (48 * 3).dp)
        ) {
            (suggestions() as? AutocompleteSearchResult.Ready)?.suggestions?.forEach {
                suggestedItem(it)
            }
        }
    }
}

private val Identity: (Any) -> Any = { it }