package com.example.storephone.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.storephone.R;
import com.example.storephone.adapters.HomeAdapter;
import com.example.storephone.adapters.PopularAdapter;
import com.example.storephone.adapters.RecommendedAdapter;
import com.example.storephone.adapters.ViewAllAdapter;
import com.example.storephone.models.HomeCategoryModel;
import com.example.storephone.models.PopularModel;
import com.example.storephone.models.RecommendedModel;
import com.example.storephone.models.ViewAllModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {


}