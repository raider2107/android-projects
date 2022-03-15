package com.example.yrocery.Fragments;

import android.os.Bundle;

import androidx.annotation.CallSuper;
import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.ListFragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.NumberPicker;

import com.example.yrocery.Adapters.CustomArrayAdapter;
import com.example.yrocery.POJO.Product;
import com.example.yrocery.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Fruits extends ListFragment {
    private ArrayList<Product> productList = new ArrayList<>();
    private CustomArrayAdapter mAdapter;

    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseDatabase database = FirebaseDatabase.getInstance("https://yrocery-default-rtdb.asia-southeast1.firebasedatabase.app");
        DatabaseReference table_products = database.getReference("Fruits");

        table_products.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(int i = 1; i <= snapshot.getChildrenCount(); i++) {
                    productList.add(snapshot.child(i + "").getValue(Product.class));
                }
                Log.d("product", "onDataChange: " + productList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mAdapter = new CustomArrayAdapter(getActivity(), R.id.menu_content, productList);
        setListAdapter(mAdapter);

//        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
//                inflater.getContext(),
//                R.layout.custom_row,
//                R.id.productName,
//                getResources().getStringArray(R.array.fruits));
//        setListAdapter(adapter);
        return super.onCreateView(inflater, container, savedInstanceState);
    }
}
