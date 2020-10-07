package mz.tech;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

/**
 * 手持科技物品点击鼠标事件
 * 攻击生物不触发该事件
 */
public class ClickItemEvent extends Event implements Listener , Cancellable
{	
    private static final HandlerList handlers = new HandlerList();
    
    /**
     * 触发事件的玩家
     */
    public Player player;
    /**
     * 科技物品的key
     */
    public String item;
    /**
     * 科技物品
     * 可以更改
     */
    public ItemStack itemStack;
    /**
     * 持有该物品的手
     */
    public EquipmentSlot hand;
    /**
     * 是否为左键
     * 否则为右键
     */
	public boolean leftClick;
	/**
	 * 是否点击的空气，及没有点击方块和实体
	 */
	public boolean clickAir;
	/**
	 * 是否点击方块
	 */
	public boolean clickBlock;
	/**
	 * 是否点击实体
	 */
	public boolean clickEntity;
	public boolean cancelled;
	/**
	 * 点击的实体（如果有的话）
	 */
	public Entity entity;
	/**
	 * 点击的方块（如果有的话）
	 */
	public Block block;

    public boolean isCancelled()
    {	
    	return cancelled;
    }
    public void setCancelled(boolean cancelled)
    {	
    	this.cancelled=cancelled;
    }
    @EventHandler
    void onPlayerInteract(PlayerInteractEvent event)
    {	
    	if(event.getAction()!=Action.PHYSICAL&&event.getItem()!=null&&event.getItem().getType()!=Material.AIR&&event.getItem().hasItemMeta()&&event.getItem().getItemMeta().hasLocalizedName())
    	{	
    		String locName=event.getItem().getType()!=Material.AIR?event.getItem().getItemMeta().getLocalizedName():"";
    		MzTech.items.forEach((s,i)->
    		{	
    			if(i.hasItemMeta()&&i.getItemMeta().hasLocalizedName()&&i.getItemMeta().getLocalizedName().equals(locName))
    			{	
    				ClickItemEvent e = new ClickItemEvent(event.getPlayer(),s,event.getHand(),event.getItem(),event.getAction()==Action.LEFT_CLICK_AIR||event.getAction()==Action.LEFT_CLICK_BLOCK,event.getAction()==Action.LEFT_CLICK_AIR||event.getAction()==Action.RIGHT_CLICK_AIR,event.getAction()==Action.LEFT_CLICK_BLOCK||event.getAction()==Action.RIGHT_CLICK_BLOCK,false,event.getClickedBlock(),null);
    				Bukkit.getServer().getPluginManager().callEvent(e);
        			if(e.isCancelled())
        			{	
        				event.setCancelled(true);
        			}
    			}
    		});
    	}
    }
	@EventHandler
    void onPlayerInteractEntity(PlayerInteractEntityEvent event)
    {	
		ItemStack item=event.getHand()==EquipmentSlot.OFF_HAND?event.getPlayer().getInventory().getItemInOffHand():event.getPlayer().getInventory().getItemInMainHand();
    	if(item.getType()!=Material.AIR&&item.hasItemMeta()&&item.getItemMeta().hasLocalizedName())
    	{	
    		String locName=item.getType()!=Material.AIR?item.getItemMeta().getLocalizedName():"";
    		MzTech.items.forEach((s,i)->
    		{	
    			if(i.hasItemMeta()&&i.getItemMeta().hasLocalizedName()&&i.getItemMeta().getLocalizedName().equals(locName))
    			{	
    				ClickItemEvent e = new ClickItemEvent(event.getPlayer(),s,event.getHand(),item,false,false,false,true,null,event.getRightClicked());
    				Bukkit.getServer().getPluginManager().callEvent(e);
        			if(e.isCancelled())
        			{	
        				event.setCancelled(true);
        			}
    			}
    		});
    	}
    }
	
    public ClickItemEvent(Player player,String item,EquipmentSlot hand,ItemStack itemStack,boolean leftClick,boolean clickAir,boolean clickBlock,boolean clickEntity,Block block,Entity entity)
    {	
    	this.player = player;
        this.item = item;
        this.hand = hand;
        this.itemStack=itemStack;
        this.leftClick=leftClick;
        this.clickAir=clickAir;
        this.clickBlock=clickBlock;
        this.clickEntity=clickEntity;
        this.entity=entity;
        this.block=block;
    }
    
    public ClickItemEvent()
    {	
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
