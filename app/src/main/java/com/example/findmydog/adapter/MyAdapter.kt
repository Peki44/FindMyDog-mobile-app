package com.example.findmydog.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.findmydog.model.data.Dog
import com.example.findmydog.R

class MyAdapter(): RecyclerView.Adapter<MyAdapter.MyViewHolder>() {

    private val dogList=ArrayList<Dog>()
    private lateinit var mListener:onItemClickListener

    interface onItemClickListener{
        fun onItemClick(position : Int)
    }
    fun setOnItemClickListener(listener:onItemClickListener){
        mListener=listener
    }
    fun deleteItem(i:Int){
        dogList.removeAt(i)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView=LayoutInflater.from(parent.context).inflate(
            R.layout.dog_item,parent,false
        )
        return MyViewHolder(itemView,mListener)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem=dogList[position]

        val imageTarget=currentItem.profileImageUri
        holder.dogName.text=currentItem.dogsName
        Glide.with(holder.itemView.context).load(imageTarget).into(holder.profilePic)
    }

    override fun getItemCount(): Int {
        return dogList.size
    }

    fun updateDogList(dogList:List<Dog>){
        this.dogList.clear()
        this.dogList.addAll(dogList)
        notifyDataSetChanged()
    }

    class MyViewHolder(itemView : android.view.View, listener:onItemClickListener) : RecyclerView.ViewHolder(itemView){
        val profilePic:ImageView=itemView.findViewById(R.id.listImage)
        val dogName:TextView=itemView.findViewById(R.id.listDogsName)

        init {
            itemView.setOnClickListener {
                listener.onItemClick(adapterPosition)
            }
        }
    }
}