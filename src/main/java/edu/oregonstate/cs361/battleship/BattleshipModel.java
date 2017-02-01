package edu.oregonstate.cs361.battleship;


import java.applet.Applet;
import java.util.*;





public class BattleshipModel {

    //Collection of ships that represent the game state
    private ship aircraftCarrier;
    private ship battleship;
    private ship cruiser;
    private ship destroyer;
    private ship submarine;
    private ship computer_aircraftCarrier;
    private ship computer_battleShip;
    private ship computer_cruiser;
    private ship computer_destroyer;
    private ship computer_submarine;

    private ArrayList<Point> AI_Points;

    //collection of hits and misses.
    private ArrayList<Point> playerHits;
    private ArrayList<Point> playerMisses;
    private ArrayList<Point> computerHits;
    private ArrayList<Point> computerMisses;

    //this will tell us if its a brand new game
    private boolean init;


    public BattleshipModel() {
        Point x = new Point(0, 0);
        init = true;
        aircraftCarrier = new ship("AircraftCarrier", 5, x, x);
        battleship = new ship("Battleship", 4, x, x);
        cruiser = new ship("Cruiser", 3, x, x);
        destroyer = new ship("Destroyer", 2, x, x);
        submarine = new ship("Submarine", 2, x, x);
        computer_aircraftCarrier = new ship("Computer_AircraftCarrier", 5, x, x);
        computer_battleShip = new ship("Computer_BattleShip", 4, x, x);
        computer_cruiser = new ship("Computer_Crusier", 3, x, x);
        computer_destroyer = new ship("Computer_Destroyer", 2, x, x);
        computer_submarine = new ship("Computer_Submarine", 2, x, x);
        playerHits = new ArrayList<Point>();
        playerMisses = new ArrayList<Point>();
        computerHits = new ArrayList<Point>();
        computerMisses = new ArrayList<Point>();
    }
/*
    DIDN't NEED and don't want to test
    // creates a new battle ship from ships and a collections of hits.
    // don't user unless you have too!
    public BattleshipModel(ship aircraftCarrier, ship battleShip, ship cruiser, ship destroyer, ship submarine,
                           ship computer_aircraftCarrier, ship computer_battleShip, ship computer_cruiser,
                           ship computer_destroyer, ship computer_submarine, ArrayList<Point> playerHits,
                           ArrayList<Point> playerMisses, ArrayList<Point> computerHits,
                           ArrayList<Point> computerMisses) {
        this.aircraftCarrier = new ship(aircraftCarrier);
        this.battleship = new ship(battleShip);
        this.cruiser = new ship(cruiser);
        this.destroyer = new ship(destroyer);
        this.submarine = new ship(submarine);

        this.computer_aircraftCarrier = new ship(computer_aircraftCarrier);
        this.computer_battleShip = new ship(computer_battleShip);
        this.computer_cruiser = new ship(computer_cruiser);
        this.computer_destroyer = new ship(computer_destroyer);
        this.computer_submarine = new ship(computer_submarine);
        this.playerHits = new ArrayList<Point>(playerHits);
        this.playerMisses = new ArrayList<Point>(playerMisses);
        this.computerHits = new ArrayList<Point>(computerHits);
        this.computerMisses = new ArrayList<Point>(computerMisses);
    }
*/
    //Copy constructor does a deep copy
    public BattleshipModel(BattleshipModel toCopy) {
        aircraftCarrier = toCopy.aircraftCarrier;
        battleship = new ship(toCopy.battleship);
        cruiser = new ship(toCopy.cruiser);
        destroyer = new ship(toCopy.destroyer);
        submarine = new ship(toCopy.submarine);

        computer_aircraftCarrier = new ship(toCopy.computer_aircraftCarrier);
        computer_battleShip = new ship(toCopy.computer_battleShip);
        computer_cruiser = new ship(toCopy.computer_cruiser);
        computer_destroyer = new ship(toCopy.computer_destroyer);
        computer_submarine = new ship(toCopy.computer_submarine);

        playerHits = new ArrayList<Point>(toCopy.playerHits);
        playerMisses = new ArrayList<Point>(toCopy.playerMisses);
        computerHits = new ArrayList<Point>(toCopy.computerHits);
        computerMisses = new ArrayList<Point>(toCopy.computerMisses);
    }

