package com.example.storephone.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.storephone.R;
import com.example.storephone.adapters.OfferAdapter;
import com.example.storephone.models.OfferModel;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class OfferActivity extends AppCompatActivity {

    private RecyclerView rvOffers;
    private List<OfferModel> offerList;
    private OfferAdapter offerAdapter;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offer);

        // Set up toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Initialize RecyclerView
        rvOffers = findViewById(R.id.rvOffers);
        rvOffers.setLayoutManager(new LinearLayoutManager(this));

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // Initialize offer list and adapter
        offerList = new ArrayList<>();
        offerAdapter = new OfferAdapter(this, offerList);
        rvOffers.setAdapter(offerAdapter);

        // Load offers from Firestore
        loadOffersFromFirestore();
    }

    private void loadOffersFromFirestore() {
        db.collection("AllOffer")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        offerList.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            OfferModel offer = document.toObject(OfferModel.class);
                            offer.setId(document.getId());
                            offerList.add(offer);
                        }
                        offerAdapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(this, "Mạng chậm, xin hãy đợi . . .: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}