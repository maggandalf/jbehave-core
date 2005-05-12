/*
 * Created on 29-Aug-2004
 * 
 * (c) 2003-2004 ThoughtWorks Ltd
 *
 * See license.txt for license details
 */
package com.thoughtworks.jbehave.story.domain;

import com.thoughtworks.jbehave.core.minimock.UsingMiniMock;
import com.thoughtworks.jbehave.story.visitor.Visitor;


/**
 * @author <a href="mailto:dan.north@thoughtworks.com">Dan North</a>
 */
public abstract class GivenUsingMiniMock extends UsingMiniMock implements Given {
    public abstract void setUp(Environment environment) throws Exception;

	public void accept(Visitor visitor) {
		visitor.visitGiven(this);
	}
	
	
}