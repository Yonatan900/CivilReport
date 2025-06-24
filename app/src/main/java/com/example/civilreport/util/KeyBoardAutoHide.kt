package com.example.civilreport.util

import android.annotation.SuppressLint
import android.content.Context
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText


@SuppressLint("ClickableViewAccessibility")
fun View.installKeyboardAutoHide() {
    setOnTouchListener { _, event ->
        if (event.action == MotionEvent.ACTION_DOWN) {
            val focused = rootView.findFocus()
            if (focused is EditText && !focused.isTouchInside(event)) {
                focused.clearFocus()
                val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE)
                        as InputMethodManager
                imm.hideSoftInputFromWindow(windowToken, 0)
            }
        }
        false // Return false to allow other touch events to be processed
    }
}

private fun View.isTouchInside(ev: MotionEvent): Boolean {
    val loc = IntArray(2)
    getLocationOnScreen(loc)
    val x = ev.rawX
    val y = ev.rawY
    return x >= loc[0] && x <= loc[0] + width &&
            y >= loc[1] && y <= loc[1] + height
}
