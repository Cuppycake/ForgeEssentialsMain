package com.forgeessentials.api.permissions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import net.minecraft.server.MinecraftServer;

import org.apache.commons.lang3.StringUtils;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.commons.selections.WorldArea;
import com.forgeessentials.commons.selections.WorldPoint;
import com.forgeessentials.util.FunctionHelper;
import com.forgeessentials.util.UserIdent;

/**
 * {@link ServerZone} contains every player on the whole server. Has second
 * lowest priority with next being {@link RootZone}.
 * 
 * @author Olee
 */
public class ServerZone extends Zone {

    /**
     * Compares groups by priority
     */
    public class GroupComparator implements Comparator<String> {

        @Override
        public int compare(String group1, String group2)
        {
            String priority1 = getGroupPermission(group1, FEPermissions.GROUP_PRIORITY);
            String priority2 = getGroupPermission(group2, FEPermissions.GROUP_PRIORITY);
            int diff = FunctionHelper.parseIntDefault(priority2, FEPermissions.GROUP_PRIORITY_DEFAULT)
                    - FunctionHelper.parseIntDefault(priority1, FEPermissions.GROUP_PRIORITY_DEFAULT);
            if (diff == 0)
                diff = group1.hashCode() - group2.hashCode();
            return diff;
        }

    }

    // ------------------------------------------------------------

    private RootZone rootZone;

    private Map<Integer, Zone> zones = new HashMap<Integer, Zone>();

    private Map<Integer, WorldZone> worldZones = new HashMap<Integer, WorldZone>();

    private int maxZoneID;

    private Map<UserIdent, Set<String>> playerGroups = new HashMap<UserIdent, Set<String>>();

    private Set<UserIdent> knownPlayers = new HashSet<UserIdent>();

    // ------------------------------------------------------------

    public ServerZone()
    {
        super(1);
        setGroupPermission(IPermissionsHelper.GROUP_DEFAULT, FEPermissions.GROUP, true);
        setGroupPermission(IPermissionsHelper.GROUP_GUESTS, FEPermissions.GROUP, true);
        setGroupPermission(IPermissionsHelper.GROUP_OPERATORS, FEPermissions.GROUP, true);
        setGroupPermissionProperty(IPermissionsHelper.GROUP_DEFAULT, FEPermissions.GROUP_PRIORITY, "0");
        setGroupPermissionProperty(IPermissionsHelper.GROUP_GUESTS, FEPermissions.GROUP_PRIORITY, "10");
        setGroupPermissionProperty(IPermissionsHelper.GROUP_OPERATORS, FEPermissions.GROUP_PRIORITY, "50");
        setGroupPermissionProperty(IPermissionsHelper.GROUP_GUESTS, FEPermissions.PREFIX, "[GUEST]");
        setGroupPermissionProperty(IPermissionsHelper.GROUP_OPERATORS, FEPermissions.PREFIX, "[OPERATOR]");
        APIRegistry.getFEEventBus().post(new PermissionEvent.Initialize(this));
        addZone(this);
    }

    public ServerZone(RootZone rootZone)
    {
        this();
        this.maxZoneID = 1;
        this.rootZone = rootZone;
        this.rootZone.setServerZone(this);
        addZone(this.rootZone);
    }

    // ------------------------------------------------------------

    @Override
    public boolean isInZone(WorldPoint point)
    {
        return true;
    }

    @Override
    public boolean isInZone(WorldArea point)
    {
        return true;
    }

    @Override
    public boolean isPartOfZone(WorldArea point)
    {
        return true;
    }

    @Override
    public String getName()
    {
        return "_SERVER_";
    }

    @Override
    public Zone getParent()
    {
        return rootZone;
    }

    @Override
    public ServerZone getServerZone()
    {
        return this;
    }

    public RootZone getRootZone()
    {
        return rootZone;
    }

    // ------------------------------------------------------------

    public Map<Integer, WorldZone> getWorldZones()
    {
        return worldZones;
    }

    public void addWorldZone(WorldZone zone)
    {
        worldZones.put(zone.getDimensionID(), zone);
        addZone(zone);
        setDirty();
    }

    public int getMaxZoneID()
    {
        return maxZoneID;
    }

    public int nextZoneID()
    {
        return ++maxZoneID;
    }

    public void setMaxZoneId(int maxId)
    {
        this.maxZoneID = maxId;
    }

    void setRootZone(RootZone rootZone)
    {
        this.rootZone = rootZone;
        addZone(this.rootZone);
    }

    // ------------------------------------------------------------

    public Set<String> getGroups()
    {
        return getGroupPermissions().keySet();
    }

    public boolean groupExists(String name)
    {
        return getGroupPermissions().containsKey(name);
    }

    public boolean createGroup(String name)
    {
        if (APIRegistry.getFEEventBus().post(new PermissionEvent.Group.Create(this, name)))
            return false;
        setGroupPermission(name, FEPermissions.GROUP, true);
        setGroupPermissionProperty(name, FEPermissions.GROUP_PRIORITY, Integer.toString(FEPermissions.GROUP_PRIORITY_DEFAULT));
        setDirty();
        return true;
    }

