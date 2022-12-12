package com.sjaindl.travelcompanion.explore

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.sjaindl.travelcompanion.R
import com.sjaindl.travelcompanion.databinding.ViewholderSearchplaceItemBinding
import kotlin.reflect.KClass

class SearchPlaceAdapter(private val onClick: (SearchPlaceViewHolderType.Item) -> Unit) :
    ListAdapter<SearchPlaceViewHolderType, ViewHolder>(SearchPlaceDiffUtilCallback()) {

    private val viewTypes = listOf<KClass<*>>(
        SearchPlaceViewHolderType.Item::class
    )

    override fun getItemViewType(position: Int): Int {
        val item = currentList.getOrNull(position) ?: return -1
        return viewTypes.indexOf(item::class)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        when (viewTypes.getOrNull(viewType)) {
            SearchPlaceViewHolderType.Item::class -> {
                val binding = DataBindingUtil.inflate<ViewholderSearchplaceItemBinding>(
                    LayoutInflater.from(parent.context),
                    R.layout.viewholder_searchplace_item,
                    parent,
                    false
                )

                return SearchPlaceItemViewHolder(binding, onClick)
            }
            else -> throw IllegalStateException("Unsupported viewType")
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val current = currentList.getOrNull(position)

        when (holder) {
            is SearchPlaceItemViewHolder -> {
                val item = current as? SearchPlaceViewHolderType.Item
                holder.configure(item)
            }
        }
    }
}

sealed class SearchPlaceViewHolderType {
    class Item(val description: String) : SearchPlaceViewHolderType()
}

class SearchPlaceDiffUtilCallback : DiffUtil.ItemCallback<SearchPlaceViewHolderType>() {
    override fun areItemsTheSame(oldItem: SearchPlaceViewHolderType, newItem: SearchPlaceViewHolderType): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: SearchPlaceViewHolderType, newItem: SearchPlaceViewHolderType): Boolean {
        when (oldItem) {
            is SearchPlaceViewHolderType.Item -> return newItem is SearchPlaceViewHolderType.Item && oldItem.description == newItem.description
        }
    }
}

class SearchPlaceItemViewHolder(
    val binding: ViewholderSearchplaceItemBinding,
    onClick: (SearchPlaceViewHolderType.Item) -> Unit,
    private var item: SearchPlaceViewHolderType.Item? = null
) : ViewHolder(binding.root) {
    init {
        binding.viewholderSearchplaceText.setOnClickListener {
            item?.let {
                onClick(it)
            }
        }
    }

    fun configure(item: SearchPlaceViewHolderType.Item?) {
        binding.viewholderSearchplaceText.text = item?.description
        this.item = item
        binding.executePendingBindings()
    }
}
