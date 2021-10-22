package GUI;

import algorithms.*;
import javafx.animation.*;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.scene.text.Font;
import javafx.util.converter.IntegerStringConverter;
import map.Arena;
import map.MapConstants;
import map.MapConstants.IMAGE_DIRECTION;
import map.PictureObstacle;
import robot.Robot;
import robot.RobotConstants;

import java.awt.*;
import java.util.ArrayList;
import java.util.function.UnaryOperator;

/**
 * Algorithm simulator with javaFX
 */
public class Simulator extends Application {
    private final int dim = MapConstants.ARENA_WIDTH;
    private final int scale = ViewConstants.SCALE;
    private final int arenaSize = dim * scale;
    private final int gridSize = arenaSize / ((MapConstants.ARENA_WIDTH / MapConstants.OBSTACLE_WIDTH) + MapConstants.ARENA_BORDER_SIZE * 2);
    private ArrayList<Obstacle> obsList = new ArrayList<>();

    private Timeline timeline;
    private double timeSeconds = 0;

    private static Robot bot;
    private static FastestPathAlgo fast;
    private static TripPlannerAlgo algo;

    private static Arena arena = null;

    @Override
    public void start(Stage primaryStage) throws Exception {
        // arena section
        // graphics context
        bot = new Robot(RobotConstants.ROBOT_INITIAL_CENTER_COORDINATES, RobotConstants.ROBOT_DIRECTION.NORTH, false);
        arena = new Arena(bot);
        fast = new FastestPathAlgo(arena);
        algo = new TripPlannerAlgo(arena);

        Pane arenaPane = new Pane();
        arenaPane.setMinWidth(arenaSize);
        arenaPane.setMinHeight(arenaSize);
        arenaPane.setBackground(new Background(new BackgroundFill(drawGridLines(), new CornerRadii(0), null)));
        // draw starting position
        Rectangle start = new Rectangle(1, 17 * gridSize, 3 * gridSize, 3 * gridSize);
        start.setFill(Color.GRAY);
        arenaPane.getChildren().add(start);
        // draw robot
        Rectangle robot = new Rectangle(0, 0, 23 * scale, 20 * scale);
        Point robotCoords = RobotConstants.ROBOT_INITIAL_CENTER_COORDINATES;
        robot.setX(robotCoords.getX() * gridSize - robot.getWidth() / 4);
        robot.setY(robotCoords.getY() * gridSize - robot.getHeight() / 4);
        System.out.println(robot.getX());
        Stop[] stops = new Stop[]{new Stop(0, Color.BLACK), new Stop(1, Color.RED)};
        LinearGradient lg1 = new LinearGradient(0.7, 0, 1, 0, true, CycleMethod.NO_CYCLE, stops);
        robot.setFill(lg1);
        robot.setStrokeWidth(20);
        robot.setRotate(-90);
        arenaPane.getChildren().addAll(robot);

        // shortest path label
        Label shortestPathLabel = new Label("Shortest path: ");
        Label timerLabel = new Label("Time: ");

        // input fields
        Label xLabel = new Label("X Pos:");
        Label yLabel = new Label("Y Pos:");
        Label dirLabel = new Label("Direction:");

        TextField xField = new TextField();
        TextField yField = new TextField();

        UnaryOperator<TextFormatter.Change> integerFilter = change -> {
            String newText = change.getControlNewText();
            if (newText.matches("-?([0-9][0-9]*)?")) {
                return change;
            }
            return null;
        };

        xField.setTextFormatter(new TextFormatter<Integer>(new IntegerStringConverter(), 0, integerFilter));
        yField.setTextFormatter(new TextFormatter<Integer>(new IntegerStringConverter(), 0, integerFilter));


        ObservableList<String> options = FXCollections.observableArrayList(
                "North", "South", "East", "West");
        ComboBox directionBox = new ComboBox(options);
        directionBox.getSelectionModel().selectFirst();

        // buttons
        Button obstacleButton = new Button("Add Obstacle");
        EventHandler<ActionEvent> addObstacle = new EventHandler<ActionEvent>() {
            public void handle(ActionEvent e) {
                String dir = (String) directionBox.getValue();
                System.out.println(Integer.parseInt(xField.getText()));
                addObstacle(arenaPane, Integer.parseInt(xField.getText()), Integer.parseInt(yField.getText()), IMAGE_DIRECTION.valueOf(dir.toUpperCase()));
            }
        };
        obstacleButton.setOnAction(addObstacle);


        Button simulateButton = new Button("Run Simulation");
        EventHandler<ActionEvent> runSimulation = new EventHandler<ActionEvent>() {
            public void handle(ActionEvent e) {
                if (timeline != null) {
                    timeline.stop();
                }
                timeSeconds = 0;

                // update timerLabel
                timerLabel.setText("Time: " + Double.toString(Math.round(timeSeconds * 100.0) / 100.0));
                timeline = new Timeline();
                timeline.setCycleCount(Timeline.INDEFINITE);
                timeline.getKeyFrames().add(
                        new KeyFrame(Duration.millis(1),
                                new EventHandler<ActionEvent>() {
                                    // KeyFrame event handler
                                    public void handle(ActionEvent event) {
                                        timeSeconds += .001;
                                        // update timerLabel
                                        timerLabel.setText("Time: " + Double.toString(
                                                Math.round(timeSeconds * 100.0) / 100.0));
                                        if (timeSeconds >= 360) {
                                            timeline.stop();
                                        }
                                    }
                                }));
                timeline.playFromStart();
                runSimulation(shortestPathLabel, robot, timeline);
            }
        };

        simulateButton.setOnAction(runSimulation);


        GridPane buttonBar = new GridPane();
        buttonBar.setAlignment(Pos.CENTER);
        buttonBar.add(xLabel, 0, 0);
        buttonBar.add(yLabel, 1, 0);
        buttonBar.add(dirLabel, 2, 0);
        buttonBar.add(xField, 0, 1);
        buttonBar.add(yField, 1, 1);
        buttonBar.add(directionBox, 2, 1);
        buttonBar.add(obstacleButton, 0, 2, 3, 1);
        buttonBar.add(simulateButton, 3, 2, 3, 1);
        obstacleButton.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        simulateButton.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        ColumnConstraints cc = new ColumnConstraints();
        cc.setPercentWidth(15);
        buttonBar.getColumnConstraints().addAll(cc, cc, cc, cc);
        buttonBar.setMinWidth(arenaSize);
        buttonBar.setMinHeight(100);

        // place arena and control panel into vertical box
        VBox vbox = new VBox(arenaPane, shortestPathLabel, timerLabel, buttonBar);

        // pack everything into the stage
        primaryStage.setTitle("Simulator");
        primaryStage.setScene(new Scene(vbox, arenaSize, arenaSize + 150));
        primaryStage.setResizable(false);
        primaryStage.show();

    }

