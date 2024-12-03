package com.example.shopping_online.admin.adapteradmin;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.shopping_online.R;
import com.example.shopping_online.admin.layoutadmin.DetailProductAdminLayout;
import com.example.shopping_online.model.ProductModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class ProductAllProductAdapter extends RecyclerView.Adapter<ProductAllProductAdapter.ViewHolderProductAllAdmin> {

    private final Context context;
    private final ArrayList<ProductModel> list;
    private FirebaseFirestore firestore;
    private String documentId;

    public ProductAllProductAdapter(Context context, ArrayList<ProductModel> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolderProductAllAdmin onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = ((Activity) context).getLayoutInflater();
        View view = inflater.inflate(R.layout.product_all_admin, parent, false);
        return new ViewHolderProductAllAdmin(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderProductAllAdmin holder, int position) {
        ProductModel productModel = list.get(position);
        firestore = FirebaseFirestore.getInstance();

        holder.image_items_product_admin.setImageResource(R.drawable.product1);
        holder.txt_name_items_edit_admin.setText(productModel.getName_product());
        holder.txt_describe_items_edit_admin.setText(productModel.getDescribe_product());
        holder.txt_rating_items_edit_admin.setText(String.valueOf(productModel.getRating_product()));
        holder.txt_price_items_edit_admin.setText(String.valueOf(productModel.getPrice_product()));
        documentId = productModel.getId();

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedPreferences = context.getSharedPreferences("ItemAdmin", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("documentId", productModel.getId());
                editor.putString("name", productModel.getName_product());
                editor.putString("price", String.valueOf(productModel.getPrice_product()));
                editor.putString("describe", productModel.getDescribe_product());
                editor.putString("rating", String.valueOf(productModel.getRating_product()));
                editor.apply();

                Intent intent = new Intent(context, DetailProductAdminLayout.class);
                context.startActivity(intent);
            }
        });

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                new AlertDialog.Builder(context)
                        .setTitle("Xác nhận")
                        .setMessage("Bạn có chắc chắn muốn xóa sản phẩm này không?")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                deleteItemOnFireBase(productModel.getId(), position);
                                Log.d("documentId",  documentId);
                            }
                        }).setNegativeButton("Hủy", null).show();
                return true;
            }
        });

    }

    private void deleteItemOnFireBase(String id, int position) {
        firestore.collection("product").document(id).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    list.remove(position);
                    deleteProductInAllUsers(id);
                    notifyDataSetChanged();
                } else {
                    Toast.makeText(context, "Xóa lỗi", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void deleteProductInAllUsers(String productId) {
        firestore.collection("users").get()
                .addOnSuccessListener(usersSnapshot -> {
                    for (DocumentSnapshot userDoc : usersSnapshot.getDocuments()) {
                        String userId = userDoc.getId();
                        firestore.collection("users").document(userId)
                                .collection("product")
                                .get()
                                .addOnSuccessListener(productsSnapshot -> {
                                    for (DocumentSnapshot productDoc : productsSnapshot.getDocuments()) {
                                        if (productDoc.getId().equals(productId)) {
                                            productDoc.getReference().delete()
                                                    .addOnSuccessListener(aVoid ->
                                                            Toast.makeText(context, "Xóa thành công", Toast.LENGTH_LONG).show()
                                                    )
                                                    .addOnFailureListener(e ->
                                                            Toast.makeText(context, "Xóa thất bại", Toast.LENGTH_LONG).show()
                                                    );
                                        }
                                    }
                                })
                                .addOnFailureListener(e ->
                                        Toast.makeText(context, "Lỗi truy vấn sub-collection", Toast.LENGTH_LONG).show()
                                );
                    }
                })
                .addOnFailureListener(e ->
                        Toast.makeText(context, "Lỗi truy vấn người dùng", Toast.LENGTH_LONG).show()
                );
    }



    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolderProductAllAdmin extends RecyclerView.ViewHolder {
        final ImageView image_items_product_admin;
        final TextView txt_name_items_edit_admin;
        final TextView txt_describe_items_edit_admin;
        final TextView txt_price_items_edit_admin;
        final TextView txt_rating_items_edit_admin;

        public ViewHolderProductAllAdmin(@NonNull View itemView) {
            super(itemView);
            image_items_product_admin = itemView.findViewById(R.id.image_items_product_admin);
            txt_name_items_edit_admin = itemView.findViewById(R.id.txt_name_items_edit_admin);
            txt_describe_items_edit_admin = itemView.findViewById(R.id.txt_describe_items_edit_admin);
            txt_price_items_edit_admin = itemView.findViewById(R.id.txt_price_items_edit_admin);
            txt_rating_items_edit_admin = itemView.findViewById(R.id.txt_rating_items_edit_admin);
        }
    }
}
