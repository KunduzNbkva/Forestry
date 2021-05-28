package kg.core.custom

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import kg.core.R
import kotlinx.android.synthetic.main.view_mark_text_image_button.view.*

class MarkImageTextButton(context: Context, attributeSet: AttributeSet) :
    ConstraintLayout(context, attributeSet) {

    private var isChecked = false
    private var normalColor = Color.BLACK

    init {
        LayoutInflater.from(context).inflate(R.layout.view_mark_text_image_button, this, true)

        context.theme.obtainStyledAttributes(attributeSet, R.styleable.MarkImageButton, 0, 0)
            .apply {
                try {
                    getResourceId(R.styleable.MarkImageButton_android_src, 0).takeIf { it > 0 }?.let {
                        image.setImageDrawable(ContextCompat.getDrawable(context, it))
                    }

                    getResourceId(R.styleable.MarkImageButton_color, 0).takeIf { it > 0 }?.let {
                        normalColor = ContextCompat.getColor(context, it)
                    }

                    getString(R.styleable.MarkImageButton_lable)?.let {
                        tv_name.text = it
                    }

                    getInt(R.styleable.MarkImageButton_custom_width, 0).takeIf { it > 0 }?.let {
                        card.layoutParams = card.layoutParams.apply {
                            width = it
                        }
                    }
                } finally {
                    recycle()
                }
            }
        updateChecked()
    }

    fun setChecked(isChecked: Boolean) {
        this.isChecked = isChecked
        updateChecked()
    }

    fun getChecked() = isChecked

    private fun updateChecked() {
        if (isChecked) setupAsChecked()
        else setupAsNormal()
    }

    private fun setupAsNormal() {
        card.setCardBackgroundColor(Color.WHITE)
        image.setColorFilter(normalColor)
        tv_name.setTextColor(normalColor)
    }

    private fun setupAsChecked() {
        card.setCardBackgroundColor(ContextCompat.getColor(context, R.color.colorPrimary))
        image.setColorFilter(Color.WHITE)
        tv_name.setTextColor(Color.WHITE)
    }
}