package mz.tech;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.FurnaceRecipe;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import com.google.common.collect.Lists;

/**
 * 熔炉配方合成表模块及相关API
 */
public class FurnaceRecipeGuide extends ShowCraftGuide
{	
	public static Map<String,FurnaceRecipe> recipes=new HashMap<>();
	public static ItemStack machine=new ItemStack(Material.FURNACE);
	static
	{	
		ItemMeta im = machine.getItemMeta();
		im.setLocalizedName("熔炉");
		im.setLore(Lists.newArrayList("§7在熔炉中烧炼"));
		machine.setItemMeta(im);
	}
	
	public static void add(String name,FurnaceRecipe sr)
	{	
		Bukkit.addRecipe(sr);
		recipes.put(name,sr);
	}
	
	public static List<List<ItemStack>> getRaws(String name)
	{	
		List<List<ItemStack>> rl=new ArrayList<>();
		rl.add(Lists.newArrayList(recipes.get(name).getInput()));
		return rl;
	}
	public static ItemStack getResult(String name)
	{	
		return recipes.get(name).getResult();
	}
	public static ItemStack getMachine()
	{	
		return machine;
	}
}
