package com.gmail.favorlock.commonutils.scoreboard.impl;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Map;

import org.bukkit.scoreboard.Team;

import com.gmail.favorlock.commonutils.reflection.CommonReflection;
import com.gmail.favorlock.commonutils.reflection.VersionHandler;
import com.gmail.favorlock.commonutils.scoreboard.api.wrappers.ScoreboardWrapper;
import com.gmail.favorlock.commonutils.scoreboard.api.wrappers.TeamWrapper;

public class TeamProxy implements InvocationHandler {

    private final CraftTeamWrapper proxying;
    private final Team noproxy;
    private CraftTeamWrapper wrapper;
    
    private TeamProxy(CraftTeamWrapper proxying, Team noproxy) {
        this.proxying = proxying;
        this.noproxy = noproxy;
        this.wrapper = null;
    }
    
    private void setTeamWrapper(CraftTeamWrapper wrapper) {
        this.wrapper = wrapper;
    }
    
    protected CraftTeamWrapper getTeamWrapper() {
        return wrapper;
    }
    
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        // This proxy doesn't need to intercept any methods, but its function is still vital.
        if (method.getDeclaringClass().equals(Team.class)) {
            method.invoke(noproxy, args);
        }
        
        return method.invoke(proxying, args);
    }
    
    
    /**
     * Create a new proxy for a Team. If the given team is already a proxy,
     * nothing will be changed, and the existing proxy will be returned.
     * <p/>
     * In this method, a newly proxied Team will inject itself into the
     * Scoreboard's map of Teams, causing all subsequent references that the
     * Scoreboard gives to be a reference to the proxy.
     * 
     * @param team  The Team that should be proxied.
     * @return A Team instance that proxies the given Team instance.
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    protected static Team newProxy(ScoreboardWrapper wrapper, Team team) {
        if (Proxy.isProxyClass(team.getClass())) {
            return team;
        }
        
        CraftTeamWrapper team_wrapper = new CraftTeamWrapper(wrapper, team);
        TeamProxy invocationHandler = new TeamProxy(team_wrapper, team);
        Team proxy = (Team) Proxy.newProxyInstance(
                TeamProxy.class.getClassLoader(),
                new Class<?>[] { Team.class, TeamWrapper.class },
                invocationHandler);
        team_wrapper.setProxy(proxy);
        invocationHandler.setTeamWrapper(new CraftTeamWrapper(wrapper, proxy).setProxy(proxy));
        
        Class<?> classCraftScoreboard = VersionHandler.getCraftBukkitClass("scoreboard.CraftScoreboard");
        Field fieldTeamsMap = CommonReflection.getField(classCraftScoreboard, "teams");
        fieldTeamsMap.setAccessible(true);
        
        try { // Replaces the Team known to the CraftScoreboard with the proxied Team
            Map teams = (Map) fieldTeamsMap.get(proxy.getScoreboard());
            teams.put(proxy.getName(), proxy);
            
            return proxy;
        } catch (IllegalArgumentException | IllegalAccessException e) {
            e.printStackTrace();
            return null;
        }
    }
}
