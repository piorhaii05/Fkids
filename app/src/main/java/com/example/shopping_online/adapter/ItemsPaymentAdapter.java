package com.example.shopping_online.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.shopping_online.model.ItemsPaymentModel;
import com.example.shopping_online.R;

import java.util.ArrayList;

public class ItemsPaymentAdapter extends RecyclerView.Adapter<ItemsPaymentAdapter.ViewHolderItemsPayment> {

    private final Context context;
    private final ArrayList<ItemsPaymentModel> list;

    public ItemsPaymentAdapter(Context context, ArrayList<ItemsPaymentModel> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolderItemsPayment onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = ((Activity) context).getLayoutInflater();
        View view = inflater.inflate(R.layout.items_product_in_history, parent, false);
        return new ViewHolderItemsPayment(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderItemsPayment holder, int position) {
        ItemsPaymentModel itemsPaymentModel = list.get(position);

        holder.imageItemsProductInHistory.setImageResource(R.drawable.product1);
        holder.txtNameProductInHistory.setText(itemsPaymentModel.getName_product_items_history());
        holder.txtPriceProductInHistory.setText(String.valueOf(itemsPaymentModel.getPrice_product_items_history()));
        holder.txtSizeProductInHistory.setText(itemsPaymentModel.getSize_product_items_history());
        holder.txtColorProductInHistory.setText(itemsPaymentModel.getColor_product_items_history());
        holder.txtQuantityProductInHistory.setText(String.valueOf(itemsPaymentModel.getQuantity_product_items_history()));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolderItemsPayment extends RecyclerView.ViewHolder {
        final ImageView imageItemsProductInHistory;
        final TextView txtNameProductInHistory;
        final TextView txtPriceProductInHistory;
        final TextView txtColorProductInHistory;
        final TextView txtSizeProductInHistory;
        final TextView txtQuantityProductInHistory;
        public ViewHolderItemsPayment(@NonNull View itemView) {
            super(itemView);
            imageItemsProductInHistory = itemView.findViewById(R.id.imageItemsProductInHistory);
            txtNameProductInHistory = itemView.findViewById(R.id.txtNameProductInHistory);
            txtPriceProductInHistory = itemView.findViewById(R.id.txtPriceProductInHistory);
            txtColorProductInHistory = itemView.findViewById(R.id.txtColorProductInHistory);
            txtSizeProductInHistory = itemView.findViewById(R.id.txtSizeProductInHistory);
            txtQuantityProductInHistory = itemView.findViewById(R.id.txtQuantityProductInHistory);
        }
    }
}
