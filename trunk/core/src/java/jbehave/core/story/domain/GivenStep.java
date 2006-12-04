package jbehave.core.story.domain;

import jbehave.core.story.renderer.Renderer;

public class GivenStep extends AbstractStep {
    
    public GivenStep(Given given) {
        super(given);
    }

    public void perform(World world) {
        given().setUp(world);
    }
    
    private Given given() {
        return (Given)component;
    }
}