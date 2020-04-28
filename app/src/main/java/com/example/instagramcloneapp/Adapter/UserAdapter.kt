package com.example.instagramcloneapp.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.instagramcloneapp.Fragments.ProfileFragment
import com.example.instagramcloneapp.Model.UserModel
import com.example.instagramcloneapp.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso

class UserAdapter (private var mContext: Context,
                   private var mUser: List<UserModel>,
                   private var isFragment: Boolean = false) : RecyclerView.Adapter<UserAdapter.ViewHolder>()
{
    private var firebaseUser: FirebaseUser? = FirebaseAuth.getInstance().currentUser

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserAdapter.ViewHolder
    {
        val view = LayoutInflater.from(mContext).inflate(R.layout.user_item_layout, parent, false)

        return UserAdapter.ViewHolder(view)
    }

    override fun getItemCount(): Int
    {
        return mUser.size
    }

    override fun onBindViewHolder(holder: UserAdapter.ViewHolder, position: Int)
    {
        val user = mUser[position]

        holder.userNameTextView.text = user.getUsername()
        holder.userFullnameTextView.text = user.getFullname()
        Picasso.get().load(user.getImage()).placeholder(R.drawable.profile).into(holder.userProfileImage)

        checkFollowingStatus(user.getUID(), holder.followButton)

        holder.itemView.setOnClickListener(View.OnClickListener {
            val pref = mContext.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit()
            pref.putString("profileId", user.getUID())
            pref.apply()

            (mContext as FragmentActivity).supportFragmentManager.beginTransaction().replace(R.id.fragment_container, ProfileFragment()).commit()
        })

        holder.followButton.setOnClickListener {
            if (holder.followButton.text.toString() == "Follow")
            {
                firebaseUser?.uid.let {it ->
                    FirebaseDatabase.getInstance().reference
                        .child("Follow").child(it.toString())
                        .child("Following").child(user.getUID()!!)
                        .setValue(true).addOnCompleteListener { task ->
                            if (task.isSuccessful)
                            {
                                firebaseUser?.uid.let {it ->
                                    FirebaseDatabase.getInstance().reference
                                        .child("Follow").child(user.getUID()!!)
                                        .child("Followers").child(it.toString())
                                        .setValue(true).addOnCompleteListener { task ->
                                            if (task.isSuccessful)
                                            {

                                            }
                                        }
                                }
                            }
                        }
                }
            }
            else
            {
                firebaseUser?.uid.let {it ->
                    FirebaseDatabase.getInstance().reference
                        .child("Follow").child(it.toString())
                        .child("Following").child(user.getUID()!!)
                        .removeValue().addOnCompleteListener { task ->
                            if (task.isSuccessful)
                            {
                                firebaseUser?.uid.let {it ->
                                    FirebaseDatabase.getInstance().reference
                                        .child("Follow").child(user.getUID()!!)
                                        .child("Followers").child(it.toString())
                                        .removeValue().addOnCompleteListener { task ->
                                            if (task.isSuccessful)
                                            {

                                            }
                                        }
                                }
                            }
                        }
                }
            }
        }
    }

    private fun checkFollowingStatus(uid: String?, followButton: Button)
    {
        val followingRef = firebaseUser?.uid.let { it ->
            FirebaseDatabase.getInstance().reference
                .child("Follow").child(it.toString())
                .child("Following")
        }

        followingRef.addValueEventListener(object: ValueEventListener
        {
            override fun onDataChange(p0: DataSnapshot)
            {
                if (p0.child(uid!!).exists())
                {
                    followButton.text = "Following"
                }
                else
                {
                    followButton.text = "Follow"
                }
            }

            override fun onCancelled(p0: DatabaseError)
            {

            }
        })
    }

    class ViewHolder (@NonNull itemView: View) : RecyclerView.ViewHolder(itemView)
    {
        var userNameTextView: TextView = itemView.findViewById(R.id.user_name_search)
        var userFullnameTextView: TextView = itemView.findViewById(R.id.user_full_name_search)
        var userProfileImage: ImageView = itemView.findViewById(R.id.user_profile_image_search)
        var followButton: Button = itemView.findViewById(R.id.follow_btn_search)
    }
}