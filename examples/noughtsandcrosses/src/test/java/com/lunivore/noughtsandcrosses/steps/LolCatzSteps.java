package com.lunivore.noughtsandcrosses.steps;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

import java.awt.Component;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.JLabel;

import org.jbehave.core.annotations.Given;
import org.jbehave.core.annotations.Then;
import org.jbehave.core.annotations.When;

import com.lunivore.noughtsandcrosses.NoughtsAndCrosses;
import com.lunivore.noughtsandcrosses.util.OAndXUniverse;
import com.lunivore.noughtsandcrosses.view.ComponentNames;

public class LolCatzSteps  {
    public static String ROWS = "abc";
    public static String COLUMNS = "123";
    protected static final String NL = System.getProperty("line.separator");
	private final OAndXUniverse universe;

    public LolCatzSteps() {
    	this(new OAndXUniverse());
    }
    
    public LolCatzSteps(OAndXUniverse universe) {
		this.universe = universe;
	}
    
	@Given("game")
    public void givenTheGameIsRunning() {
        new NoughtsAndCrosses();
    }
    
    @Given("game like $grid")
    public void givenThatTheGridLooksLike(String grid) throws Exception {
        givenTheGameIsRunning();
        ArrayList<String> oTurns = new ArrayList<String>();
        ArrayList<String> xTurns = new ArrayList<String>();
        
        captureMoves(oTurns, xTurns, grid);    
        performMoves(oTurns, xTurns);
    }
    
    @Then("message sez \"$message\"")
    public void thenTheMessageShouldRead(String message) throws Exception {
        JLabel messageLabel = (JLabel) universe.getControl().findComponent(ComponentNames.MESSAGE);
        assertThat(messageLabel.getText(), equalTo(message));
    }

    @Then("I haz grid $grid")
    public void thenTheGridShouldLookLike(String grid) throws Exception {
        Component gridPanel = universe.getControl().findComponent(ComponentNames.GRID);
        assertThat(gridPanel.toString(), equalTo(grid));
    }

    @When("I clicks $space")
    public void whenPlayerClicksInSpace(String space) throws Exception {
    	universe.getControl().clickButton(space);
    }
    
    private void performMoves(List<String> oTurns, List<String> xTurns) throws Exception {
        while (xTurns.size() > 0) {
            whenPlayerClicksInSpace(xTurns.remove(0));
            if (oTurns.size() >0) {
                whenPlayerClicksInSpace(oTurns.remove(0));
            }
        }
    }

    private void captureMoves(List<String> oTurns, List<String> xTurns, String grid) {

        List<String> lines = Arrays.asList(grid.split(NL));
        for(int row=0;row<3;row++) {
            for(int col=0;col<3;col++) {
                char player = lines.get(row).charAt(col);
                String spaceLabel = "" + ROWS.charAt(row) + COLUMNS.charAt(col);
                if(player == 'O') {oTurns.add(spaceLabel);}
                if(player == 'X') {xTurns.add(spaceLabel);}
            }
        }
    }
}
