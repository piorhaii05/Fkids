package com.example.shopping_online.layout;

import android.app.Dialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.shopping_online.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class DetailProductLayout extends AppCompatActivity {
    private TextView txtNameAllProductDetail;
    private TextView txtPriceAllProductDetail;
    private TextView txtRatingAllProductDetail;
    private TextView txtDescribeAllProductDetail;
    private TextView txtAddToListLikeAllProductDetail;
    private ImageView imageAllProductDetail;

    private boolean statusFormAdapter;
    private String productId, name, describe, image;
    private double price, rating;
    private String SizeValue, ColorValue;

    private FirebaseFirestore firestore;
    final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    final String userId = Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_detail_product_layout);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        firestore = FirebaseFirestore.getInstance();

        Toolbar toolbarDetailProductLayout = findViewById(R.id.toolbarDetailProductLayout);
        txtNameAllProductDetail = findViewById(R.id.txtNameAllProductDetail);
        txtPriceAllProductDetail = findViewById(R.id.txtPriceAllProductDetail);
        txtRatingAllProductDetail = findViewById(R.id.txtRatingAllProductDetail);
        txtDescribeAllProductDetail = findViewById(R.id.txtDescribeAllProductDetail);
        TextView txtViewAllProductDetail = findViewById(R.id.txtViewAllProductDetail);
        txtAddToListLikeAllProductDetail = findViewById(R.id.txtAddToListLikeAllProductDetail);
        imageAllProductDetail = findViewById(R.id.imageAllProductDetail);
        TextView txtAddToCartAllProductDetail = findViewById(R.id.txtAddToCartAllProductDetail);

        getDataFormAdapter();

        setSupportActionBar(toolbarDetailProductLayout);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");

        txtAddToListLikeAllProductDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addToListLikeAllLayout();
            }
        });

        txtAddToCartAllProductDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDialogDetail();
            }
        });

    }

    private void getDataFormAdapter() {
        Intent intent = getIntent();
        productId = intent.getStringExtra("productId");
        name = intent.getStringExtra("name");
        price = Double.parseDouble(intent.getStringExtra("price"));
        rating = Double.parseDouble(intent.getStringExtra("rating"));
        describe = intent.getStringExtra("describe");
        image = intent.getStringExtra("image");
        statusFormAdapter = intent.getBooleanExtra("status", false);

        if(statusFormAdapter){
            txtAddToListLikeAllProductDetail.setCompoundDrawableTintList(
                    ColorStateList.valueOf(ContextCompat.getColor(DetailProductLayout.this, R.color.red)) // Đổi sang màu đỏ
            );
        }else {
            txtAddToListLikeAllProductDetail.setCompoundDrawableTintList(
                    ColorStateList.valueOf(ContextCompat.getColor(DetailProductLayout.this, R.color.black)) // Đổi về màu bình thường
            );
        }

        imageAllProductDetail.setImageResource(R.drawable.product2);
        txtNameAllProductDetail.setText(name);
        txtPriceAllProductDetail.setText(String.valueOf(price));
        txtRatingAllProductDetail.setText(String.valueOf(rating));
        txtDescribeAllProductDetail.setText(describe);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void addToListLikeAllLayout() {

        Map<String, Object> item = new HashMap<>();
        item.put("productId", productId);
        item.put("name_product_listlike", name);
        item.put("price_product_listlike", price);
        item.put("rating_product_listlike", rating);
        item.put("describe_product_listlike", describe);
        item.put("image_product_listlike", image);

        if (!statusFormAdapter) {
            firestore.collection("users").document(userId).collection("listlike").add(item).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                @Override
                public void onSuccess(DocumentReference documentReference) {
                    Toast.makeText(DetailProductLayout.this, "Thêm sản phẩm vào danh sách yêu thích", Toast.LENGTH_SHORT).show();
                    txtAddToListLikeAllProductDetail.setCompoundDrawableTintList(
                            ColorStateList.valueOf(ContextCompat.getColor(DetailProductLayout.this, R.color.red)) // Đổi sang màu đỏ
                    );
                    updateStatusProductInFirestore(productId, true);
                    
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(DetailProductLayout.this, "Thêm lỗi", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            firestore.collection("users").document(userId).collection("listlike").whereEqualTo("name_product_listlike", name)
                    .get().addOnSuccessListener(queryDocumentSnapshots -> {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            queryDocumentSnapshots.getDocuments().get(0).getReference().delete();
                            Toast.makeText(DetailProductLayout.this, "Xóa sản phẩm khỏi danh sách yêu thích", Toast.LENGTH_SHORT).show();
                            txtAddToListLikeAllProductDetail.setCompoundDrawableTintList(
                                    ColorStateList.valueOf(ContextCompat.getColor(DetailProductLayout.this, R.color.black)) // Đổi về màu bình thường
                            );
                            updateStatusProductInFirestore(productId, false);

                        }
                    }).addOnFailureListener(e -> Toast.makeText(DetailProductLayout.this, "Xóa lỗi", Toast.LENGTH_SHORT).show());
        }
    }

    private void updateStatusProductInFirestore(String producId, boolean status) {
        firestore.collection("users").document(userId).collection("product").document(producId)
                .update("status_product", status)
                .addOnSuccessListener(aVoid -> {
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(DetailProductLayout.this, "Cập nhật trạng thái thất bại", Toast.LENGTH_SHORT).show();
                });
    }

    private void openDialogDetail() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_choose_detail_product, null);
        builder.setView(view);
        Dialog dialog = builder.create();
        dialog.show();

        // Khai báo các TextView
        TextView txtSizeSItemChooseProduct = view.findViewById(R.id.txtSizeSItemChooseProduct);
        TextView txtSizeMItemChooseProduct = view.findViewById(R.id.txtSizeMItemChooseProduct);
        TextView txtSizeLItemChooseProduct = view.findViewById(R.id.txtSizeLItemChooseProduct);
        TextView txtSizeXLItemChooseProduct = view.findViewById(R.id.txtSizeXLItemChooseProduct);
        TextView txtColorWhiteItemChooseProduct = view.findViewById(R.id.txtColorWhiteItemChooseProduct);
        TextView txtColorBlueItemChooseProduct = view.findViewById(R.id.txtColorBlueItemChooseProduct);
        TextView txtColorYellowItemChooseProduct = view.findViewById(R.id.txtColorYellowItemChooseProduct);
        TextView txtColorBlackItemChooseProduct = view.findViewById(R.id.txtColorBlackItemChooseProduct);
        TextView txtSubmitItemChooseProduct = view.findViewById(R.id.txtSubmitItemChooseProduct);

        // Sự kiện chọn Size
        View.OnClickListener sizeClickListener = v -> {
            resetSizeSelection(view); // Đặt lại màu cho tất cả các size
            v.setBackgroundColor(ContextCompat.getColor(this, R.color.red)); // Đổi màu cho item được chọn
            SizeValue = ((TextView) v).getText().toString(); // Lưu giá trị vào biến SizeValue
        };

        txtSizeSItemChooseProduct.setOnClickListener(sizeClickListener);
        txtSizeMItemChooseProduct.setOnClickListener(sizeClickListener);
        txtSizeLItemChooseProduct.setOnClickListener(sizeClickListener);
        txtSizeXLItemChooseProduct.setOnClickListener(sizeClickListener);

        // Sự kiện chọn Color
        View.OnClickListener colorClickListener = v -> {
            resetColorSelection(view); // Đặt lại màu cho tất cả các màu
            v.setBackgroundColor(ContextCompat.getColor(this, R.color.red)); // Đổi màu cho item được chọn
            ColorValue = ((TextView) v).getText().toString(); // Lưu giá trị vào biến ColorValue
        };

        txtColorWhiteItemChooseProduct.setOnClickListener(colorClickListener);
        txtColorBlueItemChooseProduct.setOnClickListener(colorClickListener);
        txtColorYellowItemChooseProduct.setOnClickListener(colorClickListener);
        txtColorBlackItemChooseProduct.setOnClickListener(colorClickListener);

        // Xử lý khi nhấn nút Submit
        txtSubmitItemChooseProduct.setOnClickListener(v -> {
            if (SizeValue == null || ColorValue == null) {
                Toast.makeText(this, "Vui lòng chọn Size và Màu sắc!", Toast.LENGTH_SHORT).show();
            } else {
                addToCart();
                dialog.dismiss();
            }
        });

    }

    private void resetSizeSelection(View view) {
        // Đặt lại màu nền cho tất cả các size
        view.findViewById(R.id.txtSizeSItemChooseProduct).setBackgroundColor(ContextCompat.getColor(this, R.color.defaultt));
        view.findViewById(R.id.txtSizeMItemChooseProduct).setBackgroundColor(ContextCompat.getColor(this, R.color.defaultt));
        view.findViewById(R.id.txtSizeLItemChooseProduct).setBackgroundColor(ContextCompat.getColor(this, R.color.defaultt));
        view.findViewById(R.id.txtSizeXLItemChooseProduct).setBackgroundColor(ContextCompat.getColor(this, R.color.defaultt));
    }

    private void resetColorSelection(View view) {
        // Đặt lại màu nền cho tất cả các màu
        view.findViewById(R.id.txtColorWhiteItemChooseProduct).setBackgroundColor(ContextCompat.getColor(this, R.color.defaultt));
        view.findViewById(R.id.txtColorBlueItemChooseProduct).setBackgroundColor(ContextCompat.getColor(this, R.color.defaultt));
        view.findViewById(R.id.txtColorYellowItemChooseProduct).setBackgroundColor(ContextCompat.getColor(this, R.color.defaultt));
        view.findViewById(R.id.txtColorBlackItemChooseProduct).setBackgroundColor(ContextCompat.getColor(this, R.color.defaultt));
    }
    private void addToCart() {
        firestore.collection("users").document(userId).collection("cart")
                .whereEqualTo("id_product_cart", productId)
                .whereEqualTo("color_product_cart", ColorValue)
                .whereEqualTo("size_product_cart", SizeValue)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        // Nếu sản phẩm đã tồn tại với cùng id, màu và size
                        DocumentReference documentReference = queryDocumentSnapshots.getDocuments().get(0).getReference();
                        int currentQuantity = Objects.requireNonNull(queryDocumentSnapshots.getDocuments().get(0).getLong("quantity_product_cart")).intValue();
                        int updatedQuantity = currentQuantity + 1; // Tăng số lượng lên 1
                        double updatedPrice = price * updatedQuantity; // Cập nhật tổng giá

                        Map<String, Object> updateFields = new HashMap<>();
                        updateFields.put("quantity_product_cart", updatedQuantity);
                        updateFields.put("price_product_cart", updatedPrice);

                        // Cập nhật Firestore
                        documentReference.update(updateFields)
                                .addOnSuccessListener(aVoid ->
                                        Toast.makeText(DetailProductLayout.this, "Cập nhật số lượng sản phẩm thành công!", Toast.LENGTH_SHORT).show())
                                .addOnFailureListener(e ->
                                        Toast.makeText(DetailProductLayout.this, "Cập nhật số lượng thất bại!", Toast.LENGTH_SHORT).show());
                    } else {
                        // Nếu sản phẩm chưa tồn tại với cùng id, màu và size, thêm sản phẩm mới
                        Map<String, Object> itemCart = new HashMap<>();
                        itemCart.put("id_product_cart", productId);
                        itemCart.put("name_product_cart", name);
                        itemCart.put("price_product_cart", price);
                        itemCart.put("rating_product_cart", rating);
                        itemCart.put("describe_product_cart", describe);
                        itemCart.put("image_product_cart", image);
                        itemCart.put("quantity_product_cart", 1); // Số lượng ban đầu là 1
                        itemCart.put("size_product_cart", SizeValue); // Thông tin kích thước
                        itemCart.put("color_product_cart", ColorValue); // Thông tin màu sắc

                        firestore.collection("users").document(userId).collection("cart").add(itemCart)
                                .addOnSuccessListener(documentReference ->
                                        Toast.makeText(DetailProductLayout.this, "Thêm sản phẩm vào giỏ hàng thành công!", Toast.LENGTH_SHORT).show())
                                .addOnFailureListener(e ->
                                        Toast.makeText(DetailProductLayout.this, "Thêm sản phẩm thất bại!", Toast.LENGTH_SHORT).show());
                    }
                })
                .addOnFailureListener(e ->
                        Toast.makeText(DetailProductLayout.this, "Kiểm tra giỏ hàng thất bại!", Toast.LENGTH_SHORT).show());
    }


}