package com.example.rahul.instagramclient;

import com.example.rahul.instagramclient.com.example.rahul.models.Comment;
import com.example.rahul.instagramclient.com.example.rahul.models.User;

import java.util.List;

/**
 * Defines the model properties
 */
public class InstagramPhoto {

    public User user;
    public List<Comment> comments;
    public String caption;
    public String imageUrl;
    public String createdTime;

    public int imageHeight;
    public int imageWidth;
    public int likesCount;
}
