package com.example.shopping_online.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.shopping_online.model.SettingModel;
import com.example.shopping_online.layout.ChangeNameAcountLayout;
import com.example.shopping_online.layout.ChangePassLayout;
import com.example.shopping_online.layout.ChangeSDTAcountLayout;
import com.example.shopping_online.R;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SettingFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SettingFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private TextView txtEmailSetting, txtPhoneNumberUser, txtNameUser, txtGenderUser;

    private final FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private SettingModel settingModel;

    private String email;

    public SettingFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Cart_Fragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SettingFragment newInstance(String param1, String param2) {
        SettingFragment fragment = new SettingFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            // TODO: Rename and change types of parameters
            String mParam1 = getArguments().getString(ARG_PARAM1);
            String mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_setting_, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        LinearLayout txtNameSetting = view.findViewById(R.id.txtNameSetting);
        txtEmailSetting = view.findViewById(R.id.txtEmailSetting);
        LinearLayout txtGenderSetting = view.findViewById(R.id.txtGenderSetting);
        LinearLayout txtPhoneNumberSetting = view.findViewById(R.id.txtPhoneNumberSetting);
        LinearLayout txtChangePassWordSetting = view.findViewById(R.id.txtChangePassWordSetting);
        txtNameUser = view.findViewById(R.id.txtNameUser);
        txtPhoneNumberUser = view.findViewById(R.id.txtPhoneNumberUser);
        txtGenderUser = view.findViewById(R.id.txtGenderUser);

        ArrayList<SettingModel> list = new ArrayList<>();

        loadDataFormFireBase();
        setEmailForAcount();

        txtChangePassWordSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), ChangePassLayout.class));
            }
        });
        txtNameSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), ChangeNameAcountLayout.class));
            }
        });
        txtPhoneNumberSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), ChangeSDTAcountLayout.class));
            }
        });
        txtGenderSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showGenderSelected();
            }
        });

    }

    private void showGenderSelected() {
        String[] genderOptions = {"Nam", "Nữ"};
        boolean isCurrentGenderFemale = settingModel.isGender();
        int checkedItem = isCurrentGenderFemale ? 1 : 0;

        new androidx.appcompat.app.AlertDialog.Builder(getContext())
                .setTitle("Chọn giới tính")
                .setSingleChoiceItems(genderOptions, checkedItem, (dialog, which) -> {
                    boolean isFemale = (which == 1);
                    settingModel.setGender(isFemale);
                    txtGenderUser.setText(isFemale ? "Nữ" : "Nam");
                })
                .setPositiveButton("Lưu", (dialog, which) -> {
                    saveGenderToDatabase(settingModel.isGender());
                    dialog.dismiss();
                })
                .setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss())
                .show();
    }

    private void saveGenderToDatabase(boolean gender) {
        if (settingModel.getId() == null || settingModel.getId().isEmpty()) {
            Toast.makeText(getContext(), "Không thể cập nhật giới tính", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> updates = new HashMap<>();
        updates.put("gender", gender);

        firestore.collection("users")
                .document(settingModel.getId())
                .update(updates)
                .addOnSuccessListener(unused -> {
                    Toast.makeText(getContext(), "Cập nhật giới tính thành công", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Lỗi khi cập nhật giới tính", Toast.LENGTH_SHORT).show();
                });
    }

    private void loadDataFormFireBase() {
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("UserCheck", Context.MODE_PRIVATE);
        String email = sharedPreferences.getString("email", "");

        if (email.isEmpty()) {
            Toast.makeText(getContext(), "Không tìm thấy email người dùng", Toast.LENGTH_LONG).show();
            return;
        }

        firestore.collection("users")
                .whereEqualTo("username", email) // "username" là tên trường chứa email trong Firestore
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        for (QueryDocumentSnapshot queryDocumentSnapshot : queryDocumentSnapshots) {
                            settingModel = queryDocumentSnapshot.toObject(SettingModel.class);
                            settingModel.setId(queryDocumentSnapshot.getId());
                        }
                        updateUI();
                        saveDataAcount(settingModel.getId());
                    } else {
                        Toast.makeText(getContext(), "Không tìm thấy thông tin người dùng", Toast.LENGTH_LONG).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Lỗi khi tải dữ liệu", Toast.LENGTH_LONG).show();
                });
    }

    private void saveDataAcount(String id) {
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("AcountChange", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("userIdAcount", id);
        editor.putString("nameacount", txtNameUser.getText().toString().trim());
        editor.putString("phonenumberacount", txtPhoneNumberUser.getText().toString().trim());
        editor.apply();
    }

    private void updateUI() {
        txtNameUser.setText(settingModel.getNameuser());
        txtPhoneNumberUser.setText(settingModel.getPhonenumber());
        txtEmailSetting.setText(settingModel.getUsername());

        txtGenderUser.setText(settingModel.isGender() ? "Nữ" : "Nam");
    }

    private void setEmailForAcount() {
        getContext();
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("User", Context.MODE_PRIVATE);
        String username = sharedPreferences.getString("username", "");
//        txtEmailSetting.setText(username);
    }
}