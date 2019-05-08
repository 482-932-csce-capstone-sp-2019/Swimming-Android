package com.example.swimmingwearable;

public class Tuple<X,Y> {
    //Implemented simply for ease
    //Missing many features of a thoroughly designed class but usable

    private X x;
    private Y y;

    //Constructor
    public Tuple(X x, Y y){
        this.x = x;
        this.y = y;
    }

    public X first(){ return this.x;}
    public Y second(){ return this.y;}

    public void setFirst(X x){this.x = x;}
    public void setSecond(Y y){this.y = y;}

    //Can't directly compare with equals but should be able to check elements
}
