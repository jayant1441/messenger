package com.jayantx.messenger

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.rv_main_activity_ticket.view.*

class MainActivity : AppCompatActivity() {

    companion object{
        var our_profile_pic_url: String= ""
    }
    val current_user_uid: String = FirebaseAuth.getInstance().currentUser!!.uid
    private val adapter = GroupAdapter<GroupieViewHolder>()



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        ListenForLatestMessage()

        val ref_users = FirebaseDatabase.getInstance().getReference("/users/$current_user_uid")
        ref_users.addListenerForSingleValueEvent(object :ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                val profile_pic_daata_from_database = p0.child("profile_pic").value
                our_profile_pic_url = profile_pic_daata_from_database.toString()
            }

        })

        rv_main_activity.adapter = adapter
        rv_main_activity.layoutManager = LinearLayoutManager(this)

    }



    private fun ListenForLatestMessage() {
        val ref_to_all_latest_message  = FirebaseDatabase.getInstance().getReference("/all-latest-messages/$current_user_uid")
        ref_to_all_latest_message.addChildEventListener(object : ChildEventListener{
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onChildMoved(p0: DataSnapshot, p1: String?) {
            }

            override fun onChildChanged(p0: DataSnapshot, p1: String?) {
            }

            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                var chatMessage = p0.getValue(ActualMsgDataClass::class.java)?: return
                adapter.add(LatestMessages_MainActivity(chatMessage))
            }

            override fun onChildRemoved(p0: DataSnapshot) {
            }

        })
    }


    class LatestMessages_MainActivity(var chatMessage: ActualMsgDataClass): Item<GroupieViewHolder>(){


        override fun getLayout(): Int = R.layout.rv_main_activity_ticket

        override fun bind(viewHolder: GroupieViewHolder, position: Int) {

            val ref = FirebaseDatabase.getInstance().getReference("/users/${chatMessage.ToId}")
            ref.addValueEventListener(object :ValueEventListener{
                override fun onCancelled(p0: DatabaseError) {

                }

                override fun onDataChange(p0: DataSnapshot) {
                    viewHolder.itemView.tv_main_activity_username.text = p0.child("username").value.toString()
                    val userDetail = p0.getValue(SignUpActivity.FirebaseDatabaseDataClass::class.java)
                    Picasso.get().load(userDetail?.profile_pic).into(viewHolder.itemView.civ_main_activity_profile_pic)
                }

            })


        }

    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.menu_new_chat->{
                startActivity(Intent(this,NewChatActivity::class.java))
            }
            R.id.menu_signout->{
                FirebaseAuth.getInstance().signOut()
                startActivity(Intent(this,SignUpActivity::class.java))
                this.finish()

            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.nav_menu,menu)
        return super.onCreateOptionsMenu(menu)
    }


}
