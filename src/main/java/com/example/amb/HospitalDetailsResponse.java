package com.example.amb;

// You might want to put this in a 'model' or 'network/response' package
// import com.google.gson.annotations.SerializedName; // If field names differ from JSON keys

public class HospitalDetailsResponse {
    // @SerializedName("id") // Use if your Java variable name is different from JSON key
    private int id;
    // @SerializedName("name")
    private String name;
    // @SerializedName("code")
    private String code;
    // @SerializedName("location")
    private String location;
    // @SerializedName("latitude")
    private Double latitude; // Use Double for precision
    // @SerializedName("longitude")
    private Double longitude;

    // Getters and Setters (or make fields public if you prefer, though getters/setters are good practice)
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public Double getLatitude() { return latitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }

    public Double getLongitude() { return longitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }

    // Optional: toString() method for debugging
    @Override
    public String toString() {
        return "HospitalDetailsResponse{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", code='" + code + '\'' +
                ", location='" + location + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                '}';
    }
}