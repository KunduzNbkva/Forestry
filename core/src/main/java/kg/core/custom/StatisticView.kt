package kg.core.custom

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import kg.core.R
import kotlinx.android.synthetic.main.view_statistic_item.view.*

class StatisticView(context: Context, attributeSet: AttributeSet) :
    ConstraintLayout(context, attributeSet) {
    init {
        LayoutInflater.from(context).inflate(R.layout.view_statistic_item, this, true)

        context.theme.obtainStyledAttributes(attributeSet, R.styleable.StatisticView, 0, 0).apply {
            try {

                tv_fact_value.text = "${getInt(R.styleable.StatisticView_fact, 0)}"


                val pair = when (getInt(R.styleable.StatisticView_status, 0)) {
                    1 -> Pair("#007AFE", "Средне")
                    2 -> Pair("#4CD964", "Хорошо")
                    else -> Pair("#FF0000", "Плохо")
                }

            } finally {
                recycle()
            }
        }
    }

    fun setHeight(height: String){
        tv_fact_value.text = height
    }
}