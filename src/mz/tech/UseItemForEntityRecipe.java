package mz.tech;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SpawnEggMeta;
import com.google.common.base.Objects;
import com.google.common.collect.Lists;

class Recipe
{	
	public ItemStack input;
	public boolean consume;
	public ItemStack entity;
	public EntityType entityType;
	public ItemStack output;
	
	public Recipe(ItemStack input, boolean consume, ItemStack entity,EntityType entityType, ItemStack output)
	{	
		this.input=input;
		this.consume=consume;
		this.entity=entity;
		this.entityType=entityType;
		this.output=output;
	}
}

/**
 * 使用物品右键实体的配方及其API
 */
public class UseItemForEntityRecipe extends ShowCraftGuide implements Listener
{	
	public static Map<String,Recipe> recipes=new HashMap<>();
	public static ItemStack machine;
	static
	{	
		machine=new ItemStack(Material.ARROW);
		ItemMeta im = machine.getItemMeta();
		im.setLocalizedName("§d对实体使用物品获得");
		machine.setItemMeta(im);
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler
    void onPlayerInteractEntity(PlayerInteractEntityEvent event)
	{	
		if(!event.isCancelled())
		{	
			ItemStack hand=event.getPlayer().getItemInHand();
			recipes.forEach((s,r)->
			{	
				if(hand.getAmount()>=r.input.getAmount()&&hand.getType().equals(r.input.getType())&&Objects.equal(hand.getItemMeta().getLocalizedName(),r.input.getItemMeta().getLocalizedName())
						&&event.getRightClicked().getType().equals(((SpawnEggMeta)r.entity.getItemMeta()).getSpawnedType()))
				{	
					if(r.consume&&event.getPlayer().getGameMode()!=GameMode.CREATIVE)
					{	
						hand.setAmount(hand.getAmount()-r.input.getAmount());
					}
					MzTech.giveInHand(event.getPlayer(),r.output);
				}
			});
		}
	}
	
	public static void add(String name,ItemStack input,boolean consume,ItemStack Entity,EntityType entityType, ItemStack output)
	{	
		recipes.put(name,new Recipe(input,consume,Entity,entityType,output));
	}
	
	public static List<List<ItemStack>> getRaws(String name)
	{	
		Recipe recipe=recipes.get(name);
		return Lists.newArrayList(Lists.newArrayList(recipe.entity),Lists.newArrayList(recipe.input));
	}
	public static ItemStack getResult(String name)
	{	
		return recipes.get(name).output;
	}
	public static ItemStack getMachine()
	{	
		return machine;
	}
}
