package edu.oregonstate.cs361.battleship;

import com.google.gson.Gson;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import spark.Request;
import spark.Spark;
import spark.utils.IOUtils;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static spark.Spark.awaitInitialization;


/**
 * Created by michaelhilton on 1/26/17.
 */
class MainTest {

    @BeforeAll
    public static void beforeClass() {
        Main.main(null);
        awaitInitialization();
    }

    @AfterAll
    public static void afterClass() {
        Spark.stop();
    }

    @Test
    public void testGetModel() {
        TestResponse res = request("GET", "/model", null);
        assertEquals(200, res.status);
        //assertEquals("MODEL",res.body);
    }

    @Test
    public void testPlaceShip() {
        BattleshipModel test = new BattleshipModel();
        Gson gson = new Gson();
        String model = gson.toJson(test);
        TestResponse res = request("POST", "/placeShip/battleShip/1/1/horizontal", model);
        assertEquals(res.status, 200);
    }

    @Test
    public void testBadPlacement(){
        BattleshipModel test = new BattleshipModel();
        Gson gson = new Gson();
        String model = gson.toJson(test);
        TestResponse res = request("POST", "/placeShip/battleShip/11/11/horizontal", model);
        assertEquals(res,null);
    }




    @Test
    public void testFireAtInvalidRow() {
        //Fire attempt at (0,3)
        //Location is off board and should return error
        // creates an object to send to the server
        BattleshipModel test = new BattleshipModel();
        Gson gson = new Gson();
        String model = gson.toJson(test);

        TestResponse res = request( "POST", "/fire/0/3", model);
        assertEquals(res, null);
        //assertEquals( "Invalid fire location! That shot was off the board.", res.body);
    }

    @Test
    public void testFireAtInvalidCol() {
        //Fire attempt at (4,0)
        //Location is off board and should return error
        //creates a model to send to the server.
        BattleshipModel test = new BattleshipModel();
        Gson gson = new Gson();
        String model = gson.toJson(test);

        TestResponse res = request( "POST", "/fire/4/0", model);
        assertEquals( res, null);
        //assertEquals( "Invalid fire location! That shot was off the board.", res.body);
    }

    @Test
    public void testValidFire(){
        BattleshipModel test = new BattleshipModel();
        Gson gson = new Gson();
        String model = gson.toJson(test);

        TestResponse res = request( "POST", "/fire/4/1", model);
        assertEquals( 200, res.status);
    }

    private TestResponse request(String method, String path, String body) {
        try {
            URL url = new URL("http://localhost:4567" + path);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod(method);
            connection.setDoOutput(true);
            if(body != null) {
                connection.setDoInput(true);
                byte[] outputInBytes = body.getBytes("UTF-8");
                OutputStream os = connection.getOutputStream();
                os.write(outputInBytes);
            }
            connection.connect();
            body = IOUtils.toString(connection.getInputStream());
            return new TestResponse(connection.getResponseCode(), body);
        } catch (IOException e) {
            return null;
        }
    }

    private static class TestResponse {

        public final String body;
        public final int status;

        public TestResponse(int status, String body) {
            this.status = status;
            this.body = body;
        }

        public Map<String,String> json() {
            return new Gson().fromJson(body, HashMap.class);
        }
    }


}