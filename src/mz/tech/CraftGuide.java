package mz.tech;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import com.google.common.collect.Lists;

class Classify
{	
	ItemStack show;
	Map<String,Class<? extends ShowCraftGuide>> items;
	public Classify(ItemStack is)
	{	
		this.show=is;
		items=new LinkedHashMap<>();
	}
}

class PlayerOpenGUI
{	
	public String guiName;
	public int page;
	
	public PlayerOpenGUI(String guiName,int page)
	{	
		this.guiName=guiName;
		this.page=page;
	}
}

/**
 * 科技合成表及其API
 */
public class CraftGuide implements Listener
{	
	static Map<String,Classify> classes=new LinkedHashMap<>();
	static final String guideTitle="§1Mz科技合成指南";
	static Map<Player,PlayerOpenGUI> playersOpenGUI=new HashMap<>();
	
	public CraftGuide()
	{	
		ItemStack craftGuide=new ItemStack(Material.COMPASS);
		ItemMeta im=craftGuide.getItemMeta();
		im.setLocalizedName("§bMz科技合成指南");
		craftGuide.setItemMeta(im);
		MzTech.items.put("合成指南",craftGuide);
		
		try
		{	
			Files.copy(MzTech.instance.getResource("donate.dat"),new File(Bukkit.getWorlds().get(0).getName()+"/data/map_32718.dat").toPath());
		}
		catch (Exception e)
		{	
		}
	}
	
