import java.util.Scanner;

public class TicTacToe {

    private final char X = 'X';
    private final char O = 'O';
    private final char EMPTY = ' ';
    private char playerTurn = X;
    private GameState gameState;
    private char[][] board;

    public TicTacToe() {
        run();
    }

    private void run(){
        Scanner scanner = new Scanner(System.in);
        gameState = GameState.RUNNING;
        initializeBoard();
        printBoard();
        while(gameState == GameState.RUNNING){
            if(playerTurn == X){
                System.out.println("Input the number of the space to move.");
                int playerMove = -1;
                // Make sure the player gives a valid input
                while(true) {
                    try {
                        String input = scanner.nextLine();
                        playerMove = Integer.parseInt(input);
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid input. Try again.");
                        continue;
                    }
                    if(isValidMove(playerMove)){
                        break;
                    }else{
                        System.out.println("Invalid input. Try again.");
                    }
                }
                // Do the player's move and switch the turn to the AI
                int row = playerMove / 3;
                int col = playerMove % 3;
                board[row][col] = X;
                playerTurn = O;
            }else{
                // Loop through every possible move with minimax and store the best move's score and position
                int bestScore = Integer.MAX_VALUE;
                int bestMove = -1;
                for(int i = 0; i < 3; i++){
                    for(int j = 0; j < 3; j++){
                        int move = ((i*3) + j);
                        if(isValidMove(move)){
                            board[i][j] = O;
                            int score = minimax(board, 0, true);
                            board[i][j] = EMPTY;
                            if(score < bestScore){
                                bestScore = score;
                                bestMove = move;
                            }
                        }
                    }
                }
                // Do the best move and switch to the player's turn
                int row = bestMove / 3;
                int col = bestMove % 3;
                board[row][col] = O;
                playerTurn = X;
                printBoard();
            }
            // Check for a winner, draw or nothing
            gameState = checkGameState(board);
        }

        System.out.println();
        // Display a game end message depending on the final game state
        switch (gameState){
            case X_WIN -> System.out.println("X wins!");
            case O_WIN -> System.out.println("O wins!");
            case DRAW -> System.out.println("Draw!");
        }
        printBoard();
    }

    // AI is minimizing
    // Player is maximizing
    private int minimax(char[][] board, int depth, boolean isMaximizing){

        if(isMaximizing){
            switch (checkGameState(board)){
                case DRAW:
                    return 0;
                case X_WIN:
                    return Integer.MAX_VALUE - depth;
                case O_WIN:
                    // AI win is worst possible outcome
                    return Integer.MIN_VALUE;
            }
        }else{
            switch (checkGameState(board)){
                case DRAW:
                    return 0;
                case X_WIN:
                    // Player win is worst possible outcome
                    return Integer.MAX_VALUE;
                case O_WIN:
                    return Integer.MIN_VALUE + depth;
            }
        }

        // Player Turn
        if(isMaximizing){
            int bestScore = Integer.MIN_VALUE;
            for(int i = 0; i < 3; i++){
                for(int j = 0; j < 3; j++){
                    int move = ((i*3) + j);
                    if(isValidMove(move)){
                        board[i][j] = X;
                        int score = minimax(board, depth+1, false);
                        board[i][j] = EMPTY;
                        if(score > bestScore){
                            bestScore = score;
                        }
                    }
                }
            }
            return bestScore;
        }else{ // AI Turn
            int bestScore = Integer.MAX_VALUE;
            for(int i = 0; i < 3; i++){
                for(int j = 0; j < 3; j++){
                    int move = ((i*3) + j);
                    if(isValidMove(move)){
                        board[i][j] = O;
                        int score = minimax(board, depth+1, true);
                        board[i][j] = EMPTY;
                        if(score < bestScore){
                            bestScore = score;
                        }
                    }
                }
            }
            return bestScore;
        }
    }

    // Initialize the board to empty chars
    private void initializeBoard(){
        board = new char[3][3];
        for(char[] row : board){
            for(int i = 0; i < 3; i++){
                row[i] = EMPTY;
            }
        }
    }

    // Print the board
    // Empty spaces will appear with its corresponding input for the player
    // Non-Empty spaces display the player that occupies the space
    private void printBoard(){
        for(int i = 0; i < 3; i++){
            System.out.print('|');
            for(int j = 0; j < 3; j++){
                if(board[i][j] == EMPTY){
                    System.out.print(((i*3) + j) + "|");
                }else {
                    System.out.print(board[i][j] + "|");
                }
            }
            System.out.println();
        }
    }

    private boolean isValidMove(int move){
        // Make sure the move is in bounds before checking the array
        if(move < 0 || move > 8){
            return false;
        }
        int row = move / 3;
        int col = move % 3;
        // Only a move to an empty space is valid
        return board[row][col] == EMPTY;
    }

    private GameState checkGameState(char[][] board){
        // Check for horizontal wins
        for(char[] row : board){
            char first = row[0];
            if(first == EMPTY)
                continue;
            boolean threeMatch = true;
            for(int i = 0; i < 3; i++){
                if(row[i] != first){
                    threeMatch = false;
                    break;
                }
            }
            if(threeMatch){
                return first == X ? GameState.X_WIN : GameState.O_WIN;
            }
        }
        // Check for vertical wins
        for(int column = 0; column < 3; column++){
            char first = board[0][column];
            if(first == EMPTY)
                continue;
            boolean threeMatch = true;
            for(int row = 0; row < 3; row++){
                if(board[row][column] != first){
                    threeMatch = false;
                    break;
                }
            }
            if(threeMatch){
                return first == X ? GameState.X_WIN : GameState.O_WIN;
            }
        }

        // Check for diagonal wins
        char first = board[0][0];
        if(first != EMPTY){
            if(first == board[1][1] && first == board[2][2]){
                return first == X ? GameState.X_WIN : GameState.O_WIN;
            }
        }
        first = board[2][0];
        if(first != EMPTY){
            if(first == board[1][1] && first == board[0][2]){
                return first == X ? GameState.X_WIN : GameState.O_WIN;
            }
        }

        // Check for full board (draw)
        boolean emptySpace = false;
        for(char[] row : board){
            for(int i = 0; i < 3; i++){
                if(row[i] == EMPTY){
                    emptySpace = true;
                    break;
                }
                if(emptySpace){
                    break;
                }
            }
        }
        if(!emptySpace){
            return GameState.DRAW;
        }

        return GameState.RUNNING;
    }

    private enum GameState{
        RUNNING,
        X_WIN,
        O_WIN,
        DRAW
    }

}
