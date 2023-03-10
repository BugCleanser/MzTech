package mz.tech;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.entity.Enderman;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerFishEvent.State;
import org.bukkit.inventory.DoubleChestInventory;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.MaterialData;
import org.bukkit.material.PistonBaseMaterial;
import org.bukkit.material.Stairs;
import org.bukkit.material.Step;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.BlockPosition;
import com.google.common.collect.Lists;

/** 
 *??????????????????????????????API
*/
public class Tools implements Listener
{	
	private static List<ItemStack> autoBreak=null;
	/*
	 * ?????????????????????????????????
	 * ??????????????????????????????playersDigging.get(??????).cancel();
	 */
	public static Map<Player,BukkitRunnable> playersDigging=new HashMap<>();
	/*
	 * ?????????
	 * ??????BlockBreakEvent?????????????????????????????????
	 * ???????????????????????????????????????????????????
	 */
	public static List<Player> breakLocks=new ArrayList<>();
	
	/**
	 *?????????????????????
	*/
	@SuppressWarnings("deprecation")
	public Tools()
	{	
		ItemStack tool=new ItemStack(Material.DIAMOND_AXE);
		ItemMeta im=tool.getItemMeta();
		im.setLocalizedName("??6??????");
		tool.setItemMeta(im);
		CraftGuide.addClassify("??????",tool);
		
		Enchantment silkTouch=null;
		try
		{	
			silkTouch = Enchantment.getByName("silk_touch");
			if(silkTouch==null)
			{	
				silkTouch=(Enchantment) Enchantment.class.getField("SILK_TOUCH").get(null);
			}
			ReflectionWrapper.setStaticFieldValue(Enchantment.class.getField("SILK_TOUCH"),Enchants.regEnchant(33,new NamespacedKey("minecraft","silk_touch"),silkTouch.getName(),silkTouch.isTreasure(),silkTouch.isCursed(),3,silkTouch.getItemTarget()));
		}
		catch (Exception e)
		{	
			MzTech.throwRuntime(e);
		}

		ItemStack silkTouch2Tool=new ItemStack(Material.DIAMOND_PICKAXE);
		im=silkTouch2Tool.getItemMeta();
		im.addEnchant(Enchantment.SILK_TOUCH,2,true);
		silkTouch2Tool.setItemMeta(im);
		ItemStack silkTouch2ToolFuntion;
		try
		{	
			silkTouch2ToolFuntion=new ItemStack(Material.MOB_SPAWNER);
		}
		catch(Throwable e)
		{	
			silkTouch2ToolFuntion=new ItemStack(Enum.valueOf(Material.class,"SPAWNER"));
		}
		im=silkTouch2ToolFuntion.getItemMeta();
		im.setLocalizedName("??4?????????????????????");
		silkTouch2ToolFuntion.setItemMeta(im);
		ItemFunctionShow.add("????????????2",silkTouch2Tool,silkTouch2ToolFuntion);
		CraftGuide.addCraftTable("??????",ItemFunctionShow.class,"????????????2");

		ItemStack silkTouch3Tool=new ItemStack(Material.DIAMOND_PICKAXE);
		im=silkTouch3Tool.getItemMeta();
		im.addEnchant(Enchantment.SILK_TOUCH,3,true);
		silkTouch3Tool.setItemMeta(im);
		ItemStack silkTouch3ToolFuntion=new ItemStack(silkTouch2ToolFuntion.getType());
		im=silkTouch3ToolFuntion.getItemMeta();
		im.setLocalizedName("??4?????????????????????????????????????????????");
		silkTouch3ToolFuntion.setItemMeta(im);
		ItemFunctionShow.add("????????????3",silkTouch3Tool,silkTouch3ToolFuntion);
		CraftGuide.addCraftTable("??????",ItemFunctionShow.class,"????????????3");
		
		ItemStack workBenchFunction=new ItemStack(Material.FEATHER);
		im=workBenchFunction.getItemMeta();
		im.setLocalizedName("??????????????????????????????");
		workBenchFunction.setItemMeta(im);
		try
		{	
			ItemFunctionShow.add("?????????",new ItemStack(Material.WORKBENCH),workBenchFunction);
		}
		catch(Throwable e)
		{	
			ItemFunctionShow.add("?????????",new ItemStack(Enum.valueOf(Material.class,"CRAFTING_TABLE")),workBenchFunction);
		}
		CraftGuide.addCraftTable("??????",ItemFunctionShow.class,"?????????");
		
		ItemStack enderChestFunction=new ItemStack(Material.FEATHER);
		im=enderChestFunction.getItemMeta();
		im.setLocalizedName("??????????????????????????????");
		enderChestFunction.setItemMeta(im);
		ItemFunctionShow.add("?????????",new ItemStack(Material.ENDER_CHEST),enderChestFunction);
		CraftGuide.addCraftTable("??????",ItemFunctionShow.class,"?????????");
		
		ItemStack is=new ItemStack(Material.FISHING_ROD);
		im=is.getItemMeta();
		im.setLocalizedName("??6??????");
		im.setLore(Lists.newArrayList("??7???????????????????????????","??4?????? ??7??????"));
		is.setItemMeta(im);
		MzTech.items.put("??????",is);
		Raw air=new Raw(null,new ItemStack(Material.AIR));
		Raw leash;
		try
		{	
			leash=new Raw(null,new ItemStack(Material.LEASH));
		}
		catch(Throwable e)
		{	
			leash=new Raw(null,new ItemStack(Enum.valueOf(Material.class,"LEAD")));
		}
		Raw ironIngot=new Raw(null,new ItemStack(Material.IRON_INGOT));
		Raw stick=new Raw(null,new ItemStack(Material.STICK));
		SmilingCraftingTable.add("??????",is,null,air,air,leash,air,leash,ironIngot,stick,ironIngot,ironIngot);
		CraftGuide.addCraftTable("??????",SmilingCraftingTable.class,"??????");
		
		Enchants.regEnchant(201,new NamespacedKey(MzTech.instance,"fast_cut"),"????????????",true,false,1,EnchantmentTarget.TOOL);
		
		ItemStack fellingAxe=new ItemStack(Material.DIAMOND_AXE);
		im=fellingAxe.getItemMeta();
		im.setLocalizedName("??4?????????");
		im.addEnchant(Enchantment.getByName("????????????"),1,true);
		fellingAxe.setItemMeta(im);
		MzTech.items.put("?????????",fellingAxe);
		Raw diamond=new Raw(null,new ItemStack(Material.DIAMOND));
		Raw emerald=new Raw(null,new ItemStack(Material.EMERALD));
		Raw blazeRod=new Raw(null,new ItemStack(Material.BLAZE_ROD));
		SmilingCraftingTable.add("?????????",fellingAxe,null,diamond,emerald,air,diamond,blazeRod,air,air,blazeRod,air);
		CraftGuide.addCraftTable("??????",SmilingCraftingTable.class,"?????????");
		
		ItemStack obsidianPickaxe=new ItemStack(Material.DIAMOND_PICKAXE);
		im=obsidianPickaxe.getItemMeta();
		im.setLocalizedName("??5????????????");
		im.setLore(Lists.newArrayList("??7????????????"));
		obsidianPickaxe.setItemMeta(im);
		MzTech.items.put("????????????",obsidianPickaxe);
		Raw obsidian=new Raw(null,new ItemStack(Material.OBSIDIAN));
		SmilingCraftingTable.add("????????????",obsidianPickaxe,null,obsidian,obsidian,obsidian,air,stick,air,air,stick,air);
		CraftGuide.addCraftTable("??????",SmilingCraftingTable.class,"????????????");
		
		ItemStack bedrockPickaxe=new ItemStack(Material.DIAMOND_PICKAXE);
		im=bedrockPickaxe.getItemMeta();
		im.setLocalizedName("??8??l?????????");
		im.setLore(Lists.newArrayList("??7????????????"));
		bedrockPickaxe.setItemMeta(im);
		MzTech.items.put("?????????",bedrockPickaxe);
		Raw bedrock=new Raw(null,new ItemStack(Material.BEDROCK));
		SmilingCraftingTable.add("?????????",bedrockPickaxe,null,bedrock,bedrock,bedrock,air,stick,air,air,stick,air);
		CraftGuide.addCraftTable("??????",SmilingCraftingTable.class,"?????????");

		Enchants.regEnchant(202,new NamespacedKey(MzTech.instance,"range_mining"),"????????????",true,false,2,EnchantmentTarget.TOOL);
		ItemStack rangeBreakBook=new ItemStack(Material.ENCHANTED_BOOK);
		Enchants.setEnchant(rangeBreakBook,Enchantment.getByName("????????????"),1);
		MzTech.items.put("????????????",rangeBreakBook);
		Raw netherStar=new Raw(null,new ItemStack(Material.NETHER_STAR));
		Raw piston;
		try
		{	
			piston=new Raw(null,new ItemStack(Material.PISTON_BASE));
		}
		catch(Throwable e)
		{	
			piston=new Raw(null,new ItemStack(Enum.valueOf(Material.class,"PISTON")));
		}
		Raw book=new Raw(null,new ItemStack(Material.BOOK));
		SmilingCraftingTable.add("????????????",rangeBreakBook,null,
				netherStar,piston,netherStar,
				piston,book,piston,
				netherStar,piston,netherStar);
		CraftGuide.addCraftTable("??????",SmilingCraftingTable.class,"????????????");
//		ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(MzTech.instance,PacketType.Play.Server.BLOCK_BREAK_ANIMATION)
//		{	
//			@Override
//			public void onPacketSending(PacketEvent event)
//			{	
//				if(event.getPacketType()==PacketType.Play.Server.BLOCK_BREAK_ANIMATION)
//				{	
//					Block block = event.getPacket().getBlockPositionModifier().read(0).toLocation(event.getPlayer().getWorld()).getBlock();
//					PacketContainer packet=new PacketContainer(PacketType.Play.Server.BLOCK_BREAK_ANIMATION);
//					packet.getIntegers().write(1,event.getPacket().getIntegers().read(1));
//					new BukkitRunnable()
//					{	
//						public void run()
//						{	
//							if(chainDamagingBlocks.containsKey(block))
//							{	
//								int[] i= {1};
//								chainDamagingBlocks.get(block).forEach(b->
//								{	
//									packet.getIntegers().write(0,i[0]);
//									packet.getBlockPositionModifier().write(0,new BlockPosition(b.getLocation().toVector()));
//									try
//									{	
//										ProtocolLibrary.getProtocolManager().sendServerPacket(event.getPlayer(),packet);
//									}
//									catch(Exception e)
//									{	
//										e.printStackTrace();
//									}
//									i[0]++;
//								});
//							}
//						}
//					}.runTask(MzTech.instance);
//				}
//			}
//		});
		
		ItemStack stoneHammer=new ItemStack(Material.STONE_PICKAXE);
		im=stoneHammer.getItemMeta();
		im.setLocalizedName("??7??????");
		stoneHammer.setItemMeta(im);
		MzTech.items.put("??????",stoneHammer);
		Raw cobblestone=new Raw(null,new ItemStack(Material.COBBLESTONE));
		SmilingCraftingTable.add("??????",stoneHammer,null,
				cobblestone,cobblestone,cobblestone,
				cobblestone,stick,cobblestone,
				air,stick);
		CraftGuide.addCraftTable("??????",SmilingCraftingTable.class,"??????");
		
		ItemStack ironHammer=new ItemStack(Material.IRON_PICKAXE);
		im=ironHammer.getItemMeta();
		im.setLocalizedName("??f??????");
		ironHammer.setItemMeta(im);
		MzTech.items.put("??????",ironHammer);
		SmilingCraftingTable.add("??????",ironHammer,null,
				ironIngot,ironIngot,ironIngot,
				ironIngot,stick,ironIngot,
				air,stick);
		CraftGuide.addCraftTable("??????",SmilingCraftingTable.class,"??????");
		
		ItemStack goldHammer;
		try
		{	
			goldHammer=new ItemStack(Material.GOLD_PICKAXE);
		}
		catch(Throwable e)
		{	
			goldHammer=new ItemStack(Enum.valueOf(Material.class,"GOLDEN_PICKAXE"));
		}
		im=goldHammer.getItemMeta();
		im.setLocalizedName("??e??????");
		goldHammer.setItemMeta(im);
		MzTech.items.put("??????",goldHammer);
		Raw goldIngot=new Raw(null,new ItemStack(Material.GOLD_INGOT));
		SmilingCraftingTable.add("??????",goldHammer,null,
				goldIngot,goldIngot,goldIngot,
				goldIngot,stick,goldIngot,
				air,stick);
		CraftGuide.addCraftTable("??????",SmilingCraftingTable.class,"??????");
		
		ItemStack diamondHammer=new ItemStack(Material.DIAMOND_PICKAXE);
		im=diamondHammer.getItemMeta();
		im.setLocalizedName("??b?????????");
		diamondHammer.setItemMeta(im);
		MzTech.items.put("?????????",diamondHammer);
		HammerRecipes.hammers.add("??????");
		HammerRecipes.hammers.add("??????");
		HammerRecipes.hammers.add("??????");
		HammerRecipes.hammers.add("?????????");
		SmilingCraftingTable.add("?????????",diamondHammer,null,
				diamond,diamond,diamond,
				diamond,stick,diamond,
				air,stick);
		CraftGuide.addCraftTable("??????",SmilingCraftingTable.class,"?????????");
		
		Bukkit.getPluginManager().registerEvents(new HammerRecipes(),MzTech.instance);
		
		ItemStack wrench=new ItemStack(Material.IRON_HOE);
		im=wrench.getItemMeta();
		im.setLocalizedName("??6??????");
		im.setLore(Lists.newArrayList("??7????????????"));
		wrench.setItemMeta(im);
		MzTech.items.put("??????",wrench);
		SmilingCraftingTable.add("??????",wrench,null,
				ironIngot,air,ironIngot,
				air,ironIngot,air,
				ironIngot,air,ironIngot);
		CraftGuide.addCraftTable("??????",SmilingCraftingTable.class,"??????");
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler(priority = EventPriority.HIGH)
	void onClickItem(ClickItemEvent event)
	{	
		if(event.cancelled)
		{	
			return;
		}
		switch(event.item)
		{	
		case "??????":
			if(event.clickBlock&&!event.leftClick)
			{	
				event.setCancelled(true);
				switch(event.block.getType())
				{	
				case PISTON_BASE:
				case PISTON_STICKY_BASE:
					if(((PistonBaseMaterial)event.block.getState().getData()).isPowered())
					{	
						return;
					}
					break;
				case CHEST:
				case TRAPPED_CHEST:
					if(((Chest)event.block.getState()).getInventory() instanceof DoubleChestInventory)
					{	
						return;
					}
					break;
				case BED_BLOCK:
				case PISTON_EXTENSION:
				case PISTON_MOVING_PIECE:
					return;
				default:
					break;
				}
				BlockPlaceEvent e=new BlockPlaceEvent(event.block,event.block.getState(),event.block,new ItemStack(Material.AIR),event.player,true);
				Bukkit.getPluginManager().callEvent(e);
				if(!e.isCancelled())
				{	
					BlockState state = e.getBlock().getState();
					MaterialData data = state.getData();
					Method getFacing=null;
					Method setFacingDirection=null;
					if(data instanceof Step)
					{	
						((Step)data).setInverted(!((Step)data).isInverted());
						state.setData(data);
						state.update();
					}
					else if(data instanceof Stairs)
					{	
						data.setData((byte) ((data.getData()+1)%8));
						state.setData(data);
						state.update();
					}
					else
					{	
						try
						{	
							getFacing=ReflectionWrapper.getMethodParent(data.getClass(),"getFacing");
							setFacingDirection=ReflectionWrapper.getMethodParent(data.getClass(),"setFacingDirection",BlockFace.class);
						}
						catch(Exception exception)
						{	
							return;
						}
						int i;
						for(i=((BlockFace)ReflectionWrapper.invokeMethod(getFacing,data)).ordinal()+1;i<BlockFace.values().length;i++)
						{	
							try
							{	
								setFacingDirection.invoke(data,BlockFace.values()[i]);
								state.setData(data);
								state.update();
								state = e.getBlock().getState();
								data = state.getData();
								if(((BlockFace)getFacing.invoke(data)).ordinal()==i)
								{	
									break;
								}
							}
							catch(Exception exc)//??????
							{	
							}
						}
						if(i==BlockFace.values().length)
						{	
							for(i=0;i<BlockFace.values().length;i++)
							{	
								try
								{	
									setFacingDirection.invoke(data,BlockFace.values()[i]);
									state.setData(data);
									state.update();
									state = e.getBlock().getState();
									data = state.getData();
									if(((BlockFace)getFacing.invoke(data)).ordinal()==i)
									{	
										break;
									}
								}
								catch(Exception exc)//??????
								{	
								}
							}
						}
					}
					if(event.player.getGameMode()!=GameMode.CREATIVE)damageTool(event.player.getItemInHand());
					try
					{	
						event.block.getWorld().playSound(event.block.getLocation().add(0.5,0.5,0.5),Sound.BLOCK_WOOD_BUTTON_CLICK_ON,SoundCategory.BLOCKS,1,10f);
					}
					catch(Throwable e1)
					{	
						event.block.getWorld().playSound(event.block.getLocation().add(0.5,0.5,0.5),Enum.valueOf(Sound.class,"BLOCK_WOODEN_BUTTON_CLICK_ON"),SoundCategory.BLOCKS,1,10f);
					}
				}
			}
			break;
		}
	}
	
	public boolean canBreak(ItemStack tool,Block block)
	{	
		switch(block.getType())//???????????????
		{	
		case BEDROCK:
			if(MzTech.isItem(tool,"????????????"))
			{	
				return true;
			}
		case BARRIER:
		case ENDER_PORTAL_FRAME:
			if(MzTech.isItem(tool,"?????????"))
			{	
				return true;
			}
		case AIR:
		case WATER:
		case STATIONARY_WATER:
		case LAVA:
		case STATIONARY_LAVA:
		case COMMAND:
		case COMMAND_CHAIN:
		case COMMAND_MINECART:
		case COMMAND_REPEATING:
		case STRUCTURE_BLOCK:
		case STRUCTURE_VOID:
			return false;
		default:
			return true;
		}
	}
	public static int forUrgent(int time,Player player)
	{	
		PotionEffect fastDig = player.getPotionEffect(PotionEffectType.FAST_DIGGING);
		if(fastDig!=null)
		{	
			time=(int) (time*(1+0.2*fastDig.getAmplifier()));
		}
		PotionEffect slowDig = player.getPotionEffect(PotionEffectType.SLOW_DIGGING);
		if(slowDig!=null)
		{	
			time=(int) (time*Math.pow(0.3,slowDig.getAmplifier()));
		}
		return time*(player.isFlying()?2:1);
	}
	public static void setBlockDamage(Location loc,int entity,int degree)
	{	
		if(degree>0)
		{	
			PacketContainer packet=new PacketContainer(PacketType.Play.Server.BLOCK_BREAK_ANIMATION);
			packet.getIntegers().write(0,entity);
			packet.getBlockPositionModifier().write(0,new BlockPosition(loc.getBlockX(),loc.getBlockY(),loc.getBlockZ()));
			packet.getIntegers().write(1,degree);
			ProtocolLibrary.getProtocolManager().broadcastServerPacket(packet,loc,32);
		}
	}
	public static int forEfficiency(int time,ItemStack tool)
	{	
		return (int) (time/(Math.pow(tool.getItemMeta().getEnchantLevel(Enchantment.DIG_SPEED),2)/8+1));
	}
	@EventHandler
	void onPlayerInteract(PlayerInteractEvent event)
	{	
		if(event.getAction()==Action.RIGHT_CLICK_AIR)
		{	
			Material craftingTable;
			try
			{	
				craftingTable=Material.WORKBENCH;
			}
			catch(Throwable e)
			{	
				craftingTable=Enum.valueOf(Material.class,"CRAFTING_TABLE");
			}
			if(event.getItem().getType()==craftingTable)//?????????????????????
			{	
				event.getPlayer().openWorkbench(null,true);
			}
			else if(event.getItem().getType()==Material.ENDER_CHEST)//?????????????????????
			{	
				event.getPlayer().openInventory(event.getPlayer().getEnderChest());
				try
				{	
					event.getPlayer().getWorld().playSound(event.getPlayer().getLocation(),Sound.BLOCK_ENDERCHEST_OPEN,1,1);
				}
				catch(Throwable e)
				{	
					event.getPlayer().getWorld().playSound(event.getPlayer().getLocation(),Enum.valueOf(Sound.class,"BLOCK_ENDER_CHEST_OPEN"),1,1);
				}
			}
		}
	}
	@EventHandler
	void onBlockDamage(BlockDamageEvent event)
	{	
		if(event.isCancelled()||event.getInstaBreak())
		{	
			return;
		}
		if(MzTech.isItem(event.getItemInHand(),"????????????"))
		{	
			if(event.getBlock().getType()==Material.BEDROCK)
			{	
				int[] i= {0};
				int[] timeTotal= {forUrgent(forEfficiency(200,event.getItemInHand()),event.getPlayer())};
				BukkitRunnable runnable=new BukkitRunnable()
				{	
					@Override
					public void run()
					{	
						if(!event.getBlock().isEmpty())
						{	
							i[0]++;
							if(timeTotal[0]>0)setBlockDamage(event.getBlock().getLocation(),0,i[0]*9/timeTotal[0]);
							if(i[0]>timeTotal[0])
							{	
								BlockBreakEvent e=new BlockBreakEvent(event.getBlock(),event.getPlayer());
								Bukkit.getPluginManager().callEvent(e);
								if(e.isCancelled())return;
								event.getBlock().getWorld().dropItemNaturally(event.getBlock().getLocation().add(0.5,0,0.5),new ItemStack(Material.BEDROCK));
								showBlockBreak(event.getBlock());
								event.getBlock().getWorld().playSound(event.getBlock().getLocation(),Sound.BLOCK_STONE_BREAK,1,1);
								event.getBlock().breakNaturally();
								damageTool(event.getItemInHand());
								this.cancel();
							}
						}
						else
						{	
							this.cancel();
						}
					}
				};
				runnable.runTaskTimer(MzTech.instance,0,1);
				playersDigging.put(event.getPlayer(),runnable);
			}
		}
		else if(MzTech.isItem(event.getItemInHand(),"?????????"))
		{	
			switch(event.getBlock().getType())
			{	
			case ENDER_PORTAL_FRAME:
			case BARRIER:
			case BEDROCK:
				int[] i= {0};
				int[] timeTotal= {forUrgent(forEfficiency(150,event.getItemInHand()),event.getPlayer())};
				BukkitRunnable runnable=new BukkitRunnable()
				{	
					@Override
					public void run()
					{	
						if(!event.getBlock().isEmpty())
						{	
							i[0]++;
							if(timeTotal[0]>0)setBlockDamage(event.getBlock().getLocation(),0,i[0]*9/timeTotal[0]);
							if(i[0]>timeTotal[0])
							{	
								BlockBreakEvent e=new BlockBreakEvent(event.getBlock(),event.getPlayer());
								Bukkit.getPluginManager().callEvent(e);
								if(e.isCancelled())return;
								event.getBlock().getWorld().dropItemNaturally(event.getBlock().getLocation().add(0.5,0,0.5),new ItemStack(event.getBlock().getType()));
								showBlockBreak(event.getBlock());
								event.getBlock().getWorld().playSound(event.getBlock().getLocation(),Sound.BLOCK_STONE_BREAK,1,1);
								event.getBlock().breakNaturally();
								this.cancel();
							}
						}
						else
						{	
							this.cancel();
						}
					}
				};
				runnable.runTaskTimer(MzTech.instance,0,1);
				playersDigging.put(event.getPlayer(),runnable);
				break;
			default:
				break;
			}
		}
	}
	@EventHandler
	void onBlockDamageStop(BlockDamageStopEvent event)
	{	
		if(playersDigging.containsKey(event.player))
		{	
			playersDigging.get(event.player).cancel();
			playersDigging.remove(event.player);
			setBlockDamage(event.block.getLocation(),0,10);
		}
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler(priority = EventPriority.HIGHEST)
	void onPlayerFish(PlayerFishEvent event)
	{	
		if(event.isCancelled())return;
		if(event.getState()==State.CAUGHT_ENTITY&&Item.class.isAssignableFrom(event.getCaught().getClass()))
		{	
			if(((Item)event.getCaught()).getPickupDelay()==32767)//QuickShop
			{	
				event.setCancelled(true);
				event.getHook().remove();
				return;
			}
		}
		
		ItemStack hook=event.getPlayer().getItemInHand();
		if(hook.getType() != Material.FISHING_ROD)hook=event.getPlayer().getInventory().getItemInOffHand();
		if(hook.getItemMeta().hasLocalizedName()&&hook.getItemMeta().getLocalizedName().equals(MzTech.items.get("??????").getItemMeta().getLocalizedName()))
		{	
			switch(event.getState())
			{	
			case FISHING://??????
				event.getHook().setVelocity(event.getHook().getVelocity().multiply(3).add(event.getPlayer().getVelocity()));
				break;
			case IN_GROUND://??????
				switch(event.getHook().getWorld().getBlockAt(event.getHook().getLocation()).getType())
				{	
				case WATER:
				case STATIONARY_WATER:
				case LAVA:
				case STATIONARY_LAVA:
					return;
				default:
					break;
				}
				Location playerLoc=event.getPlayer().getLocation();
				Location hookLoc=event.getHook().getLocation();
				event.getPlayer().setVelocity(event.getPlayer().getVelocity().add(event.getHook().getVelocity().add(hookLoc.subtract(playerLoc).toVector().multiply(0.5))));
				break;
			case CAUGHT_ENTITY://?????????
				EntityDamageByEntityEvent e=new EntityDamageByEntityEvent(event.getPlayer(),event.getCaught(),DamageCause.ENTITY_ATTACK,0);
				Bukkit.getPluginManager().callEvent(e);
				if(!e.isCancelled())
				{	
					if(Item.class.isAssignableFrom(event.getCaught().getClass()))//?????????
					{	
						((Item)event.getCaught()).setPickupDelay(-1);
						event.getCaught().teleport(event.getPlayer());
						break;
					}
					boolean[] hookItem= {false};
					if(Enderman.class.isAssignableFrom(event.getCaught().getClass()))
					{	
						MaterialData md = ((Enderman)event.getCaught()).getCarriedMaterial();
						if(md != null && md.getItemType() != Material.AIR)
						{	
							hookItem[0]=true;
							event.getCaught().getWorld().dropItem(event.getCaught().getLocation(),new ItemStack(md.getItemType(),1,md.getData()));
							((Enderman)event.getCaught()).setCarriedMaterial(new MaterialData(0));
						}
					}
					else if(LivingEntity.class.isAssignableFrom(event.getCaught().getClass()) && !Player.class.isAssignableFrom(event.getCaught().getClass()))
					{	
						EntityEquipment ee=((LivingEntity)event.getCaught()).getEquipment();
						List<ItemStack> items=Lists.newArrayList(ee.getArmorContents());
						items.add(ee.getItemInMainHand());
						items.add(ee.getItemInOffHand());
						items.forEach((i)->
						{	
							if(i.getType()!=Material.AIR)
							{	
								Item it=event.getCaught().getWorld().dropItemNaturally(event.getCaught().getLocation(),i);
								it.setVelocity(new Vector(0,0,0));
								it.teleport(event.getPlayer());
								it.setPickupDelay(-1);
								hookItem[0]=true;
							}
						});
						ee.clear();
					}
					if(!hookItem[0])
					{	
						Location EntityLoc=event.getCaught().getLocation();
						Location playerLoc2=event.getPlayer().getLocation();
						event.getCaught().setVelocity(event.getCaught().getVelocity().add(event.getPlayer().getVelocity().add(playerLoc2.subtract(EntityLoc).toVector().multiply(0.1))));
					}
				}
				break;
			default:
				break;
			}
		}
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler
	void onEntityDamageByEntity(EntityDamageByEntityEvent event)
	{	
		if(event.getCause()==DamageCause.ENTITY_ATTACK)
		{	
			if(event.getDamager() instanceof Player)
			{	
				ItemStack weapon = ((Player)event.getDamager()).getItemInHand();
				if(weapon!=null&&weapon.getType()!=Material.AIR&&MzTech.isItem(weapon,"?????????"))
				{	
					weapon.setDurability((short) 0);
					((Player)event.getDamager()).setItemInHand(weapon);
				}
			}
		}
	}
	
	@EventHandler
	void onItemSpawn(ItemSpawnEvent event)
	{	
		if(autoBreak!=null)
		{	
			autoBreak.add(event.getEntity().getItemStack());
			event.setCancelled(true);
		}
	}
	
	public static List<ItemStack> fell(Block block)
	{	
		return fell(block,8);
	}
	public static List<ItemStack> fell(Block block,int num)
	{	
		List<ItemStack> r=new ArrayList<>();
		switch(block.getType().name())
		{	
		case "LEAVES":
		case "LEAVES_2":
		case "LOG":
		case "LOG_2":
		case "OAK_LOG":
		case "SPRUCE_LOG":
		case "BIRCH_LOG":
		case "JUNGLE_LOG":
		case "ACACIA_LOG":
		case "DARK_OAK_LOG":
			if(num>0)
			{	
				num--;
			}
			else
			{	
				break;
			}
			autoBreak=r;
			BlockBreakEvent e=new BlockBreakEvent(block,null);
			MzTech.instance.onBlockBreak(e);
			if((!e.isCancelled())&&e.isDropItems())r.addAll(block.getDrops());
			autoBreak=null;
			if(block.getType()!=Material.AIR)
			{	
				showBlockBreak(block);
			}
			block.setType(Material.AIR);
			r.addAll(fell(block.getLocation().add(new Vector(0,0,1)).getBlock(),num));
			r.addAll(fell(block.getLocation().add(new Vector(0,0,-1)).getBlock(),num));
			r.addAll(fell(block.getLocation().add(new Vector(1,0,1)).getBlock(),num));
			r.addAll(fell(block.getLocation().add(new Vector(1,0,0)).getBlock(),num));
			r.addAll(fell(block.getLocation().add(new Vector(1,0,-1)).getBlock(),num));
			r.addAll(fell(block.getLocation().add(new Vector(-1,0,1)).getBlock(),num));
			r.addAll(fell(block.getLocation().add(new Vector(-1,0,0)).getBlock(),num));
			r.addAll(fell(block.getLocation().add(new Vector(-1,0,-1)).getBlock(),num));

			r.addAll(fell(block.getLocation().add(new Vector(0,1,1)).getBlock(),num));
			r.addAll(fell(block.getLocation().add(new Vector(0,1,0)).getBlock(),num));
			r.addAll(fell(block.getLocation().add(new Vector(0,1,-1)).getBlock(),num));
			r.addAll(fell(block.getLocation().add(new Vector(1,1,1)).getBlock(),num));
			r.addAll(fell(block.getLocation().add(new Vector(1,1,0)).getBlock(),num));
			r.addAll(fell(block.getLocation().add(new Vector(1,1,-1)).getBlock(),num));
			r.addAll(fell(block.getLocation().add(new Vector(-1,1,1)).getBlock(),num));
			r.addAll(fell(block.getLocation().add(new Vector(-1,1,0)).getBlock(),num));
			r.addAll(fell(block.getLocation().add(new Vector(-1,1,-1)).getBlock(),num));
			break;
		default:
			break;
		}
		return r;
	}
	/*
	 * ???????????????????????????
	 * ????????????????????????????????????
	 * ?????? ???????????????????????????
	 * ???????????????????????????
	 * @param tool ??????????????????
	 */
	public boolean damageTool(ItemStack tool)
	{	
		if(tool.getType().getMaxDurability()>0)
		{	
			if(!tool.getItemMeta().isUnbreakable())
			{	
				if(new Random().nextDouble()<=1./(tool.getItemMeta().getEnchantLevel(Enchantment.DURABILITY)+1))
				{	
					tool.setDurability((short) (tool.getDurability()+1));
				}
			}
			if(tool.getDurability()>tool.getType().getMaxDurability())
			{	
				tool.setAmount(tool.getAmount()-1);
				return true;
			}
			else
			{	
				return false;
			}
		}
		return false;
	}
	
	/*
	 * ??????????????????????????????????????????
	 * ?????????????????????????????????
	 * @param block ????????????
	 */
	public static void showBlockBreak(Block block)
	{	
		Object data;
		try
		{	
			data=ReflectionWrapper.invokeMethod(ReflectionWrapper.getMethodParent(block.getClass(),"getBlockData"),block);
		}
		catch(Throwable e)
		{	
			data=block.getState().getData();
		}
		block.getWorld().spawnParticle(Particle.BLOCK_CRACK,block.getLocation().add(0.5,0.5,0.5),20,0.5,0.5,0.5,data);
	}
	
	/*
	 * ?????????
	 * ??????????????????????????????
	 * @param block ??????
	 * @param player ??????
	 * @param tool ??????????????????
	 */
	@Deprecated
	public static void breakNaturally(Block block,Player player,ItemStack tool)
	{	
		BlockBreakEvent e=new BlockBreakEvent(block,player);
		Bukkit.getPluginManager().callEvent(e);
		if(!e.isCancelled())
		{	
			showBlockBreak(block);
			if(player!=null&&player.getGameMode()==GameMode.CREATIVE||!e.isDropItems())
			{	
				block.setType(Material.AIR);
			}
			else
			{	
				switch(block.getType())
				{	
				case BEDROCK:
				case BARRIER:
				case ENDER_PORTAL_FRAME:
					block.getWorld().dropItemNaturally(block.getLocation().add(0.5,0,0.5),new ItemStack(block.getType()));
					break;
				default:
					break;
				}
				block.breakNaturally(tool);
			}
		}
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler(priority = EventPriority.HIGHEST)
	void onBlockBreak(BlockBreakEvent event)
	{	
		if(event.isCancelled())
		{	
			return;
		}
		
		if(event.getPlayer().getItemInHand()!=null&&event.getPlayer().getItemInHand().getType()!=Material.AIR)
		{	
			if((!breakLocks.contains(event.getPlayer()))&&((short)event.getPlayer().getItemInHand().getItemMeta().getEnchantLevel(Enchantment.getByName("????????????")))>0)
			{	
				int range = event.getPlayer().getItemInHand().getItemMeta().getEnchantLevel(Enchantment.getByName("????????????"));
				breakLocks.add(event.getPlayer());
				for(int x=event.getBlock().getX()-range;x<event.getBlock().getX()+range+1;x++)
				{	
					for(int y=event.getBlock().getY()-range;y<event.getBlock().getY()+range+1;y++)
					{	
						for(int z=event.getBlock().getZ()-range;z<event.getBlock().getZ()+range+1;z++)
						{	
							if(x==event.getBlock().getX()&&y==event.getBlock().getY()&&z==event.getBlock().getZ())
							{	
								continue;
							}
							Block block=new Location(event.getBlock().getWorld(),x,y,z).getBlock();
							if(block.getType()==event.getBlock().getType())
							{	
								breakNaturally(block,event.getPlayer(),event.getPlayer().getItemInHand());
							}
						}
					}
				}
				breakLocks.remove(event.getPlayer());
			}
			if(MzTech.isItem(event.getPlayer().getItemInHand(),"?????????"))
			{	
				event.getPlayer().getItemInHand().setDurability((short) -1);
			}
			if(event.getPlayer()!=null&&((short)event.getPlayer().getItemInHand().getItemMeta().getEnchantLevel(Enchantment.getByName("????????????")))>0)
			{	
				Tools.fell(event.getBlock()).forEach((i)->
				{	
					if(i.getType()!=Material.AIR&&event.getPlayer().getGameMode()!=GameMode.CREATIVE)
					{	
						event.getBlock().getWorld().dropItem(event.getBlock().getLocation(),i);
					}
				});
				if(event.getBlock().isEmpty()&&event.getPlayer().getGameMode()!=GameMode.CREATIVE)
				{	
					damageTool(event.getPlayer().getItemInHand());
				}
			}
			Material mob_spawner;
			try
			{	
				mob_spawner=Material.MOB_SPAWNER;
			}
			catch(Throwable e)
			{	
				mob_spawner=Enum.valueOf(Material.class,"SPAWNER");
			}
			if(event.isDropItems()&&event.getBlock().getType()==mob_spawner&&event.getPlayer().getItemInHand().getItemMeta().getEnchantLevel(Enchantment.SILK_TOUCH)>=2)//???????????????
			{	
				event.setExpToDrop(0);
				ItemStack drop=new ItemStack(mob_spawner);
				if(event.getPlayer().getItemInHand().getItemMeta().getEnchantLevel(Enchantment.SILK_TOUCH)>=3)//??????????????????
				{	
					ItemMeta im = drop.getItemMeta();
					im.setLore(Lists.newArrayList("??7????????? "+((CreatureSpawner)event.getBlock().getState()).getSpawnedType().name()));
					drop.setItemMeta(im);
				}
				event.getBlock().getWorld().dropItemNaturally(event.getBlock().getLocation().add(0.5,0,0.5),drop);
			}
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	void onBlockPlace(BlockPlaceEvent event)
	{	
		if(event.isCancelled()||!event.canBuild())
		{	
			return;
		}
		Material mob_spawner;
		try
		{	
			mob_spawner=Material.MOB_SPAWNER;
		}
		catch(Throwable e)
		{	
			mob_spawner=Enum.valueOf(Material.class,"SPAWNER");
		}
		Material final_mob_spawner=mob_spawner;
		if(event.getItemInHand().getType()==mob_spawner&&event.getItemInHand().getItemMeta().hasLore())
		{	
			event.getItemInHand().getItemMeta().getLore().forEach(l->
			{	
				if(l.startsWith("??7????????? "))
				{	
					new BukkitRunnable()
					{	
						public void run()
						{	
							if(event.getBlock().getType()==final_mob_spawner)
							{	
								CreatureSpawner state = (CreatureSpawner) event.getBlock().getState();
								state.setSpawnedType(Enum.valueOf(EntityType.class,l.substring(6)));
								state.update();
							}
						}
					}.runTask(MzTech.instance);
				}
			});
		}
	}
}
