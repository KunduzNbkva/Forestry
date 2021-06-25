package kg.forestry.ui.core

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.inputmethod.EditorInfo
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import kg.core.R
import kg.core.utils.visible
import kotlinx.android.synthetic.main.view_input.view.*

class InputValueView(context: Context, attributeSet: AttributeSet) :
    ConstraintLayout(context, attributeSet) {
    init {
        LayoutInflater.from(context).inflate(R.layout.view_input, this, true)

        context.theme.obtainStyledAttributes(attributeSet, R.styleable.InputValueView, 0, 0).apply {
            try {
                getString(R.styleable.InputValueView_title)?.let {
                    tv_title.text = it
                    tv_title.visible()
                }

                val boolean = getBoolean(R.styleable.InputValueView_removeMargin, false)
                if(boolean) {
                    val set = ConstraintSet()
                    set.clone(cl_root)
                    set.clear(R.id.tv_title, ConstraintSet.START)
                    set.clear(R.id.et_input, ConstraintSet.END)
                    set.applyTo(cl_root)
                }

                et_input.inputType = getInt(R.styleable.InputValueView_android_inputType, EditorInfo.TYPE_CLASS_TEXT)
            } finally {
                recycle()
            }
        }
    }

    fun setValue(string: String) {
        et_input.setText(string)
    }

    fun getValue() = et_input.text.toString()

    fun isValidValue() = getValue().isNotEmpty()

    fun getInput() = et_input
}