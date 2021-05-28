package kg.core.custom


import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import kg.core.R
import kg.core.utils.gone
import kg.core.utils.visible
import kotlinx.android.synthetic.main.view_select.view.*

class SelectView(context: Context, attributeSet: AttributeSet) :
    ConstraintLayout(context, attributeSet) {
    init {
        LayoutInflater.from(context).inflate(R.layout.view_select, this, true)

        tv_title.gone()
        tv_text.gone()

        context.theme.obtainStyledAttributes(attributeSet, R.styleable.SelectView, 0, 0).apply {
            try {
                getString(R.styleable.SelectView_title)?.let {
                    tv_title.text = it
                    tv_title.visible()
                }
                getString(R.styleable.SelectView_lable)?.let {
                    tv_text.text = it
                    tv_text.visible()
                }
            } finally {
                recycle()
            }
        }
    }

    fun getValue() = tv_text.text.toString()

    fun isValidValue() = tv_text.text.isNotEmpty()

    fun setValue(value: String) {
        tv_text.text = value
    }
}