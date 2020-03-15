package com.jayantx.messenger.NewChatActivityRV

import android.os.Parcelable
import android.provider.ContactsContract
import kotlinx.android.parcel.Parcelize

@Parcelize
 class NewChatDataClass(var email:String,var name:String,var password:String,var profile_pic:String,var username:String,var uuid:String):Parcelable{
     constructor(): this("","","","","","")
 }