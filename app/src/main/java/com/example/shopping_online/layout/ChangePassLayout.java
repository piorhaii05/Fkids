package com.example.shopping_online.layout;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
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
import com.example.shopping_online.admin.layoutadmin.AdminLayout;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;

public class ChangePassLayout extends AppCompatActivity {
    private TextInputLayout layout_OldPassword, layout_NewPassword, layout_Confirm_NewPassword;
    private TextInputEditText edt_OldPassword, edt_NewPassword, edt_Confirm_NewPassword;

    private FirebaseFirestore firestore;

    boolean checkrole;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_change_pass_layout);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        firestore = FirebaseFirestore.getInstance();
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

        Toolbar toolbarChangePassLayout = findViewById(R.id.toolbarChangePassLayout);
        layout_OldPassword = findViewById(R.id.layout_OldPassword);
        layout_NewPassword = findViewById(R.id.layout_NewPassword);
        layout_Confirm_NewPassword = findViewById(R.id.layout_Confirm_NewPassword);
        edt_OldPassword = findViewById(R.id.edt_OldPassword);
        edt_NewPassword = findViewById(R.id.edt_NewPassword);
        edt_Confirm_NewPassword = findViewById(R.id.edt_Confirm_NewPassword);
        TextView txtSubmitChangePassAcount = findViewById(R.id.txtSubmitChangePassAcount);

        setSupportActionBar(toolbarChangePassLayout);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Đổi mật khẩu");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        SharedPreferences sharedPreferencesCheckRole = getSharedPreferences("CheckRole", MODE_PRIVATE);
        checkrole = sharedPreferencesCheckRole.getBoolean("checkrole", false);

        SharedPreferences sharedPreferences = getSharedPreferences("Acount", MODE_PRIVATE);
        String pass = sharedPreferences.getString("passwordacount", "");
        String id = sharedPreferences.getString("idacount", "");

        txtSubmitChangePassAcount.setOnClickListener(v -> {
            if (validateOldPassword(pass) && validateNewPassword() && validateConfirmPassword()) {
                updatePassword(id);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            if(checkrole){
                startActivity(new Intent(ChangePassLayout.this, AdminLayout.class));
            }else {
                startActivity(new Intent(ChangePassLayout.this, MenuLayout.class));
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private boolean validateOldPassword(String pass) {
        String passold = Objects.requireNonNull(edt_OldPassword.getText()).toString().trim();
        if (!passold.equals(pass)) {
            layout_OldPassword.setError("Mật khẩu cũ của bạn không chính xác");
            return false;
        } else {
            layout_OldPassword.setError(null);
            return true;
        }
    }

    private boolean validateNewPassword() {
        String passInput = Objects.requireNonNull(edt_NewPassword.getText()).toString();
        if (passInput.length() >= 8) {
            Pattern pattern = Pattern.compile("^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[^a-zA-Z0-9]).{8,}$");
            if (pattern.matcher(passInput).matches()) {
                layout_NewPassword.setError(null);
                layout_NewPassword.setHelperText(null);
                return true;
            } else {
                layout_NewPassword.setError("Mật khẩu của bạn phải 1 chữ Hoa, 1 chữ thường, 1 số và 1 ký tự đặc biệt");
                return false;
            }
        } else {
            layout_NewPassword.setError("Mật khẩu của bạn phải lớn hơn hoặc bằng 8 ký tự");
            return false;
        }
    }

    private boolean validateConfirmPassword() {
        String passwordInput = Objects.requireNonNull(edt_NewPassword.getText()).toString().trim();
        String passwordConfirmInput = Objects.requireNonNull(edt_Confirm_NewPassword.getText()).toString().trim();
        if (!passwordInput.equals(passwordConfirmInput)) {
            layout_Confirm_NewPassword.setError("Nhập lại mật khẩu không chính xác");
            return false;
        } else {
            layout_Confirm_NewPassword.setError(null);
            layout_Confirm_NewPassword.setHelperText(null);
            return true;
        }
    }

    private void updatePassword(String id) {
        String newpass = Objects.requireNonNull(edt_NewPassword.getText()).toString().trim();
        Map<String, Object> item = new HashMap<>();
        item.put("password", newpass);
        firestore.collection("users").document(id).update(item).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    changepassword();
                }else {
                    Toast.makeText(ChangePassLayout.this, "Thay đổi mật khẩu lỗi", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void changepassword() {
        String newpass = Objects.requireNonNull(edt_NewPassword.getText()).toString().trim();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        Objects.requireNonNull(user).updatePassword(newpass).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(ChangePassLayout.this, "Thay đổi mật khẩu thành công", Toast.LENGTH_LONG).show();
                    if(checkrole){
                        startActivity(new Intent(ChangePassLayout.this, AdminLayout.class));
                    }else {
                        startActivity(new Intent(ChangePassLayout.this, MenuLayout.class));
                    }
                    finish();
                } else {
                    Toast.makeText(ChangePassLayout.this, "Thay đổi mật khẩu thất bại. Hãy thử lại.", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
