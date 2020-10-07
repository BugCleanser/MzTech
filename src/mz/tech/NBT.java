package mz.tech;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;

/**
 * NBT相关API
 */
public class NBT
{	
	final static Class<?> nmsClass=ReflectionWrapper.getNMSClass("NBTTagCompound");
	final static Constructor<?> nmsConstructor=ReflectionWrapper.getConstructor(nmsClass);
	
	final static Class<?> CraftItemStack=ReflectionWrapper.getCraftBukkitClass("inventory.CraftItemStack");
	final static Method asNMSCopy=ReflectionWrapper.getMethod(CraftItemStack,"asNMSCopy",ItemStack.class);
	public static Object asNMSCopy(ItemStack is)
	{	
		return ReflectionWrapper.invokeStaticMethod(asNMSCopy,is);
	}

	
	final static Class<?> nmsItemStack=ReflectionWrapper.getNMSClass("ItemStack");
	final static Method hasTag=ReflectionWrapper.getMethod(nmsItemStack,"hasTag");
	public static Boolean hasTag(Object nmsItem)
	{	
		return ReflectionWrapper.invokeMethod(hasTag,nmsItem);
	}
	final static Method getTag=ReflectionWrapper.getMethod(nmsItemStack,"getTag");
	public static Object getTag(Object nmsItem)
	{	
		return ReflectionWrapper.invokeMethod(getTag,nmsItem);
	}
	final static Method setTag=ReflectionWrapper.getMethod(nmsItemStack,"setTag",nmsClass);
	public void setTag(Object nmsItem)
	{	
		ReflectionWrapper.invokeMethod(getTag,nmsItem,this.nmsNBT);
	}
	
	final static Method asBukkitCopy=ReflectionWrapper.getMethod(CraftItemStack,"asBukkitCopy",nmsItemStack);
	public static ItemStack asBukkitCopy(Object nmsItem)
	{	
		return ReflectionWrapper.invokeStaticMethod(asBukkitCopy,nmsItem);
	}
	
	Object nmsNBT;
	public String toString()
	{	
		return nmsNBT.toString();
	}
	
	public NBT()
	{	
		this.nmsNBT=ReflectionWrapper.newInstance(nmsConstructor);
	}
	public NBT(ItemStack is)
	{	
		Object nmsItem=NBT.asNMSCopy(is);
		this.nmsNBT=NBT.hasTag(nmsItem)?
					NBT.getTag(nmsItem):
					ReflectionWrapper.newInstance(nmsConstructor);
	}
	public ItemStack setToItemStack(ItemStack is)
	{	
		Object nmsItem=NBT.asNMSCopy(is);
		this.setTag(nmsItem);
		return asBukkitCopy(nmsItem);
		
	}
	
