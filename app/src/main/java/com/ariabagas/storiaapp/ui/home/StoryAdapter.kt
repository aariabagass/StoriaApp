package com.ariabagas.storiaapp.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.ariabagas.storiaapp.R
import com.ariabagas.storiaapp.core.model.Story
import com.ariabagas.storiaapp.databinding.ItemStoryBinding
import com.ariabagas.storiaapp.utils.TimeUtils
import com.bumptech.glide.Glide

class StoryAdapter :
    PagingDataAdapter<Story, StoryAdapter.StoryViewHolder>(DIFF_CALLBACK) {

    var onItemClick: ((Story, View, View, View) -> Unit)? = null

    class StoryViewHolder(val binding: ItemStoryBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(story: Story, onItemClick: ((Story, View, View, View) -> Unit)?) {
            binding.tvName.text = story.name
            binding.tvDesc.text = story.description
            binding.tvTime.text = TimeUtils.getTimeAgo(story.createdAt)

            Glide.with(binding.root.context)
                .load(story.photoUrl)
                .placeholder(R.drawable.ic_person)
                .into(binding.ivStory)

            ViewCompat.setTransitionName(binding.ivStory, "photo_${story.id}")
            ViewCompat.setTransitionName(binding.tvName, "name_${story.id}")
            ViewCompat.setTransitionName(binding.tvDesc, "desc_${story.id}")

            binding.root.setOnClickListener {
                onItemClick?.invoke(story, binding.ivStory, binding.tvName, binding.tvDesc)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoryViewHolder {
        val binding =
            ItemStoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return StoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: StoryViewHolder, position: Int) {
        val story = getItem(position)
        story?.let {
            holder.bind(it, onItemClick)
        }
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Story>() {
            override fun areItemsTheSame(oldItem: Story, newItem: Story) = oldItem.id == newItem.id
            override fun areContentsTheSame(oldItem: Story, newItem: Story) = oldItem == newItem
        }
    }
}
