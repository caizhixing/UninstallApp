package xyz.hereislookingatyoukid

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

/**
 *author : caizhixing
 *date : 2019/10/12
 */
class ShortCutActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_short_cut)

        val packageName = getSharedPreferences(Constant.SP_NAME, MODE_PRIVATE).getString(
            Constant.SP_ITEM_NAME,
            getString(R.string.default_package)
        )
        deleteApp(packageName!!)

        finish()
    }
}
