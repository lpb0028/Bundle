package com.example.overlaying;

public class Vector2 {
    public double x;
    public double y;
    public Vector2(double _x, double _y) {
        x = _x;
        y = _y;
    }
    public Vector2() {
        x = 0;
        y = 0;
    }
    public void set(double _x, double _y)
    {
        x = _x;
        y = _y;
    }
    public void set(Vector2 other)
    {
        x = other.x;
        y = other.y;
    }
    public static double distance(Vector2 from, Vector2 to)
    {
        return Math.sqrt(Math.pow(to.x - from.x, 2) + Math.pow(to.y - from.y, 2));
    }
    public static Vector2 direction(Vector2 from, Vector2 to)
    {
        double newX = to.x - from.x;
        double newY = to.y - from.y;
        return new Vector2(newX, newY);
    }
    public Vector2 round()
    {
        return new Vector2(Math.round(this.x), Math.round(this.y));
    }
    @Override
    public String toString() {
        return "(" + x + ", " + y + ")";
    }
    public Vector2 minus(Vector2 other)
    {
        return new Vector2(this.x - other.x, this.y - other.y);
    }
    public Vector2 plus(Vector2 other)
    {
        return new Vector2(this.x + other.x, this.y + other.y);
    }
    public Vector2 times(double num) { return new Vector2(this.x * num, this.y * num);}
    public Vector2 times(Vector2 other) { return new Vector2(this.x * other.x, this.y * other.y);}
    public Vector2 toOne()
    {
        Vector2 res = new Vector2();
        if(this.x > 0)
            res.x = 1;
        else if(this.x < 0)
            res.x = -1;
        else
            res.x = 0;

        if(this.y > 0)
            res.y = 1;
        else if(this.y < 0)
            res.y = -1;
        else
            res.y = 0;
        return res;
    }
    @Override
    public boolean equals(Object other)
    {
        if(other == null || other.getClass() != Vector2.class)
            return false;
        final Vector2 obj = (Vector2) other;
        if(obj.x == this.x && obj.y == this.y)
            return true;
        return false;
    }
    public boolean equals(double _x, double _y)
    {
        if(_x == this.x && _y == this.y)
            return true;
        return false;
    }
}
