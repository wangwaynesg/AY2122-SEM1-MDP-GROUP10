package com.example.mdptest;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Path;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.navigation.fragment.NavHostFragment;

import com.example.mdptest.databinding.FragmentSecondBinding;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

public class SecondFragment extends Fragment {

    // Wayne
    private FragmentSecondBinding binding;

    private static final int SNAP_GRID_INTERVAL = 40;

    private static final int ANIMATOR_DURATION = 1000;

    private boolean isObstacle1LongClicked = false;
    private boolean isObstacle2LongClicked = false;
    private boolean isObstacle3LongClicked = false;
    private boolean isObstacle4LongClicked = false;
    private boolean isObstacle5LongClicked = false;

    private int sequence = 0;

    Button sendObstaclesButton;

    Button forwardButton;
    Button turnLeftButton;
    Button turnRightButton;
    Button reverseButton;

    ImageView obstacle1, obstacle2, obstacle3, obstacle4, obstacle5;
    ImageView car;

    Map<Integer, ImageView> obstacles;

    Map<String, String> commands = new HashMap<String, String>() {{
        put("forward", "0100");
        put("reverse", "0101");
        put("turnLeft", "0902");
        put("turnRight", "0903");
    }};

    Map<String, Integer> resources = new HashMap<String, Integer>() {{
        put("o1n", R.drawable.obstacle_1_n);
        put("o1e", R.drawable.obstacle_1_e);
        put("o1s", R.drawable.obstacle_1_s);
        put("o1w", R.drawable.obstacle_1_w);

        put("o2n", R.drawable.obstacle_2_n);
        put("o2e", R.drawable.obstacle_2_e);
        put("o2s", R.drawable.obstacle_2_s);
        put("o2w", R.drawable.obstacle_2_w);

        put("o3n", R.drawable.obstacle_3_n);
        put("o3e", R.drawable.obstacle_3_e);
        put("o3s", R.drawable.obstacle_3_s);
        put("o3w", R.drawable.obstacle_3_w);

        put("o4n", R.drawable.obstacle_4_n);
        put("o4e", R.drawable.obstacle_4_e);
        put("o4s", R.drawable.obstacle_4_s);
        put("o4w", R.drawable.obstacle_4_w);

        put("o5n", R.drawable.obstacle_5_n);
        put("o5e", R.drawable.obstacle_5_e);
        put("o5s", R.drawable.obstacle_5_s);
        put("o5w", R.drawable.obstacle_5_w);

        put("1", R.drawable.number_1);
        put("2", R.drawable.number_2);
        put("3", R.drawable.number_3);
        put("4", R.drawable.number_4);
        put("5", R.drawable.number_5);
        put("6", R.drawable.number_6);
        put("7", R.drawable.number_7);
        put("8", R.drawable.number_8);
        put("9", R.drawable.number_9);

        put("A", R.drawable.alphabet_a);
        put("B", R.drawable.alphabet_b);
        put("C", R.drawable.alphabet_c);
        put("D", R.drawable.alphabet_d);
        put("E", R.drawable.alphabet_e);
        put("F", R.drawable.alphabet_f);
        put("G", R.drawable.alphabet_g);
        put("H", R.drawable.alphabet_h);
        put("S", R.drawable.alphabet_s);
        put("T", R.drawable.alphabet_t);
        put("U", R.drawable.alphabet_u);
        put("V", R.drawable.alphabet_v);
        put("W", R.drawable.alphabet_w);
        put("X", R.drawable.alphabet_x);
        put("Y", R.drawable.alphabet_y);
        put("Z", R.drawable.alphabet_z);

        put("up", R.drawable.arrow_up);
        put("down", R.drawable.arrow_down);
        put("left", R.drawable.arrow_left);
        put("right", R.drawable.arrow_right);

        put("bulls", R.drawable.bullseye);
        put("circle", R.drawable.circle);
    }};

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        binding = FragmentSecondBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    @SuppressLint("ClickableViewAccessibility")
    public void onViewCreated(@NonNull View f, Bundle savedInstanceState) {
        super.onViewCreated(f, savedInstanceState);

        binding.buttonSecond.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavHostFragment.findNavController(SecondFragment.this)
                        .navigate(R.id.action_SecondFragment_to_FirstFragment);
            }
        });

        // Register Broadcast Receiver for incoming message
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(myReceiver, new IntentFilter("IncomingMsg"));

        obstacle1 = getActivity().findViewById(R.id.obstacle1);
        obstacle2 = getActivity().findViewById(R.id.obstacle2);
        obstacle3 = getActivity().findViewById(R.id.obstacle3);
        obstacle4 = getActivity().findViewById(R.id.obstacle4);
        obstacle5 = getActivity().findViewById(R.id.obstacle5);

        obstacles = new HashMap<Integer, ImageView>() {{
            put(1, obstacle1);
            put(2, obstacle2);
            put(3, obstacle3);
            put(4, obstacle4);
            put(5, obstacle5);
        }};

        obstacle1.setOnClickListener(view -> {
            obstacle1.setRotation((obstacle1.getRotation() + 90) % 360);
            int orientation = (int) obstacle1.getRotation();
            switch (((orientation / 90) % 4 + 4) % 4) {
                case 0:
                    obstacle1.setImageResource(resources.get("o1n"));
                    break;
                case 1:
                    obstacle1.setImageResource(resources.get("o1e"));
                    break;
                case 2:
                    obstacle1.setImageResource(resources.get("o1s"));
                    break;
                case 3:
                    obstacle1.setImageResource(resources.get("o1w"));
                    break;
                default:
                    // Shouldn't reach this case
                    break;
            }
        });

        obstacle2.setOnClickListener(view -> {
            obstacle2.setRotation((obstacle2.getRotation() + 90) % 360);
            int orientation = (int) obstacle2.getRotation();
            switch (((orientation / 90) % 4 + 4) % 4) {
                case 0:
                    obstacle2.setImageResource(resources.get("o2n"));
                    break;
                case 1:
                    obstacle2.setImageResource(resources.get("o2e"));
                    break;
                case 2:
                    obstacle2.setImageResource(resources.get("o2s"));
                    break;
                case 3:
                    obstacle2.setImageResource(resources.get("o2w"));
                    break;
                default:
                    // Shouldn't reach this case
                    break;
            }
        });

        obstacle3.setOnClickListener(view -> {
            obstacle3.setRotation((obstacle3.getRotation() + 90) % 360);
            int orientation = (int) obstacle3.getRotation();
            switch (((orientation / 90) % 4 + 4) % 4) {
                case 0:
                    obstacle3.setImageResource(resources.get("o3n"));
                    break;
                case 1:
                    obstacle3.setImageResource(resources.get("o3e"));
                    break;
                case 2:
                    obstacle3.setImageResource(resources.get("o3s"));
                    break;
                case 3:
                    obstacle3.setImageResource(resources.get("o3w"));
                    break;
                default:
                    // Shouldn't reach this case
                    break;
            }
        });

        obstacle4.setOnClickListener(view -> {
            obstacle4.setRotation((obstacle4.getRotation() + 90) % 360);
            int orientation = (int) obstacle4.getRotation();
            switch (((orientation / 90) % 4 + 4) % 4) {
                case 0:
                    obstacle4.setImageResource(resources.get("o4n"));
                    break;
                case 1:
                    obstacle4.setImageResource(resources.get("o4e"));
                    break;
                case 2:
                    obstacle4.setImageResource(resources.get("o4s"));
                    break;
                case 3:
                    obstacle4.setImageResource(resources.get("o4w"));
                    break;
                default:
                    // Shouldn't reach this case
                    break;
            }
        });

        obstacle5.setOnClickListener(view -> {
            obstacle5.setRotation((obstacle5.getRotation() + 90) % 360);
            int orientation = (int) obstacle5.getRotation();
            switch (((orientation / 90) % 4 + 4) % 4) {
                case 0:
                    obstacle5.setImageResource(resources.get("o5n"));
                    break;
                case 1:
                    obstacle5.setImageResource(resources.get("o5e"));
                    break;
                case 2:
                    obstacle5.setImageResource(resources.get("o5s"));
                    break;
                case 3:
                    obstacle5.setImageResource(resources.get("o5w"));
                    break;
                default:
                    // Shouldn't reach this case
                    break;
            }
        });

        obstacle1.setOnLongClickListener(view -> {
            isObstacle1LongClicked = true;
            // Test if returning true instead might fix the image flying off
            return false;
        });

        obstacle2.setOnLongClickListener(view -> {
            isObstacle2LongClicked = true;
            return false;
        });

        obstacle3.setOnLongClickListener(view -> {
            isObstacle3LongClicked = true;
            return false;
        });

        obstacle4.setOnLongClickListener(view -> {
            isObstacle4LongClicked = true;
            return false;
        });

        obstacle5.setOnLongClickListener(view -> {
            isObstacle5LongClicked = true;
            return false;
        });

        obstacle1.setOnTouchListener(new View.OnTouchListener() {
            int x = 0;
            int y = 0;
            int dx = 0;
            int dy = 0;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (!isObstacle1LongClicked) {
                    return false;
                }
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        x = (int) event.getX();
                        y = (int) event.getY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        dx = (int) event.getX() - x;
                        dy = (int) event.getY() - y;

                        obstacle1.setX(obstacle1.getX() + dx);
                        obstacle1.setY(obstacle1.getY() + dy);
                        break;
                    case MotionEvent.ACTION_UP:
                        int snapToX = ((int) ((obstacle1.getX() + SNAP_GRID_INTERVAL / 2) / SNAP_GRID_INTERVAL)) * SNAP_GRID_INTERVAL;
                        int snapToY = ((int) ((obstacle1.getY() + SNAP_GRID_INTERVAL / 2) / SNAP_GRID_INTERVAL)) * SNAP_GRID_INTERVAL;
                        obstacle1.setX(snapToX);
                        obstacle1.setY(snapToY);
                        isObstacle1LongClicked = false;
                        break;
                    default:
                        break;
                }
                return false;
            }
        });

        obstacle2.setOnTouchListener(new View.OnTouchListener() {
            int x = 0;
            int y = 0;
            int dx = 0;
            int dy = 0;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (!isObstacle2LongClicked) {
                    return false;
                }
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        x = (int) event.getX();
                        y = (int) event.getY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        dx = (int) event.getX() - x;
                        dy = (int) event.getY() - y;

                        obstacle2.setX(obstacle2.getX() + dx);
                        obstacle2.setY(obstacle2.getY() + dy);
                        break;
                    case MotionEvent.ACTION_UP:
                        int snapToX = ((int) ((obstacle2.getX() + SNAP_GRID_INTERVAL / 2) / SNAP_GRID_INTERVAL)) * SNAP_GRID_INTERVAL;
                        int snapToY = ((int) ((obstacle2.getY() + SNAP_GRID_INTERVAL / 2) / SNAP_GRID_INTERVAL)) * SNAP_GRID_INTERVAL;
                        obstacle2.setX(snapToX);
                        obstacle2.setY(snapToY);
                        isObstacle2LongClicked = false;
                        break;
                    default:
                        break;
                }
                return false;
            }
        });

        obstacle3.setOnTouchListener(new View.OnTouchListener() {
            int x = 0;
            int y = 0;
            int dx = 0;
            int dy = 0;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (!isObstacle3LongClicked) {
                    return false;
                }
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        x = (int) event.getX();
                        y = (int) event.getY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        dx = (int) event.getX() - x;
                        dy = (int) event.getY() - y;

                        obstacle3.setX(obstacle3.getX() + dx);
                        obstacle3.setY(obstacle3.getY() + dy);
                        break;
                    case MotionEvent.ACTION_UP:
                        int snapToX = ((int) ((obstacle3.getX() + SNAP_GRID_INTERVAL / 2) / SNAP_GRID_INTERVAL)) * SNAP_GRID_INTERVAL;
                        int snapToY = ((int) ((obstacle3.getY() + SNAP_GRID_INTERVAL / 2) / SNAP_GRID_INTERVAL)) * SNAP_GRID_INTERVAL;
                        obstacle3.setX(snapToX);
                        obstacle3.setY(snapToY);
                        isObstacle3LongClicked = false;
                        break;
                    default:
                        break;
                }
                return false;
            }
        });

        obstacle4.setOnTouchListener(new View.OnTouchListener() {
            int x = 0;
            int y = 0;
            int dx = 0;
            int dy = 0;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (!isObstacle4LongClicked) {
                    return false;
                }
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        x = (int) event.getX();
                        y = (int) event.getY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        dx = (int) event.getX() - x;
                        dy = (int) event.getY() - y;

                        obstacle4.setX(obstacle4.getX() + dx);
                        obstacle4.setY(obstacle4.getY() + dy);
                        break;
                    case MotionEvent.ACTION_UP:
                        int snapToX = ((int) ((obstacle4.getX() + SNAP_GRID_INTERVAL / 2) / SNAP_GRID_INTERVAL)) * SNAP_GRID_INTERVAL;
                        int snapToY = ((int) ((obstacle4.getY() + SNAP_GRID_INTERVAL / 2) / SNAP_GRID_INTERVAL)) * SNAP_GRID_INTERVAL;
                        obstacle4.setX(snapToX);
                        obstacle4.setY(snapToY);
                        isObstacle4LongClicked = false;
                        break;
                    default:
                        break;
                }
                return false;
            }
        });

        obstacle5.setOnTouchListener(new View.OnTouchListener() {
            int x = 0;
            int y = 0;
            int dx = 0;
            int dy = 0;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (!isObstacle5LongClicked) {
                    return false;
                }
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        x = (int) event.getX();
                        y = (int) event.getY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        dx = (int) event.getX() - x;
                        dy = (int) event.getY() - y;

                        obstacle5.setX(obstacle5.getX() + dx);
                        obstacle5.setY(obstacle5.getY() + dy);
                        break;
                    case MotionEvent.ACTION_UP:
                        int snapToX = ((int) ((obstacle5.getX() + SNAP_GRID_INTERVAL / 2) / SNAP_GRID_INTERVAL)) * SNAP_GRID_INTERVAL;
                        int snapToY = ((int) ((obstacle5.getY() + SNAP_GRID_INTERVAL / 2) / SNAP_GRID_INTERVAL)) * SNAP_GRID_INTERVAL;
                        obstacle5.setX(snapToX);
                        obstacle5.setY(snapToY);
                        isObstacle5LongClicked = false;
                        break;
                    default:
                        break;
                }
                return false;
            }
        });

        // Buttons and Objects initialization
        sendObstaclesButton = getActivity().findViewById(R.id.sendObstaclesButton);
        car = getActivity().findViewById(R.id.car);
        forwardButton = getActivity().findViewById(R.id.forwardButton);
        turnRightButton = getActivity().findViewById(R.id.turnRightButton);
        turnLeftButton = getActivity().findViewById(R.id.turnLeftButton);
        reverseButton = getActivity().findViewById(R.id.reverseButton);

        // Set button click events
        sendObstaclesButton.setOnClickListener(view -> sendObstaclesEvent());
        car.setOnClickListener(view -> carClickEvent());
        forwardButton.setOnClickListener(view -> forwardButtonEvent());
        reverseButton.setOnClickListener(view -> reverseButtonEvent());
        turnRightButton.setOnClickListener(view -> turnRightButtonEvent());
        turnLeftButton.setOnClickListener(view -> turnLeftButtonEvent());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void sendObstaclesEvent() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder
                .append(getObstacleString(obstacle1)).append(',')
                .append(getObstacleString(obstacle2)).append(',')
                .append(getObstacleString(obstacle3)).append(',')
                .append(getObstacleString(obstacle4)).append(',')
                .append(getObstacleString(obstacle5));
        Log.d("Sending Obstacles",stringBuilder.toString());
        Toast.makeText(getActivity(), stringBuilder.toString(), Toast.LENGTH_LONG).show();

        byte[] bytes = stringBuilder.toString().getBytes(Charset.defaultCharset());
        BluetoothComms.writeMsg(bytes);
    }

    private String getObstacleString(ImageView obstacle) {
        return "O," +
                ((int) obstacle.getX() / 40) +
                ',' +
                ((int) obstacle.getY() / 40) +
                ',' +
                getImageOrientation(obstacle);
    }

    private String getImageOrientation(ImageView obstacle) {
        switch (((int) ((obstacle.getRotation() / 90) % 4 + 4) % 4)) {
            case 0:
                return "N";
            case 1:
                return "E";
            case 2:
                return "S";
            case 3:
                return "W";
            default:
                // Shouldn't reach this case
                return "X";
        }
    }

    private void carClickEvent() {
        String string = "Car (X: " + car.getX() + ") " +
                "(Y: " + car.getY() + ") " +
                "Facing: " + getImageOrientation(car);
        Toast.makeText(getActivity(), string, Toast.LENGTH_SHORT).show();

        // Testing values
        setObstacleImage(1, "3");
        setObstacleImage(2, "up");
        setObstacleImage(3, "2");
        setObstacleImage(4, "bulls");
        setObstacleImage(5, "Z");

        updateRobotPosition(5, 5, 'S');
    }

    private void forwardButtonEvent() {
        byte[] bytes = commands.get("forward").getBytes(Charset.defaultCharset());
        BluetoothComms.writeMsg(bytes);

        int orientation = (int) car.getRotation();
        ObjectAnimator animator;
        switch (((orientation / 90) % 4 + 4) % 4) {
            case 0:
                animator = ObjectAnimator.ofFloat(car, "y", car.getY() - SNAP_GRID_INTERVAL);
                animator.setDuration(ANIMATOR_DURATION);
                animator.start();
                break;
            case 1:
                animator = ObjectAnimator.ofFloat(car, "x", car.getX() + SNAP_GRID_INTERVAL);
                animator.setDuration(ANIMATOR_DURATION);
                animator.start();
                break;
            case 2:
                animator = ObjectAnimator.ofFloat(car, "y", car.getY() + SNAP_GRID_INTERVAL);
                animator.setDuration(ANIMATOR_DURATION);
                animator.start();
                break;
            case 3:
                animator = ObjectAnimator.ofFloat(car, "x", car.getX() - SNAP_GRID_INTERVAL);
                animator.setDuration(ANIMATOR_DURATION);
                animator.start();
                break;
            default:
                // Shouldn't reach this case
                break;
        }
    }

    private void reverseButtonEvent() {
        byte[] bytes = commands.get("reverse").getBytes(Charset.defaultCharset());
        BluetoothComms.writeMsg(bytes);

        int orientation = (int) car.getRotation();
        ObjectAnimator animator;
        switch (((orientation / 90) % 4 + 4) % 4) {
            case 0:
                animator = ObjectAnimator.ofFloat(car, "y", car.getY() + SNAP_GRID_INTERVAL);
                animator.setDuration(ANIMATOR_DURATION);
                animator.start();
                break;
            case 1:
                animator = ObjectAnimator.ofFloat(car, "x", car.getX() - SNAP_GRID_INTERVAL);
                animator.setDuration(ANIMATOR_DURATION);
                animator.start();
                break;
            case 2:
                animator = ObjectAnimator.ofFloat(car, "y", car.getY() - SNAP_GRID_INTERVAL);
                animator.setDuration(ANIMATOR_DURATION);
                animator.start();
                break;
            case 3:
                animator = ObjectAnimator.ofFloat(car, "x", car.getX() + SNAP_GRID_INTERVAL);
                animator.setDuration(ANIMATOR_DURATION);
                animator.start();
                break;
            default:
                // Shouldn't reach this case
                break;
        }
    }

    private void turnRightButtonEvent() {
        byte[] bytes = commands.get("turnRight").getBytes(Charset.defaultCharset());
        BluetoothComms.writeMsg(bytes);

        int orientation = (int) car.getRotation();

        ObjectAnimator animatorX;
        ObjectAnimator animatorY;
        ObjectAnimator animatorArc;
        ObjectAnimator rotateAnimator;
        AnimatorSet animatorSet = new AnimatorSet();
        Path path = new Path();

        switch (((orientation / 90) % 4 + 4) % 4) {
            case 0:
                animatorY = ObjectAnimator.ofFloat(car, "y", car.getY() - SNAP_GRID_INTERVAL);
                animatorY.setDuration(ANIMATOR_DURATION);

                path.arcTo(car.getX(),
                        car.getY() - SNAP_GRID_INTERVAL * 3,
                        car.getX() + SNAP_GRID_INTERVAL * 4,
                        car.getY() + SNAP_GRID_INTERVAL,
                        180f,
                        90f,
                        true);

                animatorArc = ObjectAnimator.ofFloat(car, View.X, View.Y, path);
                animatorArc.setDuration(ANIMATOR_DURATION);
                animatorArc.setStartDelay(ANIMATOR_DURATION);

                rotateAnimator = ObjectAnimator.ofFloat(car, "rotation", orientation, orientation + 90);
                rotateAnimator.setDuration(ANIMATOR_DURATION);
                rotateAnimator.setStartDelay(ANIMATOR_DURATION);

                animatorX = ObjectAnimator.ofFloat(car, "x", car.getX() + SNAP_GRID_INTERVAL * 3);
                animatorX.setDuration(ANIMATOR_DURATION);
                animatorX.setStartDelay(ANIMATOR_DURATION * 2);

                animatorSet.playTogether(animatorY, animatorArc, rotateAnimator, animatorX);
                animatorSet.start();
                break;
            case 1:
                animatorX = ObjectAnimator.ofFloat(car, "x", car.getX() + SNAP_GRID_INTERVAL);
                animatorX.setDuration(ANIMATOR_DURATION);

                path.arcTo(car.getX() - SNAP_GRID_INTERVAL,
                        car.getY(),
                        car.getX() + SNAP_GRID_INTERVAL * 3,
                        car.getY() + SNAP_GRID_INTERVAL * 4,
                        270f,
                        90f,
                        true);

                animatorArc = ObjectAnimator.ofFloat(car, View.X, View.Y, path);
                animatorArc.setDuration(ANIMATOR_DURATION);
                animatorArc.setStartDelay(ANIMATOR_DURATION);

                rotateAnimator = ObjectAnimator.ofFloat(car, "rotation", orientation, orientation + 90);
                rotateAnimator.setDuration(ANIMATOR_DURATION);
                rotateAnimator.setStartDelay(ANIMATOR_DURATION);

                animatorY = ObjectAnimator.ofFloat(car, "y", car.getY() + SNAP_GRID_INTERVAL * 3);
                animatorY.setDuration(ANIMATOR_DURATION);
                animatorY.setStartDelay(ANIMATOR_DURATION * 2);

                animatorSet.playTogether(animatorY, animatorArc, rotateAnimator, animatorX);
                animatorSet.start();
                break;
            case 2:
                animatorY = ObjectAnimator.ofFloat(car, "y", car.getY() + SNAP_GRID_INTERVAL);
                animatorY.setDuration(ANIMATOR_DURATION);

                path.arcTo(car.getX() - SNAP_GRID_INTERVAL * 4,
                        car.getY() + SNAP_GRID_INTERVAL,
                        car.getX(),
                        car.getY() + SNAP_GRID_INTERVAL * 3,
                        0f,
                        90f,
                        true);

                animatorArc = ObjectAnimator.ofFloat(car, View.X, View.Y, path);
                animatorArc.setDuration(ANIMATOR_DURATION);
                animatorArc.setStartDelay(ANIMATOR_DURATION);

                rotateAnimator = ObjectAnimator.ofFloat(car, "rotation", orientation, orientation + 90);
                rotateAnimator.setDuration(ANIMATOR_DURATION);
                rotateAnimator.setStartDelay(ANIMATOR_DURATION);

                animatorX = ObjectAnimator.ofFloat(car, "x", car.getX() - SNAP_GRID_INTERVAL * 3);
                animatorX.setDuration(ANIMATOR_DURATION);
                animatorX.setStartDelay(ANIMATOR_DURATION * 2);

                animatorSet.playTogether(animatorY, animatorArc, rotateAnimator, animatorX);
                animatorSet.start();
                break;
            case 3:
                animatorX = ObjectAnimator.ofFloat(car, "x", car.getX() - SNAP_GRID_INTERVAL);
                animatorX.setDuration(ANIMATOR_DURATION);

                path.arcTo(car.getX() - SNAP_GRID_INTERVAL * 3,
                        car.getY() - SNAP_GRID_INTERVAL * 4,
                        car.getX() + SNAP_GRID_INTERVAL,
                        car.getY(),
                        90f,
                        90f,
                        true);

                animatorArc = ObjectAnimator.ofFloat(car, View.X, View.Y, path);
                animatorArc.setDuration(ANIMATOR_DURATION);
                animatorArc.setStartDelay(ANIMATOR_DURATION);

                rotateAnimator = ObjectAnimator.ofFloat(car, "rotation", orientation, orientation + 90);
                rotateAnimator.setDuration(ANIMATOR_DURATION);
                rotateAnimator.setStartDelay(ANIMATOR_DURATION);

                animatorY = ObjectAnimator.ofFloat(car, "y", car.getY() - SNAP_GRID_INTERVAL * 3);
                animatorY.setDuration(ANIMATOR_DURATION);
                animatorY.setStartDelay(ANIMATOR_DURATION * 2);

                animatorSet.playTogether(animatorY, animatorArc, rotateAnimator, animatorX);
                animatorSet.start();
                break;
            default:
                // Shouldn't reach this case
                break;
        }
    }

    private void turnLeftButtonEvent() {
        byte[] bytes = commands.get("turnLeft").getBytes(Charset.defaultCharset());
        BluetoothComms.writeMsg(bytes);

        int orientation = (int) car.getRotation();

        ObjectAnimator animatorX;
        ObjectAnimator animatorY;
        ObjectAnimator animatorArc;
        ObjectAnimator rotateAnimator;
        AnimatorSet animatorSet = new AnimatorSet();
        Path path = new Path();

        switch (((orientation / 90) % 4 + 4) % 4) {
            case 0:
                animatorY = ObjectAnimator.ofFloat(car, "y", car.getY() - SNAP_GRID_INTERVAL);
                animatorY.setDuration(ANIMATOR_DURATION);

                path.arcTo(car.getX() - SNAP_GRID_INTERVAL * 4,
                        car.getY() - SNAP_GRID_INTERVAL * 3,
                        car.getX(),
                        car.getY() + SNAP_GRID_INTERVAL,
                        0f,
                        -90f,
                        true);

                animatorArc = ObjectAnimator.ofFloat(car, View.X, View.Y, path);
                animatorArc.setDuration(ANIMATOR_DURATION);
                animatorArc.setStartDelay(ANIMATOR_DURATION);

                rotateAnimator = ObjectAnimator.ofFloat(car, "rotation", orientation, orientation - 90);
                rotateAnimator.setDuration(ANIMATOR_DURATION);
                rotateAnimator.setStartDelay(ANIMATOR_DURATION);

                animatorX = ObjectAnimator.ofFloat(car, "x", car.getX() - SNAP_GRID_INTERVAL * 3);
                animatorX.setDuration(ANIMATOR_DURATION);
                animatorX.setStartDelay(ANIMATOR_DURATION * 2);

                animatorSet.playTogether(animatorY, animatorArc, rotateAnimator, animatorX);
                animatorSet.start();
                break;
            case 1:
                animatorX = ObjectAnimator.ofFloat(car, "x", car.getX() + SNAP_GRID_INTERVAL);
                animatorX.setDuration(ANIMATOR_DURATION);

                path.arcTo(car.getX() - SNAP_GRID_INTERVAL,
                        car.getY() - SNAP_GRID_INTERVAL * 4,
                        car.getX() + SNAP_GRID_INTERVAL * 3,
                        car.getY(),
                        90f,
                        -90f,
                        true);

                animatorArc = ObjectAnimator.ofFloat(car, View.X, View.Y, path);
                animatorArc.setDuration(ANIMATOR_DURATION);
                animatorArc.setStartDelay(ANIMATOR_DURATION);

                rotateAnimator = ObjectAnimator.ofFloat(car, "rotation", orientation, orientation - 90);
                rotateAnimator.setDuration(ANIMATOR_DURATION);
                rotateAnimator.setStartDelay(ANIMATOR_DURATION);

                animatorY = ObjectAnimator.ofFloat(car, "y", car.getY() - SNAP_GRID_INTERVAL * 3);
                animatorY.setDuration(ANIMATOR_DURATION);
                animatorY.setStartDelay(ANIMATOR_DURATION * 2);

                animatorSet.playTogether(animatorY, animatorArc, rotateAnimator, animatorX);
                animatorSet.start();
                break;
            case 2:
                animatorY = ObjectAnimator.ofFloat(car, "y", car.getY() + SNAP_GRID_INTERVAL);
                animatorY.setDuration(ANIMATOR_DURATION);

                path.arcTo(car.getX(),
                        car.getY() + SNAP_GRID_INTERVAL,
                        car.getX() + SNAP_GRID_INTERVAL * 4,
                        car.getY() + SNAP_GRID_INTERVAL * 3,
                        180f,
                        -90f,
                        true);

                animatorArc = ObjectAnimator.ofFloat(car, View.X, View.Y, path);
                animatorArc.setDuration(ANIMATOR_DURATION);
                animatorArc.setStartDelay(ANIMATOR_DURATION);

                rotateAnimator = ObjectAnimator.ofFloat(car, "rotation", orientation, orientation - 90);
                rotateAnimator.setDuration(ANIMATOR_DURATION);
                rotateAnimator.setStartDelay(ANIMATOR_DURATION);

                animatorX = ObjectAnimator.ofFloat(car, "x", car.getX() + SNAP_GRID_INTERVAL * 3);
                animatorX.setDuration(ANIMATOR_DURATION);
                animatorX.setStartDelay(ANIMATOR_DURATION * 2);

                animatorSet.playTogether(animatorY, animatorArc, rotateAnimator, animatorX);
                animatorSet.start();
                break;
            case 3:
                animatorX = ObjectAnimator.ofFloat(car, "x", car.getX() - SNAP_GRID_INTERVAL);
                animatorX.setDuration(ANIMATOR_DURATION);

                path.arcTo(car.getX() - SNAP_GRID_INTERVAL * 3,
                        car.getY(),
                        car.getX() + SNAP_GRID_INTERVAL,
                        car.getY() + SNAP_GRID_INTERVAL * 4,
                        270f,
                        -90f,
                        true);

                animatorArc = ObjectAnimator.ofFloat(car, View.X, View.Y, path);
                animatorArc.setDuration(ANIMATOR_DURATION);
                animatorArc.setStartDelay(ANIMATOR_DURATION);

                rotateAnimator = ObjectAnimator.ofFloat(car, "rotation", orientation, orientation - 90);
                rotateAnimator.setDuration(ANIMATOR_DURATION);
                rotateAnimator.setStartDelay(ANIMATOR_DURATION);

                animatorY = ObjectAnimator.ofFloat(car, "y", car.getY() + SNAP_GRID_INTERVAL * 3);
                animatorY.setDuration(ANIMATOR_DURATION);
                animatorY.setStartDelay(ANIMATOR_DURATION * 2);

                animatorSet.playTogether(animatorY, animatorArc, rotateAnimator, animatorX);
                animatorSet.start();
                break;
            default:
                // Shouldn't reach this case
                break;
        }
    }

    // Broadcast Receiver for incoming message
    BroadcastReceiver myReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            Log.d("Message", "Receiving Message!");

            String msg = intent.getStringExtra("receivingMsg");

            Log.d("msg", msg);

            Character command = msg.charAt(0);

            switch (command) {
                case 'R':
                    Log.d("Test", "ROBOT Command");
                    break;
                case 'S':
                    sequence = 1;
                    break;
                case 'T':
                    Log.d("T", String.valueOf(sequence));
                    break;
                default:
                    break;
            }
        }
    };

    private void setObstacleImage(int obstacleNumber, String image) {
        // If input values are invalid, log them and return
        if (!obstacles.containsKey(obstacleNumber) || !resources.containsKey(image)) {
            Log.d("Set Obstacle Image", "obstacleNumber = " + obstacleNumber);
            Log.d("Set Obstacle Image", "image = " + image);
            return;
        }

        obstacles.get(obstacleNumber).setImageResource(resources.get(image));
        obstacles.get(obstacleNumber).setRotation(0);
    }
    
    private void updateRobotPosition(int x, int y, char direction) {
        car.setX(x * SNAP_GRID_INTERVAL);
        car.setY(y * SNAP_GRID_INTERVAL);
        switch (direction) {
            case 'N':
                car.setRotation(0);
                break;
            case 'E':
                car.setRotation(90);
                break;
            case 'S':
                car.setRotation(180);
                break;
            case 'W':
                car.setRotation(270);
                break;
            default:
                // Shouldn't reach this case
                break;
        }
    }

    // Broadcast Receiver for incoming message
    BroadcastReceiver myReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("Message", "Receiving Message!");

            String message = intent.getStringExtra("receivingMsg").trim();

            Log.d("msg", message);

            String command = message.substring(0, message.indexOf(','));

            switch (command) {
                case "ROBOT":
                    int x = Character.getNumericValue(message.charAt(6));
                    int y = Character.getNumericValue(message.charAt(8));
                    char direction = message.charAt(10);

                    Log.d("ROBOT", "(x: " + x + ") (y: " + y + ") (direction: " + direction + ")");

                    updateRobotPosition(x, y, direction);
                    break;
                case "TARGET":
                    int obstacleNumber = Character.getNumericValue(message.charAt(7));
                    String targetId = message.substring(9);

                    Log.d("TARGET", "(obstacleNumber: " + obstacleNumber + ") (targetId: " + targetId + ")");

                    setObstacleImage(obstacleNumber,targetId);
                    break;
                default:
                    break;
            }
        }
    };
}