package tfg.com.myapp;


public class SaveGridSett {

    boolean mustTakePhoto = false;
    String mmSeg = "25";
    String mmVolt = "10";
    int ImgWidth = 800;
    int ImgHeight = 600;

    public boolean isMustTakePhoto() {
        return mustTakePhoto;
    }

    public void setMustTakePhoto(boolean mustTakePhoto) {
        this.mustTakePhoto = mustTakePhoto;
    }

    public String getMmSeg() {
        return mmSeg;
    }

    public void setMmSeg(String mmSeg) {
        this.mmSeg = mmSeg;
    }

    public String getMmVolt() {
        return mmVolt;
    }

    public void setMmVolt(String mmVolt) {
        this.mmVolt = mmVolt;
    }

    public int getImgWidth() {
        return ImgWidth;
    }

    public void setImgWidth(int imgWidth) {
        ImgWidth = imgWidth;
    }

    public int getImgHeight() {
        return ImgHeight;
    }

    public void setImgHeight(int imgHeight) {
        ImgHeight = imgHeight;
    }
}
