package com.example.androidtbc

import androidx.lifecycle.ViewModel
import kotlinx.serialization.json.Json

class MainViewModel : ViewModel() {
    private val data = mutableMapOf<Int, String>()
    private val json = Json{
        explicitNulls = false
    }

    val obj = json.decodeFromString<List<List<FieldDTO>>>(JEMALA)


    fun updateField(fieldId: Int?, value: String) {
        fieldId?.let {
            data[it] = value
        }
    }

    fun validateForm(): String? {
        for (group in obj) {
            for (field in group) {
                field.fieldId?.let { id ->
                    if (field.required == true && data[id].isNullOrEmpty()) {
                        return "არ არის შევსებული ფილდი (${field.hint})"
                    }
                }
            }
        }
        return null
    }

    companion object{
        const val JEMALA = """[
   [
      {
         "field_id":1,
         "hint":"UserName",
         "field_type":"input",
         "keyboard":"text",
         "required":false,
         "is_active":true,
         "icon":"https://jemala.png/"
      },
      {
         "field_id":2,
         "hint":"Email",
         "field_type":"input",
         "required":true,
         "keyboard":"text",
         "is_active":true,
         "icon":"https://jemala.png/"
      },
      {
         "field_id":3,
         "hint":"phone",
         "field_type":"input",
         "required":true,
         "keyboard":"number",
         "is_active":true,
         "icon":"https://jemala.png/"
      }
   ],
   [
      {
         "field_id":4,
         "hint":"FullName",
         "field_type":"input",
         "keyboard":"text",
         "required":true,
         "is_active":true,
         "icon":"https://jemala.png/"
      },
      {
         "field_id":14,
         "hint":"Jemali",
         "field_type":"input",
         "keyboard":"text",
         "required":false,
         "is_active":true,
         "icon":"https://jemala.png/"
      },
      {
         "field_id":89,
         "hint":"Birthday",
         "field_type":"chooser",
         "required":false,
         "is_active":true,
         "icon":"https://jemala.png/"
      },
      {
         "field_id":898,
         "hint":"Gender",
         "field_type":"chooser",
         "required":false,
         "is_active":true,
         "icon":"https://jemala.png/"
      }
   ]
]"""



    }



}