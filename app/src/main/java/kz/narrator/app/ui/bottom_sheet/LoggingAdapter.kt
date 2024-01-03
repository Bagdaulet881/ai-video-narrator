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
import kz.narrator.app.databinding.AdapterLoggingStateBinding

class LoggingAdapter(
    private val requireContext: Context
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val diffCallback = object : DiffUtil.ItemCallback<Any>() {

        override fun areItemsTheSame(oldItem: Any, newItem: Any): Boolean {
            return when {
                oldItem is LoggingState && newItem is LoggingState -> {
                    oldItem.id == newItem.id
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

    override fun getItemViewType(position: Int): Int =
        when (differ.currentList[position]) {
            is LoggingState -> VIEW_TYPE_TITLE_VALUE
            else -> throw IllegalStateException("Incorrect ViewType found")
        }
}