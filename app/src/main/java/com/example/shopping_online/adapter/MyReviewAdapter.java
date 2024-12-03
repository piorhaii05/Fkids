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

import com.example.shopping_online.model.MyReviewModel;
import com.example.shopping_online.R;

import java.util.ArrayList;

public class MyReviewAdapter extends RecyclerView.Adapter<MyReviewAdapter.ViewHolderMyReview> {

    private final Context context;
    private final ArrayList<MyReviewModel> list;

    public MyReviewAdapter(Context context, ArrayList<MyReviewModel> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolderMyReview onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = ((Activity) context).getLayoutInflater();
        View view = inflater.inflate(R.layout.item_myreview, parent, false);
        return new ViewHolderMyReview(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderMyReview holder, int position) {
        MyReviewModel myReviewModel = list.get(position);

        holder.imageAvatarItemMyReview.setImageResource(R.drawable.avatar);
        holder.imageProductItemMyReview.setImageResource(R.drawable.product1);
        holder.txtNameItemMyReview.setText(myReviewModel.getItems_name_myreview());
        holder.txtRatingItemMyReview.setText(String.valueOf(myReviewModel.getItems_rating_myreview()));
        holder.txtDescribeItemMyReview.setText(myReviewModel.getItems_describe_myreview());
        holder.txtDateItemMyReview.setText(myReviewModel.getItems_date_myreview());
        holder.txtColorItemMyReview.setText(myReviewModel.getItems_color_myreview());
        holder.txtSizeItemMyReview.setText(myReviewModel.getItems_size_myreview());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolderMyReview extends RecyclerView.ViewHolder {
        final ImageView imageAvatarItemMyReview;
        final ImageView imageProductItemMyReview;
        final TextView txtNameItemMyReview;
        final TextView txtRatingItemMyReview;
        final TextView txtDescribeItemMyReview;
        final TextView txtDateItemMyReview;
        final TextView txtColorItemMyReview;
        final TextView txtSizeItemMyReview;
        public ViewHolderMyReview(@NonNull View itemView) {
            super(itemView);
            imageAvatarItemMyReview = itemView.findViewById(R.id.imageAvatarItemMyReview);
            imageProductItemMyReview = itemView.findViewById(R.id.imageProductItemMyReview);
            txtNameItemMyReview = itemView.findViewById(R.id.txtNameItemMyReview);
            txtRatingItemMyReview = itemView.findViewById(R.id.txtRatingItemMyReview);
            txtDescribeItemMyReview = itemView.findViewById(R.id.txtDescribeItemMyReview);
            txtDateItemMyReview = itemView.findViewById(R.id.txtDateItemMyReview);
            txtColorItemMyReview = itemView.findViewById(R.id.txtColorItemMyReview);
            txtSizeItemMyReview = itemView.findViewById(R.id.txtSizeItemMyReview);
        }
    }
}
