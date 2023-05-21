package camchua.taixiu.v2;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import camchua.taixiu.v2.FileManager.Files;
import net.milkbowl.vault.economy.Economy;

public class TaiXiu extends JavaPlugin {
	
	private Economy econ;
	
	@Override
	public void onEnable() {
		FileManager.setup(this);
		setupEconomy();
		
		dice1 = 0; dice2 = 0; dice3 = 0;
		
		tai = new HashMap<String, Double>(); xiu = new HashMap<String, Double>();
		d1 = new HashMap<String, Double>(); d2 = new HashMap<String, Double>();
		d3 = new HashMap<String, Double>(); d4 = new HashMap<String, Double>();
		d5 = new HashMap<String, Double>(); d6 = new HashMap<String, Double>();
		t1 = new HashMap<String, Double>(); t2 = new HashMap<String, Double>();
		t3 = new HashMap<String, Double>(); t4 = new HashMap<String, Double>();
		t5 = new HashMap<String, Double>(); t6 = new HashMap<String, Double>();
		
		result = "null";
		
		countdown = FileManager.getFileConfig(Files.CONFIG).getInt("Settings.Interval");
		count = FileManager.getFileConfig(Files.CONFIG).getInt("Settings.Interval");
		
		df = new DecimalFormat(FileManager.getFileConfig(Files.CONFIG).getString("Settings.FixDouble"));
		
		Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new TaiXiuTask(), 20, 20);
	}
	
	public class TaiXiuTask implements Runnable {
		
		@Override
		public void run() {
			if(FileManager.getFileConfig(Files.CONFIG).contains("Settings.Broadcast." + count))
				Bukkit.broadcastMessage(FileManager.getFileConfig(Files.CONFIG).getString("Settings.Broadcast." + count).replace("&", "§"));
			if(count <= 0) {
				count = countdown;
				roll(true);
			} else count -= 1;
		}
		
	}
	
	private int dice1, dice2, dice3;
	
	private int countdown;
	private int count;
	
	private String result;
	
	@Override
	public void onDisable() {
		refund();
	}
	
	public void refund() {
		List<String> refund = new ArrayList<String>();
		
		for(String p : tai.keySet()) {
			econ.depositPlayer(p, tai.get(p));
			if(!refund.contains(p)) refund.add(p);
		}
		for(String p : xiu.keySet()) {
			econ.depositPlayer(p, xiu.get(p));
			if(!refund.contains(p)) refund.add(p);
		}
		
		for(String p : d1.keySet()) {
			econ.depositPlayer(p, d1.get(p));
			if(!refund.contains(p)) refund.add(p);
		}
		for(String p : d2.keySet()) {
			econ.depositPlayer(p, d2.get(p));
			if(!refund.contains(p)) refund.add(p);
		}
		for(String p : d3.keySet()) {
			econ.depositPlayer(p, d3.get(p));
			if(!refund.contains(p)) refund.add(p);
		}
		for(String p : d4.keySet()) {
			econ.depositPlayer(p, d4.get(p));
			if(!refund.contains(p)) refund.add(p);
		}
		for(String p : d5.keySet()) {
			econ.depositPlayer(p, d5.get(p));
			if(!refund.contains(p)) refund.add(p);
		}
		for(String p : d6.keySet()) {
			econ.depositPlayer(p, d6.get(p));
			if(!refund.contains(p)) refund.add(p);
		}
		
		for(String p : t1.keySet()) {
			econ.depositPlayer(p, t1.get(p));
			if(!refund.contains(p)) refund.add(p);
		}
		for(String p : t2.keySet()) {
			econ.depositPlayer(p, t2.get(p));
			if(!refund.contains(p)) refund.add(p);
		}
		for(String p : t3.keySet()) {
			econ.depositPlayer(p, t3.get(p));
			if(!refund.contains(p)) refund.add(p);
		}
		for(String p : t4.keySet()) {
			econ.depositPlayer(p, t4.get(p));
			if(!refund.contains(p)) refund.add(p);
		}
		for(String p : t5.keySet()) {
			econ.depositPlayer(p, t5.get(p));
			if(!refund.contains(p)) refund.add(p);
		}
		for(String p : t6.keySet()) {
			econ.depositPlayer(p, t6.get(p));
			if(!refund.contains(p)) refund.add(p);
		}
		
		for(String re : refund) {
			if(Bukkit.getOfflinePlayer(re).isOnline())
				Bukkit.getPlayer(re).sendMessage(FileManager.getFileConfig(Files.CONFIG).getString("Message.Refund").replace("&", "§"));
		}
	}
	
	private HashMap<String, Double> tai, xiu, d1, d2, d3, d4, d5, d6, t1, t2, t3, t4, t5, t6;
	
	private HashMap<String, Long> cooldown = new HashMap<String, Long>();
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(args.length == 0) {
			for(String help : FileManager.getFileConfig(Files.CONFIG).getStringList("Message.Help")) {
				sender.sendMessage(help.replace("&", "§"));
			}
			return true;
		}
		switch(args[0].toLowerCase()) {
		case "tai": {
			if(!(sender instanceof Player)) return true;
			switch(args.length) {
			case 1: return true;
			}
			Player p = (Player) sender;
			
			if(count <= 10) {
				p.sendMessage(FileManager.getFileConfig(Files.CONFIG).getString("Message.NoBet").replace("&", "§"));
				return true;
			}
			
			if(cooldown.containsKey(p.getName())) {
				long bet_time = cooldown.get(p.getName());
				long current = System.currentTimeMillis();
				long time = current - bet_time;
				
				Date d = new Date(time);
				
				if(d.getSeconds() < FileManager.getFileConfig(Files.CONFIG).getInt("Settings.BetCooldown")) {
					p.sendMessage(FileManager.getFileConfig(Files.CONFIG).getString("Message.BetCooldown").replace("<s>", String.valueOf(FileManager.getFileConfig(Files.CONFIG).getInt("Settings.BetCooldown") - d.getSeconds())).replace("&", "§"));
					return true;
				}
				
				cooldown.remove(p.getName());
			}
			cooldown.put(p.getName(), System.currentTimeMillis());
			
			
			double money = Double.parseDouble(args[1]);
			
			if(money <= FileManager.getFileConfig(Files.CONFIG).getDouble("Settings.MinBet")) {
				p.sendMessage(FileManager.getFileConfig(Files.CONFIG).getString("Message.InvaildMoney").replace("&", "§"));
				return true;
			}
			
			if(!econ.has(p, money)) {
				p.sendMessage(FileManager.getFileConfig(Files.CONFIG).getString("Message.NotMoney").replace("&", "§"));
				return true;
			}
			
			econ.withdrawPlayer(p, money);
			
			if(tai.containsKey(p.getName())) tai.replace(p.getName(), tai.get(p.getName()) + money);
			else tai.put(p.getName(), money);
			
			p.sendMessage(FileManager.getFileConfig(Files.CONFIG).getString("Message.Bet").replace("<type>", BetType.getFormat(BetType.TAI)).replace("<money>", String.valueOf(tai.get(p.getName()))).replace("&", "§"));
			
			String broadcast = FileManager.getFileConfig(Files.CONFIG).getString("Message.BetBroadcast");
			if(!broadcast.equalsIgnoreCase("none")) Bukkit.broadcastMessage(broadcast.replace("<player>", p.getName()).replace("<type>", BetType.getFormat(BetType.TAI)).replace("<money>", String.valueOf(money)).replace("&", "§"));
			
			return true;
		}
		
		case "xiu": {
			if(!(sender instanceof Player)) return true;
			switch(args.length) {
			case 1: return true;
			}
			Player p = (Player) sender;
			
			if(count <= 10) {
				p.sendMessage(FileManager.getFileConfig(Files.CONFIG).getString("Message.NoBet").replace("&", "§"));
				return true;
			}
			
			if(cooldown.containsKey(p.getName())) {
				long bet_time = cooldown.get(p.getName());
				long current = System.currentTimeMillis();
				long time = current - bet_time;
				
				Date d = new Date(time);
				
				if(d.getSeconds() < FileManager.getFileConfig(Files.CONFIG).getInt("Settings.BetCooldown")) {
					p.sendMessage(FileManager.getFileConfig(Files.CONFIG).getString("Message.BetCooldown").replace("<s>", String.valueOf(FileManager.getFileConfig(Files.CONFIG).getInt("Settings.BetCooldown") - d.getSeconds())).replace("&", "§"));
					return true;
				}
				
				cooldown.remove(p.getName());
			}
			cooldown.put(p.getName(), System.currentTimeMillis());
			
			double money = Double.parseDouble(args[1]);
			
			if(money <= FileManager.getFileConfig(Files.CONFIG).getDouble("Settings.MinBet")) {
				p.sendMessage(FileManager.getFileConfig(Files.CONFIG).getString("Message.InvaildMoney").replace("&", "§"));
				return true;
			}
			
			if(!econ.has(p, money)) {
				p.sendMessage(FileManager.getFileConfig(Files.CONFIG).getString("Message.NotMoney").replace("&", "§"));
				return true;
			}
			
			econ.withdrawPlayer(p, money);
			
			if(xiu.containsKey(p.getName())) xiu.replace(p.getName(), xiu.get(p.getName()) + money);
			else xiu.put(p.getName(), money);
			
			p.sendMessage(FileManager.getFileConfig(Files.CONFIG).getString("Message.Bet").replace("<type>", BetType.getFormat(BetType.XIU)).replace("<money>", String.valueOf(xiu.get(p.getName()))).replace("&", "§"));
			
			String broadcast = FileManager.getFileConfig(Files.CONFIG).getString("Message.BetBroadcast");
			if(!broadcast.equalsIgnoreCase("none")) Bukkit.broadcastMessage(broadcast.replace("<player>", p.getName()).replace("<type>", BetType.getFormat(BetType.XIU)).replace("<money>", String.valueOf(money)).replace("&", "§"));
			
			return true;
		}
		
		case "11": {
			if(!(sender instanceof Player)) return true;
			switch(args.length) {
			case 1: return true;
			}
			Player p = (Player) sender;
			
			if(count <= 10) {
				p.sendMessage(FileManager.getFileConfig(Files.CONFIG).getString("Message.NoBet").replace("&", "§"));
				return true;
			}
			
			if(cooldown.containsKey(p.getName())) {
				long bet_time = cooldown.get(p.getName());
				long current = System.currentTimeMillis();
				long time = current - bet_time;
				
				Date d = new Date(time);
				
				if(d.getSeconds() < FileManager.getFileConfig(Files.CONFIG).getInt("Settings.BetCooldown")) {
					p.sendMessage(FileManager.getFileConfig(Files.CONFIG).getString("Message.BetCooldown").replace("<s>", String.valueOf(FileManager.getFileConfig(Files.CONFIG).getInt("Settings.BetCooldown") - d.getSeconds())).replace("&", "§"));
					return true;
				}
				
				cooldown.remove(p.getName());
			}
			cooldown.put(p.getName(), System.currentTimeMillis());
			
			double money = Double.parseDouble(args[1]);
			
			if(money <= FileManager.getFileConfig(Files.CONFIG).getDouble("Settings.MinBet")) {
				p.sendMessage(FileManager.getFileConfig(Files.CONFIG).getString("Message.InvaildMoney").replace("&", "§"));
				return true;
			}
			
			if(!econ.has(p, money)) {
				p.sendMessage(FileManager.getFileConfig(Files.CONFIG).getString("Message.NotMoney").replace("&", "§"));
				return true;
			}
			
			econ.withdrawPlayer(p, money);
			
			if(d1.containsKey(p.getName())) d1.replace(p.getName(), d1.get(p.getName()) + money);
			else d1.put(p.getName(), money);
			
			p.sendMessage(FileManager.getFileConfig(Files.CONFIG).getString("Message.BetDouble").replace("<type>", "11").replace("<money>", String.valueOf(d1.get(p.getName()))).replace("&", "§"));
			
			String broadcast = FileManager.getFileConfig(Files.CONFIG).getString("Message.BetDoubleBroadcast");
			if(!broadcast.equalsIgnoreCase("none")) Bukkit.broadcastMessage(broadcast.replace("<player>", p.getName()).replace("<type>", "11").replace("<money>", String.valueOf(money)).replace("&", "§"));
			
			return true;
		}
		
		case "22": {
			if(!(sender instanceof Player)) return true;
			switch(args.length) {
			case 1: return true;
			}
			Player p = (Player) sender;
			
			if(count <= 10) {
				p.sendMessage(FileManager.getFileConfig(Files.CONFIG).getString("Message.NoBet").replace("&", "§"));
				return true;
			}
			
			if(cooldown.containsKey(p.getName())) {
				long bet_time = cooldown.get(p.getName());
				long current = System.currentTimeMillis();
				long time = current - bet_time;
				
				Date d = new Date(time);
				
				if(d.getSeconds() < FileManager.getFileConfig(Files.CONFIG).getInt("Settings.BetCooldown")) {
					p.sendMessage(FileManager.getFileConfig(Files.CONFIG).getString("Message.BetCooldown").replace("<s>", String.valueOf(FileManager.getFileConfig(Files.CONFIG).getInt("Settings.BetCooldown") - d.getSeconds())).replace("&", "§"));
					return true;
				}
				
				cooldown.remove(p.getName());
			}
			cooldown.put(p.getName(), System.currentTimeMillis());
			
			double money = Double.parseDouble(args[1]);
			
			if(money <= FileManager.getFileConfig(Files.CONFIG).getDouble("Settings.MinBet")) {
				p.sendMessage(FileManager.getFileConfig(Files.CONFIG).getString("Message.InvaildMoney").replace("&", "§"));
				return true;
			}
			
			if(!econ.has(p, money)) {
				p.sendMessage(FileManager.getFileConfig(Files.CONFIG).getString("Message.NotMoney").replace("&", "§"));
				return true;
			}
			
			econ.withdrawPlayer(p, money);
			
			if(d2.containsKey(p.getName())) d2.replace(p.getName(), d2.get(p.getName()) + money);
			else d2.put(p.getName(), money);
			
			p.sendMessage(FileManager.getFileConfig(Files.CONFIG).getString("Message.BetDouble").replace("<type>", "22").replace("<money>", String.valueOf(d2.get(p.getName()))).replace("&", "§"));
			
			String broadcast = FileManager.getFileConfig(Files.CONFIG).getString("Message.BetDoubleBroadcast");
			if(!broadcast.equalsIgnoreCase("none")) Bukkit.broadcastMessage(broadcast.replace("<player>", p.getName()).replace("<type>", "22").replace("<money>", String.valueOf(money)).replace("&", "§"));
			
			return true;
		}
		
		case "33": {
			if(!(sender instanceof Player)) return true;
			switch(args.length) {
			case 1: return true;
			}
			Player p = (Player) sender;
			
			if(count <= 10) {
				p.sendMessage(FileManager.getFileConfig(Files.CONFIG).getString("Message.NoBet").replace("&", "§"));
				return true;
			}
			
			if(cooldown.containsKey(p.getName())) {
				long bet_time = cooldown.get(p.getName());
				long current = System.currentTimeMillis();
				long time = current - bet_time;
				
				Date d = new Date(time);
				
				if(d.getSeconds() < FileManager.getFileConfig(Files.CONFIG).getInt("Settings.BetCooldown")) {
					p.sendMessage(FileManager.getFileConfig(Files.CONFIG).getString("Message.BetCooldown").replace("<s>", String.valueOf(FileManager.getFileConfig(Files.CONFIG).getInt("Settings.BetCooldown") - d.getSeconds())).replace("&", "§"));
					return true;
				}
				
				cooldown.remove(p.getName());
			}
			cooldown.put(p.getName(), System.currentTimeMillis());
			
			double money = Double.parseDouble(args[1]);
			
			if(money <= FileManager.getFileConfig(Files.CONFIG).getDouble("Settings.MinBet")) {
				p.sendMessage(FileManager.getFileConfig(Files.CONFIG).getString("Message.InvaildMoney").replace("&", "§"));
				return true;
			}
			
			if(!econ.has(p, money)) {
				p.sendMessage(FileManager.getFileConfig(Files.CONFIG).getString("Message.NotMoney").replace("&", "§"));
				return true;
			}
			
			econ.withdrawPlayer(p, money);
			
			if(d3.containsKey(p.getName())) d3.replace(p.getName(), d3.get(p.getName()) + money);
			else d3.put(p.getName(), money);
			
			p.sendMessage(FileManager.getFileConfig(Files.CONFIG).getString("Message.BetDouble").replace("<type>", "33").replace("<money>", String.valueOf(d3.get(p.getName()))).replace("&", "§"));
			
			String broadcast = FileManager.getFileConfig(Files.CONFIG).getString("Message.BetDoubleBroadcast");
			if(!broadcast.equalsIgnoreCase("none")) Bukkit.broadcastMessage(broadcast.replace("<player>", p.getName()).replace("<type>", "33").replace("<money>", String.valueOf(money)).replace("&", "§"));
			
			return true;
		}
		
		case "44": {
			if(!(sender instanceof Player)) return true;
			switch(args.length) {
			case 1: return true;
			}
			Player p = (Player) sender;
			
			if(count <= 10) {
				p.sendMessage(FileManager.getFileConfig(Files.CONFIG).getString("Message.NoBet").replace("&", "§"));
				return true;
			}
			
			if(cooldown.containsKey(p.getName())) {
				long bet_time = cooldown.get(p.getName());
				long current = System.currentTimeMillis();
				long time = current - bet_time;
				
				Date d = new Date(time);
				
				if(d.getSeconds() < FileManager.getFileConfig(Files.CONFIG).getInt("Settings.BetCooldown")) {
					p.sendMessage(FileManager.getFileConfig(Files.CONFIG).getString("Message.BetCooldown").replace("<s>", String.valueOf(FileManager.getFileConfig(Files.CONFIG).getInt("Settings.BetCooldown") - d.getSeconds())).replace("&", "§"));
					return true;
				}
				
				cooldown.remove(p.getName());
			}
			cooldown.put(p.getName(), System.currentTimeMillis());
			
			double money = Double.parseDouble(args[1]);
			
			if(money <= FileManager.getFileConfig(Files.CONFIG).getDouble("Settings.MinBet")) {
				p.sendMessage(FileManager.getFileConfig(Files.CONFIG).getString("Message.InvaildMoney").replace("&", "§"));
				return true;
			}
			
			if(!econ.has(p, money)) {
				p.sendMessage(FileManager.getFileConfig(Files.CONFIG).getString("Message.NotMoney").replace("&", "§"));
				return true;
			}
			
			econ.withdrawPlayer(p, money);
			
			if(d4.containsKey(p.getName())) d4.replace(p.getName(), d4.get(p.getName()) + money);
			else d4.put(p.getName(), money);
			
			p.sendMessage(FileManager.getFileConfig(Files.CONFIG).getString("Message.BetDouble").replace("<type>", "44").replace("<money>", String.valueOf(d4.get(p.getName()))).replace("&", "§"));
			
			String broadcast = FileManager.getFileConfig(Files.CONFIG).getString("Message.BetDoubleBroadcast");
			if(!broadcast.equalsIgnoreCase("none")) Bukkit.broadcastMessage(broadcast.replace("<player>", p.getName()).replace("<type>", "44").replace("<money>", String.valueOf(money)).replace("&", "§"));
			
			return true;
		}
		
		case "55": {
			if(!(sender instanceof Player)) return true;
			switch(args.length) {
			case 1: return true;
			}
			Player p = (Player) sender;
			
			if(count <= 10) {
				p.sendMessage(FileManager.getFileConfig(Files.CONFIG).getString("Message.NoBet").replace("&", "§"));
				return true;
			}
			
			if(cooldown.containsKey(p.getName())) {
				long bet_time = cooldown.get(p.getName());
				long current = System.currentTimeMillis();
				long time = current - bet_time;
				
				Date d = new Date(time);
				
				if(d.getSeconds() < FileManager.getFileConfig(Files.CONFIG).getInt("Settings.BetCooldown")) {
					p.sendMessage(FileManager.getFileConfig(Files.CONFIG).getString("Message.BetCooldown").replace("<s>", String.valueOf(FileManager.getFileConfig(Files.CONFIG).getInt("Settings.BetCooldown") - d.getSeconds())).replace("&", "§"));
					return true;
				}
				
				cooldown.remove(p.getName());
			}
			cooldown.put(p.getName(), System.currentTimeMillis());
			
			double money = Double.parseDouble(args[1]);
			
			if(money <= FileManager.getFileConfig(Files.CONFIG).getDouble("Settings.MinBet")) {
				p.sendMessage(FileManager.getFileConfig(Files.CONFIG).getString("Message.InvaildMoney").replace("&", "§"));
				return true;
			}
			
			if(!econ.has(p, money)) {
				p.sendMessage(FileManager.getFileConfig(Files.CONFIG).getString("Message.NotMoney").replace("&", "§"));
				return true;
			}
			
			econ.withdrawPlayer(p, money);
			
			if(d5.containsKey(p.getName())) d5.replace(p.getName(), d5.get(p.getName()) + money);
			else d5.put(p.getName(), money);
			
			p.sendMessage(FileManager.getFileConfig(Files.CONFIG).getString("Message.BetDouble").replace("<type>", "55").replace("<money>", String.valueOf(d5.get(p.getName()))).replace("&", "§"));
			
			String broadcast = FileManager.getFileConfig(Files.CONFIG).getString("Message.BetDoubleBroadcast");
			if(!broadcast.equalsIgnoreCase("none")) Bukkit.broadcastMessage(broadcast.replace("<player>", p.getName()).replace("<type>", "55").replace("<money>", String.valueOf(money)).replace("&", "§"));
			
			return true;
		}
		
		case "66": {
			if(!(sender instanceof Player)) return true;
			switch(args.length) {
			case 1: return true;
			}
			Player p = (Player) sender;
			
			if(count <= 10) {
				p.sendMessage(FileManager.getFileConfig(Files.CONFIG).getString("Message.NoBet").replace("&", "§"));
				return true;
			}
			
			if(cooldown.containsKey(p.getName())) {
				long bet_time = cooldown.get(p.getName());
				long current = System.currentTimeMillis();
				long time = current - bet_time;
				
				Date d = new Date(time);
				
				if(d.getSeconds() < FileManager.getFileConfig(Files.CONFIG).getInt("Settings.BetCooldown")) {
					p.sendMessage(FileManager.getFileConfig(Files.CONFIG).getString("Message.BetCooldown").replace("<s>", String.valueOf(FileManager.getFileConfig(Files.CONFIG).getInt("Settings.BetCooldown") - d.getSeconds())).replace("&", "§"));
					return true;
				}
				
				cooldown.remove(p.getName());
			}
			cooldown.put(p.getName(), System.currentTimeMillis());
			
			double money = Double.parseDouble(args[1]);
			
			if(money <= FileManager.getFileConfig(Files.CONFIG).getDouble("Settings.MinBet")) {
				p.sendMessage(FileManager.getFileConfig(Files.CONFIG).getString("Message.InvaildMoney").replace("&", "§"));
				return true;
			}
			
			if(!econ.has(p, money)) {
				p.sendMessage(FileManager.getFileConfig(Files.CONFIG).getString("Message.NotMoney").replace("&", "§"));
				return true;
			}
			
			econ.withdrawPlayer(p, money);
			
			if(d6.containsKey(p.getName())) d6.replace(p.getName(), d6.get(p.getName()) + money);
			else d6.put(p.getName(), money);
			
			p.sendMessage(FileManager.getFileConfig(Files.CONFIG).getString("Message.BetDouble").replace("<type>", "66").replace("<money>", String.valueOf(d6.get(p.getName()))).replace("&", "§"));
			
			String broadcast = FileManager.getFileConfig(Files.CONFIG).getString("Message.BetDoubleBroadcast");
			if(!broadcast.equalsIgnoreCase("none")) Bukkit.broadcastMessage(broadcast.replace("<player>", p.getName()).replace("<type>", "66").replace("<money>", String.valueOf(money)).replace("&", "§"));
			
			return true;
		}
		
		case "111": {
			if(!(sender instanceof Player)) return true;
			switch(args.length) {
			case 1: return true;
			}
			Player p = (Player) sender;
			
			if(count <= 10) {
				p.sendMessage(FileManager.getFileConfig(Files.CONFIG).getString("Message.NoBet").replace("&", "§"));
				return true;
			}
			
			if(cooldown.containsKey(p.getName())) {
				long bet_time = cooldown.get(p.getName());
				long current = System.currentTimeMillis();
				long time = current - bet_time;
				
				Date d = new Date(time);
				
				if(d.getSeconds() < FileManager.getFileConfig(Files.CONFIG).getInt("Settings.BetCooldown")) {
					p.sendMessage(FileManager.getFileConfig(Files.CONFIG).getString("Message.BetCooldown").replace("<s>", String.valueOf(FileManager.getFileConfig(Files.CONFIG).getInt("Settings.BetCooldown") - d.getSeconds())).replace("&", "§"));
					return true;
				}
				
				cooldown.remove(p.getName());
			}
			cooldown.put(p.getName(), System.currentTimeMillis());
			
			double money = Double.parseDouble(args[1]);
			
			if(money <= FileManager.getFileConfig(Files.CONFIG).getDouble("Settings.MinBet")) {
				p.sendMessage(FileManager.getFileConfig(Files.CONFIG).getString("Message.InvaildMoney").replace("&", "§"));
				return true;
			}
			
			if(!econ.has(p, money)) {
				p.sendMessage(FileManager.getFileConfig(Files.CONFIG).getString("Message.NotMoney").replace("&", "§"));
				return true;
			}
			
			econ.withdrawPlayer(p, money);
			
			if(t1.containsKey(p.getName())) t1.replace(p.getName(), t1.get(p.getName()) + money);
			else t1.put(p.getName(), money);
			
			p.sendMessage(FileManager.getFileConfig(Files.CONFIG).getString("Message.BetTripple").replace("<type>", "111").replace("<money>", String.valueOf(t1.get(p.getName()))).replace("&", "§"));
			
			String broadcast = FileManager.getFileConfig(Files.CONFIG).getString("Message.BetTrippleBroadcast");
			if(!broadcast.equalsIgnoreCase("none")) Bukkit.broadcastMessage(broadcast.replace("<player>", p.getName()).replace("<type>", "111").replace("<money>", String.valueOf(money)).replace("&", "§"));
			
			return true;
		}
		
		case "222": {
			if(!(sender instanceof Player)) return true;
			switch(args.length) {
			case 1: return true;
			}
			Player p = (Player) sender;
			
			if(count <= 10) {
				p.sendMessage(FileManager.getFileConfig(Files.CONFIG).getString("Message.NoBet").replace("&", "§"));
				return true;
			}
			
			if(cooldown.containsKey(p.getName())) {
				long bet_time = cooldown.get(p.getName());
				long current = System.currentTimeMillis();
				long time = current - bet_time;
				
				Date d = new Date(time);
				
				if(d.getSeconds() < FileManager.getFileConfig(Files.CONFIG).getInt("Settings.BetCooldown")) {
					p.sendMessage(FileManager.getFileConfig(Files.CONFIG).getString("Message.BetCooldown").replace("<s>", String.valueOf(FileManager.getFileConfig(Files.CONFIG).getInt("Settings.BetCooldown") - d.getSeconds())).replace("&", "§"));
					return true;
				}
				
				cooldown.remove(p.getName());
			}
			cooldown.put(p.getName(), System.currentTimeMillis());
			
			double money = Double.parseDouble(args[1]);
			
			if(money <= FileManager.getFileConfig(Files.CONFIG).getDouble("Settings.MinBet")) {
				p.sendMessage(FileManager.getFileConfig(Files.CONFIG).getString("Message.InvaildMoney").replace("&", "§"));
				return true;
			}
			
			if(!econ.has(p, money)) {
				p.sendMessage(FileManager.getFileConfig(Files.CONFIG).getString("Message.NotMoney").replace("&", "§"));
				return true;
			}
			
			econ.withdrawPlayer(p, money);
			
			if(t2.containsKey(p.getName())) t2.replace(p.getName(), t2.get(p.getName()) + money);
			else t2.put(p.getName(), money);
			
			p.sendMessage(FileManager.getFileConfig(Files.CONFIG).getString("Message.BetTripple").replace("<type>", "222").replace("<money>", String.valueOf(t2.get(p.getName()))).replace("&", "§"));
			
			String broadcast = FileManager.getFileConfig(Files.CONFIG).getString("Message.BetTrippleBroadcast");
			if(!broadcast.equalsIgnoreCase("none")) Bukkit.broadcastMessage(broadcast.replace("<player>", p.getName()).replace("<type>", "222").replace("<money>", String.valueOf(money)).replace("&", "§"));
			
			return true;
		}
		
		case "333": {
			if(!(sender instanceof Player)) return true;
			switch(args.length) {
			case 1: return true;
			}
			Player p = (Player) sender;
			
			if(count <= 10) {
				p.sendMessage(FileManager.getFileConfig(Files.CONFIG).getString("Message.NoBet").replace("&", "§"));
				return true;
			}
			
			if(cooldown.containsKey(p.getName())) {
				long bet_time = cooldown.get(p.getName());
				long current = System.currentTimeMillis();
				long time = current - bet_time;
				
				Date d = new Date(time);
				
				if(d.getSeconds() < FileManager.getFileConfig(Files.CONFIG).getInt("Settings.BetCooldown")) {
					p.sendMessage(FileManager.getFileConfig(Files.CONFIG).getString("Message.BetCooldown").replace("<s>", String.valueOf(FileManager.getFileConfig(Files.CONFIG).getInt("Settings.BetCooldown") - d.getSeconds())).replace("&", "§"));
					return true;
				}
				
				cooldown.remove(p.getName());
			}
			cooldown.put(p.getName(), System.currentTimeMillis());
			
			double money = Double.parseDouble(args[1]);
			
			if(money <= FileManager.getFileConfig(Files.CONFIG).getDouble("Settings.MinBet")) {
				p.sendMessage(FileManager.getFileConfig(Files.CONFIG).getString("Message.InvaildMoney").replace("&", "§"));
				return true;
			}
			
			if(!econ.has(p, money)) {
				p.sendMessage(FileManager.getFileConfig(Files.CONFIG).getString("Message.NotMoney").replace("&", "§"));
				return true;
			}
			
			econ.withdrawPlayer(p, money);
			
			if(t3.containsKey(p.getName())) t3.replace(p.getName(), t3.get(p.getName()) + money);
			else t3.put(p.getName(), money);
			
			p.sendMessage(FileManager.getFileConfig(Files.CONFIG).getString("Message.BetTripple").replace("<type>", "333").replace("<money>", String.valueOf(t3.get(p.getName()))).replace("&", "§"));
			
			String broadcast = FileManager.getFileConfig(Files.CONFIG).getString("Message.BetTrippleBroadcast");
			if(!broadcast.equalsIgnoreCase("none")) Bukkit.broadcastMessage(broadcast.replace("<player>", p.getName()).replace("<type>", "333").replace("<money>", String.valueOf(money)).replace("&", "§"));
			
			return true;
		}
		
		case "444": {
			if(!(sender instanceof Player)) return true;
			switch(args.length) {
			case 1: return true;
			}
			Player p = (Player) sender;
			
			if(count <= 10) {
				p.sendMessage(FileManager.getFileConfig(Files.CONFIG).getString("Message.NoBet").replace("&", "§"));
				return true;
			}
			
			if(cooldown.containsKey(p.getName())) {
				long bet_time = cooldown.get(p.getName());
				long current = System.currentTimeMillis();
				long time = current - bet_time;
				
				Date d = new Date(time);
				
				if(d.getSeconds() < FileManager.getFileConfig(Files.CONFIG).getInt("Settings.BetCooldown")) {
					p.sendMessage(FileManager.getFileConfig(Files.CONFIG).getString("Message.BetCooldown").replace("<s>", String.valueOf(FileManager.getFileConfig(Files.CONFIG).getInt("Settings.BetCooldown") - d.getSeconds())).replace("&", "§"));
					return true;
				}
				
				cooldown.remove(p.getName());
			}
			cooldown.put(p.getName(), System.currentTimeMillis());
			
			double money = Double.parseDouble(args[1]);
			
			if(money <= FileManager.getFileConfig(Files.CONFIG).getDouble("Settings.MinBet")) {
				p.sendMessage(FileManager.getFileConfig(Files.CONFIG).getString("Message.InvaildMoney").replace("&", "§"));
				return true;
			}
			
			if(!econ.has(p, money)) {
				p.sendMessage(FileManager.getFileConfig(Files.CONFIG).getString("Message.NotMoney").replace("&", "§"));
				return true;
			}
			
			econ.withdrawPlayer(p, money);
			
			if(t4.containsKey(p.getName())) t4.replace(p.getName(), t4.get(p.getName()) + money);
			else t4.put(p.getName(), money);
			
			p.sendMessage(FileManager.getFileConfig(Files.CONFIG).getString("Message.BetTripple").replace("<type>", "444").replace("<money>", String.valueOf(t4.get(p.getName()))).replace("&", "§"));
			
			String broadcast = FileManager.getFileConfig(Files.CONFIG).getString("Message.BetTrippleBroadcast");
			if(!broadcast.equalsIgnoreCase("none")) Bukkit.broadcastMessage(broadcast.replace("<player>", p.getName()).replace("<type>", "444").replace("<money>", String.valueOf(money)).replace("&", "§"));
			
			return true;
		}
		
		case "555": {
			if(!(sender instanceof Player)) return true;
			switch(args.length) {
			case 1: return true;
			}
			Player p = (Player) sender;
			
			if(count <= 10) {
				p.sendMessage(FileManager.getFileConfig(Files.CONFIG).getString("Message.NoBet").replace("&", "§"));
				return true;
			}
			
			if(cooldown.containsKey(p.getName())) {
				long bet_time = cooldown.get(p.getName());
				long current = System.currentTimeMillis();
				long time = current - bet_time;
				
				Date d = new Date(time);
				
				if(d.getSeconds() < FileManager.getFileConfig(Files.CONFIG).getInt("Settings.BetCooldown")) {
					p.sendMessage(FileManager.getFileConfig(Files.CONFIG).getString("Message.BetCooldown").replace("<s>", String.valueOf(FileManager.getFileConfig(Files.CONFIG).getInt("Settings.BetCooldown") - d.getSeconds())).replace("&", "§"));
					return true;
				}
				
				cooldown.remove(p.getName());
			}
			cooldown.put(p.getName(), System.currentTimeMillis());
			
			double money = Double.parseDouble(args[1]);
			
			if(money <= FileManager.getFileConfig(Files.CONFIG).getDouble("Settings.MinBet")) {
				p.sendMessage(FileManager.getFileConfig(Files.CONFIG).getString("Message.InvaildMoney").replace("&", "§"));
				return true;
			}
			
			if(!econ.has(p, money)) {
				p.sendMessage(FileManager.getFileConfig(Files.CONFIG).getString("Message.NotMoney").replace("&", "§"));
				return true;
			}
			
			econ.withdrawPlayer(p, money);
			
			if(t5.containsKey(p.getName())) t5.replace(p.getName(), t5.get(p.getName()) + money);
			else t5.put(p.getName(), money);
			
			p.sendMessage(FileManager.getFileConfig(Files.CONFIG).getString("Message.BetTripple").replace("<type>", "555").replace("<money>", String.valueOf(t5.get(p.getName()))).replace("&", "§"));
			
			String broadcast = FileManager.getFileConfig(Files.CONFIG).getString("Message.BetTrippleBroadcast");
			if(!broadcast.equalsIgnoreCase("none")) Bukkit.broadcastMessage(broadcast.replace("<player>", p.getName()).replace("<type>", "555").replace("<money>", String.valueOf(money)).replace("&", "§"));
			
			return true;
		}
		
		case "666": {
			if(!(sender instanceof Player)) return true;
			switch(args.length) {
			case 1: return true;
			}
			Player p = (Player) sender;
			
			if(count <= 10) {
				p.sendMessage(FileManager.getFileConfig(Files.CONFIG).getString("Message.NoBet").replace("&", "§"));
				return true;
			}
			
			if(cooldown.containsKey(p.getName())) {
				long bet_time = cooldown.get(p.getName());
				long current = System.currentTimeMillis();
				long time = current - bet_time;
				
				Date d = new Date(time);
				
				if(d.getSeconds() < FileManager.getFileConfig(Files.CONFIG).getInt("Settings.BetCooldown")) {
					p.sendMessage(FileManager.getFileConfig(Files.CONFIG).getString("Message.BetCooldown").replace("<s>", String.valueOf(FileManager.getFileConfig(Files.CONFIG).getInt("Settings.BetCooldown") - d.getSeconds())).replace("&", "§"));
					return true;
				}
				
				cooldown.remove(p.getName());
			}
			cooldown.put(p.getName(), System.currentTimeMillis());
			
			double money = Double.parseDouble(args[1]);
			
			if(money <= FileManager.getFileConfig(Files.CONFIG).getDouble("Settings.MinBet")) {
				p.sendMessage(FileManager.getFileConfig(Files.CONFIG).getString("Message.InvaildMoney").replace("&", "§"));
				return true;
			}
			
			if(!econ.has(p, money)) {
				p.sendMessage(FileManager.getFileConfig(Files.CONFIG).getString("Message.NotMoney").replace("&", "§"));
				return true;
			}
			
			econ.withdrawPlayer(p, money);
			
			if(t6.containsKey(p.getName())) t6.replace(p.getName(), t6.get(p.getName()) + money);
			else t6.put(p.getName(), money);
			
			p.sendMessage(FileManager.getFileConfig(Files.CONFIG).getString("Message.BetTripple").replace("<type>", "666").replace("<money>", String.valueOf(t6.get(p.getName()))).replace("&", "§"));
			
			String broadcast = FileManager.getFileConfig(Files.CONFIG).getString("Message.BetTrippleBroadcast");
			if(!broadcast.equalsIgnoreCase("none")) Bukkit.broadcastMessage(broadcast.replace("<player>", p.getName()).replace("<type>", "666").replace("<money>", String.valueOf(money)).replace("&", "§"));
			
			return true;
		}
		
		case "help": {
			for(String help : FileManager.getFileConfig(Files.CONFIG).getStringList("Message.Help")) {
				sender.sendMessage(help.replace("&", "§"));
			}
			return true;
		}
		
		case "status": {
			if(!(sender instanceof Player)) return true;
			Player p = (Player) sender;
			
			for(String status : FileManager.getFileConfig(Files.CONFIG).getStringList("Message.Info")) {
				p.sendMessage(status.replace("<tai>", String.valueOf(tai.get(p.getName()))).replace("<xiu>", String.valueOf(xiu.get(p.getName()))).replace("<d1>", String.valueOf(d1.get(p.getName()))).replace("<d2>", String.valueOf(d2.get(p.getName()))).replace("<d3>", String.valueOf(d3.get(p.getName()))).replace("<d4>", String.valueOf(d4.get(p.getName()))).replace("<d5>", String.valueOf(d5.get(p.getName()))).replace("<d6>", String.valueOf(d6.get(p.getName()))).replace("<t1>", String.valueOf(t1.get(p.getName()))).replace("<t2>", String.valueOf(t2.get(p.getName()))).replace("<t3>", String.valueOf(t3.get(p.getName()))).replace("<t4>", String.valueOf(t4.get(p.getName()))).replace("<t5>", String.valueOf(t5.get(p.getName()))).replace("<t6>", String.valueOf(t6.get(p.getName()))).replace("<time>", String.valueOf(count)).replace("&", "§"));
			}
			
			return true;
		}
		
		case "check": {
			if(!sender.hasPermission("taixiu.admin")) return true;
			
			double tai = 0D;
			double xiu = 0D;
			double d1 = 0D;
			double d2 = 0D;
			double d3 = 0D;
			double d4 = 0D;
			double d5 = 0D;
			double d6 = 0D;
			double t1 = 0D;
			double t2 = 0D;
			double t3 = 0D;
			double t4 = 0D;
			double t5 = 0D;
			double t6 = 0D;
			
			for(String p : this.tai.keySet()) {
				tai += Double.parseDouble(df.format(this.tai.get(p)));
			}
			for(String p : this.xiu.keySet()) {
				xiu += Double.parseDouble(df.format(this.xiu.get(p)));
			}
			for(String p : this.d1.keySet()) {
				d1 += Double.parseDouble(df.format(this.d1.get(p)));
			}
			for(String p : this.d2.keySet()) {
				d2 += Double.parseDouble(df.format(this.d2.get(p)));
			}
			for(String p : this.d3.keySet()) {
				d3 += Double.parseDouble(df.format(this.d3.get(p)));
			}
			for(String p : this.d4.keySet()) {
				d4 += Double.parseDouble(df.format(this.d4.get(p)));
			}
			for(String p : this.d5.keySet()) {
				d5 += Double.parseDouble(df.format(this.d5.get(p)));
			}
			for(String p : this.d6.keySet()) {
				d6 += Double.parseDouble(df.format(this.d6.get(p)));
			}
			for(String p : this.t1.keySet()) {
				t1 += Double.parseDouble(df.format(this.t1.get(p)));
			}
			for(String p : this.t2.keySet()) {
				t2 += Double.parseDouble(df.format(this.t2.get(p)));
			}
			for(String p : this.t3.keySet()) {
				t3 += Double.parseDouble(df.format(this.t3.get(p)));
			}
			for(String p : this.t4.keySet()) {
				t4 += Double.parseDouble(df.format(this.t4.get(p)));
			}
			for(String p : this.t5.keySet()) {
				t5 += Double.parseDouble(df.format(this.t5.get(p)));
			}
			for(String p : this.t6.keySet()) {
				t6 += Double.parseDouble(df.format(this.t6.get(p)));
			}
			
			for(String status : FileManager.getFileConfig(Files.CONFIG).getStringList("Message.Info")) {
				sender.sendMessage(status.replace("<tai>", String.valueOf(tai)).replace("<xiu>", String.valueOf(xiu)).replace("<d1>", String.valueOf(d1)).replace("<d2>", String.valueOf(d2)).replace("<d3>", String.valueOf(d3)).replace("<d4>", String.valueOf(d4)).replace("<d5>", String.valueOf(d5)).replace("<d6>", String.valueOf(d6)).replace("<t1>", String.valueOf(t1)).replace("<t2>", String.valueOf(t2)).replace("<t3>", String.valueOf(t3)).replace("<t4>", String.valueOf(t4)).replace("<t5>", String.valueOf(t5)).replace("<t6>", String.valueOf(t6)).replace("<time>", String.valueOf(count)).replace("&", "§"));
			}
			
			return true;
		}
		
		case "reload": {
			if(!sender.hasPermission("taixiu.admin")) return true;
			sender.sendMessage("§aReloading...");
			try {
				FileManager.setup(this);
				
				countdown = FileManager.getFileConfig(Files.CONFIG).getInt("Settings.Interval");
				count = FileManager.getFileConfig(Files.CONFIG).getInt("Settings.Interval");
				
				df = new DecimalFormat(FileManager.getFileConfig(Files.CONFIG).getString("Settings.FixDouble"));
				
				sender.sendMessage("§aReload complete.");
			} catch(Exception ex) {
				ex.printStackTrace();
				sender.sendMessage("§cReload failed. Check console");
			}
			return true;
		}
		
		case "result": {
			if(!sender.hasPermission("taixiu.admin")) return true;
			switch(args.length) {
			case 1: return true;
			}
			String r = args[1];
			if(!r.equalsIgnoreCase("tai") && !r.equalsIgnoreCase("xiu")) {
				sender.sendMessage("§7Nhap §ftai §7hoac §fxiu");
				return true;
			}
			result = r;
			sender.sendMessage("§aResult: §f" + result);
			return true;
		}
		
		}
		return false;
	}
	
	public void roll(boolean check) {
		this.dice1 = ThreadLocalRandom.current().nextInt(1, 7);
		this.dice2 = ThreadLocalRandom.current().nextInt(1, 7);
		this.dice3 = ThreadLocalRandom.current().nextInt(1, 7);
		
		if(check) check();
	}
	
	public void check() {
		double tai = 0D;
		double xiu = 0D;
		
		for(String p : this.tai.keySet()) {
			tai += Double.parseDouble(df.format(this.tai.get(p)));
		}
		for(String p : this.xiu.keySet()) {
			xiu += Double.parseDouble(df.format(this.xiu.get(p)));
		}
		
		double winrate = FileManager.getFileConfig(Files.CONFIG).getDouble("Settings.WinRate");
		
		if(result.equals("null")) {
			if(tai > xiu && tai - xiu >= winrate) while(this.dice1 + this.dice2 + this.dice3 >= 10) roll(false);
			if(tai < xiu && xiu - tai >= winrate) while(this.dice1 + this.dice2 + this.dice3 < 10) roll(false);
		} else {
			if(result.equalsIgnoreCase("tai")) while(this.dice1 + this.dice2 + this.dice3 < 10) roll(false);
			if(result.equalsIgnoreCase("xiu")) while(this.dice1 + this.dice2 + this.dice3 >= 10) roll(false);
			result = "null";
		}
		
		result();
	}
	
	private DecimalFormat df;
	
	public void result() {
		int total = this.dice1 + this.dice2 + this.dice3;
		
		int dn = doubleNumber();
		int tn = trippleNumber();
		
		double nw = FileManager.getFileConfig(Files.CONFIG).getDouble("Settings.Win.NormalMultiplier") + 1D;
		double dw = FileManager.getFileConfig(Files.CONFIG).getDouble("Settings.Win.DoubleMultiplier") + 1D;
		double tw = FileManager.getFileConfig(Files.CONFIG).getDouble("Settings.Win.TrippleMultiplier") + 1D;
		
		List<String> winner = new ArrayList<String>();
		List<String> winner_d = new ArrayList<String>();
		List<String> winner_t = new ArrayList<String>();
		String wintype = "";
		
		if(total > 10) {
			wintype = "Tai";
			for(String player : tai.keySet()) {
				double bet = tai.get(player);
				double win = bet * nw;
				win = Double.parseDouble(df.format(win));
				
				winner.add(player);
				
				econ.depositPlayer(player, win);
				if(Bukkit.getOfflinePlayer(player).isOnline()) {
					Bukkit.getPlayer(player).sendMessage(FileManager.getFileConfig(Files.CONFIG).getString("Message.Win").replace("<type>", BetType.getFormat(BetType.TAI)).replace("<money>", String.valueOf(win)));
				}
			}
		} else {
			wintype = "Xiu";
			for(String player : xiu.keySet()) {
				double bet = xiu.get(player);
				double win = bet * nw;
				win = Double.parseDouble(df.format(win));
				
				winner.add(player);
				
				econ.depositPlayer(player, win);
				if(Bukkit.getOfflinePlayer(player).isOnline()) {
					Bukkit.getPlayer(player).sendMessage(FileManager.getFileConfig(Files.CONFIG).getString("Message.Win").replace("<type>", BetType.getFormat(BetType.XIU)).replace("<money>", String.valueOf(win)));
				}
			}
		}
		
		String windouble = "";
		
		if(dn > 0) {
			if(dn == 1) {
				windouble = "11";
				for(String player : d1.keySet()) {
					double bet = d1.get(player);
					double win = bet * dw;
					win = Double.parseDouble(df.format(win));
					
					winner_d.add(player);
					
					econ.depositPlayer(player, win);
					if(Bukkit.getOfflinePlayer(player).isOnline()) {
						Bukkit.getPlayer(player).sendMessage(FileManager.getFileConfig(Files.CONFIG).getString("Message.WinDouble").replace("<type>", "11").replace("<money>", String.valueOf(win)));
					}
				}
			} else if(dn == 2) {
				windouble = "22";
				for(String player : d2.keySet()) {
					double bet = d2.get(player);
					double win = bet * dw;
					win = Double.parseDouble(df.format(win));
					
					winner_d.add(player);
					
					econ.depositPlayer(player, win);
					if(Bukkit.getOfflinePlayer(player).isOnline()) {
						Bukkit.getPlayer(player).sendMessage(FileManager.getFileConfig(Files.CONFIG).getString("Message.WinDouble").replace("<type>", "22").replace("<money>", String.valueOf(win)));
					}
				}
			} else if(dn == 3) {
				windouble = "33";
				for(String player : d3.keySet()) {
					double bet = d3.get(player);
					double win = bet * dw;
					win = Double.parseDouble(df.format(win));
					
					winner_d.add(player);
					
					econ.depositPlayer(player, win);
					if(Bukkit.getOfflinePlayer(player).isOnline()) {
						Bukkit.getPlayer(player).sendMessage(FileManager.getFileConfig(Files.CONFIG).getString("Message.WinDouble").replace("<type>", "33").replace("<money>", String.valueOf(win)));
					}
				}
			} else if(dn == 4) {
				windouble = "44";
				for(String player : d4.keySet()) {
					double bet = d4.get(player);
					double win = bet * dw;
					win = Double.parseDouble(df.format(win));
					
					winner_d.add(player);
					
					econ.depositPlayer(player, win);
					if(Bukkit.getOfflinePlayer(player).isOnline()) {
						Bukkit.getPlayer(player).sendMessage(FileManager.getFileConfig(Files.CONFIG).getString("Message.WinDouble").replace("<type>", "44").replace("<money>", String.valueOf(win)));
					}
				}
			} else if(dn == 5) {
				windouble = "55";
				for(String player : d5.keySet()) {
					double bet = d5.get(player);
					double win = bet * dw;
					win = Double.parseDouble(df.format(win));
					
					winner_d.add(player);
					
					econ.depositPlayer(player, win);
					if(Bukkit.getOfflinePlayer(player).isOnline()) {
						Bukkit.getPlayer(player).sendMessage(FileManager.getFileConfig(Files.CONFIG).getString("Message.WinDouble").replace("<type>", "55").replace("<money>", String.valueOf(win)));
					}
				}
			} else if(dn == 6) {
				windouble = "66";
				for(String player : d6.keySet()) {
					double bet = d6.get(player);
					double win = bet * dw;
					win = Double.parseDouble(df.format(win));
					
					winner_d.add(player);
					
					econ.depositPlayer(player, win);
					if(Bukkit.getOfflinePlayer(player).isOnline()) {
						Bukkit.getPlayer(player).sendMessage(FileManager.getFileConfig(Files.CONFIG).getString("Message.WinDouble").replace("<type>", "66").replace("<money>", String.valueOf(win)));
					}
				}
			}
		}
		
		String wintripple = "";
		
		if(tn > 0) {
			if(dn == 1) {
				wintripple = "111";
				for(String player : t1.keySet()) {
					double bet = t1.get(player);
					double win = bet * tw;
					win = Double.parseDouble(df.format(win));
					
					winner_t.add(player);
					
					econ.depositPlayer(player, win);
					if(Bukkit.getOfflinePlayer(player).isOnline()) {
						Bukkit.getPlayer(player).sendMessage(FileManager.getFileConfig(Files.CONFIG).getString("Message.WinTripple").replace("<type>", wintripple).replace("<money>", String.valueOf(win)));
					}
				}
			} else if(dn == 2) {
				wintripple = "222";
				for(String player : t2.keySet()) {
					double bet = t2.get(player);
					double win = bet * tw;
					win = Double.parseDouble(df.format(win));
					
					winner_t.add(player);
					
					econ.depositPlayer(player, win);
					if(Bukkit.getOfflinePlayer(player).isOnline()) {
						Bukkit.getPlayer(player).sendMessage(FileManager.getFileConfig(Files.CONFIG).getString("Message.WinTripple").replace("<type>", wintripple).replace("<money>", String.valueOf(win)));
					}
				}
			} else if(dn == 3) {
				wintripple = "333";
				for(String player : t3.keySet()) {
					double bet = t3.get(player);
					double win = bet * tw;
					win = Double.parseDouble(df.format(win));
					
					winner_t.add(player);
					
					econ.depositPlayer(player, win);
					if(Bukkit.getOfflinePlayer(player).isOnline()) {
						Bukkit.getPlayer(player).sendMessage(FileManager.getFileConfig(Files.CONFIG).getString("Message.WinTripple").replace("<type>", wintripple).replace("<money>", String.valueOf(win)));
					}
				}
			} else if(dn == 4) {
				wintripple = "444";
				for(String player : t4.keySet()) {
					double bet = t4.get(player);
					double win = bet * tw;
					win = Double.parseDouble(df.format(win));
					
					winner_t.add(player);
					
					econ.depositPlayer(player, win);
					if(Bukkit.getOfflinePlayer(player).isOnline()) {
						Bukkit.getPlayer(player).sendMessage(FileManager.getFileConfig(Files.CONFIG).getString("Message.WinTripple").replace("<type>", wintripple).replace("<money>", String.valueOf(win)));
					}
				}
			} else if(dn == 5) {
				wintripple = "555";
				for(String player : t5.keySet()) {
					double bet = t5.get(player);
					double win = bet * tw;
					win = Double.parseDouble(df.format(win));
					
					winner_t.add(player);
					
					econ.depositPlayer(player, win);
					if(Bukkit.getOfflinePlayer(player).isOnline()) {
						Bukkit.getPlayer(player).sendMessage(FileManager.getFileConfig(Files.CONFIG).getString("Message.WinTripple").replace("<type>", wintripple).replace("<money>", String.valueOf(win)));
					}
				}
			} else if(dn == 6) {
				wintripple = "666";
				for(String player : t6.keySet()) {
					double bet = t6.get(player);
					double win = bet * tw;
					win = Double.parseDouble(df.format(win));
					
					winner_t.add(player);
					
					econ.depositPlayer(player, win);
					if(Bukkit.getOfflinePlayer(player).isOnline()) {
						Bukkit.getPlayer(player).sendMessage(FileManager.getFileConfig(Files.CONFIG).getString("Message.WinTripple").replace("<type>", wintripple).replace("<money>", String.valueOf(win)));
					}
				}
			}
		}
		
		tai = new HashMap<String, Double>(); xiu = new HashMap<String, Double>();
		d1 = new HashMap<String, Double>(); d2 = new HashMap<String, Double>();
		d3 = new HashMap<String, Double>(); d4 = new HashMap<String, Double>();
		d5 = new HashMap<String, Double>(); d6 = new HashMap<String, Double>();
		t1 = new HashMap<String, Double>(); t2 = new HashMap<String, Double>();
		t3 = new HashMap<String, Double>(); t4 = new HashMap<String, Double>();
		t5 = new HashMap<String, Double>(); t6 = new HashMap<String, Double>();
		
		StringBuilder sb = new StringBuilder();
		for(int i = 0; i < winner.size(); i++) {
			sb.append(winner.get(i));
			if(i < winner.size() - 1) sb.append(", ");
		}
		String winstr = sb.toString();
		
		sb = new StringBuilder();
		for(int i = 0; i < winner_d.size(); i++) {
			sb.append(winner_d.get(i));
			if(i < winner_d.size() - 1) sb.append(", ");
		}
		String windstr = sb.toString();
		
		sb = new StringBuilder();
		for(int i = 0; i < winner_t.size(); i++) {
			sb.append(winner_t.get(i));
			if(i < winner_t.size() - 1) sb.append(", ");
		}
		String wintstr = sb.toString();
		
		for(String broadcast : FileManager.getFileConfig(Files.CONFIG).getStringList("Message.WinBroadcast")) {
			Bukkit.broadcastMessage(broadcast.replace("<result>", diceFormat(this.dice1, this.dice2, this.dice3)).replace("<type>", BetType.getFormat(BetType.valueOf(wintype.toUpperCase()))).replace("<winner>", winstr).replace("<winner_d>", windstr).replace("<winner_t>", wintstr).replace("&", "§"));
		}
		
		
	}
	
	private int doubleNumber() {
		if(this.dice1 == this.dice2) return this.dice1;
		if(this.dice1 == this.dice3) return this.dice1;
		if(this.dice2 == this.dice3) return this.dice2;
		return 0;
	}
	
	private int trippleNumber() {
		if(this.dice1 == this.dice2 && this.dice2 == this.dice3) return this.dice1;
		return 0;
	}
	
	private String diceFormat(int dice) {
		return FileManager.getFileConfig(Files.CONFIG).getString("Format." + dice).replace("&", "§");
	}
	
	private String diceFormat(int dice1, int dice2, int dice3) {
		String[] f1 = diceFormat(dice1).split("\n");
		String[] f2 = diceFormat(dice2).split("\n");
		String[] f3 = diceFormat(dice3).split("\n");
		
		String format = FileManager.getFileConfig(Files.CONFIG).getString("Format.Dice");
		StringBuilder sb = new StringBuilder();
		
		for(int i = 0; i < f1.length; i++) {
			sb.append(format.replace("<dice1>", f1[i]).replace("<dice2>", f2[i]).replace("<dice3>", f3[i]));
			if(i < f1.length - 1) sb.append("\n");
		}
		
		return sb.toString();
	}
	
	
	
	private boolean setupEconomy() {
        if (Bukkit.getPluginManager().getPlugin("Vault") == null) {
            return false;
        }

        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return econ != null;
    }

}
