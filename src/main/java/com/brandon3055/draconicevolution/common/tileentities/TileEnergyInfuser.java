package com.brandon3055.draconicevolution.common.tileentities;

import com.brandon3055.draconicevolution.client.render.particle.ParticleEnergy;
import com.brandon3055.draconicevolution.common.core.utills.EnergyStorage;
import cofh.api.energy.IEnergyContainerItem;
import cofh.api.energy.IEnergyHandler;
import com.brandon3055.draconicevolution.common.core.handler.ParticleHandler;
import com.brandon3055.draconicevolution.common.core.utills.EnergyHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.Random;

/**
 * Created by Brandon on 27/06/2014.
 */
public class TileEnergyInfuser extends TileEntity implements IEnergyHandler, ISidedInventory {
	ItemStack[] items = new ItemStack[1];
	public EnergyStorage energy = new EnergyStorage(1000000);
	public int maxInput = 81920;
	public boolean running = false;
	private int tick = 0;
	private int longTick = 0;
	public float rotation = 0;
	public boolean transfer = false;

	//==============================================LOGIC=======================================================//

	@Override
	public void updateEntity() {
		tick();
		if (worldObj.isRemote)
			spawnParticles();
		if (worldObj.isRemote)
			return;
		if(running && tryStartOrStop())
		{
			IEnergyContainerItem item = (IEnergyContainerItem)items[0].getItem();
			if (energy.extractEnergy(item.receiveEnergy(items[0], energy.getEnergyStored(), false), false) > 0 && !transfer)
				setTransfer(true);
			else if (energy.extractEnergy(item.receiveEnergy(items[0], energy.getEnergyStored(), false), false) <= 0 && transfer)
				setTransfer(false);
		}
	}

	private boolean tryStartOrStop(){
		if (items[0] != null && items[0].stackSize == 1 && EnergyHelper.isEnergyContainerItem(items[0])) {
			IEnergyContainerItem item = (IEnergyContainerItem)items[0].getItem();
			if (item.getEnergyStored(items[0]) < item.getMaxEnergyStored(items[0]))
				running = true;
			else{
				if (running) worldObj.markBlockForUpdate(xCoord, yCoord ,zCoord);
				running = false;
			}
		}else {
			if (running) worldObj.markBlockForUpdate(xCoord, yCoord ,zCoord);
			running = false;
		}

		return running;
	}

	private void tick()
	{
		if (tick >= 20) {
			tick = 0;
			if (running)
				worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
		}
		else
			tick++;
		if (longTick >= 100) {
			longTick = 0;
			tryStartOrStop();
		}else
			longTick++;
		if(rotation >= 360F)
			rotation = 0F;
		else if (running)
			rotation += 0.5F;

	}

	private void setTransfer(boolean t){
		transfer = t;
		worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
	}
	@SideOnly(Side.CLIENT)
	private void spawnParticles(){
		if (worldObj.isRemote && running && transfer) {
			Random rand = worldObj.rand;
			double rotationF;
			double yRand;
			double radRand;
			ParticleEnergy particle;
			float y = 0.6f;

			yRand = ((rand.nextFloat()-0.5)/2);
			radRand = 1 - rand.nextFloat()/2;
			rotationF = rotation / 57F;
			particle = new ParticleEnergy(worldObj, xCoord + 0.5 + radRand*Math.sin(rotationF), yCoord + y + yRand, zCoord + 0.5 + radRand*Math.cos(rotationF), xCoord + 0.5, yCoord + 0.7, zCoord + 0.5, 1);
			ParticleHandler.spawnCustomParticle(particle);

			yRand = ((rand.nextFloat()-0.5)/2);
			radRand = 1 - rand.nextFloat()/2;
			rotationF = (rotation + 90) / 57F;
			particle = new ParticleEnergy(worldObj, xCoord + 0.5 + radRand*Math.sin(rotationF), yCoord + y + yRand, zCoord + 0.5 + radRand*Math.cos(rotationF), xCoord + 0.5, yCoord + 0.7, zCoord + 0.5, 1);
			ParticleHandler.spawnCustomParticle(particle);

			yRand = ((rand.nextFloat()-0.5)/2);
			radRand = 1 - rand.nextFloat()/2;
			rotationF = (rotation + 180) / 57F;
			particle = new ParticleEnergy(worldObj, xCoord + 0.5 + radRand*Math.sin(rotationF), yCoord + y + yRand, zCoord + 0.5 + radRand*Math.cos(rotationF), xCoord + 0.5, yCoord + 0.7, zCoord + 0.5, 1);
			ParticleHandler.spawnCustomParticle(particle);

			yRand = ((rand.nextFloat()-0.5)/2);
			radRand = 1 - rand.nextFloat()/2;
			rotationF = (rotation + 270) / 57F;
			particle = new ParticleEnergy(worldObj, xCoord + 0.5 + radRand*Math.sin(rotationF), yCoord + y + yRand, zCoord + 0.5 + radRand*Math.cos(rotationF), xCoord + 0.5, yCoord + 0.7, zCoord + 0.5, 1);
			ParticleHandler.spawnCustomParticle(particle);

			y = 0.79f;
			radRand = 0.35;
			rotationF = rotation / 57F;
			particle = new ParticleEnergy(worldObj, xCoord + 0.5 + radRand*Math.sin(rotationF), yCoord + y, zCoord + 0.5 + radRand*Math.cos(rotationF), xCoord + 0.5, yCoord + 0.7, zCoord + 0.5, 0);
			ParticleHandler.spawnCustomParticle(particle);

			rotationF = (rotation + 90) / 57F;
			particle = new ParticleEnergy(worldObj, xCoord + 0.5 + radRand*Math.sin(rotationF), yCoord + y, zCoord + 0.5 + radRand*Math.cos(rotationF), xCoord + 0.5, yCoord + 0.7, zCoord + 0.5, 0);
			ParticleHandler.spawnCustomParticle(particle);

			rotationF = (rotation + 180) / 57F;
			particle = new ParticleEnergy(worldObj, xCoord + 0.5 + radRand*Math.sin(rotationF), yCoord + y, zCoord + 0.5 + radRand*Math.cos(rotationF), xCoord + 0.5, yCoord + 0.7, zCoord + 0.5, 0);
			ParticleHandler.spawnCustomParticle(particle);

			rotationF = (rotation + 270) / 57F;
			particle = new ParticleEnergy(worldObj, xCoord + 0.5 + radRand*Math.sin(rotationF), yCoord + y, zCoord + 0.5 + radRand*Math.cos(rotationF), xCoord + 0.5, yCoord + 0.7, zCoord + 0.5, 0);
			ParticleHandler.spawnCustomParticle(particle);
		}
	}
	//==============================================ENERGY======================================================//

