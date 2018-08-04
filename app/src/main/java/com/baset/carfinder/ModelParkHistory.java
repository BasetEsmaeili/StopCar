package com.baset.carfinder;

public class ModelParkHistory {
    private int id;
    private String carName;
    private String carColor;
    private String carPlaque;
    private String datePark;
    private String clockPark;
    private String address;
    private double latitude;
    private double longitude;

    public static final String TABLE_NAME = "parkHistory";
    public static final String COLUMN_ID = "id";
    public static final String CAR_NAME = "carName";
    public static final String CAR_COLOR = "carColor";
    public static final String CAR_PLAQUE = "carPlaque";
    public static final String CAR_DATE_PARK = "carDatePark";
    public static final String CAR_CLOCK_PARK = "carClockPark";
    public static final String CAR_PARK_ADDRESS = "carParkAddress";
    public static final String CAR_PARK_LATITUDE = "carParkAddress";
    public static final String CAR_PARK_LONGITUDE = "carParkAddress";

    public ModelParkHistory() {
    }

    public ModelParkHistory(int id, String carName, String carColor, String carPlaque, String datePark, String clockPark, String address, double latitude, double longitude) {
        this.carName = carName;
        this.carColor = carColor;
        this.carPlaque = carPlaque;
        this.datePark = datePark;
        this.clockPark = clockPark;
        this.address = address;
        this.id = id;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCarName() {
        return carName;
    }

    public void setCarName(String carName) {
        this.carName = carName;
    }

    public String getCarColor() {
        return carColor;
    }

    public void setCarColor(String carColor) {
        this.carColor = carColor;
    }

    public String getCarPlaque() {
        return carPlaque;
    }

    public void setCarPlaque(String carPlaque) {
        this.carPlaque = carPlaque;
    }

    public String getDatePark() {
        return datePark;
    }

    public void setDatePark(String datePark) {
        this.datePark = datePark;
    }

    public String getClockPark() {
        return clockPark;
    }

    public void setClockPark(String clockPark) {
        this.clockPark = clockPark;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
