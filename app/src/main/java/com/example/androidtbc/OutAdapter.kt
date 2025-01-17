package com.example.androidtbc

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.androidtbc.databinding.ItemFieldUnionBinding

class OutAdapter(
    val listList: List<List<FieldDTO>>,
    private val onValueChanged: (Int?, String) -> Unit
): RecyclerView.Adapter<OutAdapter.OutViewHolder>() {

    inner class OutViewHolder(private val binding: ItemFieldUnionBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(){
            val item = listList[adapterPosition]
            with(binding){
                rvOut.adapter = FieldsAdapter(item, onValueChanged)
                rvOut.layoutManager = LinearLayoutManager(root.context)
            }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OutViewHolder {
        return OutViewHolder(ItemFieldUnionBinding.inflate(LayoutInflater.from(parent.context),parent, false))
    }

    override fun onBindViewHolder(holder: OutViewHolder, position: Int) {
        holder.bind()
    }

    override fun getItemCount(): Int = listList.size
}