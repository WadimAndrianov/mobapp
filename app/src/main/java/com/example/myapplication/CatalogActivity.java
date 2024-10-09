package com.example.myapplication;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.content.SharedPreferences;
import android.widget.CheckBox;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.AdminUI.AddNewProductActivity;

public class CatalogActivity extends AppCompatActivity {

    static boolean isAdmin_;
    CheckBox checkBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("Каталог");
        }

        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        isAdmin_ = sharedPreferences.getBoolean("isAdmin", false); // По умолчанию false

        checkBox = findViewById(R.id.check_box_catalog);
        if (isAdmin_) {
            checkBox.setVisibility(View.VISIBLE); // Показать CheckBox
            checkBox.setEnabled(true); // Включить взаимодействие
        } else {
            checkBox.setVisibility(View.GONE); // Скрыть CheckBox
        }

    }

    public void btnClickExit(View v){
        showInfoExit("Вы действительно хотите выйти?");
    }

    private void showInfoExit(String text){
        AlertDialog.Builder builder = new AlertDialog.Builder(CatalogActivity.this);
        builder.setTitle("Выход из приложения")
                .setMessage(text)
                .setCancelable(false)
                .setPositiveButton("Да", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
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

    public void btnGo_exit_to_mainActivity(View v){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    /////////////////КНОПКИ ВЫБОРА КАТЕГОРИИ////////////////
    public void btnBasicMaterials(View v){
        if(isAdmin_ && checkBox.isChecked()){
            Intent intent = new Intent(CatalogActivity.this, AddNewProductActivity.class);
            intent.putExtra("category", "Основные материалы");
            startActivity(intent);
        }else{

        }
    }

    public void btnFinishingMaterials(View v){
        if(isAdmin_ && checkBox.isChecked()){
            Intent intent = new Intent(CatalogActivity.this, AddNewProductActivity.class);
            intent.putExtra("category", "Отделочные материалы");
            startActivity(intent);
        }else{

        }
    }

    public void btnBuildingTools(View v){
        if(isAdmin_ && checkBox.isChecked()){
            Intent intent = new Intent(CatalogActivity.this, AddNewProductActivity.class);
            intent.putExtra("category", "Строительные инструменты");
            startActivity(intent);
        }else{

        }
    }

    public void btnElectricalProducts(View v){
        if(isAdmin_ && checkBox.isChecked()){
            Intent intent = new Intent(CatalogActivity.this, AddNewProductActivity.class);
            intent.putExtra("category", "Электротовары и инструменты");
            startActivity(intent);
        }else{

        }
    }

    public void btnPlumbing(View v){
        if(isAdmin_ && checkBox.isChecked()){
            Intent intent = new Intent(CatalogActivity.this, AddNewProductActivity.class);
            intent.putExtra("category", "Сантехника и водопровод");
            startActivity(intent);
        }else{

        }
    }

    public void btnRoofs(View v){
        if(isAdmin_ && checkBox.isChecked()){
            Intent intent = new Intent(CatalogActivity.this, AddNewProductActivity.class);
            intent.putExtra("category", "Кровельные материалы");
            startActivity(intent);
        }else{

        }
    }

}
