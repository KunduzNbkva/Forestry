package kg.core.custom

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.RelativeLayout
import kg.core.R
import kotlinx.android.synthetic.main.view_main_menu_item.view.*

class MainMenuView(context: Context, private val attributeSet: AttributeSet) :
    RelativeLayout(context, attributeSet) {
    init {
        LayoutInflater.from(context).inflate(R.layout.view_main_menu_item, this, true)

        context.theme.obtainStyledAttributes(attributeSet, R.styleable.MainMenuView, 0, 0).apply {
            try {
                getDrawable(R.styleable.MainMenuView_left_icon)?.let {
                    iv_icon.setImageDrawable(it)
                }
                getString(R.styleable.MainMenuView_lable)?.let {
                    tv_text.text = it
                }
            } finally {
                recycle()
            }
        }
    }

    override fun setOnClickListener(l: OnClickListener?) {
        super.setOnClickListener(l)
        card.setOnClickListener(l)
    }
}