package com.example.shopping_online.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.shopping_online.model.ListLikeModel;
import com.example.shopping_online.model.ProductModel;
import com.example.shopping_online.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ProductListLikeAdapter extends RecyclerView.Adapter<ProductListLikeAdapter.ViewHolderProductListLike> {

    private final Context context;
    private final ArrayList<ListLikeModel> list;
    private ListLikeModel listLikeModel;
    private FirebaseFirestore firestore;
    final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    final String userId = Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid();
    private ArrayList<ProductModel> listProductModel;

    public ProductListLikeAdapter(Context context, ArrayList<ListLikeModel> list) {
        this.context = context;
        this.list = list;
    }


    @NonNull
    @Override
    public ViewHolderProductListLike onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = ((Activity) context).getLayoutInflater();
        View view = inflater.inflate(R.layout.item_product_list_like, parent, false);
        return new ViewHolderProductListLike(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderProductListLike holder, int position) {
        listLikeModel = list.get(position);
        firestore = FirebaseFirestore.getInstance();

        holder.image_items_product_list_like.setImageResource(R.drawable.product1);
        holder.txt_name_items_product_list_like.setText(listLikeModel.getName_product_listlike());
        holder.txt_price_items_product_list_like.setText(String.valueOf(listLikeModel.getPrice_product_listlike()));

        holder.txt_delete_items_product_list_like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteItemListLikeOnFireStore(position);
            }
        });
    }

    private void deleteItemListLikeOnFireStore(int position) {
        listLikeModel = list.get(position);
        firestore.collection("users").document(userId).collection("listlike").document(listLikeModel.getId()).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                list.remove(position);
                Toast.makeText(context, "Xóa 1 sản phẩm khỏi danh sách yêu thích", Toast.LENGTH_SHORT).show();
                notifyDataSetChanged();

                updateStatusProductOnFireStore(listLikeModel.getProductId());
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(context, "Xóa lỗi", Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void updateStatusProductOnFireStore(String productID) {
        Map<String, Object> itemUpdate = new HashMap<>();
        itemUpdate.put("status_product", false);  // Cập nhật trạng thái sản phẩm

        // Cập nhật trạng thái sản phẩm trong bảng 'product'
        firestore.collection("users").document(userId).collection("product").document(productID).update(itemUpdate)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        // Thành công trong việc cập nhật trạng thái sản phẩm
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Xử lý lỗi khi cập nhật trạng thái sản phẩm
                    }
                });
    }


    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolderProductListLike extends RecyclerView.ViewHolder {
        final TextView txt_name_items_product_list_like;
        final TextView txt_price_items_product_list_like;
        final TextView txt_delete_items_product_list_like;
        final ImageView image_items_product_list_like;

        public ViewHolderProductListLike(@NonNull View itemView) {
            super(itemView);
            txt_name_items_product_list_like = itemView.findViewById(R.id.txt_name_items_product_list_like);
            txt_price_items_product_list_like = itemView.findViewById(R.id.txt_price_items_product_list_like);
            txt_delete_items_product_list_like = itemView.findViewById(R.id.txt_delete_items_product_list_like);
            image_items_product_list_like = itemView.findViewById(R.id.image_items_product_list_like);
        }
    }
}
