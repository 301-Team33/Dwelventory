package com.example.dwelventory;

import com.google.firebase.auth.FirebaseAuth;

import org.junit.After;

public class PhotoFragmentTests {

    @After
    public void tearDown() {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        mAuth.signOut();
    }


}
