package com.armutyus.ninova.ui.settings

import android.content.Context
import android.util.AttributeSet
import androidx.preference.ListPreference
import com.armutyus.ninova.R
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class CustomListPreference : ListPreference {
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) :
            super(context, attrs, defStyleAttr, defStyleRes)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) :
            super(context, attrs, defStyleAttr)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    constructor(context: Context) : super(context)

    override fun onClick() {
        val builder =
            MaterialAlertDialogBuilder(context).setSingleChoiceItems(entries, getValueIndex())
            { dialog, index ->
                if (callChangeListener(entryValues[index].toString())) {
                    setValueIndex(index)
                }
                dialog.dismiss()
            }
                .setNegativeButton(R.string.cancel) { dialog, _ -> dialog.dismiss() }
                .setTitle(title)

        val dialog = builder.create()
        dialog.show()
    }

    private fun getValueIndex() = entryValues.indexOf(value)
}