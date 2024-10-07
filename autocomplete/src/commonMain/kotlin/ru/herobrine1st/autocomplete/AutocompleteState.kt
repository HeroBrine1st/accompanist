package ru.herobrine1st.autocomplete

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue

@Composable
public fun <T> rememberAutocompleteState(
    initialText: AnnotatedString? = null,
    initialItem: T? = null,
    transformToInputText: (T) -> AnnotatedString
): AutocompleteState<T> =
    remember { AutocompleteState(initialItem, initialText, transformToInputText) }

@Composable
public fun <T> rememberAutocompleteState(
    initialText: String? = null,
    initialItem: T? = null,
    transformToInputText: (T) -> AnnotatedString
): AutocompleteState<T> =
    remember { AutocompleteState(initialItem, initialText?.let { AnnotatedString("") }, transformToInputText) }

/**
 * Overriding defaults of both [initialItem] and [initialText] is considered [intermediate state][isLeftInIntermediateState].
 *
 * @param initialItem initially selected item.
 * @param initialText if null, default is used. The default is either empty string ([initialItem] is null)
 * or value given by [transformToInputText].
 * @param transformToInputText A lambda returning string exactly matching given item
 */
public class AutocompleteState<T>(
    initialItem: T? = null,
    initialText: AnnotatedString? = null,
    internal val transformToInputText: (T) -> AnnotatedString,
) {
    public var currentText: TextFieldValue by mutableStateOf(
        TextFieldValue(
            initialText
                ?: if (initialItem != null) transformToInputText(initialItem) else androidx.compose.ui.text.AnnotatedString("")
        )
    )
    public var selectedItem: T? by mutableStateOf(initialItem)
        private set
    public var isLeftInIntermediateState: Boolean by mutableStateOf(initialItem == null && initialText != null)
        internal set
    public var allowDropDownExpand: Boolean by mutableStateOf(false)
        internal set
    public var isFocused: Boolean by mutableStateOf(false)
        internal set

    public fun clear() {
        currentText = currentText.copy(text = "", selection = TextRange.Companion.Zero)
        selectedItem = null
    }

    public val isClear: Boolean get() = currentText.text.isBlank() && selectedItem == null

    public fun onValueChange(it: TextFieldValue) {
        currentText = it
        if (it.text == currentText.text) return
        selectedItem = null
        allowDropDownExpand = true
    }

    public fun selectItem(item: T) {
        val inputText = transformToInputText(item)
        allowDropDownExpand = false
        selectedItem = item
        currentText = currentText.copy(
            annotatedString = inputText,
            selection = TextRange(inputText.length)
        )
    }
}