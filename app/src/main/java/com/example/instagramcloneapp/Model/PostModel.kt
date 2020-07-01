package com.example.instagramcloneapp.Model

class PostModel
{
    private var postid: String? = null
    private var postimage: String? = null
    private var publisher: String? = null
    private var description: String? = null
    private var dateTime: String? = null

    constructor()

    constructor(postid: String?, postimage: String?, publisher: String?, description: String?, dateTime: String?)
    {
        this.postid = postid
        this.postimage = postimage
        this.publisher = publisher
        this.description = description
        this.dateTime = dateTime
    }

    fun setPostid(postid: String)
    {
        this.postid = postid
    }

    fun getPostid(): String?
    {
        return postid
    }

    fun setPostimage(postimage: String)
    {
        this.postimage = postimage
    }

    fun getPostimage(): String?
    {
        return postimage
    }

    fun setPublisher(publisher: String)
    {
        this.publisher = publisher
    }

    fun getPublisher(): String?
    {
        return publisher
    }

    fun setDescription(description: String)
    {
        this.description = description
    }

    fun getDescription(): String?
    {
        return description
    }

    fun setDateTime(dateTime: String)
    {
        this.dateTime = dateTime
    }

    fun getDateTime(): String?
    {
        return dateTime
    }
}