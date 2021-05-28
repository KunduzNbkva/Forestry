package kg.core.custom

import kg.core.R
import kg.core.utils.gone
import kg.core.utils.visible
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import kotlinx.android.synthetic.main.view_select.view.*

class MultiLineTextView(context: Context, attributeSet: AttributeSet) :
    ConstraintLayout(context, attributeSet) {
    init {
        LayoutInflater.from(context).inflate(R.layout.view_select, this, true)

        imageView4.gone()
        tv_title.gone()
        tv_text.gone()

        context.theme.obtainStyledAttributes(attributeSet, R.styleable.SimpleTextView, 0, 0).apply {
            try {
                getString(R.styleable.SimpleTextView_title)?.let {
                    tv_title.text = it
                    tv_title.visible()
                }
                getString(R.styleable.SimpleTextView_lable)?.let {
                    tv_text.text = it
                    tv_text.visible()
                }
            } finally {
                recycle()
            }
        }
    }

    fun setTitle(title:String){
        tv_text.text = title
        tv_text.visible()
    }
}