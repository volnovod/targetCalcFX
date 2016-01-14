package com.aim.test;

import com.aim.SpringLoader.SpringFXMLLoader;
import com.aim.aimcalculator.AimCalculatorImpl;
import com.aim.cams.Cams;
import com.aim.hickvisonrequests.*;
import com.aim.model.Aim;
import com.aim.view.Player;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.*;
import java.util.List;
import java.util.Timer;

/**
 * Created by victor on 08.06.15.
 */
public class ShooterController extends Pane {

    private Player player;

    public Stage getShooterStage() {
        return shooterStage;
    }

    public void setShooterStage(Stage shooterStage) {
        this.shooterStage = shooterStage;
    }

    private Stage shooterStage;

    @FXML
    private Pane videoPane;

    @FXML
    private StackPane stackPane;

    @FXML
    private TableView<Aim> tableView;

    @FXML
    private TableColumn<Aim, Integer> aimId;

    @FXML
    private TableColumn<Aim, Double> latitude;

    @FXML
    private TableColumn<Aim, Double> longitude;

    @FXML
    private TableColumn<Aim, Double> latitudeCK42;

    @FXML
    private TableColumn<Aim, Double> longitudeCK42;

    @FXML
    private TableColumn<Aim, Double> distance;

    @FXML
    private TableColumn<Aim, Double> path;

    @FXML
    private Button rightButton;

    @FXML
    private Button leftButton;

    @FXML
    private Button upButton;

    @FXML
    private Button downButton;

    @FXML
    private Label longitudeLabel;

    @FXML
    private Label latitudeLabel;

    @FXML
    private Line verticalLine;

    @FXML
    private Line horizontalLine;

    @FXML
    private Button cancelButton;

    @FXML
    private Button getButton;

    @FXML
    private TextField distanceField;

    @FXML
    private TextField longitudeField;

    @FXML
    private TextField latitudeField;

    @FXML
    private TextField heightField;

    @FXML
    private TextField azimuthField;

    @FXML
    private TextField elevationField;

    @FXML
    private Label elevationLabel;

    @FXML
    private Label latTexLab;

    @FXML
    private Label longTextLab;

    @FXML
    private Label distanceTextLab;

    @FXML
    private Label distanceLabel;

    @FXML
    private Button zoomadd;

    @FXML
    private Button zoomsub;

    @FXML
    private  Button modeButton;

    @FXML
    private Button firstButton;

    @FXML
    private Button secondButton;

    @FXML
    private Button thirdButton;

    @FXML
    private Button fourthButton;

    @FXML
    private Button fifthButton;

    @FXML
    private Button sixthButton;

    @FXML
    private ComboBox choiseBox;


    @FXML
    private Pane compass;

    @FXML
    private Text compassValue;

    @FXML
    private Pane compassPane;



    private Chooser first = new Chooser();
    private Chooser second = new Chooser();
    private Chooser third = new Chooser();
    private Chooser fourth = new Chooser();
    private Chooser fifth = new Chooser();
    private Chooser sixth = new Chooser();



    public int getModeState() {
        return modeState;
    }

    public void setModeState(int modeState) {
        this.modeState = modeState;
    }

    private int modeState=1;



    private List<Aim> aimList;
    private SpringFXMLLoader springFXMLLoader;
    private Request status;
    private MoveRequest moveRequest;
    private ContinuousMove continuousMove;
    private ZoomRequest zoomRequest;
    private HomePositionRequest homePositionRequest;
    private Aim lastAim;
    private ObservableList<Aim> data;
    private NumberFormat formatter;


