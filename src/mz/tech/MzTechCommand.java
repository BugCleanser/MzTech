package mz.tech;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.CommandSender;

/**
 * /mztech子命令基类
 */
public abstract class MzTechCommand
{	
	String name;
	boolean onlyOp;
	int argsNum;
	
	public MzTechCommand(String name,boolean onlyOp,int argsNum)
	{	
		this.name=name;
		this.onlyOp=onlyOp;
		this.argsNum=argsNum;
	}
	
	public abstract String usage();
	
	public List<String> onTabComplite(CommandSender sender,String[] args)
	{	
		return new ArrayList<>();
	}
	
	public abstract boolean execute(CommandSender sender,String[] args);
}
