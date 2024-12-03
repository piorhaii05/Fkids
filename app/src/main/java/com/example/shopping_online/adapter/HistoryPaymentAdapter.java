package com.example.shopping_online.adapter;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.shopping_online.model.HistoryPaymentModel;
import com.example.shopping_online.model.ItemsPaymentModel;
import com.example.shopping_online.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Objects;

public class HistoryPaymentAdapter extends RecyclerView.Adapter<HistoryPaymentAdapter.ViewHolderHistoryPayment> {

    private final Context context;
    private final ArrayList<HistoryPaymentModel> list;
    private final FirebaseFirestore firestore;
    final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    final String userId = Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid();

    public HistoryPaymentAdapter(Context context, ArrayList<HistoryPaymentModel> list) {
        this.context = context;
        this.list = list;
        firestore = FirebaseFirestore.getInstance();
    }

    @NonNull
    @Override
    public ViewHolderHistoryPayment onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = ((Activity) context).getLayoutInflater();
        View view = inflater.inflate(R.layout.items_history_payment, parent, false);
        return new ViewHolderHistoryPayment(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderHistoryPayment holder, int position) {
        HistoryPaymentModel historyPaymentModel = list.get(position);

        // Thiết lập thông tin của item
        holder.txtDateHistoryPayment.setText(historyPaymentModel.getDate_history());
        holder.txtInforHistoryPayment.setText(historyPaymentModel.getInfor_payment_history());
        holder.txtPriceHistoryPayment.setText(String.valueOf(historyPaymentModel.getPrice_history()));
        holder.txtDeleteItemsHistoryPayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(context)
                        .setTitle("Xóa lịch sử đặt hàng")
                        .setMessage("Bạn có chắc chắn muốn xóa lịch sử đặt hàng này không?")
                        .setPositiveButton("OK", (dialog, which) -> deleteHistoryFromFireBase(historyPaymentModel, position))
                        .setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss())
                        .create()
                        .show();
            }
        });

        // Tạo adapter chỉ một lần cho RecyclerView con
        if (holder.adapter == null) {
            holder.listitemsPaymentModels = new ArrayList<>();
            holder.adapter = new ItemsPaymentAdapter(context, holder.listitemsPaymentModels);
            holder.RecyclerViewItemsHistoryPayment.setLayoutManager(new LinearLayoutManager(context));
            holder.RecyclerViewItemsHistoryPayment.setAdapter(holder.adapter);
        }

        loadDataItemsHistoryFormFireBase(historyPaymentModel.getId(), holder);
    }

    private void deleteHistoryFromFireBase(HistoryPaymentModel historyPaymentModel, int position) {
        // Xóa dữ liệu con trong itemspayment trước
        firestore.collection("users")
                .document(userId)
                .collection("historypayment")
                .document(historyPaymentModel.getId())
                .collection("itemspayment")
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    // Xóa từng mục con trước
                    for (QueryDocumentSnapshot doc : querySnapshot) {
                        doc.getReference().delete();
                    }
                    // Sau khi xóa dữ liệu con, xóa lịch sử chính
                    firestore.collection("users")
                            .document(userId)
                            .collection("historypayment")
                            .document(historyPaymentModel.getId())
                            .delete()
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    list.remove(position);
                                    notifyItemRemoved(position); // Cập nhật RecyclerView hiệu quả hơn
                                    Toast.makeText(context, "Xóa thành công!", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .addOnFailureListener(e -> {
                                Log.e("DeleteError", "Error deleting historypayment: " + e.getMessage(), e);
                                Toast.makeText(context, "Lỗi khi xóa lịch sử đặt hàng", Toast.LENGTH_LONG).show();
                            });
                })
                .addOnFailureListener(e -> {
                    Log.e("DeleteError", "Error deleting itemspayment: " + e.getMessage(), e);
                    Toast.makeText(context, "Lỗi khi xóa mục con", Toast.LENGTH_LONG).show();
                });
    }


    private void loadDataItemsHistoryFormFireBase(String purchaseId, ViewHolderHistoryPayment holder) {
        firestore.collection("users").document(userId).collection("historypayment")
                .document(purchaseId)
                .collection("itemspayment")
                .get()
                .addOnSuccessListener(queryItemSnapshots -> {
                    holder.listitemsPaymentModels.clear();
                    for (QueryDocumentSnapshot itemDoc : queryItemSnapshots) {
                        ItemsPaymentModel itemsPaymentModel = itemDoc.toObject(ItemsPaymentModel.class);
                        itemsPaymentModel.setId(itemDoc.getId());
                        holder.listitemsPaymentModels.add(itemsPaymentModel);
                    }
                    Log.d("ItemsLoaded", "Loaded " + holder.listitemsPaymentModels.size() + " items for purchaseId: " + purchaseId);
                    holder.adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(context, "Load lỗi từ itemspayment", Toast.LENGTH_LONG).show();
                });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolderHistoryPayment extends RecyclerView.ViewHolder {
        final TextView txtDateHistoryPayment;
        final TextView txtDeleteItemsHistoryPayment;
        final TextView txtInforHistoryPayment;
        final TextView txtPriceHistoryPayment;
        final RecyclerView RecyclerViewItemsHistoryPayment;
        ItemsPaymentAdapter adapter;
        ArrayList<ItemsPaymentModel> listitemsPaymentModels;

        public ViewHolderHistoryPayment(@NonNull View itemView) {
            super(itemView);
            RecyclerViewItemsHistoryPayment = itemView.findViewById(R.id.RecyclerViewItemsHistoryPayment);
            txtDateHistoryPayment = itemView.findViewById(R.id.txtDateHistoryPayment);
            txtDeleteItemsHistoryPayment = itemView.findViewById(R.id.txtDeleteItemsHistoryPayment);
            txtInforHistoryPayment = itemView.findViewById(R.id.txtInforHistoryPayment);
            txtPriceHistoryPayment = itemView.findViewById(R.id.txtPriceHistoryPayment);
        }
    }
}
