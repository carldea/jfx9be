package com.jfxbe;

public class HouseCat implements Cat, Purrable, Meowler {

    @Override
    public String getCatKind() {
        return "Domestic Cat";
    }

    @Override
    public String getFurDescription() {
        return "mixed brown and white";
    }
    
}
