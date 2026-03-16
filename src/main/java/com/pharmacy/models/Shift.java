package com.pharmacy.models;

import java.sql.Timestamp;

public class Shift {
    private int shiftId;
    private int userId;
    private String username;
    private Timestamp openTime;
    private Timestamp closeTime;
    private double openingCash;
    private double closingCash;
    private double expectedCash;
    private String status;

    public Shift() {}

    public int getShiftId() { return shiftId; }
    public void setShiftId(int shiftId) { this.shiftId = shiftId; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public Timestamp getOpenTime() { return openTime; }
    public void setOpenTime(Timestamp openTime) { this.openTime = openTime; }

    public Timestamp getCloseTime() { return closeTime; }
    public void setCloseTime(Timestamp closeTime) { this.closeTime = closeTime; }

    public double getOpeningCash() { return openingCash; }
    public void setOpeningCash(double openingCash) { this.openingCash = openingCash; }

    public double getClosingCash() { return closingCash; }
    public void setClosingCash(double closingCash) { this.closingCash = closingCash; }

    public double getExpectedCash() { return expectedCash; }
    public void setExpectedCash(double expectedCash) { this.expectedCash = expectedCash; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
