package com.example.shopping_online.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.shopping_online.model.CartModel;
import com.example.shopping_online.model.ListLikeModel;
import com.example.shopping_online.model.ProductModel;
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

public class ProductHindAdapter extends RecyclerView.Adapter<ProductHindAdapter.ViewHolderProductHind> {

    private final Context context;
    private final ArrayList<ProductModel> list;
    private ProductModel productModel;
    private CartModel cartModel;
    private FirebaseFirestore firestore;
    final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    final String userId = Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid();
    private ArrayList<ListLikeModel> listLikeModels;
    private ArrayList<CartModel> listCartModels;
    private String ColorValue, SizeValue;

    // Định nghĩa Interface Callback
    public interface OnProductLikedListener {
        void refreshListLike();
    }

    private OnProductLikedListener onProductLikedListener;

    public ProductHindAdapter(Context context, ArrayList<ProductModel> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolderProductHind onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = ((Activity) context).getLayoutInflater();
        View view = inflater.inflate(R.layout.items_product_hind_menu, parent, false);
        return new ViewHolderProductHind(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderProductHind holder, int position) {
        productModel = list.get(position);
        firestore = FirebaseFirestore.getInstance();
        boolean checkStatus = productModel.isStatus_product();

        holder.image_items_product_hint.setImageResource(R.drawable.product1);
//        Glide.with(context).load(productModel.getImage_product()).into(holder.image_items_product_hint);
        holder.txt_name_items_product_hint.setText(productModel.getName_product());
        holder.txt_price_items_product_hint.setText(String.valueOf(productModel.getPrice_product()));
        holder.txt_rating_items_product_hint.setText(String.valueOf(productModel.getRating_product()));
        holder.txt_describe_items_product_hint.setText(productModel.getDescribe_product());

        if (checkStatus) {
            holder.txt_items_add_to_listlike_hint.setCompoundDrawableTintList(
                    ColorStateList.valueOf(ContextCompat.getColor(context, R.color.red)) // Đổi sang màu đỏ
            );
        } else {
            holder.txt_items_add_to_listlike_hint.setCompoundDrawableTintList(
                    ColorStateList.valueOf(ContextCompat.getColor(context, R.color.black)) // Đổi sang màu đỏ
            );
        }

        holder.txt_items_add_to_listlike_hint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addToListLike(holder, position);
            }
        });
        holder.btn_add_items_produdct_hint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addToCart(holder, position);
            }
        });
    }

    private void addToListLike(@NonNull ViewHolderProductHind holder, int position) {
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
                    holder.txt_items_add_to_listlike_hint.setCompoundDrawableTintList(
                            ColorStateList.valueOf(ContextCompat.getColor(context, R.color.red)) // Đổi sang màu đỏ
                    );
                    updateStatusProductOnFireStore(holder, position, true);

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
                            // Xóa sản phẩm khỏi Firestore
                            queryDocumentSnapshots.getDocuments().get(0).getReference().delete();
                            Toast.makeText(context, "Xóa sản phẩm khỏi danh sách yêu thích", Toast.LENGTH_SHORT).show();
                            productModel.setStatus_product(false);  // Cập nhật trạng thái isLiked cho sản phẩm
                            holder.txt_items_add_to_listlike_hint.setCompoundDrawableTintList(
                                    ColorStateList.valueOf(ContextCompat.getColor(context, R.color.black)) // Đổi về màu bình thường
                            );
                            updateStatusProductOnFireStore(holder, position, false);
                            notifyDataSetChanged();
                        }
                    }).addOnFailureListener(e -> Toast.makeText(context, "Xóa lỗi", Toast.LENGTH_SHORT).show());
        }
    }

    private void updateStatusProductOnFireStore(@NonNull ViewHolderProductHind holder, int position, boolean status) {
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

    class ViewHolderProductHind extends RecyclerView.ViewHolder {
        final ImageView image_items_product_hint;
        final ImageView btn_add_items_produdct_hint;
        final TextView txt_name_items_product_hint;
        final TextView txt_price_items_product_hint;
        final TextView txt_rating_items_product_hint;
        final TextView txt_describe_items_product_hint;
        final TextView txt_items_add_to_listlike_hint;

        public ViewHolderProductHind(@NonNull View itemView) {
            super(itemView);
            image_items_product_hint = itemView.findViewById(R.id.image_items_product_hint);
            btn_add_items_produdct_hint = itemView.findViewById(R.id.btn_add_items_produdct_hint);
            txt_name_items_product_hint = itemView.findViewById(R.id.txt_name_items_product_hint);
            txt_price_items_product_hint = itemView.findViewById(R.id.txt_price_items_product_hint);
            txt_rating_items_product_hint = itemView.findViewById(R.id.txt_rating_items_product_hint);
            txt_describe_items_product_hint = itemView.findViewById(R.id.txt_describe_items_product_hint);
            txt_items_add_to_listlike_hint = itemView.findViewById(R.id.txt_items_add_to_listlike_hint);
        }
    }

    private void addToCart(ViewHolderProductHind holder, int position) {
        productModel = list.get(position);

        // Gọi Dialog chọn chi tiết sản phẩm
        openDialogDetail(productModel);
    }

    private void openDialogDetail(ProductModel product) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = LayoutInflater.from(context);
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
            resetSizeSelection(view);
            v.setBackgroundColor(ContextCompat.getColor(context, R.color.red));
            SizeValue = ((TextView) v).getText().toString();
        };

        txtSizeSItemChooseProduct.setOnClickListener(sizeClickListener);
        txtSizeMItemChooseProduct.setOnClickListener(sizeClickListener);
        txtSizeLItemChooseProduct.setOnClickListener(sizeClickListener);
        txtSizeXLItemChooseProduct.setOnClickListener(sizeClickListener);

        // Sự kiện chọn Color
        View.OnClickListener colorClickListener = v -> {
            resetColorSelection(view);
            v.setBackgroundColor(ContextCompat.getColor(context, R.color.red));
            ColorValue = ((TextView) v).getText().toString();
        };

        txtColorWhiteItemChooseProduct.setOnClickListener(colorClickListener);
        txtColorBlueItemChooseProduct.setOnClickListener(colorClickListener);
        txtColorYellowItemChooseProduct.setOnClickListener(colorClickListener);
        txtColorBlackItemChooseProduct.setOnClickListener(colorClickListener);

        // Xử lý khi nhấn nút Submit
        txtSubmitItemChooseProduct.setOnClickListener(v -> {
            if (SizeValue == null || ColorValue == null) {
                Toast.makeText(context, "Vui lòng chọn Size và Màu sắc!", Toast.LENGTH_SHORT).show();
            } else {
                addProductToCart(product, SizeValue, ColorValue);
                dialog.dismiss();
            }
        });
    }

    private void resetSizeSelection(View view) {
        view.findViewById(R.id.txtSizeSItemChooseProduct).setBackgroundColor(ContextCompat.getColor(context, R.color.defaultt));
        view.findViewById(R.id.txtSizeMItemChooseProduct).setBackgroundColor(ContextCompat.getColor(context, R.color.defaultt));
        view.findViewById(R.id.txtSizeLItemChooseProduct).setBackgroundColor(ContextCompat.getColor(context, R.color.defaultt));
        view.findViewById(R.id.txtSizeXLItemChooseProduct).setBackgroundColor(ContextCompat.getColor(context, R.color.defaultt));
    }

    private void resetColorSelection(View view) {
        view.findViewById(R.id.txtColorWhiteItemChooseProduct).setBackgroundColor(ContextCompat.getColor(context, R.color.defaultt));
        view.findViewById(R.id.txtColorBlueItemChooseProduct).setBackgroundColor(ContextCompat.getColor(context, R.color.defaultt));
        view.findViewById(R.id.txtColorYellowItemChooseProduct).setBackgroundColor(ContextCompat.getColor(context, R.color.defaultt));
        view.findViewById(R.id.txtColorBlackItemChooseProduct).setBackgroundColor(ContextCompat.getColor(context, R.color.defaultt));
    }

    private void addProductToCart(ProductModel product, String size, String color) {
        firestore.collection("users").document(userId).collection("cart")
                .whereEqualTo("id_product_cart", product.getId())
                .whereEqualTo("color_product_cart", color)
                .whereEqualTo("size_product_cart", size)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        DocumentReference documentReference = queryDocumentSnapshots.getDocuments().get(0).getReference();
                        int currentQuantity = Objects.requireNonNull(queryDocumentSnapshots.getDocuments().get(0).getLong("quantity_product_cart")).intValue();
                        int updatedQuantity = currentQuantity + 1;
                        double updatedPrice = product.getPrice_product() * updatedQuantity;

                        Map<String, Object> updateFields = new HashMap<>();
                        updateFields.put("quantity_product_cart", updatedQuantity);
                        updateFields.put("price_product_cart", updatedPrice);

                        documentReference.update(updateFields)
                                .addOnSuccessListener(aVoid ->
                                        Toast.makeText(context, "Cập nhật sản phẩm thành công!", Toast.LENGTH_SHORT).show())
                                .addOnFailureListener(e ->
                                        Toast.makeText(context, "Cập nhật sản phẩm thất bại!", Toast.LENGTH_SHORT).show());
                    } else {
                        Map<String, Object> itemCart = new HashMap<>();
                        itemCart.put("id_product_cart", product.getId());
                        itemCart.put("name_product_cart", product.getName_product());
                        itemCart.put("price_product_cart", product.getPrice_product());
                        itemCart.put("rating_product_cart", product.getRating_product());
                        itemCart.put("describe_product_cart", product.getDescribe_product());
                        itemCart.put("image_product_cart", product.getImage_product());
                        itemCart.put("quantity_product_cart", 1);
                        itemCart.put("size_product_cart", size);
                        itemCart.put("color_product_cart", color);

                        firestore.collection("users").document(userId).collection("cart").add(itemCart)
                                .addOnSuccessListener(documentReference ->
                                        Toast.makeText(context, "Thêm sản phẩm thành công!", Toast.LENGTH_SHORT).show())
                                .addOnFailureListener(e ->
                                        Toast.makeText(context, "Thêm sản phẩm thất bại!", Toast.LENGTH_SHORT).show());
                    }
                })
                .addOnFailureListener(e ->
                        Toast.makeText(context, "Kiểm tra sản phẩm thất bại!", Toast.LENGTH_SHORT).show());
    }

}
