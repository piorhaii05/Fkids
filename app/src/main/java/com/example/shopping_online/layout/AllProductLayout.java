package com.example.shopping_online.layout;

import android.content.Context;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.shopping_online.adapter.ProductAllAdapter;
import com.example.shopping_online.model.ProductModel;
import com.example.shopping_online.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Objects;

public class AllProductLayout extends AppCompatActivity {
    private FirebaseFirestore firestore;
    private ArrayList<ProductModel> list;
    private ArrayList<ProductModel> filteredList;
    private ProductAllAdapter adapter;
    final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    final String userId = Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_all_product_layout);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        firestore = FirebaseFirestore.getInstance();

        Toolbar toolbarAllProductLayout = findViewById(R.id.toolbarAllProductLayout);
        RecyclerView recyclerViewAllProductLayout = findViewById(R.id.RecyclerViewAllProductLayout);
        EditText edtSearchViewAllProduct = findViewById(R.id.edtSearchViewAllProduct);

        String checkItent = getIntent().getStringExtra("value");
        if(checkItent != null && checkItent.equals("1")) {
            edtSearchViewAllProduct.requestFocus();
            // Mở bàn phím ảo
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(edtSearchViewAllProduct, InputMethodManager.SHOW_IMPLICIT);
        }

        list = new ArrayList<>();
        filteredList = new ArrayList<>(); // Khởi tạo danh sách đã lọc
        adapter = new ProductAllAdapter(this, filteredList); // Sử dụng filteredList cho adapter

        setSupportActionBar(toolbarAllProductLayout);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Tất Cả Sản Phẩm");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        recyclerViewAllProductLayout.setHasFixedSize(true);
        int numOfColumns = 2;
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, numOfColumns);
        recyclerViewAllProductLayout.setLayoutManager(gridLayoutManager);

        recyclerViewAllProductLayout.setAdapter(adapter);

        // Sử dụng để filter sản phẩm theo tên
        edtSearchViewAllProduct.addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                filterProducts(charSequence.toString());
            }

            @Override
            public void afterTextChanged(android.text.Editable editable) {}
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void loadDataProductFromFireBase() {
        list.clear();
        firestore.collection("users").document(userId).collection("product").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (QueryDocumentSnapshot queryDocumentSnapshot : queryDocumentSnapshots) {
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
                Toast.makeText(AllProductLayout.this, "Load dữ liệu lỗi", Toast.LENGTH_LONG).show();
            }
        });
    }

    // Thực thi filter theo tên
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

    @Override
    protected void onResume() {
        super.onResume();
        loadDataProductFromFireBase();
    }
}
