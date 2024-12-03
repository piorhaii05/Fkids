package com.example.shopping_online.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.shopping_online.model.ProductModel;
import com.example.shopping_online.layout.DetailProductLayout;
import com.example.shopping_online.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ProductAllAdapter extends RecyclerView.Adapter<ProductAllAdapter.ViewHolderProductAll> {
    private final Context context;
    private final ArrayList<ProductModel> list;
    private ProductModel productModel;
    private FirebaseFirestore firestore;
    final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    final String userId = Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid();

    public ProductAllAdapter(Context context, ArrayList<ProductModel> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolderProductAll onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = ((Activity) context).getLayoutInflater();
        View view = inflater.inflate(R.layout.items_product_all, parent, false);
        return new ViewHolderProductAll(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderProductAll holder, int position) {
        productModel = list.get(position);
        firestore = FirebaseFirestore.getInstance();
        boolean checkStatusAll = productModel.isStatus_product();

        holder.image_items_product_all.setImageResource(R.drawable.product2);
        holder.txt_describe_items_product_all.setText(productModel.getDescribe_product());
        holder.txt_rating_items_product_all.setText(String.valueOf(productModel.getRating_product()));
        holder.txt_name_items_product_all.setText(productModel.getName_product());
        holder.txt_price_items_product_all.setText(String.valueOf(productModel.getPrice_product()));

        holder.item_all_product_go_to_deltail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToAllProductDetail(holder, position);
            }
        });

        holder.txt_items_add_to_listlike_all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addToListLikeAllLayout(holder, position);
            }
        });

        if (checkStatusAll) {
            holder.txt_items_add_to_listlike_all.setCompoundDrawableTintList(
                    ColorStateList.valueOf(ContextCompat.getColor(context, R.color.red)) // Đổi sang màu đỏ
            );
        } else {
            holder.txt_items_add_to_listlike_all.setCompoundDrawableTintList(
                    ColorStateList.valueOf(ContextCompat.getColor(context, R.color.black)) // Đổi sang màu đỏ
            );
        }
    }

    private void goToAllProductDetail(ViewHolderProductAll holder, int position) {
        productModel = list.get(position);

        String name = holder.txt_name_items_product_all.getText().toString().trim();
        String price = holder.txt_price_items_product_all.getText().toString().trim();
        String rating = holder.txt_rating_items_product_all.getText().toString().trim();
        String describe = holder.txt_describe_items_product_all.getText().toString().trim();
        boolean status = productModel.isStatus_product();
        String image = productModel.getImage_product();
        String productId = productModel.getId();

        Intent intent = new Intent(context, DetailProductLayout.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("name", name);
        intent.putExtra("productId", productId);
        intent.putExtra("price", price);
        intent.putExtra("rating", rating);
        intent.putExtra("describe", describe);
        intent.putExtra("image", image);
        intent.putExtra("status", status);
        context.startActivity(intent);

        Log.d("ProductAllAdapter", "Status: " + status);
    }

    private void addToListLikeAllLayout(ViewHolderProductAll holder, int position) {
        productModel = list.get(position);

        String name_items_listlike = productModel.getName_product();
        double price_items_listlike = productModel.getPrice_product();
        double rating_items_listlike = productModel.getRating_product();
        String describe_items_listlike = productModel.getDescribe_product();
        String image_items_listlike = productModel.getImage_product();
        String productId = productModel.getId();

        Map<String, Object> item = new HashMap<>();
        item.put("productId", productId);
        item.put("name_product_listlike", name_items_listlike);
        item.put("price_product_listlike", price_items_listlike);
        item.put("rating_product_listlike", rating_items_listlike);
        item.put("describe_product_listlike", describe_items_listlike);
        item.put("image_product_listlike", image_items_listlike);

        if (!productModel.isStatus_product()) {
            // Thêm vào danh sách yêu thích
            firestore.collection("users").document(userId).collection("listlike").add(item).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                @Override
                public void onSuccess(DocumentReference documentReference) {
                    Toast.makeText(context, "Thêm sản phẩm vào danh sách yêu thích", Toast.LENGTH_SHORT).show();
                    productModel.setStatus_product(true);  // Cập nhật trạng thái isLiked cho sản phẩm
                    holder.txt_items_add_to_listlike_all.setCompoundDrawableTintList(
                            ColorStateList.valueOf(ContextCompat.getColor(context, R.color.red)) // Đổi sang màu đỏ
                    );
                    updateStatusProductOnFireStoreFromAllProductLayout(holder, position, true);

                    notifyDataSetChanged();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(context, "Thêm lỗi", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            // Xóa khỏi danh sách yêu thích
            firestore.collection("users").document(userId).collection("listlike").whereEqualTo("name_product_listlike", name_items_listlike)
                    .get().addOnSuccessListener(queryDocumentSnapshots -> {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            queryDocumentSnapshots.getDocuments().get(0).getReference().delete();
                            Toast.makeText(context, "Xóa sản phẩm khỏi danh sách yêu thích", Toast.LENGTH_SHORT).show();
                            productModel.setStatus_product(false);  // Cập nhật trạng thái isLiked cho sản phẩm
                            holder.txt_items_add_to_listlike_all.setCompoundDrawableTintList(
                                    ColorStateList.valueOf(ContextCompat.getColor(context, R.color.black)) // Đổi về màu bình thường
                            );
                            updateStatusProductOnFireStoreFromAllProductLayout(holder, position, false);
                            notifyDataSetChanged();
                        }
                    }).addOnFailureListener(e -> Toast.makeText(context, "Xóa lỗi", Toast.LENGTH_SHORT).show());
        }
    }

    private void updateStatusProductOnFireStoreFromAllProductLayout(ViewHolderProductAll holder, int position, boolean status) {
        productModel = list.get(position);

        Map<String, Object> itemUpdate = new HashMap<>();
        itemUpdate.put("status_product", status);

        firestore.collection("users").document(userId).collection("product").document(productModel.getId()).update(itemUpdate).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolderProductAll extends RecyclerView.ViewHolder {
        final ImageView image_items_product_all;
        final TextView txt_items_add_to_listlike_all;
        final TextView txt_name_items_product_all;
        final TextView txt_describe_items_product_all;
        final TextView txt_price_items_product_all;
        final TextView txt_rating_items_product_all;
        final LinearLayout item_all_product_go_to_deltail;

        public ViewHolderProductAll(@NonNull View itemView) {
            super(itemView);
            image_items_product_all = itemView.findViewById(R.id.image_items_product_all);
            txt_items_add_to_listlike_all = itemView.findViewById(R.id.txt_items_add_to_listlike_all);
            txt_name_items_product_all = itemView.findViewById(R.id.txt_name_items_product_all);
            txt_describe_items_product_all = itemView.findViewById(R.id.txt_describe_items_product_all);
            txt_price_items_product_all = itemView.findViewById(R.id.txt_price_items_product_all);
            txt_rating_items_product_all = itemView.findViewById(R.id.txt_rating_items_product_all);
            item_all_product_go_to_deltail = itemView.findViewById(R.id.item_all_product_go_to_deltail);
        }
    }
}
