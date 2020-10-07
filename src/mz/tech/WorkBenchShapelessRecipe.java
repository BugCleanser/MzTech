package mz.tech;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.inventory.meta.ItemMeta;

import com.google.common.collect.Lists;

/**
 * （不建议使用）
 * 原版合成台的无序配方
 */
@Deprecated
public class WorkBenchShapelessRecipe extends ShowCraftGuide implements Listener
{	
	public static Map<String,ShapelessRecipe> recipes=new HashMap<>();
	public static ItemStack machine;
	static
	{	
		try
		{	
			machine=new ItemStack(Material.WORKBENCH);
		}
		catch(Error e)
		{	
			machine=new ItemStack(Enum.valueOf(Material.class,"CRAFTING_TABLE"));
		}
		ItemMeta im = machine.getItemMeta();
		im.setLocalizedName("工作台");
		im.setLore(Lists.newArrayList("§7在工作台中合成","§4无序合成"));
		machine.setItemMeta(im);
	}
	
	public static void add(String name,ShapelessRecipe sr)
	{	
		Bukkit.addRecipe(sr);
		recipes.put(name,sr);
	}

	public static void onDisable()
	{	
		Bukkit.resetRecipes();
	}
	
	public static List<List<ItemStack>> getRaws(String name)
	{	
		List<List<ItemStack>> rl=new ArrayList<>();
		recipes.get(name).getIngredientList().forEach((i)->
		{	
			rl.add(Lists.newArrayList(i));
		});
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
