package com.androidrecipes.rest;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.moshi.MoshiConverterFactory;

public class RetrofitActivity extends AppCompatActivity implements Callback<List<Post>> {
    private static final String KEY_POSTS = "posts";
    private static final String REST_API_BASEURL = "http://jsonplaceholder.typicode.com/";
    private PostsService postsService;
    private Call<List<Post>> listCall;
    private List<Post> posts;
    private PostAdapter postAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_retrofit);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        postAdapter = new PostAdapter();
        ((RecyclerView) findViewById(R.id.content_retrofit))
                .setAdapter(postAdapter);
        ((RecyclerView) findViewById(R.id.content_retrofit))
                .setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

Retrofit retrofit = new Retrofit.Builder()
        .baseUrl(REST_API_BASEURL)
        .addConverterFactory(MoshiConverterFactory.create())
        .build();
PostsService postsService = retrofit.create(PostsService.class);

        // Make sure we can handle rotation change without having to reload from the REST API
        if(savedInstanceState != null) {
            posts = savedInstanceState.getParcelableArrayList(KEY_POSTS);
            postAdapter.notifyDataSetChanged();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if(posts != null) {
            outState.putParcelableArrayList(KEY_POSTS, new ArrayList<Parcelable>(posts));
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (listCall != null) {
            listCall.cancel();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public void doRefresh(MenuItem item) {
        if (listCall == null) {
            listCall = postsService.getPosts();
            listCall.enqueue(this);
        } else {
            listCall.cancel();
            listCall = null;
        }
    }

    @Override
    public void onResponse(Call<List<Post>> call, Response<List<Post>> response) {
        if (response.isSuccessful()) {
            posts = response.body();
            postAdapter.notifyDataSetChanged();
        }
        listCall = null;
    }

    @Override
    public void onFailure(Call<List<Post>> call, Throwable t) {
        Toast.makeText(this, "Failed to fetch posts!", Toast.LENGTH_SHORT).show();
        listCall = null;
    }

    private class PostAdapter extends RecyclerView.Adapter<PostViewHolder> {
        @Override
        public PostViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(RetrofitActivity.this)
                    .inflate(R.layout.post_item, parent, false);
            return new PostViewHolder(view);
        }

        @Override
        public void onBindViewHolder(PostViewHolder holder, int position) {
            Post post = posts.get(position);
            holder.bindData(post);
        }

        @Override
        public int getItemCount() {
            return posts != null ? posts.size() : 0;
        }
    }

    private class PostViewHolder extends RecyclerView.ViewHolder {
        private final TextView title;
        private final TextView body;

        public PostViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.title);
            body = (TextView) itemView.findViewById(R.id.body);
        }

        public void bindData(Post post) {
            title.setText(post.title);
            body.setText(post.body);
        }
    }
}
