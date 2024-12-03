package com.example.shopping_online.layout;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
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

import com.example.shopping_online.adapter.HistoryPaymentAdapter;
import com.example.shopping_online.model.HistoryPaymentModel;
import com.example.shopping_online.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Objects;

public class HistoryPaymentLayout extends AppCompatActivity {

    private ArrayList<HistoryPaymentModel> list;
    private HistoryPaymentAdapter adapter;
    private FirebaseFirestore firestore;
    final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    final String userId = Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_history_payment_layout);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        firestore = FirebaseFirestore.getInstance();

        Toolbar toolbarHistoryLayout = findViewById(R.id.toolbarHistoryLayout);
        RecyclerView recyclerViewHistory = findViewById(R.id.RecyclerViewHistory);

        setSupportActionBar(toolbarHistoryLayout);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Lịch sử đặt hàng");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        recyclerViewHistory.setLayoutManager(new LinearLayoutManager(this));
        list = new ArrayList<>();
        adapter = new HistoryPaymentAdapter(this, list);
        recyclerViewHistory.setAdapter(adapter);

        loadDataHistoryFromFirebase();

    }

    private void loadDataHistoryFromFirebase() {
        firestore.collection("users").document(userId).collection("historypayment").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for(QueryDocumentSnapshot queryDocumentSnapshot: queryDocumentSnapshots){
                    HistoryPaymentModel historyPaymentModel = queryDocumentSnapshot.toObject(HistoryPaymentModel.class);
                    historyPaymentModel.setId(queryDocumentSnapshot.getId());
                    list.add(historyPaymentModel);
                }
                adapter.notifyDataSetChanged();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(HistoryPaymentLayout.this, "Load lỗi", Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            startActivity(new Intent(HistoryPaymentLayout.this, AcountLayout.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}