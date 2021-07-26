package kg.forestry.ui.soil_districts

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kg.core.utils.LocaleManager
import kg.forestry.R
import kg.forestry.localstorage.model.District
import kotlinx.android.synthetic.main.item_list.view.*
import kotlinx.android.synthetic.main.item_multitle_list.view.*
import kotlinx.android.synthetic.main.item_multitle_list.view.tv_text

class DistrictListAdapter(val listener: RegionListClickListener) :
    RecyclerView.Adapter<DistrictListAdapter.Holder>() {

    var items: MutableList<District?> = mutableListOf()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    fun filterList(filteredList: MutableList<District?>) {
        this.items = filteredList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        return Holder.create(parent, listener)
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(items[position], position + 1,holder.itemView.context)
    }

    class Holder(view: View) : RecyclerView.ViewHolder(view) {
        lateinit var region: District
        fun bind(region: District?, index: Int,context: Context) {
            if (region != null)
                this.region = region
            var name = region!!.name_ru
            when (LocaleManager.getLanguagePref(context)){
                LocaleManager.LANGUAGE_KEY_KYRGYZ -> name = region.name_ky
                LocaleManager.LANGUAGE_KEy_ENGLISH -> name = region.name_en
            }

            itemView.tv_text.text = name
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
        fun onItemClick(regionInfo: District?)
    }
}