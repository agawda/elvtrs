package com.gawdski.elevators;

import elevator.runner.ConsoleRunner;
import elevator.runner.RunnerConfiguration;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

@Test
public class AppTest {

    @DataProvider(name = "testData")
    public Object[][] provide(){
        return new Object[][]{
                {new RunnerConfiguration(
                        10,
                        5,
                        new Main.MyRoutingController(),
                        new Main.MyButtonPressGenerator()
                )},

        };
    }

    @Test(dataProvider = "testData")
    public void elevatorTest(RunnerConfiguration config){
        try{
            ConsoleRunner.runWith(config);
        } catch (InterruptedException e){
            e.getLocalizedMessage();
        }
    }


}
