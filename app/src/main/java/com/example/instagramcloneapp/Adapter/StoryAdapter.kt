package com.example.instagramcloneapp.Adapter

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import com.example.instagramcloneapp.AddStoryActivity
import com.example.instagramcloneapp.Model.StoryModel
import com.example.instagramcloneapp.Model.UserModel
import com.example.instagramcloneapp.R
import com.example.instagramcloneapp.StoryActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class StoryAdapter(private val mContext: Context, private val mStory: List<StoryModel>) : RecyclerView.Adapter<StoryAdapter.ViewHolder>()
{
    inner class ViewHolder(@NonNull itemView: View) : RecyclerView.ViewHolder(itemView)
    {
        //Story Layout
        var storyImageSeen: CircleImageView? = null
        var storyImage: CircleImageView? = null
        var storyUsername: TextView? = null

        //Add Story Layout
        var storyPlusBtn: ImageView? = null
        var addStoryText: TextView? = null

        init {
            //Story Layout
            storyImageSeen = itemView.findViewById(R.id.story_image_seen)
            storyImage = itemView.findViewById(R.id.story_image)
            storyUsername = itemView.findViewById(R.id.story_username)

            //Add Story Layout
            storyPlusBtn = itemView.findViewById(R.id.story_add)
            addStoryText = itemView.findViewById(R.id.add_story_text)
        }
    }

    override fun getItemViewType(position: Int): Int
    {
        if (position == 0)
        {
            return 0
        }

        return 1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder
    {
        return if (viewType == 0)
        {
            val view = LayoutInflater.from(mContext).inflate(R.layout.add_story_layout, parent, false)
            ViewHolder(view)
        }
        else
        {
            val view = LayoutInflater.from(mContext).inflate(R.layout.story_layout, parent, false)
            ViewHolder(view)
        }
    }

    override fun getItemCount(): Int
    {
        return mStory.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int)
    {
        val story = mStory[position]

        userInfo(holder, story.getUserId()!!, position)

        if (holder.adapterPosition != 0)
        {
            seenStory(holder, story.getUserId()!!)
        }

        if (holder.adapterPosition == 0)
        {
            myStories(holder.addStoryText!!, holder.storyPlusBtn!!, false)
        }

        holder.itemView.setOnClickListener {
            if (holder.adapterPosition == 0)
            {
                myStories(holder.addStoryText!!, holder.storyPlusBtn!!, true)
            }
            else
            {
                val intent = Intent(mContext, StoryActivity::class.java)
                intent.putExtra("userId", story.getUserId())
                mContext.startActivity(intent)
            }
        }
    }

    private fun userInfo(viewHolder: ViewHolder, userId: String, position: Int)
    {
        val usersRef = FirebaseDatabase.getInstance().reference.child("Users").child(userId)

        usersRef.addListenerForSingleValueEvent(object : ValueEventListener
        {
            override fun onDataChange(p0: DataSnapshot)
            {
                if (p0.exists())
                {
                    val user = p0.getValue(UserModel::class.java)

                    Picasso.get().load(user!!.getImage()).placeholder(R.drawable.profile).into(viewHolder.storyImage)

                    if (position != 0)
                    {
                        Picasso.get().load(user.getImage()).placeholder(R.drawable.profile).into(viewHolder.storyImageSeen)
                        viewHolder.storyUsername?.text = user.getUsername()
                    }
                }
            }

            override fun onCancelled(p0: DatabaseError) {}
        })
    }

    private fun seenStory(viewHolder: ViewHolder, userId: String)
    {
        val storyRef = FirebaseDatabase.getInstance().reference.child("Story").child(userId)

        storyRef.addValueEventListener(object : ValueEventListener
        {
            override fun onDataChange(p0: DataSnapshot)
            {
                var i = 0

                for (snapshot in p0.children)
                {
                    if (!snapshot.child("views").child(FirebaseAuth.getInstance().currentUser!!.uid).exists() &&
                            System.currentTimeMillis() < snapshot.getValue(StoryModel::class.java)?.getTimeEnd()!!)
                    {
                        i++
                    }
                }

                if (i > 0)
                {
                    viewHolder.storyImage?.visibility = View.VISIBLE
                    viewHolder.storyImageSeen?.visibility = View.GONE
                }
                else
                {
                    viewHolder.storyImage?.visibility = View.GONE
                    viewHolder.storyImageSeen?.visibility = View.VISIBLE
                }
            }

            override fun onCancelled(p0: DatabaseError) {}
        })
    }

    private fun myStories(textView: TextView, imageView: ImageView, click: Boolean)
    {
        val storyRef = FirebaseDatabase.getInstance().reference.child("Story").child(FirebaseAuth.getInstance().currentUser!!.uid)

        storyRef.addListenerForSingleValueEvent(object: ValueEventListener
        {
            override fun onDataChange(p0: DataSnapshot)
            {
                var counter = 0
                val timeCurrent = System.currentTimeMillis()

                for (snapshot in p0.children)
                {
                    val story = snapshot.getValue(StoryModel::class.java)

                    if (timeCurrent > story!!.getTimeStart()!! && timeCurrent < story.getTimeEnd()!!)
                    {
                        counter++
                    }
                }

                if (click)
                {
                    if (counter > 0)
                    {
                        val alertDialog = AlertDialog.Builder(mContext).create()

                        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "View Story")
                        {
                                dialogInterface, _ ->
                            val intent = Intent(mContext, StoryActivity::class.java)
                            intent.putExtra("userId", FirebaseAuth.getInstance().currentUser?.uid)
                            mContext.startActivity(intent)
                            dialogInterface.dismiss()
                        }

                        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Add Story")
                        {
                                dialogInterface, _ ->
                            val intent = Intent(mContext, AddStoryActivity::class.java)
                            intent.putExtra("userId", FirebaseAuth.getInstance().currentUser?.uid)
                            mContext.startActivity(intent)
                            dialogInterface.dismiss()
                        }

                        alertDialog.show()
                    }
                    else
                    {
                        val intent = Intent(mContext, AddStoryActivity::class.java)
                        intent.putExtra("userId", FirebaseAuth.getInstance().currentUser?.uid)
                        mContext.startActivity(intent)
                    }
                }
                else
                {
                    if (counter > 0)
                    {
                        textView.text = mContext.getString(R.string.STORY_ADAPTER_MY_STORY)
                        imageView.visibility = View.GONE
                    }
                    else
                    {
                        textView.text = mContext.getString(R.string.STORY_ADAPTER_ADD_STORY)
                        imageView.visibility = View.VISIBLE
                    }
                }
            }

            override fun onCancelled(p0: DatabaseError) {}
        })
    }
}