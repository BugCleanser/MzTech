package mz.tech;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

/**
 * 玩家放置科技方块事件
 */
public class PlaceMachineEvent extends Event implements Listener
{	
    private static final HandlerList handlers = new HandlerList();
    
    public Player player;
    public String machine;
	public Block block;
	public Block blockAgainst;
	public Block blockBefor;
	public boolean cancelled;

    
    public HandlerList getHandlers()
    {	
         return handlers;
    }
    public static HandlerList getHandlerList()
    {	
        return handlers;
    }
    public PlaceMachineEvent()
    {	
    }
    public PlaceMachineEvent(Player player, String machine, Block block, Block blockAgainst,
			Block blockBefor)
    {	
    	this.player=player;
    	this.machine=machine;
    	this.block=block;
    	this.blockAgainst=blockAgainst;
    	this.blockBefor=blockBefor;
	}
	public boolean isCancelled()
    {	
    	return cancelled;
    }
    public void setCancelled(boolean cancelled)
    {	
    	this.cancelled=cancelled;
    }
}
