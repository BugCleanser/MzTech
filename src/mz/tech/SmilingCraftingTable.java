package mz.tech;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.BiConsumer;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Dropper;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.inventory.meta.ItemMeta;

import com.google.common.collect.Lists;

/**
 * 代表微笑的合成台配方中的一个原料
 */
class Raw
{	
	ItemStack output;
	List<ItemStack> input;
	
	/**
	 * @param output 使用过的原料。如原料为水桶，这个参数通常为空桶
	 * @param input 适配的所有原料，其中任意一个即可匹配
	 */
	public Raw(ItemStack output,ItemStack...input)
	{	
		this.output=output;
		this.input=Lists.newArrayList(input);
	}
}
class Table
{	
	ItemStack result;
	List<Raw> raws;
	BiConsumer<? super ItemStack[],? super ItemStack> craftMethod;
	
	public Table(ItemStack result,List<Raw> raws,BiConsumer<? super ItemStack[],? super ItemStack> craftMethod)
	{	
		this.result=result;
		this.raws=raws;
		this.craftMethod=craftMethod;
	}
}

/**
 * 微笑的合成台模块及其API
 */
public class SmilingCraftingTable extends ShowCraftGuide implements Listener
{	
	public static Map<String, Table> tables = new HashMap<>();
	public static ItemStack machine=new ItemStack(Material.DROPPER);
	
	public static String smilingCraftingTable=null;
	
	static
	{	
		ItemMeta im = machine.getItemMeta();
		im.setLocalizedName("§6微笑的合成台");
		im.setLore(Lists.newArrayList("§7在§6微笑的合成台§7中合成"));
		machine.setItemMeta(im);
	}
	
	@SuppressWarnings("deprecation")
	public SmilingCraftingTable()
	{	
		smilingCraftingTable=(String) MzTech.getConfigs().get("smilingCraftingTable");
		
		ItemStack is=new ItemStack(Material.DROPPER);
		ItemMeta im = is.getItemMeta();
		im.setLocalizedName("§6微笑的合成台");
		is.setItemMeta(im);
		MzTech.items.put("微笑的合成台",is);
		
		ShapelessRecipe sr=new ShapelessRecipe(new NamespacedKey(MzTech.instance,"SmilingCraftingTable"),is);
		try
		{	
			sr.addIngredient(Material.WORKBENCH);
		}
		catch(Error e)
		{	
			sr.addIngredient(Enum.valueOf(Material.class,"CRAFTING_TABLE"));
		}
		sr.addIngredient(Material.DROPPER);
		WorkBenchShapelessRecipe.add("微笑的合成台",sr);
		CraftGuide.addCraftTable("基础机器",WorkBenchShapelessRecipe.class,"微笑的合成台");
	}
	@EventHandler
	void onInventoryClose(InventoryCloseEvent event)
	{	
		InventoryHolder holder = event.getInventory().getHolder();
		if(holder instanceof Dropper)
		{	
			if("微笑的合成台".equals(MzTech.isMachine(((Dropper)holder).getLocation())))
			{	
				event.getPlayer().sendMessage(smilingCraftingTable+"§eShift+右键以合成物品");
			}
		}
	}
	@SuppressWarnings("deprecation")
	@EventHandler
	void onClickMachine(ClickMachineEvent event)
	{	
		if(event.machine.equals("微笑的合成台")&&event.leftClick==false)
		{	
			ItemStack mainHand=event.player.getItemInHand();
			ItemStack offHand=event.player.getInventory().getItemInOffHand();
			if(event.player.isSneaking()&&(mainHand==null||mainHand.getType()==Material.AIR||!mainHand.getType().isBlock())&&(offHand==null||offHand.getType()==Material.AIR||!offHand.getType().isBlock()))
			{	
				event.setCancelled(true);
				Dropper dropper = ((Dropper)event.block.getState());
				Inventory inv = dropper.getInventory();
				ItemStack[] contents = inv.getContents();
				
				
				for(int i=0;i<2;i++)
				{	
					if(contents[0]==null&&contents[1]==null&&contents[2]==null)
					{	
						contents[0]=contents[3];
						contents[1]=contents[4];
						contents[2]=contents[5];
						contents[3]=contents[6];
						contents[4]=contents[7];
						contents[5]=contents[8];
						contents[6]=null;
						contents[7]=null;
						contents[8]=null;
					}
				}
				for(int i=0;i<2;i++)
				{	
					if(contents[0]==null&&contents[3]==null&&contents[6]==null)
					{	
						contents[0]=contents[1];
						contents[3]=contents[4];
						contents[6]=contents[7];
						contents[1]=contents[2];
						contents[4]=contents[5];
						contents[7]=contents[8];
						contents[2]=null;
						contents[5]=null;
						contents[8]=null;
					}
				}
				
				boolean[] crafted= {false};
				SmilingCraftingTable.tables.forEach((s,t)->
				{	
					if(!crafted[0])
					{	
						boolean[] enough= {true};
						int[] i= {0};
						t.raws.forEach((r)->
						{	
							if(r!=null&&MzTech.containItem(contents[i[0]],r.input)==-1)
							{	
								enough[0]=false;
							}
							i[0]++;
						});
						if(enough[0])
						{	
							inv.clear();
							ItemStack result=new ItemStack(t.result);
							if(t.craftMethod!=null)
							{	
								t.craftMethod.accept(contents,result);
							}
							i[0] = 0 ;
							t.raws.forEach(r->
							{	
								if(r!=null)
								{	
									MzTech.removeItem(contents[i[0]],r.input);
									if(r.output != null)
									{	
										if(contents[i[0]].getAmount()==0||contents[i[0]].getType()==Material.AIR)
										{	
											contents[i[0]]=r.output.clone();
										}
										else
										{	
											inv.addItem(r.output);
											for(int j=0;j<r.output.getAmount();j++)
												dropper.drop();
										}
									}
								}
								i[0]++;
							});
							inv.addItem(result);
							for(int j=0;j<result.getAmount();j++)
								dropper.drop();
							inv.setContents(contents);
							crafted[0]=true;
						}
					}
				});
				if(crafted[0])
				{	
					switch(new Random().nextInt()%4)
					{	
					case 0:
					case 1:
						event.player.sendMessage(smilingCraftingTable+"§a诶嘿嘿嘿嘿嘿嘿");
						break;
					case 2:
						event.player.sendMessage(smilingCraftingTable.replace("微笑","狂笑")+"§a诶嘿嘿嘿嘿嘿嘿");
						break;
					case 3:
						event.player.sendMessage(smilingCraftingTable.replace("微笑","奸笑")+"§a诶嘿嘿嘿嘿嘿嘿");
						break;
					}
				}
				else
				{	
					event.player.sendMessage(smilingCraftingTable+"§4放的什么玩意");
				}
			}
		}
	}
	
	public static List<List<ItemStack>> getRaws(String name)
	{	
		List<List<ItemStack>> rl=new ArrayList<>();
		tables.get(name).raws.forEach((e)->rl.add(e.input));
		return rl;
	}
	
	public static void add(String name,ItemStack result,BiConsumer<? super ItemStack[],? super ItemStack> craftMethod,Raw... raws)
	{	
		tables.put(name,new Table(result,Lists.newArrayList(raws),craftMethod));
	}
	
	public static ItemStack getResult(String name)
	{	
		return tables.get(name).result;
	}
	public static ItemStack getMachine()
	{	
		return machine;
	}
}