    public void runSimulation(Label label, Rectangle robot, Timeline timeline) {
        ArrayList<ArrayList<MoveType>> moveList = new ArrayList<>();
        ArrayList<PictureObstacle> pictureList = Arena.getObstacles();
        SequentialTransition seqT = new SequentialTransition();

        algo.constructMap();

        // first, get the shortest path.
        int[] fastestPath = fast.planFastestPath();
        String text = "Shortest path: ";
        int[] startCoords = new int[3];
        startCoords[0] = bot.getX();
        startCoords[1] = bot.getY();
        startCoords[2] = bot.getRobotDirectionAngle();
        PictureObstacle n;
        for (int i : fastestPath) {
            n = pictureList.get(i);
            text += "<" + n.getX() + ", " + n.getY() + ">, ";
            moveList.add(algo.planPath(startCoords[0], startCoords[1], startCoords[2], n.getX(), n.getY(), n.getImadeDirectionAngle(), true, true, true));
            startCoords = algo.getEndPosition();
        }

        label.setText(text);
        seqT = getPathAnimation(robot, moveList);
        seqT.play();
        seqT.setOnFinished(e -> timeline.stop());
    }

    /**
     * Redraw the arena
     */
    private ImagePattern drawGridLines() {
        // draw the grid lines first
        Canvas canvas = new Canvas(gridSize, gridSize); // for drawing

        GraphicsContext gc =
                canvas.getGraphicsContext2D();

        gc.setStroke(Color.BLACK);
        gc.strokeRect(0.5, 0.5, gridSize, gridSize);
        gc.setFill(Color.WHITE.deriveColor(1, 1, 1, 0.2));
        gc.fillRect(0, 0, gridSize, gridSize);
        gc.strokeRect(0.5, 0.5, gridSize, gridSize);

        Image image = canvas.snapshot(new SnapshotParameters(), null);
        ImagePattern pattern = new ImagePattern(image, 0, 0, gridSize, gridSize, false);

        gc.setFill(pattern);

        return pattern;

    }

