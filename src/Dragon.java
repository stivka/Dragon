import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;


/**
 * Created by Stiv on 14/07/2017.
 */
public class Dragon {

    public int scaleThickness = 0;
    public int clawSharpness = 0;
    public int wingStrength = 0;
    public int fireBreath = -1;

    public ArrayList<ArrayList> combinations = new ArrayList<>();
    public ArrayList<ArrayList> permutations = new ArrayList<>();

    public boolean findingDragons = false;
    public boolean victorious = false;
    ArrayList<Integer> stats = new ArrayList<>();
    String gameId = "";
    String payload = "";

    String message = "";

    /*
    Set the counter to how many times you wish to fight the knights.
     */
    public void run() {
        int counter = 0;
        while (counter < 100) {
            counter++;
            getGame();
            checkWeather();
        }
    }

    public void getGame() {
        try {
            URL url = new URL("http://www.dragonsofmugloar.com/api/game");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Accept", "application/json");

            try {
                BufferedReader br = new BufferedReader(new InputStreamReader((connection.getInputStream())));
                String output;
                StringBuilder sb = new StringBuilder();

                String jsonString = "";

                while ((output = br.readLine()) != null) {
                    sb.append(output);
//                    System.out.println(sb);

                    String jsonInput = sb.toString();

                    Gson gson = new Gson();
                    Dragon thing = gson.fromJson(jsonInput, Dragon.class);;
                    if (thing.gameId != null) {
                        System.out.println("gameId is " + thing.gameId);
                    } else {
                        System.out.println("gameId element not present or value is null");
                    }
                }


            } catch (Exception e) {
                System.out.println(e);
            }

        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void checkWeather() {
        try {
            URL url = new URL("http://www.dragonsofmugloar.com/weather/api/report/{" + gameId + "}");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Accept", "application/json");

//            InputStreamReader isw = new InputStreamReader(connection.getInputStream());

            try {
                BufferedReader br = new BufferedReader(new InputStreamReader((connection.getInputStream())));
                StringBuilder sb = new StringBuilder();
                String output;
                while ((output = br.readLine()) != null) {
                    sb.append(output);

                    String jsonInput = sb.toString();

//                    Gson gson = new Gson();
//                    Dragon weather = gson.fromJson(jsonInput, Dragon.class);;
//                    if (weather.message != null) {
//                        System.out.println("weather is " + weather.message);
//                    } else {
//                        System.out.println("message element not present or value is null");
//                    }

//                    System.out.println(sb.toString());

                    if (sb.toString().contains("storm")) {
                        System.out.println("Stormy weather");
                        // do nothing?
                    }
                    else if (sb.toString().contains("rain")) {
                        System.out.println("Weather is rainy");
                        System.out.println("Dragon: scaleThickness: " + scaleThickness + " clawSharpness: "
                                + clawSharpness + " wingStrength: " + wingStrength + " fireBreath: " + fireBreath);
                        scaleThickness = 5;
                        clawSharpness = 10;
                        wingStrength = 5;
                        fireBreath = 0;

                        assignStats();
                        HttpPut();
                    }
                    else if (sb.toString().contains("normal")) {
                        System.out.println("Weather is normal");
                        System.out.println("Dragon: scaleThickness: " + scaleThickness + " clawSharpness: "
                                + clawSharpness + " wingStrength: " + wingStrength + " fireBreath: " + fireBreath);
                        scaleThickness = 4;
                        clawSharpness = 2;
                        wingStrength = 4;
                        fireBreath = 10;

                        assignStats();
                        HttpPut();
                    }
                    else if (sb.toString().contains("fog")) {
                        System.out.println("Weather is foggy");
                        //TODO need to see what happens when fog is given
                    }
                    else if (sb.toString().contains("dry")) {
                        System.out.println("Weather is dry");
                        System.out.println("Dragon: scaleThickness: " + scaleThickness + " clawSharpness: "
                                + clawSharpness + " wingStrength: " + wingStrength + " fireBreath: " + fireBreath);
                        scaleThickness = 10;
                        clawSharpness = 0;
                        wingStrength = 10;
                        fireBreath = 0;

                        assignStats();
                        HttpPut();
                    }
                }

            } catch (Exception e) {

            }

        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void findWinningDragons() {
        findingDragons = true;
        int counter = 0;
        int permutationCount = 0;
        int combinationCount = 0;

        getGame();

        while (counter <= (Math.pow(11, 4))) {
            fireBreath++;
            counter++;
            String payload = "";

            if (fireBreath == 11) {
                fireBreath = 0;
                wingStrength++;
            }
            if (wingStrength == 11) {
                wingStrength = 0;
                clawSharpness++;
            }
            if (clawSharpness == 11) {
                clawSharpness = 0;
                scaleThickness++;
            }

            if (scaleThickness + clawSharpness + wingStrength + fireBreath == 20) {
                //&& scaleThickness < 9 && clawSharpness < 9 && wingStrength < 9 && fireBreath < 9

                permutationCount++;
                assignStats();
                HttpPut();

                if (victorious) {
                    Collections.sort(stats);
                    if (!combinations.contains(stats)) {
                        combinations.add(stats);
                        System.out.println(scaleThickness + " " + clawSharpness + " " + wingStrength + " " + fireBreath);
                        combinationCount++;
                    }
                }
            }
            findingDragons = false;
            System.out.println("There we're " + combinationCount + " different combinations and "
                    + permutationCount + " different permutations.");
        }
    }

    /**
     * Creates json message for API put, by assigning the stat values to the string.
     */
    public void assignStats() {

        stats.add(scaleThickness);
        stats.add(clawSharpness);
        stats.add(wingStrength);
        stats.add(fireBreath);

        payload = "{\n" +
                "    \"dragon\": {\n" +
                "        \"scaleThickness\": " + scaleThickness + ",\n" +
                "        \"clawSharpness\": " + clawSharpness + ",\n" +
                "        \"wingStrength\": " + wingStrength + ",\n" +
                "        \"fireBreath\": " + fireBreath + "\n" +
                "    }\n" +
                "}";
    }

    public void HttpPut() {
        try {
            URL url = new URL("http://www.dragonsofmugloar.com/api/game/{" + gameId + "}/solution");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("PUT");
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Accept", "application/json");
            OutputStreamWriter osw = new OutputStreamWriter(connection.getOutputStream());
            osw.write(payload);
            osw.flush();

            try {
                BufferedReader br = new BufferedReader(new InputStreamReader((connection.getInputStream())));
                StringBuilder sb = new StringBuilder();
                String output;
                while ((output = br.readLine()) != null) {
                    sb.append(output);
                    if (findingDragons) {
                        if (!sb.toString().contains("Defeat")) {
                            victorious = true;
                            System.out.println(sb.toString());
                            System.out.println(stats);

                        } else {
                            victorious = false;
                        }
                    } else {
                        System.out.println(sb.toString());
                    }
                }

            } catch (Exception e) {
                System.out.println(e);
            }

        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public static void main(String[] args) {
        Dragon insta = new Dragon();
//        insta.findWinningDragons();
        insta.run();
    }
}
