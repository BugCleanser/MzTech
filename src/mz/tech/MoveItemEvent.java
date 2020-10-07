package mz.tech;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.DragType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * 一个格子到另一个格子的操作
 * 所有玩家对箱子容器（9格一行）的操作都会被转换为此事件
 * 不包括漏斗等自然移动
 */
public class MoveItemEvent extends Event implements Listener, Cancellable
{	
    private static final HandlerList handlers = new HandlerList();
    public List<Player> locks=new ArrayList<>();

    public Inventory fromInv;
    public int fromSlot;
    public Inventory toInv;
    public int toSlot;
    public int num;
    public ClickType clickType;
    public InventoryView inventoryView;
    public MoveItemEvent(Inventory fromInv,int fromSlot,Inventory toInv,int toSlot,int num,ClickType clickType, InventoryView inventoryView)
    {	
    	this.fromInv=fromInv;
    	this.fromSlot=fromSlot;
    	this.toInv=toInv;
    	this.toSlot=toSlot;
    	this.num=num;
    	this.clickType=clickType;
    	this.inventoryView=inventoryView;
    }
    
    private boolean cancelled=false;
    
    public MoveItemEvent()
    {	
    }
    
    public ItemStack getFromItem()
    {	
    	if(this.fromSlot<0)
    	{	
    		return this.inventoryView.getCursor();
    	}
    	else
    	{	
    		return this.fromInv.getItem(this.fromSlot);
    	}
    }
    
