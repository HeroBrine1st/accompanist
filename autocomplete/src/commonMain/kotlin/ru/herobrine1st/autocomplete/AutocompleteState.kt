package ru.herobrine1st.autocomplete

import androidx.compose.runtime.*
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue

/**
 * @see AutocompleteState
 */
@Composable
public fun <T> rememberAutocompleteState(
    initialText: String? = null,
    initialItem: T? = null,
    transformToInputText: (T) -> AnnotatedString,
): AutocompleteState<T> =
    remember { AutocompleteState(initialItem, initialText?.let { AnnotatedString("") }, transformToInputText) }

/**
 * @see AutocompleteState
 */
@Composable
public fun <T> rememberAutocompleteState(
    initialText: AnnotatedString? = null,
    initialItem: T? = null,
    transformToInputText: (T) -> AnnotatedString,
): AutocompleteState<T> =
    remember { AutocompleteState(initialItem, initialText, transformToInputText) }

/**
 * A state holder for [AutocompleteInputField].
 *
 * @param initialItem initially selected item. If set, [initialText] defaults to result of [transformToInputText], or else to empty string.
 * @param initialText if null, default is used. If both this parameter and [initialItem] are set and result of [transformToInputText]
 * is not equal to this parameter, the [AutocompleteInputField] is considered to be in [intermediateState][isLeftInIntermediateState].
 * @param transformToInputText A lambda returning string exactly matching given item.
 */
@OptIn(DelicateAutocompleteAPI::class)
public class AutocompleteState<T>(
    initialItem: T? = null,
    initialText: AnnotatedString? = null,
    public val transformToInputText: (T) -> AnnotatedString,
) {
    @set:DelicateAutocompleteAPI
    public var currentTextValue: TextFieldValue by mutableStateOf(
        TextFieldValue(
            initialText ?: if (initialItem != null) transformToInputText(initialItem) else AnnotatedString("")
        )
    )
    public inline val currentText: String get() = currentTextValue.text
    public var selectedItem: T? by mutableStateOf(initialItem)
        private set
    public var isLeftInIntermediateState: Boolean by mutableStateOf(
        initialText == null || (initialItem != null && transformToInputText(initialItem) == initialText)
    )
        internal set
    public var allowDropDownExpand: Boolean by mutableStateOf(false)
        internal set
    public var isFocused: Boolean by mutableStateOf(false)
        internal set

    public fun clear() {
        currentTextValue = currentTextValue.copy(text = "", selection = TextRange.Zero)
        selectedItem = null
    }

    public val isClear: Boolean get() = currentTextValue.text.isBlank() && selectedItem == null

    public fun onValueChange(it: TextFieldValue) {
        val old = currentTextValue
        currentTextValue = it
        if (it.text == old.text) return
        selectedItem = null
        allowDropDownExpand = true
    }

    public fun selectItem(item: T) {
        val inputText = transformToInputText(item)
        allowDropDownExpand = false
        selectedItem = item
        currentTextValue = currentTextValue.copy(
            annotatedString = inputText,
            selection = TextRange(inputText.length)
        )
    }
}