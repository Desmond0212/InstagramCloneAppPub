package com.example.instagramcloneapp.Model

class NotificationModel
{
    private var userid: String? = null
    private var text: String? = null
    private var postid: String? = null
    private var ispost = false

    constructor()
    constructor(userid: String?, text: String?, postid: String?, ispost: Boolean)
    {
        this.userid = userid
        this.text = text
        this.postid = postid
        this.ispost = ispost
    }

    fun getUserId(): String? {
        return userid
    }

    fun setUserId(userid: String?) {
        this.userid = userid
    }

    fun getText(): String? {
        return text
    }

    fun setText(text: String?) {
        this.text = text
    }

    fun getPostId(): String? {
        return postid
    }

    fun setPostId(postid: String?)
    {
        this.postid = postid
    }

    fun getIsPost(): Boolean
    {
        return ispost
    }

    fun setIsPost(isPOst: Boolean)
    {
        this.ispost = isPOst
    }
}