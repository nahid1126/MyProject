package com.example.tourmate.model_class;

public class MomentsPhoto {
    private String momentsId;
    private String momentsImage;
    private String momentsComments;

    public MomentsPhoto() {
    }

    public MomentsPhoto(String momentsId, String momentsImage, String momentsComments) {
        this.momentsId = momentsId;
        this.momentsImage = momentsImage;
        this.momentsComments = momentsComments;
    }

    public String getMomentsId() {
        return momentsId;
    }

    public void setMomentsId(String momentsId) {
        this.momentsId = momentsId;
    }

    public String getMomentsImage() {
        return momentsImage;
    }

    public void setMomentsImage(String momentsImage) {
        this.momentsImage = momentsImage;
    }

    public String getMomentsComments() {
        return momentsComments;
    }

    public void setMomentsComments(String momentsComments) {
        this.momentsComments = momentsComments;
    }
}
