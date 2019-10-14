package xyz.hereislookingatyoukid

import android.content.Context.MODE_PRIVATE
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import xyz.hereislookingatyoukid.databinding.InputDialogBinding

/**
 *author : caizhixing
 *date : 2019/10/12
 */
class InputDialog : DialogFragment() {

    private lateinit var dataBinding: InputDialogBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dataBinding = DataBindingUtil.inflate(inflater, R.layout.input_dialog, container, true)
        return dataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val sp = view.context.getSharedPreferences(Constant.SP_NAME, MODE_PRIVATE)
        val oldData = sp.getString(Constant.SP_ITEM_NAME, "")
        oldData?.let {
            if(it.isNotEmpty()){
                dataBinding.edit.setText(it)
            }
        }
        dataBinding.okClick = View.OnClickListener {
            val default = getString(R.string.default_package)
            val packageName = with(dataBinding.edit.editableText) {
                if (isNullOrEmpty()) {
                    default
                } else {
                    toString()
                }
            }
            sp.edit().putString(Constant.SP_ITEM_NAME, packageName).apply()
            dismiss()
        }
    }
}