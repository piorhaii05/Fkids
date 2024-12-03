package com.example.shopping_online.admin.adapteradmin;

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
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.shopping_online.R;
import com.example.shopping_online.admin.layoutadmin.DetailVoucherAdminLayout;
import com.example.shopping_online.model.VoucherModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class VoucherAdminAdapter extends RecyclerView.Adapter<VoucherAdminAdapter.ViewHolderVoucherAdmin> {

    private final Context context;
    private final ArrayList<VoucherModel> list;
    boolean checkStatusVoucher;
    FirebaseFirestore firestore;

    public VoucherAdminAdapter(Context context, ArrayList<VoucherModel> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolderVoucherAdmin onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_voucher, parent, false);
        return new ViewHolderVoucherAdmin(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderVoucherAdmin holder, int position) {
        VoucherModel voucherModel = list.get(position);
        firestore = FirebaseFirestore.getInstance();

        checkStatusVoucher = voucherModel.isStatus_voucher();
        Log.d("checkStatus", String.valueOf(checkStatusVoucher));
        if (!checkStatusVoucher) {
            holder.txtStatusItemVoucher.setTextColor(ContextCompat.getColor(context, R.color.red));
            holder.txtStatusItemVoucher.setText("Đã hết hạn");
            holder.txtRadiusItemVoucher.setBackgroundColor(ContextCompat.getColor(context, R.color.red));
            holder.txtUseVoucherOnList.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(context, "Voucher đã hết hạn sử dụng!", Toast.LENGTH_LONG).show();
                }
            });
        }
        holder.txtUseVoucherOnList.setText("Sửa");
        holder.imageItemVoucher.setImageResource(R.drawable.voucher);
        holder.txtTitleItemVoucher.setText(voucherModel.getTitle_voucher());
        holder.txtDateItemVoucher.setText(voucherModel.getDate_voucher());

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                new AlertDialog.Builder(context)
                        .setTitle("Xác nhận")
                        .setMessage("Bạn có chắc chắn muốn xóa voucher này không?")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                deleteVoucherOnFireBase(voucherModel.getId(), position);
                            }
                        }).setNegativeButton("Hủy", null).show();
                return true;
            }
        });

        holder.txtUseVoucherOnList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, DetailVoucherAdminLayout.class);
                saveDataOfVoucher(holder, voucherModel);
                context.startActivity(intent);
            }
        });
    }

    private void saveDataOfVoucher(ViewHolderVoucherAdmin holder, VoucherModel voucherModel) {
        String name = holder.txtTitleItemVoucher.getText().toString().trim();
        String date = holder.txtDateItemVoucher.getText().toString().trim();
        String status = String.valueOf(voucherModel.isStatus_voucher());
        String value = String.valueOf(voucherModel.getValue_voucher());

        SharedPreferences sharedPreferences = context.getSharedPreferences("VoucherAdmin", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("documentId", voucherModel.getId());
        editor.putString("date", date);
        editor.putString("name", name);
        editor.putString("status", status);
        editor.putString("value", value);
        editor.apply();
    }


    private void deleteVoucherOnFireBase(String id, int position) {
        firestore.collection("voucher").document(id).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    list.remove(position);
                    Toast.makeText(context, "Xóa voucher thành công", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "Xóa voucher thành công", Toast.LENGTH_SHORT).show();
                }
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class ViewHolderVoucherAdmin extends RecyclerView.ViewHolder {
        final ImageView imageItemVoucher;
        final TextView txtDateItemVoucher;
        final TextView txtTitleItemVoucher;
        final TextView txtStatusItemVoucher;
        final TextView txtUseVoucherOnList;
        final TextView txtUnderLineUse;
        final TextView txtRadiusItemVoucher;

        public ViewHolderVoucherAdmin(@NonNull View itemView) {
            super(itemView);
            imageItemVoucher = itemView.findViewById(R.id.imageItemVoucher);
            txtDateItemVoucher = itemView.findViewById(R.id.txtDateItemVoucher);
            txtTitleItemVoucher = itemView.findViewById(R.id.txtTitleItemVoucher);
            txtStatusItemVoucher = itemView.findViewById(R.id.txtStatusItemVoucher);
            txtUseVoucherOnList = itemView.findViewById(R.id.txtUseVoucherOnList);
            txtUnderLineUse = itemView.findViewById(R.id.txtUnderLineUse);
            txtRadiusItemVoucher = itemView.findViewById(R.id.txtRadiusItemVoucher);
        }
    }
}
