package com.example.testreduce

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.*

abstract class BaseService : Service() {

    protected val thread = HandlerThread("reduce")
    lateinit var handler: Handler


    override fun onBind(intent: Intent?): IBinder? {
        return Messenger(handler).binder
    }

    override fun onCreate() {
        super.onCreate()
        thread.start()
        handler = ReduceHandler(this, thread.looper)
    }

    override fun onDestroy() {
        super.onDestroy()
        thread.quitSafely()
    }


    abstract fun collectUriListsFormAllDevices(data: Bundle, replyTo: Messenger?)

    abstract fun classifyUrisByDeviceId(data: Bundle, replyTo: Messenger?)


    inner class ReduceHandler(context: Context, looper: Looper) : Handler(looper) {
        override fun handleMessage(msg: Message) {
            //数据安全检查
            handleMessage(msg)
            when(msg.what){
                0 -> {
                    if(!msg.data.getStringArrayList("uriMap").isNullOrEmpty()){
                        //分配uri
                        this@BaseService.classifyUrisByDeviceId(msg.data,msg.replyTo)
                    }
                }
                1 -> {
                     if(!msg.data.getStringArrayList("uriList").isNullOrEmpty()){
                         //收集来自所有设备的uriList
                         this@BaseService.collectUriListsFormAllDevices(msg.data, msg.replyTo)
                     }
                }
            }
        }

    }
}