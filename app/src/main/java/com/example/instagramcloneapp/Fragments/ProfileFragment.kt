package com.example.instagramcloneapp.Fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.instagramcloneapp.AccountSettingsActivity
import com.example.instagramcloneapp.Adapter.MyImagesAdapter
import com.example.instagramcloneapp.Model.PostModel
import com.example.instagramcloneapp.Model.UserModel
import com.example.instagramcloneapp.R
import com.example.instagramcloneapp.ShowUsersActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.android.synthetic.main.fragment_profile.view.*
import java.util.*
import java.util.Collections.reverse
import kotlin.collections.ArrayList

/**
 * A simple [Fragment] subclass.
 */
class ProfileFragment : Fragment()
{
    private lateinit var profileId: String
    private lateinit var firebaseUser: FirebaseUser
    var postList: List<PostModel>? = null
    var myImagesAdapter: MyImagesAdapter? = null
    var postListSaved: List<PostModel>? = null
    var myImagesAdapterSavedImg: MyImagesAdapter? = null
    var mySavedImg: List<String>? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
    {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        firebaseUser = FirebaseAuth.getInstance().currentUser!!

        val pref = context?.getSharedPreferences("PREFS", Context.MODE_PRIVATE)
        if (pref != null)
        {
            this.profileId = pref.getString("profileId", "none").toString()
        }

        if (profileId == firebaseUser.uid)
        {
            view.edit_account_settings_btn.text = getString(R.string.PROFILE_FRAGMENT_LBL_EDIT_PROFILE)
        }
        else if (profileId != firebaseUser.uid)
        {
            checkFollowAndFollowingButtonStatus()
        }

        //***********
        //Recycler View for Uploaded Images
        //***********
        val recyclerViewUploadImages: RecyclerView
        recyclerViewUploadImages = view.findViewById(R.id.recycler_view_upload_pic)
        recyclerViewUploadImages.setHasFixedSize(true)
        val linearLayoutManager: LinearLayoutManager = GridLayoutManager(context, 3)
        recyclerViewUploadImages.layoutManager = linearLayoutManager

        postList = ArrayList()
        myImagesAdapter = context?.let { MyImagesAdapter(it, postList as ArrayList<PostModel>) }
        recyclerViewUploadImages.adapter = myImagesAdapter
        //***********
        //Recycler View for Uploaded Images
        //***********

        //***********
        //Recycler View for Saved Images
        //***********
        val recyclerViewSaveImages: RecyclerView
        recyclerViewSaveImages = view.findViewById(R.id.recycler_view_saved_pic)
        recyclerViewSaveImages.setHasFixedSize(true)
        val linearLayoutManager2: LinearLayoutManager = GridLayoutManager(context, 3)
        recyclerViewSaveImages.layoutManager = linearLayoutManager2

        postListSaved = ArrayList()
        myImagesAdapterSavedImg = context?.let { MyImagesAdapter(it, postListSaved as ArrayList<PostModel>) }
        recyclerViewSaveImages.adapter = myImagesAdapterSavedImg
        //***********
        //Recycler View for Saved Images
        //***********

        recyclerViewSaveImages.visibility = View.GONE
        recyclerViewUploadImages.visibility = View.VISIBLE

        val uploadImagesBtn: ImageButton
        val savedImagesBtn: ImageButton
        uploadImagesBtn = view.findViewById(R.id.images_grid_view_btn)
        savedImagesBtn = view.findViewById(R.id.images_save_btn)

        uploadImagesBtn.setOnClickListener {
            uploadImagesBtn.setColorFilter(resources.getColor(R.color.blackColor))
            savedImagesBtn.setColorFilter(resources.getColor(R.color.colorBlack))
            recyclerViewSaveImages.visibility = View.GONE
            recyclerViewUploadImages.visibility = View.VISIBLE
        }

        savedImagesBtn.setOnClickListener {
            uploadImagesBtn.setColorFilter(resources.getColor(R.color.colorBlack))
            savedImagesBtn.setColorFilter(resources.getColor(R.color.blackColor))
            recyclerViewSaveImages.visibility = View.VISIBLE
            recyclerViewUploadImages.visibility = View.GONE
        }

        view.layout_followers_profile.setOnClickListener {
            val intent = Intent(context, ShowUsersActivity::class.java)
            intent.putExtra("id", profileId)
            intent.putExtra("title", "followers")
            startActivity(intent)
        }

        view.layout_following_profile.setOnClickListener {
            val intent = Intent(context, ShowUsersActivity::class.java)
            intent.putExtra("id", profileId)
            intent.putExtra("title", "following")
            startActivity(intent)
        }

        view.edit_account_settings_btn.setOnClickListener{

            when (view.edit_account_settings_btn.text.toString())
            {
                "Edit Profile" ->
                {
                    startActivity(Intent(context, AccountSettingsActivity::class.java))
                }

                "Follow" ->
                {
                    firebaseUser.uid.let {
                        FirebaseDatabase.getInstance().reference
                            .child("Follow").child(it)
                            .child("Following").child(profileId)
                            .setValue(true)
                    }

                    firebaseUser.uid.let {
                        FirebaseDatabase.getInstance().reference
                            .child("Follow").child(profileId)
                            .child("Followers").child(it)
                            .setValue(true)
                    }

                    addNotification()
                }

                "Following" ->
                {
                    firebaseUser.uid.let {
                        FirebaseDatabase.getInstance().reference
                            .child("Follow").child(it)
                            .child("Following").child(profileId)
                            .removeValue()
                    }

                    firebaseUser.uid.let {
                        FirebaseDatabase.getInstance().reference
                            .child("Follow").child(profileId)
                            .child("Followers").child(it)
                            .removeValue()
                    }
                }
            }
        }

        getFollowers()
        getFollowings()
        userInfo()
        myPhotos()
        getTotalNumberOfPhotos()
        mySaves()

        return view
    }

