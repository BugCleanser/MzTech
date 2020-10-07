package mz.tech;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;

import com.google.common.collect.Maps;

/**
 * 附魔API
 */
public class Enchants implements Listener
{	
	static Map<Object,Enchantment> byKey = null;
	static Map<Integer, Enchantment> byId = null;
	static Map<String, Enchantment> byName = null;
	
	@SuppressWarnings("unchecked")
	public Enchants()
	{	
		try
		{	
			Field acceptingNew = Class.forName("org.bukkit.enchantments.Enchantment", true, Bukkit.getPluginManager().getPlugin("ProtocolLib").getClass().getClassLoader()).getDeclaredField("acceptingNew");
			acceptingNew.setAccessible(true);
			acceptingNew.setBoolean(null,true);
			
			Field field= Class.forName("org.bukkit.enchantments.Enchantment", true, Bukkit.getPluginManager().getPlugin("ProtocolLib").getClass().getClassLoader()).getDeclaredField("byName");
			field.setAccessible(true);
			byName=(Map<String, Enchantment>) field.get(null);
			try
			{	
				field= Class.forName("org.bukkit.enchantments.Enchantment", true, Bukkit.getPluginManager().getPlugin("ProtocolLib").getClass().getClassLoader()).getDeclaredField("byId");
				field.setAccessible(true);
				byId=(Map<Integer, Enchantment>) field.get(null);
			}
			catch(Exception e)
			{	
				field= Class.forName("org.bukkit.enchantments.Enchantment", true, Bukkit.getPluginManager().getPlugin("ProtocolLib").getClass().getClassLoader()).getDeclaredField("byKey");
				field.setAccessible(true);
				byKey=(Map<Object,Enchantment>) field.get(null);
			}
		}
		catch (Exception e)
		{	
			MzTech.throwRuntime(e);
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	void onPrepareAnvil(PrepareAnvilEvent event)
	{	
		if(event.getResult()!=null&&event.getResult().getType()!=Material.AIR)
		{	
			setEnchants(event.getResult(),enchantsForItem(mergeEnchants(getItemEnchants(event.getInventory().getItem(0)),getItemEnchants(event.getInventory().getItem(1))),event.getResult()));
		}
	}
	
	/**
	 * 筛选可以附到指定物品的属性
	 */
	public static Map<Enchantment, Integer> enchantsForItem(Map<Enchantment, Integer> enchants,ItemStack item)
	{	
		Map<Enchantment, Integer> finalEnchants=Maps.newHashMap(enchants);
		
		enchants.forEach((e,l)->
		{	
			if(!e.canEnchantItem(item))
			{	
				finalEnchants.remove(e);
			}
		});
		
		return finalEnchants;
	}
	/**
	 * 合并两个附魔列表
	 * 像铁砧一样
	 */
	public static Map<Enchantment, Integer> mergeEnchants(Map<Enchantment, Integer> enchants1,Map<Enchantment, Integer> enchants2)
	{	
		Map<Enchantment, Integer> rm=Maps.newHashMap(enchants1);
		enchants2.forEach((e,l)->
		{	
			int befor=rm.getOrDefault(e,0);
			if(befor==l)
			{	
				befor=l+1;
			}
			else
			{	
				befor=befor>l?befor:l;
			}
			rm.put(e,befor>e.getMaxLevel()?e.getMaxLevel():befor);
		});
		return rm;
	}
	/**
	 * 为物品设置一个列表的附魔
	 * 考虑附魔上限和附魔书
	 */
	public static void setEnchants(ItemStack is,Map<Enchantment, Integer> enchants)
	{	
		ItemMeta im = is.getItemMeta();
		if(is.getType()==Material.ENCHANTED_BOOK)
		{	
			enchants.forEach((e,l)->
			{	
				((EnchantmentStorageMeta)im).addStoredEnchant(e,l,true);
			});
		}
		else
		{	
			enchants.forEach((e,l)->
			{	
				im.addEnchant(e,l,true);
			});
		}
		is.setItemMeta(im);
	}
	/**
	 * 设置一个物品堆的附魔
	 * 考虑附魔书和普通物品
	 */
	public static void setEnchant(ItemStack is,Enchantment enchant,int level)
	{	
		if(is==null|is.getType()==Material.AIR)
		{	
			return;
		}
		ItemMeta im = is.getItemMeta();
		if(is.getType()==Material.ENCHANTED_BOOK)
		{	
			if(level==0)
			{	
				((EnchantmentStorageMeta)im).removeStoredEnchant(enchant);
			}
			else
			{	
				((EnchantmentStorageMeta)im).addStoredEnchant(enchant,level,true);
			}
		}
		else
		{	
			if(level==0)
			{	
				im.removeEnchant(enchant);
			}
			else
			{	
				im.addEnchant(enchant,level,true);
			}
		}
		is.setItemMeta(im);
	}
	/**
	 * 注销附魔
	 * 高版本无法使用
	 */
	@Deprecated
	public static void unregEnchant(int id)
	{	
		Enchantment enchant = byId.get(id);
		if(enchant!=null)
		{	
			byId.remove(id);
			Maps.newHashMap(byName).forEach((n,e)->
			{	
				if(e==enchant)
				{	
					byName.remove(n);
				}
			});
		}
	}
	/**
	 * 注销附魔
	 */
	public static void unregEnchant(String name)
	{	
		Enchantment enchant = byName.get(name);
		if(enchant!=null)
		{	
			byName.remove(name);
			if(byId!=null)
			{	
				Maps.newHashMap(byId).forEach((i,e)->
				{	
					if(e==enchant)
					{	
						byId.remove(i);
					}
				});
			}
			if(byKey!=null)
			{	
				Maps.newHashMap(byKey).forEach((k,e)->
				{	
					if(e==enchant)
					{	
						byKey.remove(k);
					}
				});
			}
		}
	}
	/**
	 * 注销附魔
	 * 低版本无法使用
	 */
	@Deprecated
	public static void unregEnchant(NamespacedKey key)
	{	
		Enchantment enchant = byKey.get(key);
		if(enchant!=null)
		{	
			byKey.remove(key);
			if(byId!=null)
			{	
				Maps.newHashMap(byId).forEach((i,e)->
				{	
					if(e==enchant)
					{	
						byId.remove(i);
					}
				});
			}
			Maps.newHashMap(byName).forEach((n,e)->
			{	
				if(e==enchant)
				{	
					byName.remove(n);
				}
			});
		}
	}
	/**
	 * 注册一个附魔
	 * 低版本使用id
	 * 高版本自动使用key
	 */
	public static Enchantment regEnchant(int id,NamespacedKey namespacedKey, String name,boolean treasure,boolean cursed,int maxLevel,EnchantmentTarget target)
	{	
		unregEnchant(name);
		if(Enchantment.getByName(name)==null)
		{	
			Enchantment enchant=
			byId==null?
			new Enchantment(namespacedKey)
			{	
				@Override
				public boolean isTreasure()
				{	
					return treasure;
				}
				@Override
				public boolean isCursed()
				{	
					return cursed;
				}
				@Override
				public int getStartLevel()
				{	
					return 0;
				}
				@Override
				public String getName()
				{	
					return name;
				}
				@Override
				public int getMaxLevel()
				{	
					return maxLevel;
				}
				@Override
				public EnchantmentTarget getItemTarget()
				{	
					return target;
				}
				@Override
				public boolean conflictsWith(Enchantment arg0)
				{	
					return false;
				}
				@Override
				public boolean canEnchantItem(ItemStack arg0)
				{	
					return true;
				}
			}:
			new Enchantment(id)
			{	
				@Override
				public boolean isTreasure()
				{	
					return treasure;
				}
				@Override
				public boolean isCursed()
				{	
					return cursed;
				}
				@Override
				public int getStartLevel()
				{	
					return 0;
				}
				@Override
				public String getName()
				{	
					return name;
				}
				@Override
				public int getMaxLevel()
				{	
					return maxLevel;
				}
				@Override
				public EnchantmentTarget getItemTarget()
				{	
					return target;
				}
				@Override
				public boolean conflictsWith(Enchantment arg0)
				{	
					return false;
				}
				@Override
				public boolean canEnchantItem(ItemStack arg0)
				{	
					return true;
				}
			};
			byName.put(name,enchant);
			if(byKey!=null)
			{	
				byKey.put(namespacedKey,enchant);
			}
			if(byId!=null)
			{	
				byId.put(id,enchant);
			}
			return enchant;
		}
		else
		{	
			return null;
		}
	}
	
	/**
	 * 读取罗马数字
	 */
	@Deprecated
	public static int romanToInt(String s)
	{	
	    Map<Character, Integer> lookup = new HashMap<>();
	    lookup.put('M', 1000);
	    lookup.put('D', 500);
	    lookup.put('C', 100);
	    lookup.put('L', 50);
	    lookup.put('X', 10);
	    lookup.put('V', 5);
	    lookup.put('I', 1);
	    
	    int result = 0;
	    int current = 0;
	    int next = 0;
	    
	    for (int i = 0; i < s.length() - 1; i++)
	    {	
	        current = lookup.get(s.charAt(i));
	        if(s.charAt(i + 1)=='m')
	        {	
	        	current*=1000;
	        	i++;
	        }
	        next = lookup.get(s.charAt(i + 1));
	        if(s.charAt(i + 2)=='m')
	        {	
	        	next*=1000;
	        	i++;
	        }
	        
	        if (next > current)
	        {	
	            result -= current;
	        }
	        else
	        {	
	            result += current;
	        }
	    }
	    result += lookup.get(s.charAt(s.length() - 1));
	    return result;
	}
	/**
	 * 生成罗马数字
	 */
	public static String intToRoman(int number)
	{	
		if(number<=0||number>6000000)
		{	
			return ((Integer)number).toString();
		}
	    String rNumber = "";
	    int[] aArray = {	1000000,900000,500000,	400000,	100000,	90000, 	50000,	40000, 	10000,	9000 , 	5000,	4000, 	1000, 900, 	500, 400, 	100, 90, 	50,		40, 	10, 9, 		5, 		4, 		1 };
	    String[] rArray = {	"Mm",	"MmCm",	"Dm",	"CmDm",	"Cm",	"XmCm",	"Lm",	"XmLm",	"Xm",	"MXm",	"Vm",	"MVm",	"M", 	"CM", 	"D", "CD", 	"C", "XC", 	"L", 	"XL", 	"X","IX", 	"V", 	"IV", 	"I" };
	    if (number < 1)
	    {	
	      rNumber = "ERROR";
	    }
	    else
	    {
	    	for (int i = 0; i < aArray.length; i++)
	    	{	
	    		while (number >= aArray[i])
	    		{	
		        	rNumber += rArray[i];
		        	number -= aArray[i];
	    		}
	    	}
	    }
	    return rNumber;
	}
	/**
	 * 判断物品或附魔书是否有附魔
	 */
	public static boolean hasEnchants(ItemStack is)
	{	
		return is.getType()==Material.ENCHANTED_BOOK?((EnchantmentStorageMeta)is.getItemMeta()).hasStoredEnchants():is.getItemMeta().hasEnchants();
	}
	/**
	 * 得到物品的附魔
	 * 考虑附魔书和普通物品
	 */
	public static Map<Enchantment,Integer> getItemEnchants(ItemStack is)
	{	
		if(is==null||is.getType()==Material.AIR)
		{	
			return new HashMap<>();
		}
		return is.getType()==Material.ENCHANTED_BOOK?((EnchantmentStorageMeta)is.getItemMeta()).getStoredEnchants():is.getEnchantments();
	}
	/**
	 * 删除物品的所有附魔
	 * 考虑附魔书和普通物品
	 */
	public static void removeEnchants(ItemStack is)
	{	
		if(is.getType()==Material.ENCHANTED_BOOK)
		{	
			EnchantmentStorageMeta im=(EnchantmentStorageMeta)is.getItemMeta();
			Maps.newHashMap(im.getStoredEnchants()).forEach((e,l)->
			{	
				im.removeStoredEnchant(e);
			});
			is.setItemMeta(im);
		}
		else
		{	
			ItemMeta im=is.getItemMeta();
			Maps.newHashMap(im.getEnchants()).forEach((e,l)->
			{	
				im.removeEnchant(e);
			});
			is.setItemMeta(im);
		}
	}
	/**
	 * 判断一个物品的附魔是否显示
	 * 考虑附魔书和普通物品
	 */
	public static boolean isEnchantsHiden(ItemStack is)
	{	
		return is.getType()==Material.ENCHANTED_BOOK?is.getItemMeta().hasItemFlag(ItemFlag.HIDE_POTION_EFFECTS):is.getItemMeta().hasItemFlag(ItemFlag.HIDE_ENCHANTS);
	}
	/**
	 * 隐藏物品的附魔
	 * 考虑附魔书和普通物品
	 */
	public static void hideEnchants(ItemStack is)
	{	
		ItemMeta im = is.getItemMeta();
		if(is.getType()==Material.ENCHANTED_BOOK)
		{	
			im.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
		}
		else
		{	
			im.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		}
		is.setItemMeta(im);
	}
	/**
	 * 隐藏一个物品的附魔并将其转换为lore
	 * @param 物品
	 * @return 参数的物品（不是副本）
	 */
	public static ItemStack enchantToLore(ItemStack is)
	{	
		if(isEnchantsHiden(is)||!(hasEnchants(is)))
		{	
			return is;
		}
		ItemMeta im = is.getItemMeta();
		List<String> lore = im.hasLore()?im.getLore():new ArrayList<>();
		getItemEnchants(is).forEach((enchant,level)->
		{	
			level=(int)(short)(int)level;
			String thisLore="§临"+(enchant.isCursed()?"§4":"§7");
			switch(enchant.getName())
			{	
			case "PROTECTION_ENVIRONMENTAL":
				thisLore+="保护";
				break;
			case "PROTECTION_FIRE":
				thisLore+="火焰保护";
				break;
			case "PROTECTION_FALL":
				thisLore+="摔落保护";
				break;
			case "PROTECTION_EXPLOSIONS":
				thisLore+="爆炸保护";
				break;
			case "PROTECTION_PROJECTILE":
				thisLore+="投掷物保护";
				break;
			case "OXYGEN":
				thisLore+="水下呼吸";
				break;
			case "WATER_WORKER":
				thisLore+="水下速掘";
				break;
			case "THORNS":
				thisLore+="荆棘";
				break;
			case "DEPTH_STRIDER":
				thisLore+="深海探索者";
				break;
			case "FROST_WALKER":
				thisLore+="冰霜行者";
				break;
			case "BINDING_CURSE":
				thisLore+="绑定诅咒";
				break;
			case "DAMAGE_ALL":
				thisLore+="锋利";
				break;
			case "DAMAGE_UNDEAD":
				thisLore+="亡灵杀手";
				break;
			case "DAMAGE_ARTHROPODS":
				thisLore+="节肢杀手";
				break;
			case "KNOCKBACK":
				thisLore+="击退";
				break;
			case "FIRE_ASPECT":
				thisLore+="火焰附加";
				break;
			case "LOOT_BONUS_MOBS":
				thisLore+="抢夺";
				break;
			case "SWEEPING_EDGE":
				thisLore+="横扫之刃";
				break;
			case "DIG_SPEED":
				thisLore+="效率";
				break;
			case "SILK_TOUCH":
				thisLore+="精准采集";
				break;
			case "DURABILITY":
				thisLore+="耐久";
				break;
			case "LOOT_BONUS_BLOCKS":
				thisLore+="时运";
				break;
			case "ARROW_DAMAGE":
				thisLore+="力量";
				break;
			case "ARROW_KNOCKBACK":
				thisLore+="冲击";
				break;
			case "ARROW_FIRE":
				thisLore+="火矢";
				break;
			case "ARROW_INFINITE":
				thisLore+="无限";
				break;
			case "LUCK":
				thisLore+="海之眷顾";
				break;
			case "LURE":
				thisLore+="饵钓";
				break;
			case "LOYALTY":
				thisLore+="忠诚";
				break;
			case "IMPALING":
				thisLore+="穿刺";
				break;
			case "RIPTIDE":
				thisLore+="激流";
				break;
			case "CHANNELING":
				thisLore+="引雷";
				break;
			case "MULTISHOT":
				thisLore+="散射";
				break;
			case "QUICK_CHARGE":
				thisLore+="快速装填";
				break;
			case "PIERCING":
				thisLore+="穿透";
				break;
			case "MENDING":
				thisLore+="经验修补";
				break;
			case "VANISHING_CURSE":
				thisLore+="消失诅咒";
				break;
			case "SOUL_SPEED":
				thisLore+="灵魂疾行";
				break;
			default:
				thisLore+=enchant.getName().toLowerCase();
				break;
			}
			if(enchant.getMaxLevel()>1||level>1)
			{	
				thisLore+=" "+intToRoman(level);
			}
			lore.add(thisLore);
		});
		im.setLore(lore);
		is.setItemMeta(im);
		hideEnchants(is);
		return is;
	}
}
