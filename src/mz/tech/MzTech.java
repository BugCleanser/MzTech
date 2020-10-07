package mz.tech;

import static mz.tech.ReflectionWrapper.getCraftBukkitClass;
import static mz.tech.ReflectionWrapper.getField;
import static mz.tech.ReflectionWrapper.getFieldValue;
import static mz.tech.ReflectionWrapper.getMethod;
import static mz.tech.ReflectionWrapper.getNMSClass;
import static mz.tech.ReflectionWrapper.getStaticFIeldValue;
import static mz.tech.ReflectionWrapper.invokeMethod;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.BiConsumer;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Nameable;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.PistonMoveReaction;
import org.bukkit.block.Sign;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockFadeEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.LeavesDecayEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.player.PlayerEditBookEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.yaml.snakeyaml.Yaml;

import com.comphenix.protocol.utility.StreamSerializer;
import com.google.common.base.Objects;
import com.google.common.collect.Lists;
import com.google.gson.JsonParser;

import org.bukkit.Chunk;
import org.bukkit.GameMode;

class Machine
{	
	public String type;
	public Location place;
	public Inventory inv;
	
	public Machine(String type, Location place,Inventory inv)
	{	
		this.type=type;
		this.place=place;
		this.inv=inv;
	}
}

/**
 * 插件主类及常用API
 */
public class MzTech extends JavaPlugin implements Listener , CommandExecutor , TabCompleter
{	
    public static MzTech instance;
    {	
        instance = this;
    }
    public static Charset GBK=Charset.forName("GBK");
    
