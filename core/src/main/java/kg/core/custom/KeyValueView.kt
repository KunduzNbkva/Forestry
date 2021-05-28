package kg.core.custom

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import kg.core.R
import kg.core.utils.setVisible
import kotlinx.android.synthetic.main.view_key_value.view.*

class KeyValueView(context: Context, attributeSet: AttributeSet) :
    ConstraintLayout(context, attributeSet) {

    init {
        LayoutInflater.from(context).inflate(R.layout.view_key_value, this, true)

        context.theme.obtainStyledAttributes(attributeSet, R.styleable.KeyValueView, 0, 0).apply {
            try {
                getString(R.styleable.KeyValueView_key)?.let {
                    tv_key.text = it
                }
                getString(R.styleable.KeyValueView_value)?.let {
                    tv_value.setText(it)
                }

                val visible = getBoolean(R.styleable.KeyValueView_with_arrow, false)
                ic_chevron.setVisible(visible)

            } finally {
                recycle()
            }
        }
    }

    fun disableInput(){
        tv_value.isFocusable = false
        tv_value.isEnabled = false
    }

    fun setValue(value: String?) {
        tv_value.setText(value)
    }

    fun getValue() = tv_value.text.toString()

}