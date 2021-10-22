package GUI;

import javafx.animation.Interpolator;

public class ReverseInterpolator extends Interpolator {

    private final Interpolator reverse;

    public ReverseInterpolator(Interpolator reverse) {
        if (reverse == null) {
            throw new IllegalArgumentException();
        }
        this.reverse = reverse;
    }

    @Override
    protected double curve(double t) {
        return reverse.interpolate(0d, 1d, 1 - t);
    }


    public static Interpolator reverse(Interpolator interpolator) {
        return (interpolator instanceof ReverseInterpolator)
                ? ((ReverseInterpolator) interpolator).reverse
                : new ReverseInterpolator(interpolator);
    }
}