	public static List<MzTechCommand> commands=Lists.newArrayList
    (	
		new MzTechCommand("reload",true,0)
		{	
			@Override
			public String usage()
			{	
				return "reload";
			}
			
			@Override
			public boolean execute(CommandSender sender, String[] args)
			{	
				if(args.length==0)
				{	
					MzTech.reload();
					sender.sendMessage(MzTechPrefix+"§a部分已重载配置");
					sender.sendMessage(MzTechPrefix+"§e请使用插件管理器重载整个插件");
					return true;
				}
				else
				{	
					return false;
				}
			}
		},
		new MzTechCommand("guide",false,0)
		{	
			@Override
			public String usage()
			{	
				return "guide";
			}
			
			@Override
			public boolean execute(CommandSender sender, String[] args)
			{	
				if(args.length==0)
				{	
					if(Player.class.isAssignableFrom(sender.getClass()))
					{	
						((Player)sender).getInventory().addItem(items.get("合成指南"));
						sender.sendMessage(MzTechPrefix+"§a您得到了一个合成指南");
					}
					else
					{	
						sender.sendMessage(MzTechPrefix+"§4如何给予控制台一个物品？");
					}
					return true;
				}
				else
				{	
					return false;
				}
			}
		},
		new MzTechCommand("give",true,2)
		{	
			@Override
			public String usage()
			{	
				return "give <玩家> <物品>";
			}
			
			@Override
			public List<String> onTabComplite(CommandSender sender,String[] args)
			{	
				ArrayList<String> ret = new ArrayList<>();
				switch(args.length)
				{	
				case 1:
					Bukkit.getOnlinePlayers().forEach((p)->
					{	
						if(p.getName().toLowerCase().startsWith(args[0]))ret.add(p.getName());
					});
					break;
				case 2:
					MzTech.items.forEach((s,i)->
					{	
						if(s.toLowerCase().startsWith(args[1]))ret.add(s);
					});
					break;
				}
				return ret;
			}
			
			@Override
			public boolean execute(CommandSender sender, String[] args)
			{	
				Player player=Bukkit.getPlayer(args[0]);
				if(player!=null)
				{	
					ItemStack item=items.get(args[1]);
					if(item==null)
					{	
						return false;
					}
					item=new ItemStack(item);
					player.getInventory().addItem(item);
					sender.sendMessage(MzTechPrefix+"§a给予了"+player.getName()+" "+"1个"+item.getItemMeta().getLocalizedName());
				}
				else
				{	
					sender.sendMessage(MzTechPrefix+"§4该玩家不在线");
				}
				return true;
			}
		},
		new MzTechCommand("machines",true,0)
		{	
			@Override
			public String usage()
			{	
				return "machines";
			}
			
			@Override
			public boolean execute(CommandSender sender, String[] args)
			{	
				if(args.length==0)
				{	
					sender.sendMessage(MzTechPrefix+"§e已加载的机器：");
					MzTech.machines.forEach((c,a)->
					{	
						a.forEach(m->
						{	
							sender.sendMessage(m.type+": "+m.place.getWorld().getName()+"——"+m.place.getBlockX()+","+m.place.getBlockY()+","+m.place.getBlockZ());
						});
					});
					return true;
				}
				else
				{	
					return false;
				}
			}
		},
		new MzTechCommand("debugstick",true,0)
		{	
			@Override
			public String usage()
			{	
				return "debugstick";
			}
			
			@Override
			public boolean execute(CommandSender sender, String[] args)
			{
				if(args.length==0)
				{	
					if(sender instanceof Player)
					{	
						giveInHand((Player)sender,items.getOrDefault("调试棒",new ItemStack(Material.AIR)));
						sender.sendMessage(MzTechPrefix+"§a你得到了一个调试棒");
					}
					else
					{	
						sender.sendMessage(MzTechPrefix+"§4游戏中的玩家才能使用该命令");
					}
					return true;
				}
				else
				{	
					return false;
				}
			}
		},
		new MzTechCommand("iteminfo",true,0)
		{	
			@Override
			public String usage()
			{	
				return "iteminfo";
			}
			
			@SuppressWarnings("deprecation")
			@Override
			public boolean execute(CommandSender sender, String[] args)
			{	
				if(args.length==0)
				{	
					if(sender instanceof Player)
					{	
						if(((Player)sender).getItemInHand().getType()!=Material.AIR)
						{	
							sender.sendMessage(MzTechPrefix+"§a物品MID： "+((Player)sender).getItemInHand().getType().name());
							sender.sendMessage(MzTechPrefix+"§a翻译前名称： "+DropsName.getItemUnlocalizedName(((Player)sender).getItemInHand()));
							sender.sendMessage(MzTechPrefix+"§a掉落物名称： "+DropsName.getDropName(((Player)sender).getItemInHand()).replace("§","&"));
							try
							{	
								sender.sendMessage(MzTechPrefix+"§a物品数字ID： "+((Player)sender).getItemInHand().getTypeId());
							}
							catch(Throwable e)
							{	
							}
							sender.sendMessage(MzTechPrefix+(((Player)sender).getItemInHand().getType().getMaxDurability()>0?"§a物品损坏度： ":"§a物品子ID： ")+((Player)sender).getItemInHand().getDurability());
							sender.sendMessage(MzTechPrefix+"§a原版物品： "+(BasicMachines.isPureItem(((Player)sender).getItemInHand())?"是":"否"));
							sender.sendMessage(MzTechPrefix+"§aNBT： "+new NBT(((Player)sender).getItemInHand()));
							sender.sendMessage(MzTechPrefix+"§a价值： "+BasicMachines.getItemValue(((Player)sender).getItemInHand()));
						}
						else
						{	
							sender.sendMessage(MzTechPrefix+"§4主手中没有物品");
						}
					}
					else
					{	
						sender.sendMessage(MzTechPrefix+"§4游戏中的玩家才能使用该命令");
					}
					return true;
				}
				else
				{	
					return false;
				}
			}
		},
		new MzTechCommand("enchant",true,1)
		{	
			@Override
			public String usage()
			{	
				return "enchant <附魔>";
			}
			
			@Override
			public List<String> onTabComplite(CommandSender sender,String[] args)
			{	
				List<String> ret=new ArrayList<>();
				switch(args.length)
				{	
				case 1:
					Lists.newArrayList(Enchantment.values()).forEach(s->
					{	
						if(s.getName().toLowerCase().startsWith(args[0].toLowerCase()))
						{	
							ret.add(s.getName().toLowerCase());
						}
					});
					break;
				}
				return ret;
			}
			@SuppressWarnings("deprecation")
			@Override
			public boolean execute(CommandSender sender, String[] args)
			{	
				if(sender instanceof Player)
				{	
					Enchantment enchant=Enchantment.getByName(args[0].toUpperCase());
					if(enchant!=null)
					{	
						Enchants.setEnchant(((Player)sender).getItemInHand(),enchant,1);
					}
					else
					{	
						return false;
					}
				}
				else
				{	
					sender.sendMessage(MzTechPrefix+"§4游戏中的玩家才能使用该命令");
				}
				return true;
			}
		},
		new MzTechCommand("enchant",true,2)
		{	
			@Override
			public String usage()
			{	
				return "enchant <附魔> <等级>";
			}
			
			@Override
			public List<String> onTabComplite(CommandSender sender,String[] args)
			{	
				List<String> ret=new ArrayList<>();
				switch(args.length)
				{	
				case 1:
					Lists.newArrayList(Enchantment.values()).forEach(s->
					{	
						if(s.getName().toLowerCase().startsWith(args[0].toLowerCase()))
						{	
							ret.add(s.getName().toLowerCase());
						}
					});
					break;
				}
				return ret;
			}
			@SuppressWarnings("deprecation")
			@Override
			public boolean execute(CommandSender sender, String[] args)
			{	
				if(sender instanceof Player)
				{	
					Enchantment enchant=Enchantment.getByName(args[0].toUpperCase());
					Integer level=null;
					try
					{	
						level=Integer.valueOf(args[1]);
					}
					finally
					{	
						if(enchant!=null&&level!=null)
						{	
							Enchants.setEnchant(((Player)sender).getItemInHand(),enchant,level);
						}
						else
						{	
							return false;
						}
					}
				}
				else
				{	
					sender.sendMessage(MzTechPrefix+"§4游戏中的玩家才能使用该命令");
				}
				return true;
			}
		},
		new MzTechCommand("rename",true,1)
		{	
			@Override
			public String usage()
			{	
				return "rename <新名称>";
			}
			
			@Override
			public boolean execute(CommandSender sender, String[] args)
			{	
				if(sender instanceof Player)
				{	
					@SuppressWarnings("deprecation")
					ItemStack is=((Player)sender).getItemInHand();
					if(is!=null&&is.getType()!=Material.AIR)
					{	
						ItemMeta im = is.getItemMeta();
						im.setDisplayName(args[0].replace('&','§'));
						is.setItemMeta(im);
					}
					else
					{	
						return false;
					}
				}
				else
				{	
					sender.sendMessage(MzTechPrefix+"§4游戏中的玩家才能使用该命令");
				}
				return true;
			}
		},
		new MzTechCommand("locname",true,1)
		{	
			@Override
			public String usage()
			{	
				return "locname <新未翻译的名称>";
			}
			
			@Override
			public boolean execute(CommandSender sender, String[] args)
			{	
				if(sender instanceof Player)
				{	
					@SuppressWarnings("deprecation")
					ItemStack is=((Player)sender).getItemInHand();
					if(is!=null&&is.getType()!=Material.AIR)
					{	
						String type=isItem(is);
						if(type != null)
						{	
							sender.sendMessage(MzTechPrefix+"§e科技物品"+type+"更改了标识，（可能）变为了原版物品");
						}
						ItemMeta im = is.getItemMeta();
						im.setLocalizedName(args[0].replace('&','§'));
						is.setItemMeta(im);
					}
					else
					{	
						return false;
					}
				}
				else
				{	
					sender.sendMessage(MzTechPrefix+"§4游戏中的玩家才能使用该命令");
				}
				return true;
			}
		},
		new MzTechCommand("setsign",true,2)
		{	
			@Override
			public String usage()
			{	
				return "setsign <行数> <内容>";
			}
			
			@SuppressWarnings("deprecation")
			@Override
			public boolean execute(CommandSender sender, String[] args)
			{	
				if(sender instanceof Player)
				{	
					Block block=((Player)sender).getTargetBlock((HashSet<Byte>)null,10);
					if(block==null||(block.getType()!=Material.SIGN_POST&&block.getType()!=Material.WALL_SIGN))
					{	
						sender.sendMessage(MzTechPrefix+"§4请使用指针对准一个告示牌");
					}
					else
					{	
						Sign state = (Sign)block.getState();
						state.setLine(new Integer(args[0])-1,args[1].replace("&","§"));
						state.update();
					}
				}
				else
				{	
					sender.sendMessage(MzTechPrefix+"§4游戏中的玩家才能使用该命令");
				}
				return true;
			}
		}
    );
    public static Map<String,ItemStack> items=null;
    public static ConcurrentMap<Chunk,CopyOnWriteArrayList<Machine>> machines=new ConcurrentHashMap<>();
    