	@Override
	public int receiveEnergy(ForgeDirection from, int maxReceive, boolean simulate) {
		if (tick == 0)
			worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
		return this.energy.receiveEnergy(Math.min(maxInput, maxReceive), simulate);
	}

	@Override
	public int extractEnergy(ForgeDirection from, int maxExtract, boolean simulate) {;
			return this.energy.extractEnergy(maxExtract, simulate);
	}

	@Override
	public int getEnergyStored(ForgeDirection from) {
		return energy.getEnergyStored();
	}

	@Override
	public int getMaxEnergyStored(ForgeDirection from) {
		return energy.getMaxEnergyStored();
	}

	@Override
	public boolean canConnectEnergy(ForgeDirection from) {
		return true;
	}

	//==========================================SYNCHRONIZATION==================================================//

	@Override
	public Packet getDescriptionPacket() {
		NBTTagCompound tagCompound = new NBTTagCompound();
		this.writeToNBT(tagCompound);
		return new S35PacketUpdateTileEntity(this.xCoord, this.yCoord, this.zCoord, 1, tagCompound);
	}

	@Override
	public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt) {
		readFromNBT(pkt.func_148857_g());
	}

	//==============================================INVENTORY====================================================//

	@Override
	public int getSizeInventory() {
		return items.length;
	}

	@Override
	public ItemStack getStackInSlot(int i) {
		return items[i];
	}

	@Override
	public ItemStack decrStackSize(int i, int count) {
		ItemStack itemstack = getStackInSlot(i);

		if (itemstack != null) {
			if (itemstack.stackSize <= count) {
				setInventorySlotContents(i, null);
			} else {
				itemstack = itemstack.splitStack(count);
				if (itemstack.stackSize == 0) {
					setInventorySlotContents(i, null);
				}
			}
		}
		return itemstack;
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int i) {
		ItemStack item = getStackInSlot(i);
		if (item != null) setInventorySlotContents(i, null);
		return item;
	}

	@Override
	public void setInventorySlotContents(int i, ItemStack itemstack) {
		items[i] = itemstack;
		if (itemstack != null && itemstack.stackSize > getInventoryStackLimit()) {
			itemstack.stackSize = getInventoryStackLimit();
		}
		tryStartOrStop();
	}

	@Override
	public String getInventoryName() {
		return "InventoryWeatherController";
	}

	@Override
	public boolean hasCustomInventoryName() {
		return false;
	}

	@Override
	public int getInventoryStackLimit() {
		return 1;
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer player) {
		return player.getDistanceSq(xCoord + 0.5, yCoord + 0.5, zCoord + 0.4) < 64;
	}

	@Override
	public void openInventory() {System.out.println("open");}

	@Override
	public void closeInventory() {System.out.println("close");}

	@Override
	public boolean isItemValidForSlot(int i, ItemStack itemstack) {
		return EnergyHelper.isEnergyContainerItem(itemstack);
	}

	@Override
	public int[] getAccessibleSlotsFromSide(int var1) {
		return new int[] {0};
	}

	@Override
	public boolean canInsertItem(int slot, ItemStack item, int side) {
		return true;
	}

	@Override
	public boolean canExtractItem(int slot, ItemStack item, int side) {
		if (item != null && item.getItem() instanceof IEnergyContainerItem && (((IEnergyContainerItem)item.getItem()).getEnergyStored(item) >= ((IEnergyContainerItem)item.getItem()).getMaxEnergyStored(item)))
			return true;
		else
			return false;
	}

	//===========================================================================================================//

	@Override
	public void writeToNBT(NBTTagCompound compound)
	{
		NBTTagCompound[] tag = new NBTTagCompound[items.length];

		for (int i = 0; i < items.length; i++)
		{
			tag[i] = new NBTTagCompound();

			if (items[i] != null)
			{
				tag[i] = items[i].writeToNBT(tag[i]);
			}

			compound.setTag("Item" + i, tag[i]);
		}
		compound.setBoolean("Running", running);
		compound.setBoolean("Transfer", transfer);
		energy.writeToNBT(compound);

		super.writeToNBT(compound);
	}

	@Override
	public void readFromNBT(NBTTagCompound compound)
	{
		NBTTagCompound[] tag = new NBTTagCompound[items.length];

		for (int i = 0; i < items.length; i++)
		{
			tag[i] = compound.getCompoundTag("Item" + i);
			items[i] = ItemStack.loadItemStackFromNBT(tag[i]);
		}
		running = compound.getBoolean("Running");
		transfer = compound.getBoolean("Transfer");
		energy.readFromNBT(compound);

		super.readFromNBT(compound);
	}
}