package com.jfxbe.weatherwidget;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Worker;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextInputDialog;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebEvent;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import jdk.incubator.http.HttpClient;
import netscape.javascript.JSObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URI;
import java.util.Optional;
import java.util.Scanner;

import static jdk.incubator.http.HttpClient.newHttpClient;
import static jdk.incubator.http.HttpRequest.newBuilder;
import static jdk.incubator.http.HttpResponse.BodyHandler;
/**
 * The WeatherWidget application demonstrates the
 * use of the following interactions:
 * <pre>
 *  1) Communications from Java to JavaScript
 *  2) Communications from JavaScript to Java
 *  3) RESTful GET Web service end point
 *  4) Manipulate JSON Objects
 *  5) Handle HTML/JavaScript WebEvents
 *  6) Debugging using Firebug lite
 * </pre>
 *
 * <pre>
 *     The following are the steps to help
 *     demonstrate the above interactions:
 *
 * Step 1: The user enters a city state and country into the
 *         search text field. (See weather_template.html)
 * Step 2: After search button is pressed the JavaScript function
 *         findWeatherByLocation() is called. (See weather_template.html)
 * Step 3: An up call from JavaScript to Java is made to the method
 *         WeatherWidget.queryWeatherByLocationAndUnit(). (See this class)
 * Step 4: After querying the weather data the JSON data is passed to
 *         the Java method populateWeatherData(). This method will call
 *         the JavaScript function populateWeatherData(). (See this class)
 * Step 5: Populates the HTML page with the JavaScript function
 *         populateWeatherDate() (See weather_template.html)
 *
 * </pre>
 *
 * It is required to obtain a valid API key to query weather data.
 * To obtain an API key head over to Open Weather Map at
 * http://openweathermap.org
 *
 * @author cdea
 */
public class WeatherWidget extends Application {

    /** The main URL of the current weather REST end point. */
    public static final String WEATHER_URL = "http://api.openweathermap.org/data/2.5/weather";

    /** A local file containing a valid API key. */
    public static final String API_KEY_FILE = ".openweathermap-api-key";

    /** The API key to access weather and map data. */
    private static String API_KEY = null;

    /** The Weather display HTML page */
    public static final String WEATHER_DISPLAY_TEMPLATE_FILE = "weather_template.html";

    /** A WebView node to display HTML5 content */
    private WebView webView;

    /** A singleton http client to make http requests */
    private static HttpClient HTTP_CLIENT;

    @Override
    public void start(Stage stage) {
        stage.setTitle("Weather Widget");

        webView = new WebView();

        Scene scene = new Scene(webView, 300, 300);
        stage.setScene(scene);

        // obtain API key
        loadAPIKey();

        // Turns on Firebug lite for debugging
        // html,css, javascript
        // enableFirebug(webView);

        // The web view's web engine
        webView.getEngine()
                .getLoadWorker()
                .stateProperty()
                .addListener( (obs, oldValue, newValue) -> {
                    if (newValue == Worker.State.SUCCEEDED) {
                        // Let JavaScript make up calls to this (Java) class
                        JSObject jsobj = (JSObject)
                                webView.getEngine()
                                        .executeScript("window");
                        jsobj.setMember("WeatherWidget", this);
                        // default city's weather (a sunny place)
                        queryWeatherByLocationAndUnit("Miami%20FL,US", "c");
                    }
                });

        // Display a JavaFX dialog window explaining the error
        webView.getEngine().setOnAlert((WebEvent<String> t) -> {
            System.out.println("Alert Event - Message: " + t.getData());
            showErrorDialog(t.getData());
        });

        // Load HTML template to display weather
        webView.getEngine()
               .load(getClass()
                      .getResource(WEATHER_DISPLAY_TEMPLATE_FILE)
                      .toExternalForm());
        stage.show();
    }

    /**
     * If an API key file doesn't exist prompt the user to enter their key.
     * Once a valid key is saved into a file named .openweathermap-api-key
     * The application will use it to fetch weather data.
     */
    private void loadAPIKey() {
        // Load API key from local file
        File keyFile = new File(System.getProperty("user.home") + "/" + API_KEY_FILE);

        // Check for file's existence and read/write privileges.
        if (keyFile.exists() && keyFile.canRead() && keyFile.canWrite()) {
            try (FileInputStream fis = new FileInputStream(keyFile)){
                Optional<String> apiKey = Optional.ofNullable(streamToString(fis));
                apiKey.ifPresent(apiKeyStr -> API_KEY = apiKeyStr);
            } catch (Exception e) {
                Platform.exit();
                return;
            }
        } else {
            // If the API key does not exist display dialog box.
            TextInputDialog dialog = new TextInputDialog("");
            dialog.setTitle("Enter API Key");
            dialog.setHeaderText("Don't have a key? Go to: Open Weather Map http://openweathermap.org");
            dialog.setContentText("Please enter API Key:");

            Optional<String> result = dialog.showAndWait();

            result.ifPresent(key -> {
                // write to disk
                try (FileOutputStream fos = new FileOutputStream(keyFile)) {
                    String apiKey = result.get();
                    fos.write(apiKey.getBytes());
                    fos.flush();
                    API_KEY = apiKey;
                } catch (Exception e) {
                    e.printStackTrace();
                    Platform.exit();
                    return;
                }
            });
        }
    }

