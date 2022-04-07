package Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.olx.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import Helper.FirebaseConfig;
import Helper.RecyclerItemClickListener;
import Model.Ad;
import dmax.dialog.SpotsDialog;

public class Ads extends AppCompatActivity {


    //Components
    private AlertDialog alertDialog;
    private AppCompatButton btFilterState, btFilterCategory;

    //Firebase
    private FirebaseAuth auth;
    private DatabaseReference publicAdsRef;

    //RecyclerView
    private RecyclerView rvAds;
    private List<Ad> publicAdsList = new ArrayList<>();
    private Adapter.Ads adapter;

    private boolean isFilteredState = false;

    private String state, category;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ads);

        initAndConfigComponents();
        recoverPublicAds();
    }

    private void initAndConfigComponents(){

        //Components
        btFilterState = findViewById(R.id.btFilterState);
        btFilterState.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                recoverFilteredAds("state");
            }
        });

        btFilterCategory = findViewById(R.id.btFilterCategory);
        btFilterCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                recoverFilteredAds("category");
            }
        });

        //Firebase
        auth = FirebaseConfig.getAuth();
        publicAdsRef = FirebaseConfig.getReference().child("Ads");

        //Ads adapter
        adapter = new Adapter.Ads(Ads.this, publicAdsList);

        //RecyclerView
        rvAds = findViewById(R.id.rvAds);
        rvAds.setLayoutManager(new LinearLayoutManager(Ads.this));
        rvAds.setHasFixedSize(true);
        rvAds.setAdapter(adapter);
        rvAds.addOnItemTouchListener(new RecyclerItemClickListener(
                Ads.this, rvAds, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

                Intent intent = new Intent(Ads.this, AdDetails.class);
                intent.putExtra("selectedAd", publicAdsList.get(position));
                startActivity(intent);

            }

            @Override
            public void onLongItemClick(View view, int position) {

            }

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        }
        ));
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        if(auth.getCurrentUser() == null){

            menu.setGroupVisible(R.id.unLoggedUserGroup,true);

        }else{

            menu.setGroupVisible(R.id.loggedUserGroup, true);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){

            case  R.id.loggingOrRegister:
                Intent intent= new Intent(getApplicationContext(), LoginAndRegister.class);
                startActivity(intent);
                break;

            case R.id.loggout:
                auth.signOut();
                invalidateOptionsMenu();
                break;

            case R.id.myAds:
                startActivity(new Intent(Ads.this, MyAds.class));
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    private void recoverPublicAds(){

        alertDialog = new SpotsDialog.Builder()
                .setContext(this)
                .setMessage("Carregando Anúncios")
                .setCancelable(false)
                .build();
        alertDialog.show();

        publicAdsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for(DataSnapshot dsState : snapshot.getChildren()){
                    for(DataSnapshot dsCategory : dsState.getChildren()){
                        for(DataSnapshot dsAds : dsCategory.getChildren()){

                            Ad ad = dsAds.getValue(Ad.class);
                            publicAdsList.add(ad);

                        }
                    }
                }

                Collections.reverse(publicAdsList);
                adapter.notifyDataSetChanged();
                alertDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void recoverFilteredAds(String btn){

        if(btn.equals("state")){

            //Create vie to inflate spinner states
            View viewSpinner = getLayoutInflater().inflate(R.layout.dialog_spinner, null);

            //Spinner states
            AppCompatSpinner spState = viewSpinner.findViewById(R.id.filterSpinner);
            String[] statesArray = getResources().getStringArray(R.array.states);
            ArrayAdapter<String> statesAdapter = new ArrayAdapter<String>(
                    this, android.R.layout.simple_spinner_item, statesArray
            );
            statesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spState.setAdapter(statesAdapter);

            //Alert Dialog
            androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
            builder.setView(viewSpinner);
            builder.setTitle("Selecione o estado");
            builder.setCancelable(false);
            builder.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    state = spState.getSelectedItem().toString();
                    DatabaseReference stateRef = FirebaseConfig.getReference()
                            .child("Ads")
                            .child(state);
                    stateRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            publicAdsList.clear();
                            for(DataSnapshot dsCategory : snapshot.getChildren()){

                                for(DataSnapshot dsAds: dsCategory.getChildren()){

                                    Ad ad = dsAds.getValue(Ad.class);
                                    publicAdsList.add(ad);

                                    Toast.makeText(getApplicationContext(), ad.getState(), Toast.LENGTH_SHORT).show();

                                }

                                Collections.reverse(publicAdsList);
                                adapter.notifyDataSetChanged();
                                isFilteredState = true;
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                }
            });
            builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    alertDialog.dismiss();
                }
            });

            androidx.appcompat.app.AlertDialog dialog = builder.create();
            dialog.show();

        }else if(btn.equals("category") && isFilteredState){

            //Create vie to inflate spinner states
            View viewSpinner = getLayoutInflater().inflate(R.layout.dialog_spinner, null);

            //Spinner category
            AppCompatSpinner spCategory = viewSpinner.findViewById(R.id.filterSpinner);
            String[] categoryArray = getResources().getStringArray(R.array.category);
            ArrayAdapter<String> categoryAdapter = new ArrayAdapter<String>(
                    this, android.R.layout.simple_spinner_item, categoryArray
            );
            categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spCategory.setAdapter(categoryAdapter);

            //Alert Dialog
            androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
            builder.setView(viewSpinner);
            builder.setTitle("Selecione a categoria");
            builder.setCancelable(false);
            builder.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {


                    category = spCategory.getSelectedItem().toString();
                    DatabaseReference categoryRef = FirebaseConfig.getReference()
                            .child("Ads")
                            .child(state)
                            .child(category);

                    categoryRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            publicAdsList.clear();
                            for(DataSnapshot dsAds : snapshot.getChildren()){

                                Ad ad = dsAds.getValue(Ad.class);
                                publicAdsList.add(ad);
                            }

                            Collections.reverse(publicAdsList);
                            adapter.notifyDataSetChanged();

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                }
            });
            builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    alertDialog.dismiss();
                }
            });

            androidx.appcompat.app.AlertDialog dialog = builder.create();
            dialog.show();

        }else if(btn.equals("category") && !isFilteredState){

            Toast.makeText(Ads.this, "Selecione o ESTADO primeiro!", Toast.LENGTH_SHORT).show();

        }

    }
}