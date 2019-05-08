package sw.gmit.ie.crist.cameradetection.Enums;

public enum ImageReq {
    // An enum deciding
    // whether the picture is taken from the gallery
    // or taken with the camera in real time
    TAKE_IMAGE_REQUEST (0), CHOOSE_IMAGE_REQUEST (1);

    private Integer value; // The value of the image request

    // Getter
    public Integer getValue() {
        return value;
    }

    // Constructor
    ImageReq(Integer value) {
        this.value = value;
    }
}