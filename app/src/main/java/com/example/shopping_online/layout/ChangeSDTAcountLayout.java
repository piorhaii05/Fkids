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
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ChangeSDTAcountLayout extends AppCompatActivity {
    private TextInputEditText edtChangeSDTAcount;
    private TextInputLayout edtChangeSDTAcountLayout;

    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_change_sdtacount_layout);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        firestore = FirebaseFirestore.getInstance();

        Toolbar toolbarChangeSDTAcountLayout = findViewById(R.id.toolbarChangeSDTAcountLayout);
        TextView txtOldSDTAcount = findViewById(R.id.txtOldSDTAcount);
        TextView txtSubmitChangeSDTAcount = findViewById(R.id.txtSubmitChangeSDTAcount);
        edtChangeSDTAcount = findViewById(R.id.edtChangeSDTAcount);
        edtChangeSDTAcountLayout = findViewById(R.id.edtChangeSDTAcountLayout);

        setSupportActionBar(toolbarChangeSDTAcountLayout);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Sửa số điện thoại");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        SharedPreferences sharedPreferences = getSharedPreferences("AcountChange", MODE_PRIVATE);
        String sdt = sharedPreferences.getString("phonenumberacount", "");
        String id = sharedPreferences.getString("userIdAcount", "");
        txtOldSDTAcount.setText(sdt);

        edtChangeSDTAcount.addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                validatePhoneNumber();
            }

            @Override
            public void afterTextChanged(android.text.Editable editable) {}
        });

        txtSubmitChangeSDTAcount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phoneNumber = Objects.requireNonNull(edtChangeSDTAcount.getText()).toString().trim();
                if (isPhoneNumberValid(phoneNumber)) {
                    ChangeSDTAcount(id);
                } else {
                    edtChangeSDTAcountLayout.setError("Số điện thoại không hợp lệ");
                }
            }
        });
    }

    private void validatePhoneNumber() {
        String phoneNumber = Objects.requireNonNull(edtChangeSDTAcount.getText()).toString().trim();
        if (phoneNumber.length() != 10 || !phoneNumber.startsWith("0")) {
            edtChangeSDTAcountLayout.setError("Số điện thoại phải bắt đầu bằng 0 và gồm 10 chữ số");
        } else {
            edtChangeSDTAcountLayout.setError(null);
        }
    }

    private boolean isPhoneNumberValid(String phoneNumber) {
        return phoneNumber.length() == 10 && phoneNumber.startsWith("0");
    }

    private void ChangeSDTAcount(String id) {
        Map<String, Object> item = new HashMap<>();
        item.put("phonenumber", Objects.requireNonNull(edtChangeSDTAcount.getText()).toString().trim());

        firestore.collection("users").document(id).update(item).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(ChangeSDTAcountLayout.this, "Update số điện thoại thành công", Toast.LENGTH_LONG).show();
                    startActivity(new Intent(ChangeSDTAcountLayout.this, MenuLayout.class));
                } else {
                    Toast.makeText(ChangeSDTAcountLayout.this, "Update lỗi", Toast.LENGTH_LONG).show();
                }
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
