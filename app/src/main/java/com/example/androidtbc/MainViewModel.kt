package com.example.androidtbc

import androidx.lifecycle.ViewModel
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.lang.reflect.Type

class MainViewModel : ViewModel() {
    private val _messages = MutableStateFlow<List<FieldDTO>>(emptyList())
    val messages = _messages.asStateFlow()


    private val originalMessages = mutableListOf<FieldDTO>()


    private val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).add(MessageTypeAdapter()).build()
    private val type: Type = Types.newParameterizedType(List::class.java, FieldDTO::class.java)
    private val jsonAdapter: JsonAdapter<List<FieldDTO>> = moshi.adapter(type)

    init {
        parseJson(JEMALA).also {
            originalMessages.addAll(it)
            _messages.value = it
        }
    }
    private fun parseJson(json: String): List<FieldDTO> {
        return try {
            jsonAdapter.fromJson(json) ?: emptyList()
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    fun search(query: String) {
        if (query.isEmpty()) {
            _messages.value = originalMessages
            return
        }

        val filteredList = originalMessages.filter {
            it.owner.contains(query, ignoreCase = true)
        }
        _messages.value = filteredList
    }



    companion object{
        const val JEMALA = """[ 
   { 
      "id":1, 
      "image":"https://www.alia.ge/wp-content/uploads/2022/09/grisha.jpg", 
      "owner":"გრიშა ონიანი", 
      "last_message":"თავის ტერიტორიას ბომბავდა", 
      "last_active":"4:20 PM", 
      "unread_messages":3, 
      "is_typing":false, 
      "laste_message_type":"text" 
   }, 
   { 
      "id":2, 
      "image":null, 
      "owner":"ჯემალ კაკაურიძე", 
      "last_message":"შემოგევლე", 
      "last_active":"3:00 AM", 
      "unread_messages":0, 
      "is_typing":true, 
      "laste_message_type":"voice" 
   }, 
   { 
      "id":3, 
      "image":"https://i.ytimg.com/vi/KYY0TBqTfQg/hqdefault.jpg", 
      "owner":"გურამ ჯინორია", 
      "last_message":"ცოცხალი ვარ მა რა ვარ შე.. როდის იყო კვტარი ტელეფონზე ლაპარაკობდა", 
      "last_active":"1:00 ", 
      "unread_messages":0, 
      "is_typing":false, 
      "laste_message_type":"file" 
   }, 
   { 
      "id":4, 
      "image":"", 
      "owner":"კაკო წენგუაშვილი", 
      "last_message":"ადამიანი რო მოსაკლავად გაგიმეტებს თანაც ქალი ის დასანდობი არ არი", 
      "last_active":"1:00 PM", 
      "unread_messages":0, 
      "is_typing":false, 
      "laste_message_type":"text" 
   } 
] """
    }
}