package io.github.kn65op.domag.dbtests.common

import androidx.lifecycle.LiveData

fun <T> getFromLiveData(ld: LiveData<T>): T {
    ld.observeForever {}
    val v = ld.value
    if (v != null)
        return v
    throw ValueIsEmptyException();
}

class ValueIsEmptyException : Throwable() {

}