    public Set<String> getIncludedGroups(String group) 
    {
        Set<String> includedGroups = new HashSet<>();
        String includedGroupsStr = getGroupPermission(group, FEPermissions.GROUP_INCLUDES);
        if (includedGroupsStr != null && !includedGroupsStr.isEmpty())
            for (String g : includedGroupsStr.split(","))
                if (!g.isEmpty())
                    includedGroups.add(g);
        return includedGroups;
    }

    public void groupIncludeAdd(String group, String otherGroup) 
    {
        Set<String> includedGroups = getIncludedGroups(group);
        includedGroups.add(otherGroup);
        APIRegistry.perms.setGroupPermissionProperty(group, FEPermissions.GROUP_INCLUDES, StringUtils.join(includedGroups, ","));
    }

    public void groupIncludeRemove(String group, String otherGroup) 
    {
        Set<String> includedGroups = getIncludedGroups(group);
        includedGroups.remove(otherGroup);
        APIRegistry.perms.setGroupPermissionProperty(group, FEPermissions.GROUP_INCLUDES, StringUtils.join(includedGroups, ","));
    }

    // ------------------------------------------------------------

    public boolean addPlayerToGroup(UserIdent ident, String group)
    {
        registerPlayer(ident);
        if (APIRegistry.getFEEventBus().post(new PermissionEvent.User.ModifyGroups(this, ident, PermissionEvent.User.ModifyGroups.Action.ADD, group)))
            return false;
        Set<String> groupSet = playerGroups.get(ident);
        if (groupSet == null)
        {
            groupSet = new TreeSet<String>();
            playerGroups.put(ident, groupSet);
        }
        groupSet.add(group);
        setDirty();
        return true;
    }

    public boolean removePlayerFromGroup(UserIdent ident, String group)
    {
        registerPlayer(ident);
        if (APIRegistry.getFEEventBus().post(new PermissionEvent.User.ModifyGroups(this, ident, PermissionEvent.User.ModifyGroups.Action.REMOVE, group)))
            return false;
        Set<String> groupSet = playerGroups.get(ident);
        if (groupSet != null)
            groupSet.remove(group);
        setDirty();
        return true;
    }

    public SortedSet<String> getPlayerGroups(UserIdent ident)
    {
        SortedSet<String> result = getStoredPlayerGroups(ident);

        if (ident != null)
        {
            if (ident.hasPlayer() && !ident.isFakePlayer()
                    && MinecraftServer.getServer().getConfigurationManager().func_152596_g(ident.getPlayer().getGameProfile()))
            {
                result.add(IPermissionsHelper.GROUP_OPERATORS);
            }
            if (result.isEmpty())
            {
                result.add(IPermissionsHelper.GROUP_GUESTS);
            }
        }
        result.add(IPermissionsHelper.GROUP_DEFAULT);

        // Get included groups
        Set<String> checkedGroups = new HashSet<>();
        for (boolean addedGroup = true; addedGroup; addedGroup = false)
        {
            for (String existingGroup : new ArrayList<String>(result))
            {
                // Check if group was already checked for inclusion
                if (!checkedGroups.add(existingGroup))
                    continue;
                String includedGroupsStr = getGroupPermission(existingGroup, FEPermissions.GROUP_INCLUDES);
                if (includedGroupsStr != null)
                {
                    String[] includedGroups = includedGroupsStr.split(",");
                    for (String includedGroup : includedGroups)
                        if (!includedGroup.isEmpty())
                            addedGroup |= result.add(includedGroup);
                }
            }
        }

        return result;
    }

    public SortedSet<String> getStoredPlayerGroups(UserIdent ident)
    {
        registerPlayer(ident);
        Set<String> pgs = playerGroups.get(ident);
        SortedSet<String> result = new TreeSet<String>(new GroupComparator());
        if (pgs != null)
            result.addAll(pgs);
        return result;
    }

    public Map<UserIdent, Set<String>> getPlayerGroups()
    {
        return playerGroups;
    }

    public String getPrimaryPlayerGroup(UserIdent ident)
    {
        Iterator<String> it = getPlayerGroups(ident).iterator();
        if (it.hasNext())
            return it.next();
        else
            return null;
    }

    // ------------------------------------------------------------

    public void addZone(Zone zone)
    {
        zones.put(zone.getId(), zone);
    }

    public boolean removeZone(Zone zone)
    {
        return zones.remove(zone.getId()) != null;
    }

    public void rebuildZonesMap()
    {
        zones.clear();
        addZone(getRootZone());
        addZone(this);
        for (WorldZone worldZone : worldZones.values())
        {
            addZone(worldZone);
            for (AreaZone areaZone : worldZone.getAreaZones())
            {
                addZone(areaZone);
            }
        }
    }

    public Map<Integer, Zone> getZoneMap()
    {
        return zones;
    }

    public Collection<Zone> getZones()
    {
        return zones.values();
    }

    // ------------------------------------------------------------

    public void registerPlayer(UserIdent ident)
    {
        if (ident != null)
            knownPlayers.add(ident);
    }

    public Set<UserIdent> getKnownPlayers()
    {
        return knownPlayers;
    }

}
