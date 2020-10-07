package io.github.kn65op.domag.ui.items

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import io.github.kn65op.android.lib.type.FixedPointNumber
import io.github.kn65op.domag.R

class ConsumeItemDialog(
    private val item: String,
    private val unit: String,
    private val listener: ConsumeItemDialogListener
) : DialogFragment() {
    interface ConsumeItemDialogListener {
        fun onConsume(amount: FixedPointNumber)
    }

    lateinit var amountField: EditText

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val inflater = requireActivity().layoutInflater

        val builder = AlertDialog.Builder(requireContext())
        val root = inflater.inflate(R.layout.consume_dialog, null)
        amountField = root.findViewById(R.id.consume_dialog_amount_field)
        val title: TextView = root.findViewById(R.id.consume_dialog_description)
        val text = getTitleText()
        Log.i(LOG_TAG, text)
        Log.i(LOG_TAG, "TUTEJ ${getTitleText()}")
        Log.i(LOG_TAG,
        requireActivity().getString(R.string.consume_dialog_title, item)
        )
        Log.i(LOG_TAG,
            requireActivity().getString(R.string.consume_dialog_title_with_unit, item, unit)
        )
        title.text = text

        builder.setView(root)
            .setPositiveButton("Eloszka") { _, _ ->
                Log.i(LOG_TAG, " Positive")
                listener.onConsume(FixedPointNumber(amountField.text.toString().toDouble()))
            }.setNegativeButton("Not eloszka") { dialog, _ ->
                dialog.cancel()
                Log.i(LOG_TAG, " Neg")
            }
        return builder.create()
    }

    private fun getTitleText() =
        when (unit) {
            "" ->
                requireActivity().getString(R.string.consume_dialog_title, item)
            else ->
                requireActivity().getString(R.string.consume_dialog_title_with_unit, item, unit)
        }

    companion object {
        private const val LOG_TAG = "ConsumeItemDialog"
    }
}