package com.example.yrocery;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.yrocery.POJO.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Login extends AppCompatActivity {
    EditText editPhone, editPassword;
    Button btnSignIn;
    Boolean logedIn;
    SharedPreferences userCredentialsPrefs;
    String userPhone;
    String userPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        logedIn = false;
        editPhone = findViewById(R.id.editPhone);
        editPassword = findViewById(R.id.editPassword);
        btnSignIn = findViewById(R.id.btnSignIn);
        userCredentialsPrefs = getSharedPreferences("userCredentials",0);

        userPhone = userCredentialsPrefs.getString("userPhone", "empty");
        userPassword = userCredentialsPrefs.getString("userPassword", "empty");

        if(!userPhone.equals("empty")) {
            editPhone.setText(userPhone);
        }
        if(!userPassword.equals("empty")) {
            editPassword.setText(userPassword);
        }


        // Init Firebase
        FirebaseDatabase database = FirebaseDatabase.getInstance("https://yrocery-default-rtdb.asia-southeast1.firebasedatabase.app");
        DatabaseReference table_user = database.getReference("User");

        Log.d("mydb", String.valueOf(table_user));

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ProgressDialog progressDialog = new ProgressDialog(Login.this);
                progressDialog.setMessage("Пожалуйста, подождите....");
                progressDialog.show();

                table_user.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        userPhone = editPhone.getText().toString();
                        userPassword = editPassword.getText().toString();

                        if(userPhone.isEmpty() || userPassword.isEmpty()) {
                            progressDialog.dismiss();
                            Toast.makeText(Login.this, "Пожалуйста, заполните все поля", Toast.LENGTH_SHORT).show();
                        } else {
                            // Check if user not exist in database
                            if(!logedIn) {
                                if(snapshot.child(userPhone).exists()) {
                                    progressDialog.dismiss();
                                    // Get User information
                                    User user = snapshot.child(userPhone).getValue(User.class);
                                    assert user != null;
                                    if(user.getPassword().equals(userPassword)) {
                                        Toast.makeText(Login.this, "Успешная авторизация", Toast.LENGTH_SHORT).show();
                                        logedIn = true;

                                        SharedPreferences.Editor editor = userCredentialsPrefs.edit();
                                        editor.putString("userPhone", userPhone);
                                        editor.putString("userPassword", userPassword);
                                        editor.apply();

                                        Intent loginIntent = new Intent(Login.this, Menu.class);
                                        loginIntent.putExtra("userPhone", userPhone);
                                        startActivity(loginIntent);
                                    } else {
                                        Toast.makeText(Login.this, "Неправильный пароль", Toast.LENGTH_SHORT).show();
                                        editPassword.setText("");
                                    }
                                } else {
                                    editPassword.setText("");
                                    editPhone.setText("");
                                    Toast.makeText(Login.this, "Пользователь не существует, сначала зарегистрируйтесь!", Toast.LENGTH_SHORT).show();
                                    progressDialog.dismiss();
                                }
                            }
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });
    }
}