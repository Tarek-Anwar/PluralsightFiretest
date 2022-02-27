package com.tarek.pluralsighttestfire;

import android.app.Activity;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FirebaseUtil {

    private static final Integer RESULT_OK = 123;

    public static FirebaseDatabase mFirebaseDatabase;
    public static DatabaseReference mDatabaseReference;
    private static FirebaseUtil mFirebaseUtil;
    public static FirebaseStorage mStorage;

    public static StorageReference mStorageReference;
    public static FirebaseAuth mFirebaseAuthl;
    public static Activity caller;
    public static FirebaseAuth.AuthStateListener mAuthStateListener;

    public static ArrayList<TravelDeal> trevalModels;

    public static boolean isAdmin;

    private FirebaseUtil(){}

    public static void openFbReference(String ref , Activity callerAct) {

        if (mFirebaseUtil == null) {
            mFirebaseUtil = new FirebaseUtil();
            mFirebaseDatabase = FirebaseDatabase.getInstance();
            mFirebaseAuthl = FirebaseAuth.getInstance();
            caller = callerAct ;
            mAuthStateListener = firebaseAuth -> {
                if (firebaseAuth.getCurrentUser() == null) {FirebaseUtil.sginIn();}
                else {
                    String userId = firebaseAuth.getUid();
                    checkUserAdmin(userId);
                }

            };
            connectStorage();
        }
        trevalModels = new ArrayList<>();
        mDatabaseReference = mFirebaseDatabase.getReference().child(ref);
    }

    private static void checkUserAdmin(String uid) {
        FirebaseUtil.isAdmin = false;
        DatabaseReference ref =  mFirebaseDatabase.getReference().child("admin")
                .child(uid);
        ref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                FirebaseUtil.isAdmin = true;
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public static void sginIn(){
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build(),
                new AuthUI.IdpConfig.GoogleBuilder().build());

        caller.startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .build(),
                RESULT_OK);
    }

    public static void attachListener() {
        mFirebaseAuthl.addAuthStateListener(mAuthStateListener);
    }

    public static void detachListener() {
        mFirebaseAuthl.removeAuthStateListener(mAuthStateListener);
    }

    public static void connectStorage(){
        mStorage = FirebaseStorage.getInstance();
        mStorageReference = mStorage.getReference().child("deals_pictures");

    }

}
