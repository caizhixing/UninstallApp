package xyz.hereislookingatyoukid

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Resources
import android.graphics.Rect
import android.net.Uri
import android.os.Build
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.math.roundToInt

/**
 *author : caizhixing
 *date : 2019/10/12
 */

fun RecyclerView.setCommonItemDecoration() {
    addItemDecoration(object : RecyclerView.ItemDecoration() {
        override fun getItemOffsets(outRect: Rect, itemPosition: Int, parent: RecyclerView) {
            outRect.top = 10.dp2px()
        }
    })
}

fun Int.dp2px(): Int {
    val density = Resources.getSystem().displayMetrics.density;
    return (this * density).roundToInt()
}

fun Context.toast(content: String) {
    Toast.makeText(this, content, Toast.LENGTH_SHORT).show()
}

fun Activity.deleteApp(packageName: String) {
    if (this.appInstalledOrNot(packageName)) {
        GlobalScope.launch {
            withContext(Dispatchers.IO){
                val intent = Intent(Intent.ACTION_DELETE)
                intent.data = Uri.parse("package:$packageName")
                startActivity(intent)
            }
        }

    } else {
        toast(String.format(getString(R.string.tip_uninstall_app), packageName))
    }
}

fun Activity.appInstalledOrNot(uri: String): Boolean {
    val pm = packageManager
    try {
        pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES)
        return true
    } catch (e: PackageManager.NameNotFoundException) {
    }

    return false
}

fun Activity.getFirstInstallTime(packageName: String): Long {
    val pm = packageManager
    val info = pm.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES)
    return info.firstInstallTime
}