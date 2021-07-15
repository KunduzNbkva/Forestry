package kg.forestry.ui.plant.plant_info

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kg.forestry.R

class ColorAdapter(private var list: ArrayList<ExpansionModel>,private var clickListener: ExpansionClick) :
    RecyclerView.Adapter<ColorAdapter.ObservationAddHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ObservationAddHolder {
        return ObservationAddHolder(LayoutInflater.from(parent.context).inflate(R.layout.ex_sub_list,parent,false))
    }

    override fun onBindViewHolder(holder: ObservationAddHolder, position: Int) {
        holder.onBind(list[position],clickListener)
    }

    override fun getItemCount(): Int {
        return list.size
    }


    class ObservationAddHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textView = itemView.findViewById<TextView>(R.id.child_textTitle)
        val imageView = itemView.findViewById<ImageView>(R.id.child_imageView)
        fun onBind(model: ExpansionModel,expansionClick: ExpansionClick) {
            textView.text = model.title
            imageView.setImageResource(model.img)
            if(model.isColor == true)
            itemView.setOnClickListener{
                expansionClick.colorClick(model)
            }
        }
    }

}

interface ExpansionClick {
    fun colorClick(model:ExpansionModel)
    fun animalClick(model:AnimalModel)

}


