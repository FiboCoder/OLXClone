package Activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.olx.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import Adapter.Ads;
import Helper.FirebaseConfig;
import Helper.RecyclerItemClickListener;
import Model.Ad;
import dmax.dialog.SpotsDialog;

public class MyAds extends AppCompatActivity {

    private FloatingActionButton fab;

    //RecyclerView
    private RecyclerView rvMyAds;
    private List<Ad> myAdsList = new ArrayList<>();
    private Ads adapter;

    //Firebase
    private DatabaseReference myAdsRef;
    private DatabaseReference publicAdsRef;

    private AlertDialog alertDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_ads);

        initAndConfigComponents();
        recoverMyAdds();

    }

    private void initAndConfigComponents(){

        //Init and config Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Meus Anúncios");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Init and config Floating Action Button
        fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(MyAds.this, AddAd.class));
            }
        });

        //Firebase
        String currentUserId = FirebaseConfig.getUserId();
        myAdsRef = FirebaseConfig.getReference().child("My_Ads").child(currentUserId);
        publicAdsRef = FirebaseConfig.getReference().child("Ads").child(currentUserId);

        //Init and config Ads adapter
        adapter = new Ads(getApplicationContext(), myAdsList);

        //Init and config Recycler View to current user adds
        rvMyAds =  findViewById(R.id.rvMyAds);
        rvMyAds.setLayoutManager(new LinearLayoutManager(this));
        rvMyAds.setHasFixedSize(true);
        rvMyAds.setAdapter(adapter);

        rvMyAds.addOnItemTouchListener(new RecyclerItemClickListener(
                MyAds.this, rvMyAds, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

            }

            @Override
            public void onLongItemClick(View view, int position) {

                Ad ad = myAdsList.get(position);
                ad.removeAd();
            }

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        }
        ));

    }

    private void recoverMyAdds(){

        alertDialog = new SpotsDialog.Builder()
                .setContext(this)
                .setMessage("Carregando Anúncios")
                .setCancelable(false)
                .build();
        alertDialog.show();

        myAdsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                myAdsList.clear();
                for(DataSnapshot adds : snapshot.getChildren()){

                    Ad ad = adds.getValue(Ad.class);
                    myAdsList.add(ad);
                }

                Collections.reverse(myAdsList);
                adapter.notifyDataSetChanged();
                alertDialog.dismiss();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

}