    /**
     * Display error dialog window.
     * @param errorMessage
     */
    private void showErrorDialog(String errorMessage) {
        Alert dialog = new Alert(Alert.AlertType.ERROR,  errorMessage);
        dialog.setTitle("Error Retrieving Weather Data");
        dialog.show();
    }

    @Override
    public void stop() throws Exception {
        // clean up resources here...
    }

    /**
     * Quick one liner that delimits on the end of file character and
     * returning the whole input stream as a String.
     * @param inputStream byte input stream.
     * @return String A string from an input stream.
     */
    public String streamToString(InputStream inputStream) {
        String text = new Scanner(inputStream, "UTF-8")
                .useDelimiter("\\Z")
                .next();
        return text;
    }

    /**
     * Returns a string containing the URL with parameters.
     * @param cityRegion The city state and country. State and
     *                   country is separated by a comma.
     * @param unitType Specify c for celsius and f for fahrenheit.
     * @return String A query string representing the web request.
     */
    private  String generateQueryString(String cityRegion, String unitType) {
        String units = "f".equalsIgnoreCase(unitType) ? "imperial": "metric";

        String queryString = WEATHER_URL +
                "?q=" + cityRegion +
                "&" + "units=" + units +
                "&" + "mode=json" +
                "&" + "appid=" + API_KEY;
        return queryString;
    }

    /**
     * This method is called from the JavaScript function
     * findWeatherByLocation(). Refer to the weather_template.html
     * file.
     * <pre>
     * -- Step 3 --
     * </pre>
     *
     * @param cityRegion The city, state and country.
     * @param unitType The temperature in celsius or fahrenheit.
     */
    public void queryWeatherByLocationAndUnit(String cityRegion,
                                              String unitType) {

        // build a weather request
        String queryStr  = generateQueryString(cityRegion, unitType);
        System.out.println("Request  (http2): " + queryStr);

        // Make a GET request to fetch weather data asynchronously
        // The sendAsync() method returns a CompletableFuture<HttpResponse<String>>
        HTTP_CLIENT.sendAsync( newBuilder(URI.create(queryStr))
                                .GET()
                                .build(),
                        BodyHandler.asString())
                   .whenCompleteAsync( (httpResp, throwable) -> {
                       if (throwable != null){
                           showErrorDialog(throwable.getMessage());
                           return;
                       }
                       String json  = httpResp.body();
                       populateWeatherData(json, unitType);
                       System.out.println("Response (http2): " + json);
                   });
    }

    /**
     * Invokes to the JavaScript function populateWeatherData() using the web engine.
     * <pre>
     *     -- Step 4 --
     *     From Java a call to invoke a JavaScript function is made by calling populateWeatherData().
     * </pre>
     *
     * @param json The JSON string to be evaluated (converted to a real JavaScript object).
     * @param unitType The symbol and unit for the temperature.
     */
    private void populateWeatherData(String json, String unitType) {
        Platform.runLater(() -> {
            // On the JavaFX Application Thread....
            webView.getEngine()
                    .executeScript("populateWeatherData(eval(" + json + "), " +
                            "'" + unitType + "' );");
        });
    }

    /**
     * Enables Firebug Lite for debugging a webEngine.
     * @param webView the webEngine for which debugging is to be enabled.
     */
    private static void enableFirebug(WebView webView) {
        WebEngine webEngine = webView.getEngine();
        webEngine.documentProperty()
                .addListener( (prop, oldDoc, newDoc) ->
                        webEngine.executeScript("if (!document.getElementById('FirebugLite')){"
                                + "E = document['createElement' + 'NS'] && document.documentElement.namespaceURI;"
                                + "E = E ? document['createElement' + 'NS'](E, 'script') : document['createElement']('script');"
                                + "E['setAttribute']('id', 'FirebugLite');"
                                + "E['setAttribute']('src', 'https://getfirebug.com/' + 'firebug-lite.js' + '#startOpened');"
                                + "E['setAttribute']('FirebugLite', '4');"
                                + "(document['getElementsByTagName']('head')[0] || "
                                + " document['getElementsByTagName']('body')[0]).appendChild(E);"
                                + "E = new Image;"
                                + "E['setAttribute']('src', 'https://getfirebug.com/' + '#startOpened');"
                                + "}")
                );
    }

    public static void main(String[] args){
        HTTP_CLIENT = newHttpClient();
        Application.launch(args);
    }
}


//        HttpRequest request = HttpRequest.newBuilder(URI.create("http://acme/create-account"))
//                .POST(fromString("param1=abc,param2=123")).build();
//
//        HTTP_CLIENT.sendAsync(request, BodyHandler.discard(null))
//                   .whenCompleteAsync( (httpResp, throwable) -> {
//                       if (throwable != null){
//                           showErrorDialog(throwable.getMessage());
//                           return;
//                       }
//                       System.out.println("Response (http2): Saving complete.");
//                   });

// https://docs.oracle.com/javase/9/migrate/toc.htm