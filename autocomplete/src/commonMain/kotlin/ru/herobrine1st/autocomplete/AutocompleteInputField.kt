package ru.herobrine1st.autocomplete

import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.width
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
public fun <T> AutocompleteInputField(
    state: AutocompleteState<T>,
    currentSuggestions: () -> AutocompleteSearchResult<T>,
    modifier: Modifier = Modifier,
    suggestedItem: @Composable (item: T) -> Unit = { item ->
        DropdownMenuItem(
            text = { Text(state.transformToInputText(item)) },
            onClick = { state.selectItem(item) }
        )
    },
    textField: @Composable (Modifier) -> Unit = {
        OutlinedTextField(
            value = state.currentText,
            onValueChange = state::onValueChange,
            trailingIcon = {
                AutocompleteInputFieldDefaults.DefaultTrailingIcon(state, currentSuggestions)
            }
        )
    },
    enabled: Boolean = true,
) {
    val autocompleteExpanded by remember(enabled) {
        derivedStateOf {
            AutocompleteInputFieldDefaults.isExpanded(state, currentSuggestions, enabled)
        }
    }

    // hysteresis: the field is still considered left in intermediate state even after it is focused again
    // or users will be confused as error is removed on click
    LaunchedEffect(state.isFocused, state.currentText, state.selectedItem) {
        if (!state.isFocused && state.currentText.text.isNotBlank() && state.selectedItem == null) {
            state.isLeftInIntermediateState = true
        } else if (state.currentText.text.isBlank() || state.selectedItem != null) {
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
                .menuAnchor(MenuAnchorType.PrimaryEditable)
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
            (currentSuggestions() as? AutocompleteSearchResult.Ready)?.result?.forEach {
                suggestedItem(it)
            }
        }
    }
}