	/**
	 * 得到一个实体的nbt
	 * @param entity 实体
	 * @return NBT字符串
	 */
	public static String getEntityNBT(Entity entity)
	{	
		try
		{	
			String CraftBukkitPackage=Bukkit.getServer().getClass().getPackage().getName();
			String NMSPackage=CraftBukkitPackage.replace("org.bukkit.craftbukkit", "net.minecraft.server");
			Class<?> CraftEntityClass=Class.forName(CraftBukkitPackage+".entity.CraftEntity");
		    Method getHandle=CraftEntityClass.getDeclaredMethod("getHandle", new Class<?>[0]);
		    getHandle.setAccessible(true);
		    Object NMSEntity=getHandle.invoke(entity,new Object[0]);
		    Class<?> EntityClass=Class.forName(NMSPackage+".Entity");
		    try {
		    Class<?> CommandAbstract=Class.forName(NMSPackage+".CommandAbstract");
		    Method a=CommandAbstract.getDeclaredMethod("a", new Class<?>[] {EntityClass});
		    a.setAccessible(true);
		    Object NBTComponentObj=a.invoke(null, new Object[] {NMSEntity});
		    Class<?> NBTComponentClass=NBTComponentObj.getClass();
		    Method toString=NBTComponentClass.getDeclaredMethod("toString", new Class<?>[0]);
		    toString.setAccessible(true);
		    String nbt=(String)toString.invoke(NBTComponentObj, new Object[0]);
		    return nbt;
		    }catch(Throwable e) {
		    	Class<?> CommandDataAccessorEntityClass=Class.forName(NMSPackage+".CommandDataAccessorEntity");
		    	Constructor<?> tc=CommandDataAccessorEntityClass.getDeclaredConstructor(new Class<?>[] {EntityClass});
		    	tc.setAccessible(true);
		    	Object DataAccessor=tc.newInstance(NMSEntity);
		    	Method a=DataAccessor.getClass().getDeclaredMethod("a", new Class<?>[0]);
		        a.setAccessible(true);
		        Object NBTCom=a.invoke(DataAccessor, new Object[0]);
		        Class<?> NBTComponentClass=NBTCom.getClass();
			    Method toString=NBTComponentClass.getDeclaredMethod("toString", new Class<?>[0]);
			    toString.setAccessible(true);
			    String nbt=(String)toString.invoke(NBTCom, new Object[0]);
			    return nbt;
		    }
		}
		catch(Exception e)
		{	
			MzTech.throwRuntime(e);
			return null;
		}
	}
	/**
	 * 添加实体的nbt
	 * 覆盖已有NBT
	 * @param entity 实体
	 * @param newNBT 新的NBT
	 * @param combine 是否保留新NBT没有的标签
	 */
	public static void setEntityNBT(Entity entity,String newNBT,boolean combine)
	{	
		try
		{	
	        String CraftBukkitPackage=Bukkit.getServer().getClass().getPackage().getName();
	        String NMSPackage=CraftBukkitPackage.replace("org.bukkit.craftbukkit", "net.minecraft.server");
	        Class<?> CraftEntityClass=Class.forName(CraftBukkitPackage+".entity.CraftEntity");
	        Method getHandle=CraftEntityClass.getDeclaredMethod("getHandle", new Class<?>[0]);
	        getHandle.setAccessible(true);
	        Object NMSEntity=getHandle.invoke(entity, new Object[0]);
	        Class<?> EntityClass=Class.forName(NMSPackage+".Entity");
	        Class<?> MojangsonParser= Class.forName(NMSPackage+".MojangsonParser");
	        Method parse=MojangsonParser.getDeclaredMethod("parse",String.class);
	        parse.setAccessible(true);
	        Object nNBT=parse.invoke(null,newNBT);
	        try
	        {	
	            Class<?> CommandAbstract=Class.forName(NMSPackage+".CommandAbstract");
	            Method a=CommandAbstract.getDeclaredMethod("a", new Class<?>[] {EntityClass});
	            a.setAccessible(true);
	            Object NBTComponentObj=a.invoke(null, new Object[] {NMSEntity});
	            Class<?> NBTComponentClass=NBTComponentObj.getClass();
	            Method setNBT=EntityClass.getDeclaredMethod("f",NBTComponentClass);
	            setNBT.setAccessible(true);
	            if(combine)
	            {	
		            Method combineA=NBTComponentClass.getDeclaredMethod("a",NBTComponentClass);
		            combineA.setAccessible(true);
		            combineA.invoke(NBTComponentObj,nNBT);
	            }
	            setNBT.invoke(NMSEntity,NBTComponentObj);
	        }
	        catch(Throwable e)
	        {
	            Class<?> CommandDataAccessorEntityClass=Class.forName(NMSPackage+".CommandDataAccessorEntity");
	            Constructor<?> tc=CommandDataAccessorEntityClass.getDeclaredConstructor(new Class<?>[] {EntityClass});
	            tc.setAccessible(true);
	            Object DataAccessor=tc.newInstance(NMSEntity);
	            Method a=DataAccessor.getClass().getDeclaredMethod("a", new Class<?>[0]);
	            a.setAccessible(true);
	            Object NBTCom=a.invoke(DataAccessor, new Object[0]);
	            Class<?> NBTComponentClass=NBTCom.getClass();
	            Method setNBT=EntityClass.getDeclaredMethod("f",NBTComponentClass);
	            setNBT.setAccessible(true);
	            if(combine)
	            {
		            Method combineA=NBTComponentClass.getDeclaredMethod("a",NBTComponentClass);
		            combineA.setAccessible(true);
		            combineA.invoke(NBTCom,nNBT);
	            }
	            setNBT.invoke(NMSEntity,NBTCom);
	        }
		}
		catch(Exception e)
		{	
			MzTech.throwRuntime(e);
		}
	}
//	public String setNBTTag(String nbt,String key,String value)
//	{	
//		JsonObject o=new JsonParser().parse(nbt).getAsJsonObject();
//		o.remove(key);
//		o.add(property, value);
//		return null;
//	}
}
