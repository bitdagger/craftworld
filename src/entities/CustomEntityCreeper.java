package entities;

import java.util.Set;

import craftworld.NMSUtils;
import net.minecraft.server.v1_9_R1.EntityHuman;
import net.minecraft.server.v1_9_R1.EntityBat;
import net.minecraft.server.v1_9_R1.EntityCreeper;
import net.minecraft.server.v1_9_R1.PathfinderGoalFloat;
import net.minecraft.server.v1_9_R1.PathfinderGoalHurtByTarget;
import net.minecraft.server.v1_9_R1.PathfinderGoalLookAtPlayer;
import net.minecraft.server.v1_9_R1.PathfinderGoalMeleeAttack;
import net.minecraft.server.v1_9_R1.PathfinderGoalNearestAttackableTarget;
import net.minecraft.server.v1_9_R1.PathfinderGoalRandomLookaround;
import net.minecraft.server.v1_9_R1.PathfinderGoalRandomStroll;
import net.minecraft.server.v1_9_R1.PathfinderGoalSelector;
import net.minecraft.server.v1_9_R1.PathfinderGoalSwell;
import net.minecraft.server.v1_9_R1.World;

public class CustomEntityCreeper extends EntityCreeper {
	 
    public CustomEntityCreeper(World world){
        super(world);
        
        Set<?> goalB = (Set<?>)NMSUtils.getPrivateField("b", PathfinderGoalSelector.class, goalSelector); goalB.clear();
        Set<?> goalC = (Set<?>)NMSUtils.getPrivateField("c", PathfinderGoalSelector.class, goalSelector); goalC.clear();
        Set<?> targetB = (Set<?>)NMSUtils.getPrivateField("b", PathfinderGoalSelector.class, targetSelector); targetB.clear();
        Set<?> targetC = (Set<?>)NMSUtils.getPrivateField("c", PathfinderGoalSelector.class, targetSelector); targetC.clear();
        
        this.goalSelector.a(1, new PathfinderGoalFloat(this));
        this.goalSelector.a(2, new PathfinderGoalSwell(this));
        //this.goalSelector.a(3, new PathfinderGoalAvoidTarget(this, EntityOcelot.class, 6.0F, 1.0D, 1.2D));
        this.goalSelector.a(4, new PathfinderGoalMeleeAttack(this, 1.0D, false));
        this.goalSelector.a(5, new PathfinderGoalRandomStroll(this, 0.8D));
        this.goalSelector.a(6, new PathfinderGoalLookAtPlayer(this, EntityHuman.class, 8.0F));
        this.goalSelector.a(6, new PathfinderGoalRandomLookaround(this));
        this.targetSelector.a(1, new PathfinderGoalNearestAttackableTarget<EntityBat>(this, EntityBat.class, true));
        this.targetSelector.a(2, new PathfinderGoalNearestAttackableTarget<EntityHuman>(this, EntityHuman.class, true));
        this.targetSelector.a(3, new PathfinderGoalHurtByTarget(this, false));
    }
    
//    @Override
//    public void a(EntityLiving entityliving, float f){
//    	System.out.println("FIRE");
//    	
//        for (int i = 0; i < 2; ++i){
//            super.a(entityliving, f);
//        }
//    }
 
}
