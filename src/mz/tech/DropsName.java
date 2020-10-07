package mz.tech;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.regex.Pattern;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ItemMergeEvent;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.google.common.collect.Maps;

import static mz.tech.ReflectionWrapper.*;

/**
 * 掉落物模块及物品名称相关API
 */
public class DropsName implements Listener
{	
    public static DropsName instance;
    {	
        instance = this;
    }
    
    static boolean dropsName=true;
	static String customNameFormat="[§o{Name}§r]";
	static String locNameFormat="{Name}";
	static String defaultNameFormat="{Name}";
	static String countFormat="§r *{Count}";
	static Map<String,String> lang=null;
	static List<String> masks=null;
	
	static Class<?> craftItemStack=ReflectionWrapper.getCraftBukkitClass("inventory.CraftItemStack");
	static Method asNMSCopy=ReflectionWrapper.getMethod(craftItemStack,"asNMSCopy",ItemStack.class);
	static Class<?> nmsItemStack=ReflectionWrapper.getNMSClass("ItemStack");
	static Method getItem=ReflectionWrapper.getMethod(nmsItemStack,"getItem");
	/**
	 * 是否启用生草翻译
	 */
	static boolean growGrassTranslate=false;
	/**
	 * 生草翻译Map
	 * key： MID:子ID
	 * value： 生草翻译名
	 */
	static Map<String,String> growGrassTranslations=new HashMap<>();
	
