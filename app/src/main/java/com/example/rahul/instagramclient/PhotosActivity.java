package com.example.rahul.instagramclient;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;
import android.widget.Toast;

import com.example.rahul.instagramclient.com.example.rahul.models.Comment;
import com.example.rahul.instagramclient.com.example.rahul.models.User;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;

public class PhotosActivity extends AppCompatActivity {

    public static final String CLIENT_ID = "e05c462ebd86446ea48a5af73769b602";
    public static final String BASE_URL
            = "https://api.instagram.com/v1/media/popular?client_id=";
    private List<InstagramPhoto> photos;
    private InstagramPhotosAdapter aPhotos;

    @Bind(R.id.lvPhotos) ListView lvPhotos;
    @Bind(R.id.swipeContainer) SwipeRefreshLayout swipeContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_photos);

        photos = new ArrayList<>();
        // create adapter linking to the source
        aPhotos = new InstagramPhotosAdapter(this, R.layout.content_photos, photos);
        ButterKnife.bind(this);
        lvPhotos.setAdapter(aPhotos);
        fetchPopularPhotos();
        swipeContainer.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh() {
                fetchPopularPhotos();
            }
        });
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
    }

    // trigger API request
    public void fetchPopularPhotos() {
        String url = BASE_URL + CLIENT_ID;
        AsyncHttpClient client = new AsyncHttpClient();
        // trigger GET request
        client.get(url, null, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                aPhotos.clear();
                try {
                    JSONArray photosJson = response.getJSONArray("data");
                    for (int i = 0; i < photosJson.length(); i++) {
                        JSONObject photoJson = photosJson.getJSONObject(i);
                        InstagramPhoto photo = new InstagramPhoto();
                        JSONObject userObject = photoJson.getJSONObject("user");
                        photo.user = new User();
                        photo.user.username = userObject.getString("username");
                        photo.user.userProfileUrl = userObject.getString("profile_picture");
                        photo.createdTime = photoJson.getString("created_time");
                        if (!photoJson.isNull("caption")) {
                            photo.caption = photoJson.getJSONObject("caption").getString("text");
                        }
                        JSONObject image = photoJson.getJSONObject("images").getJSONObject(
                                "standard_resolution");
                        photo.imageUrl = image.getString("url");
                        photo.imageHeight = image.getInt("height");
                        photo.imageWidth = image.getInt("width");
                        photo.likesCount = photoJson.getJSONObject("likes").getInt("count");
                        if (!photoJson.isNull("comments") && !photoJson.getJSONObject("comments")
                                .isNull("data")) {
                            JSONArray commentsJSON = photoJson.getJSONObject("comments")
                                    .getJSONArray("data");
                            List<Comment> comments = new ArrayList<>();
                            for (int j = 0; j < commentsJSON.length(); j++) {
                                JSONObject commentJson = commentsJSON.getJSONObject(j);
                                Comment comment = new Comment();
                                comment.text = commentJson.getString("text");
                                comment.createdTime = commentJson.getString("created_time");
                                comment.username = commentJson.getJSONObject("from").getString(
                                        "username");
                                comments.add(comment);
                            }
                            photo.comments = comments;
                        }
                        photos.add(photo);
                    }
                } catch (JSONException e) {
                    System.out.println("something has gone very wrong " + e.toString());
                }
                aPhotos.notifyDataSetChanged();
                swipeContainer.setRefreshing(false);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString,
                    Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                Toast.makeText(getApplicationContext(), "Sorry, we couldn't retrieve any photos",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
}
