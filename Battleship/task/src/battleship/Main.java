package battleship;

import java.util.Arrays;
import java.util.Scanner;


class Field {

    char[][] field;
    char[][] foggedField;

    public Field() {
        field = new char[11][11];
        foggedField = new char[11][11];
        fillDefault(field);
        fillDefault(foggedField);
    }

    public void showField(char[][] field) {
        for (int i = 0; i < 10; i++) {
            System.out.print(field[0][i] + " ");
        }
        System.out.println("10");
        for (int i = 1; i < 11; i++) {
            for (int j = 0; j < 11; j++) {
                System.out.print(field[i][j] + " ");
            }
            System.out.println();
        }
    }

    public void setShip(Coordinate firstCoordinate, Coordinate secondCoordinate, ShipType shipType) {
        if (firstCoordinate.x != secondCoordinate.x
                && firstCoordinate.y != secondCoordinate.y) {
            throw new IllegalArgumentException("Error! Wrong ship location! Try again:\n\n> ");
        } else if (Math.abs(firstCoordinate.x - secondCoordinate.x) + 1 != shipType.length
                && Math.abs(firstCoordinate.y - secondCoordinate.y) + 1 != shipType.length) {
            throw new IllegalArgumentException("Error! Wrong length of the " + shipType.name + "! Try again:\n\n> ");
        } else if (isCloseToNeighbors(firstCoordinate, secondCoordinate)) {
            throw new IllegalArgumentException("Error! You placed it too close to another one. Try again:\n\n> ");
        } else {
            addSubmarine(firstCoordinate, secondCoordinate);
        }
    }

    private void fillDefault(char[][] field) {
        field[0][0] = ' ';
        char start = '1';
        for (int i = 1; i < 10; i++) {
            field[0][i] = start++;
        }
        start = 'A';
        for (int i = 1; i < 11; i++) {
            field[i][0] = start++;
            for (int j = 1; j < 11; j++) {
                field[i][j] = '~';
            }
        }
    }

    private void addSubmarine(Coordinate firstCoordinate, Coordinate secondCoordinate) {
        int xMin = Math.min(firstCoordinate.x, secondCoordinate.x);
        int xMax = Math.max(firstCoordinate.x, secondCoordinate.x);
        int yMin = Math.min(firstCoordinate.y, secondCoordinate.y);
        int yMax = Math.max(firstCoordinate.y, secondCoordinate.y);
        if (yMax - yMin != 0) {
            Arrays.fill(field[xMin], yMin, yMax + 1, 'O');
        } else {
            for (int j = xMin; j <= xMax; j++) {
                field[j][yMin] = 'O';
            }
        }
    }

    public boolean isCloseToNeighbors(Coordinate firstCoordinate, Coordinate secondCoordinate) {
        int xMin = Math.min(firstCoordinate.x, secondCoordinate.x);
        int xMax = Math.max(firstCoordinate.x, secondCoordinate.x);
        int yMin = Math.min(firstCoordinate.y, secondCoordinate.y);
        int yMax = Math.max(firstCoordinate.y, secondCoordinate.y);
        int xStartIndexForLoop = Math.max(xMin - 1, 0);
        int xEndIndexForLoop = Math.min(xMax + 1, 9);
        int yStartIndexForLoop = Math.max(yMin - 1, 0);
        int yEndIndexForLoop = Math.min(yMax + 1, 9);
        for (int i = xStartIndexForLoop; i <= xEndIndexForLoop; i++) {
            for (int j = yStartIndexForLoop; j <= yEndIndexForLoop; j++) {
                if (field[i][j] == 'O') {
                    return true;
                }
            }
        }
        return false;
    }
}

class Coordinate {

    int x;
    int y;

    public Coordinate(String pair) {
        this.x = pair.charAt(0) - 'A' + 1;
        this.y = Integer.parseInt(pair.substring(1));
        if (x < 1 || x > 10 || y < 1 || y > 10) {
            throw new IllegalArgumentException("Error! You entered the wrong coordinates! Try again:\n\n> ");
        }
    }
}

enum ShipType {

    AIRCRAFT_CARRIER("Aircraft Carrier", 5),
    BATTLESHIP("Battleship", 4),
    SUBMARINE("Submarine", 3),
    CRUISER("Cruiser", 3),
    DESTROYER("Destroyer", 2);

