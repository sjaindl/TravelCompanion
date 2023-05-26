package com.sjaindl.travelcompanion.explore.search

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.sjaindl.travelcompanion.R
import com.sjaindl.travelcompanion.api.google.PlacesPredictions
import com.sjaindl.travelcompanion.databinding.ViewholderSearchplaceItemBinding
import kotlin.reflect.KClass

class SearchPlaceAdapter(private val onClick: (SearchPlaceViewHolderType.PlacesPredictionItem) -> Unit) :
    ListAdapter<SearchPlaceViewHolderType, ViewHolder>(SearchPlaceDiffUtilCallback()) {

    private val viewTypes = listOf<KClass<*>>(
        SearchPlaceViewHolderType.PlacesPredictionItem::class,
    )

    override fun getItemViewType(position: Int): Int {
        val item = currentList.getOrNull(position) ?: return -1
        return viewTypes.indexOf(item::class)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        when (viewTypes.getOrNull(viewType)) {
            SearchPlaceViewHolderType.PlacesPredictionItem::class -> {
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
                (current as? SearchPlaceViewHolderType.PlacesPredictionItem)?.let {
                    holder.configure(it)
                }
            }
        }
    }
}

sealed class SearchPlaceViewHolderType {
    class PlacesPredictionItem(val placesPredictions: PlacesPredictions) : SearchPlaceViewHolderType()
}

class SearchPlaceDiffUtilCallback : DiffUtil.ItemCallback<SearchPlaceViewHolderType>() {
    override fun areItemsTheSame(oldItem: SearchPlaceViewHolderType, newItem: SearchPlaceViewHolderType): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: SearchPlaceViewHolderType, newItem: SearchPlaceViewHolderType): Boolean {
        return when (oldItem) {
            is SearchPlaceViewHolderType.PlacesPredictionItem -> newItem is SearchPlaceViewHolderType.PlacesPredictionItem && oldItem.placesPredictions == newItem.placesPredictions
        }
    }
}

class SearchPlaceItemViewHolder(
    val binding: ViewholderSearchplaceItemBinding,
    onClick: (SearchPlaceViewHolderType.PlacesPredictionItem) -> Unit,
    private var item: SearchPlaceViewHolderType.PlacesPredictionItem? = null

) : ViewHolder(binding.root) {
    init {
        binding.viewholderSearchplaceText.setOnClickListener {
            item?.let {
                onClick(it)
            }
        }
    }

    fun configure(item: SearchPlaceViewHolderType.PlacesPredictionItem?) {
        binding.viewholderSearchplaceText.text = item?.placesPredictions?.description
        this.item = item
        binding.executePendingBindings()
    }
}
