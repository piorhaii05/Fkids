package com.example.shopping_online.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.shopping_online.adapter.MyReviewAdapter;
import com.example.shopping_online.model.MyReviewModel;
import com.example.shopping_online.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MyReviewHaveItemFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MyReviewHaveItemFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private ArrayList<MyReviewModel> list;
    private MyReviewModel myReviewModel;
    private MyReviewAdapter adapter;
    private FirebaseFirestore firestore;
    final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    final String userId = Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid();


    public MyReviewHaveItemFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MyReviewHaveItem_Fragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MyReviewHaveItemFragment newInstance(String param1, String param2) {
        MyReviewHaveItemFragment fragment = new MyReviewHaveItemFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            // TODO: Rename and change types of parameters
            String mParam1 = getArguments().getString(ARG_PARAM1);
            String mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_my_review_have_item_, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        firestore = FirebaseFirestore.getInstance();

        RecyclerView recyclerViewMyReview = view.findViewById(R.id.RecyclerViewMyReview);
        recyclerViewMyReview.setLayoutManager(new LinearLayoutManager(getContext()));
        list = new ArrayList<>();
        adapter = new MyReviewAdapter(getContext(), list);
        recyclerViewMyReview.setAdapter(adapter);

        loadDataMyReviewFromFireStore();
    }

    private void loadDataMyReviewFromFireStore() {
        firestore.collection("users").document(userId).collection("myreview").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    list.clear();
                    for(QueryDocumentSnapshot queryDocumentSnapshot: task.getResult()){
                        MyReviewModel myReview = queryDocumentSnapshot.toObject(MyReviewModel.class);
                        myReview.setId(queryDocumentSnapshot.getId());
                        list.add(myReview);
                    }
                    adapter.notifyDataSetChanged();
                }
                else {
                    Toast.makeText(getContext(), "Load dữ liệu lỗi", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}