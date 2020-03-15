package com.jayantx.messenger

class ActualMsgDataClass(var FromId: String, var ActualMsgText:String, var ToId:String,var MsgId:String, var timestamp:Long) {
    constructor(): this("","","","",-1)
}