package com.example.instagramcloneapp.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.instagramcloneapp.Fragments.PostDetailsFragment
import com.example.instagramcloneapp.Fragments.ProfileFragment
import com.example.instagramcloneapp.Model.NotificationModel
import com.example.instagramcloneapp.Model.PostModel
import com.example.instagramcloneapp.Model.UserModel
import com.example.instagramcloneapp.R
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.notifications_item_layout.view.*

class NotificationAdapter(private val mContext: Context, private val mNotification: List<NotificationModel>) : RecyclerView.Adapter<NotificationAdapter.ViewHolder>()
{
    inner class ViewHolder(@NonNull itemView : View) : RecyclerView.ViewHolder(itemView)
    {
        var postImage: ImageView
        var profileImage: CircleImageView
        var userName: TextView
        var text: TextView

        init {
            postImage = itemView.findViewById(R.id.notification_post_image)
            profileImage = itemView.findViewById(R.id.notification_profile_image)
            userName = itemView.findViewById(R.id.username_notification)
            text = itemView.findViewById(R.id.comment_notification)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder
    {
        val view = LayoutInflater.from(mContext).inflate(R.layout.notifications_item_layout, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int
    {
        return mNotification.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int)
    {
        val notification = mNotification[position]

        when
        {
            notification.getText().equals(mContext.getString(R.string.NOTIFICATION_ADAPTER_START_FOLLOWING)) ->
            {
                holder.text.text = mContext.getString(R.string.NOTIFICATION_ADAPTER_START_FOLLOWING)
            }
            
            notification.getText().equals(mContext.getString(R.string.NOTIFICATION_ADAPTER_LIKED_POST)) ->
            {
                holder.text.text = mContext.getString(R.string.NOTIFICATION_ADAPTER_LIKED_POST)
            }
            
            notification.getText()!!.contains(mContext.getString(R.string.NOTIFICATION_ADAPTER_COMMENTED)) ->
            {
                holder.text.text = notification.getText()!!.replace(mContext.getString(R.string.NOTIFICATION_ADAPTER_COMMENTED), mContext.getString(R.string.NOTIFICATION_ADAPTER_COMMENTED))
            }
            else -> {
                holder.text.text = notification.getText()
            }
        }

        userInfo(holder.profileImage, holder.userName, notification.getUserId()!!)

        if (notification.getIsPost())
        {
            holder.postImage.visibility = View.VISIBLE
            holder.postImage.clipToOutline = true
            getPostImage(holder.postImage, notification.getPostId()!!)
        }
        else
        {
            holder.postImage.visibility = View.GONE
        }

        holder.itemView.setOnClickListener {
            if (notification.getIsPost())
            {
                val editor = mContext.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit()

                editor.putString("postId", notification.getPostId())
                editor.apply()
                (mContext as FragmentActivity).supportFragmentManager.beginTransaction().replace(R.id.fragment_container, PostDetailsFragment()).commit()
            }
            else
            {
                val editor = mContext.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit()

                editor.putString("profileId", notification.getUserId())
                editor.apply()
                (mContext as FragmentActivity).supportFragmentManager.beginTransaction().replace(R.id.fragment_container, ProfileFragment()).commit()
            }
        }
    }

    private fun userInfo(imageView: ImageView, userName: TextView, publisherId: String)
    {
        val usersRef = FirebaseDatabase.getInstance().reference.child("Users").child(publisherId)

        usersRef.addValueEventListener(object : ValueEventListener
        {
            override fun onDataChange(p0: DataSnapshot)
            {
                if (p0.exists())
                {
                    val user = p0.getValue<UserModel>(UserModel::class.java)

                    Picasso.get().load(user!!.getImage()).placeholder(R.drawable.profile).into(imageView)
                    userName.text = user.getUsername()
                }
            }

            override fun onCancelled(p0: DatabaseError) {}
        })
    }

    private fun getPostImage(imageView: ImageView, postId: String)
    {
        val postRef = FirebaseDatabase.getInstance().reference.child("Posts").child(postId)

        postRef.addValueEventListener(object : ValueEventListener
        {
            override fun onDataChange(p0: DataSnapshot)
            {
                if (p0.exists())
                {
                    val post = p0.getValue<PostModel>(PostModel::class.java)

                    Picasso.get().load(post?.getPostimage()).placeholder(R.drawable.profile).into(imageView)
                }
            }

            override fun onCancelled(p0: DatabaseError) {}
        })
    }
}