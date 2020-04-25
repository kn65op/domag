package com.example.domag2.uitests.common

import android.view.View
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.ViewInteraction
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.Espresso.onView

import androidx.appcompat.widget.AppCompatImageButton

import android.widget.ImageButton
import androidx.appcompat.widget.Toolbar
import androidx.test.espresso.matcher.ViewMatchers.*
import com.example.domag2.R

import org.hamcrest.CoreMatchers.allOf
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.anyOf
import java.util.regex.Matcher


private fun clickOn(view: ViewInteraction) {
    view.perform(click())
}

private fun typeNewTextOn(view: ViewInteraction, text: String) {
    view.perform(
        clearText(),
        typeText(text),
        closeSoftKeyboard()
    )
}

fun clickOnId(id: Int) {
    clickOn(Espresso.onView(ViewMatchers.withId(id)))
}

fun clickOnText(text: String) {
    clickOn(Espresso.onView(ViewMatchers.withText(text)))
}

fun typeNewTextOnId(id: Int, text: String) {
    typeNewTextOn(Espresso.onView(ViewMatchers.withId(id)), text)
}

fun clickHomeIcon() {
    //TODO: To be used in tests
    //onView(androidHomeMatcher()).perform(click())
    onView(withContentDescription(R.string.abc_action_bar_up_description)).perform(click());

}

/*
Possible to be used
fun androidHomeMatcher(): org.hamcrest.Matcher<View>? {
    return allOf(
        withParent(withClassName(`is`(Toolbar::class.java.getName()))),
        withClassName(
            anyOf(
                `is`(ImageButton::class.java.getName()),
                `is`(AppCompatImageButton::class.java.getName())
            )
        )
    )
}*/
