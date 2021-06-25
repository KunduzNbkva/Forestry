package kg.forestry.ui.plant

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kg.forestry.R
import kg.forestry.localstorage.model.Plant
import kotlinx.android.synthetic.main.item_multitle_list.view.*

class PlantListAdapter(val listener: PlantListClickListener) :
    RecyclerView.Adapter<PlantListAdapter.Holder>() {

    var items: MutableList<Plant?> = mutableListOf()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    var position: Int  = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        return Holder.create(parent, listener)
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(items[position], position + 1)
    }




    class Holder(view: View) : RecyclerView.ViewHolder(view) {
        lateinit var plant: Plant
        fun bind(plant: Plant?, index: Int) {
            when(plant!!.isDraft){
                true->itemView.isDraftTxt.visibility = View.VISIBLE
                false ->itemView.isDraftTxt.visibility = View.GONE
            }
            if (plant != null)
                this.plant = plant
            itemView.tv_plot_name.text = "$index) ${plant?.plotName}"
            itemView.tv_text.text = plant?.date
        }

        companion object {
            fun create(
                parent: ViewGroup,
                listener: PlantListClickListener
            ): Holder {
                val view =
                    LayoutInflater.from(parent.context).inflate(
                        R.layout.item_multitle_list, parent, false
                    )
                val holder = Holder(view)
                holder.itemView.setOnClickListener { listener.onItemClick(holder.plant) }
                return holder
            }
        }
    }

    interface PlantListClickListener {
        fun onItemClick(plantInfo: Plant?)
    }
}