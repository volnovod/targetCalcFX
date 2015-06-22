package com.aim.hickvisonrequests;

import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by victor on 15.05.15.
 * клас, який змушує камеру HIKVISON рухатися/зупинятися
 */
public class ContinuousMove {

    private String url = "http://192.168.1.64/ISAPI/PTZCtrl/channels/1/continuous";
    private final String USER_AGENT = "Chrome/40.0.2214.111";

    /**
     * створення запиту для початку/закінчення поворотів
     * @param pan - 0- зупинка руху, -100..-1 - рух вліво із різною швидкістю, 100..1 - рух вправо
     * @param tilt - -100..-1 - рух вниз, 1..100 - рух вверх, 0 - зупинка
     */
    public void setRequest(int pan, int tilt) {
        this.request =  "<PTZData version=\"2.0\" xmlns=\"http://www.isapi.org/ver20/XMLSchema\">\n" +
                "<pan> " + pan + " </pan>\n" +
                "<tilt> "+ tilt + " </tilt>\n" +
                "<zoom> 0 </zoom>\n" +
                "</PTZData>";
    }

    private String request;

    public ContinuousMove() {
    }

    public ContinuousMove(String url) {
        this.url = url;
    }

    /**
     * посилання PUT-запиту
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
