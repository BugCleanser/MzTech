package mz.tech;

import java.util.List;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.BrewEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SpawnEggMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.google.common.collect.Lists;

/**
 * 食物模块及相关API
 */
public class Food implements Listener
{	
	public Food()
	{	
		ItemStack food=new ItemStack(Material.APPLE);
		ItemMeta im = food.getItemMeta();
		im.setLocalizedName("§c食物");
		food.setItemMeta(im);
		CraftGuide.addClassify("食物",food);
		
		ItemStack sheepMilk=new ItemStack(Material.MILK_BUCKET);
		im=sheepMilk.getItemMeta();
		im.setLocalizedName("羊奶");
		sheepMilk.setItemMeta(im);
		MzTech.items.put("羊奶",sheepMilk);
		ItemStack sheep=null;
		if(!(Boolean)MzTech.getConfigs().get("HighVersion"))
		{	
			sheep=new ItemStack(Material.MONSTER_EGG);
			im=sheep.getItemMeta();
			((SpawnEggMeta)im).setSpawnedType(EntityType.SHEEP);
			im.setLocalizedName("羊");
			sheep.setItemMeta(im);
		}
		else
		{	
			sheep=new ItemStack(Enum.valueOf(Material.class,"SHEEP_SPAWN_EGG"));
			im=sheep.getItemMeta();
			im.setLocalizedName("羊");
			sheep.setItemMeta(im);
		}
		UseItemForEntityRecipe.add("羊奶",new ItemStack(Material.BUCKET),true,sheep,EntityType.SHEEP,sheepMilk);
		CraftGuide.addCraftTable("食物",UseItemForEntityRecipe.class,"羊奶");
		
		Raw goldApple=new Raw(null,new ItemStack(Material.GOLDEN_APPLE));
		Raw expBottle;
		try
		{	
			expBottle=new Raw(null,new ItemStack(Material.EXP_BOTTLE));
		}
		catch(Throwable e)
		{	
			expBottle=new Raw(null,new ItemStack(Enum.valueOf(Material.class,"EXPERIENCE_BOTTLE")));
		}
		ItemStack enchantGoldApple=null;
		try
		{	
			enchantGoldApple=new ItemStack(Enum.valueOf(Material.class,"ENCHANTED_GOLDEN_APPLE"));
		}
		catch (Exception e)
		{	
			enchantGoldApple=new ItemStack(Material.GOLDEN_APPLE,1,(short)1);
		}
		SmilingCraftingTable.add("附魔金苹果",enchantGoldApple,null,expBottle,expBottle,expBottle,expBottle,goldApple,expBottle,expBottle,expBottle,expBottle);
		CraftGuide.addCraftTable("食物",SmilingCraftingTable.class,"附魔金苹果");
		
		ItemStack chips=new ItemStack(Material.RABBIT_FOOT);
		im=chips.getItemMeta();
		im.setLocalizedName("§e薯片");
		chips.setItemMeta(im);
		MzTech.items.put("薯片",chips);
		Raw bakedPotato=new Raw(null,new ItemStack(Material.BAKED_POTATO));
		chips=new ItemStack(chips);
		chips.setAmount(16);
		SmilingCraftingTable.add("薯片",chips,null,bakedPotato,bakedPotato,bakedPotato);
		CraftGuide.addCraftTable("食物",SmilingCraftingTable.class,"薯片");
		
		Raw air=new Raw(null,new ItemStack(Material.AIR));
		try
		{	
			ItemStack MzStew=new ItemStack(Material.MUSHROOM_SOUP);
			im=MzStew.getItemMeta();
			im.setLocalizedName("§eMz炖菜");
			im.setLore(Lists.newArrayList("§7迷之炖菜"));
			MzStew.setItemMeta(im);
			MzTech.items.put("Mz炖菜",MzStew);
			Raw flower=new Raw(null,new ItemStack(Material.RED_ROSE,1,(short)0),new ItemStack(Material.RED_ROSE,1,(short)1),new ItemStack(Material.RED_ROSE,1,(short)2),new ItemStack(Material.RED_ROSE,1,(short)3),new ItemStack(Material.RED_ROSE,1,(short)4),new ItemStack(Material.RED_ROSE,1,(short)5),new ItemStack(Material.RED_ROSE,1,(short)6),new ItemStack(Material.RED_ROSE,1,(short)7),new ItemStack(Material.RED_ROSE,1,(short)8),new ItemStack(Material.YELLOW_FLOWER));
			Raw mushroomSoup=new Raw(null,new ItemStack(Material.MUSHROOM_SOUP));
			SmilingCraftingTable.add("Mz炖菜",MzStew,(iss,is)->
			{	
				ItemMeta meta=is.getItemMeta();
				List<String> lore=meta.getLore();
				switch(iss[0].getType())
				{	
				case RED_ROSE:
					lore.set(0,lore.get(0)+"§"+iss[0].getDurability());
					break;
				case YELLOW_FLOWER:
					lore.set(0,lore.get(0)+"§9");
				default:
					break;
				}
				meta.setLore(lore);
				is.setItemMeta(meta);
			},flower,air,air,mushroomSoup);
			CraftGuide.addCraftTable("食物",SmilingCraftingTable.class,"Mz炖菜");
		}
		catch(Throwable e)
		{	
			//生吞
		}
		
		ItemStack flour=new ItemStack(Material.SUGAR);
		im=flour.getItemMeta();
		im.setLocalizedName("§f面粉");
		flour.setItemMeta(im);
		MzTech.items.put("面粉",flour);
		Raw wheat = new Raw(null,new ItemStack(Material.WHEAT));
		SmilingCraftingTable.add("面粉",flour,null,wheat);
		CraftGuide.addCraftTable("食物",SmilingCraftingTable.class,"面粉");
		
		ItemStack moonCake=new ItemStack(Material.PUMPKIN_PIE);
		im=moonCake.getItemMeta();
		im.setLocalizedName("§6月饼");
		moonCake.setItemMeta(im);
		MzTech.items.put("月饼",moonCake);
		Raw egg = new Raw(null,new ItemStack(Material.EGG));
		Raw flourRaw=new Raw(null,flour);
		SmilingCraftingTable.add("月饼",moonCake,null,flourRaw,air,air,egg,air,air,flourRaw);
		CraftGuide.addCraftTable("食物",SmilingCraftingTable.class,"月饼");
		
		ItemStack chocolate;
		try
		{	
			chocolate=new ItemStack(Material.NETHER_BRICK_ITEM);
		}
		catch(Throwable e)
		{	
			chocolate=new ItemStack(Enum.valueOf(Material.class,"NETHER_BRICK"));
		}
		im=chocolate.getItemMeta();
		im.setLocalizedName("§4巧克力");
		chocolate.setItemMeta(im);
		MzTech.items.put("巧克力",chocolate);
		Raw cocoa;
		try
		{	
			cocoa=new Raw(null,new ItemStack(Material.INK_SACK,1,(short)3));
		}
		catch(Throwable e)
		{	
			cocoa=new Raw(null,new ItemStack(Enum.valueOf(Material.class,"COCOA_BEANS")));
		}
		Raw sugar=new Raw(null,new ItemStack(Material.SUGAR));
		SmilingCraftingTable.add("巧克力",chocolate,null,cocoa,sugar);
		CraftGuide.addCraftTable("食物",SmilingCraftingTable.class,"巧克力");
		
		ItemStack goldenChocolate=new ItemStack(Material.GOLD_INGOT);
		im=goldenChocolate.getItemMeta();
		im.setLocalizedName("§e金箔巧克力");
		goldenChocolate.setItemMeta(im);
		MzTech.items.put("金箔巧克力",goldenChocolate);
		Raw chocolateRaw=new Raw(null,chocolate);
		Raw goldDust=new Raw(null,MzTech.items.get("金粉"));
		SmilingCraftingTable.add("金箔巧克力",goldenChocolate,null,
				goldDust,goldDust,goldDust,
				goldDust,chocolateRaw,goldDust,
				goldDust,goldDust,goldDust);
		CraftGuide.addCraftTable("食物",SmilingCraftingTable.class,"金箔巧克力");
    }
	
