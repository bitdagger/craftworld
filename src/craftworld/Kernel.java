package craftworld;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarFlag;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import entities.CustomEntityBat;

public class Kernel extends BukkitRunnable implements Listener
{
	public static final int MAX_HP = 100;
	
	private final Plugin plugin;
	
	private final Server server;
	
	private final Location location;
	
	private final BossBar bossbar;
	
	private int health;
	
	private Entity crystal;
	
	private LivingEntity target;
	
	/**
	 * Construct a new Kernel object
	 * 
	 * @param plugin Plugin object
	 * @param server Server object
	 */
	public Kernel(Plugin plugin, Server server)
	{
		this.plugin = plugin;
		this.server = server;
		
		this.location = new Location(server.getWorld("world"), 0, 63, 0);
		
		this.bossbar = server.createBossBar("Kernel", BarColor.WHITE, BarStyle.SOLID, BarFlag.PLAY_BOSS_MUSIC);
		this.bossbar.removeFlag(BarFlag.PLAY_BOSS_MUSIC);
		this.bossbar.removeFlag(BarFlag.CREATE_FOG);
		this.bossbar.removeFlag(BarFlag.DARKEN_SKY);
		
		this.health = Kernel.MAX_HP; // TODO - store the HP on shutdown and reload it here
	}
	
	/**
	 * Location accessor
	 * 
	 * @return Location of the kernel
	 */
	public Location getLocation()
	{
		return this.location.clone();
	}
	
	/**
	 * Create the kernel entity if it does not exist
	 */
	public void initialize()
	{
		World world = this.location.getWorld();
		this.server.setDefaultGameMode(GameMode.SURVIVAL);
		
		// Set the spawn location
    	world.setSpawnLocation(
			this.location.getBlockX(), 
			this.location.getBlockY(), 
			this.location.getBlockZ()
		);
    	
		// Make sure that the chunks we're going to use are generated
		world.loadChunk(0, 0);
		world.loadChunk(-1, -1);
		world.loadChunk(-1, 0);
		world.loadChunk(0, -1);
		
		// Create the spawning platform (idempotent)
		this.location.clone().add(0, -1, 0).getBlock().setType(Material.BEDROCK);
		this.location.clone().add(-1, -1, 0).getBlock().setType(Material.BEDROCK);
		this.location.clone().add(0, -1, -1).getBlock().setType(Material.BEDROCK);
		this.location.clone().add(-1, -1, -1).getBlock().setType(Material.BEDROCK);
		
		// Check for an existing crystal
    	for (Entity e : world.getNearbyEntities(this.location, 1, 1, 1)) {
    		if (e.getType().equals(EntityType.ENDER_CRYSTAL)) {
    			// We already have an ender crystal, so don't make another one
    			this.crystal = e;
    			break;
    		}
    	}
    	
    	// Spawn a new crystal if we need one 
    	if (this.crystal == null) {
    		this.crystal = world.spawnEntity(this.location, EntityType.ENDER_CRYSTAL);
    	}
    	
    	// Check for an existing target
    	for (Entity e : world.getNearbyEntities(this.location, 1, 1, 1)) {
    		if (e.getType().equals(EntityType.BAT)) {
    			// We already have a target, so don't make another one
    			this.target = (LivingEntity) e;
    			break;
    		}
    	}
    	
    	// Spawn a new target if we need one 
    	if (this.target == null) {
    		this.target = CustomEntityBat.spawn(this.location);
    	}
    	this.target.teleport(this.location);
    	this.target.setAI(false);
    	
    	// Start the health task
    	this.runTaskTimer(this.plugin, 11, 11);
	}
	
	/**
	 * Handle kernel health regeneration 
	 */
	public void run()
	{
		//this.target.removePotionEffect(PotionEffectType.INVISIBILITY);
		//this.target.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 20, 1));
		
		World world = this.location.getWorld();
		world.loadChunk(0, 0);
		world.loadChunk(-1, -1);
		world.loadChunk(-1, 0);
		world.loadChunk(0, -1);
		
		if (!this.target.getLocation().equals(this.location)) {
			this.target.teleport(this.location);
		}
		
		if (this.health == MAX_HP && this.bossbar.getPlayers().size() == 0) {
			return;
		}
		
		if (this.health <= 0) {
			this.server.setDefaultGameMode(GameMode.SPECTATOR);
			for (Player p : this.server.getOnlinePlayers()) {
				p.setGameMode(GameMode.SPECTATOR);
				p.playSound(p.getLocation(), Sound.ENTITY_ENDERDRAGON_DEATH, 10, 0.5f);
				p.playSound(p.getLocation(), Sound.ENTITY_WITHER_DEATH, 10, 0.5f);
				p.playSound(p.getLocation(), Sound.ENTITY_ENDERMEN_DEATH, 10, 0.5f);
				//p.kickPlayer("End of the World...");
			}
			
			this.crystal.remove();
			this.location.getWorld().createExplosion(this.location, 1);
			
			this.cancel();
			return;
		}
		
		if (this.health < MAX_HP) {
			this.health++;
			this.bossbar.setProgress((this.health) / (double) MAX_HP);
			for (Player p : this.server.getOnlinePlayers()) {
				this.bossbar.addPlayer(p);
			}
		} else {
			this.health = MAX_HP;
			this.bossbar.removeAll();
		}
	}
	
	/**
	 * Handle the kernel getting damaged
	 * 
	 * @param e
	 */
	@EventHandler
	public void onEntityDamagedByEntity(EntityDamageByEntityEvent e)
	{
		if (!e.getEntity().equals(this.crystal) && !e.getEntity().equals(this.target)) {
			return;
		}
		
		if (e.getEntity().equals(this.target)) {
			System.out.println("BAT");
		}
		
		//if (!(e.getDamager() instanceof Player)) {
			// TODO - Arrows and stuff created by players
			
			this.health -= e.getDamage();
			this.bossbar.setProgress((double) this.health / MAX_HP);
			for (Player p : this.server.getOnlinePlayers()) {
				this.bossbar.addPlayer(p);
			}
			
			if (Math.floor(Math.random() * 10) % 4 == 0) {
				for (Player p : this.server.getOnlinePlayers()) {
					if (this.health > MAX_HP / 2) {
						p.playSound(p.getLocation(), Sound.ENTITY_ENDERDRAGON_HURT, 10, 0.5f);
						p.playSound(p.getLocation(), Sound.ENTITY_WITHER_HURT, 10, 0.5f);
						p.playSound(p.getLocation(), Sound.ENTITY_ENDERMEN_HURT, 10, 0.5f);
					} else {
						p.playSound(p.getLocation(), Sound.ENTITY_ENDERDRAGON_HURT, 10, 0.5f);
						p.playSound(p.getLocation(), Sound.ENTITY_WITHER_HURT, 10, 0.5f);
						p.playSound(p.getLocation(), Sound.ENTITY_ENDERMEN_DEATH, 10, 0.5f);
						p.playSound(p.getLocation(), Sound.ENTITY_BLAZE_DEATH, 10, 0.5f);
						p.playSound(p.getLocation(), Sound.ENTITY_LIGHTNING_THUNDER, 10, 0.5f);
						p.playSound(p.getLocation(), Sound.ENTITY_LIGHTNING_IMPACT, 10, 1f);
						p.playSound(p.getLocation(), Sound.ENTITY_ENDERMEN_SCREAM, 10, 0.5f);
					}
				}
			}
			
		//}
			
		e.setDamage(0);
		e.setCancelled(true);
	}
}
