package com.edwardhand.mobrider;

import org.bukkit.Location;
import org.bukkit.entity.Entity;

public class MobTarget
{
  private Object target;
  private MobIntent intent;

  public MobTarget()
  {
  }

  public MobTarget(Location _target)
  {
    setTarget(_target);
    this.intent = MobIntent.PASSIVE;
  }

  public MobTarget(Entity _target)
  {
    setTarget(_target);
    this.intent = MobIntent.PASSIVE;
  }

  public MobTarget(Entity _target, MobIntent _intent)
  {
    setTarget(_target);
    this.intent = _intent;
  }

  public MobIntent getIntent()
  {
    return this.intent;
  }

  public void setIntent(MobIntent _intent)
  {
    this.intent = _intent;
  }

  public Object getTarget()
  {
    return this.target;
  }

  public Location getLocation()
  {
    if ((this.target instanceof Location))
      return (Location)this.target;
    if ((this.target instanceof Entity)) {
      return ((Entity)this.target).getLocation();
    }
    return null;
  }

  public void setTarget(Location _target)
  {
    this.target = _target;
    this.intent = MobIntent.PASSIVE;
  }

  public void setTarget(Entity _target, MobIntent _intent)
  {
    this.target = _target;
    this.intent = _intent;
  }

  public void setTarget(Entity _target)
  {
    setTarget(_target, MobIntent.PASSIVE);
  }

  public TargetType getType()
  {
    if ((this.target instanceof Entity))
      return TargetType.ENTITY;
    if ((this.target instanceof Location)) {
      return TargetType.LOCATION;
    }
    return null;
  }
}