package com.gmail.favorlock.commonutils.scoreboard;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import org.bukkit.scoreboard.Score;

import com.gmail.favorlock.commonutils.scoreboard.api.ScoreboardScoreChangeEvent;

public class ScoreProxy implements InvocationHandler {

    private final ObjectiveWrapper wrapper;
    private final Score proxying;
    
    private ScoreProxy(ObjectiveWrapper wrapper, Score proxying) {
        this.wrapper = wrapper;
        this.proxying = proxying;
    }
    
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (method.getName().equals("setScore")) {
            Class<?>[] params = method.getParameterTypes();
            
            if (params.length == 1 && params[0].equals(int.class)) {
                int prevscore = proxying.getScore();
                int newscore = (int) args[0];
                
                wrapper.getWrapper().invokeListeners(new ScoreboardScoreChangeEvent(
                        wrapper.getWrapper(), wrapper, proxying.getEntry(), prevscore, newscore));
            }
        }
        
        return method.invoke(proxying, args);
    }
    
    
    /**
     * Create a new proxy for a Score.
     * 
     * @param score The Score that should be proxied.
     * @return A Score instance that proxies the given Score instance.
     */
    protected static Score newProxy(ObjectiveWrapper wrapper, Score score) {
        return (Score) Proxy.newProxyInstance(
                ScoreProxy.class.getClassLoader(),
                new Class<?>[] { Score.class },
                new ScoreProxy(wrapper, score));
    }
}
