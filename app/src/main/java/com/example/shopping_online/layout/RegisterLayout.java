package com.example.shopping_online.layout;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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

import com.example.shopping_online.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.WriteBatch;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegisterLayout extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;
    private FirebaseFirestore firestore;

    private TextInputLayout layout_Password_Confirm_Register, layout_Password_Register, layout_Username_Register;
    private TextInputEditText edt_Username_Register, edt_Password_Register, edt_Password_Confirm_Register;
    private CheckBox checkbox_rule;

    private boolean checkEmpty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register_layout);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        firebaseAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Loading ...!");
        firestore = FirebaseFirestore.getInstance();

        layout_Username_Register = findViewById(R.id.layout_Username_Register);
        layout_Password_Register = findViewById(R.id.layout_Password_Register);
        layout_Password_Confirm_Register = findViewById(R.id.layout_Password_Confirm_Register);
        edt_Username_Register = findViewById(R.id.edt_Username_Register);
        edt_Password_Register = findViewById(R.id.edt_Password_Register);
        edt_Password_Confirm_Register = findViewById(R.id.edt_Password_Confirm_Register);
        checkbox_rule = findViewById(R.id.checkbox_rule);
        Button btn_register = findViewById(R.id.btn_register);
        TextView txt_goto_Login = findViewById(R.id.txt_goto_Login);

        txt_goto_Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegisterLayout.this, LoginLayout.class));
            }
        });

        validateUserPass();

        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerAccount();
            }
        });

    }

    private void registerAccount() {

        String user = Objects.requireNonNull(edt_Username_Register.getText()).toString().trim();
        String password = Objects.requireNonNull(edt_Password_Register.getText()).toString().trim();
        String passwordConfirm = Objects.requireNonNull(edt_Password_Confirm_Register.getText()).toString().trim();

        validateEmpty();
        validateText();
        progressDialog.show();

        if (!checkbox_rule.isChecked()) {
            Toast.makeText(RegisterLayout.this, "Vui lòng chấp nhận mọi điều khoản mà chúng tôi đưa ra!", Toast.LENGTH_LONG).show();
            progressDialog.dismiss();
        } else {
            firebaseAuth.createUserWithEmailAndPassword(user, password).addOnCompleteListener(RegisterLayout.this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                        String uid = Objects.requireNonNull(firebaseUser).getUid();

                        SharedPreferences sharedPreferences = getSharedPreferences("Acount", MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("nameacount", "user" + uid);
                        editor.putString("idacount", uid);
                        editor.putString("phoneacount", "");
                        editor.putString("passwordacount", password);
                        editor.apply();

                        HashMap<String, Object> userData = new HashMap<>();
                        userData.put("username", user);
                        userData.put("password", password);
                        userData.put("nameuser", "user" + uid);
                        userData.put("phonenumber", "");
                        userData.put("gender", false);

                        createAcount(userData, uid);

                        Toast.makeText(RegisterLayout.this, "Đăng ký thành công", Toast.LENGTH_LONG).show();
                        startActivity(new Intent(RegisterLayout.this, LoginLayout.class));
                        progressDialog.dismiss();
                        finish();
                    } else {
                        Toast.makeText(RegisterLayout.this, "Đăng ký thất bại!", Toast.LENGTH_LONG).show();
                        progressDialog.dismiss();
                    }
                }
            });
        }
    }

    private void createAcount(HashMap<String, Object> userData, String uid) {
        firestore.collection("users").document(uid).set(userData).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    // Tạo các subcollections
                    createDefaultSubcollections(firestore, uid);

                    progressDialog.dismiss();
                    finish();
                } else {
                    Toast.makeText(RegisterLayout.this, "Đăng ký thất bại!", Toast.LENGTH_LONG).show();
                    progressDialog.dismiss();
                }
            }
        });
    }


    private void validateText() {
        String user = Objects.requireNonNull(edt_Username_Register.getText()).toString().trim();
        String password = Objects.requireNonNull(edt_Password_Register.getText()).toString().trim();
        String passwordConfirm = Objects.requireNonNull(edt_Password_Confirm_Register.getText()).toString().trim();

        validateEmpty();
        progressDialog.show();

        Pattern pattern = Pattern.compile("^[a-zA-Z0-9._-]+@gmail\\.com$");
        Matcher matcher = pattern.matcher(user);
        boolean userMatch = matcher.find();

        if (checkEmpty) {
            if (!userMatch) {
                layout_Username_Register.setError("Tài khoản của bạn không đúng định dạng \"@gmail.com\" ");
                progressDialog.dismiss();
            } else {
                if (password.length() < 8) {
                    layout_Password_Register.setError("Mật khẩu của bạn phải lớn hơn hoặc bằng 8 ký tự");
                    progressDialog.dismiss();

                } else {
                    Pattern patternPass = Pattern.compile("^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[^a-zA-Z0-9]).{8,}$");
                    Matcher matcherPass = patternPass.matcher(password);
                    boolean userMatchPass = matcherPass.find();
                    if (!userMatchPass) {
                        layout_Password_Register.setError("Mật khẩu của bạn phải 1 chữ Hoa, 1 chữ thường, 1 số và 1 ký tự đặc biệt");
                        progressDialog.dismiss();

                    } else {
                        if (!passwordConfirm.equals(password)) {
                            layout_Password_Confirm_Register.setError("Nhập lại mật khẩu không chính xác");
                            progressDialog.dismiss();

                        } else {
                            boolean validate = true;
                        }
                    }
                }

            }
        }
    }


    private void validateEmpty() {
        String user = Objects.requireNonNull(edt_Username_Register.getText()).toString().trim();
        String password = Objects.requireNonNull(edt_Password_Register.getText()).toString().trim();
        String passwordConfirm = Objects.requireNonNull(edt_Password_Confirm_Register.getText()).toString().trim();

        if (user.isEmpty()) {
            layout_Username_Register.setError("Vui lòng không để trống tài khoản");
            checkEmpty = false;
        }
        if (password.isEmpty()) {
            layout_Password_Register.setError("Vui lòng không để trống tài khoản");
            checkEmpty = false;
        }
        if (passwordConfirm.isEmpty()) {
            layout_Password_Confirm_Register.setError("Vui lòng không để trống tài khoản");
            checkEmpty = false;
        } else {
            checkEmpty = true;
        }

    }

    private void validateUserPass() {
        //Set điều kiện cho username giống bên Login_Layout
        edt_Username_Register.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String userInput = s.toString();
                Pattern pattern = Pattern.compile("^[a-zA-Z0-9._-]+@gmail\\.com$");
                Matcher matcher = pattern.matcher(userInput);
                boolean userMatch = matcher.find();
                if (userMatch) {
                    layout_Username_Register.setHelperText("");
                    layout_Username_Register.setError("");
                } else {
                    layout_Username_Register.setError("Tài khoản của bạn không đúng định dạng \"@gmail.com\" ");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        edt_Password_Register.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String passInput = s.toString();
                if (passInput.length() >= 8) {
                    // Giống bên Login_Layout
                    Pattern pattern = Pattern.compile("^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[^a-zA-Z0-9]).{8,}$");
                    Matcher matcher = pattern.matcher(passInput);
                    boolean passMatch = matcher.find();
                    if (passMatch) {
                        // Điều kiện khi đúng
                        layout_Password_Register.setHelperText("");
                        layout_Password_Register.setError("");
                    } else {
                        layout_Password_Register.setError("Mật khẩu của bạn phải 1 chữ Hoa, 1 chữ thường, 1 số và 1 ký tự đặc biệt");
                    }
                } else {
                    layout_Password_Register.setError("Mật khẩu của bạn phải lớn hơn hoặc bằng 8 ký tự");
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        edt_Password_Confirm_Register.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String passwordInput = Objects.requireNonNull(edt_Password_Register.getText()).toString().trim();
                String passwordConfirmInput = Objects.requireNonNull(edt_Password_Confirm_Register.getText()).toString().trim();
                if (!passwordInput.equals(passwordConfirmInput)) {
                    layout_Password_Confirm_Register.setError("Nhập lại mật khẩu không chính xác");
                } else {
                    layout_Password_Confirm_Register.setError("");
                    layout_Password_Confirm_Register.setHelperText("");
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    private void createDefaultSubcollections(FirebaseFirestore firestore, String uid) {
        // Lấy tất cả sản phẩm từ bảng "product"
        firestore.collection("product")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<DocumentSnapshot> productDocuments = task.getResult().getDocuments();

                        // Khởi tạo một batch để thực hiện việc thêm dữ liệu vào Firestore
                        WriteBatch batch = firestore.batch();

                        // Duyệt qua tất cả các sản phẩm và thêm vào subcollection "products" của user
                        for (DocumentSnapshot document : productDocuments) {
                            Map<String, Object> product = document.getData();
                            String productId = document.getId(); // Lấy ID sản phẩm từ bảng "product"

                            // Lấy reference đến document trong collection "users/{uid}/products"
                            DocumentReference userProductRef = firestore.collection("users")
                                    .document(uid)
                                    .collection("product")
                                    .document(productId);

                            // Thêm dữ liệu sản phẩm vào batch
                            batch.set(userProductRef, Objects.requireNonNull(product));
                        }

                        // Commit batch để thực hiện tất cả các thao tác
                        batch.commit().addOnCompleteListener(commitTask -> {
                            if (commitTask.isSuccessful()) {
//                                Toast.makeText(Register_Layout.this, "Di chuyển sản phẩm thành công!", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(RegisterLayout.this, "Di chuyển sản phẩm thất bại!", Toast.LENGTH_SHORT).show();
                            }
                        });

                    } else {
                        Toast.makeText(RegisterLayout.this, "Lấy sản phẩm thất bại!", Toast.LENGTH_SHORT).show();
                    }
                });
    }


//    private void createItemDefault(ArrayList<Map<String, Object>> productList) {
//        Map<String, Object> product1 = new HashMap<>();
//        product1.put("describe_product", "Áo đôi khủng long dành cho trẻ em là lựa chọn hoàn hảo để vừa thời trang vừa ý nghĩa trong việc gắn kết gia đình. Với họa tiết khủng long vui nhộn, màu sắc bắt mắt, những mẫu áo này phù hợp cho cả bé trai lẫn bé gái, tạo cảm giác thích thú và khơi gợi trí tưởng tượng về thế giới động vật thời tiền sử. Chất liệu 100% cotton mềm mại, thấm hút mồ hôi tốt, đảm bảo sự thoải mái và an toàn cho làn da nhạy cảm của trẻ. Cha mẹ và bé mặc áo đôi không chỉ tạo sự đồng điệu mà còn giúp gắn kết tình cảm gia đình, tạo nên những kỷ niệm đẹp. Đây cũng là món quà tuyệt vời cho các dịp đặc biệt như sinh nhật, lễ Tết hay chuyến dã ngoại gia đình.");
//        product1.put("image_product", "123");
//        product1.put("name_product", "Áo đôi khủng long");
//        product1.put("price_product", 8.99);
//        product1.put("rating_product", 4.5);
//        product1.put("status_product", false);
//        productList.add(product1);
//
//        // Thêm sản phẩm thứ hai
//        Map<String, Object> product2 = new HashMap<>();
//        product2.put("describe_product", "Áo trẻ em ngộ nghĩnh là lựa chọn tuyệt vời để mang đến cho bé yêu sự thoải mái, phong cách và niềm vui mỗi ngày. Với thiết kế sáng tạo, in hình các nhân vật hoạt hình, động vật đáng yêu hoặc các họa tiết vui nhộn, những chiếc áo này không chỉ thu hút ánh nhìn mà còn giúp khơi dậy trí tưởng tượng và sự sáng tạo của trẻ. Chất liệu cotton mềm mại, thoáng mát giúp bé vận động thoải mái suốt cả ngày, dù ở nhà, đi chơi hay tham gia các hoạt động ngoài trời.");
//        product2.put("image_product", "123");
//        product2.put("name_product", "Áo trẻ em ngộ nghĩnh");
//        product2.put("price_product", 12.99);
//        product2.put("rating_product", 4.9);
//        product2.put("status_product", false);
//        productList.add(product2);
//
//        Map<String, Object> product3 = new HashMap<>();
//        product3.put("describe_product", "Váy công chúa đáng yêu luôn là món đồ không thể thiếu trong tủ đồ của các bé gái yêu thích sự dịu dàng và lộng lẫy. Với thiết kế tinh tế, những chiếc váy này thường được may từ chất liệu mềm mại như voan, lụa hoặc ren, mang lại cảm giác thoải mái cho bé khi mặc. Điểm nhấn của váy là các chi tiết như nơ xinh, hoa văn lấp lánh hoặc tầng váy bồng bềnh, khiến bé trông như những nàng công chúa bước ra từ truyện cổ tích.");
//        product3.put("image_product", "123");
//        product3.put("name_product", "Váy Công Chúa Đáng Yêu");
//        product3.put("price_product", 18.99);
//        product3.put("rating_product", 4.9);
//        product3.put("status_product", false);
//        productList.add(product3);
//
//        Map<String, Object> product4 = new HashMap<>();
//        product4.put("describe_product", " Mũ xinh cho bé không chỉ là phụ kiện thời trang mà còn là vật dụng cần thiết để bảo vệ bé yêu trước nắng, gió và các yếu tố thời tiết khác. Với thiết kế đa dạng từ mũ rộng vành, mũ lưỡi trai đến mũ len đáng yêu, mỗi chiếc mũ đều mang đến phong cách riêng phù hợp với từng hoạt động như đi dạo, du lịch hay chơi ngoài trời. Màu sắc tươi sáng và họa tiết ngộ nghĩnh như hình thú, hoa lá hay nhân vật hoạt hình giúp tôn lên nét đáng yêu, hồn nhiên của các bé.");
//        product4.put("image_product", "123");
//        product4.put("name_product", "Mũ Xinh Cho Bé");
//        product4.put("price_product", 14.99);
//        product4.put("rating_product", 4.2);
//        product4.put("status_product", false);
//        productList.add(product4);
//
//        Map<String, Object> product5 = new HashMap<>();
//        product5.put("describe_product", "Áo trẻ em hiện đại không chỉ đơn thuần là trang phục mà còn là cách để các bé thể hiện cá tính và sự thoải mái trong mọi hoạt động hàng ngày. Với thiết kế tinh tế, những chiếc áo này kết hợp giữa phong cách thời trang hiện đại và sự tiện dụng, phù hợp cho cả học tập, vui chơi hay các dịp đặc biệt. Chất liệu cao cấp, an toàn, mềm mại và thoáng mát đảm bảo mang đến sự thoải mái tối đa, bảo vệ làn da nhạy cảm của trẻ. Các mẫu áo được thiết kế với màu sắc tươi sáng, họa tiết sinh động.");
//        product5.put("image_product", "123");
//        product5.put("name_product", "Áo trẻ em hiện đại");
//        product5.put("price_product", 9.99);
//        product5.put("rating_product", 4.7);
//        product5.put("status_product", false);
//        productList.add(product5);
//
//        Map<String, Object> product6 = new HashMap<>();
//        product6.put("describe_product", "Áo đôi khủng long dành cho trẻ em là lựa chọn hoàn hảo để vừa thời trang vừa ý nghĩa trong việc gắn kết gia đình. Với họa tiết khủng long vui nhộn, màu sắc bắt mắt, những mẫu áo này phù hợp cho cả bé trai lẫn bé gái, tạo cảm giác thích thú và khơi gợi trí tưởng tượng về thế giới động vật thời tiền sử. Chất liệu 100% cotton mềm mại, thấm hút mồ hôi tốt, đảm bảo sự thoải mái và an toàn cho làn da nhạy cảm của trẻ. Cha mẹ và bé mặc áo đôi không chỉ tạo sự đồng điệu mà còn giúp gắn kết tình cảm gia đình, tạo nên những kỷ niệm đẹp. Đây cũng là món quà tuyệt vời cho các dịp đặc biệt như sinh nhật, lễ Tết hay chuyến dã ngoại gia đình.");
//        product6.put("image_product", "123");
//        product6.put("name_product", "Áo đôi khủng long");
//        product6.put("price_product", 11.99);
//        product6.put("rating_product", 4.0);
//        product6.put("status_product", false);
//        productList.add(product6);
//
//        Map<String, Object> product7 = new HashMap<>();
//        product7.put("describe_product", "Áo trẻ em hiện đại không chỉ đơn thuần là trang phục mà còn là cách để các bé thể hiện cá tính và sự thoải mái trong mọi hoạt động hàng ngày. Với thiết kế tinh tế, những chiếc áo này kết hợp giữa phong cách thời trang hiện đại và sự tiện dụng, phù hợp cho cả học tập, vui chơi hay các dịp đặc biệt. Chất liệu cao cấp, an toàn, mềm mại và thoáng mát đảm bảo mang đến sự thoải mái tối đa, bảo vệ làn da nhạy cảm của trẻ. Các mẫu áo được thiết kế với màu sắc tươi sáng, họa tiết sinh động.");
//        product7.put("image_product", "123");
//        product7.put("name_product", "Áo trẻ em hiện đại");
//        product7.put("price_product", 15.99);
//        product7.put("rating_product", 4.7);
//        product7.put("status_product", false);
//        productList.add(product7);
//
//        Map<String, Object> product8 = new HashMap<>();
//        product8.put("describe_product", "Áo trẻ em hiện đại không chỉ đơn thuần là trang phục mà còn là cách để các bé thể hiện cá tính và sự thoải mái trong mọi hoạt động hàng ngày. Với thiết kế tinh tế, những chiếc áo này kết hợp giữa phong cách thời trang hiện đại và sự tiện dụng, phù hợp cho cả học tập, vui chơi hay các dịp đặc biệt. Chất liệu cao cấp, an toàn, mềm mại và thoáng mát đảm bảo mang đến sự thoải mái tối đa, bảo vệ làn da nhạy cảm của trẻ.");
//        product8.put("image_product", "123");
//        product8.put("name_product", "Áo thể thao");
//        product8.put("price_product", 10.99);
//        product8.put("rating_product", 4.5);
//        product8.put("status_product", false);
//        productList.add(product8);
//
//        Map<String, Object> product9 = new HashMap<>();
//        product9.put("describe_product", "Váy công chúa cho bé gái là trang phục mơ ước của mọi cô bé, mang lại cảm giác lộng lẫy như những nàng công chúa trong truyện cổ tích. Với thiết kế bồng bềnh, các lớp váy xếp tầng mềm mại, chất liệu như voan, ren, hoặc satin tạo nên sự duyên dáng và sang trọng. Những chi tiết như đính hạt cườm, nơ xinh xắn, hoặc họa tiết hoa lá tinh tế càng làm tôn lên vẻ đáng yêu của chiếc váy. Váy công chúa thường có màu sắc nhẹ nhàng như hồng pastel, trắng ngọc trai.");
//        product9.put("image_product", "123");
//        product9.put("name_product", "Váy công chúa");
//        product9.put("price_product", 21.99);
//        product9.put("rating_product", 4.5);
//        product9.put("status_product", false);
//        productList.add(product9);
//
//        Map<String, Object> product10 = new HashMap<>();
//        product10.put("describe_product", "Áo đôi khủng long dành cho trẻ em là lựa chọn hoàn hảo để vừa thời trang vừa ý nghĩa trong việc gắn kết gia đình. Với họa tiết khủng long vui nhộn, màu sắc bắt mắt, những mẫu áo này phù hợp cho cả bé trai lẫn bé gái, tạo cảm giác thích thú và khơi gợi trí tưởng tượng về thế giới động vật thời tiền sử. Chất liệu 100% cotton mềm mại, thấm hút mồ hôi tốt, đảm bảo sự thoải mái và an toàn cho làn da nhạy cảm của trẻ. Cha mẹ và bé mặc áo đôi không chỉ tạo sự đồng điệu mà còn giúp gắn kết tình cảm gia đình, tạo nên những kỷ niệm đẹp. Đây cũng là món quà tuyệt vời cho các dịp đặc biệt như sinh nhật, lễ Tết hay chuyến dã ngoại gia đình.");
//        product10.put("image_product", "123");
//        product10.put("name_product", "Áo đôi khủng long");
//        product10.put("price_product", 19.99);
//        product10.put("rating_product", 4.9);
//        product10.put("status_product", false);
//        productList.add(product10);
//    }
}