package sample;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.geometry.Point2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Game extends Application {

    //region vars
    private Map<KeyCode, Boolean> keys = new HashMap<>();
    private List<Node> platforms = new ArrayList<>();
    private Node player;
    private Point2D playerVelocity = new Point2D(0.0, 0.0);

    private Pane appRoot = new Pane();
    private Pane gameRoot = new Pane();
    private Pane uiRoot = new Pane();

    private boolean canJump = true;
    private int levelWidth;
    private Text text;
    private boolean fellOffScreen = false;
    //endregion

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {

        initContent();

        Scene scene = new Scene(appRoot);

        scene.setOnKeyPressed(event -> keys.put(event.getCode(), true));
        scene.setOnKeyReleased(event -> keys.put(event.getCode(), false));

        primaryStage.setTitle("Platform jumper");
        primaryStage.setScene(scene);
        primaryStage.show();

        AnimationTimer gameLoop = new AnimationTimer() {
            @Override
            public void handle(long now) {
                update();
            }
        };
        gameLoop.start();
    }

    private void initContent() {
        Rectangle bg = new Rectangle(1280, 720);
        levelWidth = LevelData.LEVEL1[0].length() * 60;

        for(int i = 0; i<LevelData.LEVEL1.length; i++) {
            String line = LevelData.LEVEL1[i];
            for(int j = 0; j < line.length(); j++) {
                switch (line.charAt(j)) {
                    case '0':
                        break;
                    case '1':
                        Node platform = getRectangle(j * 60, i * 60, 60, 60, Color.BROWN);
                        platforms.add(platform);
                        break;
                }
            }
        }

        player = getRectangle(0, 600, 40, 40, Color.BLUE);

        player.translateXProperty().addListener((obs, old, newValue) -> {
            int offset = newValue.intValue();

            if (offset > 640 && offset < levelWidth - 640) {
                gameRoot.setLayoutX(-(offset-640));
            }
        });

        appRoot.getChildren().addAll(bg, gameRoot, uiRoot);
    }

    private void update() {

        if (isKeyPressed(KeyCode.W) && player.getTranslateY() >= 5) {
            jump();
        }
        if (isKeyPressed(KeyCode.A) && player.getTranslateY() >= 5) {
            moveX(-5);
        }
        if (isKeyPressed(KeyCode.D) && player.getTranslateX() + 40 <= levelWidth - 5) {
            moveX(5);
        }

        if (playerVelocity.getY() < 10) {
            playerVelocity = playerVelocity.add(0.0,1.0);
        }

        moveY((int)playerVelocity.getY());

    }

    private Rectangle getRectangle(int x, int y, int w, int h, Color color) {

        final Rectangle rectangle = new Rectangle(w, h);
        rectangle.setTranslateX(x);
        rectangle.setTranslateY(y);
        rectangle.setFill(color);

        gameRoot.getChildren().add(rectangle);
        return rectangle;
    }

    private boolean isKeyPressed(KeyCode key) {
        return keys.getOrDefault(key, false);
    }

    private void jump() {
        if(canJump) {
            playerVelocity = playerVelocity.add(0.0, -30.0);
            canJump = false;
        }
    }

    private void moveX(int value) {
        boolean movingRight = value > 0;

        for (int i = 0; i < Math.abs(value); i++) {
            for (Node platform : platforms) {
                if (player.getBoundsInParent().intersects(platform.getBoundsInParent())){
                    if (movingRight) {
                        if (player.getTranslateX() + 40 == platform.getTranslateX()) {
                            return;
                        }
                    } else {
                        if (player.getTranslateX() == platform.getTranslateX() + 60) {
                            return;
                        }
                    }
                }
            }
            player.setTranslateX(player.getTranslateX() + (movingRight ? 1: -1));
        }
    }

    private void moveY(int value) {
        boolean movingDown = value > 0;

        for (int i = 0; i < Math.abs(value); i++) {
            for (Node platform : platforms) {
                if (player.getBoundsInParent().intersects(platform.getBoundsInParent())){
                    if (movingDown) {
                        if (player.getTranslateY() + 40 == platform.getTranslateY()) {
                            player.setTranslateY(player.getTranslateY() - 1);
                            canJump = true;
                            return;
                        }
                    } else {
                        if (player.getTranslateY() == platform.getTranslateY() + 60) {
                            return;
                        }
                    }
                }
            }
            player.setTranslateY(player.getTranslateY() + (movingDown ? 1: -1));
        }
    }


}
