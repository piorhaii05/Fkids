package com.example.shopping_online.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.shopping_online.adapter.ProductHindAdapter;
import com.example.shopping_online.model.ProductModel;
import com.example.shopping_online.layout.AcountLayout;
import com.example.shopping_online.layout.AllProductLayout;
import com.example.shopping_online.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private ProductHindAdapter adapter;
    private ArrayList<ProductModel> list;
    private FirebaseFirestore firestore;
    final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    final String userId = Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid();

    private RecyclerView RecyclerViewProductType;

    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Home_Fragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home_, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        // Ánh xạ
        firestore = FirebaseFirestore.getInstance();

        TextView txt_goto_productall_layout = view.findViewById(R.id.txt_goto_productall_layout);
        TextView txtAvatarMenu = view.findViewById(R.id.txtAvatarMenu);
        RecyclerView recyclerViewProductHind = view.findViewById(R.id.RecyclerViewProductHind);
        TextView txtSearchMenu = view.findViewById(R.id.txtSearchMenu);

        txtSearchMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), AllProductLayout.class);
                intent.putExtra("value", "1");
                startActivity(intent);
            }
        });

        txt_goto_productall_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), AllProductLayout.class));
            }
        });

        recyclerViewProductHind.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        list = new ArrayList<>();
        adapter = new ProductHindAdapter(getContext(), list);
        recyclerViewProductHind.setAdapter(adapter);

        txtAvatarMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), AcountLayout.class));
            }
        });

//        loadDataFromFireStore();

    }

    private void loadDataFromFireStore() {
        list.clear();
        firestore.collection("users").document(userId).collection("product").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                // Duyệt qua tất cả các tài liệu trả về từ Firestore và thêm vào list
                for (QueryDocumentSnapshot queryDocumentSnapshot : queryDocumentSnapshots) {
                    ProductModel productModel = queryDocumentSnapshot.toObject(ProductModel.class);
                    productModel.setId(queryDocumentSnapshot.getId());
                    list.add(productModel);
                }
                // Cập nhật dữ liệu cho adapter
                adapter.notifyDataSetChanged();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(), "Load dữ liệu lỗi", Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("HomeFragment", "onResume called");
        loadDataFromFireStore();
    }
}