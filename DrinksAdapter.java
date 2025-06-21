package com.myapp.pizzahut_admin;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.List;

public class DrinksAdapter extends RecyclerView.Adapter<DrinksAdapter.DrinksViewHolder> {
    private Context context;
    private List<Product> productList;
    private FirebaseFirestore db;

    public DrinksAdapter(Context context, List<Product> productList) {
        this.context = context;
        this.productList = productList;
        this.db = FirebaseFirestore.getInstance();
    }

    @Override
    public DrinksViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.pizza_item, parent, false);
        return new DrinksViewHolder(view);
    }

    @Override
    public void onBindViewHolder(DrinksViewHolder holder, int position) {
        Product product = productList.get(position);
        holder.productName.setText(product.getName());
        holder.productDescription.setText(product.getDescription());
        holder.productPrice.setText("Rs. " + product.getPrice());


        String imagePath = product.getImagePath();
        if (imagePath != null && !imagePath.isEmpty()) {
            File imgFile = new File(imagePath);
            if (imgFile.exists()) {
                Picasso.get()
                        .load(imgFile)
                        .placeholder(R.drawable.baseline_add_photo_alternate_24)
                        .error(R.drawable.baseline_add_photo_alternate_24)
                        .into(holder.productImage);
            } else {
                holder.productImage.setImageResource(R.drawable.baseline_add_photo_alternate_24);
            }
        } else {
            holder.productImage.setImageResource(R.drawable.baseline_add_photo_alternate_24);
        }

    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public void updateList(List<Product> newList) {
        this.productList = newList;
        notifyDataSetChanged();
    }

    public static class DrinksViewHolder extends RecyclerView.ViewHolder {
        TextView productName, productDescription, productPrice;
        ImageView productImage;

        public DrinksViewHolder(View itemView) {
            super(itemView);
            productName = itemView.findViewById(R.id.product_name);
            productDescription = itemView.findViewById(R.id.product_description);
            productPrice = itemView.findViewById(R.id.product_price);
            productImage = itemView.findViewById(R.id.product_image);
        }
    }

}