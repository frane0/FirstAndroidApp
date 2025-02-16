package com.example.helloworld

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.json.JSONArray


class MainActivity : AppCompatActivity() {

    private lateinit var toastTrigger: Button
    private lateinit var clearText: Button
    private lateinit var saveText: Button
    private lateinit var deleteInputs: Button
    private lateinit var inputText: EditText
    var textHasEdited: Boolean = false
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: MyAdapter
    private lateinit var data: List<String>
    var textWatcher: TextWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            textHasEdited = true
        }

        override fun afterTextChanged(s: Editable) {
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.enableEdgeToEdge()
        setContentView(R.layout.constraint_layout)
        val sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)

        toastTrigger = findViewById<View>(R.id.toastTrigger) as Button
        clearText = findViewById<View>(R.id.clearText) as Button
        saveText = findViewById<View>(R.id.saveText) as Button
        deleteInputs = findViewById<View>(R.id.deleteInputs) as Button
        inputText = findViewById<View>(R.id.inputText) as EditText
        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = MyAdapter(loadText(sharedPreferences))
        recyclerView.adapter = adapter

        inputText.addTextChangedListener(textWatcher)
        toastTrigger.setOnClickListener {
            if (textHasEdited) {
                Toast.makeText(applicationContext, inputText.text, Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(applicationContext, "Text has not changed", Toast.LENGTH_LONG).show()
            }
        }
        clearText.setOnClickListener { inputText.setText("") }
        deleteInputs.setOnClickListener {
            val editor=sharedPreferences.edit()
            editor.putString("last10", "[]")
            editor.apply()
            adapter = MyAdapter(loadText(sharedPreferences))
            recyclerView.adapter = adapter
        }
        saveText.setOnClickListener {
            if (textHasEdited) {
                saveNewInput(sharedPreferences, inputText.text.toString())
                adapter = MyAdapter(loadText(sharedPreferences))
                recyclerView.adapter = adapter
                Toast.makeText(applicationContext, "Text has been saved!", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(applicationContext, "Text has not changed", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun loadText(sharedPreferences: SharedPreferences): MutableList<String> {
        val jsonString = sharedPreferences.getString("last10", "[]")
        val jsonArray = JSONArray(jsonString)
        Log.d("loadText","jsonArray size is ${jsonArray.length()}")
        val list = mutableListOf<String>()
        for (i in 0 until jsonArray.length()) {
            list.add(jsonArray.getString(i))
        }
        Log.d("loadText","list size is ${list.size}")
        return list
    }

    private fun saveNewInput(sharedPreferences: SharedPreferences, newInput: String): MutableList<String> {
        val list=loadText(sharedPreferences)
        list.add(0,newInput)
        Log.d("saveInput","input added to the list. new list size is ${list.size}")
        if(list.size>10) {
            list.removeAt(list.size - 1)
            Log.d("saveInput","last input removed from the list. new list size is ${list.size}")
        }
        for ((index, isim) in list.withIndex()) {
            if(index>0){
                list.set(index, "${index+1}) ${isim.substring(3)}")
            }
            else{
                list.set(index, "${index+1}) $isim")
            }

        }
        val jsonArray = JSONArray(list)
        Log.d("saveInput","jsonArray size is ${jsonArray.length()}")
        val jsonString =jsonArray.toString()
        Log.d("saveInput","jsonString is $jsonString")
        val editor=sharedPreferences.edit()
        editor.putString("last10", jsonString)
        editor.apply()
        Log.d("saveInput","saved string is ${sharedPreferences.getString("last10","[]")}")
        return list
    }
}