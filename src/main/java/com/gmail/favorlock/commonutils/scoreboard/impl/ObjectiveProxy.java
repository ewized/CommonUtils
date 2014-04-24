package com.gmail.favorlock.commonutils.scoreboard.impl;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Map;

import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;

import com.gmail.favorlock.commonutils.reflection.CommonReflection;
import com.gmail.favorlock.commonutils.reflection.VersionHandler;
import com.gmail.favorlock.commonutils.scoreboard.api.wrappers.ObjectiveWrapper;
import com.gmail.favorlock.commonutils.scoreboard.api.wrappers.ScoreboardWrapper;

public class ObjectiveProxy implements InvocationHandler {

    private final CraftObjectiveWrapper proxying;
    private final Objective noproxy;
    private CraftObjectiveWrapper wrapper;
    
    private ObjectiveProxy(CraftObjectiveWrapper proxying, Objective noproxy) {
        this.proxying = proxying;
        this.noproxy = noproxy;
        this.wrapper = null;
    }
    
    private void setObjectiveWrapper(CraftObjectiveWrapper wrapper) {
        this.wrapper = wrapper;
    }
    
    protected CraftObjectiveWrapper getObjectiveWrapper() {
        return wrapper;
    }
    
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (method.getDeclaringClass().equals(Objective.class)) {
            // Intercept the getScore() method and replace its return with our proxied Score
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
                        return ScoreProxy.newProxy(proxying, score);
                    }
                }
            }
            
            return method.invoke(noproxy, args);
        }
        
        return method.invoke(proxying, args);
    }
    
    
    /**
     * Create a new proxy for an Objective. If the given Objective is already a
     * proxy, nothing will be changed, and the existing proxy will be returned.
     * <p/>
     * In this method, a newly proxied Objective will inject itself into the
     * Scoreboard's map of Objectives, causing all subsequent references that
     * the Scoreboard gives to be a reference to the proxy.
     * 
     * @param objective The Objective that should be proxied.
     * @return An Objective instance that proxies the given Objective instance.
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    protected static Objective newProxy(ScoreboardWrapper wrapper, Objective objective, boolean main) {
        if (Proxy.isProxyClass(objective.getClass())) {
            return objective;
        }
        
        CraftObjectiveWrapper objective_wrapper = new CraftObjectiveWrapper(wrapper, objective);
        ObjectiveProxy invocationHandler = new ObjectiveProxy(objective_wrapper, objective);
        Objective proxy = (Objective) Proxy.newProxyInstance(
                ObjectiveProxy.class.getClassLoader(),
                new Class<?>[] { Objective.class, ObjectiveWrapper.class },
                invocationHandler);
        objective_wrapper.setProxy(proxy);
        invocationHandler.setObjectiveWrapper(new CraftObjectiveWrapper(wrapper, proxy).setProxy(proxy));
        
        if (main) {
            // Don't inject the proxies into the server main Scoreboard
            return proxy;
        }
        
        Class<?> classCraftScoreboard = VersionHandler.getCraftBukkitClass("scoreboard.CraftScoreboard");
        Field fieldObjectivesMap = CommonReflection.getField(classCraftScoreboard, "objectives");
        fieldObjectivesMap.setAccessible(true);
        
        try { // Replaces the Objective known to the CraftScoreboard with the proxied Objective
            Map objectives = (Map) fieldObjectivesMap.get(proxy.getScoreboard());
            objectives.put(proxy.getName(), proxy);
            
            return proxy;
        } catch (IllegalArgumentException | IllegalAccessException e) {
            e.printStackTrace();
            return null;
        }
    }
}
