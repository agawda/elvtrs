package com.gawdski.elevators;

import elevator.ElevatorInfo;
import elevator.Request;
import elevator.RoutingController;
import elevator.RoutingOperator;
import elevator.runner.ConsoleRunner;
import elevator.runner.GuiRunner;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.stream.Collectors;

import static elevator.Request.pressElevatorButton;
import static elevator.Request.pressLevelButton;
import static elevator.runner.RunnerConfiguration.*;
import static java.util.Arrays.asList;

public class Main {

    public static void main(String[] args) throws InterruptedException, ClassNotFoundException, InstantiationException, IllegalAccessException, IOException, InvocationTargetException {
        runWithGUI();
        //or:
//         runWithConsole();
        //or:
        // runWithConsole_andDataFile();
        //or:
//        runWithGUI_NoGenerator_ForRecording();
    }

    private static void runWithConsole() throws InterruptedException, ClassNotFoundException, InstantiationException, IOException, IllegalAccessException {
        ConsoleRunner.main(new String[]{
                FLOOR_COUNT + "=10",
                ELEVATOR_COUNT + "=5",
                ROUTING_CONTROLLER_CLASS_NAME + "=" + MyRoutingController.class.getName(),
                GENERATOR_CLASS_NAME + "=" + MyButtonPressGenerator.class.getName(),
        });
    }

    //you have to record a data file with GuiRunner before using this version
    private static void runWithConsole_andDataFile() throws InterruptedException, ClassNotFoundException, InstantiationException, IOException, IllegalAccessException {
        ConsoleRunner.main(new String[]{
                FLOOR_COUNT + "=10",
                ELEVATOR_COUNT + "=5",
                ROUTING_CONTROLLER_CLASS_NAME + "=" + MyRoutingController.class.getName(),
                TEST_DATA_FILE + "=" + "recorded.elevators" //rename to recorded data file
        });
    }

    private static void runWithGUI() throws ClassNotFoundException, IOException, InstantiationException, InterruptedException, IllegalAccessException, InvocationTargetException {
        GuiRunner.main(new String[]{
                FLOOR_COUNT + "=10",
                ELEVATOR_COUNT + "=5",
                ROUTING_CONTROLLER_CLASS_NAME + "=" + MyRoutingController.class.getName(),
                GENERATOR_CLASS_NAME + "=" + MyButtonPressGenerator.class.getName(),
                //or comment out the above to run with the below data file (you will have to record a data file with GuiRunner first):
                //TEST_DATA_FILE + "=" + "recorded.elevators" //rename to recorded data file
        });

    }

    private static void runWithGUI_NoGenerator_ForRecording() throws ClassNotFoundException, IOException, InstantiationException, InterruptedException, IllegalAccessException, InvocationTargetException {
        GuiRunner.main(new String[]{
                FLOOR_COUNT + "=10",
                ELEVATOR_COUNT + "=5",
                ROUTING_CONTROLLER_CLASS_NAME + "=" + MyRoutingController.class.getName()
        });
    }


    public static class MyRoutingController implements RoutingController {

        private RoutingOperator operator;

        @Override
        public void initialize(RoutingOperator operator) {
            this.operator = operator;

            //I can observe more events than just 'elevatorButtonPressed' and 'levelButtonPressed" if I want to:
//            operator.observe().whenElevatorMoved((elevatorID, level) -> System.out.println("Hey, I can observe elevator moves! E#" + elevatorID + " is on level " + level + ". Maybe I will reroute it now."));
            //or: operator.observe().whenElevatorRequestCompleted(...);
            //or: operator.observe().whenLevelRequestCompleted(...);
        }

        @Override
        public void elevatorButtonPressed(int elevatorID, Integer level) {
            //eventually, you want to call operator.setRoute(elevatorID, ???);
            List<Integer> route = optimizeRoute(operator.getElevatorInfo(elevatorID).getCurrentRoute(), operator.getElevatorInfo(elevatorID).getCurrentPosition(), level);
            operator.setRoute(elevatorID, route);
        }

        @Override
        public void levelButtonPressed(Integer level) {
            //which elevator should go there?
            List<Integer> minFloors = new ArrayList<>();
            for (int i = 0; i < operator.getElevatorCount(); i++) {
                minFloors.add(operator.getElevatorInfo(i).getCurrentRoute().size());
            }
            int min = minFloors.stream().min(Comparator.naturalOrder()).get();
            ElevatorInfo elevator = operator.getElevatorInfo(minFloors.indexOf(min));
            List<Integer> route = optimizeRoute(elevator.getCurrentRoute(), elevator.getCurrentPosition(), level);
            operator.setRoute(minFloors.indexOf(min), route);
        }

        public List<Integer> optimizeRoute(List<Integer> route, Integer currentFloor, Integer destination) {
            boolean naturalOrder = isNaturalOrder(route);
            List<Integer> result = new ArrayList<>();
            result.addAll(route);
            result.add(destination);
            List<Integer> lower = result.stream().filter(x -> x < currentFloor).collect(Collectors.toList());
            List<Integer> upper = result.stream().filter(x -> x > currentFloor).collect(Collectors.toList());
            result = new ArrayList<>();
            if(naturalOrder) {
                result.addAll(upper);
                Collections.sort(lower, Comparator.reverseOrder());
                result.addAll(lower);
            } else {
                Collections.sort(lower, Comparator.reverseOrder());
                result.addAll(lower);
                result.addAll(upper);
            }

            return result;
        }

        boolean isNaturalOrder(List<Integer> requests) {
            return requests.size() == 1 || requests.size() == 0 || requests.get(0) < requests.get(1);
        }

    }

    public static class MyButtonPressGenerator extends Request.List {

        public MyButtonPressGenerator() {
            super(createTestData());
        }

        static Map<Long, List<Request>> createTestData() {
            Map<Long, List<Request>> reqs = new HashMap<>();//requests to be made per tick
            reqs.put(0L, asList(pressLevelButton(1), pressLevelButton(8), pressLevelButton(7)));
            reqs.put(1L, asList(pressElevatorButton(1, 5), pressElevatorButton(2, 6), pressElevatorButton(3, 9)));
            reqs.put(2L, asList(pressLevelButton(0), pressLevelButton(4), pressLevelButton(9)));
            reqs.put(3L, asList(pressElevatorButton(2, 7), pressElevatorButton(3, 8), pressElevatorButton(4, 2)));
            reqs.put(4L, asList(pressLevelButton(2), pressLevelButton(3), pressLevelButton(1)));
            return reqs;
        }
    }
}
