package com.islavdroid.blogapp;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Random;

import id.zelory.compressor.Compressor;
import id.zelory.compressor.FileUtil;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class PostActivity extends AppCompatActivity {

    private ImageButton mselectImage;
    private EditText mPostTittle;
    private EditText mPostDesc;
    private Button mSubmitBtn;
    private Uri mImagUri = null;
    private ProgressDialog mProgress;
    private File actualImage;
    private  Bitmap bm;

    private DatabaseReference mDatabaseUser;

       private FirebaseAuth mAuth;
    private FirebaseUser mCurrentUser;
    private static final int GALLERY_REQUEST = 1;

    private StorageReference mStorage;
    private DatabaseReference mDatabase;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        mAuth =FirebaseAuth.getInstance();
        mCurrentUser = mAuth.getCurrentUser();

        mStorage = FirebaseStorage.getInstance().getReference();
        //создаём child Blog в котором будут храниться image,description,title
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Blog");
        mselectImage=(ImageButton) findViewById(R.id.imageSelect);
        mDatabaseUser = FirebaseDatabase.getInstance().getReference().child("Users").child(mCurrentUser.getUid());


        mPostTittle=(EditText) findViewById(R.id.titleField);
        mPostDesc=(EditText) findViewById(R.id.descField);
        mSubmitBtn=(Button) findViewById(R.id.submitButton);
        mProgress = new ProgressDialog(this);

        mselectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //перебрасывает на галерею
                Intent galleryIntent= new Intent(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, GALLERY_REQUEST);
            }
        });
        mSubmitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startPosting();
            }
        });
    }

    private void startPosting() {

//trim-удаляет начальные и конечные пробельные символы если они есть
        final String tittle_val = mPostTittle.getText().toString().trim();
        final String desc_val = mPostDesc.getText().toString().trim();
//если поля не пустые и mImagUri есть изображение
        if (!TextUtils.isEmpty(tittle_val) && !TextUtils.isEmpty(desc_val) && actualImage !=null) {

            mProgress.setMessage("Posting to Blog...");
            mProgress.show();
//в Storage создаём папку Blog_Images для изображений
            StorageReference filepath = mStorage.child("Blog_Images").child(actualImage.getAbsolutePath());

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bm.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] b = baos.toByteArray();

            filepath.putBytes(b).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {




                  final  Uri downloadUrl=taskSnapshot.getDownloadUrl();
                final    DatabaseReference newPost = mDatabase.push();



                    mDatabaseUser.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            newPost.child("tittle").setValue(tittle_val);
                            newPost.child("desc").setValue(desc_val);
                            newPost.child("image").setValue(downloadUrl.toString());
                            newPost.child("uid").setValue(mCurrentUser.getUid());
                            newPost.child("username").setValue(dataSnapshot.child("name").getValue()).
                                    addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                          if(task.isSuccessful()){
                                              startActivity(new Intent(PostActivity.this,MainActivity.class));

                                          }
                                        }
                                    });

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                    mProgress.dismiss();

                }
            });

        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==GALLERY_REQUEST && resultCode==RESULT_OK){


            try {

                actualImage = FileUtil.from(this, data.getData());
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inSampleSize = 8;
                bm=BitmapFactory.decodeFile(actualImage.getAbsolutePath(),options);
                mselectImage.setImageBitmap(bm);
            } catch (IOException e) {
                e.printStackTrace();
            }


        }
    }}