    //Displays the game state to the console
    /* FOR TESTING ONLY
    public void display(){
            //displays the user ships
            aircraftCarrier.display();
            battleship.display();
            cruiser.display();
            destroyer.display();
            submarine.display();

            // displays the computer ships
            computer_aircraftCarrier.display();
            computer_battleShip.display();
            computer_cruiser.display();
            computer_destroyer.display();
            computer_submarine.display();


            // displays the list of hits.
            System.out.println("playerHits: " + playerHits);
            System.out.println("PlayerMisses: " + playerMisses);
            System.out.println("Computer Hits: " + computerHits);
            System.out.println("Computer Misses " + computerMisses);
    }
*/
    // This function will place the ship on the game board where it needs to go
    // and do some error checking to make sure that the request follows
    // the rules of the game
    public String placeShip(String id, int across, int down, String direction) {
        // makes sure starting point isn't out of bounds
        boolean isVertical = direction.equals("vertical");
        if(across > 10 || down > 10){
            return "Failure: Placement out of bounds";
        }
        //checks name to make sure that the ship exists
        ship toAdd;
        int length;
        if(aircraftCarrier.checkName(id)) {
            toAdd = aircraftCarrier;
            length = 5;
        }
        else if(battleship.checkName(id)) {
            toAdd = battleship;
            length = 4;
        }
        else if (cruiser.checkName(id)) {
            toAdd = cruiser;
            length = 3;
        }
        else if(destroyer.checkName(id)) {
            toAdd = destroyer;
            length = 2;
        }
        else if(submarine.checkName(id)){
            toAdd = submarine;
            length = 2;
        }
        else
            return "Failure: Ship does not exist";

        //checks to make sure the ship doesn't get placed that it falls off the board
        if(isVertical){
            if((across + length - 1 ) > 10)
                return "Failure: Placement out of bounds";
        } else{//direction == horizontal
            if((down + length - 1 ) > 10)
                return "Failure: Placement out of bounds";
        }
        int endAcross;
        int endDown;
        Point start = new Point(across, down);
        Point end = new Point();
        if(isVertical){
            endAcross = across + length - 1;
            endDown = down;
            end.setPoint(endAcross, endDown);
        } else { //direction == horizontal
            endAcross = across;
            endDown = down + length - 1;
            end.setPoint(endAcross, endDown);
        }

        ship toCheck = new ship(id, length, start, end);
        boolean overlaps = checkShipOverlap(toCheck);
        if(overlaps){
            return "Failure: Placement overlaps another ship";
        }
        toAdd.setStart(across, down);
        toAdd.setEnd(endAcross, endDown);
        return "Success: Placed " + id + " at " + across + ", " + down;
    }

    // returns false if it doesn't over lap any other ships returns true if it does
    private boolean checkShipOverlap(ship toCheck) {
        return aircraftCarrier.shipOverlap(toCheck) || battleship.shipOverlap(toCheck) || cruiser.shipOverlap(toCheck) || destroyer.shipOverlap(toCheck) || submarine.shipOverlap(toCheck);
    }

    public void placeComputerShips(){
        ship[] computerShips = {computer_aircraftCarrier,computer_battleShip,computer_cruiser,computer_destroyer,computer_submarine};
        AI_Points = new ArrayList<Point>();
        int counter = 0;
        while(counter < computerShips.length){

            ship currentShip = computerShips[counter];

            boolean test = false;

            while(!test) {

                //get a random orientation
                int randomOrientation = (int) (Math.random() * 2);
                String orientation = "";
                if (randomOrientation == 0)
                    orientation = "horizontal";
                else
                    orientation = "vertical";


                int tempAcross = (int) (Math.random() * 10 + 1);
                int tempDown = (int) (Math.random() * 10 + 1);


                if (isValidMove(currentShip.getLength(), orientation, tempAcross, tempDown)){
                    test=true;

                    //add points to computer points array
                    for(int i = 0; i < currentShip.getLength(); i++){
                        if(orientation == "horizontal") {
                            Point temp = new Point(tempAcross+i, tempDown);
                            AI_Points.add(temp);
                        }
                        else{
                            Point temp = new Point(tempAcross, tempDown+i);
                            AI_Points.add(temp);
                        }

                    }

                    System.out.println("orientation:" + orientation);
                    System.out.println("Across:" + tempAcross);
                    System.out.println("Down:" + tempDown);
                    System.out.println(currentShip.getName());
                    System.out.println("Length: " + currentShip.getLength());
                    System.out.println(AI_Points.toString());
                }

            }

            counter++;

        }

    }
    private boolean isValidMove(int length, String orientation, int across, int down){
        if(orientation == "horizontal"){
            if((across + length) > 10)
                return false;
        }
        else {
            if ((down + length) > 10)
                return false;
        }

        ArrayList<Point> temp_Points = new ArrayList<Point>();

        //add points to computer points array
        for(int i = 0; i < length; i++){
            if(orientation == "horizontal") {
                Point temp = new Point(across+i, down);
                temp_Points.add(temp);
            }
            else{
                Point temp = new Point(across, down+i);
                temp_Points.add(temp);
            }

        }

        for(int i = 0; i < temp_Points.size(); i++){
            Point checkPoint = temp_Points.get(i);

            int counter = 0;
            while(counter < AI_Points.size()-1){

                System.out.println("Comparing myPoint(" + checkPoint.getAcross()+ ", " + checkPoint.getDown() +") " +
                        "and AI POINT (" + AI_Points.get(counter).getAcross() + ", " + AI_Points.get(counter).getDown()+ ")");

                if((checkPoint.getAcross() == (AI_Points.get(counter).getAcross())) && (checkPoint.getDown() == (AI_Points.get(counter).getDown()))){
                    System.out.println("Overlapping points");
                    return false;
                }
                counter++;
            }

        }

        return true;
    }
}