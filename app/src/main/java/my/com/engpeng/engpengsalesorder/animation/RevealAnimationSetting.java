package my.com.engpeng.engpengsalesorder.animation;

import org.parceler.Parcel;

@Parcel
public class RevealAnimationSetting {
    public int centerX, centerY, width, height;

    public RevealAnimationSetting(int centerX, int centerY, int width, int height) {
        this.centerX = centerX;
        this.centerY = centerY;
        this.width = width;
        this.height = height;
    }

    public RevealAnimationSetting() {
    }

    public int getCenterX() {
        return centerX;
    }

    public int getCenterY() {
        return centerY;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }


}
