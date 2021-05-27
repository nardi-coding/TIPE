package com.example.ars;

public class Environnement {
    public final Utils utils;
    public final double envWidth;
    public final double envHeight;
    public final double landingFieldX;
    public final double landingFieldY;


    public Environnement(Utils utils){
        this.utils = utils;
        this.envWidth = utils.getDouble("width");
        this.envHeight = utils.getDouble("height");

        String[] list = (utils.getString("groundPos")).split("#");
        this.landingFieldX = Double.parseDouble(list[0].replace(",", "."));
        this.landingFieldY = Double.parseDouble(list[1].replace(",", "."));
    }
}
