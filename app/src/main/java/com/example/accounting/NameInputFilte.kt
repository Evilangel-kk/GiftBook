package com.example.accounting

import android.text.InputFilter
import android.text.Spanned
import android.widget.EditText

class NameInputFilter : InputFilter {
    override fun filter(source: CharSequence, start: Int, end: Int, dest: Spanned, dstart: Int, dend: Int): CharSequence? {
        // 正则表达式: 只允许中文字符
        val regex = "^[\\u4e00-\\u9fa5]*$"
        if (!source.toString().matches(regex.toRegex())) {
            return "" // 过滤掉非英文字符
        }
        return null // 允许输入
    }
}

// 在 Activity 或 Fragment 中设置 EditText
fun setupEditText(editText: EditText) {
    editText.filters = arrayOf(NameInputFilter())
}