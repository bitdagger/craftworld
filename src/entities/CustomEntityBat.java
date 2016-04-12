package entities;

import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_9_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_9_R1.entity.CraftLivingEntity;
import org.bukkit.entity.Bat;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;

import net.minecraft.server.v1_9_R1.EntityBat;
import net.minecraft.server.v1_9_R1.SoundEffect;
import net.minecraft.server.v1_9_R1.SoundEffects;
import net.minecraft.server.v1_9_R1.World;

public class CustomEntityBat extends EntityBat {
	 
    public CustomEntityBat(World world){
        super(world);
    }
    
    @Override
    protected SoundEffect bR()
    {
    	return SoundEffects.a;
    }
    
    
    public static Bat spawn(Location location)
    {
    	World mcworld = (World) ((CraftWorld) location.getWorld()).getHandle();
    	final CustomEntityBat bat = new CustomEntityBat(mcworld);
    	bat.setInvisible(true);
    	
    	bat.setLocation(
			location.getX(), 
			location.getY(), 
			location.getZ(), 
			location.getYaw(), 
			location.getPitch()
		);
    	
    	((CraftLivingEntity) bat.getBukkitEntity()).setRemoveWhenFarAway(false);
    	mcworld.addEntity(bat, SpawnReason.CUSTOM);
    	
    	return (Bat) bat.getBukkitEntity();
    }
}
