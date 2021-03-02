package com.example.tracker;

class Punto {
    private double lat, lng;

    public Punto(double lat, double lng) {
        this.lat = lat;
        this.lng = lng;
    }
    public Punto() {

    }
    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }
}
