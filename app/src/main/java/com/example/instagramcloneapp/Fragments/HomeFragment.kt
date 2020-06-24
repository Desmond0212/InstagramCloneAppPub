package com.example.instagramcloneapp.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.instagramcloneapp.Adapter.PostAdapter
import com.example.instagramcloneapp.Adapter.StoryAdapter
import com.example.instagramcloneapp.Model.PostModel
import com.example.instagramcloneapp.Model.StoryModel
import com.example.instagramcloneapp.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_sign_up.*
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.fragment_home.view.*

/**
 * A simple [Fragment] subclass.
 */
class HomeFragment : Fragment()
{
    private var postAdapter: PostAdapter? = null
    private var postList: MutableList<PostModel>? = null
    private var followingList: MutableList<String>? = null
    private var storyAdapter: StoryAdapter? = null
    private var storyList:MutableList<StoryModel>? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
    {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        //Recycler View Home
        val recyclerView: RecyclerView?
        recyclerView = view.findViewById(R.id.recycler_view_home)
        val linearLayoutManager = LinearLayoutManager(context)
        linearLayoutManager.reverseLayout = true
        linearLayoutManager.stackFromEnd = true
        recyclerView.layoutManager = linearLayoutManager

        postList = ArrayList()
        postAdapter = context?.let {PostAdapter(it, postList as ArrayList<PostModel>)}
        recyclerView.adapter = postAdapter
        recyclerView.setHasFixedSize(true)

        //Recycler View Story
        val recyclerViewStory: RecyclerView?
        recyclerViewStory = view.findViewById(R.id.recycler_view_story)
        val linearLayoutManager2 = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false) // To set scroll with Horizontal
        recyclerViewStory.layoutManager = linearLayoutManager2

        storyList = ArrayList()
        storyAdapter = context?.let { StoryAdapter(it, storyList as ArrayList<StoryModel>) }
        recyclerViewStory.adapter = storyAdapter

        checkFollowings()

        return view
    }

    private fun checkFollowings()
    {
        followingList = ArrayList()

        val followingRef = FirebaseDatabase.getInstance().reference
                .child("Follow").child(FirebaseAuth.getInstance().currentUser!!.uid)
                .child("Following")

        followingRef.addValueEventListener(object : ValueEventListener
        {
            override fun onDataChange(p0: DataSnapshot)
            {
                if (p0.exists())
                {
                    (followingList as ArrayList<String>).clear()

                    for (snapshot in p0.children)
                    {
                        snapshot.key?.let {(followingList as ArrayList<String>).add(it)}
                    }

                    retrievePosts()
                    retrieveStories()
                }
            }

            override fun onCancelled(p0: DatabaseError) {}
        })
    }

    private fun retrievePosts()
    {
        val postsRef = FirebaseDatabase.getInstance().reference.child("Posts")

        postsRef.addValueEventListener(object : ValueEventListener
        {
            override fun onDataChange(p0: DataSnapshot)
            {
                postList?.clear()

                for (snapshot in p0.children)
                {
                    val post = snapshot.getValue(PostModel::class.java)

                    for (id in (followingList as ArrayList<String>))
                    {
                        if (post!!.getPublisher() == id)
                        {
                            postList!!.add(post)
                        }

                        postAdapter!!.notifyDataSetChanged()
                    }
                }

                if (postList.isNullOrEmpty())
                {
                    startAnimation(true)
                    view?.empty_post?.visibility = View.VISIBLE
                    view?.recycler_view_home?.visibility = View.GONE
                }
                else
                {
                    startAnimation(false)
                    view?.empty_post?.visibility = View.GONE
                    view?.recycler_view_home?.visibility = View.VISIBLE
                }
            }

            override fun onCancelled(p0: DatabaseError) {}
        })
    }

    private fun startAnimation(isStartAnim: Boolean)
    {
        if(isStartAnim)
        {
            animLoadingView_home.visibility = View.VISIBLE
            animLoadingView_home.setAnimation("13525-empty.json")
            animLoadingView_home.playAnimation()
            animLoadingView_home.loop(true)
        }
        else
        {
            animLoadingView_home.visibility = View.GONE
            animLoadingView_home.cancelAnimation()
        }
    }

    private fun retrieveStories()
    {
        val storyRef = FirebaseDatabase.getInstance().reference.child("Story")

        storyRef.addValueEventListener(object : ValueEventListener
        {
            override fun onDataChange(p0: DataSnapshot)
            {
                val currentTime = System.currentTimeMillis()

                (storyList as ArrayList<StoryModel>).clear()

                (storyList as ArrayList<StoryModel>).add(StoryModel("", 0, 0, "", FirebaseAuth.getInstance().currentUser?.uid))

                for (id in followingList!!)
                {
                    var countStory = 0
                    var story: StoryModel? = null

                    for (snapshot in p0.child(id).children)
                    {
                        story = snapshot.getValue(StoryModel::class.java)

                        if (currentTime > story!!.getTimeStart()!! && currentTime < story.getTimeEnd()!!)
                        {
                            countStory++
                        }
                    }

                    if (countStory > 0)
                    {
                        (storyList as ArrayList<StoryModel>).add(story!!)
                    }
                }

                storyAdapter?.notifyDataSetChanged()
            }

            override fun onCancelled(p0: DatabaseError) {}
        })
    }
}
