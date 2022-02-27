package com.tarek.pluralsighttestfire;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.firebase.ui.auth.data.model.Resource;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.UUID;

public class DealActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 22;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;

    TravelDeal deal;
    EditText txtTitle, txtPriec, txtDescrtion;

    Button button, upload , delete;
    ImageView imageView;

    Uri filePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // FirebaseUtil.openFbReference("traveldeals");
        firebaseDatabase = FirebaseUtil.mFirebaseDatabase;
        databaseReference = FirebaseUtil.mDatabaseReference;

       /* ActionBar actionBar = getSupportActionBar();
        ColorDrawable colorDrawable = new ColorDrawable(Color.parseColor("#0F9D58"));
        actionBar.setBackgroundDrawable(colorDrawable);*/

        txtDescrtion = findViewById(R.id.txt_description);
        txtPriec = findViewById(R.id.txt_price);
        txtTitle = findViewById(R.id.txt_title);
        button = findViewById(R.id.upload_image);
        imageView = findViewById(R.id.image_set);
        upload = findViewById(R.id.button3);
        delete = findViewById(R.id.btn_delete);

        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadImage();
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                startActivityForResult(intent.createChooser(intent, "Insert Picture")
                        , PICK_IMAGE_REQUEST);
            }
        });

        if (!FirebaseUtil.isAdmin) {
            txtPriec.setEnabled(false);
            txtDescrtion.setEnabled(false);
            txtTitle.setEnabled(false);
        }
        TravelDeal deal = (TravelDeal) getIntent().getSerializableExtra("model");
        if (deal == null) {
            deal = new TravelDeal();
        }
        this.deal = deal;

        txtTitle.setText(deal.getTitle());
        txtDescrtion.setText(deal.getDescrption());
        txtPriec.setText(deal.getPrice());
        showImage(deal.getImageUrl());

        TravelDeal finalDeal = deal;
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(finalDeal.getImageName()!= null && finalDeal.getImageName().isEmpty()==false){
                    FirebaseStorage storageRef =FirebaseStorage.getInstance();
                    StorageReference reference = storageRef.getReference().child(finalDeal.getImageName());
                    reference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(DealActivity.this, "image deleted", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(DealActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            filePath = data.getData();

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.save,menu);
        if(!FirebaseUtil.isAdmin) {
            menu.findItem(R.id.save_memu).setVisible(false);
            menu.findItem(R.id.delete_menu).setVisible(false);
        }

        return true;
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.save_memu:
                saveDeal();
                Toast.makeText(this, "Deal Saved", Toast.LENGTH_SHORT).show();
                clean();
                backlist();
                return true;

            case R.id.delete_menu:
                deleteModel();
                Toast.makeText(this, "Travel Deleted", Toast.LENGTH_SHORT).show();
                backlist();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void clean() {
        txtPriec.setText("");
        txtTitle.setText("");
        txtDescrtion.setText("");
        txtTitle.requestFocus();
    }

    private void saveDeal() {
        deal.setTitle(txtTitle.getText().toString().trim());
        deal.setDescrption(txtDescrtion.getText().toString().trim());
        deal.setPrice(txtPriec.getText().toString().trim());

        if(deal.getId()==null) {
            databaseReference.push().setValue(deal);
        }else {
           databaseReference.child(deal.getId()).setValue(deal);
        }
    }

    private void deleteModel(){
        if(deal==null){
            Toast.makeText(this, "Save Travel First", Toast.LENGTH_SHORT).show();
            return;
        }
        databaseReference.child(deal.getId()).removeValue();
        }


    private void backlist(){
        Intent intent = new Intent(this, ListActivity.class);
        startActivity(intent);
    }

    private void uploadImage() {

        if (filePath != null) {

        // Code for showing progressDialog while uploading
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Uploading...");
        progressDialog.show();

        StorageReference ref = FirebaseUtil.mStorageReference.child(UUID.randomUUID().toString());
            // adding listeners on upload
            // or failure of image
        ref.putFile(filePath).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    progressDialog.dismiss();
                    String nameImage  = taskSnapshot.getStorage().getPath();
                    deal.setImageName(nameImage);
                    taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            deal.setImageUrl(uri.toString());

                           showImage(uri.toString());
                        }
                    });
                    Toast.makeText(getApplicationContext(), "Image Uploaded!!", Toast.LENGTH_SHORT).show(); }

            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e)
                { progressDialog.dismiss();
                    Toast.makeText(getApplicationContext(), "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                    progressDialog.setMessage("Uploaded " + (int)progress + "%"); }});


        }
    }

    private void showImage(String url){
    if(url!=null && url.isEmpty()==false){
        int width = Resources.getSystem().getDisplayMetrics().widthPixels;
        Picasso.get().load(url).resize(width, width*2/3)
                .centerCrop()
                .into(imageView);
    }

}
}
