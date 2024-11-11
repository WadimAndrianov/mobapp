package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.Adapters.DeliveryListAdapterForAdmin;
import com.example.myapplication.Adapters.DeliveryProductAdapter;
import com.example.myapplication.models.DeliveryForItem;
import com.example.myapplication.models.ProductsInDelivery;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ManagerDeliverysActivity extends AppCompatActivity {
    private DeliveryListAdapterForAdmin deliveryListAdapterForAdmin;

    ArrayList<DeliveryForItem> deliveryList = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager_deliverys);
        RecyclerView recyclerView = findViewById(R.id.recyclerViewDeliverysListForAdmin);
        deliveryListAdapterForAdmin = new DeliveryListAdapterForAdmin(this, deliveryList);
        recyclerView.setAdapter(deliveryListAdapterForAdmin);
        deliveryListAdapterForAdmin.attachSwipeToDelete(recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        fillDeliveryList(); // Заполняем список доставок
    }

    private void fillDeliveryList() {
        deliveryList.clear();
        DatabaseReference db = FirebaseDatabase.getInstance("https://delivery-of-building-mat-100df-default-rtdb.europe-west1.firebasedatabase.app").getReference();
        db.child("Users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot usersSnapshot : snapshot.getChildren()) {
                    db.child("Users").child(usersSnapshot.getKey().toString()).child("Delivery").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for(DataSnapshot deliverySnapshot : snapshot.getChildren()){
                                String deliveryNumber = deliverySnapshot.getKey().toString();
                                String deliveryAddress = deliverySnapshot.child("Address").getValue().toString();
                                DeliveryForItem delivery = new DeliveryForItem(deliveryAddress, deliveryNumber);
                                deliveryList.add(delivery);
                                deliveryListAdapterForAdmin.notifyDataSetChanged();
                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(ManagerDeliverysActivity.this, "Ошибка доступа к базе данных", Toast.LENGTH_SHORT).show();
                        }
                    });

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ManagerDeliverysActivity.this, "Ошибка доступа к базе данных", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void btnGoActivityToSearchDeliveryByUsersPhoneNumber(View v){
        startActivity(new Intent(ManagerDeliverysActivity.this, ProductsDeliveryByUsersPhone.class));
    }
}