package com.example.androidtbc.data.remote.repository

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.androidtbc.R
import com.example.androidtbc.data.remote.dto.PostDTO
import com.example.androidtbc.databinding.ItemPostBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class PostAdapter : ListAdapter<PostDTO, PostAdapter.PostViewHolder>(PostDiffUtil()) {

    inner class PostViewHolder(private val binding: ItemPostBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: PostDTO) {
            with(binding) {
                tvTitle.text = item.title
                tvComments.text = "${item.comments} Comments"
                tvLikes.text = "${item.likes} Likes"
                tvShare.text = "Share"

                tvUserFullName.text = "${item.owner.firstName} ${item.owner.lastName}".trim()
                tvDateTime.text = formatEpochToDate(item.owner.postDate)

                if (!item.owner.profile.isNullOrEmpty()) {
                    Glide.with(ivUserProfile)
                        .load(item.owner.profile)
                        .placeholder(R.drawable.ic_person)
                        .error(R.drawable.ic_person)
                        .circleCrop()
                        .into(ivUserProfile)
                }

                when {
                    item.images.isNullOrEmpty() -> {
                        imageGridContainer.visibility = View.GONE
                    }
                    item.images.size == 1 -> {
                        imageGridContainer.visibility = View.VISIBLE
                        ivLeftImage.visibility = View.VISIBLE
                        rightImagesColumn.visibility = View.GONE

                        Glide.with(ivLeftImage)
                            .load(item.images[0])
                            .centerCrop()
                            .into(ivLeftImage)
                    }
                    item.images.size == 2 -> {
                        imageGridContainer.visibility = View.VISIBLE
                        ivLeftImage.visibility = View.VISIBLE
                        rightImagesColumn.visibility = View.VISIBLE
                        ivTopRightImage.visibility = View.VISIBLE
                        ivBottomRightImage.visibility = View.GONE

                        Glide.with(ivLeftImage)
                            .load(item.images[0])
                            .centerCrop()
                            .into(ivLeftImage)

                        Glide.with(ivTopRightImage)
                            .load(item.images[1])
                            .centerCrop()
                            .into(ivTopRightImage)
                    }
                    else -> {
                        imageGridContainer.visibility = View.VISIBLE
                        ivLeftImage.visibility = View.VISIBLE
                        rightImagesColumn.visibility = View.VISIBLE
                        ivTopRightImage.visibility = View.VISIBLE
                        ivBottomRightImage.visibility = View.VISIBLE

                        Glide.with(ivLeftImage)
                            .load(item.images[0])
                            .centerCrop()
                            .into(ivLeftImage)

                        Glide.with(ivTopRightImage)
                            .load(item.images[1])
                            .centerCrop()
                            .into(ivTopRightImage)

                        Glide.with(ivBottomRightImage)
                            .load(item.images[2])
                            .centerCrop()
                            .into(ivBottomRightImage)
                    }
                }
            }
        }
    }

    private fun formatEpochToDate(epoch: Long): String {
        return try {
            val date = Date(epoch * 1000)
            val sdf = SimpleDateFormat("d MMMM 'at' h:mm a", Locale.getDefault())
            sdf.format(date)
        } catch (e: Exception) {
            "Invalid Date"
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        return PostViewHolder(
            ItemPostBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

class PostDiffUtil : DiffUtil.ItemCallback<PostDTO>() {
    override fun areItemsTheSame(oldItem: PostDTO, newItem: PostDTO): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: PostDTO, newItem: PostDTO): Boolean {
        return oldItem == newItem
    }
}