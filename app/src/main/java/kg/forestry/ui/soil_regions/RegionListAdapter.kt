package kg.forestry.ui.soil_regions

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kg.forestry.R
import kg.forestry.localstorage.model.Region
import kotlinx.android.synthetic.main.item_multitle_list.view.*

class RegionListAdapter(val listener: RegionListClickListener) :
    RecyclerView.Adapter<RegionListAdapter.Holder>() {

    var items: MutableList<Region?> = mutableListOf()
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
        lateinit var region: Region
        fun bind(region: Region?, index: Int) {
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
        fun onItemClick(regionInfo: Region?)
    }
}