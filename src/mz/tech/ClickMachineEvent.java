package mz.tech;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

/**
 * 玩家点击科技方块的事件
 */
public class ClickMachineEvent extends Event implements Listener
{	
    private static final HandlerList handlers = new HandlerList();
    
    /**
     * 玩家
     */
    public Player player;
    /**
     * 方块的key（种类）
     */
    public String machine;
    /**
     * 是否左键（否则为右键）
     */
	public boolean leftClick;
	/**
	 * 点击的方块
	 */
	public Block block;
	public boolean cancelled;
    
    public ClickMachineEvent(Player player,String machine,boolean leftClick,Block block)
    {	
    	this.player = player;
        this.machine = machine;
        this.leftClick=leftClick;
        this.block=block;
    }
    public ClickMachineEvent()
    {	
    }
    public boolean isCancelled()
    {	
    	return cancelled;
    }
    public void setCancelled(boolean cancelled)
    {	
    	this.cancelled=cancelled;
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    void onPlayerInteract(PlayerInteractEvent event)
    {	
    	if(event.isCancelled())
    	{	
    		return;
    	}
    	BlockPlaceEvent place=new BlockPlaceEvent(event.getClickedBlock(),event.getClickedBlock().getState(),event.getClickedBlock(),new ItemStack(Material.AIR),event.getPlayer(),true,EquipmentSlot.HAND);
    	Bukkit.getPluginManager().callEvent(place);
    	if(place.isCancelled()||!place.canBuild())
    	{	
    		return;
    	}
    	switch(event.getAction())
    	{	
    	case RIGHT_CLICK_BLOCK:
    	case LEFT_CLICK_BLOCK:
    		String machine=MzTech.isMachine(event.getClickedBlock().getLocation());
    		if(machine != null)
    		{	
    			ClickMachineEvent e = new ClickMachineEvent(event.getPlayer(),machine,event.getAction()==Action.LEFT_CLICK_BLOCK?true:false,event.getClickedBlock());
    			Bukkit.getPluginManager().callEvent(e);
    			if(e.isCancelled())
    			{	
    				event.setCancelled(true);
    			}
    		}
    		break;
		default:
			break;
    	}
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
