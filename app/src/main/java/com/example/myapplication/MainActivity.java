package com.example.myapplication;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toolbar;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    static boolean isAdmin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(FirebaseAuth.getInstance().getCurrentUser()==null){
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
        }
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        isAdmin = sharedPreferences.getBoolean("isAdmin", false); // По умолчанию false
    }

    public void btnClickExit(View v){
        showInfoExit("Вы действительно хотите выйти?");
    }

    private void showInfoExit(String text){
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Выход из приложения")
                .setMessage(text)
                .setCancelable(false)
                .setPositiveButton("Да", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //finish();
                        finishAffinity();
                    }
                })
                .setNegativeButton("Нет", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void btn_go_to_cart(View v){
        if(!isAdmin) {
            Intent intent = new Intent(this, CartActivity.class);
            startActivity(intent);
        }
    }

    public void btn_go_to_delivery(View v){
        if(isAdmin){
            Intent intent = new Intent(this, ManagerDeliverysActivity.class);
            startActivity(intent);
        }else {
            Intent intent = new Intent(this, DeliveryUserActivity.class);
            startActivity(intent);
        }
    }

    public void btn_go_to_catalog(View v){
        Intent intent = new Intent(this, CatalogActivity.class);
        startActivity(intent);
    }

    public void bntGoAccount(View v){
        startActivity(new Intent(MainActivity.this, MeAccountActivity.class));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}