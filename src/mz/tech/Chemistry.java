package mz.tech;

import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.inventory.FurnaceRecipe;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.material.MaterialData;

import com.google.common.collect.Lists;

/*
 * 化学模块及其API
 */
public class Chemistry implements Listener
{	
	@SuppressWarnings("deprecation")
	public Chemistry()
	{	
		ItemStack chemistry=new ItemStack(Material.POTION);
		ItemMeta im = chemistry.getItemMeta();
		((PotionMeta)im).setColor(Color.ORANGE);
		im.setLocalizedName("§b化学");
		im.addItemFlags(ItemFlag.values());
		chemistry.setItemMeta(im);
		CraftGuide.addClassify("化学",chemistry);
		
		ItemStack Fe2O3;
		try
		{	
			Fe2O3=new ItemStack(Material.NETHER_STALK);
		}
		catch(Throwable e)
		{	
			Fe2O3=new ItemStack(Enum.valueOf(Material.class,"NETHER_WART"));
		}
		im=Fe2O3.getItemMeta();
		im.setLocalizedName("§c氧化铁");
		im.setLore(Lists.newArrayList("§7Fe₂O₃"));
		Fe2O3.setItemMeta(im);
		MzTech.items.put("氧化铁",Fe2O3);
		FurnaceRecipeGuide.add("氧化铁",new FurnaceRecipe(Fe2O3,Material.IRON_INGOT));
		CraftGuide.addCraftTable("化学",FurnaceRecipeGuide.class,"氧化铁");
		
		ItemStack CaO=new ItemStack(Material.SUGAR);
		im=CaO.getItemMeta();
		im.setLocalizedName("§7氧化钙");
		im.setLore(Lists.newArrayList("§7CaO"));
		CaO.setItemMeta(im);
		MzTech.items.put("氧化钙",CaO);
		FurnaceRecipeGuide.add("氧化钙",new FurnaceRecipe(CaO,new MaterialData(Material.STONE,(byte) 3)));
		CraftGuide.addCraftTable("化学",FurnaceRecipeGuide.class,"氧化钙");
		
		ItemStack CaOH2=new ItemStack(Material.SUGAR);
		im=CaOH2.getItemMeta();
		im.setLocalizedName("§7氢氧化钙");
		im.setLore(Lists.newArrayList("§7Ca(OH)₂"));
		CaOH2.setItemMeta(im);
		MzTech.items.put("氢氧化钙",CaOH2);
		Raw waterBucket=new Raw(new ItemStack(Material.BUCKET),new ItemStack(Material.WATER_BUCKET));
		Raw limeRaw=new Raw(null,CaO);
		SmilingCraftingTable.add("氢氧化钙 CaO+H₂O→Ca(OH)₂",CaOH2,null,limeRaw,waterBucket);
		CraftGuide.addCraftTable("化学",SmilingCraftingTable.class,"氢氧化钙 CaO+H₂O→Ca(OH)₂");
		
		ItemStack Na2CO3=new ItemStack(Material.SUGAR);
		im=Na2CO3.getItemMeta();
		im.setLocalizedName("§f碳酸钠");
		im.setLore(Lists.newArrayList("§7Na₂CO₃"));
		Na2CO3.setItemMeta(im);
		MzTech.items.put("碳酸钠",Na2CO3);
		try
		{	
			FurnaceRecipeGuide.add("碳酸钠",new FurnaceRecipe(Na2CO3,Material.WATER_LILY));
		}
		catch(Throwable e)
		{	
			FurnaceRecipeGuide.add("碳酸钠",new FurnaceRecipe(Na2CO3,Enum.valueOf(Material.class,"KELP")));
		}
		CraftGuide.addCraftTable("化学",FurnaceRecipeGuide.class,"碳酸钠");
		
		ItemStack NaCl=new ItemStack(Material.SUGAR);
		im=NaCl.getItemMeta();
		im.setLocalizedName("§f氯化钠");
		im.setLore(Lists.newArrayList("§7NaCl"));
		NaCl.setItemMeta(im);
		MzTech.items.put("氯化钠",NaCl);
		ItemStack saline=new ItemStack(Material.WATER_BUCKET);
		im=saline.getItemMeta();
		im.setLocalizedName("§7盐水");
		saline.setItemMeta(im);
		MzTech.items.put("盐水",saline);
		Raw sand=new Raw(null,new ItemStack(Material.SAND),NaCl);
		Raw fullWaterBucket=new Raw(null,new ItemStack(Material.WATER_BUCKET));
		SmilingCraftingTable.add("盐水",saline,null,fullWaterBucket,sand);
		CraftGuide.addCraftTable("化学",SmilingCraftingTable.class,"盐水");
		SmilingCraftingTable.add("氯化钠",NaCl,null,new Raw(new ItemStack(Material.BUCKET),saline));
		CraftGuide.addCraftTable("化学",SmilingCraftingTable.class,"氯化钠");
		
		ItemStack S=new ItemStack(Material.GLOWSTONE_DUST);
		im=S.getItemMeta();
		im.setLocalizedName("§e硫磺");
		im.setLore(Lists.newArrayList("§7S"));
		S.setItemMeta(im);
		MzTech.items.put("硫磺",S);
		ItemStack gunpowder3;
		try
		{	
			gunpowder3=new ItemStack(Material.SULPHUR,3);
		}
		catch(Throwable e)
		{	
			gunpowder3=new ItemStack(Enum.valueOf(Material.class,"GUNPOWDER"),3);
		}
		ScreenRecipes.add("硫磺",gunpowder3,S,100);
		CraftGuide.addCraftTable("化学",ScreenRecipes.class,"硫磺");
		
		ItemStack KNO3=new ItemStack(Material.SUGAR);
		im=KNO3.getItemMeta();
		im.setLocalizedName("硝石");
		im.setLore(Lists.newArrayList("§7KNO₃"));
		KNO3.setItemMeta(im);
		MzTech.items.put("硝石",KNO3);
		ScreenRecipes.add("硝石",gunpowder3,KNO3,100);
		CraftGuide.addCraftTable("化学",ScreenRecipes.class,"硝石");
		
		Raw water=new Raw(new ItemStack(Material.BUCKET),new ItemStack(Material.WATER_BUCKET));
		SmilingCraftingTable.add("冰",new ItemStack(Material.ICE),null,water,new Raw(null,KNO3));
		CraftGuide.addCraftTable("化学",SmilingCraftingTable.class,"冰");

		ItemStack Fe=new ItemStack(gunpowder3);
		Fe.setAmount(1);
		im=Fe.getItemMeta();
		im.setLocalizedName("铁粉");
		im.setLore(Lists.newArrayList("§7Fe"));
		Fe.setItemMeta(im);
		MzTech.items.put("铁粉",Fe);
		ScreenRecipes.add("铁粉",new ItemStack(Material.GRAVEL),Fe,30);
		CraftGuide.addCraftTable("化学",ScreenRecipes.class,"铁粉");
		
		ItemStack Au=new ItemStack(Material.GLOWSTONE_DUST);
		im=Au.getItemMeta();
		im.setLocalizedName("§e金粉");
		im.setLore(Lists.newArrayList("§7Au"));
		Au.setItemMeta(im);
		MzTech.items.put("金粉",Au);
		ScreenRecipes.add("金粉",new ItemStack(Material.GRAVEL),Au,8);
		CraftGuide.addCraftTable("化学",ScreenRecipes.class,"金粉");
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler
	void onPlayerBucketEmpty(PlayerBucketEmptyEvent event)
	{	
		if(MzTech.isItem(event.getPlayer().getItemInHand().getType()==Material.WATER_BUCKET||event.getPlayer().getItemInHand().getType()==Material.LAVA_BUCKET?event.getPlayer().getItemInHand():event.getPlayer().getInventory().getItemInOffHand(),"盐水"))
		{	
			event.setCancelled(true);
		}
	}
}
