package kg.core.custom

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.RelativeLayout
import androidx.core.view.isVisible
import kg.core.R
import kg.core.utils.gone
import kg.core.utils.setVisible
import kg.core.utils.visible
import kotlinx.android.synthetic.main.view_rounded_select.view.*

class RoundedSelectView(context: Context, attrs: AttributeSet) : RelativeLayout(context, attrs) {
    init {
        LayoutInflater.from(context).inflate(R.layout.view_rounded_select, this, true)
        context.theme.obtainStyledAttributes(attrs, R.styleable.RoundedSelectView, 0, 0)
            .apply {
                try {
                    getColor(R.styleable.RoundedSelectView_backColor, Color.WHITE).let {
                        cv_back.setCardBackgroundColor(it)
                    }

                    getString(R.styleable.RoundedSelectView_lable)?.let {
                        tv_title.text = it
                        tv_title.visible()
                    }

                    getDrawable(R.styleable.RoundedSelectView_mask_image)?.let {
                        iv_mask.setImageDrawable(it)
                        iv_mask.visible()
                    }
                } finally {
                    recycle()
                }
            }
    }

    fun changeSelect() {
        fl_marker.setVisible(!fl_marker.isVisible)
    }

    fun clearSelect() = fl_marker.gone()

    fun isMarked() = fl_marker.isVisible
}