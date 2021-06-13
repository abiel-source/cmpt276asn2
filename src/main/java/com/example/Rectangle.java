package com.example;

public class Rectangle {
    private String name;
    private String color;
    private int width;
    private int height;

    // accessors
    // =========
    public String getName()
    {
        return this.name;
    }

    public String getColor()
    {
        return this.color;
    }

    public int getWidth()
    {
        return this.width;
    }

    public int getHeight()
    {
        return this.height;
    }

    // mutators
    // ========
    public void setName(String n)
    {
        this.name = n;
    }

    public void setColor(String c)
    {
        this.color = c;
    }

    public void setWidth(int w)
    {
        this.width = w;
    }

    public void setHeight(int h)
    {
        this.height = h;
    }
}