package io.github.kn65op.domag.dbtests.common

import androidx.lifecycle.LiveData

fun <T> getFromLiveData(ld: LiveData<T>): T {
    val v = getData(ld)
    if (v != null)
        return v
    throw ValueIsEmptyException()
}

fun <T> assertNoData(ld: LiveData<T>) {
    val v = getData(ld)
    if (v != null)
        throw ValueIsNotEmptyException()
}

private fun <T> getData(ld: LiveData<T>): T? {
    ld.observeForever {}
    return ld.value
}

class ValueIsEmptyException : Throwable()
class ValueIsNotEmptyException : Throwable()