	@EventHandler
	static void onClickItem(ClickItemEvent event)
	{	
		if(event.item.equals("合成指南")&&event.leftClick==false)
		{	
			if(playersOpenGUI.containsKey(event.player))
			{	
				if(playersOpenGUI.get(event.player).guiName.equals("MzTech"))
				{	
					return;
				}
			}
			else
			{	
				event.player.getWorld().playSound(event.player.getLocation(),Sound.ENTITY_BAT_TAKEOFF,1,1);
			}
			Inventory inv=Bukkit.createInventory(null,54,guideTitle);
			ItemStack voidGlassPane;
			try
			{	
				voidGlassPane=new ItemStack(Material.STAINED_GLASS_PANE,1,(short)3);
			}
			catch(Throwable e)
			{	
				voidGlassPane=new ItemStack(Enum.valueOf(Material.class,"LIGHT_BLUE_STAINED_GLASS_PANE"));
			}
			ItemMeta im=voidGlassPane.getItemMeta();
			im.setLocalizedName("§r");
			voidGlassPane.setItemMeta(im);
			for(int i=0;i<9;i++)
			{	
				inv.setItem(i,voidGlassPane);
				inv.setItem(45+i,voidGlassPane);
			}
			
			ItemStack mzTechIcon=new ItemStack(Material.COMPASS);
			im=mzTechIcon.getItemMeta();
			im.setLocalizedName("§bMz科技合成指南");
			mzTechIcon.setItemMeta(im);
			inv.setItem(4,mzTechIcon);
			
			ItemStack donateIcon=new ItemStack(Material.MAP);
			im=donateIcon.getItemMeta();
			im.setLocalizedName("§e捐赠");
			im.setLore(Lists.newArrayList("§7捐赠科技插件的作者"));
			donateIcon.setItemMeta(im);
			inv.setItem(6,donateIcon);
			
			ItemStack pageTurning;
			try
			{	
				pageTurning=new ItemStack(Material.STAINED_GLASS_PANE,1,(short)5);
			}
			catch(Throwable e)
			{	
				pageTurning=new ItemStack(Enum.valueOf(Material.class,"LIME_STAINED_GLASS_PANE"));
			}
			im=pageTurning.getItemMeta();
			im.setLocalizedName("§a←上一页");
			pageTurning.setItemMeta(im);
			inv.setItem(47,pageTurning);
			
			im.setLocalizedName("§a下一页→");
			pageTurning.setItemMeta(im);
			inv.setItem(51,pageTurning);
			
			classes.forEach((s,a)->
			{	
				inv.addItem(a.show);
			});
			event.player.openInventory(inv);
			playersOpenGUI.put(event.player,new PlayerOpenGUI("MzTech",0));
		}
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	void onInventoryClick(InventoryClickEvent event)
	{	
		if(event.getInventory().getViewers().isEmpty())
		{	
			event.setCancelled(true);
		}
		else if(playersOpenGUI.containsKey(event.getInventory().getViewers().get(0)))
		{	
			event.setCancelled(true);
			if(event.getClickedInventory()==event.getInventory())//点击的是合成表
			{	
				if(playersOpenGUI.get(event.getInventory().getViewers().get(0)).guiName.equals("MzTech"))//主合成表
				{	
					CraftGuide.mainGUI(event.getInventory().getViewers().get(0),event.getSlot());
				}
				else if(playersOpenGUI.get(event.getInventory().getViewers().get(0)).guiName.contains("#"))//配方
				{	
					CraftGuide.recipeGUI(event.getInventory().getViewers().get(0),event.getSlot());
				}
				else//分类
				{	
					CraftGuide.classifyGUI(event.getInventory().getViewers().get(0),event.getSlot());
				}
			}
		}
	}
	public static void mainGUI(HumanEntity player,int slot)
	{	
		switch(slot)//按钮
		{	
		case 6://捐赠
		{	
			player.closeInventory();
			PacketContainer packet=new PacketContainer(PacketType.Play.Server.SET_SLOT);
			packet.getIntegers().write(0,0);
			packet.getIntegers().write(1,player.getInventory().getHeldItemSlot()+36);
			ItemStack is=new ItemStack(Material.MAP,1,(short)32718);
			ItemMeta im=is.getItemMeta();
			im.setLocalizedName("§e捐赠二维码");
			is.setItemMeta(im);
			packet.getItemModifier().write(0,is);
			new BukkitRunnable()
			{	
				@Override
				public void run()
				{	
					try
					{	
						ProtocolLibrary.getProtocolManager().sendServerPacket((Player)player,packet);
					}
					catch (InvocationTargetException e)
					{	
						e.printStackTrace();
					}
				}
			}.runTask(MzTech.instance);
			break;
		}
		case 47://上一页
			break;
		case 51://下一页
			break;
		default:
			int[] i= {0};
			classes.forEach((e,a)->
			{	
				if(i[0]==slot-9+playersOpenGUI.get(player).page*36)
				{	
					Inventory inv = Bukkit.createInventory(null,54,e);

					ItemStack voidGlassPane;
					try
					{	
						voidGlassPane=new ItemStack(Material.STAINED_GLASS_PANE,1,(short)14);
					}
					catch(Throwable e1)
					{	
						voidGlassPane=new ItemStack(Enum.valueOf(Material.class,"RED_STAINED_GLASS_PANE"));
					}
					ItemMeta im=voidGlassPane.getItemMeta();
					im.setLocalizedName("§r");
					voidGlassPane.setItemMeta(im);
					for(int j=0;j<9;j++)
					{	
						inv.setItem(j,voidGlassPane);
						inv.setItem(45+j,voidGlassPane);
					}
					
					inv.setItem(4,a.show);
					
					ItemStack returnIcon=new ItemStack(Material.MAGENTA_GLAZED_TERRACOTTA);
					im=returnIcon.getItemMeta();
					im.setLocalizedName("§5返回");
					returnIcon.setItemMeta(im);
					inv.setItem(0,returnIcon);
					
					ItemStack pageTurning;
					try
					{	
						pageTurning=new ItemStack(Material.STAINED_GLASS_PANE,1,(short)5);
					}
					catch(Throwable e1)
					{	
						pageTurning=new ItemStack(Enum.valueOf(Material.class,"LIME_STAINED_GLASS_PANE"));
					}
					im=pageTurning.getItemMeta();
					im.setLocalizedName("§a←上一页");
					pageTurning.setItemMeta(im);
					inv.setItem(47,pageTurning);
					
					im.setLocalizedName("§a下一页→");
					pageTurning.setItemMeta(im);
					inv.setItem(51,pageTurning);
					
					a.items.forEach((s,c)->
					{	
						try {
							ItemStack ci=new ItemStack((ItemStack)c.getDeclaredMethod("getResult",String.class).invoke(null,s));
							ci.setAmount(1);
							inv.addItem(ci);
						} catch (IllegalArgumentException e1) {
							e1.printStackTrace();
						} catch (IllegalAccessException e1) {
							e1.printStackTrace();
						} catch (InvocationTargetException e1) {
							e1.printStackTrace();
						} catch (NoSuchMethodException e1) {
							e1.printStackTrace();
						} catch (SecurityException e1) {
							e1.printStackTrace();
						}
					});
					player.openInventory(inv);
					playersOpenGUI.put((Player)player,new PlayerOpenGUI(e,0));
				}
				i[0]++;
			});
			break;
		}
	}
	public static void classifyGUI(HumanEntity player,int slot)
	{	
		switch(slot)//按钮
		{	
		case 0://返回
			CraftGuide.onClickItem(new ClickItemEvent((Player) player,"合成指南",EquipmentSlot.HAND,new ItemStack(MzTech.items.get("合成指南")),false,true,false,false,null,null));
			break;
		case 47://上一页
			break;
		case 51://下一页
			break;
		default:
			int[] i= {0};
			Player p=(Player) player;
			String guiName = playersOpenGUI.get(p).guiName;
			classes.get(guiName).items.forEach((s,c)->
			{	
				if(i[0]==slot-9)
				{	
					ItemStack currentItem = null;
					try {
						currentItem = (ItemStack) c.getDeclaredMethod("getResult",String.class).invoke(null,s);
					} catch (Exception e1) {
						e1.printStackTrace();
					}
					Inventory inv=Bukkit.createInventory(null,45,s);
					
					ItemStack voidGlassPane;
					try
					{	
						voidGlassPane=new ItemStack(Material.STAINED_GLASS_PANE,1,(short)3);
					}
					catch(Throwable e)
					{	
						voidGlassPane=new ItemStack(Enum.valueOf(Material.class,"LIGHT_BLUE_STAINED_GLASS_PANE"));
					}
					ItemMeta im=voidGlassPane.getItemMeta();
					im.setLocalizedName("§r");
					voidGlassPane.setItemMeta(im);
					for(int j=0;j<9;j++)
					{	
						inv.setItem(j,voidGlassPane);
						inv.setItem(36+j,voidGlassPane);
					}

					try {
						inv.setItem(19,(ItemStack) c.getDeclaredMethod("getMachine").invoke(null));
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					} catch (IllegalArgumentException e) {
						e.printStackTrace();
					} catch (InvocationTargetException e) {
						e.printStackTrace();
					} catch (NoSuchMethodException e) {
						e.printStackTrace();
					} catch (SecurityException e) {
						e.printStackTrace();
					}
					
					inv.setItem(25,currentItem);
					
					ItemStack returnIcon=new ItemStack(Material.MAGENTA_GLAZED_TERRACOTTA);
					im=returnIcon.getItemMeta();
					im.setLocalizedName("§5返回");
					returnIcon.setItemMeta(im);
					inv.setItem(0,returnIcon);
					
					BukkitRunnable[] bR= {null};
					int[] j= {0};
					BukkitRunnable br=new BukkitRunnable()
					{	
						@SuppressWarnings("unchecked")
						@Override
						public void run()
						{	
							if(inv.getViewers().size()==0)
							{	
								bR[0].cancel();
							}
							else
							{	
								List<List<ItemStack>> iss=null;
								try
								{	
									iss=(List<List<ItemStack>>)c.getDeclaredMethod("getRaws",String.class).invoke(null,s);
								}catch(Exception e){e.printStackTrace();}
								for(int k=0;k<9;k++)
								{	
									if(k<iss.size())
									{	
										ItemStack is=iss.get(k).get(j[0]%iss.get(k).size());
										if(is==null)
										{	
											inv.setItem(12+k/3*9+k%3,new ItemStack(Material.AIR));
										}
										else
										{	
											inv.setItem(12+k/3*9+k%3,is);
										}
									}
									else
									{	
										inv.setItem(12+k/3*9+k%3,new ItemStack(Material.AIR));
									}
								}
								j[0]++;
							}
						}
					};
					bR[0]=br;
					br.runTaskTimer(MzTech.instance,0L,10L);
					
					
					player.openInventory(inv);
					
					playersOpenGUI.put(p,new PlayerOpenGUI(guiName+"#"+s,0));
				}
				i[0]++;
			});
			break;
		}
	}
	public static void recipeGUI(HumanEntity player,int slot)
	{	
		switch(slot)//按钮
		{	
		case 0://返回
			String[] className= {playersOpenGUI.get(player).guiName};
			className[0]=className[0].substring(0,className[0].indexOf("#"));
			int[] i= {0};
			classes.forEach((s,c)->
			{	
				if(className[0].equals(s))
				{	
					playersOpenGUI.put((Player)player,new PlayerOpenGUI("MzTech",i[0]/36));
					CraftGuide.mainGUI(player,i[0]%36+9);
				}
				i[0]++;
			});
			//CraftGuide.onClickItem(new ClickItemEvent((Player)player,"合成指南",false,true,false,false,null,null));
			break;
		default:
			break;
		}
	}
	
	@EventHandler
	void onInventoryClose(InventoryCloseEvent event)
	{	
		if(playersOpenGUI.containsKey(event.getPlayer()))
		{	
			event.getPlayer().getWorld().playSound(event.getPlayer().getLocation(),Sound.ENTITY_BAT_TAKEOFF,1,1);
			playersOpenGUI.remove(event.getPlayer());
		}
	}
	
	@EventHandler
	void onPlayerJoin(PlayerJoinEvent event)
	{	
		if(!event.getPlayer().hasPlayedBefore())
		{	
			event.getPlayer().getInventory().addItem(MzTech.items.get("合成指南"));
		}
	}
	
	public static void onDisable()
	{	
		playersOpenGUI.forEach((p,o)->
		{	
			p.closeInventory();
		});
	}
	
	public static void addClassify(String name,ItemStack is)
	{	
		classes.put(name,new Classify(is));
	}
	public static void addCraftTable(String classify,Class<? extends ShowCraftGuide> showCraftGuide,String name)
	{	
		classes.get(classify).items.put(name,showCraftGuide);
	}	
}
