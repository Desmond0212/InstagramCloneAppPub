package com.example.instagramcloneapp.Adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import com.example.instagramcloneapp.CommentsActivity
import com.example.instagramcloneapp.MainActivity
import com.example.instagramcloneapp.Model.PostModel
import com.example.instagramcloneapp.Model.UserModel
import com.example.instagramcloneapp.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.activity_account_settings.*

class PostAdapter(private val mContext: Context, private val mPost: List<PostModel>) : RecyclerView.Adapter<PostAdapter.ViewHolder>()
{
    private var firebaseUser: FirebaseUser? = null

    inner class ViewHolder(@NonNull itemView: View) : RecyclerView.ViewHolder(itemView)
    {
        var profileImage: CircleImageView
        var postImage: ImageView
        var likeButton: ImageView
        var commentButton: ImageView
        var saveButton: ImageView
        var userName: TextView
        var likes: TextView
        var publisher: TextView
        var description: TextView
        var comments: TextView

        init {
            profileImage = itemView.findViewById(R.id.user_profile_image_post)
            postImage = itemView.findViewById(R.id.post_image_home)
            likeButton = itemView.findViewById(R.id.post_image_like_btn)
            commentButton = itemView.findViewById(R.id.post_image_comment_btn)
            saveButton = itemView.findViewById(R.id.post_save_comment_btn)
            userName = itemView.findViewById(R.id.user_name_post)
            likes = itemView.findViewById(R.id.likes)
            publisher = itemView.findViewById(R.id.publisher)
            description = itemView.findViewById(R.id.description)
            comments = itemView.findViewById(R.id.comments)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder
    {
        val view = LayoutInflater.from(mContext).inflate(R.layout.posts_layout, parent, false)

        return ViewHolder(view)
    }

    override fun getItemCount(): Int
    {
        return mPost.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int)
    {
        firebaseUser = FirebaseAuth.getInstance().currentUser

        val post = mPost[position]
        Picasso.get().load(post.getPostimage()).into(holder.postImage)
        publisherInfo(holder.profileImage, holder.userName, holder.publisher, post.getPublisher())
        isLikes(post.getPostid(), holder.likeButton)
        numberOfLikes(holder.likes, post.getPostid())
        getTotalComments(holder.comments, post.getPostid())

        if (post.getDescription().equals(""))
        {
            holder.description.visibility = View.GONE
        }
        else
        {
            holder.description.visibility = View.VISIBLE
            holder.description.text = post.getDescription()
        }

        holder.likeButton.setOnClickListener {
            if (holder.likeButton.tag == "Like")
            {
                FirebaseDatabase.getInstance().reference
                    .child("Likes")
                    .child(post.getPostid()!!)
                    .child(firebaseUser!!.uid)
                    .setValue(true)
            }
            else
            {
                FirebaseDatabase.getInstance().reference
                    .child("Likes")
                    .child(post.getPostid()!!)
                    .child(firebaseUser!!.uid)
                    .removeValue()

                val intent = Intent(mContext, MainActivity::class.java)
                mContext.startActivity(intent)
            }
        }

        holder.commentButton.setOnClickListener {
            val intentComment = Intent(mContext, CommentsActivity::class.java)
            intentComment.putExtra("postId", post.getPostid())
            intentComment.putExtra("publisherId", post.getPublisher())
            mContext.startActivity(intentComment)
        }

        holder.comments.setOnClickListener {
            val intentComment = Intent(mContext, CommentsActivity::class.java)
            intentComment.putExtra("postId", post.getPostid())
            intentComment.putExtra("publisherId", post.getPublisher())
            mContext.startActivity(intentComment)
        }
    }

    private fun numberOfLikes(likes: TextView, postid: String?)
    {
        val likesRef = FirebaseDatabase.getInstance().reference.child("Likes").child(postid!!)

        likesRef.addValueEventListener(object : ValueEventListener
        {
            override fun onDataChange(p0: DataSnapshot)
            {
                if (p0.exists())
                {
                    likes.text = p0.childrenCount.toString() + " likes"
                }
            }

            override fun onCancelled(p0: DatabaseError) {}
        })
    }

    private fun getTotalComments(comments: TextView, postid: String?)
    {
        val commentsRef = FirebaseDatabase.getInstance().reference.child("Comments").child(postid!!)

        commentsRef.addValueEventListener(object : ValueEventListener
        {
            override fun onDataChange(p0: DataSnapshot)
            {
                if (p0.exists())
                {
                    comments.text = "view all " + p0.childrenCount.toString() + " comments"
                }
            }

            override fun onCancelled(p0: DatabaseError) {}
        })
    }

    private fun isLikes(postid: String?, likeButton: ImageView)
    {
        val firebaseUser = FirebaseAuth.getInstance().currentUser
        val likesRef = FirebaseDatabase.getInstance().reference.child("Likes").child(postid!!)

        likesRef.addValueEventListener(object : ValueEventListener
        {
            override fun onDataChange(p0: DataSnapshot)
            {
                if (p0.child(firebaseUser!!.uid).exists())
                {
                    likeButton.setImageResource(R.drawable.heart_clicked)
                    likeButton.tag = "Liked"
                }
                else
                {
                    likeButton.setImageResource(R.drawable.heart_not_clicked)
                    likeButton.tag = "Like"
                }
            }

            override fun onCancelled(p0: DatabaseError) {}
        })
    }

    private fun publisherInfo(profileImage: CircleImageView, userName: TextView, publisher: TextView, publisherId: String?)
    {
        val usersRef = FirebaseDatabase.getInstance().reference.child("Users").child(publisherId!!)

        usersRef.addValueEventListener(object : ValueEventListener
        {
            override fun onDataChange(p0: DataSnapshot)
            {
                if (p0.exists())
                {
                    val user = p0.getValue<UserModel>(UserModel::class.java)

                    Picasso.get().load(user!!.getImage()).placeholder(R.drawable.profile).into(profileImage)
                    userName.text = user.getUsername()
                    publisher.text = user.getFullname()
                }
            }

            override fun onCancelled(p0: DatabaseError) {}
        })
    }
}