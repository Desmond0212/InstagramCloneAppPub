package com.example.instagramcloneapp.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.instagramcloneapp.Adapter.NotificationAdapter
import com.example.instagramcloneapp.Model.NotificationModel
import com.example.instagramcloneapp.Model.PostModel
import com.example.instagramcloneapp.Model.UserModel

import com.example.instagramcloneapp.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_comments.*
import kotlinx.android.synthetic.main.fragment_profile.view.*
import java.util.*
import kotlin.collections.ArrayList

/**
 * A simple [Fragment] subclass.
 */
class NotificationFragment : Fragment()
{
    private var notificationList: List<NotificationModel>? = null
    private var notificationAdapter: NotificationAdapter? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
    {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_notification, container, false)

        val recyclerView: RecyclerView
        recyclerView = view.findViewById(R.id.recycler_view_notifications)
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(context)

        notificationList = ArrayList()
        notificationAdapter = NotificationAdapter(context!!, notificationList as ArrayList<NotificationModel>)
        recyclerView.adapter = notificationAdapter

        readNotifications()

        return view
    }

    private fun readNotifications()
    {
        val notifRef = FirebaseDatabase.getInstance().reference.child("Notifications").child(FirebaseAuth.getInstance().currentUser!!.uid)

        notifRef.addValueEventListener(object : ValueEventListener
        {
            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists())
                {
                    (notificationList as ArrayList<NotificationModel>).clear()

                    for (snapshot in p0.children)
                    {
                        val notification = snapshot.getValue(NotificationModel::class.java)

                        (notificationList as ArrayList<NotificationModel>).add(notification!!)
                    }

                    Collections.reverse(notificationList)
                    notificationAdapter!!.notifyDataSetChanged()
                }
            }

            override fun onCancelled(p0: DatabaseError) {}
        })
    }
}
