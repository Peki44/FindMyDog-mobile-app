package com.example.findmydog.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.findmydog.adapter.MyAdapter
import com.example.findmydog.ui.activity.DetailsActivity
import com.example.findmydog.model.data.Dog
import com.example.findmydog.viewmodel.DogViewModel
import com.example.findmydog.R
import com.example.findmydog.adapter.SwipeGesture

private lateinit var viewModel: DogViewModel
private lateinit var userRecyclerView: RecyclerView
lateinit var adapter: MyAdapter
private lateinit var dogList: List<Dog>

class DogsFragment : Fragment() {

    companion object {
        fun newInstance(): DogsFragment {
            return DogsFragment()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_dogs, container, false)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        userRecyclerView = view.findViewById(R.id.dogRecyclerView)
        userRecyclerView.layoutManager = LinearLayoutManager(context)
        userRecyclerView.setHasFixedSize(true)
        adapter = MyAdapter()
        userRecyclerView.adapter= adapter

        viewModel = ViewModelProvider(this)[DogViewModel::class.java]

        viewModel.allDog.observe(viewLifecycleOwner,Observer{
            adapter.updateDogList(it)
            dogList = it
        })

        val swipeGesture=object : SwipeGesture(this@DogsFragment.context) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                when(direction){
                    ItemTouchHelper.LEFT->{
                        viewModel.deleteDog(viewHolder.adapterPosition)
                        adapter.deleteItem(viewHolder.adapterPosition)
                    }
                }
            }

        }
        val touchHelper=ItemTouchHelper(swipeGesture)
        touchHelper.attachToRecyclerView(userRecyclerView)

        adapter.setOnItemClickListener(object : MyAdapter.onItemClickListener {
            override fun onItemClick(position: Int) {

                val intent= Intent(this@DogsFragment.context, DetailsActivity::class.java)
                intent.putExtra("dogsName", dogList[position].dogsName)
                intent.putExtra("breed", dogList[position].breed)
                intent.putExtra("owner", dogList[position].owner)
                intent.putExtra("phone",dogList[position].phone)
                intent.putExtra("email", dogList[position].email)
                intent.putExtra("address", dogList[position].address)
                intent.putExtra("profileImageUri", dogList[position].profileImageUri)
                intent.putExtra("qrCodeImage", dogList[position].qrCodeImage)
                startActivity(intent)
            }
        })
    }

}