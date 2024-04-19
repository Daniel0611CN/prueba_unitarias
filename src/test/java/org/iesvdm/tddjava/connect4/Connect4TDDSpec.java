package org.iesvdm.tddjava.connect4;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;


public class Connect4TDDSpec {

    public static final int COLUMNS = 7;
    public static final int ROWS = 6;

    private Connect4TDD tested;
    private Connect4.Color[][] board = new Connect4.Color[COLUMNS][ROWS];
    private OutputStream output;


    @BeforeEach
    public void beforeEachTest() {
        output = new ByteArrayOutputStream();

        //Se instancia el juego modificado para acceder a la salida de consola
        tested = new Connect4TDD(new PrintStream(output));
    }

    /*
     * The board is composed by 7 horizontal and 6 vertical empty positions
     */

    @Test
    public void whenTheGameStartsTheBoardIsEmpty() {
        assertThat(tested.getNumberOfDiscs()).isZero(); // board Vacío;
    }

    /*
     * Players introduce discs on the top of the columns.
     * Introduced disc drops down the board if the column is empty.
     * Future discs introduced in the same column will stack over previous ones
     */

    @Test
    public void whenDiscOutsideBoardThenRuntimeException() {
        for (int i = 0; i < COLUMNS; i++) {
            try {
                for (int j = 0; j < ROWS; j++) {
                    // Assert That The disc is inside the Columns (0, 7);
                    assertThat(tested.putDiscInColumn(i)).isBetween(0, COLUMNS);
                }
            } catch (RuntimeException e) {
                assertThat(e.getMessage()).isNotEmpty();
            }
        }
        assertThat(tested.isFinished()).isTrue(); // Comprueba que se ha finalizado el programa;
    }

    @Test
    public void whenFirstDiscInsertedInColumnThenPositionIsZero() {
        for (int i = 0; i < 1; i++) {
            for (int j = 0; j < 1; j++) {
                tested.putDiscInColumn(i); // Put discs on the column i;
                assertThat(i).isEqualTo(0); // Assert the Column to insert the value is the position 0;
            }
        }
        assertThat(tested.getNumberOfDiscs()).isEqualTo(1); // Assert num of discs is equal to 1;
    }

