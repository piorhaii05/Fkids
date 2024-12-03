package com.example.shopping_online.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.shopping_online.model.CardModel;
import com.example.shopping_online.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Objects;

public class CardAdapter extends RecyclerView.Adapter<CardAdapter.ViewHolderCard> {
    private final Context context;
    private final ArrayList<CardModel> list;
    private CardModel cardModel;
    private FirebaseFirestore firestore;
    final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    final String userId = Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid();

    public CardAdapter(Context context, ArrayList<CardModel> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolderCard onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = ((Activity) context).getLayoutInflater();
        View view = inflater.inflate(R.layout.items_crad, parent, false);
        return new ViewHolderCard(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderCard holder, int position) {
        cardModel = list.get(position);
        firestore = FirebaseFirestore.getInstance();

        holder.txtNumberItemCard.setText(cardModel.getFour_number_card());
        holder.txtNameItemCard.setText(cardModel.getName_acount_card());
        holder.txtDateItemCard.setText(cardModel.getDate_card());
        holder.txtDeleteCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(context)
                        .setTitle("Delete Card")
                        .setMessage("Bạn có chắc chắn muốn xóa thẻ này không?")
                        .setPositiveButton("OK", (dialog, which) -> deleteCardFromFireBase(cardModel, position))
                        .setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss())
                        .create()
                        .show();
            }
        });
    }

    private void deleteCardFromFireBase(CardModel cardModel, int position) {
        firestore.collection("users").document(userId).collection("card").document(cardModel.getId()).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    list.remove(position);
                    notifyDataSetChanged();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(context, "Delete lỗi", Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolderCard extends RecyclerView.ViewHolder {
        final TextView txtNumberItemCard;
        final TextView txtNameItemCard;
        final TextView txtDateItemCard;
        final TextView txtDeleteCard;
        public ViewHolderCard(@NonNull View itemView) {
            super(itemView);
            txtNumberItemCard = itemView.findViewById(R.id.txtNumberItemCard);
            txtNameItemCard = itemView.findViewById(R.id.txtNameItemCard);
            txtDateItemCard = itemView.findViewById(R.id.txtDateItemCard);
            txtDeleteCard = itemView.findViewById(R.id.txtDeleteCard);
        }
    }
}
