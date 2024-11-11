package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.Adapters.ProductAdapter;
import com.example.myapplication.models.Product;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ProductsViewActivity extends AppCompatActivity {

    private String search = null;
    EditText searchLine;
    private ProductAdapter productAdapter;

    private String categoryName;

    ArrayList<Product> ProductsList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_products_view);
        ActionBar actionBar = getSupportActionBar();
        categoryName = getIntent().getExtras().get("category").toString();
        if (actionBar != null) {
            actionBar.setTitle(categoryName);
        }
        searchLine = findViewById(R.id.Search_text);  // Инициализация EditText
        RecyclerView recyclerView = findViewById(R.id.recyclerViewProducts);
        productAdapter = new ProductAdapter(this, ProductsList);
        recyclerView.setAdapter(productAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        fillProductList(search); // Заполняем список продуктов
    }

    private void fillProductList(String search){
        ProductsList.clear();
        DatabaseReference db = FirebaseDatabase.getInstance("https://delivery-of-building-mat-100df-default-rtdb.europe-west1.firebasedatabase.app").getReference();
        db.child("Products").child(categoryName).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot productSnapshot : snapshot.getChildren()){
                    String productCode = productSnapshot.getKey();
                    String productName = productSnapshot.child("name").getValue(String.class);
                    String productPrice = productSnapshot.child("price").getValue(String.class);
                    String productDescription = productSnapshot.child("description").getValue(String.class);
                    String imageUri = productSnapshot.child("image").getValue(String.class);
                    Product product = new Product(productCode, productName, productPrice, productDescription, imageUri);
                    if(search == null || search.isEmpty() || productName.toLowerCase().contains(search.toLowerCase()) || productCode.equals(search))
                        ProductsList.add(product);
                }
                // Уведомляем адаптер о том, что данные изменились
                productAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ProductsViewActivity.this, "Ошибка загрузки продуктов", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void btnGoToCart(View v){
        startActivity(new Intent(ProductsViewActivity.this, CartActivity.class));
    }


    public void btnBackToCatalog(View v){
        startActivity(new Intent(ProductsViewActivity.this, CatalogActivity.class));
    }

    public void btnSearchNeededProduct(View v){
        search = searchLine.getText().toString();
        fillProductList(search);
    }
}