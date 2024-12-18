import java.io.*;
import java.net.*;
import java.util.*;

public class Server {
    private static final int PORT = 8000;
    private static Sudoku sudoku = new Sudoku();
    private static List<PrintWriter> clientWriters = new ArrayList<>();

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server is running on port " + PORT);
            sudoku.fillValues();
            while (true) {
                new ClientHandler(serverSocket.accept()).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static class ClientHandler extends Thread {
        private Socket socket;
        private PrintWriter out;
        private BufferedReader in;

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        public void run() {
            try {
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);
                synchronized (clientWriters) {
                    clientWriters.add(out);
                }

                out.println(sudoku.getSudokuString());

                String message;
                while ((message = in.readLine()) != null) {
                    if (message.startsWith("show")) {
                        out.println(sudoku.getSudokuString());
                    } else if (message.startsWith("update")) {
                        String[] parts = message.split(" ");
                        int row = Integer.parseInt(parts[1]);
                        int col = Integer.parseInt(parts[2]);
                        int num = Integer.parseInt(parts[3]);

                        boolean success = sudoku.enterNumber(row, col, num);
                        if (success) {
                            synchronized (clientWriters) {
                                for (PrintWriter writer : clientWriters) {
                                    writer.println(sudoku.getSudokuString());
                                }
                            }
                        } else {
                            out.println("Invalid move");
                        }

                        if (sudoku.isBoardFull()) {
                            synchronized (clientWriters) {
                                for (PrintWriter writer : clientWriters) {
                                    writer.println("Game over! Final board:");
                                    writer.println(sudoku.getSudokuString());
                                }
                            }
                            break;
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                synchronized (clientWriters) {
                    clientWriters.remove(out);
                }
            }
        }
    }
}
