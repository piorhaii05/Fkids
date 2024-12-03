package com.example.shopping_online.layout;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AddItemCart extends AppCompatActivity {
    private TextInputLayout layout_Username_Card, layout_Number_Card, layout_CVV_Card, layout_Date_Card;
    private TextInputEditText edt_Date_Card, edt_CVV_Card, edt_Number_Card, edt_Username_Card;
    final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    final String userId = Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid();

    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_item_cart);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        firestore = FirebaseFirestore.getInstance();

        layout_Username_Card = findViewById(R.id.layout_Username_Card);
        layout_Number_Card = findViewById(R.id.layout_Number_Card);
        layout_CVV_Card = findViewById(R.id.layout_CVV_Card);
        layout_Date_Card = findViewById(R.id.layout_Date_Card);
        edt_Date_Card = findViewById(R.id.edt_Date_Card);
        edt_CVV_Card = findViewById(R.id.edt_CVV_Card);
        edt_Number_Card = findViewById(R.id.edt_Number_Card);
        edt_Username_Card = findViewById(R.id.edt_Username_Card);
        TextView txt_Submit_Add_Card = findViewById(R.id.txt_Submit_Add_Card);
        Toolbar toolbarAddCardLayout = findViewById(R.id.toolbarAddCardLayout);

        validatetext();

        txt_Submit_Add_Card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateEditTextWhenSubmit();
            }
        });

        setSupportActionBar(toolbarAddCardLayout);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Thêm ngân hàng");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    private void validateEditTextWhenSubmit() {
        String number = Objects.requireNonNull(edt_Number_Card.getText()).toString().trim();
        String cvv = Objects.requireNonNull(edt_CVV_Card.getText()).toString().trim();
        String date = Objects.requireNonNull(edt_Date_Card.getText()).toString().trim();
        String user = Objects.requireNonNull(edt_Username_Card.getText()).toString().trim();

        boolean isValid = true;

        // Kiểm tra rỗng và các điều kiện cụ thể
        if (user.isEmpty()) {
            layout_Username_Card.setError("Vui lòng nhập tên người dùng!");
            isValid = false;
        } else {
            layout_Username_Card.setError(null);
        }

        if (number.isEmpty()) {
            layout_Number_Card.setError("Vui lòng nhập số tài khoản!");
            isValid = false;
        } else if (number.length() != 12) {
            layout_Number_Card.setError("Số tài khoản phải gồm 12 số!");
            isValid = false;
        } else {
            layout_Number_Card.setError(null);
        }

        if (cvv.isEmpty()) {
            layout_CVV_Card.setError("Vui lòng nhập CVV!");
            isValid = false;
        } else if (cvv.length() != 3) {
            layout_CVV_Card.setError("CVV phải gồm 3 số!");
            isValid = false;
        } else {
            layout_CVV_Card.setError(null);
        }

        if (date.isEmpty()) {
            layout_Date_Card.setError("Vui lòng nhập ngày hết hạn!");
            isValid = false;
        } else {
            Pattern pattern = Pattern.compile("^\\d{2}/\\d{2}/\\d{2}$");
            Matcher matcher = pattern.matcher(date);
            if (!matcher.find()) {
                layout_Date_Card.setError("Không đúng định dạng XX/XX/XX!");
                isValid = false;
            } else {
                layout_Date_Card.setError(null);
            }
        }

        // Nếu tất cả hợp lệ
        if (isValid) {
            String four_number = number.substring(number.length()-4);

            Map<String, Object> item = new HashMap<>();
            item.put("name_acount_card", user);
            item.put("number_acount_card", number);
            item.put("date_card", date);
            item.put("CVV_card", cvv);
            item.put("four_number_card", four_number);

            firestore.collection("users").document(userId).collection("card").add(item).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                @Override
                public void onSuccess(DocumentReference documentReference) {
                    Toast.makeText(AddItemCart.this, "Thêm 1 thẻ ngân hàng", Toast.LENGTH_LONG).show();
                    startActivity(new Intent(AddItemCart.this, MyCardLayout.class));
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(AddItemCart.this, "Thêm lỗi", Toast.LENGTH_LONG).show();
                }
            });
        } else {
            Toast.makeText(AddItemCart.this, "Vui lòng sửa lại thông tin", Toast.LENGTH_LONG).show();
        }
    }


    private void validatetext() {
        edt_Number_Card.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String numbercard = Objects.requireNonNull(edt_Number_Card.getText()).toString().trim();
                if(numbercard.length() != 12){
                    layout_Number_Card.setError("Số tài khoản phải gồm 12 số!");
                }
                else {
                    layout_Number_Card.setError("");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        edt_CVV_Card.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String CVVcard = Objects.requireNonNull(edt_CVV_Card.getText()).toString().trim();
                if(CVVcard.length() != 3){
                    layout_CVV_Card.setError("CVV phải gồm 3 số!");
                }
                else{
                    layout_CVV_Card.setError("");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        edt_Date_Card.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String datecard = Objects.requireNonNull(edt_Date_Card.getText()).toString().trim();

                Pattern pattern = Pattern.compile("^\\d{2}/\\d{2}/\\d{2}$");
                Matcher matcher = pattern.matcher(datecard);
                boolean passMatch = matcher.find();

                if(!passMatch){
                    layout_Date_Card.setError("Không đúng định dạng XX/XX/XX");
                }
                else {
                    layout_Date_Card.setError("");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }
}