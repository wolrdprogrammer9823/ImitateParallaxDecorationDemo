package com.wolfsea.imitateparallaxdecorationdemo

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.parallax_rv_item.view.*

/**
 *@desc  Parallax适配器
 *@author liuliheng
 *@time 2021/1/25  23:40
 **/
class ParallaxAdapter : RecyclerView.Adapter<ParallaxAdapter.ParallaxViewHolder>() {

    class ParallaxViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindData(content: String) {
            itemView.apply {
                parallax_rv_item_tv.text = content
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ParallaxViewHolder =
        ParallaxViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.parallax_rv_item, parent, false)
        )

    override fun onBindViewHolder(holder: ParallaxViewHolder, position: Int) {
        holder.bindData("Item-$position")
    }

    override fun getItemCount(): Int = 100
}