    Map<String,Object> config=null;
    static String MzTechPrefix="§e[§bMz科技§e] §r";

    public void onEnable()
    {	
    	try
    	{
    		enable();
    	}
    	catch(Exception e)
    	{	
    		Bukkit.broadcastMessage(MzTechPrefix+"§4加载时发生错误，请暂时不要使用Mz科技");
    		e.printStackTrace();
    	}
    }
    @SuppressWarnings("deprecation")
	void enable()
    {	
    	MzTech.items=new HashMap<>();
		MzTech.reload();
		Bukkit.getPluginCommand("mztech").setTabCompleter(this);
        Bukkit.getPluginManager().registerEvents(this,MzTech.instance);
		Bukkit.getPluginManager().registerEvents(new BasicMachines(),MzTech.instance);
        Bukkit.getPluginManager().registerEvents(new BlockDamageStopEvent(),MzTech.instance);
        Bukkit.getPluginManager().registerEvents(new BreakMachineEvent(),MzTech.instance);
		Bukkit.getPluginManager().registerEvents(new CraftGuide(),MzTech.instance);
        Bukkit.getPluginManager().registerEvents(new ClickItemEvent(),MzTech.instance);
        Bukkit.getPluginManager().registerEvents(new ClickMachineEvent(),MzTech.instance);
        Bukkit.getPluginManager().registerEvents(new Chemistry(),MzTech.instance);
        Bukkit.getPluginManager().registerEvents(new DropsName(),MzTech.instance);
        Bukkit.getPluginManager().registerEvents(new Enchants(),MzTech.instance);
        if((Boolean)config.get("entityStack"))
        {	
        	try
        	{	
        		Bukkit.getPluginManager().registerEvents(new EntityStack(),MzTech.instance);
        	}
        	catch(Exception e)
        	{	
        		getLogger().warning("实体堆叠模块加载异常");
        	}
        }
        Bukkit.getPluginManager().registerEvents(new EatItemEvent(),MzTech.instance);
        Bukkit.getPluginManager().registerEvents(new Food(),MzTech.instance);
        Bukkit.getPluginManager().registerEvents(new ItemFunctionShow(),MzTech.instance);
        Bukkit.getPluginManager().registerEvents(new KillEntityRecipe(),MzTech.instance);
        Bukkit.getPluginManager().registerEvents(new MoveItemEvent(),MzTech.instance);
        Bukkit.getPluginManager().registerEvents(new PlaceMachineEvent(),MzTech.instance);
        Bukkit.getPluginManager().registerEvents(new ShowItemEvent(),MzTech.instance);
        Bukkit.getPluginManager().registerEvents(new Tools(),MzTech.instance);
        Bukkit.getPluginManager().registerEvents(new Sundry(),MzTech.instance);
        Bukkit.getPluginManager().registerEvents(new UseItemForEntityRecipe(),MzTech.instance);
		Bukkit.getPluginManager().registerEvents(new WorkBenchShapelessRecipe(),MzTech.instance);
		ItemStack debugStick=new ItemStack(Material.STICK);
		ItemMeta im = debugStick.getItemMeta();
		im.setLocalizedName("§9调试棒");
		debugStick.setItemMeta(im);
		items.put("调试棒",debugStick);
		
    	Bukkit.getWorlds().forEach((w)->
    	{	
    		Lists.newArrayList(w.getLoadedChunks()).forEach((c)->
    		{	
    			this.onChunkLoad(new ChunkLoadEvent(c,false));
    		});
    	});
        new BukkitRunnable()
        {	
			@Override
			public void run()
			{	
		    	Bukkit.getWorlds().forEach((w)->
		    	{	
		    		Lists.newArrayList(w.getLoadedChunks()).forEach((c)->
		    		{	
		    			MzTech.instance.onChunkUnload(new ChunkUnloadEvent(c,true));
		    			MzTech.instance.onChunkLoad(new ChunkLoadEvent(c,false));
		    		});
		    	});
			}
        }.runTaskTimer(MzTech.instance,6000L,6000L);
        Bukkit.getPluginManager().registerEvents(new ShowEffect(),MzTech.instance);
    }
    
    @EventHandler
    void onClickItem(ClickItemEvent event)
    {	
    	switch(event.item)
    	{	
    	case "调试棒":
    		if(event.clickEntity)
    		{	
    			event.player.sendMessage(MzTechPrefix+"§a实体MID： "+event.entity.getType().toString());
    			event.player.sendMessage(MzTechPrefix+"§a实体翻译前名称： "+EntityStack.getEntityUnlocalizedName(event.entity));
    			if(event.entity instanceof Damageable)
    			{	
    				event.player.sendMessage(MzTechPrefix+"§a实体显示名： "+EntityStack.getEntityName((Damageable) event.entity).replace("§","&"));
    				event.player.sendMessage(MzTechPrefix+"§a实体堆叠数： "+EntityStack.getEntityNum((Damageable) event.entity));
    			}
    			event.player.sendMessage(MzTechPrefix+"§a可区分NBT： "+EntityStack.removeUselessNBT(new JsonParser().parse(NBT.getEntityNBT(event.entity)).getAsJsonObject()));
    		}
    		break;
    	}
    }
    
	@SuppressWarnings("unchecked")
	@EventHandler
	void onPlayerJoin(PlayerJoinEvent event)
	{	
		if(!event.getPlayer().hasPlayedBefore())
		{	
			if((Boolean)config.getOrDefault("autoWelcome",true))
			{	
				List<String> welcome = Lists.newArrayList((List<String>)config.getOrDefault("welcome",new ArrayList<>()));
				Random rand=new Random();
				Bukkit.getOnlinePlayers().forEach(p->
				{	
					if(p!=event.getPlayer())
					{	
						new BukkitRunnable()
						{	
							public void run()
							{	
								p.chat(welcome.get(Math.abs(rand.nextInt())%welcome.size()));
							}
						}.runTaskLater(MzTech.instance,10+Math.abs(rand.nextInt())%30);
					}
				});
			}
		}
	}
	
