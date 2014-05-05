package com.gmail.favorlock.commonutils.scoreboard.impl;

import java.lang.ref.WeakReference;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import org.bukkit.Bukkit;
import org.bukkit.scoreboard.Score;

import com.gmail.favorlock.commonutils.scoreboard.api.events.ScoreboardScoreChangeEvent;
import com.gmail.favorlock.commonutils.scoreboard.api.events.ScoreboardTeamScoreChangeEvent;
import com.gmail.favorlock.commonutils.scoreboard.api.wrappers.ObjectiveWrapper;
import com.gmail.favorlock.commonutils.scoreboard.api.wrappers.TeamWrapper;

public class ScoreProxy implements InvocationHandler {

    private final WeakReference<ObjectiveWrapper> wrapper;
    private final Score proxying;

    private ScoreProxy(ObjectiveWrapper wrapper, Score proxying) {
        this.wrapper = new WeakReference<>(wrapper);
        this.proxying = proxying;
    }

    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (method.getName().equals("setScore")) {
            Class<?>[] params = method.getParameterTypes();

            if (params.length == 1 && params[0].equals(int.class)) {
                int oldscore = proxying.getScore();
                int newscore = (int) args[0];
                
                ObjectiveWrapper objectiveWrapper = wrapper.get();
                
                if (objectiveWrapper != null) {
                    String entry = proxying.getEntry();
                    ScoreboardScoreChangeEvent scoreChange = new ScoreboardScoreChangeEvent(
                            objectiveWrapper, entry, oldscore, newscore);
                    Bukkit.getPluginManager().callEvent(scoreChange);
                    TeamWrapper teamWrapper = objectiveWrapper.getWrapper().getTeamForEntry(entry);
                    
                    if (teamWrapper != null) {
                        int oldteamscore = teamWrapper.getTotalScores(objectiveWrapper);
                        int newteamscore = oldteamscore + (newscore - oldscore);
                        
                        ScoreboardTeamScoreChangeEvent teamChange = new ScoreboardTeamScoreChangeEvent(
                                objectiveWrapper, teamWrapper, entry, oldteamscore, newteamscore);
                        teamChange.setCancelled(scoreChange.isCancelled());
                        Bukkit.getPluginManager().callEvent(teamChange);
                        
                        if (teamChange.isCancelled()) {
                            return null;
                        }
                    } else {
                        if (scoreChange.isCancelled()) {
                            return null;
                        }
                    }
                }
            }
        }

        return method.invoke(proxying, args);
    }

    /**
     * Create a new proxy for a Score. If the given Score is already a proxy,
     * nothing will be changed, and the existing proxy will be returned.
     * 
     * @param score The Score that should be proxied.
     * @return A Score instance that proxies the given Score instance.
     */
    protected static Score newProxy(ObjectiveWrapper wrapper, Score score) {
        if (Proxy.isProxyClass(score.getClass())) {
            return score;
        }

        return (Score) Proxy.newProxyInstance(
                ScoreProxy.class.getClassLoader(),
                new Class<?>[] { Score.class },
                new ScoreProxy(wrapper, score));
    }
}
