package com.example.shopping_online.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.shopping_online.model.CartModel;
import com.example.shopping_online.layout.CartLayout;
import com.example.shopping_online.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ProductCartAdapter extends RecyclerView.Adapter<ProductCartAdapter.ViewHolderProductCart> {

    private final Context context;
    private final ArrayList<CartModel> list;
    private CartModel cartModel;
    private double totalPrice;
    private FirebaseFirestore firestore;
    final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    final String userId = Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid();

    public ProductCartAdapter(Context context, ArrayList<CartModel> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolderProductCart onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = ((Activity) context).getLayoutInflater();
        View view = inflater.inflate(R.layout.item_product_cart, parent, false);
        return new ViewHolderProductCart(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderProductCart holder, int position) {
        cartModel = list.get(position);
        firestore = FirebaseFirestore.getInstance();

        holder.checkboxItemsProductCart.setOnCheckedChangeListener(null);
        holder.checkboxItemsProductCart.setChecked(cartModel.isSelected());

        holder.checkboxItemsProductCart.setOnCheckedChangeListener((buttonView, isChecked) -> {
            list.get(holder.getAdapterPosition()).setSelected(isChecked);
            calculateTotalPrice();
            StatusAllSelectItem();
            saveInformationSelectItem();
        });

        holder.imageItemsProductCart.setImageResource(R.drawable.product2);
        holder.txtNameItemsProductCart.setText(cartModel.getName_product_cart());
        holder.txtPriceItemsProductCart.setText(String.valueOf(cartModel.getPrice_product_cart()));
        holder.txtQuantityProduct.setText(String.valueOf(cartModel.getQuantity_product_cart()));
        holder.txtSizeItemsProductCart.setText(cartModel.getSize_product_cart());
        holder.txtColorItemsProductCart.setText(cartModel.getColor_product_cart());

        holder.txtDeleteItemsProductCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteProductCartOnFireBase(holder, position);
            }
        });

        String priceCartOld = holder.txtPriceItemsProductCart.getText().toString().trim();
        String quantityCartOld = holder.txtQuantityProduct.getText().toString().trim();

        double result = Double.parseDouble(priceCartOld) / Double.parseDouble(quantityCartOld);

        String priceCart = String.valueOf(result);

        holder.txtPlusQuantityProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PlusQuantityProductCart(holder, position, priceCart);
            }
        });
        holder.txtMinusQuantityProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MinusQuantityProductCart(holder, position, priceCart);
            }
        });

    }

    private void saveInformationSelectItem() {
        ArrayList<CartModel> selectedItems = new ArrayList<>();
        for (CartModel cart : list) {
            if (cart.isSelected()) {
                selectedItems.add(cart);
                Log.d("SelectedItem", "Sản phẩm được chọn: " + cart.getName_product_cart());
            }
        }

        // In ra danh sách các sản phẩm đã chọn để kiểm tra
        Log.d("SelectedItem", "Tổng số sản phẩm đã chọn: " + selectedItems.size());

        Gson gson = new Gson();
        String json = gson.toJson(selectedItems);

        SharedPreferences sharedPreferences = context.getSharedPreferences("SelectedItems", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("selectedItems", json);
        editor.apply();
    }


    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolderProductCart extends RecyclerView.ViewHolder {

        private final CheckBox checkboxItemsProductCart;
        private final ImageView imageItemsProductCart;
        private final TextView txtNameItemsProductCart;
        private final TextView txtPriceItemsProductCart;
        private final TextView txtMinusQuantityProduct;
        private final TextView txtQuantityProduct;
        private final TextView txtPlusQuantityProduct;
        private final TextView txtDeleteItemsProductCart;
        private final TextView txtColorItemsProductCart;
        private final TextView txtSizeItemsProductCart;

        public ViewHolderProductCart(@NonNull View itemView) {
            super(itemView);
            checkboxItemsProductCart = itemView.findViewById(R.id.checkboxItemsProductCart);
            imageItemsProductCart = itemView.findViewById(R.id.imageItemsProductCart);
            txtNameItemsProductCart = itemView.findViewById(R.id.txtNameItemsProductCart);
            txtPriceItemsProductCart = itemView.findViewById(R.id.txtPriceItemsProductCart);
            txtMinusQuantityProduct = itemView.findViewById(R.id.txtMinusQuantityProduct);
            txtQuantityProduct = itemView.findViewById(R.id.txtQuantityProduct);
            txtPlusQuantityProduct = itemView.findViewById(R.id.txtPlusQuantityProduct);
            txtDeleteItemsProductCart = itemView.findViewById(R.id.txtDeleteItemsProductCart);
            TextView txtAllPriceCart = itemView.findViewById(R.id.txtAllPriceCart);
            txtSizeItemsProductCart = itemView.findViewById(R.id.txtSizeItemsProductCart);
            txtColorItemsProductCart = itemView.findViewById(R.id.txtColorItemsProductCart);
        }
    }

    private void PlusQuantityProductCart(@NonNull ViewHolderProductCart holder, int position, String priceCart) {
        String quantityCart = holder.txtQuantityProduct.getText().toString().trim();

        int quantityCurrent = Integer.parseInt(quantityCart);
        int quantityFuture = quantityCurrent + 1;

        double priceCurrent = Double.parseDouble(priceCart);
        double priceFuture = priceCurrent * quantityFuture;

        if (quantityFuture <= 100) {
            holder.txtQuantityProduct.setText(String.valueOf(quantityFuture));
            holder.txtPriceItemsProductCart.setText(String.valueOf(priceFuture));

            list.get(position).setQuantity_product_cart(quantityFuture);
            list.get(position).setPrice_product_cart(priceFuture);

            updateQuantityInFirestore(position, quantityFuture, priceFuture);
            calculateTotalPrice();
        } else {
            holder.txtQuantityProduct.setError("Đã đạt số lượng tối đa!");
        }
    }

    private void MinusQuantityProductCart(ViewHolderProductCart holder, int position, String priceCart) {
        String quantityCart = holder.txtQuantityProduct.getText().toString().trim();

        double priceCurrent = Double.parseDouble(priceCart);
        int quantityCurrent = Integer.parseInt(quantityCart);

        if (quantityCurrent > 1) {
            int quantityFuture = quantityCurrent - 1;
            double priceFuture = priceCurrent * quantityFuture;

            holder.txtQuantityProduct.setText(String.valueOf(quantityFuture));
            holder.txtPriceItemsProductCart.setText(String.valueOf(priceFuture));

            list.get(position).setQuantity_product_cart(quantityFuture);
            list.get(position).setPrice_product_cart(priceFuture);

            updateQuantityInFirestore(position, quantityFuture, priceFuture);
            calculateTotalPrice();
        } else {
            Toast.makeText(context, "Không thể giảm sản phẩm xuống dưới 1!", Toast.LENGTH_LONG).show();
        }
    }

    private void updateQuantityInFirestore(int position, int quantityFuture, double priceFuture) {
        cartModel = list.get(position);

        Map<String, Object> item = new HashMap<>();
        item.put("quantity_product_cart", quantityFuture);
        item.put("price_product_cart", priceFuture);

        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        firestore.collection("cart")
                .document(cartModel.getId())
                .update(item)
                .addOnSuccessListener(aVoid -> {
                })
                .addOnFailureListener(e -> {
                });
    }

    public void calculateTotalPrice() {
        totalPrice = 0.0;
        for (CartModel cart : list) {
            if (cart.isSelected()) {
                totalPrice += cart.getPrice_product_cart();
            }
        }
        if (context instanceof CartLayout) {
            ((CartLayout) context).updateTotalPriceUI(totalPrice);
        }
    }

    public void resetTotalPrice() {
        totalPrice = 0.0;
        if (context instanceof CartLayout) {
            ((CartLayout) context).updateTotalPriceUI(totalPrice);
        }
    }

    public void selectAllItem(boolean isChecked) {
        for (CartModel cartModelNew : list) {
            cartModelNew.setSelected(isChecked);
        }
        notifyDataSetChanged();
        saveInformationSelectItem();
        calculateTotalPrice();
    }

    private void deleteProductCartOnFireBase(@NonNull ViewHolderProductCart holder, int position) {
        cartModel = list.get(position);
        firestore.collection("users").document(userId).collection("cart").document(cartModel.getId()).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    list.remove(position);
                    Toast.makeText(context, "Xóa 1 sản phẩm khỏi giỏ hàng", Toast.LENGTH_SHORT).show();
                    calculateTotalPrice();
                    notifyDataSetChanged();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                calculateTotalPrice();
                Toast.makeText(context, "Xóa lỗi", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void StatusAllSelectItem() {
        boolean isAllSelected = true;
        for (CartModel cart : list) {
            if (!cart.isSelected()) {
                isAllSelected = false;
                break;
            }
        }
        if (context instanceof CartLayout) {
            ((CartLayout) context).updateSelectAllState(isAllSelected);
        }
    }

}
