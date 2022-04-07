package Model;

import com.google.firebase.database.DatabaseReference;

import java.io.Serializable;
import java.util.List;

import Helper.FirebaseConfig;

public class Ad implements Serializable {

    private String adId;
    private String state;
    private String category;
    private String title;
    private String value;
    private String cellphone;
    private String description;
    private List<String> images;

    public Ad() {

        DatabaseReference addRef = FirebaseConfig.getReference()
                .child("My_Ads");

        setAdId(addRef.push().getKey());
    }

    public void save(){

        String userId = FirebaseConfig.getUserId();

        DatabaseReference addRef = FirebaseConfig.getReference()
                .child("My_Ads");

        addRef.child(userId)
                .child(adId)
                .setValue(this);

    }

    public void savePublic(){


        DatabaseReference addRef = FirebaseConfig.getReference()
                .child("Ads");

        addRef.child(getState())
                .child(getCategory())
                .child(adId)
                .setValue(this);

    }

    public void removeAd(){

        String userId = FirebaseConfig.getUserId();

        DatabaseReference adRef = FirebaseConfig.getReference()
                .child("My_Ads");

        adRef.child(userId)
                .child(adId);
        adRef.removeValue();

        removePublicAd();

    }

    private void removePublicAd(){

        DatabaseReference adRef = FirebaseConfig.getReference()
                .child("Ads");

        adRef.child(getState())
                .child(getCategory())
                .child(adId);
        adRef.removeValue();

    }

    public String getAdId() {
        return adId;
    }

    public void setAdId(String adId) {
        this.adId = adId;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getCellphone() {
        return cellphone;
    }

    public void setCellphone(String cellphone) {
        this.cellphone = cellphone;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }
}
