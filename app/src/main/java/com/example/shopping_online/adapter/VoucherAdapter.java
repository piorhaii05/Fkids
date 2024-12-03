package com.example.shopping_online.adapter;

import android.app.Activity;
import android.content.Context;
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

import com.example.shopping_online.model.VoucherModel;
import com.example.shopping_online.layout.PaymentLayout;
import com.example.shopping_online.R;

import java.util.ArrayList;

public class VoucherAdapter extends RecyclerView.Adapter<VoucherAdapter.ViewHolderVoucher> {

    private final Context context;
    private final ArrayList<VoucherModel> list;

    public VoucherAdapter(Context context, ArrayList<VoucherModel> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolderVoucher onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_voucher, parent, false);
        return new ViewHolderVoucher(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderVoucher holder, int position) {
        VoucherModel voucherModel = list.get(position);

        boolean checkStatusVoucher = voucherModel.isStatus_voucher();
        Log.d("checkStatus", String.valueOf(checkStatusVoucher));
        if (!checkStatusVoucher) {
            holder.txtStatusItemVoucher.setTextColor(ContextCompat.getColor(context, R.color.red));
            holder.txtStatusItemVoucher.setText("Đã hết hạn");
            holder.txtUseVoucherOnList.setTextColor(ContextCompat.getColor(context, R.color.gray));
            holder.txtUnderLineUse.setBackgroundColor(ContextCompat.getColor(context, R.color.gray));
            holder.txtRadiusItemVoucher.setBackgroundColor(ContextCompat.getColor(context, R.color.red));
            holder.txtUseVoucherOnList.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(context, "Voucher đã hết hạn sử dụng!", Toast.LENGTH_LONG).show();
                }
            });
        } else {
            holder.txtUseVoucherOnList.setOnClickListener(v -> {
                    useVoucherAndReturnToPayment(voucherModel);
            });
        }
        holder.imageItemVoucher.setImageResource(R.drawable.voucher);
        holder.txtTitleItemVoucher.setText(voucherModel.getTitle_voucher());
        holder.txtDateItemVoucher.setText(voucherModel.getDate_voucher());
    }

    private void useVoucherAndReturnToPayment(VoucherModel voucherModel) {
        double valueVoucher = voucherModel.getValue_voucher();
        SharedPreferences sharedPreferences = context.getSharedPreferences("ValueVoucher", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("value", String.valueOf(valueVoucher));
        editor.apply();

        Log.d("VoucherAdapter", "Selected Voucher Value: " + valueVoucher);

        Intent intent = new Intent(context, PaymentLayout.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
        if (context instanceof Activity) {
            ((Activity) context).finish();
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class ViewHolderVoucher extends RecyclerView.ViewHolder {
        final ImageView imageItemVoucher;
        final TextView txtDateItemVoucher;
        final TextView txtTitleItemVoucher;
        final TextView txtStatusItemVoucher;
        final TextView txtUseVoucherOnList;
        final TextView txtUnderLineUse;
        final TextView txtRadiusItemVoucher;

        public ViewHolderVoucher(@NonNull View itemView) {
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
