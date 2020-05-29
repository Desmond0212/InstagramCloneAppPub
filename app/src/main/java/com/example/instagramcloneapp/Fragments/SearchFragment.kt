package com.example.instagramcloneapp.Fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.instagramcloneapp.Adapter.UserAdapter
import com.example.instagramcloneapp.Model.UserModel
import com.example.instagramcloneapp.R
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.fragment_search.view.*

/**
 * A simple [Fragment] subclass.
 */
class SearchFragment : Fragment()
{
    private var recyclerView: RecyclerView? = null
    private var userAdapter: UserAdapter? = null
    private var mUser: MutableList<UserModel>? = null
    private var resultNotFound: RelativeLayout? = null
    private var emptySearch: RelativeLayout? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
    {
        val view = inflater.inflate(R.layout.fragment_search, container, false)

        recyclerView = view.findViewById(R.id.recycler_view_search)
        recyclerView?.setHasFixedSize(true)
        recyclerView?.layoutManager = LinearLayoutManager(context)

        mUser = ArrayList()
        userAdapter = context?.let { UserAdapter(it, mUser as ArrayList<UserModel>, true)}
        recyclerView?.adapter = userAdapter
        resultNotFound = view.findViewById(R.id.layout_search_result_not_found)
        emptySearch = view.findViewById(R.id.layout_empty_search)

        view.search_edit_text.addTextChangedListener(object: TextWatcher
        {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun afterTextChanged(s: Editable?) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int)
            {
                if (view.search_edit_text.text.toString() == "")
                {
                    //ToDo: Add Empty or no Result Screen
                }
                else
                {
                    recyclerView?.visibility = View.VISIBLE
                    resultNotFound?.visibility = View.GONE
                    emptySearch?.visibility = View.GONE

                    retrieveUsers()
                    searchUser(s.toString().toLowerCase())
                }
            }
        })

        return view
    }

    private fun retrieveUsers()
    {
        val userRef = FirebaseDatabase.getInstance().reference.child("Users")

        userRef.addValueEventListener(object: ValueEventListener
        {
            override fun onDataChange(p0: DataSnapshot)
            {
                if (view?.search_edit_text?.text.toString() == "")
                {
                    mUser?.clear()

                    for (snapshot in p0.children)
                    {
                        val user = snapshot.getValue(UserModel::class.java)

                        if (user != null)
                        {
                            mUser?.add(user)
                        }
                    }

                    userAdapter?.notifyDataSetChanged()
                }
            }

            override fun onCancelled(p0: DatabaseError) {}
        })
    }

    private fun searchUser(input: String)
    {
        val query = FirebaseDatabase.getInstance().reference
            .child("Users")
            .orderByChild("fullname")
            .startAt(input)
            .endAt(input + "\uf8ff")

        query.addValueEventListener(object: ValueEventListener
        {
            var isUserExist: Boolean? = false

            override fun onDataChange(p0: DataSnapshot)
            {
                mUser?.clear()

                for (snapshot in p0.children)
                {
                    val user = snapshot.getValue(UserModel::class.java)

                    if (user != null)
                    {
                        isUserExist = true
                        mUser?.add(user)
                    }
                }

                if (isUserExist == false)
                {
                    recyclerView?.visibility = View.GONE
                    resultNotFound?.visibility = View.VISIBLE
                    emptySearch?.visibility = View.GONE
                }

                userAdapter?.notifyDataSetChanged()
            }

            override fun onCancelled(p0: DatabaseError) {}
        })
    }
}
