package com.example.shopping_online.layout;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.example.shopping_online.adapter.ViewPagerMyReivewAdapter;
import com.example.shopping_online.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

public class MyReviewLayout extends AppCompatActivity {

    private ViewPager2 viewpagerMyReview;
    private FirebaseFirestore firestore;
    final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    final String userId = Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_my_review_layout);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Khởi tạo toolbar
        Toolbar toolbarMyReviewLayout = findViewById(R.id.toolbarMyReviewLayout);
        setSupportActionBar(toolbarMyReviewLayout);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Đánh giá của tôi");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Khởi tạo ViewPager
        viewpagerMyReview = findViewById(R.id.viewpagerMyReview);

        // Tải dữ liệu từ Firebase
        firestore = FirebaseFirestore.getInstance();
        loadDataFromFirebase();
    }

    private void loadDataFromFirebase() {
        firestore.collection("users").document(userId).collection("myreview").get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                boolean hasItems = !task.getResult().isEmpty();
                setupViewPager(hasItems);
            } else {
                setupViewPager(false); // Lỗi hoặc không có dữ liệu
            }
        });
    }

    private void setupViewPager(boolean hasItems) {
        ViewPagerMyReivewAdapter adapter = new ViewPagerMyReivewAdapter(getSupportFragmentManager(), getLifecycle(), hasItems);
        viewpagerMyReview.setAdapter(adapter);
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
