package kg.forestry.ui.plant_type

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kg.forestry.R
import kg.core.utils.Helper
import kg.core.utils.gone
import kg.core.utils.setVisible
import kg.forestry.localstorage.model.PlantType
import kg.forestry.ui.adapter.PlantCatalogAdapter
import kotlinx.android.synthetic.main.item_image_list.view.*


class SimpleImageGridAdapter(
    val isMultiSelect: Boolean,
    val listener: PlantCatalogAdapter.SimpleListAdapterListener
) :
    RecyclerView.Adapter<SimpleImageGridAdapter.Holder>() {

    var items: MutableList<Pair<PlantType, Boolean>> = mutableListOf()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        return Holder.create(parent, listener, isMultiSelect)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(items[position], position)
    }


    class Holder(view: View) : RecyclerView.ViewHolder(view) {
        lateinit var keyValue: PlantType

        lateinit var value: String
        var isSelected: Boolean = false
        var index = 0

        fun bind(keyValue: Pair<PlantType, Boolean>, index: Int) {
            this.keyValue = keyValue.first
            this.index = index
            this.value = keyValue.first.imgBase64
            isSelected = keyValue.second

            if (value.isNotEmpty()) {
                Helper.setExistImage(itemView.imageView3,value)
            }else{
                itemView.progressBar.gone()
                itemView.imageView3.setImageResource(
                    R.drawable.ic_photo_black_24dp)
            }
            updateBackground()
        }

        fun toggleSelectState() {
            isSelected = !isSelected
            updateBackground()
        }

        fun updateBackground() {
            itemView.iv_check.setVisible(isSelected)
        }


        companion object {
            fun create(
                parent: ViewGroup,
                listener: PlantCatalogAdapter.SimpleListAdapterListener,
                isMultiSelect: Boolean = false
            ): Holder {
                val view =
                    LayoutInflater.from(parent.context)
                        .inflate(R.layout.item_image_list, parent, false)
                val holder = Holder(view)
                holder.itemView.setOnClickListener {
                    if (isMultiSelect) holder.toggleSelectState()
                    listener.onItemClick(holder.keyValue, holder.index)
                }
                return holder
            }
        }
    }
}
