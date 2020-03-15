package com.jayantx.messenger

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.jayantx.messenger.NewChatActivityRV.NewChatDataClass
import com.jayantx.messenger.NewChatActivityRV.NewChatRVAdapter
import kotlinx.android.synthetic.main.activity_new_chat.*

class NewChatActivity : AppCompatActivity() {

    var listOfUsers:MutableList<NewChatDataClass>?  = null
    var adapter:NewChatRVAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_chat)

        retrieveUser()

        supportActionBar!!.title = "New Chat"

        listOfUsers = ArrayList()
        adapter =  NewChatRVAdapter(this,listOfUsers as ArrayList)
        rv_new_chat_activity.adapter = adapter
        rv_new_chat_activity.layoutManager = LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false)

    }



    private fun retrieveUser() {

        val userRef = FirebaseDatabase.getInstance().reference.child("users")

        userRef.addValueEventListener(object : ValueEventListener{

            override fun onCancelled(p0: DatabaseError) {

            }


            override fun onDataChange(datasnapshot: DataSnapshot) {
                for (snapshot in datasnapshot.children){
                    val listOfUsersFromFirebase = snapshot.getValue(NewChatDataClass::class.java)
                    if (listOfUsersFromFirebase!=null){
                        if(listOfUsersFromFirebase.uuid!= FirebaseAuth.getInstance().currentUser!!.uid){
                            listOfUsers?.add(listOfUsersFromFirebase)
                        }

                    }
                }

                adapter?.notifyDataSetChanged()
            }

        })
    }
}




