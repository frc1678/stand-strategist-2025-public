package org.citruscircuits.standstrategist.util

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle

/**
 * Applies the bold font style to the text in the given [block] in this [AnnotatedString.Builder].
 */
fun AnnotatedString.Builder.bold(block: AnnotatedString.Builder.() -> Unit) =
    withStyle(
        style = SpanStyle(fontWeight = FontWeight.Bold),
        block = block
    )
