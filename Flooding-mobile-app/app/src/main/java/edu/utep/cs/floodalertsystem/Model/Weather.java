package edu.utep.cs.floodalertsystem.Model;

/**
 * <h1> Weather </h1>
 *
 * Constructors, getters and setters for the weather.
 *
 *
 * @author  IRES: U.S.-Mexico Interdisciplinary Research Collaboration for
 * Smart Cities investigators and contributing participants.
 */


public class Weather {
    private WeatherData weatherData;
    private Main main;
    private Rain rain;
    private String date;

    public byte[] iconData;

    public Weather(){
        this.weatherData=new WeatherData();
        this.main= new Main();
        this.rain= new Rain();
        this.date="";
    }

    public WeatherData getWeatherData() {
        return weatherData;
    }

    public void setWeatherData(WeatherData weatherData) {
        this.weatherData = weatherData;
    }

    public Main getMain() {
        return main;
    }

    public void setMain(Main main) {
        this.main = main;
    }

    public Rain getRain() {
        return rain;
    }

    public void setRain(Rain rain) {
        this.rain = rain;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public  class WeatherData {
        private int id;
        private String main;
        private String description;
        private String icon;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getMain() {
            return main;
        }

        public void setMain(String main) {
            this.main = main;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getIcon() {
            return icon;
        }

        public void setIcon(String icon) {
            this.icon = icon;
        }
    }

    public class Main {
        private Double temp;
        private Double temp_min;
        private Double temp_max;
        private Double pressure;
        private Double humidity;

        public Double getTemp() {
            return temp;
        }

        public void setTemp(Double temp) {
            this.temp = temp;
        }

        public Double getTemp_min() {
            return temp_min;
        }

        public void setTemp_min(Double temp_min) {
            this.temp_min = temp_min;
        }

        public Double getTemp_max() {
            return temp_max;
        }

        public void setTemp_max(Double temp_max) {
            this.temp_max = temp_max;
        }

        public Double getPressure() {
            return pressure;
        }

        public void setPressure(Double pressure) {
            this.pressure = pressure;
        }

        public Double getHumidity() {
            return humidity;
        }

        public void setHumidity(Double humidity) {
            this.humidity = humidity;
        }
    }

    public class Rain {
        private Double volume;

        public Double getVolume() {
            return volume;
        }

        public void setVolume(Double volume) {
            this.volume = volume;
        }
    }
}
