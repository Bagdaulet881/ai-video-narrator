package kz.narrator.app.ui.main_page

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import kz.narrator.app.R
import kz.narrator.app.data.models.ai_data.SettingsColoredValue
import kz.narrator.app.data.models.ai_data.SettingsTitleValue
import kz.narrator.app.databinding.AdapterSettingColoredValueBinding
import kz.narrator.app.databinding.AdapterSettingTitleValueBinding
import kz.narrator.app.ui_common.callbacks.RecyclerViewItemClickCallback

class SettingsAdapter (
    private val context: Context,
    private val recyclerViewItemClickCallback: RecyclerViewItemClickCallback
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val diffCallback = object : DiffUtil.ItemCallback<Any>() {

        override fun areItemsTheSame(oldItem: Any, newItem: Any): Boolean {
            return when {
                oldItem is SettingsTitleValue && newItem is SettingsTitleValue -> {
                    oldItem.title == newItem.title
                }
                oldItem is SettingsColoredValue && newItem is SettingsColoredValue -> {
                    oldItem.title == newItem.title
                }
                else -> {
                    false
                }
            }
        }

        override fun areContentsTheSame(oldItem: Any, newItem: Any): Boolean {
            return when {
                oldItem is SettingsTitleValue && newItem is SettingsTitleValue -> {
                    oldItem as SettingsTitleValue == newItem as SettingsTitleValue
                }
                oldItem is SettingsColoredValue && newItem is SettingsColoredValue -> {
                    oldItem as SettingsColoredValue == newItem as SettingsColoredValue
                }
                else -> {
                    false
                }
            }
        }
    }

    private val differ = AsyncListDiffer(this, diffCallback)

    override fun getItemCount(): Int = differ.currentList.size

    fun submitList(list: List<Any>) {
        differ.submitList(list)
    }

    companion object {
        private const val VIEW_TYPE_TEXT = 0
        private const val VIEW_TYPE_COLORED = 1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            VIEW_TYPE_TEXT -> {
                val binding: AdapterSettingTitleValueBinding =
                    DataBindingUtil.inflate(
                        inflater,
                        R.layout.adapter_setting_title_value,
                        parent,
                        false
                    )
                SettingsTitleValueViewHolder(binding)
            }
            VIEW_TYPE_COLORED -> {
                val binding: AdapterSettingColoredValueBinding =
                    DataBindingUtil.inflate(
                        inflater,
                        R.layout.adapter_setting_colored_value,
                        parent,
                        false
                    )
                SettingsColoredViewHolder(binding)
            }
            else -> {
                throw IllegalStateException("Incorrect ViewType found")
            }
        }
    }


    inner class SettingsTitleValueViewHolder(var binding: AdapterSettingTitleValueBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun initContent(data: SettingsTitleValue) {
            binding.item = data
            binding.recyclerViewItemClickCallback = recyclerViewItemClickCallback
            binding.executePendingBindings()
        }
    }

    inner class SettingsColoredViewHolder(var binding: AdapterSettingColoredValueBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun initContent(data: SettingsColoredValue) {
            binding.item = data
            binding.recyclerViewItemClickCallback = recyclerViewItemClickCallback
            binding.executePendingBindings()

            binding.ivColor.setBackgroundColor(Color.parseColor(data.value))
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder.itemViewType) {
            VIEW_TYPE_TEXT -> {
                val viewHolder = holder as SettingsTitleValueViewHolder
                viewHolder.initContent(differ.currentList[position] as SettingsTitleValue)
            }
            VIEW_TYPE_COLORED -> {
                val viewHolder = holder as SettingsColoredViewHolder
                viewHolder.initContent(differ.currentList[position] as SettingsColoredValue)
            }
        }
    }

    override fun getItemViewType(position: Int): Int =
        when (differ.currentList[position]) {
            is SettingsTitleValue -> VIEW_TYPE_TEXT
            is SettingsColoredValue -> VIEW_TYPE_COLORED
            else -> throw IllegalStateException("Incorrect ViewType found")
        }

}

