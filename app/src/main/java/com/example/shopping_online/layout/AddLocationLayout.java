package com.example.shopping_online.layout;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.shopping_online.model.LocationModel;
import com.example.shopping_online.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class AddLocationLayout extends AppCompatActivity {
    private EditText txtCityLocation, txtDistrictLocation, txtWardLocation, txtStreetLocation;

    private ArrayList<LocationModel> list;
    private FirebaseFirestore firestore;
    final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    final String userId = Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_location_layout);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        firestore = FirebaseFirestore.getInstance();

        Toolbar toolbarAddLocationLayout = findViewById(R.id.toolbarAddLocationLayout);
        TextView txtNameUserLocation = findViewById(R.id.txtNameUserLocation);
        TextView txtSDTUserLocation = findViewById(R.id.txtSDTUserLocation);
        TextView txtAddLocationToFirebase = findViewById(R.id.txtAddLocationToFirebase);
        txtCityLocation = findViewById(R.id.txtCityLocation);
        txtDistrictLocation = findViewById(R.id.txtDistrictLocation);
        txtWardLocation = findViewById(R.id.txtWardLocation);
        txtStreetLocation = findViewById(R.id.txtStreetLocation);

        SharedPreferences sharedPreferences = getSharedPreferences("AcountChange", MODE_PRIVATE);
        String user = sharedPreferences.getString("nameacount", "");
        String sdt = sharedPreferences.getString("phonenumberacount", "");
        String id = sharedPreferences.getString("userIdAcount", "");
        txtNameUserLocation.setText(user);
        txtSDTUserLocation.setText(sdt);

        setSupportActionBar(toolbarAddLocationLayout);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Thêm địa chỉ nhận hàng");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        txtAddLocationToFirebase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addLocationOnFireBase();
            }
        });
    }

    private void addLocationOnFireBase() {

        String street = txtStreetLocation.getText().toString().trim();
        String ward = txtWardLocation.getText().toString().trim();
        String district = txtDistrictLocation.getText().toString().trim();
        String city = txtCityLocation.getText().toString().trim();

        Map<String, Object> item = new HashMap<>();
        item.put("street_location", street);
        item.put("ward_location", ward);
        item.put("district_location", district);
        item.put("city_location", city);

        firestore.collection("users").document(userId).collection("location").add(item).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
            @Override
            public void onComplete(@NonNull Task<DocumentReference> task) {
                if(task.isSuccessful()){
                    startActivity(new Intent(AddLocationLayout.this, LocationTakeProductLayout.class));
                }else {
                    Toast.makeText(AddLocationLayout.this, "Lỗi thêm địa chỉ", Toast.LENGTH_LONG).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(AddLocationLayout.this, "Add lỗi", Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}