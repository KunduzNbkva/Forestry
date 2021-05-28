package kg.forestry.ui.harvest

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kg.forestry.R
import kg.core.utils.Harvest
import kotlinx.android.synthetic.main.item_multitle_list.view.*

class HarvestListAdapter(val listener: HarvestListClickListener) :
    RecyclerView.Adapter<HarvestListAdapter.Holder>() {

    var items: MutableList<Harvest?> = mutableListOf()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        return Holder.create(parent, listener)
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(items[position], position + 1)
    }

    class Holder(view: View) : RecyclerView.ViewHolder(view) {
        lateinit var harvest: Harvest
        fun bind(harvest: Harvest?, index: Int) {
            if (harvest != null)
                this.harvest = harvest
            itemView.tv_plot_name.text = "$index) ${harvest?.plotName}"
            itemView.tv_text.text = harvest?.date
        }

        companion object {
            fun create(
                parent: ViewGroup,
                listener: HarvestListClickListener
            ): Holder {
                val view =
                    LayoutInflater.from(parent.context).inflate(R.layout.item_multitle_list, parent, false)
                val holder = Holder(view)
                holder.itemView.setOnClickListener { listener.onItemClick(holder.harvest) }
                return holder
            }
        }
    }

    interface HarvestListClickListener {
        fun onItemClick(harvestInfo: Harvest?)
    }
}