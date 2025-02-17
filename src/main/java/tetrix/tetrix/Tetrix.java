package tetrix.tetrix;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.Media;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.*;
import java.net.URL;
import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.util.*;


public class Tetrix extends Application implements Initializable {
    @FXML
    private Button aboutButton;

    @FXML
    private Button hightScoreButton;

    @FXML
    private Button leaveProgramButton;
    @FXML
    private Button pauseGameButton;
    @FXML
    private Button newGameButton;
    @FXML
    private Label levelLabel;


    @FXML
    private Label lineLabel;

    @FXML
    private Pane nextTetrisPane;

    @FXML
    private Pane pane;

    @FXML
    private Label scoreLabel;

    @FXML
    private Label timeLabel;

    private int nbrLigne;
    private int nbrColonne;
    private int level;
    private long pauseTime;
    private long levelTime;
    private Rectangle[] futureCases;
    private Rectangle[] casesEnCours;
    private ArrayList<Rectangle> cases;
    private Rectangle[][] tetris;
    private Timer timer;
    private Timer timer2;
    private Instant start;
    private boolean disable;
    private boolean pausee;
    private boolean gameOver;
    private Stage secondStage;
    private TextField gamerName;
    private String name;
    private ComboBox<String> levelChoice;
//    private Media media;
//    private MediaPlayer son;


    @Override
    public void start(Stage primaryStage) throws IOException {

        FXMLLoader fxmlLoader = new FXMLLoader(Tetrix.class.getResource("tetrix.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        primaryStage.initStyle(StageStyle.UNDECORATED);
        primaryStage.setTitle("Tétris");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }


    //-----------------++++++++++++++++++++--------------------+++++++++++++++++++++
    //-----------------++++++++++++++++++++--------------------+++++++++++++++++++++
    //-----------------++++++++++++++++++++--------------------+++++++++++++++++++++
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        gameOver = true;
        nbrLigne = 24;
        nbrColonne = 12;
        cases = new ArrayList<>();
        tetris = new Rectangle[7][4];
        disable = false;
        pausee = true;
        timer = new Timer();
        pauseTime = 0;
        timer2 = new Timer();
        levelTime = 500;
        levelChoice = new ComboBox<>();
        level = 1;
        for (int i = 1; i < 13; i++) levelChoice.getItems().add("Niveau " + i);
        levelChoice.getSelectionModel().select(0);
        try {
            media = new Media(new File("src\\main\\resources\\tetrix\\tetrix\\Tetris.mp3").toURI().toString());
        } catch (Exception ignored) {
        }
        son = new MediaPlayer(media);
        son.setOnEndOfMedia(() -> son.seek(javafx.util.Duration.ZERO));
        son.play();

        //Créations des blocs

        //BARRE
        for (int i = 0; i < 4; i++) {
            Rectangle rectangle = sentDetails();
            rectangle.setFill(Color.web("#32e6e3"));
            rectangle.setX(20 * i);
            rectangle.setY(0);
            tetris[0][i] = rectangle;
        }

        //Carré
        int compt = 0;
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 2; j++) {
                Rectangle rectangle = sentDetails();
                rectangle.setFill(Color.web("#acd700"));
                rectangle.setX(20 * j);
                rectangle.setY(20 * i);
                tetris[1][compt++] = rectangle;
            }
        }

        //Té
        for (int i = 0; i < 4; i++) {
            Rectangle rectangle = sentDetails();
            rectangle.setFill(Color.web("#560c60"));
            rectangle.setX(20 * i);
            rectangle.setY(0);
            if (i == 3) {
                rectangle.setX(20);
                rectangle.setY(20);
            }
            tetris[2][i] = rectangle;
        }

        //Lambda ou L
        for (int i = 0; i < 4; i++) {
            Rectangle rectangle = sentDetails();
            rectangle.setFill(Color.web("#a37e0d"));
            rectangle.setX(20 * i);
            rectangle.setY(0);
            if (i == 3) {

                rectangle.setX(0);
                rectangle.setY(20);
            }
            tetris[3][i] = rectangle;
        }

