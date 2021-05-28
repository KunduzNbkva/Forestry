package kg.core.custom

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import kg.core.R
import kotlinx.android.synthetic.main.view_color_title.view.*


class ColorValueView (context: Context, attributeSet: AttributeSet) :
ConstraintLayout(context, attributeSet) {

    private var normalColor = Color.BLACK

    init {
        LayoutInflater.from(context).inflate(R.layout.view_color_title, this, true)

        context.theme.obtainStyledAttributes(attributeSet, R.styleable.KeyValueView, 0, 0).apply {
            try {
                getResourceId(R.styleable.KeyValueView_color, 0).takeIf { it > 0 }?.let {
                    setupView(ContextCompat.getColor(context, it))
                }

                getString(R.styleable.KeyValueView_value)?.let {
                    tv_title.text = it
                }

            } finally {
                recycle()
            }
        }
    }

    private fun setupView(color: Int) {
        val gd = GradientDrawable()
        gd.setColor(color)
        gd.cornerRadius = 100f
        color_view.setBackgroundDrawable(gd)
    }

    fun setNewTitle(text: String){
        tv_title.text = text
    }


}