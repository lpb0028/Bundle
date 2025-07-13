package com.example.overlaying;

public class AnimationCurve {
    enum AnimForm
    {
        COS,  // Smooth In+Out
        CUBE,  // Slow Start
        CBRT, // Fast Start
        LINEAR, // Linear
        CUSTOM
    }
    public static double Evaluate(AnimForm animForm, double time)
    {
        switch (animForm)
        {
            case COS:
                return (.5d * Math.cos(Math.PI * time)) + .5d;
            case CUBE:
                return Math.pow(time, 3);
            case CBRT:
                return Math.cbrt(time);
            case LINEAR:
                return time;
            case CUSTOM:
                return Math.cbrt(time) + .25 - Math.pow(time - .5, 2);
        }
        return 0;
    }
    public static double ReverseEvaluate(AnimForm animForm, double val)
    {
        switch (animForm)
        {
            case COS:
                return Math.acos((val - .5d) * 2) / Math.PI;
            case CUBE:
                return Math.cbrt(val);
            case CBRT:
                return Math.pow(val, 3);
            case LINEAR:
                return val;
            case CUSTOM:
                return Math.pow(val, 3) - .25 + Math.sqrt(val + .5);
        }
        return 0;
    }
}
