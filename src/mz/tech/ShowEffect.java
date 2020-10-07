package mz.tech;

import java.util.List;


import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.google.common.collect.Lists;

/**
 * 显示效果的模块
 * 暂无API
 */
public class ShowEffect implements Listener
{	
	public ShowEffect()
	{	
		ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(MzTech.instance, ListenerPriority.HIGHEST, new PacketType[] {PacketType.Play.Client.SET_CREATIVE_SLOT})
	    {	
    		public void onPacketReceiving(PacketEvent event)
    		{	
    			if(event.getPacketType()==PacketType.Play.Client.SET_CREATIVE_SLOT)
    			{	
	    			ItemStack copy=event.getPacket().getItemModifier().read(0);
	    			if(copy!=null&&copy.getType()!=Material.AIR)
	    			{	
	    				ItemMeta im = copy.getItemMeta();
		    			if(im.hasDisplayName()&&im.hasLocalizedName()&&im.getDisplayName().equals("§r"+im.getLocalizedName()))
		    			{	
			    			im.setDisplayName(null);
			    			copy.setItemMeta(im);
							event.getPacket().getItemModifier().write(0,copy);
		    			}
		    			if(im.hasLore())
		    			{	
							List<String> lores = im.getLore();
							@SuppressWarnings({ "unchecked", "rawtypes" })
							List<String> copyLores=(List)Lists.newArrayList(lores.toArray());
							lores.forEach(l->
							{	
								if(l.startsWith("§临"))
								{	
									copyLores.remove(l);
									if(copy.getType()==Material.ENCHANTED_BOOK)
									{	
										im.removeItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
									}
									else
									{	
										im.removeItemFlags(ItemFlag.HIDE_ENCHANTS);
									}
								}
							});
							im.setLore(copyLores);
							copy.setItemMeta(im);
							event.getPacket().getItemModifier().write(0,copy);
		    			}
	    			}
    			}
    		}
    	});
	}
	@EventHandler
	void onPrepareItemCraft(PrepareItemCraftEvent event)
	{	
		if(event.isRepair())
		{	
			String[] item = {null};
			Lists.newArrayList(event.getInventory().getMatrix()).forEach(i->
			{	
				if(i!=null&&i.getType()!=Material.AIR)
				{	
					if(i.getItemMeta().getLocalizedName()==null)
					{	
						if(item[0]==null)
						{	
							item[0]="";
						}
						else if(!item[0].equals(""))
						{	
							event.getInventory().setResult(new ItemStack(Material.AIR));
						}
					}
					else
					{	
						if(item[0]==null)
						{	
							item[0]=MzTech.isItem(i);
						}
						else if(!item[0].equals(MzTech.isItem(i)))
						{	
							event.getInventory().setResult(new ItemStack(Material.AIR));
						}
					}
				}
			});
			if(item[0]!=null&&(!item[0].equals(""))&&event.getInventory().getResult().getType()!=Material.AIR)
			{	
				ItemStack is=new ItemStack(MzTech.items.get(item[0]));
				is.setDurability(event.getInventory().getResult().getDurability());
				event.getInventory().setResult(is);
			}
		}
		else
		{	
			Lists.newArrayList(event.getInventory().getMatrix()).forEach(i->
			{	
				if(i!=null&&i.getType()!=Material.AIR&&i.getItemMeta().hasLocalizedName()&&!(i.getType().getMaxDurability()>0&&i.getDurability()>0))
				{	
					event.getInventory().setResult(new ItemStack(Material.AIR));
				}
			});
		}
	}
	
	@EventHandler
	void onShowItem(ShowItemEvent event)
	{	
		event.itemStack=Enchants.enchantToLore(event.itemStack);
		final boolean AllowHighVersion=(Boolean)MzTech.instance.config.get("AllowHighVersion");
		if(AllowHighVersion)
		{	
			ItemMeta itemMeta = event.itemStack.getItemMeta();
			if(itemMeta.hasLocalizedName()&&!itemMeta.hasDisplayName())
			{	
				itemMeta.setDisplayName("§r"+itemMeta.getLocalizedName());
				event.itemStack.setItemMeta(itemMeta);
			}
		}
	}
}