	@Override
    public boolean onCommand(CommandSender sender,Command command,String label,String[] args)
    {	
    	if(command.getName().equalsIgnoreCase("MzTech"))
    	{	
    		if(args.length>0)
    		{	
    			boolean[] executed= {false,true};
    			commands.forEach(c->
    			{	
    				if(executed[1]&&!executed[0])
    				{	
	    				if(c.name.toLowerCase().equals(args[0].toLowerCase()))
	    				{	
	    					if(sender.isOp()||!c.onlyOp)
	    					{	
	    						if(args.length>c.argsNum)
	    						{	
	    			    			String[] childArgs=new String[c.argsNum];
	    			    			for(int i=0;i<c.argsNum;i++)
	    			    			{	
	    			    				childArgs[i]=args[i+1];
	    			    			}
	    			    			for(int i=c.argsNum+1;i<args.length;i++)
	    			    			{	
	    			    				if(childArgs.length==0)
	    			    				{	
	    			    					childArgs=new String[]{args[i]};
	    			    				}
	    			    				childArgs[c.argsNum>0?c.argsNum-1:0]+=" "+args[i];
	    			    			}
	    							executed[0]=c.execute(sender,childArgs);
	    						}
	    					}
	    					else
	    					{	
	    						sender.sendMessage(MzTechPrefix+"§4只有管理员才能使用该命令");
	    						executed[1]=false;
	    					}
	    				}
    				}
    			});
    			if(!executed[0])
    			{	
    				boolean[] contains={false};
        			commands.forEach(c->
        			{	
        				if(c.name.toLowerCase().equals(args[0].toLowerCase()))
        				{	
        					if(!contains[0])
        					{	
        						sender.sendMessage(MzTechPrefix+"§4用法：");
        						contains[0]=true;
        					}
        					sender.sendMessage("§4/MzTech "+c.usage());
        				}
        			});
        			if(!contains[0])
        			{	
        				return false;
        			}
    			}
    			return true;
    		}
    		else
    		{	
    			return false;
    		}
    	}
    	else if(command.getName().equalsIgnoreCase("color"))
    	{	
    		if(sender.isOp())
    		{	
	    		if(args.length>0&&args[0].startsWith("/"))
	    		{	
		    		StringBuilder c=new StringBuilder(args[0].substring(1));
		    		for(int i=1;i<args.length;i++)
		    		{	
		    			c.append(" "+args[i].replace("&","§"));
		    		}
		    		Bukkit.getServer().dispatchCommand(sender,c.toString());
	    			return true;
	    		}
	    		else
	    		{	
	    			return false;
	    		}
    		}
    		else
    		{	
    			sender.sendMessage(MzTechPrefix+"§4你没有权限执行该命令");
    			return true;
    		}
    	}
    	else if(command.getName().equalsIgnoreCase("sunday"))
    	{	
    		Bukkit.getServer().dispatchCommand(sender,"sun");
    		Bukkit.getServer().dispatchCommand(sender,"day");
    		return true;
    	}
        return super.onCommand(sender, command, label, args);
    }
    
