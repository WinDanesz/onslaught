package com.codetaylor.mc.onslaught.modules.onslaught.invasion.sampler.predicate;

import java.util.function.Predicate;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.math.BlockPos;

/** Responsible for testing a pillar of blocks for a valid air spawn location. */
public class SpawnPredicateAir implements Predicate<EntityLiving> {

  private final BlockPos.MutableBlockPos blockPos;
  private final int lightMin;
  private final int lightMax;
  private final int verticalRange;

  public SpawnPredicateAir(int lightMin, int lightMax, int verticalRange) {

    super();
    this.blockPos = new BlockPos.MutableBlockPos();
    this.lightMin = lightMin;
    this.lightMax = lightMax;
    this.verticalRange = verticalRange;
  }

  @Override
  public boolean test(EntityLiving entity) {

    double verticalMin = Math.max(0, -this.verticalRange + entity.posY);
    double verticalMax = Math.min(255, this.verticalRange + entity.posY);

    for (double y = verticalMax; y >= verticalMin; y--) {
      entity.setPosition(entity.posX, y, entity.posZ);

      if (this.testEntityPosition(entity)) {
        return true;
      }
    }

    return false;
  }

  private boolean testEntityPosition(EntityLiving entity) {

    this.blockPos.setPos(entity.posX, entity.posY, entity.posZ);

    if (!entity.world.isBlockLoaded(this.blockPos)) {
      return false;
    }

    // Ensure the light level isn't above the max
    int light = entity.world.getLightFromNeighbors(this.blockPos);

    if (light < this.lightMin && light > this.lightMax) {
      return false;
    }

    // Ensure we're testing an air block
    if (!entity.world.isAirBlock(this.blockPos)) {
      return false;
    }

    // Ensure the entity isn't colliding with blocks or fluids
    return entity.isNotColliding();
  }
}
