package com.example.shopping_online.admin.layoutadmin;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.shopping_online.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class DetailProductAdminLayout extends AppCompatActivity {

    TextInputLayout layout_nameProduct_new, layout_priceProduct_new, layout_ratingProduct_new, layout_describeProduct_new;
    TextInputEditText edt_nameProduct_new, edt_priceProduct_new, edt_ratingProduct_new, edt_describeProduct_new;
    ImageView imageAllProductDetail;
    TextView txtSubmitAllProductDetail;
    Toolbar toolbarDetailProductLayout;

    private FirebaseFirestore firestore;
    private String documentId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_detail_product_admin_layout);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        firestore = FirebaseFirestore.getInstance();

        imageAllProductDetail = findViewById(R.id.imageAllProductDetail);
        edt_nameProduct_new = findViewById(R.id.edt_nameProduct_new);
        edt_priceProduct_new = findViewById(R.id.edt_priceProduct_new);
        edt_ratingProduct_new = findViewById(R.id.edt_ratingProduct_new);
        edt_describeProduct_new = findViewById(R.id.edt_describeProduct_new);
        txtSubmitAllProductDetail = findViewById(R.id.txtSubmitAllProductDetail);
        toolbarDetailProductLayout = findViewById(R.id.toolbarDetailProductLayout);

        SharedPreferences sharedPreferences = getSharedPreferences("ItemAdmin", MODE_PRIVATE);
        String name = sharedPreferences.getString("name", "");
        String price = sharedPreferences.getString("price", "");
        String describe = sharedPreferences.getString("describe", "");
        String rating = sharedPreferences.getString("rating", "");
        documentId = sharedPreferences.getString("documentId", "");

        edt_nameProduct_new.setText(name);
        edt_priceProduct_new.setText(price);
        edt_describeProduct_new.setText(describe);
        edt_ratingProduct_new.setText(rating);

        setSupportActionBar(toolbarDetailProductLayout);
        Objects.requireNonNull(getSupportActionBar()).setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        txtSubmitAllProductDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String namenew = Objects.requireNonNull(edt_nameProduct_new.getText()).toString().trim();
                String pricenew = Objects.requireNonNull(edt_priceProduct_new.getText()).toString().trim();
                String ratingnew = Objects.requireNonNull(edt_ratingProduct_new.getText()).toString().trim();
                String describenew = Objects.requireNonNull(edt_describeProduct_new.getText()).toString().trim();

                if (namenew.isEmpty() || describenew.isEmpty() || pricenew.isEmpty() || ratingnew.isEmpty()) {
                    Toast.makeText(DetailProductAdminLayout.this, "Vui lòng điền đầy đủ thông tin!", Toast.LENGTH_LONG).show();
                    return;
                }

                Map<String, Object> item = new HashMap<>();
                item.put("name_product", namenew);
                item.put("price_product", Double.parseDouble(pricenew));
                item.put("rating_product", Double.parseDouble(ratingnew));
                item.put("image_product", "123");
                item.put("status_product", false);
                item.put("describe_product", describenew);

                Map<String, Object> itemupdate = new HashMap<>();
                itemupdate.put("name_product", namenew);
                itemupdate.put("price_product", Double.parseDouble(pricenew));
                itemupdate.put("rating_product", Double.parseDouble(ratingnew));
                itemupdate.put("describe_product", describenew);

                if (!name.isEmpty()) {
                    updateDataFromServer(itemupdate);
                } else {
                    addDataFromServer(item);
                }
            }
        });
    }

    private void addDataFromServer(Map<String, Object> item) {
        firestore.collection("product").add(item).addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                String productId = task.getResult().getId();
                item.put("product_id", productId);
                addProductAllUsers(productId, item);
                startActivity(new Intent(DetailProductAdminLayout.this, AdminLayout.class));
                Toast.makeText(DetailProductAdminLayout.this, "Thêm sản phẩm thành công!", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(DetailProductAdminLayout.this, "Thêm sản phẩm lỗi!", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void addProductAllUsers(String documentId, Map<String, Object> item) {
        firestore.collection("users").get()
                .addOnSuccessListener(usersSnapshot -> {
                    for (DocumentSnapshot userDoc : usersSnapshot.getDocuments()) {
                        String userId = userDoc.getId();
                        firestore.collection("users").document(userId)
                                .collection("product")
                                .document(documentId)
                                .set(item)
                                .addOnSuccessListener(aVoid ->
                                        Toast.makeText(DetailProductAdminLayout.this, "Thêm sản phẩm thành công!", Toast.LENGTH_LONG).show()
                                )
                                .addOnFailureListener(e ->
                                        Toast.makeText(DetailProductAdminLayout.this, "Thêm sản phẩm thất bại vào bảng users!", Toast.LENGTH_LONG).show()
                                );
                    }
                })
                .addOnFailureListener(e ->
                        Toast.makeText(DetailProductAdminLayout.this, "Lỗi truy vấn người dùng", Toast.LENGTH_LONG).show()
                );
    }

    private void updateDataFromServer(Map<String, Object> item) {
        firestore.collection("product").document(documentId).update(item).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
//                    Toast.makeText(DetailProductAdminLayout.this, "Update thành công", Toast.LENGTH_LONG).show();
                    updateProductAllUsers(documentId, item);
                    startActivity(new Intent(DetailProductAdminLayout.this, AdminLayout.class));
                } else {
                    Toast.makeText(DetailProductAdminLayout.this, "Update lỗi", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void updateProductAllUsers(String documentId, Map<String, Object> item) {
        firestore.collection("users").get()
                .addOnSuccessListener(usersSnapshot -> {
                    for (DocumentSnapshot userDoc : usersSnapshot.getDocuments()) {
                        String userId = userDoc.getId();
                        firestore.collection("users").document(userId)
                                .collection("product")
                                .get()
                                .addOnSuccessListener(productsSnapshot -> {
                                    for (DocumentSnapshot productDoc : productsSnapshot.getDocuments()) {
                                        if (productDoc.getId().equals(documentId)) {
                                            productDoc.getReference().update(item)
                                                    .addOnSuccessListener(aVoid ->
                                                            Toast.makeText(DetailProductAdminLayout.this, "Cập nhật thành công!", Toast.LENGTH_LONG).show()
                                                    )
                                                    .addOnFailureListener(e ->
                                                            Toast.makeText(DetailProductAdminLayout.this, "Cập nhật thất bại trong bảng users!", Toast.LENGTH_LONG).show()
                                                    );
                                        }
                                    }
                                })
                                .addOnFailureListener(e ->
                                        Toast.makeText(DetailProductAdminLayout.this, "Lỗi truy vấn sub-collection", Toast.LENGTH_LONG).show()
                                );
                    }
                })
                .addOnFailureListener(e ->
                        Toast.makeText(DetailProductAdminLayout.this, "Lỗi truy vấn người dùng", Toast.LENGTH_LONG).show()
                );
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}