        //L inversé
        for (int i = 0; i < 4; i++) {
            Rectangle rectangle = sentDetails();
            rectangle.setFill(Color.web("#2e04c5"));
            rectangle.setX(20 * i);
            rectangle.setY(0);
            if (i == 3) {
                rectangle.setX(40);
                rectangle.setY(20);
            }
            tetris[4][i] = rectangle;
        }

        //Z
        for (int i = 0; i < 2; i++) {
            Rectangle rectangle = sentDetails();
            rectangle.setFill(Color.web("#c41e06"));
            rectangle.setX(20 * i);
            rectangle.setY(0);
            tetris[5][i] = rectangle;
        }
        for (int i = 0; i < 2; i++) {
            Rectangle rectangle = sentDetails();
            rectangle.setFill(Color.web("#c41e06"));
            rectangle.setX((i + 1) * 20);
            rectangle.setY(20);
            tetris[5][i + 2] = rectangle;
        }

        //Z inversé
        for (int i = 0; i < 2; i++) {
            Rectangle rectangle = sentDetails();
            rectangle.setFill(Color.web("#00ff44"));
            rectangle.setX(20 * i);
            rectangle.setY(20);
            tetris[6][i] = rectangle;
        }
        for (int i = 0; i < 2; i++) {
            Rectangle rectangle = sentDetails();
            rectangle.setFill(Color.web("#00ff44"));
            rectangle.setX(20 * (i + 1));
            rectangle.setY(0);
            tetris[6][i + 2] = rectangle;
        }


