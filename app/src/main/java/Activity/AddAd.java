package Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatSpinner;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.blackcat.currencyedittext.CurrencyEditText;
import com.example.olx.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.santalu.maskara.widget.MaskEditText;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import Helper.FirebaseConfig;
import Helper.Permission;
import Model.Ad;
import dmax.dialog.SpotsDialog;

public class AddAd extends AppCompatActivity implements View.OnClickListener{

    private AppCompatImageView ivAdd1, ivAdd2, ivAdd3;
    private AppCompatSpinner spState, spCategory;
    private AppCompatEditText etTitle, etDescription;
    private CurrencyEditText etValue;
    private MaskEditText etCellphoneNumber;
    private Ad ad;

    private String[] permissions = new String[]{

            Manifest.permission.READ_EXTERNAL_STORAGE

    };

    private List<String> imagesList = new ArrayList<>();
    private List<String> urlImagesList = new ArrayList<>();

    private StorageReference storage;

    private android.app.AlertDialog alertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_ad);

        initAndConfigComponents();
        loadSpinnerData();
    }

    private void initAndConfigComponents(){

        Permission.validatePermissions(permissions, this, 1);

        ivAdd1 = findViewById(R.id.ivAdd1);
        ivAdd2 = findViewById(R.id.ivAdd2);
        ivAdd3 = findViewById(R.id.ivAdd3);
        spState = findViewById(R.id.spState);
        spCategory = findViewById(R.id.spCategory);
        etTitle = findViewById(R.id.etTitleAddAd);
        etDescription = findViewById(R.id.etDescriptionAddAd);

        //Init and config Edit Text Value
        etValue = findViewById(R.id.etValueAddAd);
        Locale locale = new Locale("pt", "BR");
        etValue.setLocale(locale);

        etCellphoneNumber = findViewById(R.id.etCellphoneNumberAddAd);

        storage = FirebaseConfig.getStorage();
    }

    private Ad configAdd() {

        String state = spState.getSelectedItem().toString();
        String category = spCategory.getSelectedItem().toString();
        String title = etTitle.getText().toString();
        String value = etValue.getText().toString();
        String cellphone = etCellphoneNumber.getText().toString();
        String description = etDescription.getText().toString();

        Ad ad = new Ad();
        ad.setState(state);
        ad.setCategory(category);
        ad.setTitle(title);
        ad.setValue(value);
        ad.setCellphone(cellphone);
        ad.setDescription(description);

        return ad;

    }

        public void validateAddData(View view){

        ad = configAdd();
        String value = String.valueOf(etValue.getRawValue());


            if(imagesList.size() != 0){
            
            if(ad.getState() != null && !ad.getState().isEmpty()){

                if(!ad.getCategory().isEmpty()){

                    if(!ad.getTitle().isEmpty()){

                        if(!value.isEmpty() && !value.equals(0)){

                            if(!ad.getCellphone().isEmpty() && ad.getCellphone().length() >= 11){

                                if(!ad.getDescription().isEmpty()){

                                    saveAdd();

                                }else {
                                    showMessage("Preencha o campo descrição!");
                                }

                            }else {
                                showMessage("Preencha o campo telefone com os 11 números incluindo o DDD!");
                            }

                        }else {
                            showMessage("Preencha um valor acima de R$0,00!");
                        }

                    }else {
                        showMessage("Preencha o campo título!");
                    }

                }else{
                    showMessage("Escolha uma categoria!");
                }

            }else{
                showMessage("Escolha o estado!");
            }
            
        }else{
            showMessage("Selecione pelo menos 1 foto!");
        }
    }

    private void saveAdd(){

        alertDialog = new SpotsDialog.Builder()
        .setContext(this)
        .setMessage("Salvando Anúncio")
        .setCancelable(false)
        .build();
        alertDialog.show();
        for(int i=0; i < imagesList.size(); i++){

            String imageUrl = imagesList.get(i);
            int listSize = imagesList.size();
            saveImage(imageUrl, listSize, i);
        }

    }



    private void saveImage(String url, int totalImages, int counter){

        StorageReference imageRef = storage.child("Images")
                .child("Ads")
                .child(ad.getAdId())
                .child("image"+counter);

        UploadTask uploadTask = imageRef.putFile(Uri.parse(url));
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                imageRef.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {

                        Uri url = task.getResult();
                        String convertedUrl = url.toString();

                        urlImagesList.add(convertedUrl);

                        if(totalImages == urlImagesList.size()){

                            ad.setImages(urlImagesList);
                            ad.save();
                            ad.savePublic();
                            alertDialog.dismiss();
                            finish();
                        }
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                showMessage("Erro ao fazer upload da imagem!");
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        for(int resultPermission : grantResults){

            if(resultPermission == PackageManager.PERMISSION_DENIED){

                alertNeededPermissions();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void alertNeededPermissions(){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Permissões Negadas");
        builder.setMessage("Para utilizar o OLX é necessário aceitar as permissões!");
        builder.setCancelable(false);
        builder.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void loadSpinnerData(){

        //Config States spinner
        String[] states = getResources().getStringArray(R.array.states);
        ArrayAdapter<String> statesAdapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item, states
        );

        statesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spState.setAdapter(statesAdapter);

        //Config Category spinner
        String[] category = getResources().getStringArray(R.array.category);
        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item, category
        );

        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spCategory.setAdapter(categoryAdapter);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){

            case R.id.ivAdd1:
                chooseImage(1);
                break;

            case R.id.ivAdd2:
                chooseImage(2);
                break;

            case R.id.ivAdd3:
                chooseImage(3);

                break;
        }

    }

    private void chooseImage(int requestCode){

        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, requestCode);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == Activity.RESULT_OK){

            Uri image = data.getData();
            String localImage = image.toString();

            if(requestCode == 1){

                ivAdd1.setImageURI(image);
            }else if(requestCode == 2){

                ivAdd2.setImageURI(image);
            }else if(requestCode == 3){

                ivAdd3.setImageURI(image);
            }

            imagesList.add(localImage);

        }
    }

    private void showMessage(String message){

        Toast.makeText(AddAd.this, message, Toast.LENGTH_SHORT).show();
    }
}