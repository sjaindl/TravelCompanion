package com.sjaindl.travelcompanion.plan.detail.addplace

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.sjaindl.travelcompanion.R
import com.sjaindl.travelcompanion.api.google.GooglePlace
import com.sjaindl.travelcompanion.databinding.ViewholderSearchplaceItemBinding
import kotlin.reflect.KClass

class SearchPlacesAdapter(private val onClick: (SearchPlacesViewHolderType.GooglePlaceItem) -> Unit) :
    ListAdapter<SearchPlacesViewHolderType, ViewHolder>(SearchPlaceDiffUtilCallback()) {

    private val viewTypes = listOf<KClass<*>>(
        SearchPlacesViewHolderType.GooglePlaceItem::class,
    )

    override fun getItemViewType(position: Int): Int {
        val item = currentList.getOrNull(position) ?: return -1
        return viewTypes.indexOf(item::class)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        when (viewTypes.getOrNull(viewType)) {
            SearchPlacesViewHolderType.GooglePlaceItem::class -> {
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
                (current as? SearchPlacesViewHolderType.GooglePlaceItem)?.let {
                    holder.configure(it)
                }
            }
        }
    }
}

sealed class SearchPlacesViewHolderType {
    class GooglePlaceItem(val places: GooglePlace) : SearchPlacesViewHolderType()
}

class SearchPlaceDiffUtilCallback : DiffUtil.ItemCallback<SearchPlacesViewHolderType>() {
    override fun areItemsTheSame(oldItem: SearchPlacesViewHolderType, newItem: SearchPlacesViewHolderType): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: SearchPlacesViewHolderType, newItem: SearchPlacesViewHolderType): Boolean {
        return when (oldItem) {
            is SearchPlacesViewHolderType.GooglePlaceItem -> newItem is SearchPlacesViewHolderType.GooglePlaceItem && oldItem.places == newItem.places
        }
    }
}

class SearchPlaceItemViewHolder(
    val binding: ViewholderSearchplaceItemBinding,
    onClick: (SearchPlacesViewHolderType.GooglePlaceItem) -> Unit,
    private var item: SearchPlacesViewHolderType.GooglePlaceItem? = null

) : ViewHolder(binding.root) {
    init {
        binding.viewholderSearchplaceText.setOnClickListener {
            item?.let {
                onClick(it)
            }
        }
    }

    fun configure(item: SearchPlacesViewHolderType.GooglePlaceItem?) {
        binding.viewholderSearchplaceText.text = item?.places?.name
        this.item = item
        binding.executePendingBindings()
    }
}
