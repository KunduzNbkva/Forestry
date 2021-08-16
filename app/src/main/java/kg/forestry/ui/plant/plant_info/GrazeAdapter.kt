package kg.forestry.ui.plant.plant_info

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kg.forestry.R

class GrazeAdapter(private var list: ArrayList<String>,private var clickListener: ExpansionGrazeClick) :
    RecyclerView.Adapter<GrazeAdapter.GrazeAddHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GrazeAddHolder {
        return GrazeAddHolder(LayoutInflater.from(parent.context).inflate(R.layout.expansion_sub_list,parent,false))
    }

    override fun onBindViewHolder(holder: GrazeAddHolder, position: Int) {
        holder.onBind(list[position],clickListener)
    }

    override fun getItemCount(): Int {
        return list.size
    }


    class GrazeAddHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textView = itemView.findViewById<TextView>(R.id.expansion_sub_title)
        var boolean: Boolean = false
        fun onBind(string: String,expansionClick: ExpansionGrazeClick) {
            textView.text = string
            itemView.setOnClickListener{
                expansionClick.expansionItemClick(string, adapterPosition)
            }
        }
    }

}

interface ExpansionGrazeClick {
    fun expansionItemClick(string: String, position: Int)
}