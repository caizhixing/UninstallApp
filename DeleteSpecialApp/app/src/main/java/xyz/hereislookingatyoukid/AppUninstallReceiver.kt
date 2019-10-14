package xyz.hereislookingatyoukid

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

/**
 *author : caizhixing
 *date : 2019/10/14
 */
class AppUninstallReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if(context is MainActivity){
            context.initData()
        }
    }
}