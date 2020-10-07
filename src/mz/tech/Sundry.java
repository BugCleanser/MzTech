package mz.tech;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.entity.Wither;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.world.ChunkPopulateEvent;
import org.bukkit.event.world.StructureGrowEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.inventory.meta.SpawnEggMeta;
import org.bukkit.material.MaterialData;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;
import org.bukkit.scheduler.BukkitRunnable;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * 杂项模块及其API
 */
public class Sundry implements Listener
{	
	static Random rand=new Random();
	static Map<String,Inventory> bags=new HashMap<>();
	
	/**
	 * 杂项分类的监听器
	 */
	@SuppressWarnings("deprecation")
	public Sundry()
	{	
		ItemStack sundry=new ItemStack(Material.LEATHER);
		ItemMeta im = sundry.getItemMeta();
		im.setLocalizedName("§6杂项");
		sundry.setItemMeta(im);
		CraftGuide.addClassify("杂项",sundry);
		
		Raw leather=new Raw(null,new ItemStack(Material.LEATHER));
		Raw ironIngot=new Raw(null,new ItemStack(Material.IRON_INGOT));
		Raw air=new Raw(null,new ItemStack(Material.AIR));
		SmilingCraftingTable.add("鞍",new ItemStack(Material.SADDLE),null,leather,leather,leather,leather,ironIngot,leather,ironIngot,air,ironIngot);
		CraftGuide.addCraftTable("杂项",SmilingCraftingTable.class,"鞍");

		Raw goldApple=new Raw(null,new ItemStack(Material.GOLDEN_APPLE));
		Raw enchatmentGoldApple=new Raw(null,new ItemStack(Material.GOLDEN_APPLE,1,(short)1));
		try
		{	
			SmilingCraftingTable.add("不死图腾",new ItemStack(Material.TOTEM),null,air,enchatmentGoldApple,air,goldApple,goldApple,goldApple,air,goldApple,air);
		}
		catch(Throwable e)
		{	
			SmilingCraftingTable.add("不死图腾",new ItemStack(Enum.valueOf(Material.class,"TOTEM_OF_UNDYING")),null,air,enchatmentGoldApple,air,goldApple,goldApple,goldApple,air,goldApple,air);
		}
		CraftGuide.addCraftTable("杂项",SmilingCraftingTable.class,"不死图腾");
		
		ShapelessRecipe flint=new ShapelessRecipe(new NamespacedKey(MzTech.instance,"flint"),new ItemStack(Material.FLINT));
		flint.addIngredient(Material.GRAVEL);
		flint.addIngredient(Material.GRAVEL);
		flint.addIngredient(Material.GRAVEL);
		WorkBenchShapelessRecipe.add("燧石",flint);
		CraftGuide.addCraftTable("杂项",WorkBenchShapelessRecipe.class,"燧石");
		
		Raw ironBars;
		try
		{	
			ironBars=new Raw(null,new ItemStack(Material.IRON_FENCE));
		}
		catch(Throwable e)
		{	
			ironBars=new Raw(null,new ItemStack(Enum.valueOf(Material.class,"IRON_BARS")));
		}
		Raw totem;
		try
		{	
			totem=new Raw(null,new ItemStack(Material.TOTEM));
		}
		catch(Throwable e)
		{	
			totem=new Raw(null,new ItemStack(Enum.valueOf(Material.class,"TOTEM_OF_UNDYING")));
		}
		try
		{	
			SmilingCraftingTable.add("刷怪箱",new ItemStack(Material.MOB_SPAWNER),null,ironBars,ironBars,ironBars,ironBars,totem,ironBars,ironBars,ironBars,ironBars);
		}
		catch(Throwable e)
		{	
			SmilingCraftingTable.add("刷怪箱",new ItemStack(Enum.valueOf(Material.class,"SPAWNER")),null,ironBars,ironBars,ironBars,ironBars,totem,ironBars,ironBars,ironBars,ironBars);
		}
		CraftGuide.addCraftTable("杂项",SmilingCraftingTable.class,"刷怪箱");
		
		ItemStack appleSapling;
		try
		{	
			appleSapling=new ItemStack(Material.SAPLING);
		}
		catch(Throwable e)
		{	
			appleSapling=new ItemStack(Enum.valueOf(Material.class,"OAK_SAPLING"));
		}
		im=appleSapling.getItemMeta();
		im.setLocalizedName("§c苹果树苗");
		appleSapling.setItemMeta(im);
		MzTech.items.put("苹果树苗",appleSapling);
		Raw apple=new Raw(null,new ItemStack(Material.APPLE));
		SmilingCraftingTable.add("苹果树苗",appleSapling,null,apple);
		CraftGuide.addCraftTable("杂项",SmilingCraftingTable.class,"苹果树苗");
		
		ItemStack appleLeaves;
		try
		{	
			appleLeaves=new ItemStack(Material.LEAVES);
		}
		catch(Throwable e)
		{	
			appleLeaves=new ItemStack(Enum.valueOf(Material.class,"OAK_LEAVES"));
		}
		im=appleLeaves.getItemMeta();
		im.setLocalizedName("§a苹果树叶");
		appleLeaves.setItemMeta(im);
		MzTech.items.put("苹果树叶",appleLeaves);
		
		/*ItemStack appleLog=new ItemStack(Material.LOG);
		im=appleLog.getItemMeta();
		im.setLocalizedName("§6苹果树原木");
		appleLog.setItemMeta(im);
		MzTech.items.put("苹果树原木",appleLog);*/
		
		ItemStack cnmb=new ItemStack(Material.EMERALD);
		im=cnmb.getItemMeta();
		im.setLocalizedName("§d草泥马币");
		im.setLore(Lists.newArrayList("§7草泥马的币","§7羊驼币","§4右键 §7兑换为§e￥"+MzTech.getConfigs().get("cnmb")));
		cnmb.setItemMeta(im);
		MzTech.items.put("草泥马币",cnmb);
		ItemStack cnm=null;
		if(!(Boolean)MzTech.getConfigs().get("HighVersion"))
		{	
			cnm=new ItemStack(Material.MONSTER_EGG);
			im=cnm.getItemMeta();
			im.setLocalizedName("草泥马");
			im.setLore(Lists.newArrayList("§7几率： 50%"));
			((SpawnEggMeta)im).setSpawnedType(EntityType.LLAMA);
			cnm.setItemMeta(im);
		}
		else
		{	
			cnm=new ItemStack(Enum.valueOf(Material.class,"LLAMA_SPAWN_EGG"));
			im=cnm.getItemMeta();
			im.setLocalizedName("草泥马");
			im.setLore(Lists.newArrayList("§7几率： 50%"));
			cnm.setItemMeta(im);
		}
		KillEntityRecipe.add("草泥马币",new ItemStack[] {cnm},new EntityType[] {EntityType.LLAMA},cnmb);
		CraftGuide.addCraftTable("杂项",KillEntityRecipe.class,"草泥马币");
		
		ItemStack airSpawnEgg=new ItemStack(Material.CLAY_BALL);
		im=airSpawnEgg.getItemMeta();
		im.setLocalizedName("§e空刷怪蛋");
		im.setLore(Lists.newArrayList("§4右键 §7捕捉生物"));
		airSpawnEgg.setItemMeta(im);
		MzTech.items.put("空刷怪蛋",airSpawnEgg);
		Raw egg=new Raw(null,new ItemStack(Material.EGG));
		SmilingCraftingTable.add("空刷怪蛋",airSpawnEgg,null,
				totem,totem,totem,
				totem,egg,totem,
				totem,totem,totem);
		CraftGuide.addCraftTable("杂项",SmilingCraftingTable.class,"空刷怪蛋");
		
		ItemStack bag=new ItemStack(Material.BOOK);
		im=bag.getItemMeta();
		im.setLocalizedName("§6背包");
		im.setLore(Lists.newArrayList("§4右键 §7打开","§7§m允许套娃"));
		bag.setItemMeta(im);
		MzTech.items.put("背包",bag);
		Raw string=new Raw(null,new ItemStack(Material.STRING));
		Raw shulkerShell=new Raw(null,new ItemStack(Material.SHULKER_SHELL));
		Raw ender_chest=new Raw(null,new ItemStack(Material.ENDER_CHEST));
		SmilingCraftingTable.add("背包",bag,null,
				leather,shulkerShell,leather,
				string,ender_chest,string,
				leather,shulkerShell,leather);
		CraftGuide.addCraftTable("杂项",SmilingCraftingTable.class,"背包");
		new BukkitRunnable()
		{	
			public void run()
			{	
				Maps.newHashMap(bags).forEach((id,inv)->
				{	
					if(inv.getViewers().size()==0)
					{	
						saveBag(id);
						bags.remove(id);
					}
				});
			}
		}.runTaskTimer(MzTech.instance,6000,6000);
		
		ItemStack bigBag=new ItemStack(Material.CHEST);
		im=bigBag.getItemMeta();
		im.setLocalizedName("§6大背包");
		im.setLore(Lists.newArrayList("§4右键 §7打开","§7§m允许套娃"));
		bigBag.setItemMeta(im);
		MzTech.items.put("大背包",bigBag);
		Raw bagRaw=new Raw(null,bag);
		SmilingCraftingTable.add("大背包",bigBag,null,
				shulkerShell,shulkerShell,shulkerShell,
				shulkerShell,bagRaw,shulkerShell,
				shulkerShell,shulkerShell,shulkerShell);
		CraftGuide.addCraftTable("杂项",SmilingCraftingTable.class,"大背包");
		MzTech.commands.add(
		new MzTechCommand("give",true,3)
		{	
			@Override
			public String usage()
			{	
				return "give <玩家> 背包 <背包id>";
			}
			
			@Override
			public boolean execute(CommandSender sender, String[] args)
			{	
				if(args[1].equals("背包"))
				{	
					ItemStack item = new ItemStack(MzTech.items.get("背包"));
					ItemMeta im=item.getItemMeta();
					List<String> lore = im.getLore();
					lore.add("§0ID: "+args[2]);
					im.setLore(lore);
					item.setItemMeta(im);
					((Player)sender).getInventory().addItem(item);
					sender.sendMessage(MzTech.MzTechPrefix+"§a给予了"+sender.getName()+" "+"1个"+item.getItemMeta().getLocalizedName());
					return true;
				}
				else
				{	
					return false;
				}
			}
		});
		MzTech.commands.add(
		new MzTechCommand("bag",true,1)
		{	
			@Override
			public String usage()
			{	
				return "bag <背包id>";
			}
			
			@Override
			public boolean execute(CommandSender sender, String[] args)
			{	
				if(sender instanceof Player)
				{	
					((Player)sender).openInventory(getBag(args[0],54));
				}
				else
				{	
					sender.sendMessage(MzTech.MzTechPrefix+"§4只有玩家才能使用该命令");
				}
				return true;
			}
		});
		MzTech.commands.add(
		new MzTechCommand("give",true,3)
		{	
			@Override
			public String usage()
			{	
				return "give <玩家> 大背包 <背包id>";
			}
			
			@Override
			public boolean execute(CommandSender sender, String[] args)
			{	
				if(args[1].equals("大背包"))
				{	
					ItemStack item = new ItemStack(MzTech.items.get("大背包"));
					ItemMeta im=item.getItemMeta();
					List<String> lore = im.getLore();
					lore.add("§0ID: "+args[2]);
					im.setLore(lore);
					item.setItemMeta(im);
					((Player)sender).getInventory().addItem(item);
					sender.sendMessage(MzTech.MzTechPrefix+"§a给予了"+sender.getName()+" "+"1个"+item.getItemMeta().getLocalizedName());
					return true;
				}
				else
				{	
					return false;
				}
			}
		});
		
		ItemStack VillagerNose;
		try
		{	
			VillagerNose=new ItemStack(Material.WOOD_BUTTON);
		}
		catch(Throwable e)
		{	
			VillagerNose=new ItemStack(Enum.valueOf(Material.class,"OAK_BUTTON"));
		}
		im=VillagerNose.getItemMeta();
		im.setLocalizedName("§6村民的鼻子");
		VillagerNose.setItemMeta(im);
		MzTech.items.put("村民的鼻子",VillagerNose);
		ItemStack villager=null;
		if((Boolean)MzTech.getConfigs().getOrDefault("HighVersion",false))
		{	
			villager=new ItemStack(Enum.valueOf(Material.class,"VILLAGER_SPAWN_EGG"));
		}
		else
		{	
			villager=new ItemStack(Material.MONSTER_EGG);
			im=villager.getItemMeta();
			((SpawnEggMeta)im).setSpawnedType(EntityType.VILLAGER);
			villager.setItemMeta(im);
		}
		im=villager.getItemMeta();
		im.setLocalizedName("村民");
		im.setLore(Lists.newArrayList("§7几率： 80%"));
		villager.setItemMeta(im);
		KillEntityRecipe.add("村民的鼻子",new ItemStack[] {villager},new EntityType[] {EntityType.VILLAGER},VillagerNose);
		CraftGuide.addCraftTable("杂项",KillEntityRecipe.class,"村民的鼻子");

		HammerRecipes.add("沙砾",new MaterialData(Material.COBBLESTONE),new ItemStack(Material.GRAVEL));
		CraftGuide.addCraftTable("杂项",HammerRecipes.class,"沙砾");
		HammerRecipes.add("沙子",new MaterialData(Material.GRAVEL),new ItemStack(Material.SAND));
		CraftGuide.addCraftTable("杂项",HammerRecipes.class,"沙子");
		
		ItemStack pigment=new ItemStack(Material.POTION);
		im=pigment.getItemMeta();
		im.setLocalizedName("§c颜料");
		((PotionMeta)im).addCustomEffect(new PotionEffect(PotionEffectType.POISON,200,2),false);
		((PotionMeta)im).addCustomEffect(new PotionEffect(PotionEffectType.CONFUSION,200,2),false);
		im.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
		((PotionMeta)im).setColor(Color.fromBGR(0,0,255));
		pigment.setItemMeta(im);
		MzTech.items.put("颜料",pigment);
		Raw redDye;
		Raw yellowDye;
		Raw blueDye;
		try
		{	
			redDye=new Raw(null,new ItemStack(Material.INK_SACK,1,(short)1));
			yellowDye=new Raw(null,new ItemStack(Material.INK_SACK,1,(short)11));
			blueDye=new Raw(null,new ItemStack(Material.INK_SACK,1,(short)4));
		}
		catch(Throwable e)
		{	
			redDye=new Raw(null,new ItemStack(Enum.valueOf(Material.class,"RED_DYE")));
			yellowDye=new Raw(null,new ItemStack(Enum.valueOf(Material.class,"YELLOW_DYE")));
			blueDye=new Raw(null,new ItemStack(Enum.valueOf(Material.class,"BLUE_DYE")));
		}
		ItemStack waterBottle=new ItemStack(Material.POTION);
		im=waterBottle.getItemMeta();
		((PotionMeta)im).setBasePotionData(new PotionData(PotionType.THICK));
		waterBottle.setItemMeta(im);
		Raw waterBottleRaw=new Raw(null,waterBottle);
		SmilingCraftingTable.add("颜料",pigment,null,redDye,yellowDye,blueDye,air,waterBottleRaw);
		CraftGuide.addCraftTable("杂项",SmilingCraftingTable.class,"颜料");
		
		try
		{	
			SmilingCraftingTable.add("蜘蛛网",new ItemStack(Material.WEB),null,string,string,string,string,string,string,string,string,string);
		}
		catch(Throwable e)
		{	
			SmilingCraftingTable.add("蜘蛛网",new ItemStack(Enum.valueOf(Material.class,"COBWEB")),null,string,string,string,string,string,string,string,string,string);
		}
		CraftGuide.addCraftTable("杂项",SmilingCraftingTable.class,"蜘蛛网");
		
		try
		{	
			ScreenRecipes.add("木炭",new ItemStack(Material.SULPHUR,3),new ItemStack(Material.COAL,1,(short)1),100);
		}
		catch(Throwable e)
		{	
			ScreenRecipes.add("木炭",new ItemStack(Enum.valueOf(Material.class,"GUNPOWDER"),3),new ItemStack(Enum.valueOf(Material.class,"CHARCOAL")),100);
		}
		CraftGuide.addCraftTable("杂项",ScreenRecipes.class,"木炭");
		
		ScreenRecipes.add("绿宝石",new ItemStack(Material.GRAVEL),new ItemStack(Material.EMERALD),1);
		CraftGuide.addCraftTable("杂项",ScreenRecipes.class,"绿宝石");
		
		ScreenRecipes.add("钻石",new ItemStack(Material.GRAVEL),new ItemStack(Material.DIAMOND),1);
		CraftGuide.addCraftTable("杂项",ScreenRecipes.class,"钻石");
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	void onEatItem(EatItemEvent event)
	{	
		switch(event.item)
		{	
		case "颜料":
			event.player.sendMessage(MzTech.MzTechPrefix+"§4奥利给干了兄弟们");
			break;
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	void onPlayerInteract(PlayerInteractEvent event)
	{	
		if(event.isCancelled())
		{	
			return;
		}
		if(event.getAction()==Action.RIGHT_CLICK_BLOCK)
		{	
			String machine=MzTech.isMachine(event.getClickedBlock().getLocation());
			if(machine!=null)
			{	
				switch(machine)
				{	
				case "村民的鼻子":
					if(MzTech.canOpen(event.getPlayer()))
					{
						event.getClickedBlock().getWorld().dropItemNaturally(event.getClickedBlock().getLocation().add(0.5,0.5,0.5),new ItemStack(Material.EMERALD));
						event.getClickedBlock().setType(Material.AIR);
						MzTech.removeMachine(event.getClickedBlock().getLocation());
						event.setCancelled(true);
					}
					break;
				}
			}
		}
	}
	
	public static void saveBag(String id)
	{	
		try
		{	
			File f=new File(MzTech.instance.getDataFolder().getAbsolutePath()+"/bags/"+id+".inv");
			if(!f.exists())
			{	
				f.createNewFile();
			}
			DataOutputStream fos=new DataOutputStream(new FileOutputStream(f));
			MzTech.writeInventory(fos,bags.get(id));
			fos.close();
		}
		catch(Exception e)
		{	
			e.printStackTrace();
		}
	}
	
	public static void onDisable()
	{	
		bags.forEach((id,inv)->
		{	
			Lists.newArrayList(inv.getViewers()).forEach(v->
			{	
				v.closeInventory();
			});
			saveBag(id);
		});
	}
	
	public static String getBagId(ItemStack bag,String player)
	{	
		String[] id= {null};
		if(bag!=null&&bag.hasItemMeta()&&bag.getItemMeta().hasLore())
		{	
			bag.getItemMeta().getLore().forEach(l->
			{	
				if(l.startsWith("§0ID: "))
				{	
					id[0]=l.substring(6);
				}
			});
		}
		if(player!=null&&id[0]==null)
		{	
			File path=new File(MzTech.instance.getDataFolder().getAbsolutePath()+"/bags");
			if(!path.exists())
			{	
				path.mkdirs();
			}
			for(int i=0;;i++)
			{	
				id[0]=player+"#"+i;
				try
				{	
					File f=new File(MzTech.instance.getDataFolder().getAbsolutePath()+"/bags/"+id[0]+".inv");
					if(!f.exists())
					{	
						break;
					}
				}
				catch(Exception e)
				{	
					e.printStackTrace();
				}
			}
			ItemMeta im = bag.getItemMeta();
			List<String> lore = im.hasLore()?im.getLore():new ArrayList<>();
			lore.add("§0ID: "+id[0]);
			im.setLore(lore);
			bag.setItemMeta(im);
		}
		return id[0];
	}
	
	public static Inventory getBag(String id,int slotNum)
	{	
		Inventory bag=bags.get(id);
		if(bag==null)
		{	
			File f=new File(MzTech.instance.getDataFolder().getAbsolutePath()+"/bags/"+id+".inv");
			if(!f.exists())
			{	
				File path=new File(MzTech.instance.getDataFolder().getAbsolutePath()+"/bags");
				if(!path.exists())
				{	
					path.mkdirs();
				}
				try
				{	
					f.createNewFile();
					DataOutputStream fos=new DataOutputStream(new FileOutputStream(f));
					bag=Bukkit.createInventory(null,slotNum,MzTech.items.get("背包").getItemMeta().getLocalizedName());
					MzTech.writeInventory(fos,bag);
					fos.close();
				}
				catch(Exception e)
				{	
					e.printStackTrace();
				}
			}
			else
			{	
				try
				{	
					File f2=new File(MzTech.instance.getDataFolder().getAbsolutePath()+"/bags/"+id+".inv");
					DataInputStream fis=new DataInputStream(new FileInputStream(f2));
					bag=MzTech.readInventory(fis,"背包");
					fis.close();
				}
				catch(Exception e)
				{	
					e.printStackTrace();
				}
			}
			if(slotNum>0&&bag.getSize()!=slotNum)
			{	
				Inventory newBag=Bukkit.createInventory(null,slotNum,MzTech.items.get("背包").getItemMeta().getLocalizedName());
				newBag.setContents(bag.getContents());
				bag=newBag;
			}
		}
		bags.put(id,bag);
		return bag;
	}
	
//	@EventHandler(priority = EventPriority.HIGHEST)
//	void onInventoryClick(InventoryClickEvent event)
//	{	
//		if(event.isCancelled())
//		{	
//			return;
//		}
//		if(event.getClick()==ClickType.RIGHT)
//		{	
//			ItemStack is=event.getCurrentItem();
//			if(MzTech.isItem(is,"背包"))
//			{	
//				event.setCancelled(true);
//				event.getWhoClicked().openInventory(getBag(getBagId(is,event.getWhoClicked().getName())));
//				event.getWhoClicked().getWorld().playSound(event.getWhoClicked().getLocation(),Sound.BLOCK_NOTE_BELL,1,1);
//				event.setCurrentItem(is);
//			}
//		}
//	}
	
	@EventHandler
	void onMoveItem(MoveItemEvent event)
	{	
		bags.forEach((id,inv)->
		{	
			inv.getViewers().forEach(h->
			{	
				if(h==event.inventoryView.getPlayer())
				{	
					if(id.equals(getBagId(event.getFromItem(),null)))
					{	
						event.setCancelled(true);
					}
					else if(event.num<=0&&id.equals(getBagId(event.getToItem(),null)))
					{	
						event.setCancelled(true);
					}
				}
			});
		});
//		if(event.fromInv!=null&&event.fromSlot>=0&&event.clickType==ClickType.RIGHT)
//		{	
//			ItemStack is=event.fromInv.getItem(event.fromSlot);
//			if(MzTech.isItem(is,"背包"))
//			{	
//				event.setCancelled(true);
//				((Player)event.toInv.getHolder()).openInventory(getBag(getBagId(is,((Player)event.toInv.getHolder()).getName())));
//				((Player)event.toInv.getHolder()).getWorld().playSound(((Player)event.toInv.getHolder()).getLocation(),Sound.ENTITY_BAT_TAKEOFF,1,1);
//				event.fromInv.setItem(event.fromSlot,is);
//			}
//		}
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler
	void onClickItem(ClickItemEvent event)
	{	
		if(event.isCancelled()||event.leftClick)
		{	
			return;
		}
		switch(event.item)
		{	
		case "背包":
		case "大背包":
			event.setCancelled(true);
			String id;
			if(event.itemStack.getAmount()>1)
			{	
				ItemStack is=new ItemStack(event.itemStack);
				is.setAmount(is.getAmount()-1);
				event.itemStack.setAmount(1);
				id = getBagId(event.itemStack,event.player.getName());
				MzTech.giveInHand(event.player,is);
			}
			else
			{	
				id = getBagId(event.itemStack,event.player.getName());
			}
			switch(event.hand)
			{	
			case HAND:
				event.player.getInventory().setItemInMainHand(event.itemStack);
				break;
			case OFF_HAND:
				event.player.getInventory().setItemInOffHand(event.itemStack);
				break;
			default:
				break;
			}
			Inventory inv=getBag(id,event.item.equals("背包")?9:27);
			event.player.openInventory(inv);
			event.player.getWorld().playSound(event.player.getLocation(),Sound.ENTITY_BAT_TAKEOFF,1,1);
			break;
		case "草泥马币":
			event.itemStack.setAmount(event.itemStack.getAmount()-1);
			Object cnmb=MzTech.getConfigs().get("cnmb");
			double value=1;
			if(cnmb instanceof Integer)
			{	
				value=(Integer)cnmb;
			}
			else if(cnmb instanceof Double)
			{	
				value=(Double)cnmb;
			}
			BasicMachines.econ.depositPlayer(event.player,value);
			event.player.sendMessage(MzTech.MzTechPrefix+"§a￥"+MzTech.getConfigs().get("cnmb")+"已添加至您的账户");
			try
			{	
				event.player.playSound(event.player.getLocation(),Sound.BLOCK_NOTE_BELL,1,1);
			}
			catch(Throwable e)
			{	
				event.player.playSound(event.player.getLocation(),Enum.valueOf(Sound.class,"BLOCK_NOTE_BLOCK_BELL"),1,1);
			}
			break;
		case "空刷怪蛋":
			if(event.clickEntity && event.entity instanceof LivingEntity)
			{	
				event.setCancelled(true);
				if(event.entity instanceof Villager)
				{	
					event.player.sendMessage("§4你也想尝尝被塞进蛋里的滋味吗？");
					return;
				}
				if(event.entity instanceof Player)
				{	
					event.player.sendMessage("§4你也想尝尝被塞进蛋里的滋味吗？");
					return;
				}
				else if(event.entity instanceof Wither || event.entity instanceof EnderDragon)
				{	
					event.player.sendMessage("§4这……太大一只了");
					return;
				}
				EntityDamageByEntityEvent e=new EntityDamageByEntityEvent(event.player,event.entity,DamageCause.ENTITY_ATTACK,1);
				Bukkit.getPluginManager().callEvent(e);
				if(!e.isCancelled())
				{	
					if((Boolean)MzTech.getConfigs().get("HighVersion"))
					{	
						if(Material.getMaterial(event.entity.getType().toString().toUpperCase()+"_SPAWN_EGG")==null)
						{	
							return;
						}
						MzTech.giveInHand(event.player,new ItemStack(Material.getMaterial(event.entity.getType().toString().toUpperCase()+"_SPAWN_EGG")));
						event.itemStack.setAmount(event.itemStack.getAmount()-1);
					}
					else
					{	
						ItemStack is=new ItemStack(Material.MONSTER_EGG);
						ItemMeta im = is.getItemMeta();
						((SpawnEggMeta)im).setSpawnedType(event.entity.getType());
						is.setItemMeta(im);
						MzTech.giveInHand(event.player,is);
						event.itemStack.setAmount(event.itemStack.getAmount()-1);
					}
					event.entity.remove();
				}
			}
			break;
		}
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler
	void onBreakMachine(BreakMachineEvent event)
	{	
		if(event.isCancelled()||!event.isDrops())
		{	
			return;
		}
		if(event.machine.equals("苹果树叶"))
		{	
			if(event.player==null||(event.player.getGameMode()!=GameMode.CREATIVE&&(event.player.getItemInHand()==null||event.player.getItemInHand().getType()==Material.AIR||event.player.getItemInHand().getType()!=Material.SHEARS&&event.player.getItemInHand().getItemMeta().getEnchantLevel(Enchantment.SILK_TOUCH)<=0)))
			{	
				event.setDrops(false);
				event.block.getWorld().dropItem(event.block.getLocation().add(0.5,0.5,0.5),new ItemStack(Material.APPLE,1));
				if(new Random().nextInt(100)<5)event.block.getWorld().dropItem(event.block.getLocation().add(0.5,0,0.5),new ItemStack(MzTech.items.get("苹果树苗")));
			}
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	void onChunkPopulate(ChunkPopulateEvent event)
	{	
		if(rand.nextInt(10)<100)
		{
			Block block=event.getChunk().getBlock(rand.nextInt(16),0,rand.nextInt(16));
			block=event.getWorld().getHighestBlockAt(block.getX(),block.getZ());
			if(block!=null&&block.getY()<255)
			{	
				switch(block.getType())
				{	
				case GRASS:
					block=block.getLocation().add(0,1,0).getBlock();
					break;
				case LONG_GRASS:
					break;
				default:
					return;
				}
				block.setType(MzTech.items.get("苹果树苗").getType());
				MzTech.addMachine(block.getLocation(),"苹果树苗",null);
			}
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	void onStructureGrow(StructureGrowEvent event)
	{	
		if(event.isCancelled())
		{	
			return;
		}
		if("苹果树苗".equals(MzTech.isMachine(event.getLocation())))
		{	
			MzTech.removeMachine(event.getLocation());
			event.getBlocks().forEach(b->
			{	
				try
				{
					switch(b.getType())
					{	
//					case LOG:
//						MzTech.addMachine(b.getLocation(),"苹果树原木");
//						break;
					case LEAVES:
						MzTech.addMachine(b.getLocation(),"苹果树叶",null);
						break;
					case SAPLING:
						MzTech.addMachine(b.getLocation(),"苹果树苗",null);
						break;
					default:
						break;
					}
				}
				catch(Throwable e)
				{	
					if(b.getType()==Enum.valueOf(Material.class,"OAK_LEAVES"))
					{	
						MzTech.addMachine(b.getLocation(),"苹果树叶",null);
					}
					else if(b.getType()==Enum.valueOf(Material.class,"OAK_SAPLING"))
					{	
						MzTech.addMachine(b.getLocation(),"苹果树苗",null);
					}
				}
			});
		}
	}
}
