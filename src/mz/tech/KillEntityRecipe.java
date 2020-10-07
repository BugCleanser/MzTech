package mz.tech;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import com.google.common.collect.Lists;

class KillEntity
{	
	ItemStack[] entitis;
	EntityType[] entitisType;
	ItemStack result;
	
	public KillEntity(ItemStack[] entitis,EntityType[] entitisType,ItemStack result)
	{	
		this.entitis=entitis;
		this.entitisType=entitisType;
		this.result=result;
	}
}

/**
 * 击杀生物掉落物品配方及其API
 */
public class KillEntityRecipe extends ShowCraftGuide implements Listener
{	
	public static Map<String,KillEntity> recipes=new HashMap<>();
	public static ItemStack machine;
	static
	{	
		try
		{	
			machine=new ItemStack(Material.GOLD_SWORD);
		}
		catch(Throwable e)
		{	
			machine=new ItemStack(Enum.valueOf(Material.class,"GOLDEN_SWORD"));
		}
		ItemMeta im = machine.getItemMeta();
		im.setLocalizedName("击杀生物掉落");
		machine.setItemMeta(im);
	}
	
	public KillEntityRecipe()
	{	
	}
	
	@EventHandler
	public void onEntityDeath(EntityDeathEvent event)
	{	
		recipes.values().forEach((k)->
		{	
			int[] index= {0};
			Lists.newArrayList(k.entitis).forEach((i)->
			{	
				if(k.entitisType[index[0]].equals(event.getEntityType()))
				{	
					String[] ss=i.getItemMeta().getLore().get(0).split(" ");
					ss=ss[1].split("%");
					if(new Random().nextInt()%100<Integer.valueOf(ss[0]))
					{	
						event.getEntity().getWorld().dropItemNaturally(event.getEntity().getLocation(),new ItemStack(k.result));
					}
				}
				index[0]++;
			});
		});
	}
	
	public static List<List<ItemStack>> getRaws(String name)
	{	
		List<List<ItemStack>> rl=new ArrayList<>();
		
		Lists.newArrayList(recipes.get(name).entitis).forEach((e)->
		{	
			rl.add(Lists.newArrayList(e));
		});
		
		return rl;
	}
	public static void add(String name,ItemStack[] entitis,EntityType[] entitisType,ItemStack result)
	{	
		recipes.put(name,new KillEntity(entitis,entitisType,result));
	}
	public static ItemStack getResult(String name)
	{	
		return recipes.get(name).result;
	}
	public static ItemStack getMachine()
	{	
		return machine;
	}
}
