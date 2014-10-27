package fetcher.model;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;

public class Utils {
    /**
     * Opens up a connection with the given URL and returns true if the response code is 200 (success)
     * @param URL_String the URL to test
     * @return Host reacability
     */
    public static boolean isValidURL(String URL_String) {
        try {
            URL url = new URL(URL_String);
            url.toURI();
        } catch (MalformedURLException e) {
            return false;
        } catch (URISyntaxException e) {
            return false;
        }
        return true;
    }

    public static boolean isDirectLinkImage(String image){
        return image.endsWith(".jpg") || image.endsWith(".png") || image.endsWith(".bmp");
    }
}