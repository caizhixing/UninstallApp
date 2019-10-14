package xyz.hereislookingatyoukid

import android.app.PendingIntent
import android.content.Intent
import android.content.Intent.ACTION_MAIN
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.content.pm.ShortcutInfo
import android.content.pm.ShortcutManager
import android.graphics.drawable.Icon
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import xyz.hereislookingatyoukid.databinding.ActivityMainBinding


/**
 *author : caizhixing
 *date : 2019/10/12
 */
class MainActivity : AppCompatActivity(), ItemLongClick {

    companion object {
        val TAG = "MainActivity"
    }

    private lateinit var dataBinding: ActivityMainBinding
    private lateinit var adapter: AppItemAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dataBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        setSupportActionBar(toolbar)
        adapter = AppItemAdapter()
        adapter.setListener(this)
        dataBinding.swipe.isRefreshing = true
        initDataBinding()
        initData()
        registerUnInstallReceiver()
    }

    private fun registerUnInstallReceiver() {
        val receiver = AppUninstallReceiver()
        val intentFilter = IntentFilter()
        intentFilter.addAction(Intent.ACTION_PACKAGE_REMOVED)
        intentFilter.addDataScheme("package")
        registerReceiver(receiver, intentFilter)
    }

    private fun initDataBinding() {
        dataBinding.fabClick = View.OnClickListener {
            val packageName = getSharedPreferences(Constant.SP_NAME, MODE_PRIVATE).getString(
                Constant.SP_ITEM_NAME,
                getString(R.string.default_package)
            )
            deleteApp(packageName!!)
        }
        dataBinding.rcv.adapter = adapter
        dataBinding.rcv.setCommonItemDecoration()
        dataBinding.swipe.setOnRefreshListener {
            initData()
        }
    }

    fun initData() {
        GlobalScope.launch {
            withContext(Dispatchers.Main) {
                dataBinding.swipe.isRefreshing = true
            }
            withContext(Dispatchers.IO) {
                val pm = packageManager
                val packages = pm.getInstalledApplications(PackageManager.GET_META_DATA)
                val apps = arrayListOf<AppInfo>()
                try {
                    for (packageInfo in packages) {
                        val appName = packageInfo.loadLabel(pm).toString()
                        val packageName = packageInfo.packageName
                        val installedTime = getFirstInstallTime(packageName)
                        val info = AppInfo(appName, packageName, installedTime)
                        apps.add(info)
                    }
                    apps.sortByDescending {
                        it.firstInstallTime
                    }
                    withContext(Dispatchers.Main) {
                        dataBinding.swipe.isRefreshing = false
                        adapter.submitList(apps)
                    }
                } catch (e: Exception) {
                    Log.e(TAG, e.toString())
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> {
                val dialog = InputDialog()
                dialog.show(supportFragmentManager, "input")
                true
            }
            R.id.action_add_short_cut -> {
                addPinnedShortCut()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun addPinnedShortCut() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val shortcutManager = getSystemService(ShortcutManager::class.java)
            if (shortcutManager!!.isRequestPinShortcutSupported) {
                val intent = Intent(this,ShortCutActivity::class.java)
                intent.action = ACTION_MAIN
                val pinShortcutInfo = ShortcutInfo.Builder(this, "delete default app")
                    .setShortLabel(getString(R.string.tip_quick_uninstall))
                    .setIcon(Icon.createWithResource(this, R.mipmap.ic_launcher))
                    .setIntent(intent)
                    .build()
                val pinnedShortcutCallbackIntent =
                    shortcutManager.createShortcutResultIntent(pinShortcutInfo)
                val successCallback = PendingIntent.getBroadcast(
                    this, /* request code */ 0,
                    pinnedShortcutCallbackIntent, /* flags */ 0
                )
                shortcutManager.requestPinShortcut(
                    pinShortcutInfo,
                    successCallback.intentSender
                )
                toast(getString(R.string.tip_add_pinned_short_success))
            }

        } else {
            toast(getString(R.string.tip_add_pinned_short_cut))
        }
    }

    override fun click(appInfo: AppInfo) {
        Snackbar.make(rcv, getString(R.string.cancel), Snackbar.LENGTH_LONG)
            .setAction(getString(R.string.uninstall)) {
                deleteApp(appInfo.packageName)
            }.show()
    }

}
