package mz.tech;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Hopper;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.FurnaceRecipe;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.Lever;
import org.bukkit.material.MaterialData;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.scheduler.BukkitRunnable;
import com.google.common.collect.Lists;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;

/**
 * 基础机器模块
 * 基础机器相关API
 */
public class BasicMachines implements Listener
{	
	static Map<Player,Inventory> playersOpenConversionTable=new LinkedHashMap<>();
	static Map<Player,Integer> playerOpenConversionPage=new LinkedHashMap<>();
    private static final Logger log = Logger.getLogger("Minecraft");
    public static Economy econ = null;
    public static Permission perms = null;
    public static Map<Player,List<ItemStack>> playersConversions=new HashMap<>();
    public static Map<ItemStack,Double> values=new HashMap<>();
    public static double clinkeringValue=0.02;

	public BasicMachines()
	{	
        if (!setupEconomy())
        {	
            log.severe(String.format("[%s] - 无法启动，请安装Vault插件",MzTech.instance.getDescription().getName()));
            Bukkit.getPluginManager().disablePlugin(MzTech.instance);
            return;
        }
        setupPermissions();
        
		ItemStack basicMachine;
		try
		{	
			basicMachine=new ItemStack(Material.PISTON_BASE);
		}
		catch(Throwable e)
		{	
			basicMachine=new ItemStack(Enum.valueOf(Material.class,"PISTON"));
		}
		ItemMeta im = basicMachine.getItemMeta();
		im.setLocalizedName("§6基础机器");
		basicMachine.setItemMeta(im);
		CraftGuide.addClassify("基础机器",basicMachine);
		
		Bukkit.getPluginManager().registerEvents(new SmilingCraftingTable(),MzTech.instance);
		
		ItemStack conversionTable=new ItemStack(Material.DAYLIGHT_DETECTOR);
		im=conversionTable.getItemMeta();
		im.setLocalizedName("§1转化桌");
		im.setLore(Lists.newArrayList("§7§m系统商店"));
		conversionTable.setItemMeta(im);
		MzTech.items.put("转化桌",conversionTable);
		Raw obsidian=new Raw(null,new ItemStack(Material.OBSIDIAN));
		Raw redstone=new Raw(null,new ItemStack(Material.REDSTONE));
		Raw jukebox;
		try
		{	
			jukebox=new Raw(null,new ItemStack(Material.WORKBENCH));
		}
		catch(Error e)
		{	
			jukebox=new Raw(null,new ItemStack(Enum.valueOf(Material.class,"CRAFTING_TABLE")));
		}
		SmilingCraftingTable.add("转化桌",conversionTable,null,obsidian,redstone,obsidian,redstone,jukebox,redstone,obsidian,redstone,obsidian);
		CraftGuide.addCraftTable("基础机器",SmilingCraftingTable.class,"转化桌");
        
        Bukkit.getPluginManager().registerEvents(new ChemicalFurnace(),MzTech.instance);
        
        ItemStack metronome=new ItemStack(Material.LEVER);
        im=metronome.getItemMeta();
        im.setLocalizedName("节拍器");
        im.setLore(Lists.newArrayList("§7脉冲"));
        metronome.setItemMeta(im);
        MzTech.items.put("节拍器",metronome);
        Raw stone=new Raw(null,new ItemStack(Material.STONE));
        Raw redstoneTorch;
        try
        {	
        	redstoneTorch=new Raw(null,new ItemStack(Material.REDSTONE_TORCH_ON));
        }
        catch(Error e)
        {	
        	redstoneTorch=new Raw(null,new ItemStack(Enum.valueOf(Material.class,"REDSTONE_TORCH")));
        }
        Raw air=new Raw(null,new ItemStack(Material.AIR));
        SmilingCraftingTable.add("节拍器",metronome,null,stone,redstone,stone,redstoneTorch,stone,redstoneTorch,air,air,air);
        CraftGuide.addCraftTable("基础机器",SmilingCraftingTable.class,"节拍器");
        
        ItemStack railDuplicator=new ItemStack(Material.HOPPER);
        im=railDuplicator.getItemMeta();
        im.setLocalizedName("铁轨复制机");
        im.setLore(Lists.newArrayList("§7复制上方的铁轨","§7原版特性"));
        railDuplicator.setItemMeta(im);
        MzTech.items.put("铁轨复制机",railDuplicator);
        Raw slime=new Raw(null,new ItemStack(Material.SLIME_BLOCK));
        Raw slime2=new Raw(null,new ItemStack(Material.SLIME_BLOCK,2));
        Raw stickyPiston;
        try
        {	
        	stickyPiston=new Raw(null,new ItemStack(Material.PISTON_STICKY_BASE));
        }
        catch(Error e)
        {	
        	stickyPiston=new Raw(null,new ItemStack(Enum.valueOf(Material.class,"STICKY_PISTON")));
        }
        Raw hopper=new Raw(null,new ItemStack(Material.HOPPER));
        Raw metronomeRaw=new Raw(null,metronome);
        SmilingCraftingTable.add("铁轨复制机",railDuplicator,null,air,slime,stickyPiston,slime2,slime,metronomeRaw,air,hopper,air);
        CraftGuide.addCraftTable("基础机器",SmilingCraftingTable.class,"铁轨复制机");
        
        ItemStack trash=new ItemStack(Material.HOPPER);
        im=trash.getItemMeta();
        im.setLocalizedName("§6垃圾桶");
        im.setLore(Lists.newArrayList("§7销毁一切"));
        trash.setItemMeta(im);
        MzTech.items.put("垃圾桶",trash);
        Raw ironHelmet=new Raw(null,new ItemStack(Material.IRON_HELMET));
        Raw lavaBucket=new Raw(null,new ItemStack(Material.LAVA_BUCKET));
        SmilingCraftingTable.add("垃圾桶",trash,null,ironHelmet,air,air,lavaBucket,air,air,air,air,air);
        CraftGuide.addCraftTable("基础机器",SmilingCraftingTable.class,"垃圾桶");
        
        ItemStack griddle;
        try
        {	
        	griddle=new ItemStack(Material.WEB);
        }
        catch(Error e)
        {	
        	griddle=new ItemStack(Enum.valueOf(Material.class,"COBWEB"));
        }
        im=griddle.getItemMeta();
        im.setLocalizedName("筛子");
        griddle.setItemMeta(im);
        MzTech.items.put("筛子",griddle);
        List<ItemStack> itemPlanks=new ArrayList<>();
        try
        {	
        	itemPlanks.add(new ItemStack(Material.WOOD,1,(short)0));
        	itemPlanks.add(new ItemStack(Material.WOOD,1,(short)1));
        	itemPlanks.add(new ItemStack(Material.WOOD,1,(short)2));
        	itemPlanks.add(new ItemStack(Material.WOOD,1,(short)3));
        	itemPlanks.add(new ItemStack(Material.WOOD,1,(short)4));
        	itemPlanks.add(new ItemStack(Material.WOOD,1,(short)5));
        }
        catch(Throwable e)
        {	
        	itemPlanks.add(new ItemStack(Enum.valueOf(Material.class,"ACACIA_PLANKS")));
        	itemPlanks.add(new ItemStack(Enum.valueOf(Material.class,"BIRCH_PLANKS")));
        	itemPlanks.add(new ItemStack(Enum.valueOf(Material.class,"DARK_OAK_PLANKS")));
        	itemPlanks.add(new ItemStack(Enum.valueOf(Material.class,"JUNGLE_PLANKS")));
        	itemPlanks.add(new ItemStack(Enum.valueOf(Material.class,"OAK_PLANKS")));
        	itemPlanks.add(new ItemStack(Enum.valueOf(Material.class,"SPRUCE_PLANKS")));
        	try
        	{	
            	itemPlanks.add(new ItemStack(Enum.valueOf(Material.class,"CRIMSON_PLANKS")));
            	itemPlanks.add(new ItemStack(Enum.valueOf(Material.class,"WARPED_PLANKS")));
        	}
            catch(Throwable e2)
        	{	
        	}
        }
        Raw planks=new Raw(null,itemPlanks.parallelStream().toArray(ItemStack[]::new));
        Raw web;
        try
        {	
        	web=new Raw(null,new ItemStack(Material.WEB));
        }
        catch(Throwable e)
        {	
        	web=new Raw(null,new ItemStack(Enum.valueOf(Material.class,"COBWEB")));
        }
        Raw stick=new Raw(null,new ItemStack(Material.STICK));
        SmilingCraftingTable.add("筛子",griddle,null,
        		planks,air,planks,
        		planks,web,planks,
        		stick,air,stick);
        CraftGuide.addCraftTable("基础机器",SmilingCraftingTable.class,"筛子");
        Bukkit.getPluginManager().registerEvents(new ScreenRecipes(),MzTech.instance);
        new BukkitRunnable()
        {	
        	public void run()
        	{	
        		MzTech.forEachMachine("筛子",(b,i)->
        		{	
    				b.getWorld().getEntitiesByClass(FallingBlock.class).forEach(e->
        			{	
        				if(e.getLocation().getBlock().equals(b))
        				{	
        					NBT.setEntityNBT(e,"{Time:1}",true);
        				}
        			});
        		});
        	}
        }.runTaskTimer(MzTech.instance,0,300);
        
        new BukkitRunnable()
        {	
        	public void run()
        	{	
				MzTech.forEachMachine("垃圾桶",(block,inv)->
        		{	
        			BlockState state = block.getState();
        			if(state instanceof Hopper)
        			{	
        				((Hopper)state).getInventory().clear();
        			}
        		});
        	}
        }.runTaskTimer(MzTech.instance,100,100);
        
        new BukkitRunnable()
        {	
        	boolean lock=false;
        	public void run()
        	{	
        		new BukkitRunnable()
        		{	
        			public void run()
                	{	
                		new BukkitRunnable()
                		{	
                			public void run()
                        	{	
		        				MzTech.forEachMachine("节拍器",(block,inv)->
				        		{	
				        			if(block.getType()==Material.LEVER)
				        			{	
				        				try
				        				{	
				        					Object blockData=ReflectionWrapper.invokeMethod(ReflectionWrapper.getMethod(Block.class,"getBlockData"),block);
				        					ReflectionWrapper.invokeMethod(ReflectionWrapper.getMethod(Class.forName("org.bukkit.block.data.Powerable")," setPowered",boolean.class),blockData,
				        					!(boolean)ReflectionWrapper.invokeMethod(ReflectionWrapper.getMethod(Class.forName("org.bukkit.block.data.Powerable")," isPowered"),blockData));
				        					ReflectionWrapper.invokeMethod(ReflectionWrapper.getMethod(Block.class,"setBlockData",Class.forName("org.bukkit.block.data.BlockData"),boolean.class),block,blockData,true);
				        				}
				        				catch(Throwable e)
				        				{	
				        					e.printStackTrace();
					        				BlockState state = block.getState();
					        				Lever data = (Lever) state.getData();
					        				data.setPowered(!data.isPowered());
					        				state.setData(data);
					        				state.update();
				        				}
		                        	}
				        		});
                        	}
	                	}.runTask(MzTech.instance);
        				if(!lock)
        				{	
        					lock=true;
			        		MzTech.forEachMachine("铁轨复制机",(block,inv)->
			        		{	
			        			Block rail=block.getLocation().add(0,1,0).getBlock();
			        			switch(rail.getType())
			        			{	
			        			case RAILS:
			        			case ACTIVATOR_RAIL:
			        			case DETECTOR_RAIL:
			        			case POWERED_RAIL:
			        				((org.bukkit.block.Hopper)block.getState()).getInventory().addItem(new ItemStack(rail.getType()));
			        				break;
								default:
									break;
			        			}
			        			block.getLocation().getWorld().playSound(block.getLocation(),Sound.BLOCK_PISTON_EXTEND,1,1);
				        		try
				        		{	
									Thread.sleep(50);
								}
				        		catch (InterruptedException e)
				        		{	
									e.printStackTrace();
								}
			        		});
				        	lock=false;
			        	}
                	}
        		}.runTaskAsynchronously(MzTech.instance);
        	}
        }.runTaskTimer(MzTech.instance,20,20);
        
        new BukkitRunnable()
        {	
        	public void run()
        	{	
        		//save
        		File dir=new File("plugins/MzTech/Conversions");
        		if(!dir.exists())
        		{	
        			dir.mkdirs();
        		}
        		List<Player> deleteList=new ArrayList<>();
        		playersConversions.forEach((p,iss)->
        		{	
        			File f=new File("plugins/MzTech/Conversions/"+p.getUniqueId().toString()+".pc");
        			if(f.exists())
        			{	
        				f.delete();
        			}
        			try
        			{	
        				f.createNewFile();
        				DataOutputStream dos=new DataOutputStream(new FileOutputStream(f));
        				dos.writeInt(iss.size());
        				iss.forEach(is->
        				{	
        					try
        					{	
        						dos.writeInt(is.getType().ordinal());
        						dos.writeShort(is.getDurability());
        					}
        					catch (Exception e)
        					{	
        						e.printStackTrace();
        					}
        				});
        				dos.close();
        			}
        			catch (Exception e)
        			{	
        				e.printStackTrace();
        			}
        			if(!playersOpenConversionTable.containsKey(p))
        			{	
        				deleteList.add(p);
        			}
        		});
        		deleteList.forEach(p->
        		{	
        			playersConversions.remove(p);
        		});
        	}
        }.runTaskTimer(MzTech.instance,1200,1200);
    }
	public static Double getItemValue(ItemStack is)
	{	
    	return getItemValue(is,5);
	}
    @SuppressWarnings({ "unchecked" })
	public static Double getItemValue(ItemStack is,int restrict)
    {	
    	if(restrict<0)
    	{	
    		return null;
    	}
    	ItemStack i=getPureItem(is);
    	if(i==null) {return null;}
    	Double[] rd= {null};
    	if(values.containsKey(i))
    	{	
    		rd[0]=values.get(getPureItem(i))*is.getAmount();
    	}
    	else
    	{	
    		((Map<String,Object>)MzTech.getConfigs().get("values")).forEach((s,v)->//取配置
	    	{	
	    		String[] ss=s.split(":");
	    		Material t=Material.getMaterial(ss[0].toUpperCase());
	    		if(t!=null)
	    		{	
		    		ItemStack conS=new ItemStack(t);
		    		if(ss.length>1)
		    		{	
		    			conS.setDurability(Short.valueOf(ss[1]));
		    		}
		    		if(v instanceof Double)
		    		{	
			    		if(conS.isSimilar(i)&&(rd[0]==null||((Double)v)*is.getAmount()<rd[0]))
			    		{	
			    			rd[0]=((Double)v)*(double)is.getAmount();
			    		}
		    		}
		    		else if(v instanceof Integer)
		    		{	
			    		if(conS.isSimilar(i)&&(rd[0]==null||((Integer)v)*is.getAmount()<rd[0]))
			    		{	
			    			rd[0]=((Integer)v)*(double)is.getAmount();
			    		}
		    		}
	    		}
	    	});
    		if(rd[0]==null)
    		{	
		    	Bukkit.getRecipesFor(i).forEach(r->
		    	{	
		    		if(FurnaceRecipe.class.isAssignableFrom(r.getClass()))
		    		{	
		    			Double v=getItemValue(getPureItem(((FurnaceRecipe)r).getInput()),restrict-1);
		    			if(v!=null&&(rd[0]==null||v/((FurnaceRecipe)r).getInput().getAmount()*is.getAmount()+clinkeringValue<rd[0]))
		    			{	
		    				rd[0]=v/((FurnaceRecipe)r).getInput().getAmount()*is.getAmount()+clinkeringValue;
		    			}
		    		}
		    		else if(ShapedRecipe.class.isAssignableFrom(r.getClass()))
		    		{	
		    			Double[] v= {0.};
		    			Lists.newArrayList(((ShapedRecipe)r).getShape()).forEach(a->
		    			{	
		    				for(Character a1:a.toCharArray())
		    				{	
		    					ItemStack tis=((ShapedRecipe)r).getIngredientMap().get(a1);
		    					if(tis!=null)
		    					{	
			    					Double va=getItemValue(tis,restrict-1);
			    					if(va!=null)
			    					{	
			    						if(v[0]!=null)
			    						{	
			    							v[0]+=va/((ShapedRecipe)r).getResult().getAmount();
			    						}
			    					}
			    					else
			    					{	
			    						v[0]=null;
			    					}
		    					}
		    				}
		    			});
		    			if(v[0]!=null&&v[0]!=0.&&(rd[0]==null||v[0]*is.getAmount()<rd[0]))
		    			{	
		    				rd[0]=v[0]*is.getAmount();
		    			}
		    		}
		    		else if(ShapelessRecipe.class.isAssignableFrom(r.getClass()))
		    		{	
		    			Double[] v= {0.};
		    			((ShapelessRecipe)r).getIngredientList().forEach(a->
		    			{	
		    				if(a!=null)
		    				{	
			    				Double va=getItemValue(a,restrict-1);
			    				if(va!=null)
			    				{	
			    					if(v[0]!=null)
			    					{	
			    						v[0]+=va/((ShapelessRecipe)r).getResult().getAmount();
			    					}
			    				}
			    				else
			    				{	
			    					v[0]=null;
			    				}
		    				}
		    			});
		    			if(v[0]!=null&&v[0]!=0.&&(rd[0]==null||v[0]*is.getAmount()<rd[0]))
		    			{	
		    				rd[0]=v[0]*is.getAmount();
		    			}
		    		}
		    	});
    		}
    		if(rd[0]!=null)values.put(getPureItem(i),rd[0]/is.getAmount());
    	}
    	if(rd[0]!=null&&is.getType().getMaxDurability()>0)
    	{	
    		rd[0]*=is.getType().getMaxDurability()-is.getDurability();
    		rd[0]/=is.getType().getMaxDurability();
    	}
    	return rd[0];
    }
	@SuppressWarnings("deprecation")
	@EventHandler
	void onPlaceMachine(PlaceMachineEvent event)
	{	
		if(event.isCancelled())
		{	
			return;
		}
		switch(event.machine)
		{	
		case "筛子":
			try
			{	
				event.block.setType(Material.STEP);
				event.block.setData((byte) 1);
			}
			catch(Throwable e)
			{	
				event.block.setType(Enum.valueOf(Material.class,"SANDSTONE_SLAB"));
			}
			FallingBlock block;
			try
			{	
				block=event.block.getWorld().spawnFallingBlock(event.block.getLocation().add(0.5,0,0.5),new MaterialData(Material.WEB));
			}
			catch(Throwable e)
			{	
				block=event.block.getWorld().spawnFallingBlock(event.block.getLocation().add(0.5,0,0.5),new MaterialData(Enum.valueOf(Material.class,"COBWEB")));
			}
			block.setGravity(false);
			block.setInvulnerable(true);
			block.setDropItem(false);
			break;
		case "转化桌":
			event.setCancelled(true);
			break;
		}
	}
	@EventHandler(priority = EventPriority.MONITOR)
	void onMachineMove(MachineMoveEvent event)
	{	
		if(event.isCancelled())
		{	
			return;
		}
		switch(event.machine)
		{	
		case "筛子":
			event.block.getWorld().getEntitiesByClass(FallingBlock.class).forEach(e->
			{	
				if(e.getLocation().getBlock().equals(event.block))
				{	
					new BukkitRunnable()
					{	
						public void run()
						{	
							e.teleport(event.targetBlock.getLocation().add(0.5,0,0.5));
						}
					}.runTaskLater(MzTech.instance,2);
				}
			});
			break;
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	void onBreakMachine(BreakMachineEvent event)
	{	
		if(event.isCancelled())
		{	
			return;
		}
		switch(event.machine)
		{	
		case "筛子":
			event.block.getWorld().getEntitiesByClass(FallingBlock.class).forEach(e->
			{	
				if(e.getLocation().getBlock().equals(event.block))
				{	
					e.remove();
				}
			});
			break;
		}
	}
	@EventHandler
	void onClickItem(ClickItemEvent event)
	{	
		switch(event.item)
		{	
		case "转化桌":
			if(!event.leftClick)
			{	
				Inventory inv=Bukkit.createInventory(null,54,"§1转化桌");
				ItemStack glassPane;
				try
				{	
					glassPane=new ItemStack(Material.STAINED_GLASS_PANE,1,(short)3);
				}
				catch(Throwable e)
				{	
					glassPane=new ItemStack(Enum.valueOf(Material.class,"LIGHT_BLUE_STAINED_GLASS_PANE"));
				}
				for(int i=0;i<9;i++)
				{	
					inv.setItem(45+i,glassPane);
				}
				ItemStack page;
				try
				{	
					page=new ItemStack(Material.STAINED_GLASS_PANE,1,(short)5);
				}
				catch(Throwable e)
				{	
					page=new ItemStack(Enum.valueOf(Material.class,"LIME_STAINED_GLASS_PANE"));
				}
				ItemMeta im = page.getItemMeta();
				im.setLocalizedName("§a上一页");
				page.setItemMeta(im);
				inv.setItem(47,page);
				im.setLocalizedName("§a下一页");
				page.setItemMeta(im);
				inv.setItem(51,page);
				event.player.getWorld().playSound(event.player.getLocation(),Sound.BLOCK_CHEST_OPEN,1,1);
				if(!playersConversions.containsKey(event.player))
				{	
					playersConversions.put(event.player,new ArrayList<>());
					File f=new File("plugins/MzTech/Conversions/"+event.player.getUniqueId().toString()+".pc");
					if(f.exists())
					{	
						try
						{	
							DataInputStream dis=new DataInputStream(new FileInputStream(f));
							int size=dis.readInt();
							for(int i=0;i<size;i++)
							{	
								playersConversions.get(event.player).add(new ItemStack(Material.values()[dis.readInt()],1,dis.readShort()));
							}
							dis.close();
						}
						catch (Exception e)
						{	
							e.printStackTrace();
						}
					}
				}
				event.player.openInventory(inv);
				playersOpenConversionTable.put(event.player,inv);
				playerOpenConversionPage.put(event.player,0);
				setConversions(event.player);
				setMoney(event.player);
			}
			break;
		}
	}
	public static void setConversions(Player player)
	{	
		List<ItemStack> iss=playersConversions.get(player);
		if(iss!=null)
		{	
			for(int i=0;i<45;i++)
			{	
				if(playerOpenConversionPage.containsKey(player)&&i+playerOpenConversionPage.get(player)*45<iss.size())
				{	
					ItemStack is=new ItemStack(iss.get(iss.size()-1-i-playerOpenConversionPage.get(player)*45));
					ItemMeta im = is.getItemMeta();
					if(getItemValue(getPureItem(is))!=null)
						im.setLore(Lists.newArrayList("§e价值： "+new BigDecimal(getItemValue(getPureItem(is))).setScale(5,RoundingMode.HALF_UP).doubleValue()));
					is.setItemMeta(im);
					player.getOpenInventory().setItem(i,is);
				}
				else
				{	
					player.getOpenInventory().setItem(i,new ItemStack(Material.AIR));
				}
			}
		}
	}
	public static void setMoney(Player player)
	{	
		ItemStack money;
		try
		{	
			money=new ItemStack(Material.DOUBLE_PLANT,1,(short)0);
		}
		catch(Throwable e)
		{	
			money=new ItemStack(Enum.valueOf(Material.class,"SUNFLOWER"));
		}
		ItemMeta im = money.getItemMeta();
		im.setDisplayName("§e通用货币： "+new BigDecimal(econ.getBalance(player)).setScale(2,BigDecimal.ROUND_DOWN).doubleValue());
		im.setLore(Lists.newArrayList("§7点击背包物品学习并转化"));
		money.setItemMeta(im);
		player.getOpenInventory().setItem(49,money);
	}
	public static boolean isPureItem(ItemStack is)
	{	
		return is!=null?is.isSimilar(new ItemStack(is.getType(),1,is.getDurability())):false;
	}
	public static ItemStack getPureItem(ItemStack is)
	{	
		if(is==null)
		{	
			return null;
		}
		ItemStack pure=new ItemStack(is.getType(),1,is.getDurability());
		if(pure.getDurability()==32767)
		{	
			pure.setDurability((short) 0);
		}
		if(pure.getType().getMaxDurability()>0)
		{	
			pure.setDurability((short)0);
		}
		return pure;
	}
	@EventHandler
	void onInventoryClick(InventoryClickEvent event)
	{	
		if(event.getInventory().getViewers().isEmpty())
		{	
			event.setCancelled(true);
		}
		else if(playersOpenConversionTable.containsKey(event.getWhoClicked()))
		{	
			event.setCancelled(true);
			if(event.getClickedInventory()==event.getWhoClicked().getInventory())
			{	
				if(BasicMachines.isPureItem(event.getClickedInventory().getItem(event.getSlot())))
				{	
					Double value=BasicMachines.getItemValue(event.getClickedInventory().getItem(event.getSlot()));
					if(value!=null)
					{	
				    	if(playersConversions.get(event.getWhoClicked()).contains(getPureItem(event.getClickedInventory().getItem(event.getSlot()))))
				    	{	
				    		try
				    		{	
				    			event.getWhoClicked().getWorld().playSound(event.getWhoClicked().getLocation(),Sound.BLOCK_NOTE_HAT,1,1);
				    		}
				    		catch(Throwable e)
				    		{	
				    			event.getWhoClicked().getWorld().playSound(event.getWhoClicked().getLocation(),Enum.valueOf(Sound.class,"BLOCK_NOTE_BLOCK_HAT"),1,1);
				    		}
							playersConversions.get(event.getWhoClicked()).remove(getPureItem(event.getClickedInventory().getItem(event.getSlot())));
				    	}
				    	else
				    	{	
				    		try
				    		{	
				    			event.getWhoClicked().getWorld().playSound(event.getWhoClicked().getLocation(),Sound.BLOCK_NOTE_BELL,1,1);
				    		}
				    		catch(Throwable e)
				    		{	
				    			event.getWhoClicked().getWorld().playSound(event.getWhoClicked().getLocation(),Enum.valueOf(Sound.class,"BLOCK_NOTE_BLOCK_BELL"),1,1);
				    		}
				    	}
				    	playersConversions.get(event.getWhoClicked()).add(getPureItem(event.getClickedInventory().getItem(event.getSlot())));
				    	setConversions((Player) event.getWhoClicked());
						BasicMachines.econ.depositPlayer((OfflinePlayer) event.getWhoClicked(),value);
						event.getCurrentItem().setAmount(0);
						setMoney((Player) event.getWhoClicked());
					}
				}
			}
			else if(event.getClickedInventory()==event.getInventory()&&event.getInventory().getItem(event.getSlot())!=null&&event.getInventory().getItem(event.getSlot()).getType()!=Material.AIR)
			{	
				if(event.getSlot()<45)
				{	
					Double value=BasicMachines.getItemValue(getPureItem(event.getInventory().getItem(event.getSlot())))*((event.isShiftClick())?event.getInventory().getItem(event.getSlot()).getType().getMaxStackSize():1);
					if(value!=null)
					{	
						if(BasicMachines.econ.getBalance((OfflinePlayer) event.getWhoClicked())>=value)
						{	
							try
							{	
								event.getWhoClicked().getWorld().playSound(event.getWhoClicked().getLocation(),Sound.BLOCK_NOTE_BELL,1,1);
							}
							catch(Throwable e)
							{	
								event.getWhoClicked().getWorld().playSound(event.getWhoClicked().getLocation(),Enum.valueOf(Sound.class,"BLOCK_NOTE_BLOCK_BELL"),1,1);
							}
							BasicMachines.econ.withdrawPlayer((OfflinePlayer) event.getWhoClicked(),value);
							ItemStack is=getPureItem(event.getInventory().getItem(event.getSlot()));
							is.setAmount((event.isShiftClick())?event.getInventory().getItem(event.getSlot()).getType().getMaxStackSize():1);
							MzTech.giveInHand((Player) event.getWhoClicked(),is);
							setMoney((Player) event.getWhoClicked());
						}
						else
						{	
				    		try
				    		{	
				    			event.getWhoClicked().getWorld().playSound(event.getWhoClicked().getLocation(),Sound.BLOCK_NOTE_XYLOPHONE,1,1);
				    		}
				    		catch(Throwable e)
				    		{	
				    			event.getWhoClicked().getWorld().playSound(event.getWhoClicked().getLocation(),Enum.valueOf(Sound.class,"BLOCK_NOTE_BLOCK_XYLOPHONE"),1,1);
				    		}
							event.getWhoClicked().sendMessage(MzTech.MzTechPrefix+"§4金币不足");
						}
					}
				}
				else
				{	
					if(event.getClickedInventory().getItem(event.getSlot()).getItemMeta().hasLocalizedName())
					{	
						switch(event.getClickedInventory().getItem(event.getSlot()).getItemMeta().getLocalizedName())
						{	
						case "§a上一页":
							if(playerOpenConversionPage.get(event.getWhoClicked())>0)
							{	
								event.getWhoClicked().getWorld().playSound(event.getWhoClicked().getLocation(),Sound.BLOCK_CHEST_OPEN,1,1);
								playerOpenConversionPage.put((Player)event.getWhoClicked(),playerOpenConversionPage.get(event.getWhoClicked())-1);
								setConversions((Player) event.getWhoClicked());
							}
							break;
						case "§a下一页":
							if(playerOpenConversionPage.get(event.getWhoClicked())*45+45<playersConversions.get(event.getWhoClicked()).size())
							{	
								event.getWhoClicked().getWorld().playSound(event.getWhoClicked().getLocation(),Sound.BLOCK_CHEST_OPEN,1,1);
								playerOpenConversionPage.put((Player)event.getWhoClicked(),playerOpenConversionPage.get(event.getWhoClicked())+1);
								setConversions((Player) event.getWhoClicked());
							}
							break;
						}
					}
				}
			}
		}
	}
	@EventHandler
	void onInventoryClose(InventoryCloseEvent event)
	{	
		if(playersOpenConversionTable.containsKey(event.getPlayer()))
		{	
			playersOpenConversionTable.remove(event.getPlayer());
			playerOpenConversionPage.remove(event.getPlayer());
			event.getPlayer().getWorld().playSound(event.getPlayer().getLocation(),Sound.BLOCK_CHEST_CLOSE,1,1);
		}
	}
	public static void onDisable()
	{	
		playersOpenConversionTable.forEach((p,i)->
		{	
			p.closeInventory();
		});
		//save
		File dir=new File("plugins/MzTech/Conversions");
		if(!dir.exists())
		{	
			dir.mkdirs();
		}
		playersConversions.forEach((p,iss)->
		{	
			File f=new File("plugins/MzTech/Conversions/"+p.getUniqueId().toString()+".pc");
			if(f.exists())
			{	
				f.delete();
			}
			try
			{	
				f.createNewFile();
				DataOutputStream dos=new DataOutputStream(new FileOutputStream(f));
				dos.writeInt(iss.size());
				iss.forEach(is->
				{	
					try
					{	
						dos.writeInt(is.getType().ordinal());
						dos.writeShort(is.getDurability());
					}
					catch (Exception e)
					{	
						e.printStackTrace();
					}
				});
				dos.close();
			}
			catch (Exception e)
			{	
				e.printStackTrace();
			}
		});
	}
	
    private boolean setupEconomy() {
        if (Bukkit.getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = Bukkit.getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return econ != null;
    }
    
    private boolean setupPermissions() {
        RegisteredServiceProvider<Permission> rsp = Bukkit.getServicesManager().getRegistration(Permission.class);
        perms = rsp.getProvider();
        return perms != null;
    }
	public static void reload(Map<String, Object> map)
	{	
		Object value=map.get("clinkeringValue");
		if(value instanceof Integer)
		{	
			clinkeringValue=(double)(Integer)value;
		}
		else if(value instanceof Double)
		{	
			clinkeringValue=(Double)value;
		}
	}
}
