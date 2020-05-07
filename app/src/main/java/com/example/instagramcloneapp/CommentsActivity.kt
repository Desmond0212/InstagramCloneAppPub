package com.example.instagramcloneapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.instagramcloneapp.Adapter.CommentsAdapter
import com.example.instagramcloneapp.Model.CommentModel
import com.example.instagramcloneapp.Model.UserModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_account_settings.*
import kotlinx.android.synthetic.main.activity_comments.*

class CommentsActivity : AppCompatActivity()
{
    private var postId = ""
    private var publisherId = ""
    private var firebaseUser: FirebaseUser? = null
    private var commentAdapter: CommentsAdapter? = null
    private var commentList: MutableList<CommentModel>? = null

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_comments)

        val intent = intent
        postId = intent.getStringExtra("postId")
        publisherId = intent.getStringExtra("publisherId")
        firebaseUser = FirebaseAuth.getInstance().currentUser

        val recyclerView: RecyclerView = findViewById(R.id.recycler_view_comments)
        val linearLayoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = linearLayoutManager

        commentList = ArrayList()
        commentAdapter = CommentsAdapter(this, commentList)
        recyclerView.adapter = commentAdapter

        userInfo()
        readComments()
        getPostImage()

        post_comment.setOnClickListener(View.OnClickListener {
            if (add_comment.text.toString() == "")
            {
                Toast.makeText(this, "Please write your comment.", Toast.LENGTH_LONG).show()
            }
            else
            {
                addComment()
            }
        })
    }

    private fun userInfo()
    {
        val usersRef = FirebaseDatabase.getInstance().reference.child("Users").child(firebaseUser!!.uid)

        usersRef.addValueEventListener(object : ValueEventListener
        {
            override fun onDataChange(p0: DataSnapshot)
            {
                if (p0.exists())
                {
                    val user = p0.getValue<UserModel>(UserModel::class.java)

                    Picasso.get().load(user!!.getImage()).placeholder(R.drawable.profile).into(profile_image_comment)
                }
            }

            override fun onCancelled(p0: DatabaseError) {}
        })
    }

    private fun addComment()
    {
        val commentsRef = FirebaseDatabase.getInstance().reference.child("Comments").child(postId)
        val commentsMap = HashMap<String, Any>()
        commentsMap["comment"] = add_comment.text.toString()
        commentsMap["publisher"] = firebaseUser!!.uid

        commentsRef.push().setValue(commentsMap)

        //For Notification Use.
        addNotification()

        add_comment.text.clear()
    }

    private fun readComments()
    {
        val commentsRef = FirebaseDatabase.getInstance().reference.child("Comments").child(postId)

        commentsRef.addValueEventListener(object : ValueEventListener
        {
            override fun onDataChange(p0: DataSnapshot)
            {
                if (p0.exists())
                {
                    commentList!!.clear()

                    for (snapshot in p0.children)
                    {
                        val comment = snapshot.getValue(CommentModel::class.java)
                        commentList!!.add(comment!!)
                    }

                    commentAdapter!!.notifyDataSetChanged()
                }
            }

            override fun onCancelled(p0: DatabaseError) {}
        })
    }

    private fun getPostImage()
    {
        val postRef = FirebaseDatabase.getInstance().reference.child("Posts").child(postId).child("postimage")

        postRef.addValueEventListener(object : ValueEventListener
        {
            override fun onDataChange(p0: DataSnapshot)
            {
                if (p0.exists())
                {
                    val image = p0.value.toString()

                    Picasso.get().load(image).placeholder(R.drawable.profile).into(post_image_comment)
                }
            }

            override fun onCancelled(p0: DatabaseError) {}
        })
    }

    private fun addNotification()
    {
        val notifRef = FirebaseDatabase.getInstance().reference.child("Notifications").child(publisherId)
        val notifMap = HashMap<String, Any>()

        notifMap["userid"] = firebaseUser!!.uid
        notifMap["text"] = "Commented: " + add_comment.text.toString()
        notifMap["postid"] = postId
        notifMap["ispost"] = true

        notifRef.push().setValue(notifMap)
    }
}
