package com.example.mychat.ui.activity

import android.graphics.drawable.BitmapDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mychat.R
import com.example.mychat.adapter.MessageAdapter
import com.example.mychat.databinding.ActivityMainBinding
import com.example.mychat.model.MessageModel
import com.example.mychat.service.FirebaseDbService
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var adapter: MessageAdapter
    private val dbService = FirebaseDbService()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth
        setupActionBar()


        binding.saveBtn.setOnClickListener {
            if(binding.messageEditText.text.isNullOrBlank()) {
                binding.messageEditText.error = getString(R.string.cant_be_blank)
            } else {
                dbService.addMessage(MessageModel(auth.currentUser?.displayName, binding.messageEditText.text.toString()))
                binding.messageEditText.setText("")
            }
        }
        onChangedListener(dbService.messagesRef)
        initRcView()
    }

    private fun initRcView() = with(binding) {
        adapter = MessageAdapter()
        rcView.layoutManager = LinearLayoutManager(this@MainActivity)
        rcView.adapter = adapter
    }

    private fun onChangedListener(dbRef: DatabaseReference) {
        dbRef.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = ArrayList<MessageModel>()
                for(s in snapshot.children) {
                    val message = s.getValue(MessageModel::class.java)
                    if(message != null) {
                        list.add(message)
                    }
                }
                adapter.submitList(list)
            }
            override fun onCancelled(error: DatabaseError) {
            }

        })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.signOut -> {
                auth.signOut()
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setupActionBar() {
        var actionBar = supportActionBar
        Thread {
            val bMap = Picasso.get().load(auth.currentUser?.photoUrl).get()
            val drawableIcon = BitmapDrawable(resources, bMap)
            runOnUiThread {
                actionBar?.setDisplayHomeAsUpEnabled(true)
                actionBar?.setHomeAsUpIndicator(drawableIcon)
                actionBar?.title = auth.currentUser?.displayName
            }
        }.start()
    }

}