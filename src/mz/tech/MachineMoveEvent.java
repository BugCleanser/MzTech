package mz.tech;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * 科技方块移动事件
 * 通常为活塞推动
 */
public class MachineMoveEvent extends Event implements Cancellable
{	
    private static final HandlerList handlers = new HandlerList();
    
    public String machine;
    public Block block;
    public Block targetBlock;
    
    private boolean cancelled=false;
    
    public MachineMoveEvent(Block block,BlockFace direction)
    {	
    	this.machine=MzTech.isMachine(block.getLocation());
    	this.block=block;
    	Location place=block.getLocation();
		switch(direction)
		{	
		case UP:
			place.add(0,1,0);
			break;
		case DOWN:
			place.add(0,-1,0);
			break;
		case EAST:
			place.add(1,0,0);
			break;
		case SOUTH:
			place.add(0,0,1);
			break;
		case WEST:
			place.add(-1,0,0);
			break;
		case NORTH:
			place.add(0,0,-1);
			break;
		default:
			break;
		}
		this.targetBlock=place.getBlock();
    }
    
    
	public boolean isCancelled()
    {	
    	return cancelled;
    }
    public void setCancelled(boolean cancelled)
    {	
    	this.cancelled=cancelled;
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
