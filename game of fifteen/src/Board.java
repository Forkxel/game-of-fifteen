import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.Collections;

/**
 * This class is used for the game
 */
public class Board implements ActionListener {
    JLabel time = new JLabel();
    JFrame frame = new JFrame();
    JPanel timer = new JPanel();
    JPanel grid = new JPanel();
    JButton[][] buttons = new JButton[4][4];
    ArrayList<Integer> numbers = new ArrayList<>();
    private int seconds = 0;
    private int minutes = 0;
    private int hours = 0;
    private int elapsed = 0;
    private String stringSeconds = String.format("%02d", seconds);
    private String stringMinutes = String.format("%02d", minutes);
    private String stringHours = String.format("%02d", hours);
    Timer stopWatch = new Timer(1000, e -> {
        elapsed += 1000;
        hours = elapsed/3600000;
        minutes = (elapsed/60000) % 60;
        seconds = (elapsed/1000) % 60;
        stringSeconds = String.format("%02d", seconds);
        stringMinutes = String.format("%02d", minutes);
        stringHours = String.format("%02d", hours);
        time.setText(stringHours + ":" + stringMinutes + ":" + stringSeconds);
    });

    /**
     * Generates the frame, timer and the grid of the buttons
     */
    public Board(){
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setTitle("15 puzzle game");
        frame.setSize(800,900);
        frame.setLayout(new BorderLayout());
        frame.setLocationRelativeTo(null);

        time.setBackground(new Color(0, 0, 128));
        time.setForeground(new Color(200,200,200));
        time.setFont(new Font("Montserrat",Font.BOLD,75));
        time.setHorizontalAlignment(JLabel.CENTER);
        time.setText(stringHours + ":" + stringMinutes + ":" + stringSeconds);
        time.setOpaque(true);

        timer.setLayout(new BorderLayout());
        timer.setBounds(0,0,800,100);
        timer.add(time);
        frame.add(timer,BorderLayout.NORTH);

        grid.setLayout(new GridLayout(4,4));

        generateButtons();
        frame.add(grid);
        frame.setVisible(true);
    }

    /**
     * This method is used to generate buttons and place them in the grid
     */
    public void generateButtons(){
        for (int i = 0; i < 16;i++){
            numbers.add(i);
        }

        shuffle();
        int index = 0;

        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                buttons[i][j] = new JButton();
                if (index < numbers.size()) {
                    int value = numbers.get(index++);
                    buttons[i][j].setFont(new Font("Montserrat", Font.BOLD, 50));
                    buttons[i][j].setFocusable(false);
                    buttons[i][j].addActionListener(this);


                    if (value == 0) {
                        buttons[i][j].setText("");
                        buttons[i][j].setBackground(Color.black);
                    } else {
                       buttons[i][j].setText(String.valueOf(value));
                       buttons[i][j].setBackground(Color.lightGray);
                    }
                    grid.add(buttons[i][j]);
                }
            }
        }
    }

    /**
     * This method is searching coordinates of the clicked button and is starting Timer
     * @param e the event to be processed
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        Object clickedButton = e.getSource();
        int clickedRow = 0;
        int clickedCollumn = 0;
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                if (buttons[i][j] == clickedButton) {
                    if (!stopWatch.isRunning()){
                        stopWatch.start();
                    }
                    clickedRow = i;
                    clickedCollumn = j;
                    break;
                }
            }
        }

        if (!isGameWon()){
            if (nextEmptySpace(clickedRow, clickedCollumn)) {
                moveButton(clickedRow, clickedCollumn);
                if (isGameWon()) {
                    stopWatch.stop();
                    win();
                }
            }
        }
    }

    /**
     * This method is checking if the clicked button is next the empty space
     * @param row of the clicked button
     * @param column of the clicked button
     * @return true if the clicked button is next the empty space
     */

    private boolean nextEmptySpace(int row, int column) {
        return (row > 0 && buttons[row - 1][column].getText().isEmpty()) ||
                (row < 3 && buttons[row + 1][column].getText().isEmpty()) ||
                (column > 0 && buttons[row][column - 1].getText().isEmpty()) ||
                (column < 3 && buttons[row][column + 1].getText().isEmpty());
    }

    /**
     * This method swaps the button with the free space
     * @param row where the player pressed the button
     * @param column where the player pressed the button
     */
    private void moveButton(int row, int column) {
        int emptyRow = 0;
        int emptyCollumn = 0;
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                if (buttons[i][j].getText().isEmpty()) {
                    emptyRow = i;
                    emptyCollumn = j;
                    break;
                }
            }
        }
        String temp = buttons[row][column].getText();
        buttons[row][column].setText("");
        buttons[emptyRow][emptyCollumn].setText(temp);
        buttons[row][column].setBackground(Color.black);
        buttons[emptyRow][emptyCollumn].setBackground(Color.LIGHT_GRAY);
    }

    /**
     * checks if all the buttons are at the right place or not
     * @return true if they are or false if they aren't
     */
    private boolean isGameWon() {
        int count = 1;
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                String buttonText = buttons[i][j].getText();
                if (!buttonText.isEmpty() && Integer.parseInt(buttonText) != count) {
                    return false;
                }
                count++;
            }
        }
        return true;
    }

    /**
     * Checks if the game is solvable
     * @return true if it is
     */
    private boolean solvable() {
        int inversion = 0;
        Collections.shuffle(numbers);

        for (int i = 0; i < 15; i++) {
            for (int j = i + 1; j < 16; j++) {
                if (numbers.get(i) > numbers.get(j) && numbers.get(i) > 0 && numbers.get(j) > 0) {
                    inversion++;
                }
            }
        }

        int emptyRow = 0;
        for (int i = 0; i < 16; i++) {
            if (numbers.get(i) == 0) {
                emptyRow = i / 4;
                break;
            }
        }

        if (inversion % 2 == 0 && emptyRow % 2 == 1) {
            return true;
        } else if (inversion % 2 == 1 && emptyRow % 2 == 0) {
            return true;
        }

        return false;
    }

    /**
     * This method shuffles the numbers in ArrayList until it is solvable
     */
    private void shuffle(){
        while (!solvable()){
            Collections.shuffle(numbers);
        }
    }

    /**
     * This method generates winning message
     */
    private void win(){
        JFrame wframe = new JFrame();
        JLabel wlabel = new JLabel();

        wframe.setLocationRelativeTo(null);
        wframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        wframe.setSize(500,500);
        wframe.setLayout(new BorderLayout());
        wframe.setTitle("You won");

        wlabel.setText("<HTML>You won!!!<BR>Your time was: " + stringHours + ":" + stringMinutes + ":" + stringSeconds + "</HTML>");
        wlabel.setFont(new Font("Montserrat",Font.BOLD,30));
        wlabel.setOpaque(true);
        wlabel.setForeground(new Color(200,200,200));
        wlabel.setBackground(new Color(0,0,128));
        wlabel.setHorizontalAlignment(JLabel.CENTER);

        wframe.add(wlabel);
        wframe.setVisible(true);
    }
}