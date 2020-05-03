package com.example.instagramcloneapp.Model

class CommentModel
{
    private var comment: String? = null
    private var publisher: String? = null

    constructor()
    constructor(comment: String?, publisher: String?)
    {
        this.comment = comment
        this.publisher = publisher
    }

    fun getComment(): String?
    {
        return comment
    }

    fun setComment(comment: String?)
    {
        this.comment = comment
    }

    fun getPublisher(): String?
    {
        return publisher
    }

    fun setPublisher(publisher: String?)
    {
        this.publisher = publisher
    }
}