package io.kn65op.domag2.ui.common

import android.app.AlertDialog
import android.content.DialogInterface
import androidx.fragment.app.FragmentActivity
import com.kn65op.domag2.R

fun createDialog(
    activity: FragmentActivity,
    message: Int,
    positiveButton: Int = R.string.ok,
    listener: DialogInterface.OnClickListener = DialogInterface.OnClickListener { _, _ -> }
) {
    val builder: AlertDialog.Builder = AlertDialog.Builder(activity)
    builder.setMessage(message)
    builder.setPositiveButton(positiveButton, listener)
    builder.create().show()
}
