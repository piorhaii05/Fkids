package com.example.shopping_online.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.shopping_online.model.LocationModel;
import com.example.shopping_online.layout.UpdateLocationLayout;
import com.example.shopping_online.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Objects;

public class LocationAdapter extends RecyclerView.Adapter<LocationAdapter.ViewHolderLocation> {

    private final Context context;
    private final ArrayList<LocationModel> list;
    private LocationModel locationModel;
    private FirebaseFirestore firestore;
    final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    final String userId = Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid();

    public LocationAdapter(Context context, ArrayList<LocationModel> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolderLocation onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = ((Activity) context).getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_item_location, parent, false);
        return new ViewHolderLocation(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderLocation holder, int position) {
        locationModel = list.get(position);
        firestore = FirebaseFirestore.getInstance();

        String street = locationModel.getStreet_location();
        String ward = locationModel.getWard_location();
        String district = locationModel.getDistrict_location();
        String city = locationModel.getCity_location();

        String location = street + ", Phường " + ward + ", Quận " + district + ", Thành Phố " + city;

        holder.txtNameLocaion.setText(location);
        holder.txtDeleteLocaion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(context)
                        .setTitle("Delete Location")
                        .setMessage("Bạn có chắc chắn muốn xóa địa chỉ này không?")
                        .setPositiveButton("OK", (dialog, which) -> deleteLocationFromFireBase(locationModel, position))
                        .setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss())
                        .create()
                        .show();
            }
        });

        holder.txtUpdateLocaion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, UpdateLocationLayout.class);
                intent.putExtra("id", locationModel.getId());
                intent.putExtra("street_location", locationModel.getStreet_location());
                intent.putExtra("ward_location", locationModel.getWard_location());
                intent.putExtra("district_location", locationModel.getDistrict_location());
                intent.putExtra("city_location", locationModel.getCity_location());
                context.startActivity(intent);
            }
        });
    }

    private void deleteLocationFromFireBase(LocationModel locationModel, int position) {
        firestore.collection("users").document(userId).collection("card").document(locationModel.getId()).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
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

    class ViewHolderLocation extends RecyclerView.ViewHolder {
        final TextView txtNameLocaion;
        final TextView txtUpdateLocaion;
        final TextView txtDeleteLocaion;

        public ViewHolderLocation(@NonNull View itemView) {
            super(itemView);
            txtNameLocaion = itemView.findViewById(R.id.txtNameLocaion);
            txtUpdateLocaion = itemView.findViewById(R.id.txtUpdateLocaion);
            txtDeleteLocaion = itemView.findViewById(R.id.txtDeleteLocaion);
        }
    }
}
