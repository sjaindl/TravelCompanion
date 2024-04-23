package com.sjaindl.travelcompanion.explore.search

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.sjaindl.travelcompanion.R
import com.sjaindl.travelcompanion.api.google.GeocodingResult
import com.sjaindl.travelcompanion.api.google.PlacePrediction
import com.sjaindl.travelcompanion.databinding.ViewholderSearchplaceItemBinding
import kotlin.reflect.KClass

class SearchPlaceAdapter(private val onClick: (SearchPlaceViewHolderType) -> Unit) :
    ListAdapter<SearchPlaceViewHolderType, ViewHolder>(SearchPlaceDiffUtilCallback()) {

    private val viewTypes = listOf<KClass<*>>(
        SearchPlaceViewHolderType.PlacesPredictionItem::class,
        SearchPlaceViewHolderType.PlaceItem::class,
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

            SearchPlaceViewHolderType.PlaceItem::class -> {
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
                (current as? SearchPlaceViewHolderType.PlaceItem)?.let {
                    holder.configure(it)
                }
            }
        }
    }
}

sealed class SearchPlaceViewHolderType {
    class PlacesPredictionItem(val placePrediction: PlacePrediction) : SearchPlaceViewHolderType()

    class PlaceItem(val geocoded: GeocodingResult) : SearchPlaceViewHolderType()
}

class SearchPlaceDiffUtilCallback : DiffUtil.ItemCallback<SearchPlaceViewHolderType>() {
    override fun areItemsTheSame(oldItem: SearchPlaceViewHolderType, newItem: SearchPlaceViewHolderType): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: SearchPlaceViewHolderType, newItem: SearchPlaceViewHolderType): Boolean {
        return when (oldItem) {
            is SearchPlaceViewHolderType.PlacesPredictionItem -> newItem is SearchPlaceViewHolderType.PlacesPredictionItem && oldItem.placePrediction == newItem.placePrediction
            is SearchPlaceViewHolderType.PlaceItem -> newItem is SearchPlaceViewHolderType.PlaceItem && oldItem.geocoded == newItem.geocoded
        }
    }
}

class SearchPlaceItemViewHolder(
    val binding: ViewholderSearchplaceItemBinding,
    onClick: (SearchPlaceViewHolderType) -> Unit,
    private var item: SearchPlaceViewHolderType? = null

) : ViewHolder(binding.root) {
    init {
        binding.viewholderSearchplaceText.setOnClickListener {
            item?.let {
                onClick(it)
            }
        }
    }

    fun configure(item: SearchPlaceViewHolderType?) {
        when (item) {
            is SearchPlaceViewHolderType.PlaceItem -> binding.viewholderSearchplaceText.text = item.geocoded.formattedAddress
            is SearchPlaceViewHolderType.PlacesPredictionItem -> binding.viewholderSearchplaceText.text =
                item.placePrediction.description?.text

            null -> binding.viewholderSearchplaceText.text = null
        }

        this.item = item
        binding.executePendingBindings()
    }
}
