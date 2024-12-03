package com.example.shopping_online.layout;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.shopping_online.admin.layoutadmin.AdminLayout;
import com.example.shopping_online.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LoginLayout extends AppCompatActivity {
    private FirebaseAuth firebaseAuth;

    private TextInputLayout layout_Username, layout_Password;
    private TextView txt_Admin;
    private TextView txt_User;
    private TextInputEditText edt_Password, edt_Username;
    private CheckBox checkbox_remember;

    private boolean checkEmpty;
    private boolean validate = false;
    private boolean checkrole = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login_layout);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        firebaseAuth = FirebaseAuth.getInstance();

        layout_Password = findViewById(R.id.layout_Password);
        layout_Username = findViewById(R.id.layout_Username);
        txt_User = findViewById(R.id.txt_User);
        txt_Admin = findViewById(R.id.txt_Admin);
        edt_Password = findViewById(R.id.edt_Password);
        edt_Username = findViewById(R.id.edt_Username);
        TextView txt_forgot_Password = findViewById(R.id.txt_forgot_Password);
        TextView txt_goto_Register = findViewById(R.id.txt_goto_Register);
        Button btn_Login = findViewById(R.id.btn_login);
        checkbox_remember = findViewById(R.id.checkbox_remember);

//        loadAcountToScreen();
        chooseRule();
        validateUserAndPassword();

        txt_goto_Register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginLayout.this, RegisterLayout.class));
            }
        });
        btn_Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginAccount();
            }
        });

    }

    private void chooseRule() {
        txt_User.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txt_User.setBackgroundResource(R.drawable.background_choose_user);
                txt_Admin.setBackground(null);
                checkrole = false;
            }
        });
        txt_Admin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txt_Admin.setBackgroundResource(R.drawable.background_choose_user);
                txt_User.setBackground(null);
                checkrole = true;
            }
        });
    }

    private void loginAccount() {

        String user = Objects.requireNonNull(edt_Username.getText()).toString().trim();
        String pass = Objects.requireNonNull(edt_Password.getText()).toString().trim();

        validateEmpty();
        validateText();


        if (validate) {
            firebaseAuth.signInWithEmailAndPassword(user, pass).addOnCompleteListener(LoginLayout.this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(LoginLayout.this, "Đăng nhập thành công", Toast.LENGTH_LONG).show();
                        SharedPreferences sharedPreferences = getSharedPreferences("UserCheck", MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("email", user);
                        editor.apply();
                        if (checkbox_remember.isChecked()) {
                            saveLoginWithCheckbox();
                        } else {
                            deleteInformationOfAcount();
                        }
//                        startActivity(new Intent(Login_Layout.this, ASM_GD1.class));
                        SharedPreferences sharedPreferencesCheckRole = getSharedPreferences("CheckRole", MODE_PRIVATE);
                        SharedPreferences.Editor editorCheckRole = sharedPreferencesCheckRole.edit();
                        editorCheckRole.putBoolean("checkrole", checkrole);
                        editorCheckRole.apply();

                        if(!checkrole){
                            startActivity(new Intent(LoginLayout.this, MenuLayout.class));
                        }else {
                            startActivity(new Intent(LoginLayout.this, AdminLayout.class));
                        }
                        finish();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(LoginLayout.this, "Tài khoản hoặc mật khẩu của bạn không chính xác", Toast.LENGTH_LONG).show();

                }
            });
        }

    }

    private void saveLoginWithCheckbox() {
        String username = Objects.requireNonNull(edt_Username.getText()).toString().trim();
        String password = Objects.requireNonNull(edt_Password.getText()).toString().trim();
        // Lấy thông tin từ SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("User", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("username", username);
        editor.putString("password", password);
        editor.apply();
        // In thông tin ra Log hoặc Toast
//        Toast.makeText(Login_Layout.this, "Tài khoản: " + username + "\nMật khẩu: " + password, Toast.LENGTH_LONG).show();
        Log.d("login", "Tài khoản: " + username + "\nMật khẩu: " + password);
    }

    private void deleteInformationOfAcount() {
        // Lấy thông tin từ SharedPreferences
        String username = Objects.requireNonNull(edt_Username.getText()).toString().trim();

        SharedPreferences sharedPreferences = getSharedPreferences("User", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("username", username);
        editor.putString("password", "");
        editor.apply();
    }

    private void validateText() {
        String user = Objects.requireNonNull(edt_Username.getText()).toString().trim();
        String pass = Objects.requireNonNull(edt_Password.getText()).toString().trim();

        validateEmpty();

        Pattern pattern = Pattern.compile("^[a-zA-Z0-9._-]+@gmail\\.com$");
        Matcher matcher = pattern.matcher(user);
        boolean userMatch = matcher.find();

        if (checkEmpty) {
            if (!userMatch) {
                layout_Username.setError("Tài khoản của bạn không đúng định dạng \"@gmail.com\"");
            } else {
                if (pass.length() < 8) {
                    layout_Password.setError("Mật khẩu của bạn phải lớn hơn hoặc bằng 8 ký tự");

                } else {
                    Pattern patternPass = Pattern.compile("^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[^a-zA-Z0-9]).{8,}$");
                    Matcher matcherPass = patternPass.matcher(pass);
                    boolean passMatch = matcherPass.find();
                    if (!passMatch) {
                        layout_Password.setError("Mật khẩu của bạn phải 1 chữ Hoa, 1 chữ thường, 1 số và 1 ký tự đặc biệt");
                    } else {
                        validate = true;
                    }
                }
            }
        }
    }

    private void validateEmpty() {
        String user = Objects.requireNonNull(edt_Username.getText()).toString().trim();
        String pass = Objects.requireNonNull(edt_Password.getText()).toString().trim();

        if (user.isEmpty()) {
            layout_Username.setError("Vui lòng điền thông tin tài khoản");
            checkEmpty = false;
        }
        if (pass.isEmpty()) {
            layout_Password.setError("Vui lòng điền mật khẩu");
            checkEmpty = false;
        } else {
            checkEmpty = true;

        }
    }


    private void validateUserAndPassword() {

        // Set điều kiện cho password
        edt_Password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String passInput = s.toString();
                if (passInput.length() >= 8) {
                    // Điều kiện định dạng 1 chữ Hoa, 1 chữ thường, 1 số và 1 kí tự đặc biệt
                    Pattern pattern = Pattern.compile("^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[^a-zA-Z0-9]).{8,}$");
                    Matcher matcher = pattern.matcher(passInput);
                    boolean passMatch = matcher.find();
                    if (passMatch) {
                        // khi passMatch trả về true, sẽ không hiện lỗi
                        layout_Password.setError("");
                        layout_Password.setHelperText("");
                    } else {
                        // hiện lỗi của edittext khi nhập không đúng điều kiện
                        layout_Password.setError("Mật khẩu của bạn phải 1 chữ Hoa, 1 chữ thường, 1 số và 1 ký tự đặc biệt");
                    }
                } else {
                    // hiện lỗi của edittext khi nhập không đúng điều kiện
                    layout_Password.setError("Mật khẩu của bạn phải lớn hơn hoặc bằng 8 ký tự");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        // Set điều kiện username
        edt_Username.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String passInput = s.toString();
                // Điều kiện định dạng @gmail.com
                Pattern pattern = Pattern.compile("^[a-zA-Z0-9._-]+@gmail\\.com$");
                Matcher matcher = pattern.matcher(passInput);
                boolean passMatch = matcher.find();
                if (passMatch) {
                    layout_Username.setHelperText("");
                    layout_Username.setError("");
                } else {
                    // Trả lỗi lên màn hình
                    layout_Username.setError("Tài khoản của bạn không đúng định dạng \"@gmail.com\" ");
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

}
