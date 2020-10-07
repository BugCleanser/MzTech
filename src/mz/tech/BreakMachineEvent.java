package mz.tech;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

/**
 * 玩家破坏科技方块API
 */
public class BreakMachineEvent extends Event implements Cancellable , Listener
{	
    private static final HandlerList handlers = new HandlerList();
    
    /**
     * 玩家
     */
    public Player player;
    /**
     * 机器的key
     */
    public String machine;
    /**
     * 机器的block
     */
    public Block block;
    /**
     * 是否掉落机器
     */
    public boolean drops=true;
    
    private boolean cancelled=false;

	public boolean isCancelled()
    {	
    	return cancelled;
    }
    public void setCancelled(boolean cancelled)
    {	
    	this.cancelled=cancelled;
    }
	public boolean isDrops()
    {	
    	return drops;
    }
    public void setDrops(boolean drops)
    {	
    	this.drops=drops;
    }
	public HandlerList getHandlers()
    {	
         return handlers;
    }
    public static HandlerList getHandlerList()
    {	
        return handlers;
    }
}
