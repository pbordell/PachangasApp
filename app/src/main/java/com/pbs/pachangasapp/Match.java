package com.pbs.pachangasapp;

public class Match {
    // Business variables
    private String id;
    private String title;
    private String description;
    private String matchDate;

    // Spatial data (Coordinates)
    private double latitude;
    private double longitude;

    // Internal audit data
    private long createdAt;
    private int occupiedPlaces;
    private String status;

    // 🟢 REQUIRED EMPTY CONSTRUCTOR FOR FIREBASE
    public Match() {
    }

    // Full constructor for local use
    public Match(String id, String title, String description, String matchDate,
                 double latitude, double longitude, long createdAt,
                 int occupiedPlaces, String status) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.matchDate = matchDate;
        this.latitude = latitude;
        this.longitude = longitude;
        this.createdAt = createdAt;
        this.occupiedPlaces = occupiedPlaces;
        this.status = status;
    }

    // 🟢 GETTERS AND SETTERS FOR FIREBASE SERIALIZATION
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getMatchDate() { return matchDate; }
    public void setMatchDate(String matchDate) { this.matchDate = matchDate; }

    public double getLatitude() { return latitude; }
    public void setLatitude(double latitude) { this.latitude = latitude; }

    public double getLongitude() { return longitude; }
    public void setLongitude(double longitude) { this.longitude = longitude; }

    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }

    public int getOccupiedPlaces() { return occupiedPlaces; }
    public void setOccupiedPlaces(int occupiedPlaces) { this.occupiedPlaces = occupiedPlaces; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}