    @FXML
    public void initialize(){


        springFXMLLoader = new SpringFXMLLoader();
        aimList = springFXMLLoader.getService().getAllAim();
        Iterator<Aim> iterator = aimList.iterator();
        data = FXCollections.observableArrayList();
        while (iterator.hasNext()){
            Aim aim = iterator.next();
            data.add(aim);
        }
        Collections.sort(data, (Aim a1, Aim a2) -> a1.getId().compareTo(a2.getId()));
        aimId.setCellValueFactory(new PropertyValueFactory<>("id"));
        latitude.setCellValueFactory(new PropertyValueFactory<>("latitude"));
        longitude.setCellValueFactory(new PropertyValueFactory<>("longitude"));
        latitudeCK42.setCellValueFactory(new PropertyValueFactory<>("latitudeck42"));
        longitudeCK42.setCellValueFactory(new PropertyValueFactory<>("longitudeck42"));
        distance.setCellValueFactory(new PropertyValueFactory<>("distance"));
        path.setCellValueFactory(new PropertyValueFactory<>("path"));
        tableView.setItems(data);
        player = new Player();
        player.setPlayerHolder(videoPane);
        player.initialize();
        this.setFirstMode();
        this.choiseBox.getSelectionModel().select(0);

        if(player.getCams() == Cams.HICKVISION){
            goToHomePosition();
        }
        if(data.size() != 0){
            lastAim = data.get(data.size()-1);
            displayLastAim(formatter.format(lastAim.getLatitudeck42()), formatter.format(lastAim.getLongitudeck42()), String.valueOf(lastAim.getDistance()));
        } else {
            displayLastAim("", "", "Цілі відсутні");
        }
        startBackgroundAnalyze();



    }
    javax.swing.Timer timer = new javax.swing.Timer(40, new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            compass.setRotate(compass.getRotate()+1);
            compassValue.setText(String.valueOf((int)(compass.getRotate())));
            if (compass.getRotate()==360){

                compass.setRotate(0);
            }

        }
    });


    @FXML
    public void play(){
        player.initialize();
    }

    @FXML
    public void stop(){
        player.stop();
    }

    @FXML
    public void changeScene() throws IOException {
//        Stage stage;
//        Parent root;
//
//        if (choiseBox.getValue().equals("Наводчик")){
////            player.stop();
//            System.out.println("--------------------------------------");
//            FXMLLoader loader = new  FXMLLoader(getClass().getResource("/fxml/test.fxml"));
//            root = loader.load();
//            AimerController aimerController = loader.getController();
////            stage = (Stage) choiseBox.getScene().getWindow();
//            stage = new Stage();
//            aimerController.setShoterStage(stage);
//            aimerController.initialize(this.player);
////            aimerController.setPlayer(player);
//            stage.setScene(new Scene(root));
//            this.shooterStage.close();
//            stage.show();
//            return;
//        }
    }





    public ObservableList<Aim> sortData(ObservableList<Aim> data){
        Collections.sort(data, (Aim aim1, Aim aim2) -> aim1.getId().compareTo(aim2.getId()));
        return data;
    }

    public void setTableData(ObservableList<Aim> data){
        this.tableView.setItems(data);
    }

    public void displayLastAim(String latitudeCK42, String longitudeCK42, String distance){
        latitudeLabel.setText(latitudeCK42);
        longitudeLabel.setText(longitudeCK42);
        distanceLabel.setText(distance);
    }

    @FXML
    protected void modeChooser(){
        if (getModeState()==1){
            this.setSecondMode();
            return;
        }
        if (getModeState()==2){
            this.setFirstMode();
            return;
        }
    }

    public void setFirstMode(){
        setModeState(1);

        this.modeButton.setText("Mode 1");
        setDefaultButton(firstButton, first);
        this.firstButton.setText("F11");
        setDefaultButton(secondButton, second);
        this.secondButton.setText("F12");
        setDefaultButton(thirdButton, third);
        this.thirdButton.setText("Компас");
        setDefaultButton(fourthButton, fourth);
        this.fourthButton.setText("F14");
        setDefaultButton(fifthButton, fifth);
        this.fifthButton.setText("F15");
        setDefaultButton(sixthButton, sixth);
        this.sixthButton.setText("F16");
    }

    public void setDefaultButton(Button button, Chooser chooser){
        button.setStyle("-fx-base: rgba(226, 232, 227, 1);");
        chooser.setValue(false);
    }

    public void setSecondMode(){
        setModeState(2);

        this.modeButton.setText("Mode 2");
        this.firstButton.setText("F21");
        setDefaultButton(firstButton, first);
        this.secondButton.setText("F22");
        setDefaultButton(secondButton, second);
        this.thirdButton.setText("F23");
        setDefaultButton(thirdButton, third);
        this.fourthButton.setText("F24");
        setDefaultButton(fourthButton, fourth);
        this.fifthButton.setText("F25");
        setDefaultButton(fifthButton, fifth);
        this.sixthButton.setText("F26");
        setDefaultButton(sixthButton, sixth);
    }

    @FXML
    protected void firstAction(){
        buttonColorChooser(firstButton, first);
    }
    @FXML
    protected void secondAction(){
        buttonColorChooser(secondButton, second);
    }
    @FXML
    protected void thirdAction(){
        buttonColorChooser(thirdButton, third);
        if (getModeState()==1){
            if (third.isValue()){
                compassPane.setVisible(true);
                timer.start();
            } else {
                compassPane.setVisible(false);
                timer.stop();
            }
        }

    }
    @FXML
    protected void fourthAction(){
        buttonColorChooser(fourthButton, fourth);
    }
    @FXML
    protected void fifthAction(){
        buttonColorChooser(fifthButton, fifth);
    }
    @FXML
    protected void sixthAction(){
        buttonColorChooser(sixthButton, sixth);
    }

    public void buttonColorChooser(Button button, Chooser change){
        if(!change.isValue()){
            button.setStyle("-fx-base: rgba(144, 231, 153, 1);");
            change.setValue(true);

            return;
        }
        if (change.isValue()){
            button.setStyle("-fx-base: rgba(226, 232, 227, 1);");
            change.setValue(false);
            return;
        }
    }

    @FXML
    protected void stepToRight(){
        if(ifHickvision()) {
            new Thread() {
                public void run() {
                    Platform.runLater(() -> {
                        status.start();
                        moveRequest.setRequest(Double.parseDouble(status.getElevation()) * 10, ((Double.parseDouble(status.getAzimuth()) + 1) * 10), ((Double.parseDouble(status.getZoom()))*10));
                        moveRequest.start();
                    });
                }
            }.start();
        }
    }

    @FXML
    protected void moveToRight(){
        if(ifHickvision()) {
            new Thread() {
                public void run() {
                    Platform.runLater(() -> {
                        continuousMove.setRequest(50, 0, 0);
                        continuousMove.start();
                    });
                }
            }.start();
        }
    }

    @FXML
    protected void stopMove(){
        if (ifHickvision()) {
            new Thread() {
                public void run() {
                    Platform.runLater(() -> {

                        continuousMove.setRequest(0, 0, 0);
                        continuousMove.start();
                    });
                }
            }.start();
        }
    }

    @FXML
    protected void stepToLeft(){
        if (ifHickvision()) {
            new Thread() {
                public void run() {
                    Platform.runLater(() -> {
                        status.start();
                        moveRequest.setRequest(Double.parseDouble(status.getElevation()) * 10, ((Double.parseDouble(status.getAzimuth()) - 1) * 10), ((Double.parseDouble(status.getZoom()))*10));
                        moveRequest.start();
                    });
                }
            }.start();
        }
    }

    @FXML
    protected void moveToLeft(){
        if (ifHickvision()) {
            new Thread() {
                public void run() {
                    Platform.runLater(() -> {
                        continuousMove.setRequest(-50, 0, 0);
                        continuousMove.start();
                    });
                }
            }.start();
        }
    }

    @FXML
    protected void stepToDown(){
        if (ifHickvision()) {
            new Thread() {
                public void run() {
                    Platform.runLater(() -> {
                        status.start();
                        moveRequest.setRequest(((Double.parseDouble(status.getElevation())) + 1) * 10, (Double.parseDouble(status.getAzimuth())) * 10, ((Double.parseDouble(status.getZoom()))*10));
                        moveRequest.start();
                    });
                }
            }.start();
        }
    }

    @FXML
    protected void moveToDown(){
        if (ifHickvision()) {
            new Thread() {
                public void run() {
                    Platform.runLater(() -> {
                        continuousMove.setRequest(0, -25, 0);
                        continuousMove.start();
                    });
                }
            }.start();
        }
    }

    @FXML
    protected void stepToUp(){
        if (ifHickvision()) {
            new Thread() {
                public void run() {
                    Platform.runLater(() -> {
                        status.start();
                        moveRequest.setRequest(((Double.parseDouble(status.getElevation())) - 1) * 10, (Double.parseDouble(status.getAzimuth())) * 10, (Double.parseDouble(status.getZoom()))*10);
                        moveRequest.start();
                    });
                }
            }.start();
        }
    }

    @FXML
    protected void moveToUp(){
        if (ifHickvision()) {
            new Thread() {
                public void run() {
                    Platform.runLater(() -> {
                        continuousMove.setRequest(0, 25, 0);
                        continuousMove.start();
                    });
                }
            }.start();
        }
    }

    @FXML
    protected void startzoomadd(){
        if (ifHickvision()){
            new Thread(){
                public void run(){
                    Platform.runLater(() -> {
                        zoomRequest.setRequest(20);
                        zoomRequest.start();
                    });
                }
            }.start();
        }
    }

    @FXML
    protected void stopzoomadd(){
        if (ifHickvision()){
            new Thread(){
                public void run(){
                    Platform.runLater(() -> {
                        zoomRequest.setRequest(0);
                        zoomRequest.start();
                    });
                }
            }.start();
        }
    }

    @FXML
    protected void startzoomsub(){
        if (ifHickvision()){
            new Thread(){
                public void run(){
                    Platform.runLater(() -> {
                        zoomRequest.setRequest(-20);
                        zoomRequest.start();
                    });
                }
            }.start();
        }
    }

    @FXML
    protected void stopzoomsub(){
        if (ifHickvision()){
            new Thread(){
                public void run(){
                    Platform.runLater(() -> {
                        zoomRequest.setRequest(0);
                        zoomRequest.start();
                    });
                }
            }.start();
        }
    }

    @FXML
    protected void saveAim(){
        double latitude = Double.parseDouble(latitudeField.getText());
        double longtitude = Double.parseDouble(longitudeField.getText());
        double azimuth = Double.parseDouble(azimuthField.getText());
        double distance = Double.parseDouble(distanceField.getText());
        double height = Double.parseDouble(heightField.getText());
        latitudeField.setText("");
        longitudeField.setText("");
        azimuthField.setText("");
        elevationField.setText("");
        distanceField.setText("");



        AimCalculatorImpl calculator = new AimCalculatorImpl();
        calculator.setHeight(height);
        calculator.calcCoordinate(latitude, longtitude, azimuth, distance);
        Aim aim = calculator.getAim();
        aim.setDistance(distance);


        BufferedImage bi = player.getMediaPlayer().getSnapshot();
        try {
            if(lastAim != null){
                File image = new File("img/" + (lastAim.getId()+1) + ".png");
                ImageIO.write(bi, "png", image);
                aim.setPath(image.getPath());
            } else {
                File image = new File("img/0.png");
                ImageIO.write(bi, "png", image);
                aim.setPath(image.getPath());
            }

        } catch (IOException ex) {

            ex.printStackTrace();
        }


        springFXMLLoader.getService().createAim(aim);
        data = FXCollections.observableArrayList(springFXMLLoader.getService().getAllAim());
        data = sortData(data);
        setTableData(data);
        if( data.size() !=0){
            lastAim = data.get(data.size()-1);
            displayLastAim(formatter.format(lastAim.getLatitudeck42()), formatter.format(lastAim.getLongitudeck42()), String.valueOf(lastAim.getDistance()) );
        }

    }

    public ShooterController() {
        this.formatter = new DecimalFormat("#0.000000");
        this.status = new Request();
        this.moveRequest = new MoveRequest();
        this.continuousMove = new ContinuousMove();
        this.homePositionRequest = new HomePositionRequest();
        this.zoomRequest  =new ZoomRequest();
        this.moveRequest.setZoom(this.zoomRequest);
        this.continuousMove.setZoom(this.zoomRequest);
    }


    public void startBackgroundAnalyze(){
        new Timer().schedule(
                new TimerTask() {

                    @Override
                    public void run() {
                        controlBackgroundFon();
                    }
                }, 0, 3000);
    }

    @FXML
    public void setLineColor(int color){
        if(color < 9097402){
            verticalLine.setStroke(Color.WHITE);
            horizontalLine.setStroke(Color.WHITE);
            longitudeLabel.setTextFill(Color.WHITE);
            latitudeLabel.setTextFill(Color.WHITE);
            distanceLabel.setTextFill(Color.WHITE);
            latTexLab.setTextFill(Color.WHITE);
            longTextLab.setTextFill(Color.WHITE);
            distanceTextLab.setTextFill(Color.WHITE);
        }else {
            verticalLine.setStroke(Color.BLACK);
            horizontalLine.setStroke(Color.BLACK);
            longitudeLabel.setTextFill(Color.BLACK);
            latitudeLabel.setTextFill(Color.BLACK);
            distanceLabel.setTextFill(Color.BLACK);
            latTexLab.setTextFill(Color.BLACK);
            longTextLab.setTextFill(Color.BLACK);
            distanceTextLab.setTextFill(Color.BLACK);
        }
    }
    Integer count = 0;

    public void snapshot(){
        BufferedImage bi = player.getMediaPlayer().getSnapshot();
        try {
            if(bi != null){
                File image = new File("img/" + (count++) + ".png");
                ImageIO.write(bi, "png", image);
                System.out.println(image.getPath());
            }

        } catch (IOException ex) {

            ex.printStackTrace();
        }
    }

    /**
     * метод аналізує центральну частину екрану на яскравість
     * і змінює в залежності від яскравості колір маркеру цілі
     */
    public void controlBackgroundFon(){

        BufferedImage image = player.getMediaPlayer().getSnapshot();
        if(image!=null){
            int w = 60;
            int h = 60;

            int[] dataBuff = image.getRGB( 610, 330, w, h, null, 0, w);
            java.awt.Color color = new java.awt.Color(dataBuff[100]);
            int red = color.getRed();
            int green = color.getGreen();
            int blue = color.getBlue();
            String hexRed = Integer.toHexString(red);
            String hexGreen = Integer.toHexString(green);
            String hexBlue = Integer.toHexString(blue);
            String hexColor = hexRed + hexGreen + hexBlue;
            int rescolor = Integer.parseInt(hexColor,16);
            setLineColor(rescolor);
        }

    }

    public void goToHomePosition(){
        new Thread(){
            public void run(){
                Platform.runLater(homePositionRequest::start);
            }
        }.start();
    }

    public void getInfo(){

        if (ifHickvision()){
            new Thread(){
                public void run(){
                    Platform.runLater(() -> {
                        if(moveRequest.isMoveAround()){
                            status.start();
                            double angle1 = Double.parseDouble(status.getAzimuth()) - 180;
                            String res = "";
                            res+= angle1;
                            azimuthField.setText(res);
                            double angle2 = 180 - Double.parseDouble(status.getElevation());
                            String res2 = "";
                            res2+=angle2;
                            elevationField.setText(res2);
                        } else {
                            status.start();
                            azimuthField.setText(status.getAzimuth());
                            elevationField.setText(status.getElevation());
                        }
                    });
                }
            }.start();
        }
    }

    public void cancel(){
        this.longitudeField.setText("");
        this.latitudeField.setText("");
        this.azimuthField.setText("");
        this.distanceField.setText("");
        this.heightField.setText("");
        this.elevationField.setText("");
    }

    public boolean ifHickvision(){
        return player.getCams() == Cams.HICKVISION;
    }


    public ZoomRequest getZoomRequest() {
        return zoomRequest;
    }

    public void setZoomRequest(ZoomRequest zoomRequest) {
        this.zoomRequest = zoomRequest;
    }
}
