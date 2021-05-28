package kg.forestry.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kg.forestry.R
import kotlinx.android.synthetic.main.item_list.view.*

class SimpleListAdapter(val isMultiSelect: Boolean, val listener: SimpleListAdapterListener) :
    RecyclerView.Adapter<SimpleListAdapter.Holder>() {

    var items: MutableList<String> = mutableListOf()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        return Holder.create(parent, listener, isMultiSelect)
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(items[position], position)
    }

    class Holder(view: View) : RecyclerView.ViewHolder(view) {
        var index: Int = 0
        lateinit var value: String

        fun bind(title: String, index: Int) {
            this.index = index
            this.value = title
            itemView.tv_text.text = title
            updateBackground()
        }

        fun toggleSelectState() {
            updateBackground()
        }

        private fun updateBackground() {
           // itemView.setBackgroundColor(if (kv.second) Color.LTGRAY else Color.WHITE)
        }

        companion object {
            fun create(
                parent: ViewGroup,
                listener: SimpleListAdapterListener,
                isMultiSelect: Boolean = false
            ): Holder {
                val view =
                    LayoutInflater.from(parent.context).inflate(R.layout.item_list, parent, false)
                val holder = Holder(view)
                holder.itemView.setOnClickListener {
                    if (isMultiSelect) holder.toggleSelectState()
                    listener.onItemClick(holder.value, holder.index)
                }
                return holder
            }
        }
    }

    interface SimpleListAdapterListener {
        fun onItemClick(title: String, position: Int)
    }
}