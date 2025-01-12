package com.am.catapp

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.am.catapp.models.Cat

class CatListAdapter(
    private var cats: List<Cat>
) : RecyclerView.Adapter<CatListAdapter.ViewHolder>() {

    private var listener: Listener? = null

    interface Listener {
        fun onClick(position: Int)
    }

    fun setListener(listener: Listener?) {
        this.listener = listener
    }

    fun setData(cats: List<Cat>) {
        this.cats = cats
        notifyDataSetChanged()
    }

    class ViewHolder(itemView: CardView) : RecyclerView.ViewHolder(itemView) {
        var cardView: CardView = itemView
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val cv = LayoutInflater.from(parent.context)
            .inflate(R.layout.card_cat, parent, false) as CardView
        return ViewHolder(cv)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val cardView = holder.cardView
        val cat = cats[position]

        val imageView = cardView.findViewById<ImageView>(R.id.cat_image)
        val titleTextView = cardView.findViewById<TextView>(R.id.cat_name)
        val authorTextView = cardView.findViewById<TextView>(R.id.cat_temperament)
        val descTextView = cardView.findViewById<TextView>(R.id.cat_description)

        Log.d("ImageUrl", cat.imageUrl.toString())
        if (cat.imageUrl != null) {
            Glide.with(cardView.context).load(cat.imageUrl).into(imageView)
        } else {
            if (cat.breed.imageUrl != null) {
                Glide.with(cardView.context).load(cat.breed.imageUrl).into(imageView)
            }
        }

        titleTextView.text = cat.breed.name
        authorTextView.text = cat.breed.temperament
        descTextView.text = cat.breed.description

        cardView.setOnClickListener {
            listener?.onClick(position)
        }
    }

    override fun getItemCount(): Int {
        return cats.size
    }
}