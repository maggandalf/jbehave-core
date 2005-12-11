/*
 * Created on 05-Oct-2004
 * 
 * (c) 2003-2004 ThoughtWorks Ltd
 *
 * See license.txt for license details
 */
package jbehave.core.behaviour;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import jbehave.core.Ensure;
import jbehave.core.exception.JBehaveFrameworkError;
import jbehave.core.listener.ResultListener;
import jbehave.core.minimock.ChainedConstraint;
import jbehave.core.minimock.Constraint;
import jbehave.core.minimock.Mock;
import jbehave.core.minimock.UsingMiniMock;
import jbehave.core.result.Result;
import jbehave.core.result.Result.Type;


/**
 * @author <a href="mailto:dan.north@thoughtworks.com">Dan North</a>
 */
public class BehaviourMethodBehaviour extends UsingMiniMock {
	
	public static class StoresInvocation {
        public boolean methodWasInvoked = false;
        public void shouldDoSomething() {
            methodWasInvoked = true;
        }
    }
	
    public void shouldVerifyByInvokingMethod() throws Throwable {
        // given
		StoresInvocation instance = new StoresInvocation();
        ResultListener listener = (ResultListener) stub(ResultListener.class);
        BehaviourMethod behaviourMethod = createBehaviourMethod(instance, "shouldDoSomething");
        
        // when
        behaviourMethod.verifyTo(listener);
        
        // then
        Ensure.that(instance.methodWasInvoked);
    }
    
    public static class HasSuccessfulMethod {
        public void shouldWork() {
            // succeeds
        }
    }
    
    private Constraint resultOfType(Type type) {
        return new Constraint() {
            public boolean matches(Object arg) {
                return ((Result)arg).succeeded();
            }
        };
    }
    
    public void shouldTellListenerWhenVerifySucceeds() throws Exception {
        // given...
        Object instance = new HasSuccessfulMethod();
        Mock listener = mock(ResultListener.class);
        Behaviour behaviour = createBehaviourMethod(instance, "shouldWork");
        
        // expect...
        listener.expects("gotResult").with(resultOfType(Result.SUCCEEDED));
        
        // when...
        behaviour.verifyTo((ResultListener) listener);
        
        // then...
        verifyMocks();
    }
    
    public static class HasSetUpAndTearDown {
        public static final List whatHappened = new ArrayList();
        
        public void setUp() throws Exception {
            whatHappened.add("setUp");
        }
        public void tearDown() throws Exception {
            whatHappened.add("tearDown");
        }
        public void shouldDoSomething() throws Exception {
            whatHappened.add("shouldDoSomething");
        }
    }
    
    private BehaviourMethod createBehaviourMethod(Object instance, String methodName) {
        try {
            Method method = instance.getClass().getMethod(methodName, null);
            return new BehaviourMethod(instance, method);
        } catch (Exception e) {
            throw new JBehaveFrameworkError("No method " + methodName + " on class " + instance.getClass().getName());
        }
    }
    
    public void shouldInvokeSetUpAndTearDownInTheCorrectSequence() throws Throwable {
        // given
        Object instance = new HasSetUpAndTearDown();
        ResultListener listener = (ResultListener) stub(ResultListener.class);
        Behaviour behaviour = createBehaviourMethod(instance, "shouldDoSomething");
        
        // expect
        List expected = Arrays.asList(new String[] {
                "setUp", "shouldDoSomething", "tearDown"
        });
        
        // when
        behaviour.verifyTo(listener);
        
        // then
        ensureThat(HasSetUpAndTearDown.whatHappened, eq(expected));
    }

    public static class CheckedException extends Exception {}

    private ChainedConstraint resultContainingCheckedException() {
        return new ChainedConstraint() {
            public boolean matches(Object arg) {
                return isA(CheckedException.class).matches(((Result)arg).cause());
            }
            public String toString() {
                return "result containing a CheckedException";
            }
        };
    }
    
    public static class MethodThrowsException {
        public void shouldDoSomething() throws Exception {
            throw new CheckedException();
        }
    }
    
    public void shouldReportExceptionFromMethod() throws Throwable {
        // given
        Behaviour behaviour = createBehaviourMethod(new MethodThrowsException(), "shouldDoSomething");
        Mock listener = mock(ResultListener.class);
        
        // expect
        listener.expects("gotResult").with(resultContainingCheckedException());
        
        // when
        behaviour.verifyTo((ResultListener) listener);
        
        // then
        verifyMocks();
    }
    
    public static class SetUpThrowsException extends HasSetUpAndTearDown {
        public void setUp() throws Exception {
            throw new CheckedException();
        }
    }
    
    public void shouldReportExceptionFromSetUp() throws Throwable {
        // given
        Behaviour behaviour = createBehaviourMethod(new SetUpThrowsException(), "shouldDoSomething");
        Mock listener = mock(ResultListener.class);
        
        // expect
        listener.expects("gotResult").with(resultContainingCheckedException());
        
        // when
        behaviour.verifyTo((ResultListener) listener);
        
        // then
        verifyMocks();
    }
    
    public static class TearDownThrowsException extends HasSetUpAndTearDown {
        public void tearDown() throws Exception {
            throw new CheckedException();
        }
    }
    
    public void shouldReportExceptionFromTearDown() throws Throwable {
        // given
        Behaviour behaviour = createBehaviourMethod(new TearDownThrowsException(), "shouldDoSomething");
        Mock listener = mock(ResultListener.class);
        
        // expect
        listener.expects("gotResult").with(resultContainingCheckedException());
        
        // when
        behaviour.verifyTo((ResultListener) listener);
        
        // then
        verifyMocks();
    }
    
    public static class MethodAndTearDownBothThrowException extends HasSetUpAndTearDown {
        public void tearDown() throws Exception {
            throw new Exception("from tearDown");
        }
        public void shouldDoSomething() throws Exception {
            throw new Exception("from method");
        }
    }
    
    private Constraint resultContainingExceptionMessage(final String message) {
        return new Constraint() {
            public boolean matches(Object arg) {
                return message.equals(((Result)arg).cause().getMessage());
            }
            public String toString() {
                return "result containing CheckedException with message=" + message;
            }
        };
    }
    
    public void shouldReportExceptionFromMethodIfMethodAndTearDownBothThrowException() throws Throwable {
        // given
        Behaviour behaviour = createBehaviourMethod(new MethodAndTearDownBothThrowException(), "shouldDoSomething");
        Mock listener = mock(ResultListener.class);
        
        // expect
        listener.expects("gotResult").with(resultContainingExceptionMessage("from method"));
        
        // when
        behaviour.verifyTo((ResultListener) listener);
        
        // then
        verifyMocks();
    }
}