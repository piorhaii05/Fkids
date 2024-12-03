package com.example.shopping_online.admin.layoutadmin;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class DetailVoucherAdminLayout extends AppCompatActivity {

    Toolbar toolbarDetailVoucherLayout;
    TextInputEditText edt_valueVoucher_new, edt_dateVoucher_new, edt_nameVoucher_new;
    TextView txtSubmitAllVouchertDetail, txt_statusVoucher_new;

    FirebaseFirestore firestore;

    private boolean statusNew = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_detail_voucher_admin_layout);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        firestore = FirebaseFirestore.getInstance();

        toolbarDetailVoucherLayout = findViewById(R.id.toolbarDetailVoucherLayout);
        edt_valueVoucher_new = findViewById(R.id.edt_valueVoucher_new);
        edt_dateVoucher_new = findViewById(R.id.edt_dateVoucher_new);
        edt_nameVoucher_new = findViewById(R.id.edt_nameVoucher_new);
        txtSubmitAllVouchertDetail = findViewById(R.id.txtSubmitAllVouchertDetail);
        txt_statusVoucher_new = findViewById(R.id.txt_statusVoucher_new);

        SharedPreferences sharedPreferences = getSharedPreferences("VoucherAdmin", MODE_PRIVATE);
        String id = sharedPreferences.getString("documentId", "");
        String name = sharedPreferences.getString("name", "");
        String date = sharedPreferences.getString("date", "");
        boolean status = Boolean.parseBoolean(sharedPreferences.getString("status", ""));
        String value = sharedPreferences.getString("value", "");

        Log.d("status", "status " + status);
        Log.d("status", "value " + value);

        edt_nameVoucher_new.setText(name);
        edt_dateVoucher_new.setText(date);
        edt_valueVoucher_new.setText(value);
        txt_statusVoucher_new.setText(status ? "Có thể dùng" : "Đã hết hạn");
        txt_statusVoucher_new.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialogSelected();
            }
        });

        txtSubmitAllVouchertDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String name = Objects.requireNonNull(edt_nameVoucher_new.getText()).toString().trim();
                String date = Objects.requireNonNull(edt_dateVoucher_new.getText()).toString().trim();
                String valueStr = Objects.requireNonNull(edt_valueVoucher_new.getText()).toString().trim();
                if (valueStr.isEmpty()) {
                    Toast.makeText(DetailVoucherAdminLayout.this, "Vui lòng nhập giá trị voucher", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(name.isEmpty() || date.isEmpty()){
                    Toast.makeText(DetailVoucherAdminLayout.this, "Vui lòng không để trống thông tin", Toast.LENGTH_SHORT).show();
                    return;
                }
                double value = Double.parseDouble(valueStr);

                Map<String, Object> item = new HashMap<>();
                item.put("title_voucher", name);
                item.put("date_voucher", date);
                item.put("value_voucher", value);
                item.put("image_voucher", "123");
                item.put("status_voucher", statusNew);

                Map<String, Object> itemUpdate = new HashMap<>();
                itemUpdate.put("title_voucher", name);
                itemUpdate.put("date_voucher", date);
                itemUpdate.put("value_voucher", value);
                itemUpdate.put("image_voucher", "123");
                itemUpdate.put("status_voucher", statusNew);

                if (id.isEmpty()) {
                    addVoucherToFireBase(item);
                } else {
                    updateVoucherToFireBase(itemUpdate, id);
                }
            }
        });

        setSupportActionBar(toolbarDetailVoucherLayout);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Hồ sơ");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void updateVoucherToFireBase(Map<String, Object> itemUpdate, String id) {
        firestore.collection("voucher").document(id).update(itemUpdate).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(DetailVoucherAdminLayout.this, "Update thành công", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(DetailVoucherAdminLayout.this, AdminLayout.class));
                }
                else {
                    Toast.makeText(DetailVoucherAdminLayout.this, "Update thành công", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void showDialogSelected() {
        String[] options = {"Có thể dùng", "Đã hết hạn"};

        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Chọn trạng thái")
                .setSingleChoiceItems(options, statusNew ? 0 : 1, (dialog, which) -> {
                    statusNew = (which == 0);
                })
                .setPositiveButton("Xác nhận", (dialog, which) -> {
                    txt_statusVoucher_new.setText(statusNew ? "Có thể dùng" : "Đã hết hạn");
                })
                .setNegativeButton("Hủy", (dialog, which) -> {
                    dialog.dismiss();
                })
                .show();
    }

    private void addVoucherToFireBase(Map<String, Object> item) {
        firestore.collection("voucher").add(item).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
            @Override
            public void onComplete(@NonNull Task<DocumentReference> task) {
                if(task.isSuccessful()){
                    Toast.makeText(DetailVoucherAdminLayout.this, "Add thành công", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(DetailVoucherAdminLayout.this, AdminLayout.class));
                }
                else {
                    Toast.makeText(DetailVoucherAdminLayout.this, "Add thành công", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

}