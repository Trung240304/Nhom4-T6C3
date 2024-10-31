package com.example.storephone.fragments;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.storephone.R;
import com.example.storephone.adapters.MyCartAdapter;
import com.example.storephone.models.MyCartModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class MyCartsFragment extends Fragment {

    FirebaseFirestore db;
    FirebaseAuth auth;

    TextView overTotalAmount;
    RecyclerView recyclerView;
    MyCartAdapter cartAdapter;
    List<MyCartModel> cartModelList;
    int totalPrice = 0;

    public MyCartsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_my_carts, container, false);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        recyclerView = root.findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        overTotalAmount = root.findViewById(R.id.textView4);

        LocalBroadcastManager.getInstance(requireActivity())
                .registerReceiver(mMessageReceiver, new IntentFilter("MyTotalAmount"));

        cartModelList = new ArrayList<>();
        cartAdapter = new MyCartAdapter(getActivity(), cartModelList);
        recyclerView.setAdapter(cartAdapter);

        loadCartItems();

        return root;
    }

    private void loadCartItems() {
        db.collection("AddToCart")
                .document(Objects.requireNonNull(auth.getCurrentUser()).getUid())
                .collection("CurrentUser")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            cartModelList.clear();
                            totalPrice = 0;
                            for (DocumentSnapshot documentSnapshot : task.getResult().getDocuments()) {
                                String documentId = documentSnapshot.getId();
                                MyCartModel cartModel = documentSnapshot.toObject(MyCartModel.class);
                                if (cartModel != null) {
                                    cartModel.setDocumentId(documentId);
                                    cartModelList.add(cartModel);
                                    totalPrice += cartModel.getTotalPrice();
                                }
                            }
                            cartAdapter.notifyDataSetChanged();
                            updateTotalAmountUI(totalPrice);
                        }
                    }
                });
    }

    @SuppressLint("SetTextI18n")
    private void updateTotalAmountUI(int totalAmount) {
        NumberFormat numberFormat = NumberFormat.getInstance(new Locale("vi", "VN"));
        overTotalAmount.setText("Tổng Hóa Đơn: " + numberFormat.format(totalAmount) + " đ");
    }

    public BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @SuppressLint("SetTextI18n")
        @Override
        public void onReceive(Context context, Intent intent) {
            int totalBill = intent.getIntExtra("totalAmount", 0);
            updateTotalAmountUI(totalBill);
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        loadCartItems();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        LocalBroadcastManager.getInstance(requireActivity()).unregisterReceiver(mMessageReceiver);
    }
}