    private SequentialTransition getPathAnimation(Rectangle robot, ArrayList<ArrayList<MoveType>> pathList) {
        //Creating a Path
        SequentialTransition seqT = new SequentialTransition();
        double radiusY, radiusX;
        double nextX;
        double nextY;
        double duration;
        int endDir;

        ArrayList<MoveType> paths;
        int len = pathList.size();
        for (int i = 0; i < len; i++) {
            paths = pathList.get(i);
            if (paths == null) { // handle unreachable pictures
                PauseTransition pauseTransition = new PauseTransition(Duration.millis(1));
                seqT.getChildren().add(pauseTransition);
                continue;
            }
            nextX = paths.get(0).getX1() * scale;
            nextY = paths.get(0).getY1() * scale;
            for (MoveType move : paths) {
                Path path = new Path();
                PathTransition pathTransition = new PathTransition();
                pathTransition.setNode(robot);
                pathTransition.setOrientation(
                        PathTransition.OrientationType.ORTHOGONAL_TO_TANGENT);
                pathTransition.setCycleCount(0);
                pathTransition.setAutoReverse(false);
                duration = move.getLength() / RobotConstants.MOVE_SPEED;
                pathTransition.setDuration(Duration.millis(duration * 1000));
                pathTransition.setInterpolator(Interpolator.LINEAR);

                if (move.isLine()) { // moving straight
                    if (move.isReverse()) {
                        pathTransition.setInterpolator(ReverseInterpolator.reverse(Interpolator.LINEAR));
                        MoveTo moveTo = new MoveTo(move.getX2() * scale, move.getY2() * scale);
                        path.getElements().add(moveTo);
                        LineTo line = new LineTo(nextX, nextY);
                        path.getElements().add(line);
                    } else {
                        MoveTo moveTo = new MoveTo(nextX, nextY);
                        path.getElements().add(moveTo);
                        LineTo line = new LineTo(move.getX2() * scale, move.getY2() * scale);
                        path.getElements().add(line);
                    }
                    pathTransition.setPath(path);
                } else { // its a turn
                    MoveTo moveTo = new MoveTo(nextX, nextY);
                    path.getElements().add(moveTo);
                    endDir = move.getDirInDegrees();
                    ArcTo turn = new ArcTo();
                    ArcMove arc = (ArcMove) move;
                    radiusY = arc.getRadiusY() * scale;
                    radiusX = arc.getRadiusX() * scale;

                    turn.setX(move.getX2() * scale);
                    turn.setY(move.getY2() * scale);
                    if (arc.isTurnLeft()) {
                        turn.setSweepFlag(false);
                        if (endDir == 90 || endDir == 270) {
                            turn.setRadiusX(radiusX);
                            turn.setRadiusY(radiusY);
                        } else {
                            turn.setRadiusX(radiusY);
                            turn.setRadiusY(radiusX);
                        }
                    } else {
                        turn.setSweepFlag(true);
                        if (endDir == 180 || endDir == 0) {
                            turn.setRadiusX(radiusY);
                            turn.setRadiusY(radiusX);
                        } else {
                            turn.setRadiusX(radiusX);
                            turn.setRadiusY(radiusY);
                        }
                    }
                    path.getElements().add(turn);
                    pathTransition.setPath(path);
                }
                nextX = move.getX2() * scale;
                nextY = move.getY2() * scale;
                seqT.getChildren().add(pathTransition);
            }
            if (i < len - 1) { // not the last path, so we perform a reverse
                PauseTransition pauseTransition = new PauseTransition(Duration.millis(2000));
                seqT.getChildren().add(pauseTransition);
            }
        }
        return seqT;
    }

    private void addObstacle(Pane arenaPane, int x, int y, IMAGE_DIRECTION dir) {
        boolean success = arena.addPictureObstacle(x, y, dir);
        if (success) {
            Obstacle obs = new Obstacle(x, y, dir);
            obs.addToPane(arenaPane);
            obsList.add(obs);
        }
    }

    private class Obstacle {
        Rectangle obstacle;
        Rectangle indicator;
        Label idLabel;

        //StackPane stack;
        public Obstacle(int x, int y, IMAGE_DIRECTION dir) {
            int xPos = x * gridSize;
            int yPos = y * gridSize;

            obstacle = new Rectangle(xPos, yPos, gridSize, gridSize);
            obstacle.setFill(ViewConstants.OBSTACLE_COLOR);
            switch (dir) {
                case NORTH:
                    indicator = new Rectangle(xPos, yPos, gridSize, gridSize / 10);
                    break;
                case SOUTH:
                    indicator = new Rectangle(xPos, yPos + (gridSize - gridSize / 10), gridSize, gridSize / 10);
                    break;
                case WEST:
                    indicator = new Rectangle(xPos, yPos, gridSize / 10, gridSize);
                    break;
                case EAST:
                    indicator = new Rectangle(xPos + (gridSize - gridSize / 10), yPos, gridSize / 10, gridSize);
                    break;
                default: // ???
                    indicator = null;
            }
            indicator.setFill(ViewConstants.IMAGE_INDICATOR_COLOR);
            idLabel = new Label(String.valueOf(obsList.size() + 1));
            idLabel.setAlignment(Pos.CENTER);
            idLabel.setFont(new Font(5 * scale));
            idLabel.setTextFill(ViewConstants.OBSTACLE_TEXT_COLOR);
            idLabel.setTranslateX(xPos + (gridSize / 4));
            idLabel.setTranslateY(yPos + (gridSize / 4));
        }

        public void setText(String text) {
            idLabel.setText(text);
        }

        public void addToPane(Pane pane) {
            pane.getChildren().addAll(obstacle, indicator, idLabel);
        }

        public void removeFromPane(Pane pane) {
            pane.getChildren().removeAll(obstacle, indicator, idLabel);
        }
    }


    public static void main(String[] args) {
        launch(args);
    }
}
