package me.kambing.gardenia.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;

import java.util.ArrayList;
import java.util.List;

public class EntityUtil {
    private static Minecraft mc = Minecraft.getMinecraft();

    public static List<EntityLivingBase> getTargets(long reach) {
        List<EntityLivingBase> targets = new ArrayList<EntityLivingBase>();

        for (Entity ent : mc.theWorld.loadedEntityList) {
            if (ent instanceof EntityLivingBase) {
                if (!ent.isDead &&  ((EntityLivingBase) ent).getHealth() > 0 && ent != mc.thePlayer && ent.getDistanceToEntity(mc.thePlayer) < reach){
                    targets.add((EntityLivingBase) ent);
                }
            }
        }

        return targets;
    }

    public static List<EntityLivingBase> sortByRange(List<EntityLivingBase> entities) {
        List<EntityLivingBase> targetsSorted = new ArrayList<EntityLivingBase>();

        while (targetsSorted.size() < entities.size()) {
            int index = 0;
            double smallestDistance = -1;
            for (int ind = 0; ind <= entities.size()-1;ind++) {
                EntityLivingBase elb = entities.get(ind);
                double distanceToPlayer = elb.getDistanceToEntity(mc.thePlayer);
                if (smallestDistance == -1) {
                    smallestDistance = distanceToPlayer;
                    index = ind;
                } else {
                    if (distanceToPlayer < smallestDistance) {
                        smallestDistance = distanceToPlayer;
                        index = ind;
                    }
                }
            }

            targetsSorted.add(entities.get(index));
        }

        return targetsSorted;
    }

    public static ArrayList<EntityPlayer> getPlayers(List<EntityLivingBase> entities, long reach) {
        ArrayList<EntityPlayer> targets = new ArrayList<EntityPlayer>();

        for (Entity ent : entities) {
            if (ent instanceof EntityPlayer) {
                if (ent != mc.thePlayer && ent.getDistanceToEntity(mc.thePlayer) < reach){
                    targets.add((EntityPlayer) ent);
                }
            }
        }

        return targets;
    }

    public static float[] getRotations(Entity e) {
        double deltaX = e.posX + (e.posX - e.lastTickPosX) - mc.thePlayer.posX;
        double deltaZ = e.posZ + (e.posZ - e.lastTickPosZ) - mc.thePlayer.posZ;
        double deltaY = e.posY - 3.5 + e.getEyeHeight() - mc.thePlayer.posY + mc.thePlayer.getEyeHeight();
        double distance = Math.sqrt(Math.pow(deltaX, 2) + Math.pow(deltaZ, 2));

        float yaw = (float) Math.toDegrees(-Math.atan(deltaX / deltaZ));
        float pitch = (float) -Math.toDegrees(Math.atan(deltaY/distance));

        if(deltaX < 0 && deltaZ < 0)
            yaw = (float) (90 + Math.toDegrees(Math.atan(deltaZ / deltaX)));
        else if (deltaX > 0 && deltaZ < 0)
            yaw = (float) (-90 + Math.toDegrees(Math.atan(deltaZ / deltaX)));

        return new float[] {yaw, pitch};
    }
    public static boolean isTeam(final EntityPlayer e, final Entity ep) {
        if (ep instanceof EntityPlayer && ((EntityPlayer)ep).getTeam() != null && e.getTeam() != null) {
            final Character target = ep.getDisplayName().getFormattedText().charAt(3);
            final Character player = e.getDisplayName().getFormattedText().charAt(3);
            final Character target2 = ep.getDisplayName().getFormattedText().charAt(2);
            final Character player2 = e.getDisplayName().getFormattedText().charAt(2);
            boolean isTeam = false;
            if (target.equals(player) && target2.equals(player2)) {
                isTeam = true;
            }
            else {
                final Character target3 = ep.getDisplayName().getFormattedText().charAt(1);
                final Character player3 = e.getDisplayName().getFormattedText().charAt(1);
                final Character target4 = ep.getDisplayName().getFormattedText().charAt(0);
                final Character player4 = e.getDisplayName().getFormattedText().charAt(0);
                if (target3.equals(player3) && Character.isDigit(0) && target4.equals(player4)) {
                    isTeam = true;
                }
            }
            return isTeam;
        }
        return true;
    }

}
