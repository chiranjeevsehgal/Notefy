package com.notes.noteapp

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.notes.noteapp.databinding.ActivityMainBinding
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import io.realm.kotlin.ext.query
import io.realm.kotlin.notifications.ResultsChange
import io.realm.kotlin.notifications.UpdatedResults
import io.realm.kotlin.query.RealmResults
import io.realm.kotlin.query.Sort
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var recyclerView: RecyclerView
    private lateinit var realm: Realm

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        val config = RealmConfiguration.create(schema = setOf(Note::class))
        realm = Realm.open(config)

        val notes: RealmResults<Note> = realm.query<Note>().sort("createdTime",Sort.DESCENDING).find()

        recyclerView = binding.recyclerView
        recyclerView.layoutManager = LinearLayoutManager(this)
        val adapter = NoteAdapter(this,notes)
        recyclerView.adapter = adapter

        // Floating button


        binding.btnAddNote.setOnClickListener {
            startActivity(Intent(this,NoteActivity::class.java))
        }



        val notesListener = CoroutineScope(Dispatchers.Default).launch {
            // create a Flow from the Item collection, then add a listener to the Flow
            val notesFlow = notes.asFlow()
            notesFlow.collect { changes: ResultsChange<Note> ->
                when (changes) {
                    // UpdatedResults means this change represents an update/insert/delete operation
                    is UpdatedResults -> {
                        withContext(Dispatchers.Main){
                            adapter.updateData(realm.query<Note>().sort("createdTime",Sort.DESCENDING).find())
                        }
                    }
                    else -> { }// types other than UpdatedResults are not changes -- ignore them
                }
            }
        }

//        binding.btnAddNote.setOnClickListener {
//            startActivity(Intent(this,NoteActivity::class.java))
//        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId){
            R.id.about -> {
//                Toast.makeText(this, "Settings Selected", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, AboutActivity::class.java))
                return true
            }

        }
        return super.onOptionsItemSelected(item)
    }

    override fun onDestroy() {
        super.onDestroy()
        realm.close()
    }
}