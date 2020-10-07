package mz.tech;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * 高温化合锅模块及其配方API
 */
public class ChemicalFurnace extends ShowCraftGuide implements Listener
{	
	public ChemicalFurnace()
	{	
		ItemStack chemicalFurnace;
		try
		{	
			chemicalFurnace=new ItemStack(Material.CAULDRON_ITEM);
		}
		catch(Error e)
		{	
			chemicalFurnace=new ItemStack(Enum.valueOf(Material.class,"CAULDRON"));
		}
		ItemMeta im = chemicalFurnace.getItemMeta();
		im.setLocalizedName("§4高温化合锅");
		chemicalFurnace.setItemMeta(im);
		MzTech.items.put("高温化合锅",chemicalFurnace);
		Raw air=new Raw(null,new ItemStack(Material.AIR));
		Raw ironIngot=new Raw(null,new ItemStack(Material.IRON_INGOT));
		Raw glass=new Raw(null,new ItemStack(Material.GLASS));
		Raw furnace=new Raw(null,new ItemStack(Material.FURNACE));
		Raw anvil=new Raw(null,new ItemStack(Material.ANVIL));
		SmilingCraftingTable.add("高温化合锅",chemicalFurnace,null,
				ironIngot,	air	,		ironIngot,
				glass,		air	,		glass,
				anvil,		furnace,	anvil);
		CraftGuide.addCraftTable("基础机器",SmilingCraftingTable.class,"高温化合锅");
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
    void onClickMachine(ClickMachineEvent event)
    {	
		if(event.cancelled)
			return;
    	switch(event.machine)
    	{	
    	case "高温化合锅":
    		if(MzTech.canOpen(event.player)&&!event.leftClick)
    		{	
    			event.setCancelled(true);
    			Inventory inv=MzTech.getMachineInventory(event.block.getLocation());
    			if(inv==null)
    			{	
    				inv=Bukkit.createInventory(null,54,MzTech.items.get("高温化合锅").getItemMeta().getLocalizedName());
    				ItemStack glassPane=new ItemStack(Material.STAINED_GLASS_PANE);
    				glassPane.setDurability((short)5);
    				ItemMeta im = glassPane.getItemMeta();
    				im.setLocalizedName("§板");
    				glassPane.setItemMeta(im);
    				for(int i=0;i<9;i++)
    				{	
    					inv.setItem(36+i,glassPane);
    				}
    				glassPane.setDurability((short)14);
    				for(int i=0;i<9;i++)
    				{	
    					inv.setItem(45+i,glassPane);
    				}
    				inv.setItem(40,new ItemStack(Material.AIR));
    				ItemStack coalSlot=new ItemStack(Material.COAL);
    				im=coalSlot.getItemMeta();
    				im.setLocalizedName("§槽煤炭槽");
    				coalSlot.setItemMeta(im);
    				inv.setItem(49,coalSlot);
    				MzTech.setMachineInventory(event.block.getLocation(),inv);
    			}
    			event.player.openInventory(inv);
    		}
    		break;
    	}
    }
	
	@EventHandler
	void onMoveItem(MoveItemEvent event)
	{	
		Inventory inv=null;
		int slot=-1;
		if(MzTech.isMachineInventory(event.fromInv,"高温化合锅"))
		{	
			inv=event.fromInv;
			slot=event.fromSlot;
		}
		else if(MzTech.isMachineInventory(event.toInv,"高温化合锅"))
		{	
			inv=event.toInv;
			slot=event.toSlot;
		}
		else
		{	
			return;
		}
		ItemStack fromItem=event.fromSlot>=0?event.fromInv.getItem(event.fromSlot):((Player)event.fromInv.getHolder()).getItemOnCursor();
		if(slot==49&&fromItem.getType()==Material.COAL)
		{	
			if(inv==event.fromInv&&inv.getItem(49).getAmount()==event.num)
			{	
				Inventory finalInv=inv;
				new BukkitRunnable()
				{	
					public void run()
					{	
						if(finalInv.getItem(49)==null||finalInv.getItem(49).getType()==Material.AIR)
						{	
		    				ItemStack coalSlot=new ItemStack(Material.COAL);
		    				ItemMeta im = coalSlot.getItemMeta();
		    				im.setLocalizedName("§槽煤炭槽");
		    				coalSlot.setItemMeta(im);
		    				finalInv.setItem(49,coalSlot);
						}
					}
				}.runTask(MzTech.instance);
			}
		}
		else if(!(slot<36))
		{	
			event.setCancelled(true);
		}
	}
}
