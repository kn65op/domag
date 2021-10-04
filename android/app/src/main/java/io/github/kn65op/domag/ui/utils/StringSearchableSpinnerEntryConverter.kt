package io.github.kn65op.domag.ui.utils

import io.github.kn65op.android.lib.gui.searchablespinner.SearchableSpinnerEntryConverter

class StringSearchableSpinnerEntryConverter : SearchableSpinnerEntryConverter<String> {
    override fun toText(t: String): String = t
}