	@EventHandler
	void onEatItem(EatItemEvent event)
	{	
		switch (event.item)
		{	
		case "Mz炖菜":
			PotionEffect effect=null;
			switch(event.itemStack.getItemMeta().getLore().get(0).charAt(7))
			{	
			case '0':
				effect=new PotionEffect(PotionEffectType.NIGHT_VISION,40*20,0);
				break;
			case '1':
			case '9':
				effect=new PotionEffect(PotionEffectType.SATURATION,30*20,0);
				break;
			case '2':
				effect=new PotionEffect(PotionEffectType.FIRE_RESISTANCE,20*20,0);
				break;
			case '3':
				effect=new PotionEffect(PotionEffectType.BLINDNESS,60*20,0);
				break;
			case '4':
			case '5':
			case '6':
			case '7':
				effect=new PotionEffect(PotionEffectType.WEAKNESS,70*20,0);
				break;
			case '8':
				effect=new PotionEffect(PotionEffectType.REGENERATION,60*20,0);
				break;
			}
			event.player.addPotionEffect(effect);
			break;
		}
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	void onBrew(BrewEvent event)
	{	
		if(MzTech.isItem(event.getContents().getIngredient(),"薯片"))
		{	
			event.setCancelled(true);
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	void onClickItem(ClickItemEvent event)
	{	
		if(event.isCancelled())
		{	
			return;
		}
		switch(event.item)
		{	
		case "巧克力":
			if(event.leftClick==false)
			{	
				if(event.player.getGameMode()!=GameMode.CREATIVE&&event.player.getFoodLevel()<20)
				{	
					event.itemStack.setAmount(event.itemStack.getAmount()-1);
					event.player.setFoodLevel(event.player.getFoodLevel()+4);
					event.player.setSaturation(event.player.getSaturation()+8);
					event.player.getWorld().playSound(event.player.getLocation(),Sound.ENTITY_GENERIC_EAT,1,1);
				}
				event.setCancelled(true);
			}
			break;
		case "金箔巧克力":
			if(event.leftClick==false)
			{	
				if(event.player.getGameMode()!=GameMode.CREATIVE)
				{	
					event.itemStack.setAmount(event.itemStack.getAmount()-1);
					event.player.setFoodLevel(event.player.getFoodLevel()+20);
					event.player.setSaturation(event.player.getSaturation()+40);
					event.player.removePotionEffect(PotionEffectType.DAMAGE_RESISTANCE);
					event.player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE,4800,0));
					event.player.removePotionEffect(PotionEffectType.ABSORPTION);
					event.player.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION,2400,2));
					event.player.removePotionEffect(PotionEffectType.REGENERATION);
					event.player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION,600,1));
					event.player.removePotionEffect(PotionEffectType.FIRE_RESISTANCE);
					event.player.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE,6000,0));
					event.player.getWorld().playSound(event.player.getLocation(),Sound.ENTITY_GENERIC_EAT,1,1);
				}
				event.setCancelled(true);
			}
			break;
		case "薯片":
			if(event.leftClick==false)
			{	
				if(event.player.getGameMode()!=GameMode.CREATIVE&&event.player.getFoodLevel()<20)
				{	
					event.itemStack.setAmount(event.itemStack.getAmount()-1);
					event.player.setFoodLevel(event.player.getFoodLevel()+1);
					event.player.setSaturation(event.player.getSaturation()+2);
					event.player.getWorld().playSound(event.player.getLocation(),Sound.ENTITY_GENERIC_EAT,1,1);
				}
				event.setCancelled(true);
			}
			break;
		}
	}
}
