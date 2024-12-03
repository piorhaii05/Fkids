package com.example.shopping_online.layout;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.shopping_online.model.CartModel;
import com.example.shopping_online.R;
import com.google.common.reflect.TypeToken;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;

import java.lang.reflect.Type;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class PaymentLayout extends AppCompatActivity {

    private SharedPreferences sharedPreferences;
    private TextView txtTotalPrice;
    private TextView txtTaxPrice;
    private TextView txtToTalResultPrice;
    private TextView txtValueVoucher;
    private TextView txtTotalPriceAll;
    private TextView txtFourNumberCard;
    private CheckBox CheckboxTakeProductAtHome;
    private LinearLayout txtUseVoucher, txtUseVoucherFuture, txtPaymentOnline, txtUseCardFuture;
    private String fournumber;
    private FirebaseFirestore firestore;
    private ArrayList<CartModel> selectedItems;
    private boolean checkStatus = false;
    final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    final String userId = Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_payment_layout);

        // Adjust for edge-to-edge UI
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        firestore = FirebaseFirestore.getInstance();

        // Initialize views
        Toolbar toolbarPaymentLayout = findViewById(R.id.toolbarPaymentLayout);
        txtTotalPrice = findViewById(R.id.txtTotalPrice);
        txtTaxPrice = findViewById(R.id.txtTaxPrice);
        txtToTalResultPrice = findViewById(R.id.txtToTalResultPrice);
        CheckboxTakeProductAtHome = findViewById(R.id.CheckboxTakeProductAtHome);
        txtUseVoucher = findViewById(R.id.txtUseVoucher);
        txtValueVoucher = findViewById(R.id.txtValueVoucher);
        txtUseVoucherFuture = findViewById(R.id.txtUseVoucherFuture);
        txtTotalPriceAll = findViewById(R.id.txtTotalPriceAll);
        txtPaymentOnline = findViewById(R.id.txtPaymentOnline);
        txtUseCardFuture = findViewById(R.id.txtUseCardFuture);
        txtFourNumberCard = findViewById(R.id.txtFourNumberCard);
        TextView txtPayProductOnCart = findViewById(R.id.txtPayProductOnCart);

        CheckboxTakeProductAtHome.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                // Khi checkbox được chọn, hiển thị phương thức thanh toán online và ẩn phương thức thẻ
                checkStatus = true;
                txtPaymentOnline.setVisibility(View.VISIBLE); // Hiển thị thanh toán online
                txtUseCardFuture.setVisibility(View.GONE); // Ẩn thanh toán thẻ
            } else {
                // Khi checkbox không được chọn, ẩn phương thức thanh toán online và hiển thị phương thức thẻ
                checkStatus = false;
                txtPaymentOnline.setVisibility(View.GONE); // Ẩn thanh toán online
                txtUseCardFuture.setVisibility(View.VISIBLE); // Hiển thị thanh toán thẻ
            }
        });

        txtPayProductOnCart.setOnClickListener(v -> {
            // Check if a bank card is selected (fournumber is not null, not empty, or "0")
            boolean isBankCardSelected = fournumber != null && !fournumber.isEmpty() && !fournumber.equals("0");

            // Check if "Take Product At Home" checkbox is checked (indicating payment on delivery)
            boolean isPaymentOnDeliverySelected = CheckboxTakeProductAtHome.isChecked();

            // If neither payment method is selected, show a Toast
            if (!isBankCardSelected && !isPaymentOnDeliverySelected) {
                Toast.makeText(PaymentLayout.this, "Vui lòng chọn phương thức thanh toán", Toast.LENGTH_LONG).show();
            } else {
                // Show confirmation dialog if a payment method is selected
                showDialogConfirm();
            }
        });


        // Navigation to other layouts
        txtPaymentOnline.setOnClickListener(v -> startActivity(new Intent(PaymentLayout.this, UseCardLayout.class)));
        txtUseCardFuture.setOnClickListener(v -> startActivity(new Intent(PaymentLayout.this, UseCardLayout.class)));
        txtUseVoucher.setOnClickListener(v -> startActivity(new Intent(PaymentLayout.this, VoucherLayout.class)));
        txtUseVoucherFuture.setOnClickListener(v -> startActivity(new Intent(PaymentLayout.this, VoucherLayout.class)));

        // Load data on layout
        loadDataOnLayout();
        loadDataUseCardOnLayout();

        selectedItems = getSelectedItemsFromPreferences(); // Lấy các sản phẩm đã chọn
        displaySelectedItems();

        // Set toolbar configuration
        setSupportActionBar(toolbarPaymentLayout);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Thanh toán");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private ArrayList<CartModel> getSelectedItemsFromPreferences() {
        SharedPreferences sharedPreferences = getSharedPreferences("SelectedItems", MODE_PRIVATE);
        String json = sharedPreferences.getString("selectedItems", null);
        if (json != null) {
            Gson gson = new Gson();
            Type type = new TypeToken<ArrayList<CartModel>>() {
            }.getType();
            return gson.fromJson(json, type);
        }
        return new ArrayList<>();
    }

    // Phương thức hiển thị các sản phẩm đã chọn
    private void displaySelectedItems() {
        StringBuilder itemDetails = new StringBuilder();

        for (CartModel cart : selectedItems) {
            itemDetails.append(cart.getName_product_cart())
                    .append(" - ")
                    .append(cart.getPrice_product_cart())
                    .append("\n");
        }

        txtTotalPrice.setText(itemDetails.toString());
    }

    private void showDialogConfirm() {
        new AlertDialog.Builder(this)
                .setTitle("Thanh toán")
                .setMessage("Bạn có chắc chắn thanh toán không?")
                .setPositiveButton("OK", (dialog, which) -> showDialogSucess())
                .setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss())
                .create()
                .show();
    }

    private void showDialogSucess() {
        addItemToFireBase();
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_success_payment, null);
        new AlertDialog.Builder(this).setView(view).create().show();

        TextView txtGoBack = view.findViewById(R.id.txtGoBack);
        TextView txtViewDetailProduct = view.findViewById(R.id.txtViewDetailProduct);

        txtGoBack.setOnClickListener(v -> startActivity(new Intent(PaymentLayout.this, MenuLayout.class)));
        txtViewDetailProduct.setOnClickListener(v -> {
            startActivity(new Intent(PaymentLayout.this, HistoryPaymentLayout.class));
        });
    }


    private void addItemToFireBase() {
        ArrayList<CartModel> selectedItems = getSelectedItemsFromPreferences();

        if (selectedItems.isEmpty()) {
            Toast.makeText(PaymentLayout.this, "Không có sản phẩm nào được chọn", Toast.LENGTH_SHORT).show();
            return;
        }
        double totalPrice = Double.parseDouble(txtTotalPriceAll.getText().toString());

        Log.d("SelectedItems", "Số lượng sản phẩm đã chọn: " + selectedItems.size());
        for (CartModel item : selectedItems) {
            Log.d("SelectedItems", "Sản phẩm: " + item.getName_product_cart());
        }

        LocalDate currentDate = LocalDate.now();
        String datecurrent = String.valueOf(currentDate);

        Map<String, Object> paymentData = new HashMap<>();
        paymentData.put("price_history", totalPrice);
        paymentData.put("date_history", datecurrent);
        if (checkStatus) {
            paymentData.put("infor_payment_history", "Thanh toán khi nhận hàng");
        } else {
            paymentData.put("infor_payment_history", "Đã thanh toán");
        }

        firestore.collection("users").document(userId).collection("historypayment").add(paymentData)
                .addOnSuccessListener(documentReference -> {
                    String purchaseId = documentReference.getId();

                    for (CartModel item : selectedItems) {
                        Map<String, Object> itemData = new HashMap<>();
                        itemData.put("name_product_items_history", item.getName_product_cart());
                        itemData.put("describe_product_items_history", item.getDescribe_product_cart());
                        itemData.put("quantity_product_items_history", item.getQuantity_product_cart());
                        itemData.put("price_product_items_history", item.getPrice_product_cart());
                        itemData.put("size_product_items_history", item.getSize_product_cart());
                        itemData.put("rating_product_items_history", item.getRating_product_cart());
                        itemData.put("color_product_items_history", item.getColor_product_cart());

                        firestore.collection("users").document(userId).collection("historypayment")
                                .document(purchaseId)
                                .collection("itemspayment")
                                .add(itemData)
                                .addOnSuccessListener(documentReference1 -> {
                                    Log.d("Firestore", "Item added to historypayment successfully");
                                })
                                .addOnFailureListener(e -> {
                                    Log.e("Firestore Error", "Error adding item", e);
                                });
                    }

                    selectedItems.clear();

                    SharedPreferences sharedPreferences = getSharedPreferences("SelectedItems", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("selectedItems", null);
                    editor.apply();

                    Toast.makeText(PaymentLayout.this, "Thanh toán thành công!", Toast.LENGTH_SHORT).show();

                })
                .addOnFailureListener(e -> {
                    Log.e("Firestore Error", "Error adding payment", e);
                    Toast.makeText(PaymentLayout.this, "Đã xảy ra lỗi, vui lòng thử lại!", Toast.LENGTH_SHORT).show();
                });
    }


    private void loadDataUseCardOnLayout() {
        sharedPreferences = getSharedPreferences("UseCard", MODE_PRIVATE);
        fournumber = sharedPreferences.getString("numbercard", "0");

        if (fournumber.isEmpty() || fournumber.equals("0")) {
            txtUseCardFuture.setVisibility(View.GONE);
            txtPaymentOnline.setVisibility(View.VISIBLE);
        } else {
            txtUseCardFuture.setVisibility(View.VISIBLE);
            txtPaymentOnline.setVisibility(View.GONE);
            txtFourNumberCard.setText(fournumber);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadDataOnLayout(); // Refresh data when returning to Payment_Layout
        loadDataUseCardOnLayout();
    }

    private void loadDataOnLayout() {
        sharedPreferences = getSharedPreferences("TotalPrice", MODE_PRIVATE);
        String totalPrice = sharedPreferences.getString("price", "0");

        DecimalFormat decimalFormat = new DecimalFormat("#.##");

        txtTotalPrice.setText(totalPrice);

        // Calculate tax
        double taxresult = 0.005 * Double.parseDouble(totalPrice);
        txtTaxPrice.setText(decimalFormat.format(taxresult));

        // Calculate total result
        double totalrult = 10 + Double.parseDouble(totalPrice) + taxresult;
        txtToTalResultPrice.setText(decimalFormat.format(totalrult));

        // Handle voucher
        SharedPreferences sharedPreferencesValue = getSharedPreferences("ValueVoucher", MODE_PRIVATE);
        String valueVoucher = sharedPreferencesValue.getString("value", "0");

        if (valueVoucher.isEmpty() || valueVoucher.equals("0")) {
            txtUseVoucherFuture.setVisibility(View.GONE);
            txtUseVoucher.setVisibility(View.VISIBLE);
            txtValueVoucher.setText("");
            txtTotalPriceAll.setText(decimalFormat.format(totalrult));
        } else {
            txtUseVoucherFuture.setVisibility(View.VISIBLE);
            txtUseVoucher.setVisibility(View.GONE);
            txtValueVoucher.setText("-" + valueVoucher);

            // Update total price after applying voucher
            double voucherValue = Double.parseDouble(valueVoucher);
            double updatedTotal = totalrult - voucherValue;
            txtTotalPriceAll.setText(decimalFormat.format(Math.max(updatedTotal, 0)));
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (isFinishing()) {
            // Reset voucher and card data when leaving screen
            SharedPreferences sharedPreferencesValue = getSharedPreferences("ValueVoucher", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferencesValue.edit();
            editor.putString("value", "0"); // Reset voucher value
            editor.apply();

            SharedPreferences sharedPreferencesValueCard = getSharedPreferences("UseCard", MODE_PRIVATE);
            SharedPreferences.Editor editorCard = sharedPreferencesValueCard.edit();
            editorCard.putString("numbercard", "0"); // Reset card value
            editorCard.apply();
        }
    }

}
