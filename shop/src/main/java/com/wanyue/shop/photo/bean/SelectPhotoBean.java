package com.wanyue.shop.photo.bean;

public class SelectPhotoBean {
    private String photo;
    private boolean isBtn;

    public SelectPhotoBean(){

    }
    public SelectPhotoBean(String photo) {
        this.photo = photo;
    }

    public SelectPhotoBean(boolean isBtn) {
        this.isBtn = isBtn;
    }

    public String getPhoto() {
        return photo;
    }
    public void setPhoto(String photo) {
        this.photo = photo;
    }


    public boolean isBtn() {
        return isBtn;
    }

    public void setBtn(boolean btn) {
        isBtn = btn;
    }
}
