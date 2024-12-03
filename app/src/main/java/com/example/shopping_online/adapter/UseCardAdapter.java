package com.example.shopping_online.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.shopping_online.model.CardModel;
import com.example.shopping_online.layout.PaymentLayout;
import com.example.shopping_online.R;

import java.util.ArrayList;

public class UseCardAdapter extends RecyclerView.Adapter<UseCardAdapter.ViewHolderUseCard> {
    private final Context context;
    private final ArrayList<CardModel> list;

    public UseCardAdapter(Context context, ArrayList<CardModel> list) {
        this.context = context;
        this.list = list;
    }


    @NonNull
    @Override
    public ViewHolderUseCard onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = ((Activity) context).getLayoutInflater();
        View view = inflater.inflate(R.layout.items_crad, parent, false);
        return new ViewHolderUseCard(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderUseCard holder, int position) {
        CardModel cardModel = list.get(position);

        holder.txtNumberItemCard.setText(cardModel.getFour_number_card());
        holder.txtNameItemCard.setText(cardModel.getName_acount_card());
        holder.txtDateItemCard.setText(cardModel.getDate_card());
        holder.txtDeleteCard.setVisibility(View.GONE);

        if (holder.txtChooseUseCard != null) {
            holder.txtChooseUseCard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("UseCardAdapter", "Choose card clicked: " + cardModel.getFour_number_card());
                    useVoucherAndReturnToPayment(cardModel);
                }
            });
        } else {
            Log.d("UseCardAdapter", "txtChooseUseCard is null");
        }
    }

    private void useVoucherAndReturnToPayment(CardModel cardModel) {
        String numberCard = cardModel.getFour_number_card();
        SharedPreferences sharedPreferences = context.getSharedPreferences("UseCard", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("numbercard", numberCard);
        editor.apply();

        Log.d("number1", numberCard);

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

    class ViewHolderUseCard extends RecyclerView.ViewHolder {
        final TextView txtNumberItemCard;
        final TextView txtNameItemCard;
        final TextView txtDateItemCard;
        final TextView txtDeleteCard;
        final LinearLayout txtChooseUseCard;
        public ViewHolderUseCard(@NonNull View itemView) {
            super(itemView);
            txtNumberItemCard = itemView.findViewById(R.id.txtNumberItemCard);
            txtNameItemCard = itemView.findViewById(R.id.txtNameItemCard);
            txtDateItemCard = itemView.findViewById(R.id.txtDateItemCard);
            txtChooseUseCard = itemView.findViewById(R.id.txtChooseUseCard);
            txtDeleteCard = itemView.findViewById(R.id.txtDeleteCard);
        }
    }
}
