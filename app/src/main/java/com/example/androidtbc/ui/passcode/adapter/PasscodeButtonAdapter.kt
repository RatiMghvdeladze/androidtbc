package com.example.androidtbc.ui.passcode.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.androidtbc.R
import com.example.androidtbc.databinding.ItemPasscodeButtonBinding

class PasscodeButtonAdapter(private val onBtnClick: (String) -> Unit) :
    RecyclerView.Adapter<PasscodeButtonAdapter.ButtonViewHolder>() {

    private val keys = listOf(
        "1", "2", "3",
        "4", "5", "6",
        "7", "8", "9",
        "fp", "0", "del"
    )

    inner class ButtonViewHolder(private val binding: ItemPasscodeButtonBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(key: String) {
            with(binding) {
                when (key) {
                    "fp" -> {
                        tvDigit.visibility = View.GONE
                        ivIcon.visibility = View.VISIBLE
                        ivIcon.setImageResource(R.drawable.ic_fingerprint)
                        root.setOnClickListener { onBtnClick("fingerprint") }
                    }
                    "del" -> {
                        tvDigit.visibility = View.GONE
                        ivIcon.visibility = View.VISIBLE
                        ivIcon.setImageResource(R.drawable.ic_backspace)
                        root.setOnClickListener { onBtnClick("delete") }
                    }
                    else -> {
                        ivIcon.visibility = View.GONE
                        tvDigit.visibility = View.VISIBLE
                        tvDigit.text = key
                        root.setOnClickListener { onBtnClick(key) }
                    }
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ButtonViewHolder {
        val binding = ItemPasscodeButtonBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ButtonViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ButtonViewHolder, position: Int) {
        holder.bind(keys[position])
    }

    override fun getItemCount() = keys.size
}
