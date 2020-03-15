package com.jayantx.messenger.NewChatActivityRV

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.jayantx.messenger.ChattingArea
import com.jayantx.messenger.NewChatActivity
import com.jayantx.messenger.R
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.new_chat_rv_ticket.view.*

class NewChatRVAdapter(var context : Context,var list_of_users:ArrayList<NewChatDataClass>): RecyclerView.Adapter<NewChatRVAdapter.ViewHolder>() {



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.new_chat_rv_ticket,parent,false))
    }

    override fun getItemCount(): Int {
        return list_of_users.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val listItem = list_of_users[position]
        holder.tv_name_of_person_ticket.text = listItem.name
//        Glide.with(context).load(listItem.profile_pic).into(holder.iv_profile_image_ticket)
        Picasso.get().load(listItem.profile_pic).into(holder.iv_profile_image_ticket)


        holder.rl_rv_new_chat_activity.setOnClickListener {
            val intent_to_chating_area = Intent(context,ChattingArea::class.java)
            intent_to_chating_area.putExtra("User_Key", listItem)
            intent_to_chating_area.putExtra("listItem_name_profile_pic",listItem.profile_pic)
            context.startActivity(intent_to_chating_area)
            NewChatActivity().finish()

        }

    }

    inner class ViewHolder(itemview: View) :RecyclerView.ViewHolder(itemview){
        val iv_profile_image_ticket  = itemview.iv_profile_image_ticket
        val tv_name_of_person_ticket  = itemview.tv_name_of_person_ticket
        val rl_rv_new_chat_activity  = itemview.rl_rv_new_chat_activity

    }

}