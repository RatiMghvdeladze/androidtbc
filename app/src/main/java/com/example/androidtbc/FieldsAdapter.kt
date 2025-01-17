package com.example.androidtbc

import android.app.DatePickerDialog
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import com.example.androidtbc.databinding.ChooserFieldBinding
import com.example.androidtbc.databinding.InputFieldBinding
import java.util.Calendar

class FieldsAdapter(
    val fieldList: List<FieldDTO>,
    private val onValueChanged: (Int?, String) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    inner class InputViewHolder(private val binding: InputFieldBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind() {
            val item = fieldList[adapterPosition]
            with(binding) {
                etInputField.setHint(item.hint)

                etInputField.addTextChangedListener(object : TextWatcher {
                    override fun beforeTextChanged(
                        s: CharSequence?,
                        start: Int,
                        count: Int,
                        after: Int
                    ) {
                    }

                    override fun onTextChanged(
                        s: CharSequence?,
                        start: Int,
                        before: Int,
                        count: Int
                    ) {
                    }

                    override fun afterTextChanged(s: Editable?) {
                        val text = s?.toString() ?: ""

                        val errorMessage = when (item.hint?.lowercase()) {
                            "email" -> if (!android.util.Patterns.EMAIL_ADDRESS.matcher(text)
                                    .matches()
                            )
                                "Invalid email!" else null

                            "phone" -> if (text.length < 9)
                                "Invalid phone number!" else null

                            else -> null
                        }

                        binding.etInputField.error = errorMessage
                        onValueChanged(item.fieldId, text)
                    }
                })

                when (item.keyboard) {
                    KeyboardType.TEXT.name.lowercase() -> etInputField.inputType =
                        InputType.TYPE_CLASS_TEXT

                    KeyboardType.NUMBER.name.lowercase() -> etInputField.inputType =
                        InputType.TYPE_CLASS_NUMBER
                }
            }
        }
    }

    inner class ChooserViewHolder(private val binding: ChooserFieldBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind() {
            val item = fieldList[adapterPosition]
            with(binding) {
                tvChooser.setHint(item.hint)
                when (item.hint?.lowercase()) {
                    "birthday" -> chooserContainer.setBackgroundResource(R.drawable.ic_calendar)
                    "gender" -> chooserContainer.setBackgroundResource(R.drawable.ic_person)
                }


                root.setOnClickListener {
                    if (item.hint?.lowercase() == "birthday") {

                        val calendar = Calendar.getInstance()
                        val year = calendar.get(Calendar.YEAR)
                        val month = calendar.get(Calendar.MONTH)
                        val day = calendar.get(Calendar.DAY_OF_MONTH)

                        val datePickerDialog = DatePickerDialog(
                            root.context,
                            { _, selectedYear: Int, selectedMonth: Int, selectedDay: Int ->
                                val date = "$day/${month + 1}/$year"
                                tvChooser.text = date
                                onValueChanged(item.fieldId, date)
                            },
                            year,
                            month,
                            day
                        )

                        datePickerDialog.show()

                    } else {


                        val popupMenu = PopupMenu(root.context, root)

                        listOf("Male", "Female").forEachIndexed { index, item ->
                            popupMenu.menu.add(0, index, index, item)
                        }

                        popupMenu.setOnMenuItemClickListener { menuItem ->
                            tvChooser.text = menuItem.title
                            onValueChanged(item.fieldId, menuItem.title.toString())
                            true
                        }
                        popupMenu.show()
                    }

                }
            }
        }

    }

    override fun getItemViewType(position: Int): Int {
        val item = fieldList[position]
        return when (item.fieldType) {
            Type.INPUT.name.lowercase() -> INPUT
            else -> {
                CHOOSER
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == INPUT) {
            InputViewHolder(
                InputFieldBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
        } else {
            ChooserViewHolder(
                ChooserFieldBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
        }
    }

    override fun getItemCount(): Int = fieldList.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        if (holder is InputViewHolder) {
            holder.bind()
        } else if (holder is ChooserViewHolder) {
            holder.bind()
        }
    }


    companion object {
        private const val INPUT = 1
        private const val CHOOSER = 2
    }


}
