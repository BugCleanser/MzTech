package mz.tech;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.google.common.collect.Lists;

/**
 * 使用筛子的配方及其API
 */
public class ScreenRecipes extends ShowCraftGuide implements Listener
{	
	public static ItemStack machine;
	public static Map<String,ItemStack> raws=new HashMap<>();
	public static Map<String,ItemStack> drops=new HashMap<>();
	public static Map<String,Integer> probabilities=new HashMap<>();
	
	static
	{	
		try
		{	
			machine=new ItemStack(Material.WEB);
		}
		catch(Throwable e)
		{	
			machine=new ItemStack(Enum.valueOf(Material.class,"COBWEB"));
		}
		ItemMeta im = machine.getItemMeta();
		im.setLocalizedName("§f筛子");
		im.setLore(Lists.newArrayList("§7使用筛子筛就完了"));
		machine.setItemMeta(im);
	}
	
	public ScreenRecipes()
	{	
		
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler(priority = EventPriority.HIGHEST)
	void onClickMachine(ClickMachineEvent event)
	{	
		if(event.isCancelled()||!MzTech.canOpen(event.player))
		{	
			return;
		}
		switch(event.machine)
		{	
		case "筛子":
			if(!event.leftClick)
			{	
				event.setCancelled(true);
				ItemStack is=event.player.getItemInHand();
				if(is!=null&&is.getType()!=Material.AIR)
				{	
					raws.forEach((n,i)->
					{	
						if(is.isSimilar(i)&&is.getAmount()>=i.getAmount())
						{	
							if(event.player.getGameMode()!=GameMode.CREATIVE)
							{	
								is.setAmount(is.getAmount()-i.getAmount());
							}
							if(new Random().nextInt(100)<probabilities.get(n))
							{	
								event.block.getWorld().dropItemNaturally(event.block.getLocation().add(0.5,0.5,0.5),drops.get(n));
							}
							event.block.getWorld().playSound(event.block.getLocation().add(0.5,0.5,0.5),Sound.ENTITY_BAT_TAKEOFF,1,1);
						}
					});
					event.player.setItemInHand(is);
				}
			}
			break;
		}
	}
	
	public static List<List<ItemStack>> getRaws(String name)
	{	
		List<List<ItemStack>> rl=new ArrayList<>();
		ItemStack is = new ItemStack(raws.get(name));
		ItemMeta im = is.getItemMeta();
		List<String> lore = im.hasLore()?im.getLore():new ArrayList<>();
		lore.add("§7几率： "+probabilities.get(name));
		im.setLore(lore);
		is.setItemMeta(im);
		rl.add(Lists.newArrayList(is));
		return rl;
	}
	public static void add(String name,ItemStack raw,ItemStack drop,int probability)
	{	
		raws.put(name,raw);
		drops.put(name,drop);
		probabilities.put(name,probability);
	}
	public static ItemStack getResult(String name)
	{	
		return drops.get(name);
	}
	public static ItemStack getMachine()
	{	
		return machine;
	}
}
