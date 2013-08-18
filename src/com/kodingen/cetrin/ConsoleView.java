package com.kodingen.cetrin;

import org.fusesource.jansi.Ansi;
import org.fusesource.jansi.AnsiConsole;
import static org.fusesource.jansi.Ansi.*;

import java.io.IOException;
import java.util.Scanner;

public class ConsoleView extends View {
    public static final String ANSI_CLS = "\u001b[2J";
    public static final String ANSI_HOME = "\u001b[H";

    private GameModel gm;
    private Scanner in;

    public ConsoleView() {
        in = new Scanner(System.in);
    }

    public void modelChanged(BaseModel model) {
        clearConsole();
        gm = (GameModel) model;
        showGameField();
        if (gm.getWinner() != null) {
            System.out.println("Congratulations to Player " + gm.getWinner().getSymbol() + "!");
            return;
        } else if (!gm.hasMoreTurns()) {
            System.out.println("Draw!");
            return;
        }
        if (gm.getCurrentPlayer().showInputForm()) {
            processInput();
        } else {
            System.out.println("Waiting for Player " + gm.getCurrentPlayer().getSymbol() + " turn...");
            try {
                gm.getCurrentPlayer().makeTurn();
            } catch (IOException e) {
                showMessageAndExit(e.getMessage());
            }
        }
    }

    private void showGameField() {
        //StringBuilder sb = new StringBuilder("*** TicTacToe Game ***\n   0  1  2\n");
        StringBuilder sb = new StringBuilder();
        sb.append(ansi().fg(Color.GREEN).a("* TicTacToe Game *").fg(Color.WHITE).a("\n    0  1  2\n"));
        for (int x = 0; x < gm.getFieldSize(); x++) {
            sb.append(' ').append(x).append(' ');
            for (int y = 0; y < gm.getFieldSize(); y++) {
                sb.append('[');
                //sb.append(gm.getFieldCellChar(x, y));
                Ansi.Color color = gm.getFieldCell(x, y) == GameModel.X ? Color.CYAN : Color.YELLOW;
                sb.append(ansi().fg(color).a(gm.getFieldCellChar(x, y)).fg(Color.WHITE));
                sb.append(']');
            }
            sb.append('\n');
        }
        AnsiConsole.systemInstall();
        System.out.println(sb.toString());
        AnsiConsole.systemUninstall();
        System.out.println("q - to quit; u - to undo last move in game with computer\n");
    }

    private void processInput() {
        System.out.print("Player " + gm.getCurrentPlayer().getSymbol() + " turn (row column): ");
        String[] input = in.nextLine().trim().split(" ");
        if (input.length == 1) {
            switch (input[0].charAt(0)) {
                case 'q':
                    gm.unsubscribe(this);
                    System.exit(0);
                    break;
                case 'u':
                    if (gm.canDiscardLastPlayerMove()) {
                        gm.discardLastPlayerMove();
                    } else {
                        System.out.println("You can't undo last move in game with real player or if there is no moves.");
                        processInput();
                    }
                    break;
                default:
                    System.out.println("Illegal input.\nTry again.");
                    processInput();
            }
        } else if (input.length == 2) {
            int x, y;
            try {
                x = Integer.parseInt(input[0]);
                y = Integer.parseInt(input[1]);
                String message;
                while ((message = gm.makeTurn(x, y)) != null) {
                    System.out.println(message);
                    processInput();
                }
            } catch (NumberFormatException nfe) {
                System.out.println("Illegal input.\nTry again.");
                processInput();
            }
        }
    }

    public void start() {
        System.out.println("Starting new game.\nSelect game type.");
        System.out.println("1) Real player - Real player");
        System.out.println("2) Real player - Computer");
        System.out.println("3) Create network game");
        System.out.println("4) Connect to network game");
        System.out.print("Enter game type: ");
        int gameTypeCode = in.nextInt();
        in.nextLine(); //read next line symbol
        while (gameTypeCode < 1 || gameTypeCode > 4) {
            System.out.println("Wrong input. Try again: ");
            gameTypeCode = in.nextInt();
        }
        Player xPlayer = null;
        Player oPlayer = null;
        switch (gameTypeCode) {
            case 1:
                xPlayer = new RealPlayer(GameModel.X);
                oPlayer = new RealPlayer(GameModel.O);
                break;
            case 2:
                xPlayer = new RealPlayer(GameModel.X);
                oPlayer = new ComputerPlayer();
                break;
            case 3:
                xPlayer = new RealPlayer(GameModel.X);
                try {
                    System.out.println("Waiting for connection...");
                    oPlayer = new NetworkPlayer(GameModel.O, NetworkPlayer.SERVER, null);
                    System.out.println("Connection accepted.");
                } catch (IOException e) {
                    showMessageAndExit("Unable to start server.");
                }
                break;
            case 4:
                oPlayer = new RealPlayer(GameModel.O);
                System.out.println("Enter server address:");
                String addr = in.nextLine();
                try {
                    System.out.println("Trying to connect to " + addr);
                    xPlayer = new NetworkPlayer(GameModel.X, NetworkPlayer.CLIENT, addr);
                    System.out.println("Connection established.");
                } catch (IOException e) {
                    showMessageAndExit("Unable connect to server.");
                }
                break;
        }
        GameModel gm = new GameModel(xPlayer, oPlayer);
        gm.subscribe(this);

    }

    private void showMessageAndExit(String msg) {
        System.out.println(msg + " Exiting...");
        System.exit(0);
    }

    private void clearConsole() {
        try {
            String os = System.getProperty("os.name");
            if (os.contains("Windows")) {
                //AnsiConsole.systemInstall();
                AnsiConsole.out.println(ANSI_CLS);
                AnsiConsole.out.println(ANSI_HOME);
                //AnsiConsole.systemUninstall();
            } else {
                Runtime.getRuntime().exec("clear");
            }
        } catch (IOException e) {
            // ignore
        }
    }
}
