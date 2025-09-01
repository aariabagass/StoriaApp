package com.ariabagas.storiaapp.ui.customviews

import com.ariabagas.storiaapp.R
import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatEditText
import com.google.android.material.textfield.TextInputLayout

class MyEdtPassword(context: Context, attrs: AttributeSet) : AppCompatEditText(context, attrs) {

    init {
        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(s: Editable?) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val password = s.toString()
                val parentLayout = parent.parent
                if (parentLayout is TextInputLayout) {
                    if (password.isNotEmpty() && password.length < 8) {
                        parentLayout.error = context.getString(R.string.pass_req)
                    } else {
                        parentLayout.error = null
                    }
                }
            }
        })
    }
}