        repeindre();
        gameOver = false;
        tirerLeTetris();
        casesEnCours = futureCases;
        for (int i = 0; i < 4; i++) pane.getChildren().add(casesEnCours[i]);
        tirerLeTetris();
        gameOver = true;

    }

    //-----------------++++++++++++++++++++--------------------+++++++++++++++++++++

    @FXML
    void about(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText("""
                ---->But du jeu : Créer plus de ligne complète sans trous avec les blocs .
                ---->Chaque ligne complète vous raporte 10 fois le niveau points (10*niveau).
                Un bonus de plus 2 points est offert si vous remplissez au moins 2 lignes du coups.
                               \s
                ---->Ce jeu a été implémenté par Parfait BOTCHI""");

        alert.show();
    }

    //-----------------++++++++++++++++++++--------------------+++++++++++++++++++++

    @FXML
    void hightScore(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText("Pas de scores disponibles");

        String nameOftheBest = null;
        int hightscoree = 0;
        BufferedReader bic;
        try {
            bic = new BufferedReader(new FileReader("src/main/resources/tetrix/tetrix/scoresHistory.txt"));
        } catch (FileNotFoundException e) {
            alert.show();
            return;
        }

        String read;
        try {
            while ((read = bic.readLine()) != null) {
                String[] result = read.split("///");
                if (Integer.parseInt(result[1]) > hightscoree) {
                    hightscoree = Integer.parseInt(result[1]);
                    nameOftheBest = result[0].trim();
                }
            }
        } catch (IOException ignored) {
            alert.show();
            return;
        }

        Alert alert1 = new Alert(Alert.AlertType.INFORMATION);
        alert1.setContentText(nameOftheBest + " avec un score de " + hightscoree);
        alert1.show();
    }

    //-----------------++++++++++++++++++++--------------------+++++++++++++++++++++

    @FXML
    void pauseGame() {
        if (!pausee) {
//            son.pause();
            pauseGameButton.setText("Continuer");
            pausee = true;
            timer.cancel();
            timer2.cancel();
            son.pause();
            try {
                pauseTime += Duration.between(start, Instant.now()).getSeconds();
            } catch (Exception ignored) {
            }

        } else {
            pauseGameButton.setText("Pause");
            desativerBoutons();
            pausee = false;
            timer = new Timer();
            timer2 = new Timer();
            start = Instant.now();
            if (!gameOver) lancer();
//            son.play();
        }
    }

    //-----------------++++++++++++++++++++--------------------+++++++++++++++++++++
    @FXML
    private void deplacer(KeyEvent event) {
        pane.requestFocus();
        if (event.isShiftDown()) {
            desativerBoutons();
        }

        if (!pausee && !gameOver) {
            if (event.getCode().equals(KeyCode.LEFT)) {
                for (int i = 0; i < 4; i++) {
                    if (casesEnCours[i].getX() == 0) return;
                    for (Rectangle rectangle : cases)
                        if (rectangle.getX() - casesEnCours[i].getX() == -20 && rectangle.getY() == casesEnCours[i].getY())
                            return;
                }
                for (int i = 0; i < 4; i++) casesEnCours[i].setX(casesEnCours[i].getX() - 20);
            }

            if (event.getCode().equals(KeyCode.RIGHT)) {
                for (int i = 0; i < 4; i++) {
                    if (casesEnCours[i].getX() == 220) return;
                    for (Rectangle rectangle : cases)
                        if (rectangle.getX() - casesEnCours[i].getX() == 20 && rectangle.getY() == casesEnCours[i].getY())
                            return;
                }
                for (int i = 0; i < 4; i++) casesEnCours[i].setX(casesEnCours[i].getX() + 20);
            }

            if (event.getCode().equals(KeyCode.UP)) {
                makeTheRotate();
            }
            if (event.getCode().equals(KeyCode.DOWN)) {
                descendre();
            }
        }
    }

    //-----------------++++++++++++++++++++--------------------+++++++++++++++++++++
    @FXML
    public void starTGame() {
        pausee = false;
        try {
            pauseGame();
        } catch (Exception ignored) {
        }
        try {
            gameOver = false;
//            son.play();
            secondStage = new Stage();
            Pane pane1 = new Pane();
            pane1.setPrefSize(414, 314);
            pane1.setStyle("-fx-background-color: #e8fff1;");
            Label label = new Label("Tetris");
            label.setLayoutX(129);
            label.setLayoutY(14);
            label.setTextFill(Paint.valueOf("#520073"));
            label.setFont(new Font("Bodoni MT Black", 53.0));
            Label label1 = new Label("Niveau :");
            label1.setFont(new Font("Bodoni MT Black", 19));
            label1.setLayoutX(6);
            label1.setLayoutY(182);
            levelChoice.setLayoutX(132);
            levelChoice.setLayoutY(181);
            levelChoice.setPrefWidth(150);
            Font font = new Font("Bodoni MT Black", 14);
            Button button = new Button("Commencer");
            button.setFont(font);
            button.setLayoutX(302);
            button.setLayoutY(260);
            button.setOnAction(this::startGame);
            button.setPrefSize(110, 53);
            Label label2 = new Label("Nom du joueur :");
            label2.setLayoutX(6);
            label2.setLayoutY(115);
            label2.setPrefSize(121, 34);
            label2.setFont(font);
            gamerName = new TextField("Inconnu");
            gamerName.setFont(font);
            gamerName.setLayoutX(132);
            gamerName.setLayoutY(115);
            gamerName.setPrefSize(149, 34);
            gamerName.setPromptText("Entrez votre nom");
            Button returnButton = new Button("Retour");
            returnButton.setFont(font);
            returnButton.setLayoutX(0);
            returnButton.setLayoutY(288);
            returnButton.setPrefSize(80, 25);
            returnButton.setOnAction(_ -> secondStage.close());
            pane1.getChildren().addAll(Arrays.asList(label, button, label1, label2, gamerName, levelChoice, returnButton));
            secondStage.setScene(new Scene(pane1));
            secondStage.show();
            secondStage.toFront();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    //-----------------++++++++++++++++++++--------------------+++++++++++++++++++++

    @FXML
    void startGame(ActionEvent event) {
        if (name != null) try {
            saveScore();
        } catch (Exception ignored) {
        }
        desativerBoutons();
        level = levelChoice.getSelectionModel().getSelectedIndex() + 1;
        name = gamerName.getText().trim();
        if (name.isEmpty()) name = "Inconnue";
        secondStage.close();
        levelLabel.setText(String.valueOf(level));
        cases = new ArrayList<>();
        pausee = false;
        timer.cancel();
        timer = new Timer();
        pauseTime = 0;
        pauseGameButton.setText("Pause");
        timer2.cancel();
        timer2 = new Timer();
        levelTime = (long) (1000 / (level * 1.3));
        scoreLabel.setText("0");
        nextTetrisPane.getChildren().removeFirst();
        repeindre();
        tirerLeTetris();
        casesEnCours = futureCases;
        for (int i = 0; i < 4; i++) pane.getChildren().add(casesEnCours[i]);
        tirerLeTetris();
        start = Instant.now();
        lancer();
    }

    //-----------------++++++++++++++++++++--------------------+++++++++++++++++++++
    @FXML
    void leaveProgram(ActionEvent actionEvent) {
        try {
            saveScore();
        } catch (Exception ignored) {
        }
        System.exit(0);
    }


    //-----------------++++++++++++++++++++--------------------+++++++++++++++++++++
    //-----------------++++++++++++++++++++--------------------+++++++++++++++++++++
    //-----------------++++++++++++++++++++--------------------+++++++++++++++++++++


    //-----------------++++++++++++++++++++-  Effectuer des mouvements -------------------+++++++++++++++++++++
//-----------------++++++++++++++++++++--------------------+++++++++++++++++++++
// -----------------++++++++++++++++++++--------------------+++++++++++++++++++++
    public void makeTheRotate() {
        if (!gameOver) {
            if (casesEnCours[0].getFill().equals(Color.web("#acd700"))) return;
            Rectangle center;
            if (casesEnCours[0].getFill().equals(Color.web("#32e6e3")) || casesEnCours[0].getFill().equals(Color.web("#c41e06")))
                center = casesEnCours[2];
            else center = casesEnCours[1];
            Rectangle[] result = cloner(casesEnCours);
            for (Rectangle rectangle : result) {
                double oldX = rectangle.getX();
                double oldY = rectangle.getY();
                double newX = oldY - center.getY() + center.getX();
                double newY = -oldX + center.getX() + center.getY();
                if (newX >= 240 || newX <= 0 || newY >= 480) {
                    result = cloner(casesEnCours);
                    if (center == casesEnCours[1]) center = casesEnCours[2];
                    else center = casesEnCours[1];
                    for (Rectangle rectangle1 : result) {
                        double oldX2 = rectangle1.getX();
                        double oldY2 = rectangle1.getY();
                        double newX2 = oldY2 - center.getY() + center.getX();
                        double newY2 = -oldX2 + center.getX() + center.getY();
                        if (newX2 >= 240 || newX2 <= 0 || newY2 >= 480) {
                            return;
                        }
                        rectangle1.setX(newX2);
                        rectangle1.setY(newY2);
                    }
                    break;
                }
                rectangle.setX(newX);
                rectangle.setY(newY);
            }
            for (int i = 0; i < 4; i++) pane.getChildren().remove(casesEnCours[i]);
            casesEnCours = Arrays.copyOf(result, 4);
            for (int i = 0; i < 4; i++) pane.getChildren().add(casesEnCours[i]);
        }
    }
//-----------------++++++++++++++++++++--------------------+++++++++++++++++++++

    public void descendre() {
        if (!gameOver) {
            for (Rectangle rect : cases) {
                if (rect.getY() == 0) {
                    gameOver = true;
                    Platform.runLater(() -> {
                        nextTetrisPane.getChildren().remove(0, 4);
                        Label label = new Label("Game Over");
                        label.setFont(new Font("Bodoni MT Black", 14));
                        label.setLayoutY(1);
                        nextTetrisPane.getChildren().add(label);
                    });
                    return;
                }
            }
            try {
                Platform.runLater(() -> {
                    if (peutDescendre() && !gameOver)
                        for (int i = 0; i < 4; i++) casesEnCours[i].setY(casesEnCours[i].getY() + 20);
                    else {
                        cases.addAll(Arrays.asList(casesEnCours).subList(0, 4));
                        casesEnCours = Arrays.copyOf(futureCases, 4);
                        for (int i = 0; i < 4; i++) pane.getChildren().add(casesEnCours[i]);
                        cases.sort(Comparator.comparingInt(rect -> (int) rect.getY()));
                        supprimer();
                        tirerLeTetris();
                    }
                });
            } catch (Exception ignored) {
            }
        }
    }

    //-----------------++++++++++++++++++++--------------------+++++++++++++++++++++
    //-----------------++++++++++++++++++++--------------------+++++++++++++++++++++

    //-----------------++++++++++++++  OUTILS  ++++++--------------------+++++++++++++++++++++
    void repeindre() {
        try {
            pane.getChildren().clear();
        } catch (Exception ignored) {
        }
        for (int i = 0; i < nbrLigne; i++) {
            for (int j = 0; j < nbrColonne; j++) {
                Rectangle rectangle = new Rectangle(20 * j, 20 * i, 20, 20);
                if (j % 2 == 0) rectangle.setFill(Color.web("#f7daf7"));
                else rectangle.setFill(Color.web("#ffffff"));
                pane.getChildren().add(rectangle);
            }
        }
    }
//-----------------++++++++++++++++++++--------------------+++++++++++++++++++++

    public void tirerLeTetris() {
        if (!gameOver) {
            SecureRandom random = new SecureRandom();
            Random random1 = new Random();
            int choix = (random1.nextInt(-1, 8) + random.nextInt(-1, 8)) / 2;
            if (choix == -1 || choix == 7) choix = (random1.nextInt(0, 7) + random.nextInt(0, 7)) / 2;

            futureCases = cloner(tetris[choix]);
            try {
                for (int i = 0; i < 4; i++) nextTetrisPane.getChildren().removeFirst();
            } catch (Exception ignored) {
            }
            for (int i = 0; i < 4; i++) nextTetrisPane.getChildren().add(tetris[choix][i]);

            for (int i = 0; i < 4; i++) {
                futureCases[i].setX(futureCases[i].getX() + 20 * 5);
                futureCases[i].setY(futureCases[i].getY() - 40);
            }
        }
    }
//-----------------++++++++++++++++++++--------------------+++++++++++++++++++++

    void desativerBoutons() {
        try {
            if (!disable) {
                for (Button button1 : Arrays.asList(newGameButton, leaveProgramButton, pauseGameButton, aboutButton, hightScoreButton))
                    button1.setDisable(true);
                disable = true;
                pane.requestFocus();
            } else {
                for (Button button1 : Arrays.asList(newGameButton, leaveProgramButton, pauseGameButton, aboutButton, hightScoreButton))
                    button1.setDisable(false);
                newGameButton.requestFocus();
                disable = false;
            }
        } catch (Exception ignored) {
        }
    }

    //-----------------++++++++++++++++++++--------------------+++++++++++++++++++++
    public void supprimer() {
        if (!gameOver) {
            int minY = -1;
            int compteur = 1;
            int nbrLine = 0;
            for (int i = 0; i < cases.size(); i++) {
                if (minY == (int) cases.get(i).getY()) ++compteur;
                else {
                    minY = (int) cases.get(i).getY();
                    compteur = 1;
                }
                if (compteur == 12) {


                    while (compteur != 0) {
                        Rectangle rect = cases.get(i);
                        pane.getChildren().remove(rect);
                        cases.remove(rect);
                        for (Rectangle rect1 : cases)
                            if (rect1.getX() == rect.getX() && rect1.getY() < rect.getY())
                                rect1.setY(rect1.getY() + 20);
                        --i;
                        --compteur;
                    }
                    compteur = 1;
                    ++nbrLine;
                }
            }
            scoreLabel.setText(String.valueOf(Integer.parseInt(scoreLabel.getText()) + level * nbrLine * 10));
            if (nbrLine > 1) scoreLabel.setText(String.valueOf(Integer.parseInt(scoreLabel.getText()) + nbrLine * 2));
            lineLabel.setText(String.valueOf(Integer.parseInt(lineLabel.getText()) + nbrLine));
        }
    }

    //-----------------++++++++++++++++++++--------------------+++++++++++++++++++++
    public void saveScore() {
        int score = Integer.parseInt(scoreLabel.getText());
        StringBuilder finalresult = new StringBuilder();
        boolean heexist = false;
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader("src\\main\\resources\\tetrix\\tetrix\\scoresHistory.txt"));
        } catch (FileNotFoundException ignored) {
        }
        String read;
        try {
            assert reader != null;
            while ((read = reader.readLine()) != null) {
                String[] result = read.split("///");
                String name1 = result[0].trim();
                int oldScore = Integer.parseInt(result[1]);
                if (name.equalsIgnoreCase(name1)) {
                    heexist = true;
                    if (oldScore < score) read = result[0].concat("///" + score);
                }
                finalresult.append(read.concat("\n"));
            }
        } catch (IOException ignored) {
        }
        if (!heexist) finalresult.append(name.concat("///" + score));

        FileWriter pen;
        try {
            pen = new FileWriter("src\\main\\resources\\tetrix\\tetrix\\scoresHistory.txt");
            pen.write(finalresult.toString());
            pen.close();
        } catch (IOException ignored) {
        }
    }

    //-----------------++++++++++++++++++++--------------------+++++++++++++++++++++
    public static Rectangle sentDetails() {
        Rectangle rectangle = new Rectangle();
        rectangle.setWidth(20);
        rectangle.setHeight(20);
        rectangle.setStroke(Color.BLACK);
        rectangle.setStrokeWidth(1);
        return rectangle;
    }

    //-----------------++++++++++++++++++++--------------------+++++++++++++++++++++
    public Rectangle[] cloner(Rectangle[] tabRectangles) {
        int n = tabRectangles.length;
        Rectangle[] tabRectangles1 = new Rectangle[n];
        for (int i = 0; i < n; i++) {
            tabRectangles1[i] = sentDetails();
            tabRectangles1[i].setX(tabRectangles[i].getX());
            tabRectangles1[i].setY(tabRectangles[i].getY());
            tabRectangles1[i].setFill(tabRectangles[i].getFill());
        }
        return tabRectangles1;
    }

    //-----------------++++++++++++++++++++--------------------+++++++++++++++++++++
    private boolean peutDescendre() {
        for (int i = 0; i < 4; i++) {
            if (casesEnCours[i].getY() + 20 == 480) return false;
            for (Rectangle rectangle : cases)
                if (rectangle.getY() == casesEnCours[i].getY() + 20 && rectangle.getX() == casesEnCours[i].getX())
                    return false;
        }
        return true;

    }

    //-----------------++++++++++++++++++++--------------------+++++++++++++++++++++
//-----------------++++++++++++++++++++ Début--------------------+++++++++++++++++++++
// -----------------++++++++++++++++++++--------------------+++++++++++++++++++++
    public void lancer() {
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                descendre();
                if (gameOver) timer.cancel();
            }
        }, 0, levelTime);
        timer2.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(() -> timeLabel.setText(Duration.between(start, Instant.now()).getSeconds() + pauseTime + " s"));
                if (gameOver) timer2.cancel();
            }
        }, 0, 1000);

    }

    //-----------------++++++++++++++++++++--------------------+++++++++++++++++++++
    public static void main(String[] args) {
        launch();
    }
}


