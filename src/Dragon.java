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


/**
 * Created by Stiv on 14/07/2017.
 */
public class Dragon {

    public int scaleThickness = 0;
    public int clawSharpness = 0;
    public int wingStrength = 0;
    public int fireBreath = 0;

    ArrayList<Integer> stats = new ArrayList<>();
    String gameId = "";
    String payload = "";

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
                    System.out.println(sb.toString());

                    if (sb.toString().contains("storm")) {
                        // do nothing?
                    }
                    else if (sb.toString().contains("rain")) {
                        scaleThickness = 5;
                        clawSharpness = 10;
                        wingStrength = 5;
                        fireBreath = 0;

                        assignStats();
                        HttpPut();
                    }
                    else if (sb.toString().contains("normal")) {
                        scaleThickness = 4;
                        clawSharpness = 2;
                        wingStrength = 4;
                        fireBreath = 10;

                        assignStats();
                        HttpPut();
                    }
                    else if (sb.toString().contains("fog")) {

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

        int counter = 0;
        int solutionCount = 0;

        while (counter <= 14641) {
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

                solutionCount++;
                assignStats();
                HttpPut();

//                Collections.sort(stats);
//                if (!arrayList.contains(stats)) {
//                    arrayList.add(stats);
//                    System.out.println(scaleThickness + " " + clawSharpness + " " + wingStrength + " " + fireBreath);
//                    solutionCount++;
                }
            }
        System.out.println(solutionCount);
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
                    if (!sb.toString().contains("Defeat")) {
                        System.out.println(sb.toString());
                        System.out.println(stats);
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

        int counter = 0;
        while (counter < 1) {
            counter++;
            insta.getGame();
            insta.checkWeather();
        }
    }
}
