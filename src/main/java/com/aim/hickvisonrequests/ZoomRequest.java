package com.aim.hickvisonrequests;

import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by victor on 13.08.15.
 */
public class ZoomRequest {
    private int zoom;

    private String url = "http://192.168.2.64/ISAPI/PTZCtrl/channels/1/continuous";
    private final String USER_AGENT = "Chrome/40.0.2214.111";

    public int getZoom() {
        return zoom;
    }

    public void setZoom(int zoom) {
        this.zoom = zoom;
    }

    /**
     * створення запиту для курування зумом
     * @param  zoom - -100..-1 - зум "-", 1..100 - зум "+", 0 - зупинка
     */
    public void setRequest(int zoom) {

        setZoom(zoom);
        this.request =  "<PTZData version=\"2.0\" xmlns=\"http://www.isapi.org/ver20/XMLSchema\">\n" +
                "<pan> 0 </pan>\n" +
                "<tilt> 0 </tilt>\n" +
                "<zoom> " + zoom + " </zoom>\n" +
                "</PTZData>";
    }

    private String request;

    public ZoomRequest() {
    }

    public ZoomRequest(String url) {
        this.url = url;
    }

    /**
     * посилання PUT-запиту
     */
    public void start() {
        try {
            URL urlAdr = new URL(this.url);

            HttpURLConnection urlConnection = (HttpURLConnection) urlAdr.openConnection();


            urlConnection.setRequestMethod("PUT");
            String userpass = "admin" + ":" + "12345";
            String basicAuth = "Basic " + javax.xml.bind.DatatypeConverter.printBase64Binary(userpass.getBytes());
            urlConnection.setRequestProperty("Authorization", basicAuth);
            urlConnection.setRequestProperty("User-Agent", USER_AGENT);
            urlConnection.setDoOutput(true);

            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(urlConnection.getOutputStream());

            outputStreamWriter.write(request);

            outputStreamWriter.close();

            int responseCode = urlConnection.getResponseCode();

        } catch (Exception e){
            e.printStackTrace();
        }
    }

}
