package com.support.utilities;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.StateListDrawable;

/**
 * Created by Jeffery on 3/4/2016.
 */
public class ShapeBuilder {

    public static Drawable generateSelectorFromDrawables(Drawable pressed, Drawable normal) {
        StateListDrawable states = new StateListDrawable();
        states.addState(new int[]{ -android.R.attr.state_focused, -android.R.attr.state_pressed, -android.R.attr.state_selected}, normal);
        states.addState(new int[]{ android.R.attr.state_pressed}, pressed);
        states.addState(new int[]{ android.R.attr.state_focused}, pressed);
        states.addState(new int[]{ android.R.attr.state_selected}, pressed);

        return states;
    }

    public static Drawable generateShape(String colorTop, String colorBot, String colorStroke, int stokeSize, float strokeRadius) {
        int top, bot, stroke;
        top = Integer.valueOf(colorTop);
        bot = Integer.valueOf(colorBot);
        stroke = Integer.valueOf(colorStroke);

        GradientDrawable drawable = new GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP, new int[]{top, bot});
        drawable.setStroke(stokeSize, stroke);
        drawable.setCornerRadius(strokeRadius);

        return drawable;
    }

    public static Drawable buildSelectorShapeFromColors(String colorNormalStroke, String colorNormalBackTop, String colorNormalBackBot,
                                                        String colorPressedStroke, String colorPressedBackTop, String colorPressedBackBot,
                                                        int strokeSize, float strokeRadius) {

        Drawable pressed = generateShape(colorPressedBackTop, colorPressedBackBot, colorPressedStroke, strokeSize, strokeRadius);
        Drawable normal = generateShape(colorNormalBackTop, colorNormalBackBot, colorNormalStroke, strokeSize, strokeRadius);
        return generateSelectorFromDrawables(pressed, normal);
    }
}