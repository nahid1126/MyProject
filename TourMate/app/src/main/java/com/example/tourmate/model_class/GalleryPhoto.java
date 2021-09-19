package com.example.tourmate.model_class;

public class GalleryPhoto {
    private String galleryPhotoId;
    private String galleryPhotoLink;

    public GalleryPhoto() {
    }

    public GalleryPhoto(String galleryPhotoId, String galleryPhotoLink) {
        this.galleryPhotoId = galleryPhotoId;
        this.galleryPhotoLink = galleryPhotoLink;
    }

    public String getGalleryPhotoId() {
        return galleryPhotoId;
    }

    public void setGalleryPhotoId(String galleryPhotoId) {
        this.galleryPhotoId = galleryPhotoId;
    }

    public String getGalleryPhotoLink() {
        return galleryPhotoLink;
    }

    public void setGalleryPhotoLink(String galleryPhotoLink) {
        this.galleryPhotoLink = galleryPhotoLink;
    }
}
