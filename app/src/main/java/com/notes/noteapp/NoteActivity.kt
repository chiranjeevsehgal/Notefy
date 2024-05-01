package com.notes.noteapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.notes.noteapp.databinding.ActivityNoteBinding
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration

class NoteActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNoteBinding
    private lateinit var config: RealmConfiguration
    private lateinit var realm: Realm

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNoteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        config = RealmConfiguration.create(schema = setOf(Note::class))
        realm = Realm.open(config)

        binding.btnSaveNote.setOnClickListener {
            if (writeNote()) {
                finish()
            }
        }

        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.arrow_back)
        }

        toolbar.setNavigationOnClickListener {
            // Handle navigation icon click here
            onBackPressed()
        }
    }

    private fun writeNote(): Boolean {
        val title = binding.singleLine.text.toString().trim()
        val description = binding.multiLine.text.toString().trim()

        if (title.isNotEmpty() || description.isNotEmpty()) {
            realm.writeBlocking {
            this.copyToRealm(Note().apply {
                this.title = title
                this.description = description
                createdTime = System.currentTimeMillis()
            })
        }
        Toast.makeText(this,"Note added successfully!",Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Title or description cannot be empty!", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    override fun onDestroy() {
        super.onDestroy()
        realm.close()
    }
}