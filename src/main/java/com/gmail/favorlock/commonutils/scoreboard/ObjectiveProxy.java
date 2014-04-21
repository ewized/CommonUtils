package com.gmail.favorlock.commonutils.scoreboard;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Map;

import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;

import com.gmail.favorlock.commonutils.reflection.CommonReflection;

public class ObjectiveProxy implements InvocationHandler {

    private final ScoreboardWrapper wrapper;
    private final Objective proxying;
    
    private ObjectiveProxy(ScoreboardWrapper wrapper, Objective proxying) {
        this.wrapper = wrapper;
        this.proxying = proxying;
    }
    
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (method.getName().equals("getScore")) {
            Class<?>[] params = method.getParameterTypes();
            
            if (params.length > 0) {
                String entry = null;
                
                if (params[0].equals(Player.class)) {
                    entry = ((Player) args[0]).getName();
                } else if (params[0].equals(String.class)) {
                    entry = (String) args[0];
                }
                
                if (entry != null) {
                    Score score = proxying.getScore(entry);
                    return ScoreProxy.newProxy(wrapper.getObjective(proxying.getName()), score);
                }
            }
        }
        
        return method.invoke(proxying, args);
    }
    
    
    /**
     * Create a new proxy for an Objective.
     * 
     * @param objective The Objective that should be proxied.
     * @return An Objective instance that proxies the given Objective instance.
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    protected static Objective newProxy(ScoreboardWrapper wrapper, Objective objective) {
        Objective proxy = (Objective) Proxy.newProxyInstance(
                ObjectiveProxy.class.getClassLoader(),
                new Class<?>[] { Objective.class },
                new ObjectiveProxy(wrapper, objective));
        
        Class<?> classCraftScoreboard = proxy.getScoreboard().getClass();
        Field fieldObjectivesMap = CommonReflection.getField(classCraftScoreboard, "objectives");
        fieldObjectivesMap.setAccessible(true);
        
        try {
            Map objectives = (Map) fieldObjectivesMap.get(proxy.getScoreboard());
            objectives.put(proxy.getName(), proxy);
            
            return proxy;
        } catch (IllegalArgumentException | IllegalAccessException e) {
            e.printStackTrace();
            return null;
        }
    }
}
