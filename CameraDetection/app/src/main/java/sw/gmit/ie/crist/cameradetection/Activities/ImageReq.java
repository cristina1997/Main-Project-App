package sw.gmit.ie.crist.cameradetection.Activities;

public enum ImageReq {
    TAKE_IMAGE_REQUEST (0), CHOOSE_IMAGE_REQUEST (1);
    private Integer value;

    public Integer getValue() {
        return value;
    }

    ImageReq(Integer value) {
        this.value = value;
    }
}