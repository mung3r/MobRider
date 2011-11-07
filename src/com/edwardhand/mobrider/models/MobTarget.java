package com.edwardhand.mobrider.models;

import org.bukkit.Location;


public abstract class MobTarget
{
	protected MobIntent _intent;
	protected float _speed = 1.0F;

	public MobIntent getIntent()
	{
		return _intent;
	}
	
	public float getSpeed()
	{
	    return _speed;
	}
	
	public void setSpeed(float d)
	{
	    d = _speed;
	}

	public abstract TargetType getType();
	public abstract Location getLocation();
}
