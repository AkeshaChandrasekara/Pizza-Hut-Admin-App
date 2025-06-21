package com.myapp.pizzahut_admin;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.io.Serializable;
import java.util.List;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {
    private Context context;
    private List<Order> orderList;

    public OrderAdapter(Context context, List<Order> orderList) {
        this.context = context;
        this.orderList = orderList;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_order, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Order order = orderList.get(position);

        holder.textEmail.setText(order.getEmail());
        holder.textAddress.setText(order.getAddress());
        holder.textTotal.setText("Total: Rs" + order.getTotal());
        holder.textFinalAmount.setText("Final Amount: Rs" + order.getFinalAmount());
        holder.textStatus.setText("Status: " + order.getStatus());

        if (order.getItems() != null && !order.getItems().isEmpty()) {
            CartItem firstItem = order.getItems().get(0);
            holder.textProduct.setText(firstItem.getProductName() + " (+" + (order.getItems().size() - 1) + " more)");

            Glide.with(context)
                    .load(firstItem.getImagePath())
                    .apply(new RequestOptions().placeholder(R.drawable.pizzaim).error(R.drawable.pizzaim))
                    .into(holder.imageProduct);
        }


        holder.btnUpdateOrder.setOnClickListener(v -> {
            Intent intent = new Intent(context, OrderUpdateActivity.class);
            intent.putExtra("phoneNumber", "0754027915");
            context.startActivity(intent);
        });

    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    public static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView textEmail, textAddress, textProduct, textTotal, textFinalAmount, textStatus;
        ImageView imageProduct;
        Button btnUpdateOrder;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            textEmail = itemView.findViewById(R.id.textViewEmail);
            textAddress = itemView.findViewById(R.id.textAddress);
            textProduct = itemView.findViewById(R.id.textProduct);
            textTotal = itemView.findViewById(R.id.textTotalPrice);
            textFinalAmount = itemView.findViewById(R.id.textTotalAmount);
            textStatus = itemView.findViewById(R.id.textStatus);
            imageProduct = itemView.findViewById(R.id.imageProduct);
            btnUpdateOrder = itemView.findViewById(R.id.btnUpdateOrder);
        }
    }
}