    public ItemStack getToItem()
    {	
    	if(this.num>0||this.toInv==null)
    	{	
    		return null;
    	}
    	if(this.toSlot<0)
    	{	
    		return this.inventoryView.getCursor();
    	}
    	else
    	{	
    		return this.toInv.getItem(this.toSlot);
    	}
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInventoryDrag(InventoryDragEvent event)
    {	
    	if(locks.contains(event.getWhoClicked()))
    	{	
    		event.setCancelled(true);
    		return;
    	}
    	if(event.getInventory().getType()==InventoryType.CHEST||event.getInventory().getType()==InventoryType.SHULKER_BOX)
    	{	
	    	if(event.isCancelled())
	    	{	
	    		return;
	    	}
	    	int everyCount=event.getType()==DragType.SINGLE?1:event.getOldCursor().getAmount()/event.getRawSlots().size();
	    	int[] i= {0};
	    	ItemStack cursor=event.getOldCursor();
	    	event.setCancelled(true);
	    	event.getWhoClicked().setItemOnCursor(cursor);
	    	event.getRawSlots().forEach(s->
	    	{	
	    		Inventory inv=s<event.getInventory().getSize()?
	    					event.getInventory():
	    					event.getWhoClicked().getInventory();
	    		MoveItemEvent e=new MoveItemEvent(event.getWhoClicked().getInventory(),-1,inv,
	    				(int) event.getInventorySlots().toArray()[i[0]],everyCount,event.getType()==DragType.EVEN?ClickType.LEFT:ClickType.RIGHT,event.getView());
	    		if(e.getToItem()!=null&&e.num+e.getToItem().getAmount()>e.getToItem().getType().getMaxStackSize())
	    		{	
	    			e.num=e.getToItem().getType().getMaxStackSize()-e.getToItem().getAmount();
	    		}
	    		Bukkit.getPluginManager().callEvent(e);
	    		if(!e.isCancelled())
	    		{	
	    			if(inv.getItem((int)event.getInventorySlots().toArray()[i[0]])!=null&&
	    					inv.getItem((int)event.getInventorySlots().toArray()[i[0]]).getType()!=Material.AIR)
	    			{	
	    				inv.getItem((int)event.getInventorySlots().toArray()[i[0]]).setAmount(inv.getItem((int)event.getInventorySlots().toArray()[i[0]]).getAmount()+e.num);
	    			}
	    			else
	    			{	
	    				ItemStack is=new ItemStack(event.getOldCursor());
	    				is.setAmount(e.num);
	    				inv.setItem((int)event.getInventorySlots().toArray()[i[0]],is);
	    			}
	    			cursor.setAmount(cursor.getAmount()-e.num);
	    		}
	    		i[0]++;
	    	});
	    	locks.add((Player)event.getWhoClicked());
	    	new BukkitRunnable()
	    	{	
	    		public void run()
	    		{	
	    	    	locks.remove((Player)event.getWhoClicked());
	    			event.getWhoClicked().setItemOnCursor(cursor);
	    		}
	    	}.runTask(MzTech.instance);
    	}
    }
    
    @SuppressWarnings("deprecation")
	@EventHandler(priority = EventPriority.HIGHEST)
    public void onInventoryClick(InventoryClickEvent event)
    {	
    	if(locks.contains(event.getWhoClicked()))
    	{	
    		event.setCancelled(true);
    		return;
    	}
    	if(event.getInventory().getType()==InventoryType.CHEST||event.getInventory().getType()==InventoryType.SHULKER_BOX)
    	{	
	    	if(event.isCancelled())
	    	{	
	    		return;
	    	}
	    	event.setCancelled(true);
	    	switch(event.getAction())
	    	{	
	    	case CLONE_STACK:
	    	{	
	    		ItemStack is=event.getClickedInventory().getItem(event.getSlot()).clone();
	    		if(isEmpty(is))break;
	    		is.setAmount(is.getType().getMaxStackSize());
	    		event.getWhoClicked().setItemOnCursor(is);
	    		break;
	    	}
	    	case COLLECT_TO_CURSOR:
	    	{	
	    		int surplus=event.getCursor().getType().getMaxStackSize()-event.getCursor().getAmount();
    			InventoryView inv=event.getWhoClicked().getOpenInventory();
    			//单独
    			if(surplus>0)
    			{	
    				for(int i=0;i<inv.getTopInventory().getSize();i++)
	    			{	
	    				if(surplus>0&&(!isEmpty(inv.getTopInventory().getItem(i)))&&inv.getTopInventory().getItem(i).isSimilar(event.getCursor())&&inv.getTopInventory().getItem(i).getAmount()<inv.getTopInventory().getItem(i).getType().getMaxStackSize())
	    				{	
	    					int num;
	    					if(inv.getTopInventory().getItem(i).getAmount()>surplus)
	    					{	
	    						num=surplus;
	    					}
	    					else
	    					{	
	    						num=inv.getTopInventory().getItem(i).getAmount();
	    					}
	    					if(num>0)
	    					{	
	    						MoveItemEvent e=new MoveItemEvent(inv.getTopInventory(),i,event.getWhoClicked().getInventory(),-999,num,event.getClick(),event.getView());
	    						Bukkit.getPluginManager().callEvent(e);
	    						if(!e.isCancelled())
	    						{	
	    							inv.getTopInventory().getItem(i).setAmount(inv.getTopInventory().getItem(i).getAmount()-num);
	    							surplus-=num;
	    						}
	    					}
	    				}
	    			}
    			}
    			if(surplus>0)
    			{	
    				for(int i=0;i<inv.getBottomInventory().getSize();i++)
	    			{	
	    				if(surplus>0&&(!isEmpty(inv.getBottomInventory().getItem(i)))&&inv.getBottomInventory().getItem(i).isSimilar(event.getCursor())&&inv.getBottomInventory().getItem(i).getAmount()<inv.getBottomInventory().getItem(i).getType().getMaxStackSize())
	    				{	
	    					int num;
	    					if(inv.getBottomInventory().getItem(i).getAmount()>surplus)
	    					{	
	    						num=surplus;
	    					}
	    					else
	    					{	
	    						num=inv.getBottomInventory().getItem(i).getAmount();
	    					}
	    					if(num>0)
	    					{	
	    						MoveItemEvent e=new MoveItemEvent(inv.getBottomInventory(),i,event.getWhoClicked().getInventory(),-999,num,event.getClick(),event.getView());
	    						Bukkit.getPluginManager().callEvent(e);
	    						if(!e.isCancelled())
	    						{	
	    							inv.getBottomInventory().getItem(i).setAmount(inv.getBottomInventory().getItem(i).getAmount()-num);
	    							surplus-=num;
	    						}
	    					}
	    				}
	    			}
    			}
    			//整组
    			if(surplus>0)
    			{	
    				for(int i=0;i<inv.getTopInventory().getSize();i++)
	    			{	
	    				if(surplus>0&&(!isEmpty(inv.getTopInventory().getItem(i)))&&inv.getTopInventory().getItem(i).isSimilar(event.getCursor())&&inv.getTopInventory().getItem(i).getAmount()>=inv.getTopInventory().getItem(i).getType().getMaxStackSize())
	    				{	
							MoveItemEvent e=new MoveItemEvent(inv.getTopInventory(),i,event.getWhoClicked().getInventory(),-1,surplus,event.getClick(),event.getView());
							Bukkit.getPluginManager().callEvent(e);
							if(!e.isCancelled())
							{	
								inv.getTopInventory().getItem(i).setAmount(inv.getTopInventory().getItem(i).getAmount()-surplus);
								surplus=0;
								break;
							}
	    				}
	    			}
    			}
    			if(surplus>0)
    			{	
    				for(int i=0;i<inv.getBottomInventory().getSize();i++)
	    			{	
	    				if(surplus>0&&(!isEmpty(inv.getBottomInventory().getItem(i)))&&inv.getBottomInventory().getItem(i).isSimilar(event.getCursor())&&inv.getBottomInventory().getItem(i).getAmount()>=inv.getBottomInventory().getItem(i).getType().getMaxStackSize())
	    				{	
							MoveItemEvent e=new MoveItemEvent(inv.getBottomInventory(),i,event.getWhoClicked().getInventory(),-1,surplus,event.getClick(),event.getView());
							Bukkit.getPluginManager().callEvent(e);
							if(!e.isCancelled())
							{	
								inv.getBottomInventory().getItem(i).setAmount(inv.getBottomInventory().getItem(i).getAmount()-surplus);
								surplus=0;
								break;
							}
	    				}
	    			}
    			}
    			inv.getCursor().setAmount(event.getCursor().getType().getMaxStackSize()-surplus);
    			break;
	    	}
			case DROP_ALL_CURSOR:
			{	
				MoveItemEvent e=new MoveItemEvent(event.getWhoClicked().getInventory(),-1,null,0,event.getCursor().getAmount(),event.getClick(),event.getView());
				Bukkit.getPluginManager().callEvent(e);
				if(!e.isCancelled())
				{	
					event.setCancelled(false);
				}
				break;
	    	}
			case DROP_ALL_SLOT:
			{	
				if(isEmpty(event.getCurrentItem()))
				{	
					break;
				}
				MoveItemEvent e=new MoveItemEvent(event.getClickedInventory(),event.getSlot(),null,0,event.getClickedInventory().getItem(event.getSlot()).getAmount(),event.getClick(),event.getView());
				Bukkit.getPluginManager().callEvent(e);
				if(!e.isCancelled())
				{	
					event.setCancelled(false);
				}
				break;
			}
			case DROP_ONE_CURSOR:
			{	
				MoveItemEvent e=new MoveItemEvent(event.getWhoClicked().getInventory(),-1,null,0,1,event.getClick(),event.getView());
				Bukkit.getPluginManager().callEvent(e);
				if(!e.isCancelled())
				{	
					event.setCancelled(false);
				}
				break;
	    	}
			case DROP_ONE_SLOT:
			{	
				if(isEmpty(event.getCurrentItem()))
				{	
					break;
				}
				MoveItemEvent e=new MoveItemEvent(event.getClickedInventory(),event.getSlot(),null,0,1,event.getClick(),event.getView());
				Bukkit.getPluginManager().callEvent(e);
				if(!e.isCancelled())
				{	
					event.setCancelled(false);
				}
				break;
			}
			case PLACE_ALL:
			{	
				MoveItemEvent e=new MoveItemEvent(event.getWhoClicked().getInventory(),-1,event.getClickedInventory(),event.getSlot(),event.getCursor().getAmount(),event.getClick(),event.getView());
				Bukkit.getPluginManager().callEvent(e);
				if(!e.isCancelled())
				{	
					int num=event.getCursor().getAmount();
					if(!isEmpty(event.getClickedInventory().getItem(event.getSlot())))
					{	
						num+=event.getClickedInventory().getItem(event.getSlot()).getAmount();
					}
					event.getClickedInventory().setItem(event.getSlot(),event.getCursor());
					event.getClickedInventory().getItem(event.getSlot()).setAmount(num);
					event.getCursor().setAmount(0);
				}
				break;
			}
			case PLACE_ONE:
			{	
				MoveItemEvent e=new MoveItemEvent(event.getWhoClicked().getInventory(),-1,event.getClickedInventory(),event.getSlot(),1,event.getClick(),event.getView());
				Bukkit.getPluginManager().callEvent(e);
				if(!e.isCancelled())
				{	
					ItemStack is=event.getCurrentItem();
					if(is==null||is.getType()==Material.AIR||is.getAmount()==0)
					{	
						is=event.getCursor().clone();
						is.setAmount(e.num);
					}
					else
					{	
						is.setAmount(is.getAmount()+e.num);
					}
					event.setCurrentItem(is);
					event.getCursor().setAmount(event.getCursor().getAmount()-e.num);
				}
				break;
			}
			case PICKUP_ALL:
			{	
				if(isEmpty(event.getCurrentItem()))
				{	
					break;
				}
				MoveItemEvent e=new MoveItemEvent(event.getClickedInventory(),event.getSlot(),event.getWhoClicked().getInventory(),-1,event.getCurrentItem().getAmount(),event.getClick(),event.getView());
				Bukkit.getPluginManager().callEvent(e);
				if(!e.isCancelled())
				{	
					event.setCursor(event.getCurrentItem());
					event.setCurrentItem(new ItemStack(Material.AIR));
				}
				break;
			}
			case PICKUP_HALF:
			{	
				if(isEmpty(event.getCurrentItem()))
				{	
					break;
				}
				MoveItemEvent e=new MoveItemEvent(event.getClickedInventory(),event.getSlot(),event.getWhoClicked().getInventory(),-1,(event.getCurrentItem().getAmount()+1)/2,event.getClick(),event.getView());
				Bukkit.getPluginManager().callEvent(e);
				if(!e.isCancelled())
				{	
					event.setCursor(event.getCurrentItem());
					event.getCursor().setAmount(e.num);
					event.getCurrentItem().setAmount(event.getCurrentItem().getAmount()-e.num);
				}
				break;
			}
			case HOTBAR_MOVE_AND_READD:
			case HOTBAR_SWAP:
			{	
				MoveItemEvent e=null;
				if((!isEmpty(event.getCurrentItem()))&&(!isEmpty(event.getWhoClicked().getInventory().getItem(event.getHotbarButton()))))
				{	
					e=new MoveItemEvent(event.getWhoClicked().getInventory(),event.getHotbarButton(),event.getClickedInventory(),event.getSlot(),0,event.getClick(),event.getView());
				}
				else if(!isEmpty(event.getCurrentItem()))
				{	
					e=new MoveItemEvent(event.getClickedInventory(),event.getSlot(),event.getWhoClicked().getInventory(),event.getHotbarButton(),event.getCurrentItem().getAmount(),event.getClick(),event.getView());
				}
				else if(!isEmpty(event.getWhoClicked().getInventory().getItem(event.getHotbarButton())))
				{	
					e=new MoveItemEvent(event.getWhoClicked().getInventory(),event.getHotbarButton(),event.getClickedInventory(),event.getSlot(),event.getWhoClicked().getInventory().getItem(event.getHotbarButton()).getAmount(),event.getClick(),event.getView());
				}
				else
				{	
					break;
				}
				
				Bukkit.getPluginManager().callEvent(e);
				if(!e.isCancelled())
				{	
					ItemStack is=event.getCurrentItem();
					if(MoveItemEvent.isEmpty(is))
					{	
						is=new ItemStack(Material.AIR);
					}
					event.setCurrentItem(event.getWhoClicked().getInventory().getItem(event.getHotbarButton()));
					event.getWhoClicked().getInventory().setItem(event.getHotbarButton(),is);
				}
				break;
			}
			case SWAP_WITH_CURSOR:
			{	
				if(isEmpty(event.getCurrentItem()))
				{	
					MoveItemEvent e=new MoveItemEvent(event.getWhoClicked().getInventory(),-1,event.getClickedInventory(),event.getSlot(),event.getCursor().getAmount(),event.getClick(),event.getView());
					Bukkit.getPluginManager().callEvent(e);
					if(!e.isCancelled())
					{	
						event.setCurrentItem(event.getCursor());
						event.setCursor(new ItemStack(Material.AIR));
					}
				}
				else
				{	
					MoveItemEvent e=new MoveItemEvent(event.getWhoClicked().getInventory(),-1,event.getClickedInventory(),event.getSlot(),0,event.getClick(),event.getView());
					Bukkit.getPluginManager().callEvent(e);
					if(!e.isCancelled())
					{	
						ItemStack is=event.getCursor();
						event.setCursor(event.getCurrentItem());
						event.setCurrentItem(is);
					}
				}
				break;
			}
			case PLACE_SOME:
			{	
				MoveItemEvent e=new MoveItemEvent(event.getWhoClicked().getInventory(),-1,event.getClickedInventory(),event.getSlot(),event.getCurrentItem().getType().getMaxStackSize()-event.getCurrentItem().getAmount(),event.getClick(),event.getView());
				Bukkit.getPluginManager().callEvent(e);
				if(!e.isCancelled())
				{	
					event.getCursor().setAmount(event.getCursor().getAmount()-e.num);
					event.getCurrentItem().setAmount(event.getCurrentItem().getType().getMaxStackSize());
				}
				break;
			}
			case MOVE_TO_OTHER_INVENTORY:
			{	
				if(isEmpty(event.getCurrentItem()))
				{	
					break;
				}
				int surplus=event.getCurrentItem().getAmount();
				if(event.getClickedInventory()!=event.getInventory())// 背包 → 箱子
				{	
					Inventory inv=event.getInventory();
					for(int i = 0;i<inv.getSize();i++)
					{	
						if(surplus>0&&(!isEmpty(inv.getItem(i)))&&inv.getItem(i).isSimilar(event.getCurrentItem()))
						{	
							int size=0;
							if(inv.getItem(i).getAmount()+surplus>event.getCurrentItem().getType().getMaxStackSize())
							{	
								size=event.getCurrentItem().getType().getMaxStackSize()-inv.getItem(i).getAmount();
							}
							else
							{	
								size=surplus;
							}
							MoveItemEvent e=new MoveItemEvent(event.getClickedInventory(),event.getSlot(),inv,i,size,event.getClick(),event.getView());
							Bukkit.getPluginManager().callEvent(e);
							if(!e.isCancelled())
							{	
								inv.getItem(i).setAmount(inv.getItem(i).getAmount()+e.num);
								surplus-=size;
							}
						}
					}
					if(surplus>0)
					{	
						List<Integer> empties = getChestEmpties(inv);
						int[] aSurplus= {surplus};
						empties.forEach(i->
						{	
							if(aSurplus[0]>0)
							{	
								MoveItemEvent e=new MoveItemEvent(event.getClickedInventory(),event.getSlot(),inv,i,aSurplus[0],event.getClick(),event.getView());
								Bukkit.getPluginManager().callEvent(e);
								if(!e.isCancelled())
								{	
									ItemStack is=event.getCurrentItem().clone();
									is.setAmount(aSurplus[0]);
									inv.setItem(i,is);
									aSurplus[0]=0;
								}
							}
						});
						surplus=aSurplus[0];
					}
				}
				else// 箱子 → 背包
				{	
					PlayerInventory inv=event.getWhoClicked().getInventory();
			    	for(int i=8;i>=0;i--)
					{	
						if(surplus>0&&(!isEmpty(inv.getItem(i)))&&inv.getItem(i).isSimilar(event.getCurrentItem()))
						{	
							int size=0;
							if(inv.getItem(i).getAmount()+surplus>event.getCurrentItem().getType().getMaxStackSize())
							{	
								size=event.getCurrentItem().getType().getMaxStackSize()-inv.getItem(i).getAmount();
							}
							else
							{	
								size=surplus;
							}
							MoveItemEvent e=new MoveItemEvent(event.getClickedInventory(),event.getSlot(),inv,i,size,event.getClick(),event.getView());
							Bukkit.getPluginManager().callEvent(e);
							if(!e.isCancelled())
							{	
								inv.getItem(i).setAmount(inv.getItem(i).getAmount()+e.num);
								surplus-=size;
							}
						}
					}
			    	for(int i=35;i>=9;i--)
					{	
						if(surplus>0&&(!isEmpty(inv.getItem(i)))&&inv.getItem(i).isSimilar(event.getCurrentItem()))
						{	
							int size=0;
							if(inv.getItem(i).getAmount()+surplus>event.getCurrentItem().getType().getMaxStackSize())
							{	
								size=event.getCurrentItem().getType().getMaxStackSize()-inv.getItem(i).getAmount();
							}
							else
							{	
								size=surplus;
							}
							MoveItemEvent e=new MoveItemEvent(event.getClickedInventory(),event.getSlot(),inv,i,size,event.getClick(),event.getView());
							Bukkit.getPluginManager().callEvent(e);
							if(!e.isCancelled())
							{	
								inv.getItem(i).setAmount(inv.getItem(i).getAmount()+e.num);
								surplus-=size;
							}
						}
					}
					if(surplus>0)
					{	
						List<Integer> empties = playerInventoryEmpties(inv);
						int[] aSurplus= {surplus};
						empties.forEach(i->
						{	
							if(aSurplus[0]>0)
							{	
								MoveItemEvent e=new MoveItemEvent(event.getClickedInventory(),event.getSlot(),inv,i,aSurplus[0],event.getClick(),event.getView());
								Bukkit.getPluginManager().callEvent(e);
								if(!e.isCancelled())
								{	
									ItemStack is=event.getCurrentItem().clone();
									is.setAmount(aSurplus[0]);
									inv.setItem(i,is);
									aSurplus[0]=0;
								}
							}
						});
						surplus=aSurplus[0];
					}
				}
				event.getCurrentItem().setAmount(surplus);
				return;
			}
			case UNKNOWN:
			case PICKUP_SOME://疑惑行为
			case PICKUP_ONE://疑惑行为
				event.getWhoClicked().sendMessage(MzTech.MzTechPrefix+"§4疑惑行为（"+event.getAction()+"）！请反馈管理员该消息如何触发");
			case NOTHING:
				break;
	    	}
    	}
    }
    
    public static boolean isEmpty(ItemStack is)
    {	
    	return is==null||is.getType()==Material.AIR||(is.hasItemMeta()&&is.getItemMeta().hasLocalizedName()&&is.getItemMeta().getLocalizedName().startsWith("§槽"));
    }
    public List<Integer> getChestEmpties(Inventory inv)
    {	
    	List<Integer> rl=new ArrayList<>();
    	for(int i=0;i<inv.getSize();i++)
    	{	
    		if(isEmpty(inv.getItem(i)))
    		{	
    			rl.add(i);
    		}
    	}
    	return rl;
    }
    public List<Integer> playerInventoryEmpties(PlayerInventory inv)
    {	
    	List<Integer> rl=new ArrayList<>();
    	for(int i=8;i>=0;i--)
    	{	
    		if(isEmpty(inv.getItem(i)))
    		{	
    			rl.add(i);
    		}
    	}
    	for(int i=35;i>=9;i--)
    	{	
    		if(isEmpty(inv.getItem(i)))
    		{	
    			rl.add(i);
    		}
    	}
    	return rl;
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
