package com.example.shopping_online.admin.adapteradmin;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.shopping_online.R;
import com.example.shopping_online.adapter.ItemsPaymentAdapter;
import com.example.shopping_online.model.HistoryPaymentModel;
import com.example.shopping_online.model.ItemsPaymentModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Objects;

public class HistoryOrderAdminAdapter extends RecyclerView.Adapter<HistoryOrderAdminAdapter.ViewHolderHistoryOrder> {

    private final Context context;
    private final ArrayList<HistoryPaymentModel> list;
    private final FirebaseFirestore firestore;
    final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    String userId = Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid();

    public HistoryOrderAdminAdapter(Context context, ArrayList<HistoryPaymentModel> list) {
        this.context = context;
        this.list = list;
        firestore = FirebaseFirestore.getInstance();
    }

    @NonNull
    @Override
    public ViewHolderHistoryOrder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = ((Activity) context).getLayoutInflater();
        View view = inflater.inflate(R.layout.items_history_payment, parent, false);
        return new ViewHolderHistoryOrder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderHistoryOrder holder, int position) {
        HistoryPaymentModel historyPaymentModel = list.get(position);

        holder.txtDateHistoryPayment.setText(historyPaymentModel.getDate_history());
        holder.txtInforHistoryPayment.setText(historyPaymentModel.getInfor_payment_history());
        holder.txtPriceHistoryPayment.setText(String.valueOf(historyPaymentModel.getPrice_history()));
        holder.txtDeleteItemsHistoryPayment.setVisibility(View.GONE);
        // Tải dữ liệu itemsPayment từ Firebase nếu chưa có dữ liệu
        if (holder.adapter == null) {
            holder.listitemsPaymentModels = new ArrayList<>();
            holder.adapter = new ItemsPaymentAdapter(context, holder.listitemsPaymentModels);
            holder.RecyclerViewItemsHistoryPayment.setLayoutManager(new LinearLayoutManager(context));
            holder.RecyclerViewItemsHistoryPayment.setAdapter(holder.adapter);
        }

        loadDataItemsHistoryFromFirebase(holder, historyPaymentModel);
    }

    private void loadDataItemsHistoryFromFirebase(ViewHolderHistoryOrder holder, HistoryPaymentModel historyPaymentModel) {
        if (!holder.listitemsPaymentModels.isEmpty()) {
            // Nếu danh sách đã được tải, không tải lại
            holder.adapter.notifyDataSetChanged();
            return;
        }

        firestore.collection("users")
                .get()
                .addOnSuccessListener(querySnapshots -> {
                    holder.listitemsPaymentModels.clear();

                    for (QueryDocumentSnapshot userDoc : querySnapshots) {
                        userDoc.getReference()
                                .collection("historypayment")
                                .document(historyPaymentModel.getId())
                                .collection("itemspayment")
                                .get()
                                .addOnSuccessListener(queryItemSnapshots -> {
                                    for (QueryDocumentSnapshot itemDoc : queryItemSnapshots) {
                                        ItemsPaymentModel itemsPaymentModel = itemDoc.toObject(ItemsPaymentModel.class);
                                        itemsPaymentModel.setId(itemDoc.getId());

                                        if (!isDuplicate(holder.listitemsPaymentModels, itemsPaymentModel)) {
                                            holder.listitemsPaymentModels.add(itemsPaymentModel);
                                        }
                                    }

                                    holder.adapter.notifyDataSetChanged();
                                })
                                .addOnFailureListener(e -> {
                                    Log.e("LoadError", "Error loading items for userId: " + userDoc.getId(), e);
                                    Toast.makeText(context, "Error loading items for payment", Toast.LENGTH_LONG).show();
                                });
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("LoadError", "Error loading users", e);
                    Toast.makeText(context, "Error loading user data", Toast.LENGTH_LONG).show();
                });
    }

    // Hàm kiểm tra trùng lặp
    private boolean isDuplicate(ArrayList<ItemsPaymentModel> list, ItemsPaymentModel item) {
        for (ItemsPaymentModel existingItem : list) {
            if (existingItem.getId().equals(item.getId())) {
                return true;
            }
        }
        return false;
    }


    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolderHistoryOrder extends RecyclerView.ViewHolder {
        final TextView txtDateHistoryPayment;
        final TextView txtDeleteItemsHistoryPayment;
        final TextView txtInforHistoryPayment;
        final TextView txtPriceHistoryPayment;
        final RecyclerView RecyclerViewItemsHistoryPayment;
        ItemsPaymentAdapter adapter;
        ArrayList<ItemsPaymentModel> listitemsPaymentModels;

        public ViewHolderHistoryOrder(@NonNull View itemView) {
            super(itemView);
            RecyclerViewItemsHistoryPayment = itemView.findViewById(R.id.RecyclerViewItemsHistoryPayment);
            txtDateHistoryPayment = itemView.findViewById(R.id.txtDateHistoryPayment);
            txtDeleteItemsHistoryPayment = itemView.findViewById(R.id.txtDeleteItemsHistoryPayment);
            txtInforHistoryPayment = itemView.findViewById(R.id.txtInforHistoryPayment);
            txtPriceHistoryPayment = itemView.findViewById(R.id.txtPriceHistoryPayment);
        }
    }
}

