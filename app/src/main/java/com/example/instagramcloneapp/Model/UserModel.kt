package com.example.instagramcloneapp.Model

class UserModel
{
    private var username: String? = null
    private var fullname: String? = null
    private var bio: String? = null
    private var image: String? = null
    private var uid: String? = null

    constructor()

    constructor(username: String, fullnema: String, bio: String, image: String, uid: String)
    {
        this.username = username
        this.fullname = username
        this.bio = bio
        this.image = image
        this.uid = uid
    }

    fun getUsername(): String?
    {
        return username
    }

    fun setUsername(username: String?)
    {
        this.username = username
    }

    fun getFullname(): String?
    {
        return fullname
    }

    fun setFullname(fullname: String?)
    {
        this.fullname = fullname
    }

    fun getBio(): String?
    {
        return bio
    }

    fun setBio(bio: String?)
    {
        this.bio = bio
    }

    fun getImage(): String?
    {
        return image
    }

    fun setImage(image: String?)
    {
        this.image = image
    }

    fun getUID(): String?
    {
        return uid
    }

    fun setUID(uid: String?)
    {
        this.uid = uid
    }
}