package com.brandon3055.draconicevolution.common.blocks.machine;

import com.brandon3055.draconicevolution.DraconicEvolution;
import com.brandon3055.draconicevolution.common.ModBlocks;
import com.brandon3055.draconicevolution.common.blocks.BlockDE;
import com.brandon3055.draconicevolution.common.blocks.itemblocks.EnergyCrystalItemBlock;
import com.brandon3055.draconicevolution.common.lib.References;
import com.brandon3055.draconicevolution.common.lib.Strings;
import com.brandon3055.draconicevolution.common.tileentities.energynet.*;
import com.brandon3055.draconicevolution.common.utills.IHudDisplayBlock;
import com.brandon3055.draconicevolution.common.utills.InfoHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Brandon on 10/02/2015.
 */
public class EnergyCrystal extends BlockDE implements IHudDisplayBlock
{
	public static final byte RELAY_TIER_1  			= 0;
	public static final byte RELAY_TIER_2			= 1;
	public static final byte TRANSCEIVER_TIER_1		= 2;
	public static final byte TRANSCEIVER_TIER_2		= 3;

	public EnergyCrystal()
	{
		super(Material.glass);
		this.setBlockName(Strings.energyCrystalName);
		this.setCreativeTab(DraconicEvolution.tabBlocksItems);
		this.setStepSound(soundTypeGlass);
		ModBlocks.register(this, EnergyCrystalItemBlock.class);
	}

	@SuppressWarnings("unchecked")
	@SideOnly(Side.CLIENT)
	@Override
	public void getSubBlocks(Item item, CreativeTabs p_149666_2_, List list) {
		list.add(new ItemStack(item, 1, 0));
		list.add(new ItemStack(item, 1, 1));
		list.add(new ItemStack(item, 1, 2));
		list.add(new ItemStack(item, 1, 3));
	}

	@Override
	public void setBlockBoundsBasedOnState(IBlockAccess world, int x, int y, int z) {
		int meta = world.getBlockMetadata(x, y, z);
		if (meta == 1 || meta == 0) setBlockBounds(0.37F, 0.135F, 0.37F, 0.63F, 0.865F, 0.63F);
		else if (meta == 2 || meta == 3)
		{
			TileEnergyTransceiver tile = world.getTileEntity(x, y, z) instanceof TileEnergyTransceiver ? (TileEnergyTransceiver) world.getTileEntity(x, y, z) : null;
			if (tile != null)
			{
				switch (tile.facing)
				{
					case 0:
						setBlockBounds(0.37F, 0.59F, 0.37F, 0.63F, 1.0F, 0.63F);
						break;
					case 1:
						setBlockBounds(0.37F, 0.0F, 0.37F, 0.63F, 0.41F, 0.63F);
						break;
					case 2:
						setBlockBounds(0.37F, 0.37F, 0.59F, 0.63F, 0.63F, 1.0F);
						break;
					case 3:
						setBlockBounds(0.37F, 0.37F, 0.0F, 0.63F, 0.63F, 0.41F);
						break;
					case 4:
						setBlockBounds(0.59F, 0.37F, 0.37F, 1.0F, 0.63F, 0.63F);
						break;
					case 5:
						setBlockBounds(0.0F, 0.37F, 0.37F, 0.41F, 0.63F, 0.63F);
						break;
				}
			}
		}
	}

	@Override
	public boolean hasTileEntity(int metadata) {
		return true;
	}

	@Override
	public TileEntity createTileEntity(World world, int metadata) {
		return metadata == 0 || metadata == 1 ? new TileEnergyRelay(metadata) : metadata == 2 || metadata == 3 ? new TileEnergyTransceiver(metadata - 2) : null;
	}

	@Override
	public void registerBlockIcons(IIconRegister iconRegister) {
		blockIcon = iconRegister.registerIcon(References.RESOURCESPREFIX + "draconium_block_0");
	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float xx, float yy, float zz) {
		if (world.getTileEntity(x, y, z) instanceof TileRemoteEnergyBase) ((TileRemoteEnergyBase)world.getTileEntity(x, y, z)).onBlockActivated(player);
		return true;
	}

	@Override
	public int getRenderType() {
		return -1;
	}

	@Override
	public boolean renderAsNormalBlock() {
		return false;
	}

	@Override
	public boolean isOpaqueCube() {
		return false;
	}

	@Override
	public int getLightValue(IBlockAccess world, int x, int y, int z) {
		return 4;//super.getLightValue(world, x, y, z);
	}

	@Override
	public ItemStack getPickBlock(MovingObjectPosition target, World world, int x, int y, int z, EntityPlayer player) {
		return new ItemStack(this, 1, world.getBlockMetadata(x, y, z));
	}

	@Override
	public List<String> getDisplayData(World world, int x, int y, int z) {
		List<String> list = new ArrayList<String>();
		IRemoteEnergyHandler tile = world.getTileEntity(x, y, z) instanceof IRemoteEnergyHandler ? (IRemoteEnergyHandler) world.getTileEntity(x, y, z) : null;
		if (tile != null)
		{
			list.add(InfoHelper.HITC()+getLocalizedName());
			list.add("RF: " + tile.getEnergyStored(ForgeDirection.DOWN));
			list.add("Cap: " + tile.getCapacity() + "%");
			if (tile instanceof TileRemoteEnergyBase)
			{
				for (LinkedEnergyDevice l : ((TileRemoteEnergyBase) tile).linkedDevices)
				{
					list.add(l.toString());
				}
			}
		}
		return list;
	}

}