    override fun onStop()
    {
        super.onStop()

        val pref = context?.getSharedPreferences("PREFS", Context.MODE_PRIVATE)?.edit()
        pref?.putString("profileId", firebaseUser.uid)
        pref?.apply()
    }

    override fun onPause()
    {
        super.onPause()

        val pref = context?.getSharedPreferences("PREFS", Context.MODE_PRIVATE)?.edit()
        pref?.putString("profileId", firebaseUser.uid)
        pref?.apply()
    }

    override fun onDestroy()
    {
        super.onDestroy()

        val pref = context?.getSharedPreferences("PREFS", Context.MODE_PRIVATE)?.edit()
        pref?.putString("profileId", firebaseUser.uid)
        pref?.apply()
    }

    private fun checkFollowAndFollowingButtonStatus()
    {
        val followingRef = firebaseUser.uid.let {
            FirebaseDatabase.getInstance().reference
                .child("Follow").child(it)
                .child("Following")
        }

        followingRef.addValueEventListener(object : ValueEventListener
        {
            override fun onDataChange(p0: DataSnapshot)
            {
                if (p0.child(profileId).exists())
                {
                    view?.edit_account_settings_btn?.text = getString(R.string.PROFILE_FRAGMENT_LBL_FOLLOWING)
                }
                else
                {
                    view?.edit_account_settings_btn?.text = getString(R.string.PROFILE_FRAGMENT_LBL_FOLLOW)
                }
            }

            override fun onCancelled(p0: DatabaseError) {}
        })
    }

    private fun getFollowers()
    {
        val followersRef = FirebaseDatabase.getInstance().reference
                .child("Follow").child(profileId)
                .child("Followers")

        followersRef.addValueEventListener(object : ValueEventListener
        {
            override fun onDataChange(p0: DataSnapshot)
            {
                if (p0.exists())
                {
                    view?.total_followers?.text = p0.childrenCount.toString()
                }
            }

            override fun onCancelled(p0: DatabaseError)
            {

            }
        })
    }

    private fun getFollowings()
    {
        val followersRef = FirebaseDatabase.getInstance().reference
                .child("Follow").child(profileId)
                .child("Following")


        followersRef.addValueEventListener(object : ValueEventListener
        {
            override fun onDataChange(p0: DataSnapshot)
            {
                if (p0.exists())
                {
                    view?.total_following?.text = p0.childrenCount.toString()
                }
            }

            override fun onCancelled(p0: DatabaseError)
            {

            }
        })
    }

