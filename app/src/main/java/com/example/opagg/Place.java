package com.example.opagg;

public class Place {
    private Long id;

    private String name;

    private String priceandtype;

    private String rating;

    private String adress;

    private String point;

    private String contacts;

    private String tags;

    public Place(String name, String priceandtype, String rating, String adress, String point, String contacts, String tags) {
        this.name = name;
        this.priceandtype = priceandtype;
        this.rating = rating;
        this.adress = adress;
        this.point = point;
        this.contacts = contacts;
        this.tags = tags;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPriceandtype() {
        return priceandtype;
    }

    public void setPriceandtype(String priceandtype) {
        this.priceandtype = priceandtype;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getAdress() {
        return adress;
    }

    public void setAdress(String adress) {
        this.adress = adress;
    }

    public String getPoint() {
        return point;
    }

    public void setPoint(String point) {
        this.point = point;
    }

    public String getContacts() {
        return contacts;
    }

    public void setContacts(String contacts) {
        this.contacts = contacts;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
