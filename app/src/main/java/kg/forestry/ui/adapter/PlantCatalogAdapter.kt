package kg.forestry.ui.adapter


import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kg.core.utils.LocaleManager
import kg.forestry.R
import kg.forestry.localstorage.model.PlantType
import kotlinx.android.synthetic.main.item_list.view.*

class PlantCatalogAdapter(val isMultiSelect: Boolean, val listener: SimpleListAdapterListener) :
    RecyclerView.Adapter<PlantCatalogAdapter.Holder>() {

    var items: MutableList<Pair<PlantType, Boolean>> = mutableListOf()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        return Holder.create(parent, listener, isMultiSelect)
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(items[position], position, holder.itemView.context)
    }

    class Holder(view: View) : RecyclerView.ViewHolder(view) {
        lateinit var kv:  Pair<PlantType, Boolean>
        var index: Int = 0
        lateinit var value: String

        fun bind(title: Pair<PlantType, Boolean>, index: Int, context: Context) {
            this.kv = title
            this.index = index

            var name = title.first.name_ru
            when (LocaleManager.getLanguagePref(context)){
                LocaleManager.LANGUAGE_KEY_KYRGYZ -> name = title.first.name_ky
                LocaleManager.LANGUAGE_KEy_ENGLISH -> name = title.first.name_en
            }

            this.value = name
            itemView.tv_text.text = name
            updateBackground()
        }

        fun toggleSelectState() {
            updateBackground()
        }

        private fun updateBackground() {
            itemView.setBackgroundColor(if(kv.second) {
                if(kv.first.eatable){
                    Color.GREEN
                }else {
                    Color.LTGRAY
                }
            }else {
                Color.WHITE
            })
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
        fun onItemClick(title: PlantType, position: Int)
    }

    fun filterList(filteredList: MutableList<Pair<PlantType, Boolean>>) {
        this.items = filteredList
        notifyDataSetChanged()
    }
}