package mz.tech;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;

/**
 * 玩家食用科技物品的事件
 */
public class EatItemEvent extends Event implements Listener
{	
    private static final HandlerList handlers = new HandlerList();
    
    public Player player;
    public String item;
	public ItemStack itemStack;
	public boolean cancelled;

	public EatItemEvent()
	{	
	}
	public EatItemEvent(Player player,String item,ItemStack itemStack)
	{	
		this.player=player;
		this.item=item;
		this.itemStack=itemStack;
	}
    public HandlerList getHandlers()
    {	
         return handlers;
    }
    public static HandlerList getHandlerList()
    {	
        return handlers;
    }
    public boolean isCancelled()
    {	
    	return cancelled;
    }
    public void setCancelled(boolean cancelled)
    {	
    	this.cancelled=cancelled;
    }
    
    @EventHandler
    public void onPlayerItemConsume(PlayerItemConsumeEvent event)
    {	
    	if(event.getItem().getItemMeta().hasLocalizedName())
    	{	
	    	MzTech.items.forEach((s,is)->
	    	{	
	    		if(is.getItemMeta().hasLocalizedName()&&is.getItemMeta().getLocalizedName().equals(event.getItem().getItemMeta().getLocalizedName()))
	    		{	
	    			EatItemEvent e=new EatItemEvent(event.getPlayer(),s,event.getItem());
	    			e.setCancelled(event.isCancelled());
	    			Bukkit.getPluginManager().callEvent(e);
	    			event.setCancelled(e.isCancelled());
	    		}
	    	});
    	}
    }
}