package mz.tech;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.GameMode;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.MaterialData;

import com.google.common.collect.Lists;

/**
 * 锤子锤方块配方及其API
 */
public class HammerRecipes extends ShowCraftGuide implements Listener
{	
	public static List<String> hammers=new ArrayList<>();
	public static Map<String,MaterialData> raws=new HashMap<>();
	public static Map<String,ItemStack> drops=new HashMap<>();
	
	public static ItemStack machine=new ItemStack(MzTech.items.get("钻石锤"));
	static
	{	
		ItemMeta im = machine.getItemMeta();
		im.setDisplayName("§b锤子");
		im.setLore(Lists.newArrayList("§7使用锤子敲碎得到"));
		machine.setItemMeta(im);
	}
	
	public HammerRecipes()
	{	
		
	}
	
	public static List<List<ItemStack>> getRaws(String name)
	{	
		List<List<ItemStack>> rl=new ArrayList<>();
		rl.add(Lists.newArrayList(raws.get(name).toItemStack(1)));
		return rl;
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler(priority = EventPriority.HIGH)
	void onBlockBreakEvent(BlockBreakEvent event)
	{	
		if(event.isCancelled()||MzTech.isMachine(event.getBlock().getLocation())!=null||!event.isDropItems()||(event.getPlayer()!=null&&event.getPlayer().getGameMode()==GameMode.CREATIVE))
		{	
			return;
		}
		boolean[] useHammer= {false};
		hammers.forEach(hammer->
		{	
			if(event.getPlayer().getItemInHand()!=null&&MzTech.isItem(event.getPlayer().getItemInHand(),hammer))
			{	
				useHammer[0]=true;
			}
		});
		if(useHammer[0])
		{	
			raws.forEach((n,r)->
			{	
				if(event.getBlock().getState().getData().equals(r))
				{	
					event.setDropItems(false);
					event.getBlock().getWorld().dropItemNaturally(event.getBlock().getLocation().add(0.5,0,0.5),drops.get(n));
				}
			});
		}
	}
	
	/*
	 * 添加一个锤子配方
	 * @param name 配方名称
	 * @param block 方块的MaterialData
	 * @param drop 挖掉方块的掉落物
	 */
	public static void add(String name,MaterialData block,ItemStack drop)
	{	
		raws.put(name,block);
		drops.put(name,drop);
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
