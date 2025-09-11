package com.ariabagas.storiaapp.ui.customviews

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatEditText
import android.util.Patterns
import com.ariabagas.storiaapp.R
import com.google.android.material.textfield.TextInputLayout


class MyEdtEmail(context: Context, attrs: AttributeSet) : AppCompatEditText(context, attrs) {

    init {
        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(s: Editable?) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val email = s.toString().trim()
                val parentLayout = parent.parent
                if (parentLayout is TextInputLayout) {
                    if (email.isNotEmpty() && !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                        parentLayout.error = context.getString(R.string.invalid_email_format)
                    } else {
                        parentLayout.error = null
                    }
                }
            }
        })
    }
}
