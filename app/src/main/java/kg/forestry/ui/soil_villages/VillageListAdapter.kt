package kg.forestry.ui.soil_villages

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kg.forestry.R
import kg.forestry.localstorage.model.Village
import kotlinx.android.synthetic.main.item_multitle_list.view.*

class VillageListAdapter(val listener: RegionListClickListener) :
    RecyclerView.Adapter<VillageListAdapter.Holder>() {

    var items: MutableList<Village?> = mutableListOf()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    fun filterList(filteredList: MutableList<Village?>) {
        this.items = filteredList;
        notifyDataSetChanged();
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        return Holder.create(parent, listener)
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(items[position], position + 1)
    }

    class Holder(view: View) : RecyclerView.ViewHolder(view) {
        lateinit var region: Village
        fun bind(region: Village?, index: Int) {
            if (region != null)
                this.region = region
            itemView.tv_text.text = region!!.name
        }

        companion object {
            fun create(
                parent: ViewGroup,
                listener: RegionListClickListener
            ): Holder {
                val view =
                    LayoutInflater.from(parent.context).inflate(R.layout.item_list, parent, false)
                val holder = Holder(view)
                holder.itemView.setOnClickListener { listener.onItemClick(holder.region) }
                return holder
            }
        }
    }

    interface RegionListClickListener {
        fun onItemClick(regionInfo: Village?)
    }
}