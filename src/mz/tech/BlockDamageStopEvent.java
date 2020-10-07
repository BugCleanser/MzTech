package mz.tech;

import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.ListenerOptions;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.injector.GamePhase;
import com.comphenix.protocol.wrappers.EnumWrappers.PlayerDigType;

/**
 * 玩家停止挖掘方块事件
 */
public class BlockDamageStopEvent extends Event implements Listener
{	
    private static final HandlerList handlers = new HandlerList();
    
    /**
     * 玩家
     */
    public Player player;
    /**
     * 方块
     */
    public Block block;

    public BlockDamageStopEvent()
    {	
    	ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(PacketAdapter.params()
    			.plugin(MzTech.instance).clientSide().serverSide()
    			.listenerPriority(ListenerPriority.NORMAL)
    			.gamePhase(GamePhase.PLAYING)
    			.options(ListenerOptions.SKIP_PLUGIN_VERIFIER)
    			.types(PacketType.Play.Client.BLOCK_DIG))
    	{	
			@Override
    		public void onPacketReceiving(PacketEvent event)
    		{	
    			if(event.getPacketType()==PacketType.Play.Client.BLOCK_DIG)
    			{	
    				new BukkitRunnable()
    				{	
    					@Override
    					public void run()
    					{	
		    				if(event.getPacket().getPlayerDigTypes().getValues().contains(PlayerDigType.STOP_DESTROY_BLOCK)||event.getPacket().getPlayerDigTypes().getValues().contains(PlayerDigType.ABORT_DESTROY_BLOCK))
		    				{	
		    					BlockDamageStopEvent e=new BlockDamageStopEvent(event.getPlayer(),event.getPacket().getBlockPositionModifier().read(0).toLocation(event.getPlayer().getWorld()).getBlock());
								Bukkit.getPluginManager().callEvent(e);
		    				}
    					}
    				}.runTaskLater(MzTech.instance,1);
    			}
    		}
		});
        /*new TinyProtocol(MzTech.instance)
        {	
			@SuppressWarnings("unchecked")
			@Override
			public Object onPacketInAsync(Player sender, Channel channel, Object packet)
			{	
				if(packet.getClass().getSimpleName().equals("PacketPlayInBlockDig"))
				{	
					Object type=getFieldValue(getField(packet.getClass(),"c"),packet);
					@SuppressWarnings("rawtypes")
					Class enumtype=getInnerClass(packet.getClass(), "EnumPlayerDigType");
					if(Enum.valueOf(enumtype, "STOP_DESTROY_BLOCK").equals(type)||Enum.valueOf(enumtype, "ABORT_DESTROY_BLOCK").equals(type))
					{	
						new BukkitRunnable()
						{	
							@Override
							public void run()
							{	
								BlockDamageStopEvent e=new BlockDamageStopEvent(sender);
								Bukkit.getPluginManager().callEvent(e);
							}
						}.runTaskLater(MzTech.instance,1);
					}
				}
				return packet;
			}
        };*/
    }
    public BlockDamageStopEvent(Player player,Block block)
    {	
    	this.player=player;
    	this.block=block;
    }
    
    
    
	public HandlerList getHandlers()
    {	
         return handlers;
    }
    public static HandlerList getHandlerList()
    {	
        return handlers;
    }
}