	@SuppressWarnings("unchecked")
	static void reload(Map<String,Object> map)
	{	
		dropsName			=(Boolean)map.get("dropsName");
		customNameFormat	=(String)map.get("customNameFormat");
		locNameFormat		=(String)map.get("locNameFormat");
		defaultNameFormat	=(String)map.get("defaultNameFormat");
		countFormat			=(String)map.get("countFormat");
		masks				=(List<String>)map.get("masks");
		
		try
		{	
			File path=new File(MzTech.instance.getDataFolder().getPath()+"/lang");
			if(!path.exists())
			{	
				path.mkdirs();
			}
			File langFile=new File(MzTech.instance.getDataFolder().getPath()+"/lang/"+map.get("lang").toString()+".lang");
			if(!langFile.exists())
			{	
				Files.copy(MzTech.instance.getResource("lang/"+map.get("lang").toString()+".lang"),langFile.toPath());
			}
			
			lang=new HashMap<>();
			@SuppressWarnings("resource")
			InputStream res = new FileInputStream(langFile);
			Vector<Byte> bs = new Vector<>();
			int t;
			while((t=res.read())!=-1)
			{	
				bs.add((byte)t);
			}
			byte[] bb=new byte[bs.size()];
			int i;
			for(i=0;i<bb.length;i++)
			{	
				bb[i]=bs.get(i);
			}
			String s = new String(bb,Charset.forName("UTF-8"));
			for(String j : s.replace("\r\n","\n").split("\n"))
			{	
				String[] temp=j.split("=",2);
				lang.put(temp[0],temp[1]);
			}
			res.close();
			if(growGrassTranslate=(Boolean)map.get("growGrassTranslate"))
			{	
				growGrassTranslations=(Map<String, String>) map.get("growGrassTranslations");
				Maps.newHashMap(growGrassTranslations).forEach((id,translation)->
				{	
					growGrassTranslations.remove(id);
					if(!id.contains(":"))
					{	
						id+=":0";
					}
					growGrassTranslations.put(id.toUpperCase(),translation.replace("&","§"));
				});
			}
		}
		catch(Exception e)
		{	
			MzTech.throwRuntime(e);
		}
	}
	
	
	/**
	 * 得到一个物品的翻译前名称
	 * 形如item.?.name
	 * 部分物品不完整（药水刷怪蛋等nbt决定名称的物品）
	 * @param is 物品
	 * @return 未翻译的名称
	 */
	public static String getItemRawUnlocalizedName(ItemStack is)
	{	
		Object itemStack=invokeStaticMethod(asNMSCopy,is);
		Object item=invokeMethod(getItem,itemStack);
		String name;
		try
		{	
			name=invokeMethod(getMethodParent(item.getClass(),"a",nmsItemStack),item,itemStack);
		}
		catch(Exception e)
		{	
			name=invokeMethod(getMethodParent(item.getClass(),"f",nmsItemStack),item,itemStack);
		}
		return name+".name";
	}
	/**
	 * 得到物品翻译前名称并还原旧版（统一版本）
	 */
	public static String getItemUnlocalizedName(ItemStack is)
	{	
		String raw=getItemRawUnlocalizedName(is);
		if(raw.startsWith("item.minecraft."))
		{	
			raw="item."+raw.substring("item.minecraft.".length());
		}
		if(raw.startsWith("block.minecraft."))
		{	
			raw="tile."+raw.substring("block.minecraft.".length());
		}
		char[] chars=raw.toCharArray();
		for(int i=0;i<chars.length;i++)
		{	
			if(chars[i]=='_')
			{	
				chars[i+1]=Character.toUpperCase(chars[i+1]);
			}
		}
		raw=new String(chars).replace("_","");
		return raw.replace("Golden","Gold");
	}
	/**
	 * 得到一个物品的生草名
	 * @param is 物品
	 * @return 生草名，没有则返回null
	 */
	public static String getGrowGrassName(ItemStack is)
	{	
		String grassTranslation = growGrassTranslations.get(is.getType().toString()+":"+(is.getType().getMaxDurability()==0?is.getDurability():0));
		if(grassTranslation!=null)
		{	
			return grassTranslation;
		}
		else
		{	
			return null;
		}
	}
	/**
	 * 得到一个物品的显示名
	 * 按照配置的格式
	 * 客户端的显示效果
	 * 部分物品不完整（药水刷怪蛋等nbt决定名称的物品）
	 * @param is 物品
	 * @return 名称
	 */
	public static String getShowName(ItemStack is)
	{	
		if(is==null)
		{	
			return "空气";
		}
		ItemMeta im = is.getItemMeta();
		if(im.hasDisplayName())
		{	
			return customNameFormat.replace("{Name}",im.getDisplayName());
		}
		if(im.hasLocalizedName())
		{	
			return ("§r"+locNameFormat).replace("{Name}",im.getLocalizedName());
		}
		if(growGrassTranslate)
		{
			String grassTranslation = getGrowGrassName(is);
			if(grassTranslation!=null)
			{	
				return grassTranslation;
			}
		}
		String langName=lang.get(getItemUnlocalizedName(is));
		if(langName!=null)
		{	
			return defaultNameFormat.replace("{Name}",langName);
		}
		return getItemUnlocalizedName(is);
	}
	/**
	 * 得到掉落物的名称
	 * 物品名+数量且依照配置文件的格式
	 * @param is 物品
	 * @return 名称
	 */
	public static String getDropName(ItemStack is)
	{	
		String showName=getShowName(is);
		if(showName!=null)
		{	
			if(is.getAmount()<=1)
			{	
				return showName;
			}
			return showName+countFormat.replace("{Count}",is.getAmount()+"");
		}
		return null;
	}
	@EventHandler(priority = EventPriority.HIGHEST)
	void onItemSpawn(ItemSpawnEvent event)
	{	
		if(dropsName)
		{	
	    	event.getEntity().setCustomNameVisible(true);
	    	String name=getDropName(event.getEntity().getItemStack());
	    	if(name!=null)
	    	{
		    	event.getEntity().setCustomName(name);
		    	
		    	masks.forEach((t)->
		    	{	
			    	if(Pattern.matches(t,event.getEntity().getCustomName()))
			    	{	
				    	event.getEntity().setCustomNameVisible(false);
				    	return;
			    	}
		    	});
	    	}
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	void onItemMerge(ItemMergeEvent event)
	{	
		if(dropsName)
		{	
	    	ItemStack is=event.getEntity().getItemStack().clone();
	    	is.setAmount(is.getAmount()+event.getTarget().getItemStack().getAmount());
	    	String name=getDropName(is);
	    	if(name!=null)
	    	{
		    	event.getTarget().setCustomName(name);
		    	
		    	masks.forEach((t)->
		    	{	
			    	if(Pattern.matches(t,event.getTarget().getCustomName()))
			    	{	
				    	event.getTarget().setCustomNameVisible(false);
				    	return;
			    	}
		    	});
	    	}
		}
	}
}
