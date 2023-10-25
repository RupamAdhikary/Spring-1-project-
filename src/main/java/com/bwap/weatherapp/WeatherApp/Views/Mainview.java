package com.bwap.weatherapp.WeatherApp.Views;

import com.bwap.weatherapp.WeatherApp.controller.WeatherService;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.ClassResource;
import com.vaadin.server.ExternalResource;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;

@SpringUI(path="")
public class Mainview  extends UI {
    @Autowired
    private WeatherService weatherService;

    private VerticalLayout mainLyout;
    private NativeSelect<String> unitSelect;
    private TextField cityTextFeild;
    private Button searchButton;
    private HorizontalLayout dashboard;
    private Label location;
    private Label currentTemp;
    private HorizontalLayout mainDescriptionLayout;
    private Label weatherDescription;
    private Label Maxweather;
    private Label Minweather;
    private Label Humidity;
    private Label Pressure;
    private Label Wind;
    private Label FeelsLike;
    private Image iconImg;



    @Override
    protected void init(VaadinRequest vaadinRequest) {
        mainlayout();
        setHeader();
        setlogo();
        setForm();
        dashoardTitle();
        dashoardDetails();
        searchButton.addClickListener(ClickEvent -> {
            if (!cityTextFeild.getValue().equals("")) {
                try {
                    updateUI();
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            } else {
                Notification.show("Please Enter The City Name");
            }
        });


    }

    private void mainlayout() {
        iconImg=new Image();
        mainLyout = new VerticalLayout();
        mainLyout.setWidth("100%");
        mainLyout.setSpacing(true);
        mainLyout.setMargin(true);
        mainLyout.setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);

        setContent(mainLyout);

    }

    private void setHeader(){
        HorizontalLayout header = new HorizontalLayout();
        header.setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);
        Label title = new Label("Weather App by Rupam_Adhikary");

        header.addComponent(title);

        mainLyout.addComponent(header);
    }

    private void setlogo(){
        HorizontalLayout logo = new HorizontalLayout();
        logo.setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);
        Image img1 = new Image(null,new ClassResource("/static/WeatherApplogo.jpg"));
        logo.setHeight("240px");
        logo.setWidth("240px");

        logo.addComponent(img1);
        mainLyout.addComponent(logo);
    }

    private void setForm(){
        HorizontalLayout formLayout = new HorizontalLayout();
        formLayout.setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);
        formLayout.setSpacing(true);
        formLayout.setMargin(true);

        unitSelect = new NativeSelect<>();
        ArrayList<String> itmes = new ArrayList<>();
        itmes.add("C");
        itmes.add("F");


        unitSelect.setItems(itmes);
        unitSelect.setValue(itmes.get(0));
        formLayout.addComponent(unitSelect);

        cityTextFeild = new TextField();
        cityTextFeild.setWidth("88%");
        formLayout.addComponent(cityTextFeild);

        searchButton=new Button();
        searchButton.setIcon(VaadinIcons.SEARCH);
        formLayout.addComponent(searchButton);






        mainLyout.addComponent(formLayout);




    }
    private void dashoardTitle(){
        dashboard=new HorizontalLayout();
        dashboard.setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);

        location=new Label("Currently in Kalkata");
        location.addStyleName(ValoTheme.LABEL_H2);
        location.addStyleName(ValoTheme.LABEL_LIGHT);


        currentTemp=new Label("10C");
        currentTemp.setStyleName(ValoTheme.LABEL_BOLD);
        currentTemp.setStyleName(ValoTheme.LABEL_H1);





        dashboard.addComponents(location,iconImg,currentTemp);



    }
    private void dashoardDetails(){
        mainDescriptionLayout= new HorizontalLayout();
        mainDescriptionLayout.setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);
        VerticalLayout descriptionlayout =new VerticalLayout();
        descriptionlayout.setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);
        weatherDescription=new Label("DescriptionL:Clear Skies");
        weatherDescription.setStyleName(ValoTheme.LABEL_SUCCESS);
        descriptionlayout.addComponent(weatherDescription);
        Minweather=new Label("Min:53");
        descriptionlayout.addComponent(Minweather);
        Maxweather=new Label("Min:53");
        descriptionlayout.addComponent(Maxweather);

        VerticalLayout pressureLayout=new VerticalLayout();
        pressureLayout.setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);
        Pressure=new Label("Pressure:231Pa");
        pressureLayout.addComponent(Pressure);

        Humidity=new Label("Humidity: 23");
        pressureLayout.addComponent(Humidity);

        Wind=new Label("Wind:2");
        pressureLayout.addComponent(Wind);

        FeelsLike=new Label("FeelsLike:231Pa");
        pressureLayout.addComponent(FeelsLike);


        mainDescriptionLayout.addComponents(descriptionlayout,pressureLayout);



    }
    private void updateUI() throws JSONException {
        String city=cityTextFeild.getValue();
        String defaultUnit;
        weatherService.setCityName(city);

        if(unitSelect.getValue().equals("F")){
            weatherService.setUnit("imperials");
            unitSelect.setValue("F");
            defaultUnit="\u00b0"+"F";
        }else {
            weatherService.setUnit("metric");
            defaultUnit="\u00b0"+"C";
            unitSelect.setValue("C");
        }

        location.setValue("Currently in "+city);
        JSONObject mainOject= weatherService.returnMain();
        int temp =mainOject.getInt("temp");
        currentTemp.setValue(temp+defaultUnit);

        String iconCode=null;
        String weatherDescriptionNew=null;
        JSONArray jsonArray= weatherService.returnWeatherArray();
        for (int i=0;i<jsonArray.length();i++){
            JSONObject weatherObj=jsonArray.getJSONObject(i);
            iconCode=weatherObj.getString("icon");
            weatherDescriptionNew=weatherObj.getString("description");
            System.out.println(weatherDescriptionNew);

        }
        iconImg.setSource(new ExternalResource("https://openweathermap.org/img/wn/"+iconCode+"@2x.png"));

        weatherDescription.setValue("Description:" +weatherDescriptionNew);
        Minweather.setValue("Min Temp:"+weatherService.returnMain().getInt("temp_min")+unitSelect.getValue());
        Pressure.setValue("Pressure:"+weatherService.returnMain().getInt("pressure"));
        Humidity.setValue("Humidity:"+weatherService.returnMain().getInt("humidity"));
        FeelsLike.setValue("FeelsLike:"+weatherService.returnMain().getInt("feels_like"));
        //Wind.setValue("Wind:"+weatherService.returnMain().getInt("speed"));


        mainLyout.addComponents(dashboard,mainDescriptionLayout);

    }







}
