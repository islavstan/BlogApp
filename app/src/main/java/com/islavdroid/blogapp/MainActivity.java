package com.islavdroid.blogapp;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

public class MainActivity extends AppCompatActivity {
private RecyclerView rvBlogList;
    private DatabaseReference mDataBase;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabaseUsers;
    //с помощью AuthStateListener можно отслеживать когда пользователь входит и выходит
    private FirebaseAuth.AuthStateListener mAuthListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        mAuth=FirebaseAuth.getInstance();
        mAuthListener=new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(firebaseAuth.getCurrentUser()==null){
                  Intent loginIntent =new Intent(MainActivity.this,LogInActivity.class);
                    loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(loginIntent);
                }
            }
        };




        mDataBase= FirebaseDatabase.getInstance().getReference().child("Blog");
        mDatabaseUsers=FirebaseDatabase.getInstance().getReference().child("Users");
        mDatabaseUsers.keepSynced(true);
        mDataBase.keepSynced(true);
        rvBlogList=(RecyclerView)findViewById(R.id.blog_list);
        //rvBlogList.setHasFixedSize(true);
        rvBlogList.setLayoutManager(new LinearLayoutManager(this));
        checkUserExist();
    }
    @Override
    protected void onStart() {
        super.onStart();


        mAuth.addAuthStateListener(mAuthListener);

        FirebaseRecyclerAdapter<Blog,BlogViewHolder>firebaseRecyclerAdapter=new
                FirebaseRecyclerAdapter<Blog, BlogViewHolder>(Blog.class,R.layout.blog_row,
                        BlogViewHolder.class,mDataBase) {

                    @Override
                    protected void populateViewHolder(BlogViewHolder viewHolder, Blog model, int position) {
                        viewHolder.setTitle(model.getTittle());
                        viewHolder.setDesc(model.getDesc());
                        viewHolder.setImage(getApplicationContext(),model.getImage());
                        viewHolder.setUserName(model.getUsername());
                    }
                };
        rvBlogList.setAdapter(firebaseRecyclerAdapter);
    }

    private void checkUserExist() {
        if(mAuth.getCurrentUser() != null){
        final String user_id =mAuth.getCurrentUser().getUid();
        mDatabaseUsers.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(!dataSnapshot.hasChild(user_id)){
                    Intent mainIntent =new Intent(MainActivity.this,SetupActivity.class);
                    mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(mainIntent);

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }}


    public static class BlogViewHolder extends RecyclerView.ViewHolder{
          View mView;
        public BlogViewHolder(View itemView) {
            super(itemView);
            mView=itemView;
        }
        public void setTitle(String tittle){
            TextView post_title=(TextView)mView.findViewById(R.id.post_title);
            post_title.setText(tittle);
        }
        public void setDesc(String desc){
            TextView post_desc=(TextView)mView.findViewById(R.id.post_descr);
            post_desc.setText(desc);
        }

        public void setUserName(String username){
            TextView post_username=(TextView)mView.findViewById(R.id.post_username);
            post_username.setText(username);

        }






        public void setImage(final Context con,final String image){
            final ImageView post_image = (ImageView)mView.findViewById(R.id.post_image);
           // Picasso.with(con).load(image).into(post_image);
            Picasso.with(con).load(image).networkPolicy(NetworkPolicy.OFFLINE).into(post_image, new Callback() {
                @Override
                public void onSuccess() {

                }

                @Override
                public void onError() {
                    Picasso.with(con).load(image).into(post_image);
                }
            });

        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==R.id.action_add){
            startActivity(new Intent(MainActivity.this,PostActivity.class));
        }
        if(item.getItemId()==R.id.action_logout){
            logout();
        }
        return super.onOptionsItemSelected(item);
    }

    private void logout() {
        mAuth.signOut();
    }
}
