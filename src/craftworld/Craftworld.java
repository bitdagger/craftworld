package craftworld;

import org.bukkit.Server;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import entities.CustomEntityBat;
import entities.CustomEntityCreeper;
import entities.CustomEntitySkeleton;
import entities.CustomEntityZombie;
import net.minecraft.server.v1_9_R1.EntityBat;
import net.minecraft.server.v1_9_R1.EntityCreeper;
import net.minecraft.server.v1_9_R1.EntitySkeleton;
import net.minecraft.server.v1_9_R1.EntityZombie;

/**
 * Craftworld plugin core
 */
public final class Craftworld extends JavaPlugin implements Listener
{
	/**
	 * World kernel
	 */
	private Kernel kernel;
	
	/**
	 * Construct a new Craftworld object
	 */
	public Craftworld()
	{
		
	}
	
	/**
	 * Fired when the plugin is enabled
	 */
    @Override
    public void onEnable()
    {
    	Server server = this.getServer();
    	
    	// Create and initialize the kernel
    	(this.kernel = new Kernel(this, server)).initialize();
    	
    	// Register the kernel as a listener
    	server.getPluginManager().registerEvents(this.kernel, this);
    	
    	
    	NMSUtils nms = new NMSUtils();
    	nms.registerEntity("Bat", 65, EntityBat.class, CustomEntityBat.class);
    	nms.registerEntity("Skeleton", 51, EntitySkeleton.class, CustomEntitySkeleton.class);
    	nms.registerEntity("Creeper", 50, EntityCreeper.class, CustomEntityCreeper.class);
    	nms.registerEntity("Zombie", 54, EntityZombie.class, CustomEntityZombie.class);
    	
    }
    
    
    
    /**
     * Fired when the plugin is disabled
     */
    @Override
    public void onDisable()
    {
    	this.kernel.cancel();
    	this.kernel = null;
    }
}
