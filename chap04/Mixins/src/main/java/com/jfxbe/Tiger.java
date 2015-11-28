package com.jfxbe;

public class Tiger implements Cat, Roarable {

    @Override
    public String getCatKind() {
        return getClass().getSimpleName();
    }

    @Override
    public String getFurDescription() {
        return "striped";
    }
}
