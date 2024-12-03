package com.example.shopping_online.admin.fragmentadmin;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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

import com.example.shopping_online.R;
import com.example.shopping_online.admin.adapteradmin.VoucherAdminAdapter;
import com.example.shopping_online.admin.layoutadmin.DetailVoucherAdminLayout;
import com.example.shopping_online.model.VoucherModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link VoucherFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class VoucherFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    RecyclerView RecyclerVoucherAdmin;
    FloatingActionButton fabAddVoucher;

    FirebaseFirestore firestore;
    private ArrayList<VoucherModel> list;
    private VoucherAdminAdapter adapter;

    public VoucherFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Voucher_Layout.
     */
    // TODO: Rename and change types and number of parameters
    public static VoucherFragment newInstance(String param1, String param2) {
        VoucherFragment fragment = new VoucherFragment();
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
        return inflater.inflate(R.layout.fragment_voucher__layout, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        firestore = FirebaseFirestore.getInstance();

        fabAddVoucher = view.findViewById(R.id.fabAddVoucher);
        RecyclerVoucherAdmin = view.findViewById(R.id.RecyclerVoucherAdmin);

        RecyclerVoucherAdmin.setLayoutManager(new LinearLayoutManager(getContext()));
        list = new ArrayList<>();
        adapter = new VoucherAdminAdapter(getContext(), list);
        RecyclerVoucherAdmin.setAdapter(adapter);

        fabAddVoucher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getContext();
                SharedPreferences sharedPreferences = getContext().getSharedPreferences("VoucherAdmin", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("date", null);
                editor.putString("date", null);
                editor.putString("name", null);
                editor.putString("status", null);
                editor.putString("value", null);
                editor.apply();
                startActivity(new Intent(getContext(), DetailVoucherAdminLayout.class));
            }
        });

        loadDataFromFireBase();
    }

    private void loadDataFromFireBase() {
        firestore.collection("voucher").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for(QueryDocumentSnapshot queryDocumentSnapshot: queryDocumentSnapshots){
                    VoucherModel voucherModel = queryDocumentSnapshot.toObject(VoucherModel.class);
                    voucherModel.setId(queryDocumentSnapshot.getId());
                    list.add(voucherModel);
                }
                adapter.notifyDataSetChanged();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(), "Load lỗi", Toast.LENGTH_SHORT).show();
            }
        });
    }
}