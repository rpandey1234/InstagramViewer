package com.example.rahul.instagramclient;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.text.Html;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.rahul.instagramclient.com.example.rahul.models.Comment;
import com.makeramen.roundedimageview.RoundedImageView;
import com.makeramen.roundedimageview.RoundedTransformationBuilder;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class InstagramPhotosAdapter extends ArrayAdapter<InstagramPhoto> {

    // The number of comments to show in beneath the photo
    public static final int NUM_COMMENTS_SHOWN = 2;

    public InstagramPhotosAdapter(Context context, int resource, List<InstagramPhoto> objects) {
        super(context, resource, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater layoutInflater = LayoutInflater.from(getContext());
        ViewHolder holder;
        ButterKnife.bind(this, convertView);
        InstagramPhoto photo = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_photo, parent,
                    false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        Transformation transformation = new RoundedTransformationBuilder()
                .borderColor(Color.GRAY)
                .borderWidthDp(1)
                .oval(true)
                .build();

        Resources resources = getContext().getResources();
        int pixelsProfile = (int) DeviceDimensionsHelper.convertDpToPixel(
                resources.getDimension(R.dimen.user_profile), getContext());
        Picasso.with(getContext())
                .load(photo.user.userProfileUrl)
                .placeholder(R.drawable.progress_animation)
                .resize(pixelsProfile, pixelsProfile)
                .transform(transformation)
                .into(holder.profilePic);
        holder.username.setText(photo.user.username);
        String formattedString = String.format("<b>%s</b> %s", photo.user.username, photo.caption);
        holder.tvCaption.setText(Html.fromHtml(formattedString));
        String likeString = resources.getString(R.string.likes, photo.likesCount);
        holder.tvLikes.setText(likeString);
        int numCommentsTotal = photo.comments.size();
        int numCommentsShown = Math.min(NUM_COMMENTS_SHOWN, numCommentsTotal);
        for (int i = 0; i < numCommentsShown; i++) {
            Comment comment = photo.comments.get(i);
            View commentView = layoutInflater.inflate(R.layout.comment, null);
            TextView commentTv = (TextView) commentView.findViewById(R.id.comment);
            String commentFormat = String.format("<b>%s</b> %s", comment.username, comment.text);
            commentTv.setText(Html.fromHtml(commentFormat));
            holder.comments.addView(commentView);
        }
//        if (numCommentsTotal > numCommentsShown) {
//
//        }
        if (photo.createdTime != null) {
            Long timestamp = Long.valueOf(photo.createdTime);
            CharSequence relativeTimestamp = DateUtils.getRelativeTimeSpanString(timestamp * 1000);
            holder.tvTime.setText(relativeTimestamp.toString());
        }
        holder.ivPhoto.setImageResource(0);
        Picasso.with(getContext())
                .load(photo.imageUrl)
                .placeholder(R.drawable.progress_animation)
                .into(holder.ivPhoto);
        return convertView;
    }


    static class ViewHolder {
        @Bind(R.id.tvCaption) TextView tvCaption;
        @Bind(R.id.ivPhoto) ImageView ivPhoto;
        @Bind(R.id.tvLikes) TextView tvLikes;
        @Bind(R.id.tvTime) TextView tvTime;
        @Bind(R.id.tvUsername) TextView username;
        @Bind(R.id.ivProfile) RoundedImageView profilePic;
        @Bind(R.id.comments) LinearLayout comments;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
