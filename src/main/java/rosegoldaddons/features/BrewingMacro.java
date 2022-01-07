package rosegoldaddons.features;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.Slot;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Vec3;
import net.minecraft.util.Vec3i;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import rosegoldaddons.Main;
import rosegoldaddons.utils.ChatUtils;
import rosegoldaddons.utils.RenderUtils;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class BrewingMacro {
    private Thread thread;
    private BlockPos stand;
    private boolean sell = false;

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        if (!Main.brewingMacro || !Main.configFile.openstand || Main.configFile.alchindex != 0) return;
        if (event.phase == TickEvent.Phase.END) {
            if (Minecraft.getMinecraft().currentScreen == null && stand != null && !sell) {
                if (Minecraft.getMinecraft().playerController.onPlayerRightClick(
                        Minecraft.getMinecraft().thePlayer,
                        Minecraft.getMinecraft().theWorld,
                        Minecraft.getMinecraft().thePlayer.inventory.getCurrentItem(),
                        stand,
                        EnumFacing.fromAngle(Minecraft.getMinecraft().thePlayer.rotationYaw),
                        new Vec3(Math.random(), Math.random(), Math.random())
                )) {
                    Minecraft.getMinecraft().thePlayer.swingItem();
                }
            }
        }
    }

    @SubscribeEvent
    public void guiDraw(GuiScreenEvent.BackgroundDrawnEvent event) {
        if (!Main.brewingMacro) return;
        if (thread == null || !thread.isAlive()) {
            thread = new Thread(() -> {
                try {
                    if (event.gui instanceof GuiChest) {
                        Container container = ((GuiChest) event.gui).inventorySlots;
                        if (container instanceof ContainerChest) {
                            String chestName = ((ContainerChest) container).getLowerChestInventory().getDisplayName().getUnformattedText();
                            int sleep = Main.configFile.alchsleep;
                            if (Main.configFile.alchindex == 0) {
                                if (chestName.contains("Brewing Stand")) {
                                    List<Slot> chestInventory = ((GuiChest) Minecraft.getMinecraft().currentScreen).inventorySlots.inventorySlots;
                                    for (Slot slot : chestInventory) {
                                        if (!slot.getHasStack()) continue;
                                        if ((slot.getStack().getDisplayName().contains("Speed") || slot.getStack().getDisplayName().contains("Weakness")) && slot.slotNumber < 54) {
                                            clickSlot(slot.slotNumber, 1, 1);
                                            Thread.sleep(sleep);
                                            if (isInventoryFull()) {
                                                sell = true;
                                                Minecraft.getMinecraft().thePlayer.sendChatMessage("/sbmenu");
                                            }
                                        }
                                    }
                                }
                                if (sell) {
                                    if (chestName.contains("SkyBlock")) {
                                        Thread.sleep(100);
                                        clickSlot(22, 0, 0);
                                    } else if (chestName.contains("Trades")) {
                                        List<Slot> chestInventory = ((GuiChest) Minecraft.getMinecraft().currentScreen).inventorySlots.inventorySlots;
                                        for (Slot slot : chestInventory) {
                                            if (!slot.getHasStack()) continue;
                                            if ((slot.getStack().getDisplayName().contains("Speed") || slot.getStack().getDisplayName().contains("Weakness")) && slot.slotNumber >= 54) {
                                                clickSlot(slot.slotNumber, 1, 0);
                                                Thread.sleep(sleep);
                                            }
                                        }
                                        Minecraft.getMinecraft().thePlayer.closeScreen();
                                        sell = false;
                                    }
                                }
                            } else if (Main.configFile.alchindex == 1) {
                                if (chestName.contains("Brewing Stand")) {
                                    List<Slot> chestInventory = ((GuiChest) Minecraft.getMinecraft().currentScreen).inventorySlots.inventorySlots;
                                    for (Slot slot : chestInventory) {
                                        if (!slot.getHasStack()) continue;
                                        if (!chestInventory.get(42).getHasStack()) {
                                            if (slot.getStack().getDisplayName().contains("Water Bottle") && slot.slotNumber >= 54) {
                                                clickSlot(slot.slotNumber, 1, 1);
                                                Thread.sleep(sleep);
                                            }
                                        }
                                    }
                                    if(Main.configFile.alchclose) {
                                        Minecraft.getMinecraft().thePlayer.closeScreen();
                                    }
                                }
                            } else if (Main.configFile.alchindex == 2) {
                                if (chestName.contains("Brewing Stand")) {
                                    List<Slot> chestInventory = ((GuiChest) Minecraft.getMinecraft().currentScreen).inventorySlots.inventorySlots;
                                    for (Slot slot : chestInventory) {
                                        if (!slot.getHasStack()) continue;
                                        if (!chestInventory.get(13).getHasStack()) {
                                            if (slot.getStack().getDisplayName().contains("Nether Wart") && slot.slotNumber >= 54) {
                                                clickSlot(slot.slotNumber, 0, 0);
                                                Thread.sleep(sleep/2);
                                                clickSlot(13, 1, 0);
                                                Thread.sleep(sleep/2);
                                                clickSlot(slot.slotNumber, 0, 0);
                                                break;
                                            }
                                        }
                                    }
                                    if(Main.configFile.alchclose) {
                                        Minecraft.getMinecraft().thePlayer.closeScreen();
                                    }
                                }
                            } else if (Main.configFile.alchindex == 3) {
                                if (chestName.contains("Brewing Stand")) {
                                    List<Slot> chestInventory = ((GuiChest) Minecraft.getMinecraft().currentScreen).inventorySlots.inventorySlots;
                                    for (Slot slot : chestInventory) {
                                        if (!slot.getHasStack()) continue;
                                        if (!chestInventory.get(13).getHasStack()) {
                                            if ((slot.getStack().getDisplayName().contains("Sugar") || slot.getStack().getDisplayName().contains("Spider Eye")) && slot.slotNumber >= 54) {
                                                clickSlot(slot.slotNumber, 0, 0);
                                                Thread.sleep(sleep/2);
                                                clickSlot(13, 1, 0);
                                                Thread.sleep(sleep/2);
                                                clickSlot(slot.slotNumber, 0, 0);
                                                break;
                                            }
                                        }
                                    }
                                    if(Main.configFile.alchclose) {
                                        Minecraft.getMinecraft().thePlayer.closeScreen();
                                    }
                                }
                            } else if (Main.configFile.alchindex == 4) {
                                if (chestName.contains("Brewing Stand")) {
                                    List<Slot> chestInventory = ((GuiChest) Minecraft.getMinecraft().currentScreen).inventorySlots.inventorySlots;
                                    for (Slot slot : chestInventory) {
                                        if (!slot.getHasStack()) continue;
                                        if (!chestInventory.get(13).getHasStack()) {
                                            if (slot.getStack().getDisplayName().contains("Glowstone") && slot.slotNumber >= 54) {
                                                clickSlot(slot.slotNumber, 0, 0);
                                                Thread.sleep(sleep/2);
                                                clickSlot(13, 1, 0);
                                                Thread.sleep(sleep/2);
                                                clickSlot(slot.slotNumber, 0, 0);
                                                break;
                                            }
                                        }
                                    }
                                    if(Main.configFile.alchclose) {
                                        Minecraft.getMinecraft().thePlayer.closeScreen();
                                    }
                                }
                            }
                        }
                    }
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
            }, "brewing");
            thread.start();
        }
    }

    @SubscribeEvent
    public void renderWorld(RenderWorldLastEvent event) {
        if (!Main.brewingMacro) return;
        stand = closestStand();
        if (stand != null) {
            RenderUtils.drawBlockBox(stand, Color.YELLOW, true, event.partialTicks);
        }
    }

    private boolean isInventoryFull() {
        List<Slot> inventory = Minecraft.getMinecraft().thePlayer.inventoryContainer.inventorySlots;
        for (Slot slot : inventory) {
            if (!slot.getHasStack() && slot.slotNumber > 8) {
                return false;
            }
        }
        return true;
    }

    private BlockPos closestStand() {
        int r = 6;
        if (Minecraft.getMinecraft().thePlayer == null) return null;
        BlockPos playerPos = Minecraft.getMinecraft().thePlayer.getPosition();
        playerPos.add(0, 1, 0);
        Vec3 playerVec = Minecraft.getMinecraft().thePlayer.getPositionVector();
        Vec3i vec3i = new Vec3i(r, r, r);
        ArrayList<Vec3> stands = new ArrayList<Vec3>();
        if (playerPos != null) {
            for (BlockPos blockPos : BlockPos.getAllInBox(playerPos.add(vec3i), playerPos.subtract(vec3i))) {
                IBlockState blockState = Minecraft.getMinecraft().theWorld.getBlockState(blockPos);
                //Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(blockState.getBlock().toString()));
                if (blockState.getBlock() == Blocks.brewing_stand) {
                    stands.add(new Vec3(blockPos.getX() + 0.5, blockPos.getY(), blockPos.getZ() + 0.5));
                }
            }
        }
        double smallest = 9999;
        Vec3 closest = null;
        for (int i = 0; i < stands.size(); i++) {
            double dist = stands.get(i).distanceTo(playerVec);
            if (dist < smallest) {
                smallest = dist;
                closest = stands.get(i);
            }
        }
        if (closest != null && smallest < 5) {
            return new BlockPos(closest.xCoord, closest.yCoord, closest.zCoord);
        }
        return null;
    }

    private void clickSlot(int slot, int type, int mode) {
        Minecraft.getMinecraft().playerController.windowClick(Minecraft.getMinecraft().thePlayer.openContainer.windowId, slot, type, mode, Minecraft.getMinecraft().thePlayer);
    }
}
