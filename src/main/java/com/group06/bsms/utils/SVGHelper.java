package com.group06.bsms.utils;

import com.formdev.flatlaf.extras.FlatSVGIcon;
import java.awt.Color;

public class SVGHelper {

    public static FlatSVGIcon createSVGIconWithFilter(
            String url, Color current, Color light, Color dark,
            Integer width, Integer height
    ) {
        var icon = (width == null || height == null)
                ? new FlatSVGIcon(url) : new FlatSVGIcon(url, width, height);
        var colorFilter = new FlatSVGIcon.ColorFilter();

        colorFilter.add(current, light, dark);
        icon.setColorFilter(colorFilter);

        return icon;
    }

    public static FlatSVGIcon createSVGIconWithFilter(
            String url, Color current, Color color,
            Integer width, Integer height
    ) {
        if (color == Color.white) {
            return createSVGIconWithFilter(
                    url, current, color, Color.black, width, height
            );
        }

        if (color == Color.black) {
            return createSVGIconWithFilter(
                    url, current, color, Color.white, width, height
            );
        }

        return createSVGIconWithFilter(
                url, current, color, color, width, height
        );
    }
}
