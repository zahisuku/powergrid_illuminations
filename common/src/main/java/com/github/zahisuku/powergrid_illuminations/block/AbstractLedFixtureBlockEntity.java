package com.github.zahisuku.powergrid_illuminations.block;

import com.simibubi.create.content.schematics.requirement.ItemRequirement;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.Nullable;
import org.patryk3211.powergrid.electricity.base.ElectricBlockEntity;
import com.github.zahisuku.powergrid_illuminations.electricity.ILedFixtureEntity;
import com.github.zahisuku.powergrid_illuminations.electricity.ILedBulb;
import com.github.zahisuku.powergrid_illuminations.electricity.LedBulbState;


public abstract class AbstractLedFixtureBlockEntity extends ElectricBlockEntity implements ILedFixtureEntity {
    @Nullable
    protected LedBulbState bulbState;
    protected final boolean dyeable;

    public AbstractLedFixtureBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state, boolean dyeable) {
        super(type, pos, state);
        this.dyeable = dyeable;
    }

    @Override
    public void initialize() {
        super.initialize();
        if(!level.isClientSide)
            electricBehaviour.setSyncAppender(bulbState);
    }

    @Override
    public void electricalTick() {
        super.electricalTick();
        if(bulbState != null) {
            bulbState.tick();
            setUnsaved();
        }
    }

    protected void lightBulbChanged() {
        var filament = getFilament();
        if(bulbState == null) {
            filament.setState(false);
        } else {
            filament.setResistance(bulbState.resistance());
            filament.setState(!bulbState.isBurned());
        }
        electricBehaviour.setSyncAppender(bulbState);
        if(level != null && !level.isClientSide) {
            notifyUpdate();
        }
    }

    @Nullable
    public LedBulbState getBulbState() {
        return bulbState;
    }

    @Override
    protected void write(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
        super.write(tag, registries, clientPacket);
        if(bulbState != null) {
            bulbState.write(tag);
        }
    }

    @Override
    public void writeSafe(CompoundTag tag, HolderLookup.Provider registries) {
        super.writeSafe(tag, registries);
        if(bulbState != null) {
            bulbState.write(tag);
        }
    }

    @Override
    protected void read(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
        super.read(tag, registries, clientPacket);
        var currentItem = bulbState != null ? bulbState.getItem() : null;
        var nbtItem = LedBulbState.getBulbItem(tag);
        if(currentItem != nbtItem) {
            if(nbtItem == null) {
                bulbState = null;
            } else {
                bulbState = ((ILedBulb) nbtItem).createState(this);
            }
        }
        if(bulbState != null) {
            bulbState.read(tag);
        }
        lightBulbChanged();
    }

    public boolean replaceBulb(Player player, InteractionHand hand, ItemStack usedStack) {
        assert level != null;
        boolean result = replaceBulbInternal(player, hand, usedStack);
        if(result) {
            lightBulbChanged();
            if(!level.isClientSide) {
                setPowerLevel(0);
            }
        }
        return result;
    }

    private boolean replaceBulbInternal(Player player, InteractionHand hand, ItemStack usedStack) {
        assert level != null;
        if(usedStack == null || usedStack.isEmpty()) {
            if(bulbState == null)
                return false;
            if(!level.isClientSide) {
                if(!bulbState.isBurned())
                    player.setItemInHand(hand, bulbState.toStack());
                bulbState = null;
            }
            return true;
        } else {
            if(bulbState == null) {
                if(!level.isClientSide) {
                    var item = usedStack.getItem();
                    if(item instanceof ILedBulb bulb) {
                        bulbState = bulb.createState(this);
                        if(dyeable && bulb.canBeDyed() && player.getOffhandItem().getItem() instanceof DyeItem dye) {
                            bulbState.setColor(dye.getDyeColor());
                        }
                        if (!player.isCreative())
                            usedStack.shrink(1);
                    }
                }
                return true;
            } else if(bulbState.isBurned()) {
                if(!level.isClientSide) {
                    bulbState = null;
                }
                return true;
            } else if(bulbState.isOf(usedStack.getItem()) && usedStack.getCount() < usedStack.getMaxStackSize()) {
                if(!level.isClientSide) {
                    if(!player.isCreative())
                        usedStack.grow(1);
                    bulbState = null;
                }
                return true;
            } else if(player.isCreative()) {
                bulbState = null;
                return true;
            }
        }
        return false;
    }

    @Override
    protected AABB createRenderBoundingBox() {
        return new AABB(worldPosition);
    }

    @Override
    public ItemRequirement getRequiredItems(BlockState state) {
        if(bulbState != null)
            return new ItemRequirement(ItemRequirement.ItemUseType.CONSUME, bulbState.getItem());
        return ItemRequirement.NONE;
    }

    public ItemInteractionResult setColor(DyeColor color) {
        if(bulbState == null || !dyeable)
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        if(bulbState.setColor(color)) {
            notifyUpdate();
            return ItemInteractionResult.SUCCESS;
        }
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }
}
