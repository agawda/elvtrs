package com.gawdski.elevators;

import elevator.Request;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static elevator.Request.pressElevatorButton;
import static elevator.Request.pressLevelButton;
import static java.util.Arrays.asList;

/**
 * Created by sergey on 05.07.17.
 */
public class TestButtonPressGenerator extends Request.List {

    public TestButtonPressGenerator(int buildingLevel) {
        super(createTestData(buildingLevel));
    }

    static Map<Long, List<Request>> createTestData(int buildinglevel) {
        Map<Long, List<Request>> reqs = new HashMap<>();
        switch (buildinglevel){
            case 0:
                reqs.put(0L, asList(pressLevelButton(1), pressLevelButton(8), pressLevelButton(7)));
                reqs.put(1L, asList(pressElevatorButton(1, 5), pressElevatorButton(2, 6), pressElevatorButton(3, 9)));
                reqs.put(2L, asList(pressLevelButton(0), pressLevelButton(4), pressLevelButton(9)));
                reqs.put(3L, asList(pressElevatorButton(2, 7), pressElevatorButton(3, 8), pressElevatorButton(4, 2)));
                reqs.put(4L, asList(pressLevelButton(2), pressLevelButton(3), pressLevelButton(1)));
                break;
            case 1:
                break;
            case 3:
                break;
        }
        return reqs;
    }
}
