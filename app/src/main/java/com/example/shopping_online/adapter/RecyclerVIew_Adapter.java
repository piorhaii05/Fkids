package com.example.shopping_online.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.shopping_online.ClothesModel;
import com.example.shopping_online.layout.APIService;
import com.example.shopping_online.R;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RecyclerVIew_Adapter extends RecyclerView.Adapter<RecyclerVIew_Adapter.ViewHolderClothes> {

    private final Context context;
    private final ArrayList<ClothesModel> list;
    private final APIService apiService;

    public RecyclerVIew_Adapter(Context context, ArrayList<ClothesModel> list) {
        this.context = context;
        this.list = list;

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(APIService.DOMAIN)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        apiService = retrofit.create(APIService.class);
    }

    @NonNull
    @Override
    public ViewHolderClothes onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = ((Activity) context).getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_item_clothes, parent, false);
        return new ViewHolderClothes(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderClothes holder, int position) {

        ClothesModel clothesModel = list.get(position);

        holder.txt_name.setText(clothesModel.getName());
        holder.txt_date.setText(clothesModel.getDate());
        holder.txt_brand.setText(clothesModel.getBrand());
        holder.txt_price.setText(String.valueOf(clothesModel.getPrice()));

        Glide.with(context)
                .load(clothesModel.getImage()) // Thay bằng link ảnh
                .placeholder(R.drawable.product1) // Ảnh chờ nếu link chưa tải xong
                .error(R.drawable.product1) // Ảnh lỗi nếu không tải được
                .into(holder.iamge);

        holder.btn_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                update_item_goto_Server(position);
            }
        });

        holder.btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                delete_item_goto_Server(position);
            }
        });
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showProductDetail(clothesModel);
            }
        });
    }

    private void showProductDetail(ClothesModel clothesModel) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = ((Activity) context).getLayoutInflater();
        View view = inflater.inflate(R.layout.detail, null);
        builder.setView(view);
        Dialog dialog = builder.create();
        dialog.show();

        // Ánh xạ các view trong dialog
        ImageView imageDetail = view.findViewById(R.id.image_detail);
        TextView textNameDetail = view.findViewById(R.id.text_name_detail);
        TextView textDateDetail = view.findViewById(R.id.text_date_detail);
        TextView textBrandDetail = view.findViewById(R.id.text_brand_detail);
        TextView textPriceDetail = view.findViewById(R.id.text_price_detail);

        // Hiển thị dữ liệu
        Glide.with(context)
                .load(clothesModel.getImage())
                .placeholder(R.drawable.product1)
                .error(R.drawable.product1)
                .into(imageDetail);

        textNameDetail.setText(clothesModel.getName());
        textDateDetail.setText(clothesModel.getDate());
        textBrandDetail.setText(clothesModel.getBrand());
        textPriceDetail.setText(String.valueOf(clothesModel.getPrice()));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolderClothes extends RecyclerView.ViewHolder {

        final TextView txt_name;
        final TextView txt_date;
        final TextView txt_brand;
        final TextView txt_price;
        final Button btn_update;
        final Button btn_delete;
        final ImageView iamge;

        public ViewHolderClothes(@NonNull View itemView) {
            super(itemView);

            iamge = itemView.findViewById(R.id.iamge);
            txt_name = itemView.findViewById(R.id.txt_name);
            txt_date = itemView.findViewById(R.id.txt_date);
            txt_brand = itemView.findViewById(R.id.txt_brand);
            txt_price = itemView.findViewById(R.id.txt_price);
            btn_update = itemView.findViewById(R.id.btn_update);
            btn_delete = itemView.findViewById(R.id.btn_delete);

        }
    }

    private void update_item_goto_Server(int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = ((Activity) context).getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_item_submit, null);
        builder.setView(view);
        Dialog dialog = builder.create();
        dialog.show();

        ImageView image_update = view.findViewById(R.id.iamge_new);
        EditText edt_name_update = view.findViewById(R.id.edt_name_new);
        EditText edt_date_update = view.findViewById(R.id.edt_date_new);
        EditText edt_brand_update = view.findViewById(R.id.edt_brand_new);
        EditText edt_price_update = view.findViewById(R.id.edt_price_new);
        Button btn_submit_update = view.findViewById(R.id.btn_submit);

        ClothesModel clothesModel = list.get(position);
        edt_name_update.setText(clothesModel.getName());
        edt_date_update.setText(clothesModel.getDate());
        edt_brand_update.setText(clothesModel.getBrand());
        edt_price_update.setText(String.valueOf(clothesModel.getPrice()));

        Glide.with(context)
                .load(clothesModel.getImage()) // Đảm bảo đây là URL ảnh hợp lệ
                .placeholder(R.drawable.product1) // Ảnh thay thế nếu chưa tải được
                .error(R.drawable.product1) // Ảnh lỗi nếu không tải được
                .into(image_update);

        btn_submit_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = edt_name_update.getText().toString().trim();
                String date = edt_date_update.getText().toString().trim();
                String brand = edt_brand_update.getText().toString().trim();
                String price = edt_price_update.getText().toString().trim();

                // Validate empty fields
                if (name.isEmpty() || date.isEmpty() || brand.isEmpty() || price.isEmpty()) {
                    Toast.makeText(context, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Validate price (must be a number)
                try {
                    Integer.parseInt(price);
                } catch (NumberFormatException e) {
                    Toast.makeText(context, "Giá không hợp lệ. Vui lòng nhập số.", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Validate date format (XX/XX/XX)
                if (!date.matches("\\d{2}/\\d{2}/\\d{4}")) {
                    Toast.makeText(context, "Ngày phải có định dạng XX/XX/XXXX", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Proceed with update
                ClothesModel clothesModel_update = new ClothesModel();
                clothesModel_update.set_id(clothesModel.get_id());
                clothesModel_update.setName(name);
                clothesModel_update.setDate(date);
                clothesModel_update.setBrand(brand);
                clothesModel_update.setPrice(Integer.parseInt(price));

                Call<ArrayList<ClothesModel>> call_update = apiService.updateClothes(clothesModel_update);
                call_update.enqueue(new Callback<ArrayList<ClothesModel>>() {
                    @Override
                    public void onResponse(Call<ArrayList<ClothesModel>> call, Response<ArrayList<ClothesModel>> response) {
                        if (response.isSuccessful()) {
                            list.clear();
                            list.addAll(response.body());
                            notifyDataSetChanged();
                            Toast.makeText(context, "Cập nhật thành công", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(context, "Cập nhật thất bại", Toast.LENGTH_SHORT).show();
                        }
                        dialog.dismiss();
                    }

                    @Override
                    public void onFailure(Call<ArrayList<ClothesModel>> call, Throwable t) {
                        Toast.makeText(context, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }



    private void delete_item_goto_Server(int position) {
        ClothesModel clothesModel = list.get(position);

        // Xác nhận xóa
        new AlertDialog.Builder(context)
                .setTitle("Xác nhận!")
                .setMessage("Bạn có chắc chắn muốn xóa item này không?")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Xóa item khỏi danh sách trước khi gọi API
                        list.remove(position);
                        notifyDataSetChanged();

                        // Gọi API để xóa item trên server
                        Call<Void> call_delete = apiService.deleteClothes(clothesModel.get_id());
                        call_delete.enqueue(new Callback<Void>() {
                            @Override
                            public void onResponse(Call<Void> call, Response<Void> response) {
                                if (response.isSuccessful()) {
                                    Toast.makeText(context, "Xóa thành công", Toast.LENGTH_SHORT).show();
                                } else {
                                    // Trả lại item vào danh sách nếu xóa thất bại
                                    list.add(position, clothesModel);
                                    notifyDataSetChanged();
                                    Toast.makeText(context, "Xóa thất bại", Toast.LENGTH_SHORT).show();
                                }
                                dialog.dismiss();
                            }

                            @Override
                            public void onFailure(Call<Void> call, Throwable t) {
                                // Trả lại item vào danh sách nếu có lỗi kết nối
                                list.add(position, clothesModel);
                                notifyDataSetChanged();
                                Toast.makeText(context, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                            }
                        });
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .show();
    }


}


