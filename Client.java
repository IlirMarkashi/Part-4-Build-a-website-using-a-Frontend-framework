import java.io.*;
import java.net.*;

public class Client {
    private static final String SERVER_ADDRESS = "localhost";  // Address of the server
    private static final int SERVER_PORT = 8000;  // Port of the server

    public static void main(String[] args) {
        try (Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in))) {

            // Display the initial Sudoku board
            String response;
            while ((response = in.readLine()) != null) {
                System.out.println(response);  // Display the board
                if (response.contains("Game over!")) {
                    break;
                }

                // Ask the user for their move
                System.out.println("Enter your move (e.g., 'update row col num') or 'show' to view the board again:");
                String userMove = userInput.readLine();
                out.println(userMove);  // Send the move to the server

                // Handle the response from the server
                if (userMove.startsWith("update")) {
                    String moveResponse = in.readLine();
                    System.out.println(moveResponse);  // Display the server's response
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
