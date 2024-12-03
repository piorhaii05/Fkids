package com.example.shopping_online.layout;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.shopping_online.adapter.RecyclerVIew_Adapter;
import com.example.shopping_online.ClothesModel;
import com.example.shopping_online.R;

import java.io.IOException;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ASMGD1 extends AppCompatActivity {

    private RecyclerView recyclerView_clothes;
    private ArrayList<ClothesModel> list;
    private RecyclerVIew_Adapter adapter;
    private APIService apiService;
    private Retrofit retrofit;

    private static final int PICK_IMAGE_REQUEST = 1;
    private ImageView image_new;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_asm_gd1);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        recyclerView_clothes = findViewById(R.id.recyclerView_clothes);
        Button btn_add = findViewById(R.id.btn_Add);

        retrofit = new Retrofit.Builder()
                .baseUrl(APIService.DOMAIN)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        load_item_goto_Server();

        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                add_item_goto_Server();
            }
        });

    }

    private void load_item_goto_Server() {
        apiService = retrofit.create(APIService.class);
        Call<ArrayList<ClothesModel>> listCall = apiService.getClothes();

        listCall.enqueue(new Callback<ArrayList<ClothesModel>>() {
            @Override
            public void onResponse(@NonNull Call<ArrayList<ClothesModel>> call, @NonNull Response<ArrayList<ClothesModel>> response) {
                if(response.isSuccessful()){
                    list = response.body();

                    recyclerView_clothes.setLayoutManager(new LinearLayoutManager(ASMGD1.this));
                    adapter = new RecyclerVIew_Adapter(ASMGD1.this, list);
                    recyclerView_clothes.setAdapter(adapter);

                }
                else {
                    Toast.makeText(ASMGD1.this, "Load lỗi", Toast.LENGTH_SHORT).show();

                }
            }

            @Override
            public void onFailure(@NonNull Call<ArrayList<ClothesModel>> call, @NonNull Throwable t) {

            }
        });
    }

    private void add_item_goto_Server() {

        AlertDialog.Builder builder = new AlertDialog.Builder(ASMGD1.this);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_item_submit, null);
        builder.setView(view);
        Dialog dialog = builder.create();
        dialog.show();

        image_new = view.findViewById(R.id.iamge_new);
        EditText edt_name_new = view.findViewById(R.id.edt_name_new);
        EditText edt_date_new = view.findViewById(R.id.edt_date_new);
        EditText edt_brand_new = view.findViewById(R.id.edt_brand_new);
        EditText edt_price_new = view.findViewById(R.id.edt_price_new);
        Button btn_submit = view.findViewById(R.id.btn_submit);

        image_new.setOnClickListener(v -> {
            openImageChooser();
        });

        btn_submit.setOnClickListener(v -> {
            String name_new = edt_name_new.getText().toString().trim();
            String date_new = edt_date_new.getText().toString().trim();
            String brand_new = edt_brand_new.getText().toString().trim();
            String price_new = edt_price_new.getText().toString().trim();

            // Check if any field is empty
            if (name_new.isEmpty() || date_new.isEmpty() || brand_new.isEmpty() || price_new.isEmpty()) {
                Toast.makeText(ASMGD1.this, "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                return;
            }

            // Validate price (must be a number)
            try {
                Integer.parseInt(price_new);
            } catch (NumberFormatException e) {
                Toast.makeText(ASMGD1.this, "Giá không hợp lệ. Vui lòng nhập số.", Toast.LENGTH_SHORT).show();
                return;
            }

            // Validate date format (XX/XX/XX)
            if (!date_new.matches("\\d{2}/\\d{2}/\\d{4}")) {
                Toast.makeText(ASMGD1.this, "Ngày phải có định dạng XX/XX/XXXX", Toast.LENGTH_SHORT).show();
                return;
            }

            ClothesModel clothesModel_new = new ClothesModel();
            clothesModel_new.set_id(null);
            clothesModel_new.setName(name_new);
            clothesModel_new.setDate(date_new);
            clothesModel_new.setBrand(brand_new);
            clothesModel_new.setPrice(Integer.parseInt(price_new));

            Call<ArrayList<ClothesModel>> call = apiService.addClothes(clothesModel_new);
            call.enqueue(new Callback<ArrayList<ClothesModel>>() {
                @Override
                public void onResponse(@NonNull Call<ArrayList<ClothesModel>> call, @NonNull Response<ArrayList<ClothesModel>> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        list.clear();
                        list.addAll(response.body());
                        load_item_goto_Server();
                        dialog.dismiss();
                        Toast.makeText(ASMGD1.this, "Thêm thành công!", Toast.LENGTH_LONG).show();
                    } else {
                        int statusCode = response.code(); // Mã trạng thái HTTP
                        Toast.makeText(ASMGD1.this, "Lỗi: " + statusCode + " - " + response.message(), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<ArrayList<ClothesModel>> call, @NonNull Throwable t) {
                    Toast.makeText(ASMGD1.this, "Add thất bại: " + t.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        });

    }

    private void openImageChooser() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            // Lấy URI của ảnh đã chọn
            Uri imageUri = data.getData();
            try {
                // Hiển thị ảnh vào ImageView
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                image_new.setImageBitmap(bitmap);

                // Tại đây, bạn có thể upload ảnh lên server (chưa làm)
                // uploadImageToServer(imageUri); // Ví dụ

            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(ASMGD1.this, "Lỗi khi lấy ảnh", Toast.LENGTH_SHORT).show();
            }
        }
    }
}