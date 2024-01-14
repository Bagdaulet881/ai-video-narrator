package kz.narrator.app.ui.bottom_sheet

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import kz.narrator.app.R
import kz.narrator.app.data.models.ai_data.LoggingState
import kz.narrator.app.data.models.ai_data.ScriptAudioSettings
import kz.narrator.app.databinding.AdapterLoggingStateBinding
import kz.narrator.app.databinding.AdapterSelectScriptBinding
import kz.narrator.app.ui_common.callbacks.RecyclerViewItemClickCallback

class LoggingAdapter(
    private val recyclerViewItemClickCallback: RecyclerViewItemClickCallback,
    private val requireContext: Context
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val diffCallback = object : DiffUtil.ItemCallback<Any>() {

        override fun areItemsTheSame(oldItem: Any, newItem: Any): Boolean {
            return when {
                oldItem is LoggingState && newItem is LoggingState -> {
                    oldItem.id == newItem.id
                }
                oldItem is ScriptAudioSettings && newItem is ScriptAudioSettings -> {
                    oldItem.name == newItem.name
                }
                else -> {
                    false
                }
            }
        }

        override fun areContentsTheSame(oldItem: Any, newItem: Any): Boolean {
            return when {
                oldItem is LoggingState && newItem is LoggingState -> {
                    oldItem as LoggingState == newItem as LoggingState
                }
                oldItem is ScriptAudioSettings && newItem is ScriptAudioSettings -> {
                    oldItem as ScriptAudioSettings == newItem as ScriptAudioSettings
                }
                else -> {
                    true
                }
            }
        }
    }

    private val differ = AsyncListDiffer(this, diffCallback)


    override fun getItemCount(): Int = differ.currentList.size

    fun submitList(list: List<Any>) {
        differ.submitList(list)
    }

    private val VIEW_TYPE_TITLE_VALUE = 0
    private val VIEW_TYPE_SCRIPTS = 1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            VIEW_TYPE_TITLE_VALUE -> {
                val binding: AdapterLoggingStateBinding =
                    DataBindingUtil.inflate(
                        inflater,
                        R.layout.adapter_logging_state,
                        parent,
                        false
                    )
                LoggingStateViewHolder(binding)
            }
            VIEW_TYPE_SCRIPTS -> {
                val binding: AdapterSelectScriptBinding =
                    DataBindingUtil.inflate(
                        inflater,
                        R.layout.adapter_select_script,
                        parent,
                        false
                    )
                ScriptInfoViewHolder(binding)
            }
            else -> {
                throw IllegalStateException("Incorrect ViewType found")
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder.itemViewType) {
            VIEW_TYPE_TITLE_VALUE -> {
                var viewHolder = holder as LoggingStateViewHolder
                viewHolder.initContent(differ.currentList[position] as LoggingState)
            }
            VIEW_TYPE_SCRIPTS -> {
                var viewHolder = holder as ScriptInfoViewHolder
                viewHolder.initContent(differ.currentList[position] as ScriptAudioSettings)
            }
        }
    }

    inner class LoggingStateViewHolder(var binding: AdapterLoggingStateBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun initContent(item: LoggingState) {
            binding.item = item
            binding.executePendingBindings()

            val color = ContextCompat.getColor(requireContext, item.getTextColor())
            binding.tvStateMsg.setTextColor(color)
            binding.tvStatus.setTextColor(color)
            binding.tvStatus.setText(item.getStatusMsg())
        }
    }

    inner class ScriptInfoViewHolder(var binding: AdapterSelectScriptBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun initContent(item: ScriptAudioSettings) {
            binding.item = item
            binding.recyclerViewItemClickCallback = recyclerViewItemClickCallback
            binding.executePendingBindings()
        }
    }

    override fun getItemViewType(position: Int): Int =
        when (differ.currentList[position]) {
            is LoggingState -> VIEW_TYPE_TITLE_VALUE
            is ScriptAudioSettings -> VIEW_TYPE_SCRIPTS
            else -> throw IllegalStateException("Incorrect ViewType found")
        }
}