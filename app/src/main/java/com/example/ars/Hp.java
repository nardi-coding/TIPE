package com.example.ars;

public class Hp {
    public int steps;
    public double learningRate;
    public int directions;
    public int bestDirections;
    public double gravity;

    public double noise;

    public Hp(Utils utils){
        this.steps = (int) utils.getDouble("steps", "1000");
        this.learningRate = utils.getDouble("lr", "0.02");
        this.directions = (int) utils.getDouble("directions", "8");
        this.bestDirections = (int) utils.getDouble("bestdirections", "4");
        this.noise = utils.getDouble("noise", "0.03");
        this.gravity = utils.getDouble("gravity", "9.8");
    }
}
