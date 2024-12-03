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

import com.example.shopping_online.adapter.CardAdapter;
import com.example.shopping_online.model.CardModel;
import com.example.shopping_online.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Objects;

public class MyCardLayout extends AppCompatActivity {

    private ArrayList<CardModel> list;
    private CardAdapter adapter;

    private FirebaseFirestore firestore;
    final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    final String userId = Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_my_card_layout);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        firestore = FirebaseFirestore.getInstance();

        Toolbar toolbarCardLayout = findViewById(R.id.toolbarCardLayout);
        RecyclerView recyclerViewCardLayout = findViewById(R.id.RecyclerViewCardLayout);
        TextView txtAddCard = findViewById(R.id.txtAddCard);

        txtAddCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MyCardLayout.this, AddItemCart.class));
            }
        });

        list = new ArrayList<>();
        recyclerViewCardLayout.setLayoutManager(new LinearLayoutManager(this));
        adapter = new CardAdapter(this, list);
        recyclerViewCardLayout.setAdapter(adapter);

        loadDataCardFromFireBase();

        setSupportActionBar(toolbarCardLayout);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Tài khoản ngân hàng");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void loadDataCardFromFireBase() {
        firestore.collection("users").document(userId).collection("card").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for(QueryDocumentSnapshot queryDocumentSnapshot: queryDocumentSnapshots){
                    CardModel cardModel = queryDocumentSnapshot.toObject(CardModel.class);
                    cardModel.setId(queryDocumentSnapshot.getId());
                    list.add(cardModel);
                }
                adapter.notifyDataSetChanged();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MyCardLayout.this, "Load lỗi", Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            startActivity(new Intent(MyCardLayout.this, AcountLayout.class));
        }
        return super.onOptionsItemSelected(item);
    }
}