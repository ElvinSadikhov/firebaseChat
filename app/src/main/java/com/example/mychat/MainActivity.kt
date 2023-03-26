package com.example.mychat

import android.graphics.drawable.BitmapDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import com.example.mychat.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.ktx.app
import com.squareup.picasso.Picasso

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth
        setupActionBar()

        val database = Firebase.database
        val myRef = database.getReference("message")

        binding.saveBtn.setOnClickListener {
            myRef.setValue(binding.messageEditText.text.toString())
        }
        onChangedListener(myRef)
    }


    private fun onChangedListener(dbRef: DatabaseReference) {
        dbRef.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                binding.apply {
                    rcView.append("\n")
                    rcView.append("Sergey: ${snapshot.value.toString()}")
                }
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