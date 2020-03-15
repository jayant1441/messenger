package com.jayantx.messenger

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.jayantx.messenger.NewChatActivityRV.NewChatDataClass
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.activity_chatting_area.*
import kotlinx.android.synthetic.main.chat_from_me_ticket.view.*
import kotlinx.android.synthetic.main.chat_to_me_ticket.view.*


class ChattingArea : AppCompatActivity() {

    val adapter = GroupAdapter<GroupieViewHolder>()
//    val intent_extra =

    val our_profile_pic_url = MainActivity.our_profile_pic_url

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chatting_area)

        supportActionBar?.title = intent.getParcelableExtra<NewChatDataClass>("User_Key").name



        SendMessage()
        PerformRecieveMessage()


        rv_chattting_area.adapter = adapter
        rv_chattting_area.layoutManager = LinearLayoutManager(this)
    }


    private fun SendMessage() {

        val FromId = FirebaseAuth.getInstance().currentUser?.uid
        val ToId = intent.getParcelableExtra<NewChatDataClass>("User_Key").uuid

        val ref  = FirebaseDatabase.getInstance().getReference("/all-messages/$FromId/$ToId")
        val opposite_ref =  FirebaseDatabase.getInstance().getReference("/all-messages/$ToId/$FromId")
        val latest_messages_ref  = FirebaseDatabase.getInstance().getReference("/all-latest-messages/$FromId/$ToId")
        val  latest_opposite_messages_ref=  FirebaseDatabase.getInstance().getReference("/all-latest-messages/$ToId/$FromId")


        val message_key = ref.push()
        val opposite_message_key = opposite_ref.push()

        val MsgId = message_key.toString()
        val timestamp = System.currentTimeMillis()





        btn_send_message.setOnClickListener {
            if (FromId == null)return@setOnClickListener
            if (et_send_message.text.isEmpty()) return@setOnClickListener

            Log.d("btao", intent.getParcelableExtra<NewChatDataClass>("User_Key").profile_pic)
            message_key.setValue(ActualMsgDataClass(FromId, et_send_message.text.toString(), ToId, MsgId, timestamp)).addOnCompleteListener {
                et_send_message.setText("")
                Log.d("btao", "Success")
            }

            opposite_message_key.setValue(ActualMsgDataClass(FromId, et_send_message.text.toString(), ToId, MsgId, timestamp))
            latest_messages_ref.setValue(ActualMsgDataClass(FromId, et_send_message.text.toString(), ToId, MsgId, timestamp))
            latest_opposite_messages_ref.setValue(ActualMsgDataClass(FromId, et_send_message.text.toString(), ToId, MsgId, timestamp))
        }

    }

    private fun PerformRecieveMessage() {
        val FromId = FirebaseAuth.getInstance().currentUser?.uid
        val ToId = intent.getParcelableExtra<NewChatDataClass>("User_Key").uuid

        var ref  = FirebaseDatabase.getInstance().getReference("/all-messages/$FromId/$ToId")

        ref.addChildEventListener(object :ChildEventListener{
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onChildMoved(p0: DataSnapshot, p1: String?) {
            }

            override fun onChildChanged(p0: DataSnapshot, p1: String?) {

              var opposite_user_profile_pic = intent.getParcelableExtra<NewChatDataClass>("User_Key")?.profile_pic

//                var opposite_user_profile_pic = intent.getParcelableExtra<NewChatDataClass>("User_Key").profile_pic
                val dataSnapshot = p0.getValue(ActualMsgDataClass()::class.java)

                if (dataSnapshot?.FromId == FirebaseAuth.getInstance().currentUser!!.uid){
                    adapter.add(ChatFromMe(dataSnapshot.ActualMsgText,our_profile_pic_url))

                }
                else{
                    adapter.add(ChatToMe(dataSnapshot!!.ActualMsgText,opposite_user_profile_pic!!))
                }
            }

            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                val opposite_user_profile_pic = intent.getParcelableExtra<NewChatDataClass>("User_Key").profile_pic


                val dataSnapshot = p0.getValue(ActualMsgDataClass()::class.java)
                if (dataSnapshot?.FromId == FirebaseAuth.getInstance().currentUser!!.uid){
                    adapter.add(ChatFromMe(dataSnapshot.ActualMsgText,our_profile_pic_url))
                }
                else{
                    adapter.add(ChatToMe(dataSnapshot!!.ActualMsgText,opposite_user_profile_pic))
                }
            }
            override fun onChildRemoved(p0: DataSnapshot) {
            }
        })
    }
}

class ChatFromMe(var msg_from_us: String, var my_profile_pic_url: String): Item<GroupieViewHolder>(){
    override fun getLayout(): Int = R.layout.chat_from_me_ticket


    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.tv_msg_from_us.text = msg_from_us
       Picasso.get().load(my_profile_pic_url).into(viewHolder.itemView.civ_our_profile_pic)

    }

}

class ChatToMe(var msg_to_us: String, var opposite_user_profile_pic_url: String): Item<GroupieViewHolder>(){
    override fun getLayout(): Int = R.layout.chat_to_me_ticket


    override fun bind(viewHolder: GroupieViewHolder, position: Int) {

        viewHolder.itemView.tv_msg_to_us.text = msg_to_us
//        Glide.with(ChattingArea()).load(opposite_user_profile_pic_url).into(viewHolder.itemView.civ_our_profile_pic)
        Picasso.get().load(opposite_user_profile_pic_url).into(viewHolder.itemView.civ_opposite_profile_pic)

    }

}