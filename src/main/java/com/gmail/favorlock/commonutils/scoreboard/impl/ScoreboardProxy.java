package com.gmail.favorlock.commonutils.scoreboard.impl;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Collection;
import java.util.HashSet;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import com.gmail.favorlock.commonutils.reflection.CommonReflection;
import com.gmail.favorlock.commonutils.reflection.VersionHandler;
import com.gmail.favorlock.commonutils.scoreboard.api.wrappers.ObjectiveWrapper;
import com.gmail.favorlock.commonutils.scoreboard.api.wrappers.ScoreboardWrapper;
import com.google.common.collect.ImmutableSet;

public class ScoreboardProxy implements InvocationHandler {

    private final CraftScoreboardWrapper proxying;
    private final Scoreboard noproxy;
    
    private ScoreboardProxy(CraftScoreboardWrapper proxying, Scoreboard noproxy) {
        this.proxying = proxying;
        this.noproxy = noproxy;
    }
    
    public Object invoke(Object proxy, Method method, Object[] args) throws Exception {
        Class<?>[] params = method.getParameterTypes();
        
        if (method.getDeclaringClass().equals(Scoreboard.class)) {
            switch (method.getName()) {
            case "registerNewObjective": // Intercept and proxy new Objectives
                if (params.length == 2 && params[0].equals(String.class) && params[1].equals(String.class)) {
                    Objective created = (Objective) method.invoke(noproxy, args);
                    
                    if (Proxy.isProxyClass(created.getClass())) {
                        return created;
                    }
                    
                    return ObjectiveProxy.newProxy(proxying, created);
                }
                
                break;
            case "registerNewTeam": // Intercept and proxy new Teams
                if (params.length == 1 && params[0].equals(String.class)) {
                    Team created = (Team) method.invoke(noproxy, args);
                    
                    if (Proxy.isProxyClass(created.getClass())) {
                        return created;
                    }
                    
                    return TeamProxy.newProxy(proxying, created);
                }
                
                break;
            case "getObjectivesByCriteria":
                // Our hacky means would be foiled by this method, so we make our own.
                if (params.length == 1 && params[0].equals(String.class)) {
                    String criterion = (String) args[0];
                    ImmutableSet.Builder<Objective> objectives = ImmutableSet.builder();
                    
                    for (ObjectiveWrapper objective : proxying.getObjectiveSet()) {
                        if (objective.getCriterion().equals(criterion)) {
                            objectives.add(objective.getObjective());
                        }
                    }
                    
                    return objectives.build();
                } else {
                    return new HashSet<Objective>();
                }
            case "getScores":
                // Our hacky means would be foiled by this method, so we make our own.
                if (params.length == 1) {
                    String entry = null;
                    
                    if (params[0].equals(OfflinePlayer.class)) {
                        entry = ((OfflinePlayer) args[0]).getName();
                    } else if (params[0].equals(String.class)) {
                        entry = (String) args[0];
                    }
                    
                    if (entry != null) {
                        ImmutableSet.Builder<Score> scores = new ImmutableSet.Builder<>();
                        
                        for (ObjectiveWrapper objective : proxying.getObjectiveSet()) {
                            scores.add(ScoreProxy.newProxy(objective, objective.getObjective().getScore(entry)));
                        }
                        
                        return scores.build();
                    }
                }
                
                return new HashSet<Score>();
            default:
                break;
            }
            
            return method.invoke(noproxy, args);
        }
        
        return method.invoke(proxying, args);
    }
    
    
    /**
     * Create a new proxy for the Scoreboard underlying the given
     * ScoreboardWrapper. If the given Scoreboard is already a proxy, nothing
     * will be changed, and the existing proxy will be returned.
     * <p/>
     * In this method, a newly proxied Scoreboard will inject itself into
     * Bukkit's ScoreboardManager, causing all subsequent references that the
     * ScoreboardManager gives to be a reference to the proxy.
     * 
     * @return A Scoreboard instance that proxies the given ScoreboardWrapper's
     *         Scoreboard.
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    protected static Scoreboard newProxy(CraftScoreboardWrapper wrapper, boolean main) {
        Scoreboard scoreboard = wrapper.bypassProxy();
        
        if (Proxy.isProxyClass(scoreboard.getClass())) {
            return scoreboard;
        }
        
        Scoreboard proxy = (Scoreboard) Proxy.newProxyInstance(
                ScoreboardProxy.class.getClassLoader(),
                new Class<?>[] { Scoreboard.class, ScoreboardWrapper.class },
                new ScoreboardProxy(wrapper, scoreboard));
        wrapper.setProxy(proxy);
        
        Class<?> classCraftScoreboardManager = VersionHandler.getCraftBukkitClass("scoreboard.CraftScoreboardManager");
        Field fieldScoreboardsCollection = CommonReflection.getField(classCraftScoreboardManager, "scoreboards");
        fieldScoreboardsCollection.setAccessible(true);
        
        if (main) {
            // We cannot replace the server main Scoreboard,
            // only instances retrieved from this API will be proxied.
            return proxy;
        } else {
            try { // Replaces the Scoreboard known to the CraftScoreboardManager with the proxied Scoreboard
                Collection scoreboards = (Collection) fieldScoreboardsCollection.get(Bukkit.getScoreboardManager());
                scoreboards.remove(scoreboard);
                scoreboards.add(proxy);
                
                // Handle any existing Objectives; ideally there won't be any.
                for (Objective objective : proxy.getObjectives()) {
                    ObjectiveProxy.newProxy(wrapper, objective);
                }
                // Handle any existing Teams; ideally there won't be any.
                for (Team team : proxy.getTeams()) {
                    TeamProxy.newProxy(wrapper, team);
                }
                
                return proxy;
            } catch (IllegalArgumentException | IllegalAccessException e) {
                e.printStackTrace();
                return null;
            }
        }
    }
}