    private fun userInfo()
    {
        val usersRef = FirebaseDatabase.getInstance().reference.child("Users").child(profileId)

        usersRef.addValueEventListener(object : ValueEventListener
        {
            override fun onDataChange(p0: DataSnapshot)
            {
                if (p0.exists())
                {
                    val user = p0.getValue(UserModel::class.java)

                    Picasso.get().load(user?.getImage()).placeholder(R.drawable.profile).into(view?.pro_image_profile_frag)
                    view?.profile_fragment_username?.text = user?.getUsername()
                    view?.full_name_profile_frag?.text = user?.getFullname()
                    view?.bio_profile_frag?.text = user?.getBio()
                }
            }

            override fun onCancelled(p0: DatabaseError) {}
        })
    }

    private fun myPhotos()
    {
        val postRef = FirebaseDatabase.getInstance().reference.child("Posts")

        postRef.addValueEventListener(object : ValueEventListener
        {
            override fun onDataChange(p0: DataSnapshot)
            {
                if (p0.exists())
                {
                    (postList as ArrayList<PostModel>).clear()

                    for (snapshot in p0.children)
                    {
                        val post = snapshot.getValue(PostModel::class.java)

                        if (post?.getPublisher().equals(profileId))
                        {
                            (postList as ArrayList<PostModel>).add(post!!)
                        }

                        reverse(postList)
                        myImagesAdapter?.notifyDataSetChanged()
                    }
                }
            }

            override fun onCancelled(p0: DatabaseError) {}
        })
    }

    private fun getTotalNumberOfPhotos()
    {
        val postsRef = FirebaseDatabase.getInstance().reference.child("Posts")

        postsRef.addValueEventListener(object : ValueEventListener
        {
            override fun onDataChange(p0: DataSnapshot)
            {
                if (p0.exists())
                {
                    var postCounter = 0

                    for (snapShot in p0.children)
                    {
                        val post = snapShot.getValue(PostModel::class.java)

                        if (post?.getPublisher() == profileId)
                        {
                            postCounter++
                        }
                    }

                    total_posts.text = postCounter.toString()//" $postCounter"
                }
            }

            override fun onCancelled(p0: DatabaseError) {}
        })
    }

    private fun  mySaves()
    {
        mySavedImg = ArrayList()

        val savedRef = FirebaseDatabase.getInstance().reference.child("Saves").child(firebaseUser.uid)

        savedRef.addValueEventListener(object : ValueEventListener
        {
            override fun onDataChange(p0: DataSnapshot)
            {
                if (p0.exists())
                {
                    for (snapshot in p0.children)
                    {
                        (mySavedImg as ArrayList<String>).add(snapshot.key!!)
                    }

                    readSavedImagesData()
                }
            }

            override fun onCancelled(p0: DatabaseError) {}
        })
    }

    private fun readSavedImagesData()
    {
        val postsRef = FirebaseDatabase.getInstance().reference.child("Posts")

        postsRef.addValueEventListener(object : ValueEventListener
        {
            override fun onDataChange(p0: DataSnapshot)
            {
                if (p0.exists())
                {
                    (postListSaved as ArrayList<PostModel>).clear()

                    for (snapshot in p0.children)
                    {
                        val post = snapshot.getValue(PostModel::class.java)

                        for (key in mySavedImg!!)
                        {
                            if (post?.getPostid() == key)
                            {
                                (postListSaved as ArrayList<PostModel>).add(post)
                            }
                        }
                    }

                    myImagesAdapterSavedImg?.notifyDataSetChanged()
                }
            }

            override fun onCancelled(p0: DatabaseError) {}
        })
    }

    private fun addNotification()
    {
        val notificationRef = FirebaseDatabase.getInstance().reference.child("Notifications").child(profileId)
        val notificationMap = HashMap<String, Any>()

        notificationMap["userid"] = firebaseUser.uid
        notificationMap["text"] = "Started following you"
        notificationMap["postid"] = ""
        notificationMap["ispost"] = false

        notificationRef.push().setValue(notificationMap)
    }
}
