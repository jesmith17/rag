package com.mongodb.api.models;

public class Justification {
    private boolean complete;
    private boolean accurate;
    private boolean easyToUnderstand;

    public boolean isComplete() {
        return complete;
    }

    public void setComplete(boolean complete) {
        this.complete = complete;
    }

    public boolean isAccurate() {
        return accurate;
    }

    public void setAccurate(boolean accurate) {
        this.accurate = accurate;
    }

    public boolean isEasyToUnderstand() {
        return easyToUnderstand;
    }

    public void setEasyToUnderstand(boolean easyToUnderstand) {
        this.easyToUnderstand = easyToUnderstand;
    }
}