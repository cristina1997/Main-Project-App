package sw.gmit.ie.crist.cameradetection.Activities;

public enum ImageReq {
    PICK_IMAGE_REQUEST (1), CAPTURE_IMAGE_REQUEST (0);
    private int value;

    public int getValue() {
        return value;
    }

    ImageReq(int value) {
        this.value = value;
    }
}