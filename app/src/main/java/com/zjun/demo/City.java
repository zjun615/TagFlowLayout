package com.zjun.demo;

/**
 * City
 *
 * @author Ralap
 * @description
 * @date 2018/4/1
 */

public class City {
    private int id;
    private String name;
    private boolean isChosen;

    public City() {
    }

    public City(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public City(int id, String name, boolean isChosen) {
        this.id = id;
        this.name = name;
        this.isChosen = isChosen;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isChosen() {
        return isChosen;
    }

    public void setChosen(boolean chosen) {
        isChosen = chosen;
    }
}
