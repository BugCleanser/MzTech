package mz.tech;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;

/**
 * 为玩家显示一个物品的事件
 * 不包括物品掉落物
 * 不包括展示框中的物品
 * 不包括聊天栏中的物品
 * 不包括创造模式物品栏中的物品
 */
public class ShowItemEvent extends Event implements Cancellable, Listener
{	
	/**
	 * 显示的物品
	 * 可以覆盖
	 */
	public ItemStack itemStack;
	
	public ShowItemEvent()
	{	
    	ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(MzTech.instance, ListenerPriority.NORMAL, new PacketType[] {PacketType.Play.Server.SET_SLOT,PacketType.Play.Server.WINDOW_ITEMS,PacketType.Play.Client.SET_CREATIVE_SLOT})
	    {	
    		public void onPacketSending(PacketEvent event)
    		{	
				PacketContainer packet=event.getPacket();
				if (event.getPacketType().equals(PacketType.Play.Server.SET_SLOT))
		        {	
		        	ItemStack itemStack = (ItemStack)packet.getItemModifier().read(0);
		        	if ((itemStack != null) && (!itemStack.getType().equals(Material.AIR)))
			        {	
		        		ShowItemEvent e=new ShowItemEvent(itemStack);
		        		Bukkit.getPluginManager().callEvent(e);
		        		itemStack=e.itemStack;
		        		if(e.isCancelled())
		        		{	
		        			event.setCancelled(true);
		        		}
		        	}
		        	return;
		        }
		        else if (event.getPacketType().equals(PacketType.Play.Server.WINDOW_ITEMS))
		        {
		        	List<ItemStack> list = new ArrayList<>();
		        	if (packet.getItemListModifier().size() > 0)
		        	{
		        		list.addAll((Collection<ItemStack>)packet.getItemListModifier().read(0));
		        	}
		        	else
		        	{	
		        		list.addAll(Arrays.asList((ItemStack[])packet.getItemArrayModifier().read(0)));
		        	}
		        	for (int i=0;i<list.size();i++)
		        	{
		        		if ((list.get(i) != null) && (!list.get(i).getType().equals(Material.AIR)))
		        		{	
		        			ShowItemEvent e=new ShowItemEvent(list.get(i));
			        		Bukkit.getPluginManager().callEvent(e);
		        			list.set(i,Enchants.enchantToLore(e.itemStack));
			        		if(e.isCancelled())
			        		{	
			        			event.setCancelled(true);
			        		}
			            }
		        	}
			        if (packet.getItemListModifier().size() > 0)
			        {	
			            packet.getItemListModifier().write(0, list);
			        }
			        else
			        {	
			            packet.getItemArrayModifier().write(0, (ItemStack[])list.toArray(new ItemStack[list.size()]));
			        }
			        return;
		        }
		        /*else if (event.getPacketType().equals(PacketType.Play.Server.CHAT))
		        {	
		        	String meta=(String)event.getPacket().getMetadata("json");
		        	if(meta!=null)
		        	{	
			        	JsonObject json = new JsonParser().parse(meta).getAsJsonObject();
			        	JsonArray array=json.getAsJsonArray("extra");
			        	for(int j=0;j<array.size();j++)
			        	{	
			        		JsonObject i = array.get(j).getAsJsonObject();
			        			Bukkit.getLogger().info(i.toString());
			        		if(i.has("hoverEvent"))
			        		{	
			        			JsonObject hoverEvent = i.getAsJsonObject("hoverEvent");
			        			if(hoverEvent.get("action").getAsString().equals("show_item"))
			        			{	
			        				ItemStack is=NBTItem.convertNBTtoItem(new NBTContainer(hoverEvent.get("value").getAsString()));
			        				Bukkit.getPluginManager().callEvent(new ShowItemEvent(is));
			        				hoverEvent.remove("value");
			        				hoverEvent.addProperty("value",NBTItem.convertItemtoNBT(is).toString());
			        				i.remove("hoverEvent");
			        				i.add("hoverEvent",hoverEvent);
			        				array.set(j,i);
			        			}
			        		}
			        	}
			        	event.getPacket().setMeta("json",json.toString());
		        	}
		        }*/
    		}
    		public void onPacketReceiving(PacketEvent event)
    		{	
    			if(event.getPacketType()==PacketType.Play.Client.SET_CREATIVE_SLOT)
    			{	
	    			ItemStack copy=event.getPacket().getItemModifier().read(0);
	    			if(copy.hasItemMeta())
	    			{	
	    				ItemMeta im = copy.getItemMeta();
	    				if(im.hasDisplayName()&&im.getDisplayName().startsWith("§生§草"))
	    				{	
	    					im.setDisplayName(null);
	    					copy.setItemMeta(im);
							event.getPacket().getItemModifier().write(0,copy);
	    				}
	    			}
    			}
    		}
    	});
	}
	
    public ShowItemEvent(ItemStack itemStack)
    {	
		this.itemStack=itemStack;
		if(DropsName.growGrassTranslate&&((!itemStack.hasItemMeta())||((!itemStack.getItemMeta().hasLocalizedName())&&(!itemStack.getItemMeta().hasDisplayName()))))
		{	
			String grassTranslation = DropsName.getGrowGrassName(itemStack);
			if(grassTranslation!=null)
			{	
				ItemMeta im = itemStack.getItemMeta();
				im.setDisplayName("§生§草§r"+grassTranslation);
				itemStack.setItemMeta(im);
			}
		}
	}

	private static final HandlerList handlers = new HandlerList();
	private boolean cancelled=false;
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
}
