package Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatTextView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toolbar;

import com.example.olx.R;
import com.squareup.picasso.Picasso;
import com.synnapps.carouselview.CarouselView;
import com.synnapps.carouselview.ImageListener;

import Model.Ad;

public class AdDetails extends AppCompatActivity {

    private CarouselView cvAdImages;
    private AppCompatTextView tvTitle, tvPrice, tvState, tvDescription;
    private AppCompatButton btGetCellphone;

    private Ad ad;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ad_details);

        initAndConfigComponents();
        recoverSelectedAdData();
    }

    private void initAndConfigComponents(){

        getSupportActionBar().setTitle("Detalhes do Anúncio");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        cvAdImages = findViewById(R.id.cvAdImages);
        tvTitle = findViewById(R.id.tvTitleAdDetails);
        tvPrice = findViewById(R.id.tvPriceAdDetails);
        tvState = findViewById(R.id.tvStateAdDetails);
        tvDescription = findViewById(R.id.tvDescriptionAdDetails);

        btGetCellphone = findViewById(R.id.btGetCellphone);
        btGetCellphone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("cellphone", ad.getCellphone(), null));
                startActivity(intent);
            }
        });

    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    private void recoverSelectedAdData(){

        ad = (Ad) getIntent().getSerializableExtra("selectedAd");
        if(ad != null){

            tvTitle.setText(ad.getTitle());
            tvPrice.setText("R$"+ad.getValue());
            tvState.setText(ad.getState());
            tvDescription.setText(ad.getDescription());

            ImageListener imageListener = new ImageListener() {
                @Override
                public void setImageForPosition(int position, ImageView imageView) {

                    String urlImage = ad.getImages().get(position);
                    Picasso.get().load(urlImage).into(imageView);

                }
            };

            cvAdImages.setPageCount(ad.getImages().size());
            cvAdImages.setImageListener(imageListener);

        }
    }
}