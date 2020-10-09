package io.github.kn65op.domag.dbtests.common

import androidx.lifecycle.LiveData
import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo

fun <T> expectNoDataInLiveData(ld: LiveData<T>) {
    ld.observeForever {}
    val v = ld.value
    assertThat(v, equalTo(null))
}
