package com.example.overlaying;

import java.io.Serializable;

public class Vector2Int implements Serializable {
    public int x;
    public int y;
    public Vector2Int(int _x, int _y) {
        x = _x;
        y = _y;
    }
    public Vector2Int() {
        x = 0;
        y = 0;
    }
    public void set(int _x, int _y)
    {
        x = _x;
        y = _y;
    }
    public void set(Vector2Int other)
    {
        x = other.x;
        y = other.y;
    }
    public static double distance(Vector2Int from, Vector2Int to)
    {
        return Math.sqrt(Math.pow(to.x - from.x, 2) + Math.pow(to.y - from.y, 2));
    }
    public static Vector2Int direction(Vector2Int from, Vector2Int to)
    {
        int newX = to.x - from.x;
        int newY = to.y - from.y;
        return new Vector2Int(newX, newY);
    }
    public Vector2Int plus(Vector2Int other)
    {
        return new Vector2Int(this.x + other.x, this.y + other.y);
    }
    public Vector2Int plus(int _x, int _y) {
        return new Vector2Int(this.x + _x, this.y + _y);
    }
    public Vector2Int minus(Vector2Int other)
    {
        return new Vector2Int(this.x - other.x, this.y - other.y);
    }
    public Vector2Int minus(int _x, int _y) {
        return new Vector2Int(this.x - _x, this.y - _y);
    }
    public Vector2Int times(int num) {
        return new Vector2Int(this.x * num, this.y * num);
    }
    public Vector2Int times(Vector2Int other) {
        return new Vector2Int(this.x * other.x, this.y * other.y);
    }
    public Vector2Int divide(int num) {
        return new Vector2Int(this.x / num, this.y / num);
    }
    public Vector2Int divide(Vector2Int other) {
        return new Vector2Int(this.x / other.x, this.y / other.y);
    }
    public Vector2Int mod(int num) {
        return new Vector2Int(this.x % num, this.y % num);
    }
    public Vector2Int mod(Vector2Int other) {
        return new Vector2Int(this.x % other.x, this.y % other.y);
    }

    public Vector2Int toOne()
    {
        Vector2Int res = new Vector2Int();
        if(this.x > 0) res.x = 1;
        else if(this.x < 0) res.x = -1;
        else res.x = 0;

        if(this.y > 0) res.y = 1;
        else if(this.y < 0) res.y = -1;
        else res.y = 0;

        return res;
    }

    @Override
    public boolean equals(Object other)
    {
        if(other == null || other.getClass() != Vector2Int.class)
            return false;
        final Vector2Int obj = (Vector2Int) other;
        return obj.x == this.x && obj.y == this.y;
    }
    public boolean equals(int _x, int _y)
    {
        return _x == this.x && _y == this.y;
    }

    @Override
    public String toString() {
        return "Vector2Int - (" + x + ", " + y + ")";
    }
}
