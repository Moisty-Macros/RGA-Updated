package rosegoldaddons.features;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.item.ItemSkull;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Vec3;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import rosegoldaddons.Main;
import rosegoldaddons.utils.RenderUtils;

import java.awt.*;
import java.util.ArrayList;

public class EntityReach {
    private static Entity toInteract;
    private static final ArrayList<Entity> solved = new ArrayList<>();

    @SubscribeEvent
    public void onInteract(PlayerInteractEvent event) {
        if (!Main.configFile.entityReach) return;
        if (event.action == PlayerInteractEvent.Action.RIGHT_CLICK_AIR) {
            if (toInteract != null) {
                if (toInteract instanceof EntityArmorStand) {
                    interactWithEntity2(toInteract);
                }
                interactWithEntity(toInteract);
                toInteract = null;
            }
        } else if (event.action == PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK) {
            if (toInteract != null) {
                if (toInteract instanceof EntityArmorStand) {
                    interactWithEntity2(toInteract);
                    solved.add(toInteract);
                }
                toInteract = null;
            }
        }
    }

    @SubscribeEvent
    public void clear(WorldEvent.Load event) {
        solved.clear();
    }

    @SubscribeEvent
    public void renderWorld(RenderWorldLastEvent event) {
        if (!Main.configFile.entityReach) return;
        if (toInteract != null) {
            Entity stand = getClosestArmorStand(toInteract);
            String entityName = "Null";
            if(stand != null) {
                entityName = stand.getCustomNameTag();
            }
            if(entityName.equals("") && stand != null) {
                entityName = stand.getName();
            }
            RenderUtils.drawEntityBox(toInteract, Color.RED, true, event.partialTicks);
            RenderUtils.renderWaypointText(entityName, toInteract.posX, toInteract.posY + toInteract.height, toInteract.posZ, event.partialTicks);
        }
        boolean found = false;
        ArrayList<Entity> entities = getAllEntitiesInRange();
        for (Entity entity : entities) {
            if (isLookingAtAABB(entity.getEntityBoundingBox(), event)) {
                toInteract = entity;
                found = true;
            }
            if(entity instanceof EntityArmorStand) {
                ItemStack itemStack = ((EntityArmorStand) entity).getCurrentArmor(3);
                if (itemStack != null && itemStack.getItem() instanceof ItemSkull) {
                    if(itemStack.serializeNBT().getCompoundTag("tag").getCompoundTag("SkullOwner").getCompoundTag("Properties").toString().contains("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjk2OTIzYWQyNDczMTAwMDdmNmFlNWQzMjZkODQ3YWQ1Mzg2NGNmMTZjMzU2NWExODFkYzhlNmIyMGJlMjM4NyJ9fX0=")) {
                        if(solved.contains(entity)) {
                            RenderUtils.drawEntityBox(entity, Color.YELLOW, true, event.partialTicks);
                        } else {
                            RenderUtils.drawEntityBox(entity, Color.MAGENTA, true, event.partialTicks);
                        }
                    }
                    else if(itemStack.serializeNBT().getCompoundTag("tag").getCompoundTag("SkullOwner").getCompoundTag("Properties").toString().contains("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvODZhZGRiZDVkZWRhZDQwOTk5NDczYmU0YTdmNDhmNjIzNmE3OWEwZGNlOTcxYjVkYmQ3MzcyMDE0YWUzOTRkIn19fQ==")) {
                        RenderUtils.drawEntityBox(entity, Color.GREEN, true, event.partialTicks);
                    }
                    else if(itemStack.serializeNBT().getCompoundTag("tag").getCompoundTag("SkullOwner").getCompoundTag("Properties").toString().contains("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOGRhNDE0ZDI5Y2M5ZWJiZmMxY2JkY2QyMTFlZWU0NzI2ZDA2NzZiZTI2MmU5Y2I4ZWVmZmFmZDFmYzM4MGIxNCJ9fX0=")) {
                        RenderUtils.drawEntityBox(entity, Color.YELLOW, true, event.partialTicks);
                    }
                    else if(itemStack.serializeNBT().getCompoundTag("tag").getCompoundTag("SkullOwner").getCompoundTag("Properties").toString().contains("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNjMxMmE1YTEyZWNiMjRkNjg1MmRiMzg4ZTZhMzQ3MjFjYzY3ZjUyMmNjZGU3ZTgyNGI5Zjc1ZTk1MDM2YWM5MyJ9fX0=")) {
                        RenderUtils.drawEntityBox(entity, Color.WHITE, true, event.partialTicks);
                    }
                }
            }
        }
        if (!Main.configFile.sticky && !found) {
            toInteract = null;
        }
    }

    private static Entity getClosestArmorStand(Entity entity) {
        Entity closest = null;
        double smallest = 9999;
        for (Entity entity1 : (Minecraft.getMinecraft().theWorld.loadedEntityList)) {
            if (entity1 instanceof EntityArmorStand) {
                double dist = entity.getDistanceToEntity(entity1);
                if(dist < smallest) {
                    smallest = dist;
                    closest = entity1;
                }
            }
        }
        return closest;
    }

    private static boolean isLookingAtAABB(AxisAlignedBB aabb, RenderWorldLastEvent event) {
        Vec3 position = new Vec3(Minecraft.getMinecraft().thePlayer.posX, (Minecraft.getMinecraft().thePlayer.posY + Minecraft.getMinecraft().thePlayer.getEyeHeight()), Minecraft.getMinecraft().thePlayer.posZ);
        Vec3 look = Minecraft.getMinecraft().thePlayer.getLook(event.partialTicks);
        look = scaleVec(look, 0.2F);
        for (int i = 0; i < 200; i++) {
            if (aabb.minX <= position.xCoord && aabb.maxX >= position.xCoord && aabb.minY <= position.yCoord && aabb.maxY >= position.yCoord && aabb.minZ <= position.zCoord && aabb.maxZ >= position.zCoord) {
                return true;
            }
            position = position.add(look);
        }

        return false;
    }

    private static ArrayList<Entity> getAllEntitiesInRange() {
        ArrayList<Entity> entities = new ArrayList<>();
        for (Entity entity1 : (Minecraft.getMinecraft().theWorld.loadedEntityList)) {
            if (!(entity1 instanceof EntityItem) && !(entity1 instanceof EntityXPOrb) &&!(entity1 instanceof EntityWither) && !(entity1 instanceof EntityPlayerSP)) {
                entities.add(entity1);
            }
        }
        return entities;
    }

    private static void interactWithEntity(Entity entity) {
        PlayerControllerMP playerControllerMP = Minecraft.getMinecraft().playerController;
        playerControllerMP.interactWithEntitySendPacket(Minecraft.getMinecraft().thePlayer, entity);
    }

    private static void interactWithEntity2(Entity entity) {
        PlayerControllerMP playerControllerMP = Minecraft.getMinecraft().playerController;
        playerControllerMP.isPlayerRightClickingOnEntity(Minecraft.getMinecraft().thePlayer, entity, Minecraft.getMinecraft().objectMouseOver);
    }


    private static Vec3 scaleVec(Vec3 vec, float f) {
        return new Vec3(vec.xCoord * (double) f, vec.yCoord * (double) f, vec.zCoord * (double) f);
    }

}


