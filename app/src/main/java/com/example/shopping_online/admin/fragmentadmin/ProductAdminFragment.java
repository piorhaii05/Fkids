package com.example.shopping_online.admin.fragmentadmin;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.example.shopping_online.R;
import com.example.shopping_online.admin.adapteradmin.ProductAllProductAdapter;
import com.example.shopping_online.admin.layoutadmin.DetailProductAdminLayout;
import com.example.shopping_online.model.ProductModel;
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
 * Use the {@link ProductAdminFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProductAdminFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private ArrayList<ProductModel> list;
    private ArrayList<ProductModel> filteredList;
    private ProductAllProductAdapter adapter;

    private FirebaseFirestore firestore;

    public ProductAdminFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProductAdmin_Fragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProductAdminFragment newInstance(String param1, String param2) {
        ProductAdminFragment fragment = new ProductAdminFragment();
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
        return inflater.inflate(R.layout.fragment_product_admin_, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        firestore = FirebaseFirestore.getInstance();

        RecyclerView recyclerViewAllProductAdmin = view.findViewById(R.id.RecyclerViewAllProductAdmin);
        EditText edtSearchAllProductAdmin = view.findViewById(R.id.edtSearchAllProductAdmin);
        FloatingActionButton fabAddProduct = view.findViewById(R.id.fabAddProduct);

        list = new ArrayList<>();
        filteredList = new ArrayList<>();
        adapter = new ProductAllProductAdapter(getContext(), filteredList);

        edtSearchAllProductAdmin.addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                filterProducts(charSequence.toString());
            }

            @Override
            public void afterTextChanged(android.text.Editable editable) {}
        });

        recyclerViewAllProductAdmin.setHasFixedSize(true);
        int numOfColumns = 2;
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), numOfColumns);
        recyclerViewAllProductAdmin.setLayoutManager(gridLayoutManager);
        recyclerViewAllProductAdmin.setAdapter(adapter);

        fabAddProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getContext();
                SharedPreferences sharedPreferences = getContext().getSharedPreferences("ItemAdmin", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("name", null);
                editor.putString("price", null);
                editor.putString("describe", null);
                editor.putString("rating", null);
                editor.apply();
                startActivity(new Intent(getContext(), DetailProductAdminLayout.class));
            }
        });

        loadDataFromFireBase();
    }

    private void loadDataFromFireBase() {
        firestore.collection("product").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for(QueryDocumentSnapshot queryDocumentSnapshot: queryDocumentSnapshots){
                    ProductModel productModel = queryDocumentSnapshot.toObject(ProductModel.class);
                    productModel.setId(queryDocumentSnapshot.getId());
                    list.add(productModel);
                }
                // Cập nhật dữ liệu cho adapter
                filteredList.clear(); // Clear danh sách lọc trước khi thêm vào
                filteredList.addAll(list); // Thêm toàn bộ sản phẩm vào danh sách lọc
                adapter.notifyDataSetChanged();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(), "Load lỗi", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void filterProducts(String query) {
        filteredList.clear();

        if (query.isEmpty()) {
            filteredList.addAll(list);
        } else {
            for (ProductModel product : list) {
                if (product.getName_product().toLowerCase().contains(query.toLowerCase())) {
                    filteredList.add(product);
                }
            }
        }
        adapter.notifyDataSetChanged();
    }
}