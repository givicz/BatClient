package me.BATapp.batclient.utils;

public class SmoothScroll {
    private double currentScroll = 0;
    private double targetScroll = 0;
    private static final double SMOOTHNESS = 0.15;
    
    public SmoothScroll(double initial) {
        this.currentScroll = initial;
        this.targetScroll = initial;
    }
    
    public void setTarget(double target) {
        this.targetScroll = target;
    }
    
    public double update() {
        currentScroll = currentScroll + (targetScroll - currentScroll) * SMOOTHNESS;
        return currentScroll;
    }
    
    public double getCurrent() {
        return currentScroll;
    }
    
    public void setCurrent(double value) {
        this.currentScroll = value;
        this.targetScroll = value;
    }
}
