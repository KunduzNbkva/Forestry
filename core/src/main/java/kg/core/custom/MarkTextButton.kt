package kg.core.custom

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import kg.core.R
import kotlinx.android.synthetic.main.view_mark_text_button.view.*

class MarkTextButton(context: Context, attributeSet: AttributeSet) :
    ConstraintLayout(context, attributeSet) {

    private var isChecked = false

    init {
        LayoutInflater.from(context).inflate(R.layout.view_mark_text_button, this, true)

        context.theme.obtainStyledAttributes(attributeSet, R.styleable.MarkTextButton, 0, 0).apply {
            try {
                getString(R.styleable.MarkTextButton_title)?.let {
                    tv_text.text = it
                }
                getInt(R.styleable.MarkTextButton_custom_width, 0).takeIf { it > 0 }?.let {
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

    fun getLable() = tv_text.text.toString()

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
        tv_text.setTextColor(ContextCompat.getColor(context, R.color.colorPrimary))
    }

    private fun setupAsChecked() {
        card.setCardBackgroundColor(ContextCompat.getColor(context, R.color.colorPrimary))
        tv_text.setTextColor(Color.WHITE)
    }

    private fun setupAsDisabled() {
        card.setCardBackgroundColor(Color.GRAY)
        tv_text.setTextColor(Color.WHITE)
    }

    override fun setEnabled(enabled: Boolean) {
        super.setEnabled(enabled)
        if(!enabled) {
            setChecked(false)
            setupAsDisabled()
        } else {
            updateChecked()
        }
    }
}