	public static boolean canOpen(Player player)
	{	
		if(player.isSneaking())
		{	
			if(player.getInventory().getItemInMainHand()!=null&&player.getInventory().getItemInMainHand().getType()!=Material.AIR)
			{	
				return false;
			}
			if(player.getInventory().getItemInOffHand()!=null&&player.getInventory().getItemInOffHand().getType()!=Material.AIR)
			{	
				return false;
			}
		}
		return true;
	}
	
	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args)
	{	
		if(command.getName().equalsIgnoreCase("MzTech"))
		{	
			List<String> ret=new ArrayList<>();
			if(args.length==1)
			{	
				commands.forEach((s)->
				{	
					if(sender.isOp()||!s.onlyOp)
					{	
						if(s.name.toLowerCase().startsWith(args[0]))
						{	
							ret.add(s.name);
						}
					}
				});
			}
			else if(args.length>1)
			{	
				commands.forEach(s->
				{	
					if(sender.isOp()||!s.onlyOp)
					{	
						if(s.name.toLowerCase().startsWith(args[0]))
						{	
			    			String[] childArgs=new String[args.length-1];
			    			for(int i=1;i<args.length;i++)
			    			{	
			    				childArgs[i-1]=args[i];
			    			}
							ret.addAll(s.onTabComplite(sender,childArgs));
						}
					}
				});
			}
			return ret;
		}
		return Lists.newArrayList();
	}
	public static String chunkFile(Chunk c)
	{	
		return "plugins\\MzTech\\machines\\"+c.getWorld().getName()+"("+c.getX()+","+c.getZ()+").mc";
	}
	@EventHandler
	void onChunkLoad(ChunkLoadEvent event)
	{	
		try
		{	
			File f=new File(MzTech.chunkFile(event.getChunk()));
			if(f.exists())
			{	
				DataInputStream fo=new DataInputStream(new FileInputStream(f));
				int size = fo.readInt();
				CopyOnWriteArrayList<Machine> ms=new CopyOnWriteArrayList<>();
				for(;size>0;size--)
				{	
					byte[] bs=new byte[fo.readInt()];
					fo.read(bs);
					String type=new String(bs);
					
					bs=new byte[fo.readInt()];
					fo.read(bs);
					String world=new String(bs,GBK);
					
					Location loc=new Location(Bukkit.getWorld(world),fo.readDouble(),fo.readDouble(),fo.readDouble());
					
					Inventory inv=readInventory(fo,items.get(type).getItemMeta().getLocalizedName());
					
					ms.add(new Machine(type,loc,inv));
					
//					if(loc.getBlock().isEmpty()||loc.getBlock().getType()==Material.AIR)
//					{	
//						Bukkit.getLogger().log(Level.WARNING,"方块丢失： "+loc.getBlock());
//						removeMachine(loc);
//					}
					
					if(type.equals("转化桌"))
					{	
						ms.remove(ms.size()-1);
					}
				}
				fo.close();
				MzTech.machines.put(event.getChunk(),ms);
			}
		}
		catch(IOException e)
		{	
			e.printStackTrace();
		}
	}
	@Deprecated
	public static String[] getItemID(ItemStack is)
	{	
		Class<?> cisc=getCraftBukkitClass("inventory.CraftItemStack");
		Object NMSStack = null;
		try {
			Constructor<?> cs=ReflectionWrapper.getConstructor(cisc,org.bukkit.inventory.ItemStack.class);
			cs.setAccessible(true);
			NMSStack = getFieldValue(getField(cisc,"handle"),cs.newInstance(is));
		} catch (Exception e) {
		}
		Object nitem=getFieldValue(getField(getNMSClass("ItemStack"),"item"),NMSStack);
		Object registry=getStaticFIeldValue(getField(getNMSClass("Item"), "REGISTRY"));
		Object mkey=invokeMethod(getMethod(registry.getClass(), "b", Object.class),registry,nitem);
		String namespace=getFieldValue(getField(mkey.getClass(), "a"),mkey);
		String name=getFieldValue(getField(mkey.getClass(), "b"),mkey);
		return new String[] {namespace,name};
	}
	@EventHandler
	void onChunkUnload(ChunkUnloadEvent event)
	{	
		File path=new File("plugins/MzTech/machines");
		if(!path.exists())
		{	
			path.mkdirs();
		}
		File f=new File(MzTech.chunkFile(event.getChunk()));
		if(machines.containsKey(event.getChunk())&&machines.get(event.getChunk()).size()>0)
		{	
			try
			{	
				if(!f.exists())
				{	
					f.createNewFile();
				}
				try(DataOutputStream dfo=new DataOutputStream(new FileOutputStream(f)))
				{
					List<Machine> ms=MzTech.machines.get(event.getChunk());
					dfo.writeInt(ms.size());
					ms.forEach(a->
					{	
						try
						{	
							byte[] bs=a.type.getBytes(GBK);
							int length=bs.length;
							dfo.writeInt(length);
							dfo.write(bs);
							
							bs=event.getChunk().getWorld().getName().getBytes();
							length=bs.length;
							dfo.writeInt(length);
							dfo.write(bs);
							
							dfo.writeDouble(a.place.getX());
							dfo.writeDouble(a.place.getY());
							dfo.writeDouble(a.place.getZ());
							
							writeInventory(dfo,a.inv);
						}
						catch(IOException e)
						{	
							e.printStackTrace();
						}
					});
					MzTech.machines.remove(event.getChunk());
				}
			}
			catch(IOException e)
			{	
				e.printStackTrace();
			}
		}
		else
		{	
			if(f.exists())
			{	
				f.delete();
			}
		}
	}
	public static String isMachine(Location loc)
	{	
		if(MzTech.machines.containsKey(loc.getChunk()))
		{	
			String[] r= {null};
			MzTech.machines.get(loc.getChunk()).forEach(m->
			{	
				if(m.place.equals(loc))
				{	
					r[0]=m.type;
				}
			});
			return r[0];
		}
		else return null;
	}
	public static void forEachMachine(String type,BiConsumer<? super Block,? super Inventory> action)
	{	
		MzTech.machines.forEach((c,l)->
		{	
			l.forEach(m->
			{	
				if(m.type.equals(type))
				{	
					action.accept(m.place.getBlock(),m.inv);
				}
			});
		});
	}
	public static Inventory getMachineInventory(Location loc)
	{	
		if(MzTech.machines.containsKey(loc.getChunk()))
		{	
			Inventory[] r= {null};
			Lists.newArrayList(MzTech.machines.get(loc.getChunk())).forEach(m->
			{	
				if(m.place.equals(loc))
				{	
					r[0]=m.inv;
				}
			});
			return r[0];
		}
		else return null;
	}
	public static void setMachineInventory(Location loc,Inventory inv)
	{	
		if(MzTech.machines.containsKey(loc.getChunk()))
		{	
			MzTech.machines.get(loc.getChunk()).forEach(m->
			{	
				if(m.place.equals(loc))
				{	
					m.inv=inv;
				}
			});
		}
	}
	
	public static boolean containItem(List<ItemStack> ls,ItemStack h)
	{	
		boolean[] rb= {false};
		if(h==null)
		{	
			h=new ItemStack(Material.AIR);
		}
		final ItemStack t=h;
		ls.forEach(i->
		{	
			if(i==null)
			{	
				i=new ItemStack(Material.AIR);
			}
			if(i.isSimilar(t)&&i.getAmount()>=t.getAmount())
			{	
				rb[0]=true;
			}
		});
		return rb[0];
	}
	@SuppressWarnings("deprecation")
	public static boolean giveInHand(Player player,ItemStack is)
	{	
		if(player.getItemInHand().getType()==Material.AIR)
		{	
			player.getInventory().setItemInMainHand(is);
			return true;
		}
		else
		{	
			player.getInventory().forEach((i)->
			{	
				if(is.getAmount()>0&&is.isSimilar(i))
				{	
					if(i.getAmount()+is.getAmount()>is.getMaxStackSize())
					{	
						is.setAmount(is.getAmount()-(is.getMaxStackSize()-i.getAmount()));
						i.setAmount(i.getMaxStackSize());
					}
					else
					{	
						i.setAmount(i.getAmount()+is.getAmount());
						is.setAmount(0);
					}
				}
			});
			if(is.getAmount()>0)
			{	
				if(player.getInventory().firstEmpty()==-1)//丢出
				{	
					player.getWorld().dropItemNaturally(player.getLocation(),is);
				}
				else//放入
				{	
					player.getInventory().addItem(is);
				}
			}
			return false;
		}
	}
	public static boolean isItem(ItemStack is,String name)
	{	
		return (is!=null&&is.hasItemMeta()&&is.getItemMeta().hasLocalizedName()&&MzTech.items.containsKey(name)&&MzTech.items.get(name).hasItemMeta()&&Objects.equal(MzTech.items.get(name).getItemMeta().getLocalizedName(),is.getItemMeta().getLocalizedName()));
	}
	public static String isItem(ItemStack is)
	{	
		if(is!=null&&is.getType()!=Material.AIR)
		{	
			if(is.hasItemMeta()&&is.getItemMeta().hasLocalizedName())
			{	
				String[] item= {null};
				items.forEach((s,i)->
				{	
					if(is.getItemMeta().getLocalizedName().equals(i.getItemMeta().getLocalizedName()))
					{	
						item[0]=s;
					}
				});
				return item[0];
			}
			else
			{	
				return null;
			}
		}
		else
		{	
			return null;
		}
	}
	
    public static Map<String, Object> getConfigs()
    {	
    	return instance.config;
    }

    @SuppressWarnings("unchecked")
	public static void reload()
    {	
		try
		{	
			new File("plugins\\MzTech").mkdirs();
	    	File config=new File("plugins\\MzTech\\config.yml");
	    	if(!config.exists())
	    	{	
    			InputStream res = MzTech.instance.getResource("mz/tech/config.yml");
    			Files.copy(res, config.toPath());
	    	}
			try(FileInputStream fis=new FileInputStream(config))
			{
				Map<String,Object> map=new Yaml().loadAs(fis,Map.class);
				
				instance.config=map;
				MzTechPrefix = (String)map.get("MzTech");
				DropsName.reload(map);
				BasicMachines.reload(map);
			}
		}
		catch(IOException e)
		{	
			throw new RuntimeException(e);
		}
	}
	@SuppressWarnings("deprecation")
	@Override
    public void onDisable()
    {	
    	Bukkit.getWorlds().forEach((w)->
    	{	
    		Lists.newArrayList(w.getLoadedChunks()).forEach((c)->
    		{	
    			this.onChunkUnload(new ChunkUnloadEvent(c,true));
    		});
    	});
    	
		WorkBenchShapelessRecipe.onDisable();
		CraftGuide.onDisable();
		Sundry.onDisable();
		BasicMachines.onDisable();//Material
    }
	public static int containItem(ItemStack h,List<ItemStack> ls)
	{	
		int[] ri= {-1};
		if(h==null)
		{	
			h=new ItemStack(Material.AIR);
		}
		final ItemStack t=h;
		int[] j= {0};
		ls.forEach((i)->
		{	
			if(ri[0]==-1)
			{	
				if(i==null)
				{	
					i=new ItemStack(Material.AIR);
				}
				if(i.isSimilar(t)&&i.getAmount()<=t.getAmount())
				{	
					ri[0]=j[0];
				}
			}
			j[0]++;
		});
		return ri[0];
	}
	public static boolean removeItem(ItemStack h,List<ItemStack> ls)
	{	
		boolean[] rb= {false};
		if(h==null)
		{	
			h=new ItemStack(Material.AIR);
		}
		final ItemStack t=h;
		ls.forEach((i)->
		{	
			if(!rb[0])
			{	
				if(i==null)
				{	
					i=new ItemStack(Material.AIR);
				}
				if(i.isSimilar(t)&&i.getAmount()<=t.getAmount())
				{	
					t.setAmount(t.getAmount()-i.getAmount());
					rb[0]=true;
				}
			}
		});
		return rb[0];
	}
	public static void removeMachine(Location loc)
	{	
		if(loc!=null&&MzTech.machines.containsKey(loc.getChunk()))
		{	
			MzTech.machines.get(loc.getChunk()).forEach(m->
			{	
				if(m.place.equals(loc))
				{	
					MzTech.machines.get(loc.getChunk()).remove(m);
				}
			});
		}
	}
	public static void addMachine(Location loc,String type,Inventory inv)
	{	
		if(!MzTech.machines.containsKey(loc.getChunk()))
		{	
			MzTech.machines.put(loc.getChunk(),new CopyOnWriteArrayList<>());
		}
		MzTech.machines.get(loc.getChunk()).add(new Machine(type,loc,inv));
	}
	@EventHandler(priority = EventPriority.HIGHEST)
	void onBlockBreak(BlockBreakEvent event)
	{	
		if(event.isCancelled())
		{	
			return;
		}
		if(MzTech.machines.containsKey(event.getBlock().getChunk()))
		{	
			MzTech.machines.get(event.getBlock().getChunk()).forEach((m)->
			{	
				if(m.place.equals(event.getBlock().getLocation()))
				{	
					BreakMachineEvent e=new BreakMachineEvent();
					e.block=event.getBlock();
					e.machine=m.type;
					e.player=event.getPlayer();
					Bukkit.getPluginManager().callEvent(e);
					if(e.isCancelled())
					{	
						event.setCancelled(true);
					}
					else
					{	
						if(m.inv!=null)
						{	
							int[] i= {0};
							m.inv.forEach(is->
							{	
								if(!MoveItemEvent.isEmpty(is))
								{	
									MoveItemEvent e2=new MoveItemEvent(m.inv,i[0],null,-1,is.getAmount(),ClickType.CONTROL_DROP,null);
									Bukkit.getPluginManager().callEvent(e2);
									if(!e2.isCancelled())
									{	
										m.place.getWorld().dropItemNaturally(m.place.add(new Location(m.place.getWorld(),0.5,0,0.5)),is);
									}
								}
								i[0]++;
							});
						}
						if(e.isDrops()&&(event.getPlayer()==null||event.getPlayer().getGameMode()!=GameMode.CREATIVE)&&event.isDropItems())
						{	
				            event.getBlock().getWorld().dropItemNaturally(event.getBlock().getLocation().add(new Location(event.getBlock().getWorld(),0.5,0,0.5)),new ItemStack(MzTech.items.get(m.type)));
						}
						event.setDropItems(false);
						MzTech.machines.get(event.getBlock().getChunk()).remove(m);
						if(MzTech.machines.get(event.getBlock().getChunk()).isEmpty())
						{	
							MzTech.machines.remove(event.getBlock().getChunk());
						}
					}
				}
			});
		}
	}
	@EventHandler(priority = EventPriority.HIGHEST)
	void onBlockFromTo(BlockFromToEvent event)
	{	
		if(event.isCancelled()||!event.getToBlock().getChunk().isLoaded())
		{	
			return;
		}
		BlockBreakEvent e=new BlockBreakEvent(event.getToBlock(),null);
		this.onBlockBreak(e);
		
		if(!e.isDropItems())
		{	
			event.setCancelled(true);
			event.getToBlock().setType(Material.AIR);
		}
	}
	@EventHandler(priority = EventPriority.HIGHEST)
	void onBlockFade(BlockFadeEvent event)
	{	
		if(event.isCancelled())
		{	
			return;
		}
		BlockBreakEvent e=new BlockBreakEvent(event.getBlock(),null);
		this.onBlockBreak(e);
		event.setCancelled(e.isCancelled());
	}
	@EventHandler(priority = EventPriority.HIGHEST)
	void onLeavesDecay(LeavesDecayEvent event)
	{	
		if(event.isCancelled())
		{	
			return;
		}
		BlockBreakEvent e=new BlockBreakEvent(event.getBlock(),null);
		this.onBlockBreak(e);
		if(!e.isDropItems())
		{	
			event.setCancelled(true);
			event.getBlock().setType(Material.AIR);
		}
	}
	@SuppressWarnings("deprecation")
	public static Block facingBlock(Block block)
	{	
		if(block.getType()==Material.TORCH||block.getType()==Material.REDSTONE_TORCH_OFF||block.getType()==Material.REDSTONE_TORCH_ON)
		{	
			switch(block.getData())
			{	
			case 1:
				return block.getLocation().add(-1,0,0).getBlock();
			case 2:
				return block.getLocation().add(1,0,0).getBlock();
			case 3:
				return block.getLocation().add(0,0,-1).getBlock();
			case 4:
				return block.getLocation().add(0,0,1).getBlock();
			case 5:
				return block.getLocation().add(0,-1,0).getBlock();
			default:
				return null;
			}
		}
		else
		{	
			switch(block.getData())
			{	
			case 0:
				return block.getLocation().add(0,-1,0).getBlock();
			case 1:
				return block.getLocation().add(0,1,0).getBlock();
			case 2:
				return block.getLocation().add(0,0,-1).getBlock();
			case 3:
				return block.getLocation().add(0,0,1).getBlock();
			case 4:
				return block.getLocation().add(-1,0,0).getBlock();
			case 5:
				return block.getLocation().add(1,0,0).getBlock();
			default:
				return null;
			}
		}
	}
	/**
	 * 得到方块依赖的方块
	 * 比如按钮依赖它的墙壁
	 * 红石粉依赖地板
	 * 没有依赖则返回它本身
	 * @param block 方块
	 * @return 依赖的方块
	 */
	public static Block getDependBlock(Block block)
	{	
		if(block.getPistonMoveReaction()==PistonMoveReaction.BREAK)
		{	
			try
			{	
				switch((BlockFace)ReflectionWrapper.invokeMethod(ReflectionWrapper.getMethodParent(block.getState().getData().getClass(),"getAttachedFace"),block.getState().getData()))
				{
				case DOWN:
					return block.getLocation().add(0,-1,0).getBlock();
				case UP:
					return block.getLocation().add(0,1,0).getBlock();
				case EAST:
					return block.getLocation().add(1,0,0).getBlock();
				case WEST:
					return block.getLocation().add(-1,0,0).getBlock();
				case SOUTH:
					return block.getLocation().add(0,0,1).getBlock();
				case NORTH:
					return block.getLocation().add(0,0,-1).getBlock();
				default:
					break;
				}
			}
			catch(Throwable e)
			{	
				return block.getLocation().add(0,-1,0).getBlock();
			}
		}
		return block;
	}
	@EventHandler(priority = EventPriority.HIGHEST)
	void onEntityExplode(EntityExplodeEvent event)
	{	
		if(event.isCancelled())
		{	
			return;
		}
		event.blockList().forEach(b->
		{	
			BlockBreakEvent e=new BlockBreakEvent(b,null);
			e.setDropItems(true);
			this.onBlockBreak(e);
		});
	}
	@EventHandler(priority = EventPriority.HIGHEST)
	void onBlockExplode(BlockExplodeEvent event)
	{	
		if(event.isCancelled())
		{	
			return;
		}
		event.blockList().forEach(b->
		{	
			BlockBreakEvent e=new BlockBreakEvent(b,null);
			e.setDropItems(true);
			this.onBlockBreak(e);
		});
	}
	@EventHandler(priority = EventPriority.HIGHEST)
	void onBlockPhysics(BlockPhysicsEvent event)
	{	
		if(event.isCancelled())
		{	
			return;
		}
		Block depand=getDependBlock(event.getBlock());
		if(depand!=event.getBlock()&&depand.isEmpty())
		{	
			BlockBreakEvent e=new BlockBreakEvent(event.getBlock(),null);
			e.setDropItems(true);
			this.onBlockBreak(e);
			if(!e.isDropItems())
			{	
				event.setCancelled(true);
				event.getBlock().setType(Material.AIR);
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
		ItemStack is=event.getItemInHand();
		if(is.hasItemMeta()&&is.getItemMeta().hasLocalizedName())
		{	
			String locName=is.getItemMeta().getLocalizedName();
			MzTech.items.forEach((s,i)->
			{	
				if(i.getItemMeta().hasLocalizedName()&&i.getItemMeta().getLocalizedName().contains(locName))
				{	
					PlaceMachineEvent e=new PlaceMachineEvent(event.getPlayer(),s,event.getBlock(),event.getBlockAgainst(),event.getBlockReplacedState().getBlock());
					Bukkit.getPluginManager().callEvent(e);
					if(e.isCancelled())
					{	
						event.setCancelled(true);
					}
					else
					{	
						if(!MzTech.machines.containsKey(event.getBlock().getChunk()))
						{	
							MzTech.machines.put(event.getBlock().getChunk(),new CopyOnWriteArrayList<>());
						}
						MzTech.machines.get(event.getBlock().getChunk()).add(new Machine(s,event.getBlock().getLocation(),null));
						if(event.getBlock().getState() instanceof Nameable)
						{	
							((Nameable)event.getBlockPlaced().getState()).setCustomName(locName);
						}
					}
				}
			});
		}
	}
	@EventHandler(priority = EventPriority.HIGHEST)
	void onBlockPistonExtend(BlockPistonExtendEvent event)
	{	
		if(event.isCancelled())
		{	
			return;
		}
		event.getBlocks().forEach(b->
		{	
			String machine=MzTech.isMachine(b.getLocation());
			if(machine != null)
			{	
				switch(b.getPistonMoveReaction())
				{	
				case BREAK:
				{	
					BlockBreakEvent e=new BlockBreakEvent(b,null);
					onBlockBreak(e);
					b.setType(Material.AIR);
					break;
				}
				case MOVE:
				{	
					machines.get(b.getLocation().getChunk()).forEach(t->
					{	
						if(t.place.equals(b.getLocation()))
						{	
							MachineMoveEvent e=new MachineMoveEvent(b,event.getDirection());
							Bukkit.getPluginManager().callEvent(e);
							if(!e.isCancelled())
							{	
								machines.get(b.getLocation().getChunk()).remove(t);
								t.place=e.targetBlock.getLocation();
								if(!machines.containsKey(t.place.getChunk()))
								{	
									machines.put(t.place.getChunk(),new CopyOnWriteArrayList<>());
								}
								machines.get(t.place.getChunk()).add(t);
							}
						}
					});
					break;
				}
				default:
					break;
				}
			}
		});
	}
	@EventHandler
	void onPlayerEditBook(PlayerEditBookEvent event)
	{	
		BookMeta im = event.getNewBookMeta();
		int length=im.getPages().toString().length();
		if(length>13000&&im.getPageCount()<=50)
		{	
			event.getPlayer().sendMessage(MzTechPrefix+"§4非法书与笔，已清空");
			im.setPages(new ArrayList<>());
			event.setNewBookMeta(im);
		}
		else if(length>2000&&!(im.hasLocalizedName()&&im.getLocalizedName().equals("§a经过检查的书与笔")))
		{	
			event.getPlayer().sendMessage(MzTechPrefix+"§e过长书与笔，已交给管理员处理");
			ItemStack is=event.getPlayer().getInventory().getItem(event.getSlot());
			im.setAuthor(event.getPlayer().getName());
			im.setLocalizedName("§a经过检查的书与笔");
			is.setItemMeta(im);
			Sundry.getBag("过长书与笔",54).addItem(is);
			event.getPlayer().getInventory().setItem(event.getSlot(),new ItemStack(Material.AIR));
			event.setCancelled(true);
			Bukkit.getOnlinePlayers().forEach(p->
			{	
				if(p.isOp())
				{	
					event.getPlayer().sendMessage(MzTechPrefix+"§e"+event.getPlayer().getName()+"§e编辑了过长的书与笔");
					event.getPlayer().sendMessage(MzTechPrefix+"§e长度： "+length+"/13088");
					event.getPlayer().sendMessage(MzTechPrefix+"§e页数： "+im.getPageCount()+"/50");
					event.getPlayer().sendMessage(MzTechPrefix+"§e请使用指令查看该书与笔/mztech bag 过长书与笔");
					event.getPlayer().sendMessage(MzTechPrefix+"§e如为合情合理的大作，归还即可");
				}
			});
		}
	}
	public static void throwRuntime(final Throwable e)
	{	
	    if (e instanceof Error)
	    {	
	    	throw (Error)e;
	    }
	    else if(e instanceof RuntimeException)
	    {	
	    	throw (RuntimeException)e;
	    }
	    else
	    {	
	    	throw new RuntimeException(e);
	    }
	}
	@EventHandler(priority = EventPriority.HIGHEST)
	void onBlockPistonRetract(BlockPistonRetractEvent event)
	{	
		onBlockPistonExtend(new BlockPistonExtendEvent(event.getBlock(),event.getBlocks(),event.getDirection()));
	}
	
	public static boolean isMachineInventory(Inventory inv,String Machine)
	{	
		if(inv==null||Machine==null)
		{	
			return false;
		}
		boolean[] rb= {false};
		machines.values().forEach(ms->
		{	
			ms.forEach(m->
			{	
				if(m.type.equals(Machine)&&inv.equals(m.inv))
				{	
					rb[0]=true;
				}
			});
		});
		return rb[0];
	}
	

	public static void writeInventory(DataOutputStream stream, Inventory inv)
	{	
		try
		{	
			if(inv==null)
			{	
				stream.writeInt(0);
			}
			else
			{	
				stream.writeInt(inv.getSize());
				
				byte[] title;
				try
				{	
					title=inv.getTitle().getBytes(GBK);
				}
				catch(Throwable e)
				{	
					title= new byte[]{};
				}
				stream.writeInt(title.length);
				stream.write(title);
				
				for(int i=0;i<inv.getSize();i++)
				{	
					ItemStack is=inv.getItem(i);
					if(is==null||is.getType()==Material.AIR)
					{	
						stream.writeBoolean(false);
					}
					else
					{	
						stream.writeBoolean(true);
						writeItemStack(stream,is);
					}
				}
			}
		}
		catch(Exception e)
		{	
			e.printStackTrace();
		}
	}
	public static Inventory readInventory(DataInputStream stream,String name)
	{	
		Inventory inv=null;
		try
		{	
			int isNum=stream.readInt();//物品数量
			if(isNum>0)
			{	
				byte[] title=new byte[stream.readInt()];
				stream.read(title);
				
				inv=Bukkit.createInventory(null,isNum,name==null?new String(title,GBK):name);
				for(int i=0;i<isNum;i++)
				{	
					if(stream.readBoolean())
					{	
						inv.setItem(i,readItemStack(stream));
					}
				}
			}
		}
		catch(Exception e)
		{	
			e.printStackTrace();
		}
		return inv;
	}
	public static void writeItemStack(DataOutputStream stream, ItemStack is)
	{	
		final StreamSerializer ss=new StreamSerializer();
		try
		{	
			String sItem=ss.serializeItemStack(is);
			byte[] buf=sItem.getBytes(GBK);
			stream.writeInt(buf.length);
			stream.write(buf);
		}
		catch (IOException e)
		{	
			e.printStackTrace();
		}
	}
	public static ItemStack readItemStack(DataInputStream stream)
	{	
		final StreamSerializer ss=new StreamSerializer();
		try
		{	
			byte[] buf=new byte[stream.readInt()];
			stream.read(buf);
			return ss.deserializeItemStack(new String(buf,GBK));
		}
		catch (IOException e)
		{	
			e.printStackTrace();
		}
		return null;
	}
}
