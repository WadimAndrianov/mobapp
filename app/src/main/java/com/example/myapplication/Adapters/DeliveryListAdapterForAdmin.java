package com.example.myapplication.Adapters;

import static androidx.core.content.ContextCompat.startActivity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.ProductByDeliveryNumber;
import com.example.myapplication.R;
import com.example.myapplication.models.DeliveryForItem;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class DeliveryListAdapterForAdmin extends RecyclerView.Adapter<DeliveryListAdapterForAdmin.MyViewHolder4> {
    Context context;
    ArrayList<DeliveryForItem> DeliveryList;

    public DeliveryListAdapterForAdmin(Context context, ArrayList<DeliveryForItem> DeliveryList) {
        this.context = context;
        this.DeliveryList = DeliveryList;
    }

    @NonNull
    @Override
    public MyViewHolder4 onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_delivery_adress_and_number, parent, false);
        return new MyViewHolder4(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder4 holder, int position) {
        DeliveryForItem delivery = DeliveryList.get(position);

        holder.delivery_address.setText(delivery.getDeliveryAddress());
        holder.delivery_number.setText(delivery.getDeliveryNumber());

        holder.delivery_address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("Delivery Address", delivery.getDeliveryAddress());
                clipboard.setPrimaryClip(clip);

                Toast.makeText(context, "Адрес скопирован", Toast.LENGTH_SHORT).show();
            }
        });

        holder.delivery_number.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ProductByDeliveryNumber.class);
                intent.putExtra("deliverynumber", ((TextView) view).getText().toString());
                context.startActivity(intent);
            }
        });

        holder.delete_number.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteDelivery(position); // Используем новый метод удаления
            }
        });
    }

    @Override
    public int getItemCount() {
        return this.DeliveryList.size();
    }

    public void attachSwipeToDelete(RecyclerView recyclerView) {
        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false; // Не поддерживаем перетаскивание
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                deleteDelivery(position); // Удаление по индексу
            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    private void deleteDelivery(int position) {
        DeliveryForItem delivery = DeliveryList.get(position);

        DatabaseReference db = FirebaseDatabase.getInstance("https://delivery-of-building-mat-100df-default-rtdb.europe-west1.firebasedatabase.app").getReference();

        db.child("Users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot usersSnapshot : snapshot.getChildren()) {
                    db.child("Users").child(usersSnapshot.getKey().toString()).child("Delivery").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot deliverySnapshot : snapshot.getChildren()) {
                                if (deliverySnapshot.getKey().toString().equals(delivery.getDeliveryNumber())) {
                                    db.child("Users").child(usersSnapshot.getKey().toString()).child("Delivery").child(deliverySnapshot.getKey().toString()).removeValue();
                                    DeliveryList.remove(position);
                                    notifyItemRemoved(position);
                                    notifyItemRangeChanged(position, DeliveryList.size());
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(context, "Ошибка доступа к базе данных", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(context, "Ошибка доступа к базе данных", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static class MyViewHolder4 extends RecyclerView.ViewHolder {
        TextView delivery_address, delivery_number;
        ImageButton delete_number;

        public MyViewHolder4(@NonNull View itemView) {
            super(itemView);
            delivery_address = itemView.findViewById(R.id.DeliveryAdressForAdminInItem);
            delivery_number = itemView.findViewById(R.id.DeliveryNumberForAdminInItem);
            delete_number = itemView.findViewById(R.id.buttonDeleteDelivery);
        }
    }
}
