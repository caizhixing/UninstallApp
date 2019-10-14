package xyz.hereislookingatyoukid

import android.content.pm.PackageManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import xyz.hereislookingatyoukid.databinding.ItemAppBinding

/**
 *author : caizhixing
 *date : 2019/10/12
 */
class AppItemAdapter : ListAdapter<AppInfo, AppItemAdapter.ImageViewHolder>(AppDiffCallback()) {

    private lateinit var pm:PackageManager
    private var listener:ItemLongClick? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        pm = parent.context.packageManager
        return ImageViewHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.item_app,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ImageViewHolder(private val binding: ItemAppBinding) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            binding.clickListener = View.OnClickListener {
            }
        }
        fun bind(appInfo: AppInfo) {
            val drawable = pm.getApplicationIcon(appInfo.packageName)
            Glide.with(binding.appIcon).load(drawable).into(binding.appIcon)
            binding.appIcon.setImageDrawable(drawable)
            binding.appName.text = appInfo.appName
            binding.appPackage.text = appInfo.packageName
            binding.wrapper.setOnLongClickListener {
                listener?.let {
                    listener!!.click(appInfo)
                }
                true
            }
        }
    }

    fun setListener(listener:ItemLongClick){
        this.listener = listener
    }

}

interface ItemLongClick{
    fun click(appInfo: AppInfo)
}

private class AppDiffCallback : DiffUtil.ItemCallback<AppInfo>() {

    override fun areItemsTheSame(oldItem: AppInfo, newItem: AppInfo): Boolean {
        return oldItem.packageName == newItem.packageName
    }

    override fun areContentsTheSame(oldItem: AppInfo, newItem: AppInfo): Boolean {
        return oldItem.packageName == newItem.packageName
    }
}