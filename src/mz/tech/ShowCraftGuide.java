package mz.tech;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.inventory.ItemStack;

/**
 * 科技合成表的基类
 */
public abstract class ShowCraftGuide
{	
	public static List<List<ItemStack>> getRaws(String name)
	{	
		return new ArrayList<>();
	}
}