    final String name;
    final int length;

    ShipType(String name, int length) {
        this.name = name;
        this.length = length;
    }
}

class GamePlay {

    private static final String[] MESSAGES = {
            "\nEnter the coordinates of the Aircraft Carrier (5 cells):\n\n> ",
            "\nEnter the coordinates of the Battleship (4 cells):\n\n> ",
            "\nEnter the coordinates of the Submarine (3 cells):\n\n> ",
            "\nEnter the coordinates of the Cruiser (3 cells):\n\n> ",
            "\nEnter the coordinates of the Destroyer (2 cells):\n\n> "
    };

    private final Field firstField;
    private final Field secondField;
    private boolean firstTurn;
    private boolean gameFinished;

    public GamePlay() {
        this.firstField = new Field();
        this.secondField = new Field();
        this.firstTurn = true;
        this.gameFinished = false;
    }

    public void playTheGame() {
        System.out.println("Player 1, place your ships on the game field\n");
        initializeTheField(firstField);
        moveTurn();
        System.out.println("Player 2, place your ships on the game field\n");
        initializeTheField(secondField);
        moveTurn();
        System.out.println("The game starts!\n");
        while (true) {
            startShooting(firstTurn ? firstField : secondField);
            if (gameFinished) {
                break;
            } else {
                moveTurn();
            }
        }
    }

    private void initializeTheField(Field field) {
        Scanner scanner = new Scanner(System.in);
        field.showField(field.field);
        ShipType[] types = ShipType.values();
        for (int i = 0; i < 5; i++) {
            System.out.print(MESSAGES[i]);
            while (true) {
                try {
                    Coordinate firstCoordinate = new Coordinate(scanner.next());
                    Coordinate secondCoordinate = new Coordinate(scanner.next());
                    System.out.println();
                    field.setShip(firstCoordinate, secondCoordinate, types[i]);
                    field.showField(field.field);
                    break;
                } catch (IllegalArgumentException e) {
                    System.out.print(e.getMessage());
                }
            }
        }
    }

    private void startShooting(Field field) {
        Field foeField = firstTurn ? secondField : firstField;
        foeField.showField(foeField.foggedField);
        System.out.println("---------------------");
        field.showField(field.field);
        System.out.printf("\nPlayer %d, it's your turn\n\n> ", firstTurn ? 1 : 2);
        Scanner scanner = new Scanner(System.in);
        while (true) {
            try {
                Coordinate coordinate = new Coordinate(scanner.next());
                System.out.println();
                String message = "";
                if (foeField.field[coordinate.x][coordinate.y] == '~') {
                    foeField.field[coordinate.x][coordinate.y] = 'M';
                    foeField.foggedField[coordinate.x][coordinate.y] = 'M';
                    message = "You missed!";
                } else if (foeField.field[coordinate.x][coordinate.y] == 'O') {
                    foeField.field[coordinate.x][coordinate.y] = 'X';
                    foeField.foggedField[coordinate.x][coordinate.y] = 'X';
                    message = isGameFinished(foeField)
                            ? "You sank the last ship. You won. Congratulations!\n"
                            : foeField.isCloseToNeighbors(coordinate, coordinate)
                            ? "You hit a ship!"
                            : "You sank a ship!";
                }
                System.out.print(message);
                break;
            } catch (IllegalArgumentException e) {
                System.out.print(e.getMessage());
            }
        }
    }

    private boolean isGameFinished(Field field) {
        for (int i = 0; i < field.field.length; i++) {
            for (int j = 0; j < field.field[0].length; j++) {
                if (firstField.field[i][j] == 'O') {
                    return false;
                }
            }
        }
        gameFinished = true;
        return true;
    }

    private void moveTurn() {
        System.out.println("\nPress Enter and pass the move to another player\n");
        new Scanner(System.in).nextLine();
        System.out.print("\033[H\033[2J");
        System.out.flush();
        firstTurn = !firstTurn;
    }
}

class Main {

    public static void main(String[] args) {
        GamePlay gamePlay = new GamePlay();
        gamePlay.playTheGame();
    }
}