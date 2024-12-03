package com.example.shopping_online.layout;

import android.content.Intent;
import android.content.SharedPreferences;
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

import com.example.shopping_online.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ChangeNameAcountLayout extends AppCompatActivity {
    private TextInputEditText edtChangeNameAcount;

    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_change_name_acount_layout);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        firestore = FirebaseFirestore.getInstance();

        Toolbar toolbarChangeNameAcountLayout = findViewById(R.id.toolbarChangeNameAcountLayout);
        TextView txtOldNameAcount = findViewById(R.id.txtOldNameAcount);
        TextView txtSubmitChangeNameAcount = findViewById(R.id.txtSubmitChangeNameAcount);
        edtChangeNameAcount = findViewById(R.id.edtChangeNameAcount);

        setSupportActionBar(toolbarChangeNameAcountLayout);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Sửa tên tài khoản");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        SharedPreferences sharedPreferences = getSharedPreferences("AcountChange", MODE_PRIVATE);
        String name = sharedPreferences.getString("nameacount", "");
        String id = sharedPreferences.getString("userIdAcount", "");
        txtOldNameAcount.setText(name);

        txtSubmitChangeNameAcount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChangeUserNameAcount(id);
            }
        });
    }

    private void ChangeUserNameAcount(String id) {

        Map<String, Object> item = new HashMap<>();
        item.put("nameuser", Objects.requireNonNull(edtChangeNameAcount.getText()).toString().trim());

        firestore.collection("users").document(id).update(item).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(ChangeNameAcountLayout.this, "Update tên người dùng thành công", Toast.LENGTH_LONG).show();
                    startActivity(new Intent(ChangeNameAcountLayout.this, MenuLayout.class));
                }else {
                    Toast.makeText(ChangeNameAcountLayout.this, "Update lỗi", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}