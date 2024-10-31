package com.example.storephone.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.storephone.R;
import com.example.storephone.adapters.OfferAdapter;
import com.example.storephone.models.OfferModel;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class OffersFragment extends Fragment {

    private RecyclerView rvOffers;
    private OfferAdapter offerAdapter;
    private List<OfferModel> offerList;
    private FirebaseFirestore db;

    public OffersFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_offers, container, false);

        rvOffers = view.findViewById(R.id.rvOffers);
        rvOffers.setLayoutManager(new LinearLayoutManager(getContext()));

        offerList = new ArrayList<>();
        offerAdapter = new OfferAdapter(getContext(), offerList);
        rvOffers.setAdapter(offerAdapter);

        db = FirebaseFirestore.getInstance();
        loadOffersFromFirestore();

        return view;
    }

    @SuppressLint("NotifyDataSetChanged")
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
                        Toast.makeText(getContext(), "Lỗi tải!!: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}