package io.github.kn65op.domag.ui.dialogs

import android.util.Log
import androidx.fragment.app.FragmentActivity
import io.github.kn65op.android.lib.type.FixedPointNumber
import io.github.kn65op.domag.database.database.DatabaseFactoryImpl
import io.github.kn65op.domag.database.entities.Category

class ConsumeDialogController(private val activity: FragmentActivity) {
    private val dbFactory = DatabaseFactoryImpl()

    fun startConsumeDialog(
        fullName: String,
        category: Category,
        currentAmount : FixedPointNumber,
    ) {
        val dialog = ConsumeItemDialog(
            fullName,
            category.unit,
            currentAmount,
            object : ConsumeItemDialog.ConsumeItemDialogListener {
                override fun onConsume(amount: FixedPointNumber) {
                    Log.i(LOG_TAG, "Will consume")
                    //val consumeDao = dbFactory.factory.createDatabase(activity.applicationContext).consumeDao()
                    //consumeDao.consume(item, amount)
                    Log.i(LOG_TAG, "Consumed $fullName: $amount")
                }
            })
        dialog.show(activity.supportFragmentManager, "ConsumeDialog")
    }

    companion object {
        private const val LOG_TAG = "ConsumeDialogController"
    }
}