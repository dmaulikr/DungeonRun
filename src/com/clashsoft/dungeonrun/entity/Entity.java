package com.clashsoft.dungeonrun.entity;

import org.newdawn.slick.SlickException;

import com.clashsoft.dungeonrun.entity.render.RenderEntity;
import com.clashsoft.dungeonrun.world.BlockInWorld;
import com.clashsoft.dungeonrun.world.World;
import com.clashsoft.nbt.INBTSaveable;
import com.clashsoft.nbt.NBTTagCompound;

public abstract class Entity implements INBTSaveable
{
	public static int	nextEntityId	= 0;
	
	public int			entityId;
	
	public final World	worldObj;
	
	public double		posX			= 0.5;
	public double		posY			= 32;
	public double		posZ			= 0.5;
	public double		velocityX		= 0;
	public double		velocityY		= 0;
	public double		velocityZ		= 0;
	public byte			rot				= 3;
	
	public int			airTime			= 0;
	
	public Entity(World world)
	{
		this.entityId = nextEntityId++;
		this.worldObj = world;
		this.setLocation(0.5F, 32, 0.5F);
		this.setVelocity(0, 0, 0);
		this.setRotation(3); // South
	}
	
	public void setLocation(double x, double y, double z)
	{
		this.posX = x;
		this.posY = y;
		this.posZ = z;
	}
	
	public void setRotation(int rot)
	{
		this.rot = (byte) (rot % 4);
	}
	
	public void setVelocity(double x, double y, double z)
	{
		this.velocityX = x;
		this.velocityY = y;
		this.velocityZ = z;
	}
	
	public void move(double x, double y, double z)
	{
		if (this.canMove(x, y, z))
		{
			this.posX += x;
			this.posY += y;
			this.posZ += z;
		}
	}
	
	public void addVelocity(double x, double y, double z)
	{
		this.velocityX += x;
		this.velocityY += y;
		this.velocityZ += z;
	}
	
	public void move(double distance, int dir)
	{
		if (dir == 0)
		{
			this.move(0, 0, -distance);
		}
		else if (dir == 1)
		{
			this.move(distance, 0, 0);
		}
		else if (dir == 2)
		{
			this.move(0, 0, distance);
		}
		else if (dir == 3)
		{
			this.move(-distance, 0, 0);
		}
	}
	
	public void setDead()
	{
		this.worldObj.removeEntity(this.entityId);
	}
	
	public void updateEntity()
	{
		this.applyGravity();
		this.processVelocity();
	}
	
	protected void applyGravity()
	{
		if (!this.isCollidedVertically())
		{
			this.posY -= 0.1F + this.airTime * 0.1F;
			this.airTime++;
		}
		else
		{
			this.airTime = 0;
		}
	}
	
	protected void processVelocity()
	{
		this.posX += this.velocityX;
		this.posY += this.velocityY;
		this.posZ += this.velocityZ;
		
		this.addVelocity(this.getNormalizer(this.velocityX, 0.1F), this.getNormalizer(this.velocityY, 0.1F), this.getNormalizer(this.velocityZ, 0.1F));
	}
	
	private double getNormalizer(double par1, double par2)
	{
		if (par1 >= par2)
		{
			return -par2;
		}
		else if (par1 <= -par2)
		{
			return par2;
		}
		else if (par1 < par2 && par1 >= 0)
		{
			return 0 - par1;
		}
		else if (par1 > -par2 && par1 <= 0)
		{
			return par1;
		}
		return 0F;
	}
	
	public boolean isCollidedVertically()
	{
		if (this.posY < 64 && this.posY >= 0)
		{
			BlockInWorld block = this.worldObj.getBlock((int) Math.floor(this.posX), (int) Math.floor(this.posY), (int) Math.floor(this.posZ));
			return this.canCollideWithBlockVertically(block);
		}
		else if (this.posY <= -64)
		{
			this.setDead();
		}
		return false;
	}
	
	public boolean canMove(double distance, int dir)
	{
		if (dir == 0)
		{
			return this.canMove(0, 0, -distance);
		}
		else if (dir == 1)
		{
			return this.canMove(distance, 0, 0);
		}
		else if (dir == 2)
		{
			return this.canMove(0, 0, distance);
		}
		else if (dir == 3)
		{
			return this.canMove(-distance, 0, 0);
		}
		return false;
	}
	
	public boolean canMove(double x, double y, double z)
	{
		double newX = this.posX + x;
		double newY = this.posY + y;
		double newZ = this.posZ + z;
		
		if (newY + 1 >= 64)
		{
			return true;
		}
		
		BlockInWorld block1 = this.worldObj.getBlock((int) Math.floor(newX), (int) Math.floor(newY + 1), (int) Math.floor(newZ));
		
		if (newY + 2 >= 64)
		{
			return !this.canCollideWithBlockHorizontally(block1);
		}
		
		BlockInWorld block2 = this.worldObj.getBlock((int) Math.floor(newX), (int) Math.floor(newY + 2), (int) Math.floor(newZ));
		
		boolean b1 = !this.canCollideWithBlockHorizontally(block1);
		boolean b2 = !this.canCollideWithBlockHorizontally(block2);
		return b1 && b2;
	}
	
	public boolean canCollideWithBlockVertically(BlockInWorld block)
	{
		return block != null && block.getBlock() != null && block.getBlock().canCollideVertically(block.getMetadata(), this);
	}
	
	public boolean canCollideWithBlockHorizontally(BlockInWorld block)
	{
		return block != null && block.getBlock() != null && block.getBlock().canCollideHorizontally(block.getMetadata(), this);
	}
	
	public abstract RenderEntity getRenderer() throws SlickException;
	
	public abstract String getTexture();
	
	@Override
	public void writeToNBT(NBTTagCompound nbt)
	{
		nbt.setString("EntityType", EntityList.getNameFromClass(this.getClass()));
		nbt.setInteger("EntityID", this.entityId);
		
		NBTTagCompound pos = new NBTTagCompound("Position");
		pos.setDouble("X", this.posX);
		pos.setDouble("Y", this.posY);
		pos.setDouble("Z", this.posZ);
		nbt.setTagCompound(pos);
		
		NBTTagCompound momentum = new NBTTagCompound("Momentum");
		momentum.setDouble("X", this.velocityX);
		momentum.setDouble("Y", this.velocityY);
		momentum.setDouble("Z", this.velocityZ);
		nbt.setTagCompound(momentum);
		
		nbt.setByte("Rotation", this.rot);
		nbt.setInteger("AirTime", this.airTime);
	}
	
	@Override
	public void readFromNBT(NBTTagCompound nbt)
	{
		this.entityId = nbt.getInteger("EntityID");
		
		NBTTagCompound pos = nbt.getTagCompound("Position");
		if (pos != null)
		{
			this.posX = pos.getDouble("X");
			this.posY = pos.getDouble("Y");
			this.posZ = pos.getDouble("Z");
		}
		
		NBTTagCompound momentum = nbt.getTagCompound("Momentum");
		if (momentum != null)
		{
			this.velocityX = momentum.getDouble("X");
			this.velocityY = momentum.getDouble("Y");
			this.velocityZ = momentum.getDouble("Z");
		}
		
		this.rot = nbt.getByte("Rotation");
		this.airTime = nbt.getInteger("AirTime");
	}
}
