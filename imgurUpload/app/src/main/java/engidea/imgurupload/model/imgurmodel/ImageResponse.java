package engidea.imgurupload.model.imgurmodel;

import androidx.annotation.NonNull;

/**
 * Response from imgur when uploading to the server.
 */
public class ImageResponse {
    public boolean success;
//    private int status;
    public UploadedImage data;

    public static class UploadedImage {
        public String title;
        public String link;

        String id;
        String description;
        String type;
        boolean animated;
        int width;
        int height;
        int size;
        int views;
        int bandwidth;
        String vote;
        boolean favorite;
        String account_url;
        String deletehash;
        String name;

        @Override @NonNull
        public String toString() {
            return "UploadedImage{" +
                    "id='" + id + '\'' +
                    ", title='" + title + '\'' +
                    ", description='" + description + '\'' +
                    ", type='" + type + '\'' +
                    ", animated=" + animated +
                    ", width=" + width +
                    ", height=" + height +
                    ", size=" + size +
                    ", views=" + views +
                    ", bandwidth=" + bandwidth +
                    ", vote='" + vote + '\'' +
                    ", favorite=" + favorite +
                    ", account_url='" + account_url + '\'' +
                    ", deletehash='" + deletehash + '\'' +
                    ", name='" + name + '\'' +
                    ", link='" + link + '\'' +
                    '}';
        }
    }

    @Override @NonNull
    public String toString() {
        return "ImageResponse{" +
                "success=" + success +
//                ", status=" + status +
                ", data=" + data.toString() +
                '}';
    }
}
