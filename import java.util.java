import java.util.HashSet;

public class Sudoku {
    int[][] mat;
    int N = 9; // number of columns/rows.
    int SRN; // square root of N
    int K = 25; // No. Of missing digits
    int R = 25; // No. Of remaining missing digits
    HashSet<String> missingLoc; // keeping track of missing locations

    // Constructor
    public Sudoku() {
        Double SRNd = Math.sqrt(N);
        SRN = SRNd.intValue();
        mat = new int[N][N];
        missingLoc = new HashSet<String>();
    }

    public void fillValues() {
        fillDiagonal();
        fillRemaining(0, SRN);
        removeKDigits();
    }

    private void fillDiagonal() {
        for (int i = 0; i < N; i = i + SRN)
            fillBox(i, i);
    }

    private boolean unUsedInBox(int rowStart, int colStart, int num) {
        for (int i = 0; i < SRN; i++)
            for (int j = 0; j < SRN; j++)
                if (mat[rowStart + i][colStart + j] == num)
                    return false;
        return true;
    }

    private void fillBox(int row, int col) {
        int num;
        for (int i = 0; i < SRN; i++) {
            for (int j = 0; j < SRN; j++) {
                do {
                    num = randomGenerator(N);
                } while (!unUsedInBox(row, col, num));
                mat[row + i][col + j] = num;
            }
        }
    }

    private int randomGenerator(int num) {
        return (int) Math.floor((Math.random() * num + 1));
    }

    private boolean CheckIfSafe(int i, int j, int num) {
        return (unUsedInRow(i, num) && unUsedInCol(j, num) && unUsedInBox(i - i % SRN, j - j % SRN, num));
    }

    private boolean unUsedInRow(int i, int num) {
        for (int j = 0; j < N; j++)
            if (mat[i][j] == num)
                return false;
        return true;
    }

    private boolean unUsedInCol(int j, int num) {
        for (int i = 0; i < N; i++)
            if (mat[i][j] == num)
                return false;
        return true;
    }

    private boolean fillRemaining(int i, int j) {
        if (j >= N && i < N - 1) {
            i = i + 1;
            j = 0;
        }
        if (i >= N && j >= N)
            return true;

        if (i < SRN) {
            if (j < SRN)
                j = SRN;
        } else if (i < N - SRN) {
            if (j == (int) (i / SRN) * SRN)
                j = j + SRN;
        } else {
            if (j == N - SRN) {
                i = i + 1;
                j = 0;
                if (i >= N)
                    return true;
            }
        }

        for (int num = 1; num <= N; num++) {
            if (CheckIfSafe(i, j, num)) {
                mat[i][j] = num;
                if (fillRemaining(i, j + 1))
                    return true;
                mat[i][j] = 0;
            }
        }
        return false;
    }

    private void removeKDigits() {
        int count = K;
        while (count != 0) {
            int cellId = randomGenerator(N * N) - 1;

            int i = (cellId / N);
            int j = cellId % N;
            if (j != 0)
                j = j - 1;

            if (mat[i][j] != 0) {
                count--;
                mat[i][j] = 0;
                missingLoc.add(i + "-" + j);
            }
        }
    }

    public String getSudokuString() {
        StringBuilder sb = new StringBuilder();
        sb.append("     ");
        for (int j = 0; j < N; j++) {
            sb.append("[" + j + "] ");
        }
        sb.append('\n');
        for (int i = 0; i < N; i++) {
            sb.append("[" + i + "]   ");
            for (int j = 0; j < N; j++)
                sb.append(mat[i][j] + "   ");
            sb.append('\n');
        }
        sb.append('\n');
        return sb.toString();
    }

    private boolean isLocationUpdatable(int i, int j) {
        return missingLoc.contains(i + "-" + j);
    }

    public boolean isBoardFull() {
        return this.R == 0;
    }

    public boolean enterNumber(int i, int j, int num) {
        if (i < 0 || i >= N || j < 0 || j >= N || num < 1 || num > 9) {
            return false; // Invalid row, column, or number range
        }

        if (!isLocationUpdatable(i, j)) {
            return false; // Location is not updatable
        }

        if (!unUsedInRow(i, num) || !unUsedInCol(j, num) || !unUsedInBox(i - i % SRN, j - j % SRN, num)) {
            return false; // Number violates Sudoku rules
        }

        mat[i][j] = num;
        R--;
        missingLoc.remove(i + "-" + j);
        return true; // Successful update
    }

    public static void main(String[] args) {
        Sudoku sudoku = new Sudoku();
        sudoku.fillValues();
        System.out.println("Initial Sudoku Board:");
        System.out.println(sudoku.getSudokuString());

        boolean success = sudoku.enterNumber(0, 1, 5);
        if (success) {
            System.out.println("Update successful!");
        } else {
            System.out.println("Update failed!");
        }

        System.out.println("Updated Sudoku Board:");
        System.out.println(sudoku.getSudokuString());
    }
}
