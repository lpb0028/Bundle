package com.example.overlaying;

public class AnimationCurve {
    enum AnimForm
    {
        COS,  // Smooth In+Out
        CUBE,  // Slow Start
        CBRT, // Fast Start
        LINEAR, // Linear
        CUSTOM1,
        CUSTOM2
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
            case CUSTOM1:
                return Math.cbrt(time) + .25 - Math.pow(time - .5, 2);
            case CUSTOM2:
                return -1 * Math.pow(3/2f * time - 3/4f, 2) + 9/16f;
        }
        return 0;
    }
}
