package com.notes.noteapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import io.realm.kotlin.query.RealmResults
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Locale

class NoteAdapter(private val context: Context, private var dataSet: RealmResults<Note>) :
    RecyclerView.Adapter<NoteAdapter.ViewHolder>() {
        class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val title: TextView
            val description: TextView
            val timeCreated: TextView

            init {
                title = view.findViewById(R.id.title)
                description = view.findViewById(R.id.description)
                timeCreated = view.findViewById(R.id.timeCreated)
            }
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_view,parent,false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val note: Note = dataSet[position]
        holder.title.text = note.title
        holder.description.text = note.description

//        val formatTime = DateFormat.getDateTimeInstance().format(note.createdTime)
//        holder.timeCreated.text = formatTime

        val dateFormat = SimpleDateFormat("dd/MM/yyyy, h:mm a", Locale.getDefault())
        val formattedTime = dateFormat.format(note.createdTime)
        holder.timeCreated.text = formattedTime



        holder.itemView.setOnLongClickListener {
            val menu = PopupMenu(context,it)
            menu.menu.add("Edit")
            menu.menu.add("Delete")
            menu.setOnMenuItemClickListener { menuItem: MenuItem ->
                when (menuItem.title) {
                    "Edit" -> {
                        val noteData = Bundle()
                        noteData.putString("title",note.title)
                        noteData.putString("desc",note.description)
                        context.startActivity(Intent(context,EditActivity::class.java).putExtras(noteData))
                        true
                    }
                    "Delete" -> {
                        val config = RealmConfiguration.create(schema = setOf(Note::class))
                        val realm = Realm.open(config)
                        realm.writeBlocking {
                            findLatest(note)?.let { it -> delete(it) }
                        }
                        realm.close()
                        Toast.makeText(context,"Note deleted successfully!", Toast.LENGTH_SHORT).show()
                        true
                    }
                    else -> false
                }
            }
            menu.show()
            true
        }
    }

    override fun getItemCount() = dataSet.size
    fun updateData(notes: RealmResults<Note>) {
        this.dataSet = notes
        notifyDataSetChanged()
    }
}
