package mz.tech;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.google.common.collect.Lists;

/**
 * 物品功能展示合成表及其API
 */
public class ItemFunctionShow extends ShowCraftGuide implements Listener
{	
	public static Map<String,List<ItemStack>> recipes=new HashMap<>();
	public static ItemStack machine=new ItemStack(Material.NAME_TAG);
	static
	{	
		ItemMeta im = machine.getItemMeta();
		im.setLocalizedName("物品信息");
		im.setLore(Lists.newArrayList("§7功能介绍"));
		machine.setItemMeta(im);
	}
	
	public ItemFunctionShow()
	{	
		
	}
	
	public static void add(String name,ItemStack ...recipe)
	{	
		recipes.put(name,Lists.newArrayList(recipe));
	}
	public static List<List<ItemStack>> getRaws(String name)
	{	
		List<List<ItemStack>> rl=new ArrayList<>();
		List<ItemStack> recipe = recipes.get(name);
		for(int i=1;i<recipe.size();i++)
		{	
			rl.add(Lists.newArrayList(recipe.get(i)));
		}
		return rl;
	}
	public static ItemStack getResult(String name)
	{	
		return recipes.get(name).get(0);
	}
	public static ItemStack getMachine()
	{	
		return machine;
	}
}
