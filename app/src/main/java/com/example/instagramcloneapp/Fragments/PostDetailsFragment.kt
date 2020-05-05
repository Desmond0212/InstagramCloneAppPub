package com.example.instagramcloneapp.Fragments

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.instagramcloneapp.Adapter.PostAdapter
import com.example.instagramcloneapp.Model.PostModel

import com.example.instagramcloneapp.R
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.fragment_home.view.*

/**
 * A simple [Fragment] subclass.
 */
class PostDetailsFragment : Fragment()
{
    private var postAdapter: PostAdapter? = null
    private var postList: MutableList<PostModel>? = null
    private var postId: String = ""

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
    {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_post_details, container, false)

        val preferences = context?.getSharedPreferences("PREFS", Context.MODE_PRIVATE)

        if (preferences != null)
        {
            postId = preferences.getString("postId", "none")!!
        }

        val recyclerView: RecyclerView
        recyclerView = view.findViewById(R.id.recycler_view_post_details)
        recyclerView.setHasFixedSize(true)

        val linearLayoutManager = LinearLayoutManager(context)
        recyclerView.layoutManager = linearLayoutManager

        postList = ArrayList()
        postAdapter = context?.let {PostAdapter(it, postList as ArrayList<PostModel>)}
        recyclerView.adapter = postAdapter

        retrievePosts()

        return view
    }

    private fun retrievePosts()
    {
        val postsRef = FirebaseDatabase.getInstance().reference.child("Posts").child(postId)

        postsRef.addValueEventListener(object : ValueEventListener
        {
            override fun onDataChange(p0: DataSnapshot)
            {
                postList?.clear()
                val post = p0.getValue(PostModel::class.java)
                postList!!.add(post!!)
                postAdapter!!.notifyDataSetChanged()
            }

            override fun onCancelled(p0: DatabaseError) {}
        })
    }
}
