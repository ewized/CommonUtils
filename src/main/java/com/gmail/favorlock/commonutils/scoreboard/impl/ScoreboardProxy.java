package com.gmail.favorlock.commonutils.scoreboard.impl;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashSet;

import org.bukkit.OfflinePlayer;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import com.gmail.favorlock.commonutils.scoreboard.api.wrappers.ObjectiveWrapper;
import com.gmail.favorlock.commonutils.scoreboard.api.wrappers.ScoreboardWrapper;
import com.google.common.collect.ImmutableSet;

public class ScoreboardProxy implements InvocationHandler {

    public static final boolean PROXY_OBJECTIVES = true;
    public static final boolean PROXY_TEAMS = false;
    
    private final CraftScoreboardWrapper proxying;
    private final Scoreboard noproxy;
    private final boolean main;
    
    private ScoreboardProxy(CraftScoreboardWrapper proxying, Scoreboard noproxy, boolean main) {
        this.proxying = proxying;
        this.noproxy = noproxy;
        this.main = main;
    }
    
    public CraftScoreboardWrapper getProxiedScoreboardWrapper() {
        return proxying;
    }
    
    public Scoreboard getUnproxiedScoreboard() {
        return noproxy;
    }
    
    public boolean isMain() {
        return main;
    }
    
    public Object invoke(Object proxy, Method method, Object[] args) throws Exception {
        Class<?>[] params = method.getParameterTypes();
        
        if (method.getDeclaringClass().equals(Scoreboard.class)) {
            switch (method.getName()) {
            case "registerNewObjective": // Intercept and proxy new Objectives
                if (params.length == 2 && params[0].equals(String.class) && params[1].equals(String.class)) {
                    for (Objective objective : noproxy.getObjectives()) {
                        if (objective.getName().equals((String) args[0])) {
                            if (Proxy.isProxyClass(objective.getClass())) {
                                return objective;
                            } else {
                                if (PROXY_OBJECTIVES) {
                                    return ObjectiveProxy.newProxy(proxying, objective, main);
                                } else {
                                    return objective;
                                }
                            }
                        }
                    }
                    
                    Objective created = (Objective) method.invoke(noproxy, args);
                    
                    if (Proxy.isProxyClass(created.getClass())) {
                        return created;
                    }
                    
                    if (PROXY_OBJECTIVES) {
                        return ObjectiveProxy.newProxy(proxying, created, main);
                    } else {
                        return created;
                    }
                }
                
                break;
            case "registerNewTeam": // Intercept and proxy new Teams
                if (params.length == 1 && params[0].equals(String.class)) {
                    for (Team team : noproxy.getTeams()) {
                        if (team.getName().equals((String) args[0])) {
                            if (Proxy.isProxyClass(team.getClass())) {
                                return team;
                            } else {
                                if (PROXY_TEAMS) {
                                    return TeamProxy.newProxy(proxying, team, main);
                                } else {
                                    return team;
                                }
                            }
                        }
                    }
                    
                    Team created = (Team) method.invoke(noproxy, args);
                    
                    if (Proxy.isProxyClass(created.getClass())) {
                        return created;
                    }
                    
                    if (PROXY_TEAMS) {
                        return TeamProxy.newProxy(proxying, created, main);
                    } else {
                        return created;
                    }
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
            case "getPlayerTeam":
                // Our hacky means would also be foiled by this method.
                if (params.length == 1 && params[0].equals(OfflinePlayer.class)) {
                    OfflinePlayer offlineplayer = (OfflinePlayer) args[0];
                    
                    if (offlineplayer == null) {
                        return null;
                    }
                    
                    return proxying.getTeamForEntry(offlineplayer.getName());
                }
                
                return null;
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
     * 
     * @return A Scoreboard instance that proxies the given ScoreboardWrapper's
     *         Scoreboard.
     */
    protected static Scoreboard newProxy(CraftScoreboardWrapper wrapper, boolean main) {
        Scoreboard scoreboard = wrapper.bypassProxy();
        
        if (Proxy.isProxyClass(scoreboard.getClass())) {
            if (Proxy.getInvocationHandler(scoreboard) instanceof ScoreboardProxy)
                return scoreboard;
        }
        
        Scoreboard proxy = (Scoreboard) Proxy.newProxyInstance(
                ScoreboardProxy.class.getClassLoader(),
                new Class<?>[] { Scoreboard.class, ScoreboardWrapper.class },
                new ScoreboardProxy(wrapper, scoreboard, main));
        wrapper.setProxy(proxy);
        
        if (main) {
            // Don't do any injection for the server main Scoreboard.
            return proxy;
        } else {
            // Handle any existing Objectives; as if this Scoreboard was integrated there will be some.
            for (Objective objective : proxy.getObjectives()) {
                ObjectiveProxy.newProxy(wrapper, objective, main);
            }
            // Handle any existing Teams; as if this Scoreboard was integrated there will be some.
            for (Team team : proxy.getTeams()) {
                TeamProxy.newProxy(wrapper, team, main);
            }
            
            return proxy;
        }
    }
}
