package kg.forestry.ui.plant.plant_info

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import kg.forestry.R

class AnimalAdapter(
    private var list: ArrayList<AnimalModel>,
    private var clickListener: ExpansionClick
) :
    RecyclerView.Adapter<AnimalAdapter.AnimalAddHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AnimalAddHolder {
        return AnimalAddHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.ex_sub_list,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: AnimalAddHolder, position: Int) {
        holder.onBind(list[position], clickListener)
    }

    override fun getItemCount(): Int {
        return list.size
    }


    class AnimalAddHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textView = itemView.findViewById<TextView>(R.id.child_textTitle)
        val imageView = itemView.findViewById<ImageView>(R.id.child_imageView)
        @SuppressLint("ResourceAsColor")
        fun onBind(model: AnimalModel, expansionClick: ExpansionClick) {
            textView.text = model.title
            imageView.setImageResource(model.img)
            itemView.setBackgroundColor(if (model.isSelected) ContextCompat.getColor(itemView.context,R.color.light_green) else Color.WHITE)
            itemView.setOnClickListener {
                model.isSelected = !model.isSelected
                itemView.setBackgroundColor(if (model.isSelected) ContextCompat.getColor(itemView.context,R.color.light_green) else Color.WHITE)
                expansionClick.animalClick(model)
            }
        }
    }
}


