package kg.forestry.ui.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kg.forestry.R
import kotlinx.android.synthetic.main.item_list.view.*

class TreeTypeAdapter(val isMultiSelect: Boolean, val listener: SimpleListAdapterListener) :
    RecyclerView.Adapter<TreeTypeAdapter.Holder>() {

    var items: MutableList<Pair<String, Boolean>> = mutableListOf()
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
        lateinit var kv: Pair<String, Boolean>
        var index: Int = 0
        lateinit var value: String

        fun bind(title: Pair<String, Boolean>, index: Int) {
            this.kv = title
            this.index = index
            this.value = title.first
            itemView.tv_text.text = title.first
            updateBackground()
        }

        fun toggleSelectState() {
            updateBackground()
        }

        private fun updateBackground() {
            itemView.setBackgroundColor(if (kv.second) Color.LTGRAY else Color.WHITE)
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
                    listener.onItemClick(holder.kv.first, holder.index)
                }
                return holder
            }
        }
    }

    interface SimpleListAdapterListener {
        fun onItemClick(title: String, position: Int)
    }

    fun filterList(filteredList: MutableList<Pair<String, Boolean>>) {
        this.items = filteredList;
        notifyDataSetChanged();
    }
}
