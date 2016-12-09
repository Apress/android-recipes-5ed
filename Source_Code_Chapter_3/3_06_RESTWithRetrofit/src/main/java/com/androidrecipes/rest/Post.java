package com.androidrecipes.rest;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

public class Post implements Parcelable {
    public Long id;
    public Long userId;
    public String title;
    public String body;

    protected Post(Parcel in) {
        id = in.readLong();
        userId = in.readLong();
        title = in.readString();
        body = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeLong(userId);
        dest.writeString(title);
        dest.writeString(body);
    }

    public static final Parcelable.Creator<Post> CREATOR = new Parcelable.Creator<Post>() {
        @Override
        public Post createFromParcel(Parcel in) {
            return new Post(in);
        }

        @Override
        public Post[] newArray(int size) {
            return new Post[size];
        }
    };

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Post post = (Post) o;

        return id != null ? id.equals(post.id) : post.id == null;

    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
