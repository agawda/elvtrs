package com.gawdski.elevators;


import elevator.Request;
import elevator.runner.ConsoleRunner;
import elevator.runner.LogUtil;
import elevator.runner.RunnerConfiguration;
import elevator.runner.SimulationResults;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.logging.Level;
import java.util.logging.Logger;

@Test
public class AppTest {

    private Logger logger = Logger.getLogger("testLog");

    @BeforeTest
    public void init(){
        logger.setLevel(Level.INFO);
    }


    @DataProvider(name = "testData")
    public Object[][] provide(){
        return new Object[][]{
                {1,10, 5, 0}
        };
    }

    @Test(dataProvider = "testData")
    public void elevatorTest(int testId, int floorCount, int elevatorCount, int buildingLevel){
        RunnerConfiguration configuration = new RunnerConfiguration(
                floorCount,
                elevatorCount,
                new Main.MyRoutingController(),
                new TestButtonPressGenerator(buildingLevel)
        );

        try{
            SimulationResults sim = new ConsoleRunner().runWith(configuration, logger);
            String message = String.format("------------------ \nTest #%d \nTotal duration is: %d \nCompleted requests: %d\nThe worst duration: %d \n------------------", testId, sim.getTotalDuration(), sim.getCompletedRequests(), sim.getWorstDurationOfCompletedRequest());
            System.out.println(message);
        } catch (InterruptedException e){
            e.getLocalizedMessage();
        }
    }


}
