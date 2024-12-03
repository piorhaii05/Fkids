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

import com.example.shopping_online.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class UpdateLocationLayout extends AppCompatActivity {
    private EditText txtCityLocationNew, txtDistrictLocationNew, txtWardLocationNew, txtStreetLocationNew;

    private FirebaseFirestore firestore;
    final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    final String userId = Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_update_location_layout);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        firestore = FirebaseFirestore.getInstance();

        Toolbar toolbarUpdateLocationLayout = findViewById(R.id.toolbarUpdateLocationLayout);
        TextView txtUsernameOldLocation = findViewById(R.id.txtUsernameOldLocation);
        TextView txtSDTOldLocation = findViewById(R.id.txtSDTOldLocation);
        TextView txtOldLocation = findViewById(R.id.txtOldLocation);
        TextView txtAddLocationToFirebaseNew = findViewById(R.id.txtAddLocationToFirebaseNew);
        txtCityLocationNew = findViewById(R.id.txtCityLocationNew);
        txtDistrictLocationNew = findViewById(R.id.txtDistrictLocationNew);
        txtWardLocationNew = findViewById(R.id.txtWardLocationNew);
        txtStreetLocationNew = findViewById(R.id.txtStreetLocationNew);


        SharedPreferences sharedPreferences = getSharedPreferences("AcountChange", MODE_PRIVATE);
        String sdt = sharedPreferences.getString("phonenumberacount", "");
        String user = sharedPreferences.getString("nameacount", "");
        String id = sharedPreferences.getString("userIdAcount", "");
        txtUsernameOldLocation.setText(user);
        txtSDTOldLocation.setText(sdt);

        setSupportActionBar(toolbarUpdateLocationLayout);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Sửa địa chỉ nhận hàng");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        String idLocation = intent.getStringExtra("id");
        String street = intent.getStringExtra("street_location");
        String ward = intent.getStringExtra("ward_location");
        String district = intent.getStringExtra("district_location");
        String city = intent.getStringExtra("city_location");

        String location = street + ", Phường " + ward + ", Quận " + district + ", Thành Phố " + city;
        txtOldLocation.setText(location);
        txtStreetLocationNew.setText(street);
        txtWardLocationNew.setText(ward);
        txtDistrictLocationNew.setText(district);
        txtCityLocationNew.setText(city);

        txtAddLocationToFirebaseNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UpdateLocation(idLocation);
            }
        });
    }

    private void UpdateLocation(String idLocation) {

        String streetnew = txtStreetLocationNew.getText().toString().trim();
        String wardnew = txtWardLocationNew.getText().toString().trim();
        String districtnew = txtDistrictLocationNew.getText().toString().trim();
        String citynew = txtCityLocationNew.getText().toString().trim();

        Map<String, Object> item = new HashMap<>();
        item.put("street_location", streetnew);
        item.put("ward_location", wardnew);
        item.put("district_location", districtnew);
        item.put("city_location", citynew);

        firestore.collection("users").document(userId).collection("location").document(idLocation).update(item).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    startActivity(new Intent(UpdateLocationLayout.this, LocationTakeProductLayout.class));
                }else {
                    Toast.makeText(UpdateLocationLayout.this, "Sửa lỗi", Toast.LENGTH_LONG).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(UpdateLocationLayout.this, "Sửa lỗi", Toast.LENGTH_LONG).show();
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