package com.syldarion.catastrophe.entity;

import com.syldarion.catastrophe.Catastrophe;
import net.minecraft.block.*;
import net.minecraft.block.SaplingBlock;
import net.minecraft.block.TNTBlock;
import net.minecraft.block.Blocks;
import net.minecraft.item.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.*;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Random;


public class TileEntityData extends TileEntity implements ITickableTileEntity {
    public TileEntityData() {
        super(Catastrophe.tileEntityDataType);
    }

    @Override
    public void tick() {

    }
}
