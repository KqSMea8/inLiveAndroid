package tw.chiae.inlive.data.bean;

/**
 * Just like {@link android.hardware.Camera.Size}, but make it an outer class so it can be
 * initialized anywhere.
 * @author Muyangmin
 * @since 1.0.0
 */
public class CameraSize {

    /**
     * width of the picture
     */
    public int width;
    /**
     * height of the picture
     */
    public int height;

    /**
     * Sets the dimensions for pictures.
     *
     * @param w the photo width (pixels)
     * @param h the photo height (pixels)
     */
    public CameraSize(int w, int h) {
        width = w;
        height = h;
    }

    /**
     * Compares {@code obj} to this size.
     *
     * @param obj the object to compare this size with.
     * @return {@code true} if the width and height of {@code obj} is the
     * same as those of this size. {@code false} otherwise.
     */
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof CameraSize)) {
            return false;
        }
        CameraSize s = (CameraSize) obj;
        return width == s.width && height == s.height;
    }

    @Override
    public int hashCode() {
        return width * 32713 + height;
    }

    @Override
    public String toString() {
        return "CameraSize{" +
                "width=" + width +
                ", height=" + height +
                '}';
    }
}