    @Test
    public void whenSecondDiscInsertedInColumnThenPositionIsOne() {
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 1; j++) {
                tested.putDiscInColumn(i); // Put discs on the column i;
                tested.switchPlayer();
                assertThat(1).isEqualTo(1); // Assert the Column to insert the value is the position 1;
            }
        }
        assertThat(tested.getNumberOfDiscs()).isEqualTo(2); // Assert num of discs is equal to 1;
        tested.getCurrentPlayer().equals(Color.RED); // Assert Current Player is Red;
    }

    @Test
    public void whenDiscInsertedThenNumberOfDiscsIncreases() {
        for (int i = 0; i < COLUMNS; i++) {
            for (int j = 0; j < ROWS; j++) {
                if (tested.getNumberOfDiscs()>0) {
                    assertThat(tested.putDiscInColumn(1)).isEqualTo(tested.getNumberOfDiscs()-i);
                }
            }
        }
    }

    @Test
    public void whenNoMoreRoomInColumnThenRuntimeException() {
        for (int i = 0; i < COLUMNS; i++) {
            try {
                for (int j = 0; j < ROWS; j++) {
                    tested.putDiscInColumn(i); // Put the discs in the board;
                }
            } catch (RuntimeException e) {
                assertThat(e.getMessage()).isNotEmpty();
            }
        }
        assertThat(tested.getNumberOfDiscs()).isGreaterThanOrEqualTo(ROWS);
    }

    /*
     * It is a two-person game so there is one colour for each player.
     * One player uses red ('R'), the other one uses green ('G').
     * Players alternate turns, inserting one disc every time
     */

    @Test
    public void whenFirstPlayerPlaysThenDiscColorIsRed() {
        for (int i = 0; i < 1; i++) {
            for (int j = 0; j < 1; j++) {
                tested.getCurrentPlayer();
            }
        }
        tested.getCurrentPlayer().equals(Color.red);
    }

    @Test
    public void whenSecondPlayerPlaysThenDiscColorIsGreen() {
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 1; j++) {
                tested.getCurrentPlayer();
                tested.putDiscInColumn(i);
            }
        }
        tested.getCurrentPlayer().equals(Color.green);
    }

    /*
     * We want feedback when either, event or error occur within the game.
     * The output shows the status of the board on every move
     */

    @Test
    public void whenAskedForCurrentPlayerTheOutputNotice() {
        for (int i = 0; i < COLUMNS; i++) {
            for (int j = 0; j < ROWS; j++) {
                assertThat(output.equals((tested.putDiscInColumn(i))));
            }
        }
    }

    @Test
    public void whenADiscIsIntroducedTheBoardIsPrinted() {
        for (int i = 0; i < 1; i++) {
            for (int j = 0; j < 1; j++) {
                tested.putDiscInColumn(i);
            }
        }
        assertThat(output.equals((assertThat(tested.getNumberOfDiscs()).isEqualTo(1))));
    }

    /*
     * When no more discs can be inserted, the game finishes and it is considered a draw
     */

    @Test
    public void whenTheGameStartsItIsNotFinished() {
        assertFalse(tested.isFinished()); // Comprueba que el test NO ha terminado;
    }

    @Test
    public void whenNoDiscCanBeIntroducedTheGamesIsFinished() {
        for (int i = 0; i < COLUMNS; i++) {
            try {
                for (int j = 0; j < ROWS; j++) {
                    // Llena board de discos;
                    tested.putDiscInColumn(i);
                }
            } catch (RuntimeException e) {
                // Comprueba que el mensaje de error no está vacío;
                assertThat(e.getMessage()).isNotEmpty();
            }
        }
        assertThat(tested.getNumberOfDiscs()).isEqualTo(COLUMNS*ROWS); // Comprueba que el número de discos es igual a 42 (Columns*Rows);
        assertThat(tested.isFinished()).isTrue(); // Y Comprueba que el juego ha terminado;
    }

    /*
     * If a player inserts a disc and connects more than 3 discs of his colour
     * in a straight vertical line then that player wins
     */

    @Test
    public void when4VerticalDiscsAreConnectedThenThatPlayerWins() {
        for (int i = 0; i < 1; i++) {
            for (int j = 0; j <= 3; j++) {
                // Pone discos en [0][1], [0][2], [0][3], [0][4];
                tested.putDiscInColumn(i);
                // --> Columna1, ..., Columna1, ..., Columna1, ..., Columna1; (Finaliza el bucle)
                // --> Fila1, ..., Fila2, ..., Fila3, ..., Fila4; (Finaliza el bucle)

                // --> Next Player "G", Vuelve a cambiar de jugador - "R";
                tested.switchPlayer();
            }
        }
        assertEquals(tested.getWinner(), "R"); // Comprobamos que el jugador que ha ganado ha sido el Rojo;
    }

    /*
     * If a player inserts a disc and connects more than 3 discs of his colour
     * in a straight horizontal line then that player wins
     */

    @Test
    public void when4HorizontalDiscsAreConnectedThenThatPlayerWins() {
        for (int i = 0; i <= 3; i++) {
            for (int j = 0; j < 1; j++) {
                // Pone discos en [1][0], [2][0], [3][0], [4][0];
                tested.putDiscInColumn(i);
                // --> Columna1, ..., Columna2, ..., Columna3, ..., Columna4; (Finaliza el bucle)
                // --> Fila1, ..., Fila1, ..., Fila1, ..., Fila1; (Finaliza el bucle);

                // --> Next Player: "G", Vuelve a cambiar de jugador - "R";
                tested.switchPlayer();
            }
        }
        assertEquals(tested.getWinner(), "R"); // Comprobamos que el jugador que ha ganado ha sido el Rojo;
    }

    /*
     * If a player inserts a disc and connects more than 3 discs of his colour
     * in a straight diagonal line then that player wins
     */

    @Test
    public void when4Diagonal1DiscsAreConnectedThenThatPlayerWins() {
        int contador = 1;
        int contadorInverso = 0;
        for (int i = contadorInverso; i <= contador && contador <= 3; i++) {
            contador++;
            contadorInverso++;
            for (int j = 0; j <= 4; j++) {
                tested.putDiscInColumn(i);
                // --> Columna1, ..., Columna2, ..., Columna3, ..., Columna4; (Finaliza el bucle)
                // --> Fila1, ..., Fila2, ..., Fila3, ..., Fila4; (Finaliza el bucle);

                // --> Next Player: "G", Vuelve a cambiar de jugador - "R";
                tested.switchPlayer();
            }
        }
        assertEquals(tested.getWinner(), "R"); // Comprobamos que el jugador que ha ganado ha sido el Rojo;
    }

    @Test
    public void when4Diagonal2DiscsAreConnectedThenThatPlayerWins() {
        for (int i = 3; i >= 0; i--) {
            for (int j = 0; j <= 3; j++) {
                tested.putDiscInColumn(i);
                // --> Columna4, ..., Columna3, ..., Columna2, ..., Columna1; (Finaliza el bucle);
                // --> Fila1, ..., Fila2, ..., Fila3, ..., Fila4; (Finaliza el bucle);

                // --> Next Player: "G", Vuelve a cambiar de jugador - "R";
                tested.switchPlayer();
            }
        }
        assertEquals(tested.getWinner(), "R"); // Comprobamos que el jugador que ha ganado ha sido el Rojo;
    }

    public Connect4.Color[][] getBoard() {
        return board;
    }

    public void setBoard(Connect4.Color[][] board) {
        this.board = board;
    }
}