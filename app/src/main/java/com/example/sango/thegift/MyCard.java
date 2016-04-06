package com.example.sango.thegift;

/**
 * Created by sango on 2016/4/6.
 */
public class MyCard {
    private int id;
    private String name;
    private int status;

    public MyCard(int id, String name, int status) {
        this.id = id;
        this.name = name;
        this.status = status;
    }

    public int getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public int getStatus() {
        return this.status;
    }
}
