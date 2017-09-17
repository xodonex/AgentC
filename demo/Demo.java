// Copyright 2017 Henrik Lauritzen
/*
  Copying and distribution of this file, with or without modification,
  are permitted in any medium without royalty provided the copyright
  notice and this notice are preserved.  This file is offered as-is,
  without any warranty.
*/
package demo;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import javax.swing.JFrame;

import players.*;
import dk.dtu.imm.cse.agent.act.demo.DemoAcme;
import dk.dtu.imm.cse.agent.act.demo.HaplomacyDemo;


/*
 * AgentC toolkit demonstration.
 * The main program instantiates the 4 agents included, and sets up
 * a HaplomacyDemo game played by these 4 agents.
 */
public final class Demo {

    public final static void main(String[] args) {
        // Default choices of various parameter values.
        boolean debug = true;
        double zoomFactor = 1.0;
        int stepSize = 1;
        Random randomSource = new Random();
        int severalTurns = 250;
        double speedFactor = 1.0;
        long negotiateDelay = (long)(speedFactor * 125);
        long negotiateRate = (long)(speedFactor * 10);
        long orderDelay = (long)(speedFactor * 300);


        // Instantiate the 4 agents
        DemoAcme[] acmes = {
            new players.Ruthless(0),
            new players.Vindictive(1),
            new players.Cautious(2),
            new players.Cowardly(3)
        };

        // Instantiate the game simulation
        HaplomacyDemo demo = new HaplomacyDemo(acmes, randomSource);
        demo.getGameBoard().setZoomLevel(zoomFactor);

        // Set up debugging, if desired
        for (DemoAcme a: acmes) {
            a.setReceivedMessageLogged(debug);
            a.setSentMessageLogged(debug);
        }

        // Show the game board
        JFrame frame = new JFrame();
        frame.getContentPane().add(demo.getDisplay());
        frame.pack();
        frame.setVisible(true);

        // Run the demo
        System.out.println("A new turn is played for every line of input");
        System.out.println("Type 'quit' on a separate line to exit the demo");
        System.out.println(
            "Type 'go' on a separate line to play several turns");
        demo.start();

        BufferedReader input = new BufferedReader(
            new InputStreamReader(System.in));
        try {
            while (true) {
                // Read a single line of input
                String line = input.readLine();
                if (line.equals("quit")) {
                    break;
                }

                // Display any buffered debug messages
                for (DemoAcme a: acmes) {
                    List log = a.getLog();
                    for (Object o: log) {
                        String s;
                        if (o == null) {
                            continue;
                        }
                        if (o instanceof Object[]) {
                            s = Arrays.toString((Object[])o);
                        }
                        else {
                            s = o.toString();
                        }
                        System.out.println(s);
                    }
                    log.clear();
                }

                // Play the turns
                int turns = line.equals("go") ? severalTurns : 1;
                demo.playTurns(
                    turns, negotiateDelay,
                    negotiateRate, orderDelay);
            }
        }
        catch (InterruptedException e) {
            // Ignore and terminate
        }
        catch (IOException e) {
            // Ignore nad terminate
        }
        System.exit(0);
    }
}
