package mz.tech;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * （未实现）
 * 使用科技机器合成物品的事件
 */
public class CraftItemEvent extends Event
{	
    private static final HandlerList handlers = new HandlerList();
	public HandlerList getHandlers()
    {	
         return handlers;
    }
    public static HandlerList getHandlerList()
    {	
        return handlers;
    }
    
    
    
}
