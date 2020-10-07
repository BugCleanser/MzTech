package org.bukkit.enchantments;

import org.bukkit.Keyed;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;

public abstract class Enchantment implements Keyed 
{	
    public static final Enchantment PROTECTION_ENVIRONMENTAL = null;
    public static final Enchantment PROTECTION_FIRE = null;
    public static final Enchantment PROTECTION_FALL= null ;
    public static final Enchantment PROTECTION_EXPLOSIONS= null ;
    public static final Enchantment PROTECTION_PROJECTILE = null;
    public static final Enchantment OXYGEN= null ;
    public static final Enchantment WATER_WORKER= null ;
    public static final Enchantment THORNS = null;
    public static final Enchantment DEPTH_STRIDER = null;
    public static final Enchantment FROST_WALKER = null;
    public static final Enchantment BINDING_CURSE = null;
    public static final Enchantment DAMAGE_ALL = null;
    public static final Enchantment DAMAGE_UNDEAD = null;
    public static final Enchantment DAMAGE_ARTHROPODS = null;
    public static final Enchantment KNOCKBACK = null;
    public static final Enchantment FIRE_ASPECT = null;
    public static final Enchantment LOOT_BONUS_MOBS= null ;
    public static final Enchantment SWEEPING_EDGE= null ;
    public static final Enchantment DIG_SPEED = null;
    public static final Enchantment SILK_TOUCH = null;
    public static final Enchantment DURABILITY = null;
    public static final Enchantment LOOT_BONUS_BLOCKS = null;
    public static final Enchantment ARROW_DAMAGE= null ;
    public static final Enchantment ARROW_KNOCKBACK = null;
    public static final Enchantment ARROW_FIRE = null;
    public static final Enchantment ARROW_INFINITE = null;
    public static final Enchantment LUCK = null;
    public static final Enchantment LURE = null;
    public static final Enchantment LOYALTY = null;
    public static final Enchantment IMPALING = null;
    public static final Enchantment RIPTIDE = null;
    public static final Enchantment CHANNELING = null;
    public static final Enchantment MULTISHOT= null ;
    public static final Enchantment QUICK_CHARGE = null;
    public static final Enchantment PIERCING = null;
    public static final Enchantment MENDING = null;
    public static final Enchantment VANISHING_CURSE = null;
    public static final Enchantment SOUL_SPEED = null;
	public Enchantment(int id)
	{	
		
	}
	public Enchantment(NamespacedKey key)
	{	
		
	}
	public static void registerEnchantment(Enchantment enchant)
	{	
		
	}
	public static Enchantment getByName(String name)
	{	
		return null;
	}
	public int getId()
	{	
		return 0;
	}
	public NamespacedKey getKey()
	{	
		return null;
	}
	public static Enchantment[] values()
	{	
		return null;
	}
	public abstract boolean isTreasure();
	public abstract boolean isCursed();
	public abstract int getStartLevel();
	public abstract String getName();
	public abstract int getMaxLevel();
	public abstract EnchantmentTarget getItemTarget();
	public abstract boolean conflictsWith(Enchantment enchant);
	public abstract boolean canEnchantItem(ItemStack is);
}
