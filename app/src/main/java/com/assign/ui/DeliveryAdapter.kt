package com.assign.ui

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.assign.R
import com.assign.beans.Delivery
import com.bumptech.glide.Glide


class DeliveryAdapter(
    private val context: Context,
    private val listItems: ArrayList<Delivery>,
    private val itemSelectListener: ItemSelectListener,
    private val itemsFilterListener: ItemFilterListener
) :
    RecyclerView.Adapter<DeliveryAdapter.ViewHolder>(), Filterable {
    private var filterList = listItems


    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val query = constraint.toString()
                val fList: List<Delivery>
                fList = if (query.isEmpty())
                    listItems
                else {
                    listItems.filter { it.description.toLowerCase().contains(query.toLowerCase()) }
                }

                val filterResults = FilterResults()
                filterResults.values = fList
                return filterResults
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                filterList = results?.values as ArrayList<Delivery>
                notifyDataSetChanged()
                itemsFilterListener.onItemFilter(filterList.size)
            }
        }
    }

    interface ItemSelectListener {
        fun onItemSelected(delivery: Delivery)
    }

    interface ItemFilterListener {
        fun onItemFilter(size: Int)
    }

    private fun ViewGroup.inflate(layout: Int): View {
        return LayoutInflater.from(context).inflate(layout, this, false)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(parent.inflate(R.layout.item_delivery))
    }

    override fun getItemCount(): Int {
        return filterList.size
    }

    override fun onBindViewHolder(vH: ViewHolder, position: Int) {
        val item = filterList[position]
        vH.textView.text = item.description

        Glide.with(context).load(item.imageUrl).into(vH.imageView)

        vH.parent.setOnClickListener {
            itemSelectListener.onItemSelected(item)
        }
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.imgView)
        val textView: TextView = itemView.findViewById(R.id.txtDesc)
        val parent: View = itemView
    }

}