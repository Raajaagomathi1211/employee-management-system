package com.ems.model;

public class LeaveBalance {

    private int casualLeaves;
    private int sickLeaves;
    private int earnedLeaves;

    public LeaveBalance() {
        this.casualLeaves = 12;
        this.sickLeaves = 10;
        this.earnedLeaves = 15;
    }

    public LeaveBalance(int casualLeaves, int sickLeaves, int earnedLeaves) {
        this.casualLeaves = casualLeaves;
        this.sickLeaves = sickLeaves;
        this.earnedLeaves = earnedLeaves;
    }

    public boolean deduct(LeaveType type, int days) {
        switch (type) {
            case CASUAL:
                if (casualLeaves >= days) { casualLeaves -= days; return true; }
                return false;
            case SICK:
                if (sickLeaves >= days) { sickLeaves -= days; return true; }
                return false;
            case EARNED:
                if (earnedLeaves >= days) { earnedLeaves -= days; return true; }
                return false;
            default:
                return false;
        }
    }

    public int getCasualLeaves() { return casualLeaves; }
    public void setCasualLeaves(int casualLeaves) { this.casualLeaves = casualLeaves; }
    public int getSickLeaves() { return sickLeaves; }
    public void setSickLeaves(int sickLeaves) { this.sickLeaves = sickLeaves; }
    public int getEarnedLeaves() { return earnedLeaves; }
    public void setEarnedLeaves(int earnedLeaves) { this.earnedLeaves = earnedLeaves; }
}
