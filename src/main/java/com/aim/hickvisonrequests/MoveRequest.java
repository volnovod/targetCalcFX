package com.aim.hickvisonrequests;

import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by victor on 26.03.15.
 * клас, який повертає камеру HIKVISION в потрібному положенні
 */
public class MoveRequest {

    private String url = "http://192.168.2.64/ISAPI/PTZCtrl/channels/1/absolute";
    private final String USER_AGENT = "Chrome/40.0.2214.111";
    private boolean isMoveAround = false;
    private int elevation;
    private int azimuth;
    private ZoomRequest zoom;

    public ZoomRequest getZoom() {
        return zoom;
    }

    public void setZoom(ZoomRequest zoom) {
        this.zoom = zoom;
    }

    public int getAzimuth() {
        return azimuth;
    }

    public void setAzimuth(int azimuth) {
        this.azimuth = azimuth;
    }

    public int getElevation() {
        return elevation;
    }

    public void setElevation(int elevation) {
        this.elevation = elevation;
    }



    public boolean isMoveAround() {
        return isMoveAround;
    }

    public void setIsMoveAround(boolean isMoveAround) {
        this.isMoveAround = isMoveAround;
    }

    /**
     * створення запиту на поворот
     * @param elevation - кут місця камери після повороту
     * @param azimuth - азимут камери після повороту
     */
    public void setRequest(double elevation, double azimuth, double zoom) {
        this.request =  "<PTZData version=\"2.0\" xmlns=\"http://www.isapi.org/ver20/XMLSchema\">\n" +
                "<AbsoluteHigh>\n" +
                "<elevation>" + (int)elevation + "</elevation>\n" +
                "<azimuth>" + (int)azimuth + "</azimuth>\n" +
                "<absoluteZoom> "+(int)zoom+" </absoluteZoom>\n" +
                "</AbsoluteHigh>\n" +
                "</PTZData>";
    }

    private String request;

    public MoveRequest() {
    }

    public MoveRequest(String url) {
        this.url = url;
    }

    public MoveRequest(int elevation, double azimuth, int zoom){
        this.setRequest(elevation, (int)azimuth, zoom);
    }

    /**
     * відправлення PUT-запиту для повороту камери
     */
    public void start() {
        try {
            URL urladr = new URL(this.url);

            HttpURLConnection urlConnection = (HttpURLConnection) urladr.openConnection();


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
//            System.out.println("\nSending 'PUT' request to URL : " + url);
//            System.out.println("Response Code : " + responseCode);


        } catch (Exception e){
            e.printStackTrace();
        }


    }

}
