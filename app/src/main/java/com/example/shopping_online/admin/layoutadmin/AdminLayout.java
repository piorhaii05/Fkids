package com.example.shopping_online.admin.layoutadmin;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.shopping_online.R;
import com.example.shopping_online.admin.fragmentadmin.ContactFragment;
import com.example.shopping_online.admin.fragmentadmin.ProductAdminFragment;
import com.example.shopping_online.admin.fragmentadmin.RevenueFragment;
import com.example.shopping_online.admin.fragmentadmin.VoucherFragment;
import com.example.shopping_online.admin.fragmentadmin.WarehouseFragment;
import com.example.shopping_online.layout.ChangePassLayout;
import com.example.shopping_online.layout.LoginLayout;
import com.google.android.material.navigation.NavigationView;

import java.util.Objects;

public class AdminLayout extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    public static final int FRAGMENT_HOME = 0;
    public static final int FRAGMENT_CHANGEPASS = 1;
    public static final int FRAGMENT_VOUCHER = 2;
    public static final int FRAGMENT_WAREHOUSE = 3;
    public static final int FRAGMENT_REVENUE = 4;
    public static final int FRAGMENT_CONTACT = 5;

    private DrawerLayout drawer_layout_admin;
    private Toolbar toolbarAdminLayout;
    private TextView txtNameAdmin, txtEmailAdmin;

    private int currentfragmet = FRAGMENT_HOME;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin_layout);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.drawer_layout_admin), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        toolbarAdminLayout = findViewById(R.id.toolbarAdminLayout);
        drawer_layout_admin = findViewById(R.id.drawer_layout_admin);
        NavigationView navigationViewAdminLayout = findViewById(R.id.navigationViewAdminLayout);

        View header = navigationViewAdminLayout.getHeaderView(0);
        txtNameAdmin = header.findViewById(R.id.txtNameUserAdmin);
        txtEmailAdmin = header.findViewById(R.id.txtEmailAdmin);

        setEmailForAcount();

        setSupportActionBar(toolbarAdminLayout);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Product");

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer_layout_admin, toolbarAdminLayout, R.string.nav_drawer_open, R.string.nav_drawer_close);
        drawer_layout_admin.addDrawerListener(toggle);
        toggle.syncState();

        navigationViewAdminLayout.setNavigationItemSelectedListener(this);
        replaceFragment(new ProductAdminFragment());
        navigationViewAdminLayout.getMenu().findItem(R.id.nav_home).setChecked(true);
    }

    private void setEmailForAcount() {
        SharedPreferences sharedPreferencesName = getSharedPreferences("AcountChange", MODE_PRIVATE);
        String name = sharedPreferencesName.getString("nameacount", "");
        String id = sharedPreferencesName.getString("userIdAcount", "");
        txtNameAdmin.setText(name);

        SharedPreferences sharedPreferences = getSharedPreferences("User", MODE_PRIVATE);
        String username = sharedPreferences.getString("username", "");

        txtEmailAdmin.setText(username);
    }

    private void replaceFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.FrameAdminLayout, fragment);
        transaction.commit();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.nav_home) {
            if (currentfragmet != FRAGMENT_HOME) {
                replaceFragment(new ProductAdminFragment());
                toolbarAdminLayout.setTitle("Product");
                currentfragmet = FRAGMENT_HOME;
                drawer_layout_admin.closeDrawer(GravityCompat.START);
            }
        } else if (id == R.id.nav_change) {
            startActivity(new Intent(AdminLayout.this, ChangePassLayout.class));
        } else if (id == R.id.nav_voucher) {
            if (currentfragmet != FRAGMENT_VOUCHER) {
                replaceFragment(new VoucherFragment());
                toolbarAdminLayout.setTitle("Voucher");
                currentfragmet = FRAGMENT_VOUCHER;
                drawer_layout_admin.closeDrawer(GravityCompat.START);
            }
        } else if (id == R.id.nav_warehouse) {
            if (currentfragmet != FRAGMENT_WAREHOUSE) {
                replaceFragment(new WarehouseFragment());
                toolbarAdminLayout.setTitle("Warehouse");
                currentfragmet = FRAGMENT_WAREHOUSE;
                drawer_layout_admin.closeDrawer(GravityCompat.START);
            }
        } else if (id == R.id.nav_revenue) {
            if (currentfragmet != FRAGMENT_REVENUE) {
                replaceFragment(new RevenueFragment());
                toolbarAdminLayout.setTitle("Revenue");
                currentfragmet = FRAGMENT_REVENUE;
                drawer_layout_admin.closeDrawer(GravityCompat.START);
            }
        } else if (id == R.id.nav_contact) {
            if (currentfragmet != FRAGMENT_CONTACT) {
                replaceFragment(new ContactFragment());
                toolbarAdminLayout.setTitle("Contact");
                currentfragmet = FRAGMENT_CONTACT;
                drawer_layout_admin.closeDrawer(GravityCompat.START);
            }
        } else if (id == R.id.nav_logout) {
            startActivity(new Intent(AdminLayout.this, LoginLayout.class));
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        if (drawer_layout_admin.isDrawerOpen(GravityCompat.START)) {
            drawer_layout_admin.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}