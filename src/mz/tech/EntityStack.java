package mz.tech;

import java.lang.reflect.Method;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.Logger;
import org.apache.logging.log4j.message.Message;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Chicken;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.scheduler.BukkitRunnable;
import com.google.common.collect.Sets;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import static mz.tech.ReflectionWrapper.*;

/**
 * 实体堆叠模块及实体相关API
 */
public class EntityStack implements Listener
{	
	static boolean spawnLock=false;
	static Method nmsEntityB=null;
	static Class<?> entityTypes=null;
	static Method entityTypeF=null;
	static Method entitygetEntityType=null;
	static
	{	
		try
		{	
			nmsEntityB=ReflectionWrapper.getNMSClass("EntityTypes").getMethod("b",ReflectionWrapper.getNMSClass("Entity"));
		}
		catch(Exception e)
		{	
			entityTypes=ReflectionWrapper.getNMSClass("EntityTypes");
			entityTypeF=ReflectionWrapper.getMethod(entityTypes,"f");
			entitygetEntityType=ReflectionWrapper.getMethodParent(ReflectionWrapper.getNMSClass("Entity"),"getEntityType");
		}
	}
	
	public EntityStack()
	{	
		((Logger)LogManager.getRootLogger()).addFilter(new Filter()
				{	
					@Override
					public State getState()
					{	
						return State.STARTED;
					}
					@Override
					public void initialize()
					{	
					}
					@Override
					public boolean isStarted()
					{	
						return true;
					}
					@Override
					public boolean isStopped()
					{	
						return false;
					}
					@Override
					public void start()
					{	
					}
					@Override
					public void stop()
					{	
					}
					@Override
					public Result filter(LogEvent var1)
					{	
						if(var1.getLevel()==Level.WARN&&var1.getMessage().getFormattedMessage().startsWith("Tried to add entity ")&&var1.getMessage().getFormattedMessage().endsWith(" but it was marked as removed already"))
						{	
							return Result.DENY;
						}
						return Result.NEUTRAL;
					}
					@Override
					public Result filter(Logger var1, Level var2, Marker var3, String var4, Object... var5)
					{	
						return Result.NEUTRAL;
					}
					@Override
					public Result filter(Logger var1, Level var2, Marker var3, String var4, Object var5)
					{	
						return Result.NEUTRAL;
					}
					@Override
					public Result filter(Logger var1, Level var2, Marker var3, Object var4, Throwable var5)
					{	
						return Result.NEUTRAL;
					}
					@Override
					public Result filter(Logger var1, Level var2, Marker var3, Message var4, Throwable var5)
					{	
						return Result.NEUTRAL;
					}
					@Override
					public Result filter(Logger var1, Level var2, Marker var3, String var4, Object var5, Object var6)
					{	
						return Result.NEUTRAL;
					}
					@Override
					public Result filter(Logger var1, Level var2, Marker var3, String var4, Object var5, Object var6, Object var7)
					{	
						return Result.NEUTRAL;
					}
					@Override
					public Result filter(Logger var1, Level var2, Marker var3, String var4, Object var5, Object var6, Object var7,
							Object var8)
					{	
						return Result.NEUTRAL;
					}
					@Override
					public Result filter(Logger var1, Level var2, Marker var3, String var4, Object var5, Object var6, Object var7,
							Object var8, Object var9)
					{	
						return Result.NEUTRAL;
					}
					@Override
					public Result filter(Logger var1, Level var2, Marker var3, String var4, Object var5, Object var6, Object var7,
							Object var8, Object var9, Object var10)
					{	
						return Result.NEUTRAL;
					}
					@Override
					public Result filter(Logger var1, Level var2, Marker var3, String var4, Object var5, Object var6, Object var7,
							Object var8, Object var9, Object var10, Object var11)
					{	
						return Result.NEUTRAL;
					}
					@Override
					public Result filter(Logger var1, Level var2, Marker var3, String var4, Object var5, Object var6, Object var7,
							Object var8, Object var9, Object var10, Object var11, Object var12)
					{	
						return Result.NEUTRAL;
					}
					@Override
					public Result filter(Logger var1, Level var2, Marker var3, String var4, Object var5, Object var6, Object var7,
							Object var8, Object var9, Object var10, Object var11, Object var12, Object var13)
					{	
						return Result.NEUTRAL;
					}
					@Override
					public Result filter(Logger var1, Level var2, Marker var3, String var4, Object var5, Object var6, Object var7,
							Object var8, Object var9, Object var10, Object var11, Object var12, Object var13, Object var14)
					{	
						return Result.NEUTRAL;
					}
					@Override
					public Result getOnMatch()
					{	
						return Result.NEUTRAL;
					}
					@Override
					public Result getOnMismatch()
					{	
						return Result.NEUTRAL;
					}
				});
		new BukkitRunnable()
		{	
			public void run()
			{	
				Bukkit.getWorlds().forEach(world->
				{	
					world.getEntitiesByClass(Damageable.class).forEach(entity->
					{	
						if(entity.isDead())
						{	
							return;
						}
						entity.getNearbyEntities(5,5,5).forEach(e->
						{	
							if((!e.isDead())&&e instanceof Damageable&&canStack(entity,(Damageable)e))
							{	
								setEntityNum(entity,getEntityNum(entity)+getEntityNum((Damageable)e));
								e.remove();
							}
						});
					});
				});
			}
		}.runTaskTimer(MzTech.instance,0,600);
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	void onEntitySpawn(EntitySpawnEvent event)
	{	
		if(event.isCancelled()||spawnLock)
		{	
			return;
		}
		if(event.getEntity() instanceof Damageable&&!(event.getEntity() instanceof ArmorStand))
		{	
			event.getEntity().getNearbyEntities(5,5,5).forEach(entity->
			{	
				if((!event.getEntity().isDead())&&(!entity.isDead())&&entity instanceof Damageable&&canStack((Damageable) entity,(Damageable)event.getEntity()))
				{	
					setEntityNum((Damageable) entity,getEntityNum((Damageable) entity)+getEntityNum((Damageable)event.getEntity()));
					event.getEntity().remove();
				}
			});
			if(!event.getEntity().isDead())
			{	
				Damageable entity=(Damageable) event.getEntity();
				setEntityNum(entity,1);
			}
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	Damageable onEntityDamageByEntity(EntityDamageByEntityEvent event)
	{	
		if(event.isCancelled())
		{	
			return null;
		}
		if(event.getCause()!=null)switch(event.getCause())
		{	
		case ENTITY_EXPLOSION:
		case ENTITY_SWEEP_ATTACK:
		case FALL:
		case FALLING_BLOCK:
		case FIRE_TICK:
		case VOID:
		case FLY_INTO_WALL:
		case BLOCK_EXPLOSION:
		case CONTACT:
		case THORNS:
			return null;
		default:
			break;
		}
		if(event.getDamager() instanceof Damageable)
		{	
			if(getEntityNum((Damageable) event.getDamager())>1)
			{	
				event.setDamage(event.getFinalDamage()*getEntityNum((Damageable) event.getDamager()));
			}
		}
		else if(event.getDamager() instanceof Projectile)
		{	
			ProjectileSource entity=((Projectile) event.getDamager()).getShooter();
			if(entity instanceof Damageable)
			{	
				if(getEntityNum((Damageable) entity)>1)
				{	
					event.setDamage(event.getFinalDamage()*getEntityNum((Damageable)entity));
				}
			}
		}
		if(event.getEntity() instanceof Damageable&&!(event.getEntity() instanceof Player))
		{	
			Damageable entity=(Damageable)event.getEntity();
			int num=getEntityNum(entity);
			if(num>1)
			{	
				setEntityNum(entity,1);
				spawnLock=true;
				setEntityNum(cloneEntity(entity),num-1);
				spawnLock=false;
				return entity;
			}
		}
		return null;
	}
	@EventHandler
	void onEntityDeath(EntityDeathEvent event)
	{	
		if(event.getEntity() instanceof Damageable&&!(event.getEntity() instanceof Player))
		{	
			if(getEntityNum(event.getEntity())>1)
			{	
				event.setDroppedExp(event.getDroppedExp()*getEntityNum(event.getEntity()));
				event.getDrops().forEach(itemStack->
				{	
					int[] i= {1};
					int num=getEntityNum(event.getEntity());
					Location place=event.getEntity().getLocation();
					World world=place.getWorld();
					new BukkitRunnable()
					{	
						public void run()
						{	
							if(i[0]<num)
							{	
								world.dropItemNaturally(place,itemStack);
							}
							else
							{	
								this.cancel();
							}
							i[0]++;
						}
					}.runTaskTimer(MzTech.instance,0,1);
				});
			}
		}
	}
	@SuppressWarnings("deprecation")
	@EventHandler(priority = EventPriority.HIGHEST)
	void onEntityPickupItem(EntityPickupItemEvent event)
	{	
		if(event.isCancelled())
		{	
			return;
		}
		onEntityDamageByEntity(new EntityDamageByEntityEvent(null,event.getEntity(),null,0));
	}
	@SuppressWarnings("deprecation")
	@EventHandler(priority = EventPriority.HIGHEST)
	void onPlayerInteractAtEntity(PlayerInteractAtEntityEvent event)
	{	
		if(event.isCancelled())
		{	
			return;
		}
		Damageable entity=onEntityDamageByEntity(new EntityDamageByEntityEvent(null,event.getRightClicked(),null,0));
		if(entity!=null) new BukkitRunnable()
		{	
			public void run()
			{	
				onEntitySpawn(new EntitySpawnEvent(entity));
			}
		}.runTask(MzTech.instance);
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	void onItemSpawn(ItemSpawnEvent event)
	{	
		if(event.isCancelled())
		{	
			return;
		}
		StackTraceElement[] s=new Throwable().getStackTrace();
		for(StackTraceElement i : s)
		{	
			if(i.getClassName().equals(ReflectionWrapper.getNMSClass("EntityChicken").getName()))
			{	
				Chicken[] chicken= {null};
				event.getEntity().getWorld().getEntitiesByClass(Chicken.class).forEach(chickens->
				{	
					if(chickens instanceof Chicken&&(chicken[0]==null||chickens.getLocation().distance(event.getEntity().getLocation())<chicken[0].getLocation().distance(event.getEntity().getLocation())))
					{	
						chicken[0]=(Chicken) chickens;
					}
				});
				if(chicken[0]!=null&&event.getEntity().getItemStack().getAmount()==1)
				{	
					int num=getEntityNum(chicken[0]);
					if(num<=Material.EGG.getMaxStackSize())
					{	
						event.getEntity().setItemStack(new ItemStack(Material.EGG,num));
					}
					else
					{	
						event.getEntity().setItemStack(new ItemStack(Material.EGG,Material.EGG.getMaxStackSize()));
						for(num-=Material.EGG.getMaxStackSize();num>Material.EGG.getMaxStackSize();num-=Material.EGG.getMaxStackSize())
						{	
							chicken[0].getWorld().dropItemNaturally(chicken[0].getLocation(),new ItemStack(Material.EGG,Material.EGG.getMaxStackSize()));
						}
						chicken[0].getWorld().dropItemNaturally(chicken[0].getLocation(),new ItemStack(Material.EGG,num));
					}
				}
				break;
			}
		}
	}
	
	/**
	 * 在原来的位置克隆一个满血的实体
	 * @param entity 原来的实体
	 * @return 克隆的实体
	 */
	public static Damageable cloneEntity(Damageable entity)
	{	
		Damageable copy=(Damageable) entity.getWorld().spawnEntity(entity.getLocation(),entity.getType());
		
		copy.setCustomName(entity.getCustomName());
		copy.setCustomNameVisible(entity.isCustomNameVisible());
		
//		JsonParser parser=new JsonParser();
//		JsonObject jsonCopy=parser.parse(NBT.getEntityNBT(copy)).getAsJsonObject();
//		removeUselessNBT(parser.parse(NBT.getEntityNBT(copy)).getAsJsonObject()).entrySet().forEach(a->
//		{	
//			jsonCopy.remove(a.getKey());
//		});
//		NBT.setEntityNBT(copy,jsonCopy.toString(),false);
//		NBT.setEntityNBT(copy,removeUselessNBT(parser.parse(NBT.getEntityNBT(entity)).getAsJsonObject()).toString(),true);
		NBT.setEntityNBT(copy,NBT.getEntityNBT(entity),true);
		
		copy.setVelocity(entity.getVelocity());
		return copy;
	}
	
	/**
	 * 移除不能比较的实体nbt
	 * @param nbt 实体nbt
	 * @return 返回参数
	 */
	public static JsonObject removeUselessNBT(JsonObject nbt)
	{	
		nbt.remove("Pos");
		nbt.remove("Motion");
		nbt.remove("Rotation");
		nbt.remove("PortalCooldown");
		nbt.remove("UUIDLeast");
		nbt.remove("UUID");
		nbt.remove("UUIDMost");
		nbt.remove("AbsorptionAmount");
		nbt.remove("HurtTime");
		nbt.remove("HurtByTimestamp");
		nbt.remove("SleepingX");
		nbt.remove("SleepingY");
		nbt.remove("SleepingZ");
		nbt.remove("LeftHanded");
		nbt.remove("LoveCause");
		nbt.remove("LoveCauseLeast");
		nbt.remove("LoveCauseMost");
		nbt.remove("FallDistance");
		nbt.remove("Spigot.ticksLived");
		nbt.remove("Tags");
		nbt.remove("Air");
		nbt.remove("OnGround");
		nbt.remove("EggLayTime");
		nbt.remove("Strength");
		nbt.remove("TravelPosX");
		nbt.remove("TravelPosY");
		nbt.remove("TravelPosZ");
		nbt.remove("HomePosX");
		nbt.remove("HomePosY");
		nbt.remove("HomePosZ");
		nbt.remove("APX");
		nbt.remove("APY");
		nbt.remove("APZ");
		nbt.remove("Bukkit.updateLevel");
		nbt.remove("Paper.SpawnReason");
		nbt.remove("Paper.Origin");
		nbt.remove("Bukkit.MaxDomestication");
		nbt.remove("Bukkit.Aware");
		nbt.remove("Spigot.ticksLived");
		
		nbt.remove("Attributes");
		nbt.remove("Fire");
		nbt.remove("CustomName");
		nbt.remove("CustomNameVisible");
		return nbt;
	}
	
	/*
	 * 检测两个生物是否能堆叠
	 * @param a 第一个生物
	 * @param b 第二个生物
	 * @return 是否能堆叠
	 */
	public static boolean canStack(Damageable a,Damageable b)
	{	
		if(a.getType()!=b.getType()||a.getType()==EntityType.VILLAGER||a.getType()==EntityType.ARMOR_STAND)
		{	
			return false;
		}
		if(a.getPassengers().size()>0||b.getPassengers().size()>0)
		{	
			return false;
		}
		String nameA=a.getCustomName();
		String nameB=a.getCustomName();
		if(nameA==null||nameA.startsWith("§无"))
		{	
			nameA="";
		}
		if(nameB==null||nameB.startsWith("§无"))
		{	
			nameB="";
		}
		if(!nameA.equals(nameB))
		{	
			return false;
		}
		JsonParser p=new JsonParser();
		JsonObject ao=p.parse(NBT.getEntityNBT(a)).getAsJsonObject();
		JsonObject bo=p.parse(NBT.getEntityNBT(b)).getAsJsonObject();
		removeUselessNBT(ao);
		removeUselessNBT(bo);
		if(ao.toString().equals(bo.toString()))
		{	
			return true;
		}
		else
		{	
			//Bukkit.getLogger().info("\r\n"+ao+"\r\n"+bo);
			return false;
		}
	}
	
	/*
	 * 得到实体的数量
	 * @return 数量
	 */
	public static int getEntityNum(Damageable entity)
	{	
		int[] ri= {1};
		entity.getScoreboardTags().forEach(tag->
		{	
			if(tag.startsWith("MzTech.num."))
			{	
				ri[0]=Integer.valueOf(tag.substring("MzTech.num.".length()));
			}
		});
		return ri[0];
	}
	
	/*
	 * 设置实体的数量
	 * @param num 数量
	 */
	public static void setEntityNum(Damageable entity,int num)
	{	
		if(num<=0)
		{	
			entity.remove();
		}
		else
		{	
			Sets.newConcurrentHashSet(entity.getScoreboardTags()).forEach(tag->
			{	
				if(tag.startsWith("MzTech.num."))
				{	
					entity.removeScoreboardTag(tag);
				}
			});
			if(num>1)
			{	
				entity.addScoreboardTag("MzTech.num."+num);
				entity.setCustomName("§无"+getEntityName(entity)+" x"+num);
			}
			else
			{	
				entity.setCustomName("§无"+getEntityName(entity));
			}
			entity.setCustomNameVisible(true);
		}
	}
	
	/*
	 * 得到实体翻译前的名称
	 */
	public static String getEntityRawUnlocalizedName(Entity entity)
	{	
		if(nmsEntityB != null)
		{	
			return "entity."+invokeStaticMethod(nmsEntityB,new Object[] {invokeMethod(getMethodParent(entity.getClass(),"getHandle"),entity)})+".name";
		}
		else
		{	
			Object nmsEntity=invokeMethod(getMethodParent(entity.getClass(),"getHandle"),entity);
			Object entityType=invokeMethod(entitygetEntityType,nmsEntity);
			return invokeMethod(entityTypeF,entityType)+".name";
		}
	}
	/*
	 * 得到实体翻译前的名称并还原旧版（统一版本）
	 */
	public static String getEntityUnlocalizedName(Entity entity)
	{	
		String raw=getEntityRawUnlocalizedName(entity);
		if(raw.startsWith("entity.minecraft."))
		{	
			raw="entity."+raw.substring("entity.minecraft.".length());
		}
		char[] chars=raw.toCharArray();
		chars[raw.indexOf(".")+1]=Character.toUpperCase(chars[raw.indexOf(".")+1]);
		for(int i=0;i<chars.length;i++)
		{	
			if(chars[i]=='_')
			{	
				chars[i+1]=Character.toUpperCase(chars[i+1]);
			}
		}
		raw=new String(chars).replace("_","");
		return raw;
	}
	
	/*
	 * 得到实体的显示名
	 */
	public static String getEntityName(Damageable entity)
	{	
		if(entity.getCustomName()!=null)
		{	
			if(entity.getCustomName().startsWith("§无"))
			{	
				if(entity.getCustomName().contains(" x"))
				{	
					return entity.getCustomName().substring("§无".length(),entity.getCustomName().lastIndexOf(" x"));
				}
				else
				{	
					return entity.getCustomName().substring("§无".length());
				}
			}
			else
			{	
				return entity.getCustomName();
			}
		}
		else
		{	
			try
			{	
				return DropsName.lang.getOrDefault(getEntityUnlocalizedName(entity),entity.getName());
			}
			catch(Exception e)
			{	
				MzTech.throwRuntime(e);
				return entity.getName();
			}
		}
	}
}
