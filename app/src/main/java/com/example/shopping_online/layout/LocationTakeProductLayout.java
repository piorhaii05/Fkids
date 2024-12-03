package com.example.shopping_online.layout;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.shopping_online.adapter.LocationAdapter;
import com.example.shopping_online.model.LocationModel;
import com.example.shopping_online.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Objects;

public class LocationTakeProductLayout extends AppCompatActivity {

    private ArrayList<LocationModel> list;
    private LocationAdapter adapter;

    private FirebaseFirestore firestore;
    final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    final String userId = Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_location_take_product_layout);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        firestore = FirebaseFirestore.getInstance();

        Toolbar toolbarLocationLayout = findViewById(R.id.toolbarLocationLayout);
        RecyclerView recyclerViewLocationLayout = findViewById(R.id.RecyclerViewLocationLayout);
        TextView txtAddLocation = findViewById(R.id.txtAddLocation);

        txtAddLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LocationTakeProductLayout.this, AddLocationLayout.class));
            }
        });

        recyclerViewLocationLayout.setLayoutManager(new LinearLayoutManager(this));
        list = new ArrayList<>();
        adapter = new LocationAdapter(this, list);
        recyclerViewLocationLayout.setAdapter(adapter);

        setSupportActionBar(toolbarLocationLayout);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Địa chỉ nhận hàng");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        loadDataFromFirebase();
    }

    private void loadDataFromFirebase() {
        firestore.collection("users").document(userId).collection("location").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for(QueryDocumentSnapshot queryDocumentSnapshot: queryDocumentSnapshots){
                    LocationModel locationModel = queryDocumentSnapshot.toObject(LocationModel.class);
                    locationModel.setId(queryDocumentSnapshot.getId());
                    list.add(locationModel);
                }
                adapter.notifyDataSetChanged();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(LocationTakeProductLayout.this, "Load Lỗi", Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            startActivity(new Intent(LocationTakeProductLayout.this, AcountLayout.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}